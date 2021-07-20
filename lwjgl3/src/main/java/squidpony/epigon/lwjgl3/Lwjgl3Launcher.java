package squidpony.epigon.lwjgl3;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;

import com.badlogic.gdx.backends.lwjgl3.audio.OpenALLwjgl3Audio;
import squidpony.epigon.game.*;
import squidpony.epigon.game.Epigon;
import squidpony.epigon.files.Config;

public class Lwjgl3Launcher {

    public static void main(String... args) {
        // load splashscreen

        // now that assets are ready, load main game screen
        mainGame();

    }

    private static void mainGame() {
        //start independent creators
        System.out.println("Loading...");

        //read in all external data files
        Config config = Config.instance(); // will cause the config file to be read if it hasn't already

        // need to cross-config set window size if it's not in the configs
        if (config.displayConfig.windowWidth <= 0) {
            config.displayConfig.windowWidth = config.displayConfig.defaultPixelWidth();
        }
        if (config.displayConfig.windowHeight <= 0) {
            config.displayConfig.windowHeight = config.displayConfig.defaultPixelHeight();
        }

        System.out.println("Files loaded!");
        Epigon epigon = new Crawl(config);

        //start independent listeners
        //load and initialize resources
        //initialize the display
        //initialize the world
        //start dependent creators
        //start dependent listeners
        //hand control over to the display
        Lwjgl3ApplicationConfiguration appConfig = new Lwjgl3ApplicationConfiguration();
        Lwjgl3WindowAdapter primaryWindowListener = new Lwjgl3WindowAdapter() {
            private Lwjgl3Window win;

            @Override
            public void created(Lwjgl3Window window) {
                super.created(window);
                win = window;
            }

            @Override
            public void maximized(boolean isMaximized) {
                config.displayConfig.maximized = isMaximized;
                if (!isMaximized) {
                    Gdx.app.postRunnable(()
                        -> Gdx.graphics.setWindowedMode(config.displayConfig.windowWidth, config.displayConfig.windowHeight)
                    );
                }

                super.maximized(isMaximized);
            }

            @Override
            public boolean closeRequested() {
                if (!config.displayConfig.maximized && !config.displayConfig.fullscreen) {
                    config.displayConfig.windowXPosition = win.getPositionX();
                    config.displayConfig.windowYPosition = win.getPositionY();
                    config.displayConfig.windowWidth = Gdx.graphics.getWidth();
                    config.displayConfig.windowHeight = Gdx.graphics.getHeight();
                    config.displayConfig.monitorName = Gdx.graphics.getMonitor().name;
                    config.saveDisplay();
                }

                return super.closeRequested();
            }
        };

        appConfig.setWindowListener(primaryWindowListener);

        // get monitor info for display
        String lastMonitorName = config.displayConfig.monitorName;
        Monitor monitor = null;
        if (lastMonitorName != null && !lastMonitorName.isEmpty()) {
            for (Monitor m : Lwjgl3ApplicationConfiguration.getMonitors()) {
                if (m.name.equals(lastMonitorName)) {
                    monitor = m;
                    break;
                }
            }
        }
        if (monitor == null) {
            monitor = Lwjgl3ApplicationConfiguration.getPrimaryMonitor();
        }

        DisplayMode display = Lwjgl3ApplicationConfiguration.getDisplayMode(monitor);

        if (config.displayConfig.maximized) {
            appConfig.setMaximized(true);
        } else if (config.displayConfig.fullscreen) {
            appConfig.setFullscreenMode(display);
        } else {
            appConfig.setWindowedMode(config.displayConfig.windowWidth, config.displayConfig.windowHeight);

            int x = config.displayConfig.windowXPosition;
            int y = config.displayConfig.windowYPosition;
            System.out.println("Window position: (" + x + ", " + y + ")");

            if (x < 0) {
                x = (display.width - config.displayConfig.windowWidth) / 2;
                config.displayConfig.windowXPosition = x;
            }
            if (y < 0) {
                y = (display.height - config.displayConfig.windowHeight) / 2;
                config.displayConfig.windowYPosition = y;
            }

            appConfig.setWindowPosition(x, y); // This doesn't take into account the upper left including title bar, just content (libgdx / lwjgl limitation)
        }

        appConfig.setTitle(Config.gameTitle);
        //set to useVsync(false) if testing FPS
        appConfig.useVsync(true);
        appConfig.setWindowIcon(Files.FileType.Internal, "images/icons/libgdx128.png", "images/icons/libgdx64.png", "images/icons/libgdx32.png", "images/icons/libgdx16.png");
        new Lwjgl3Application(epigon, appConfig) {
            @Override
            public void exit() {
                ((OpenALLwjgl3Audio)Gdx.audio).dispose();
                primaryWindowListener.closeRequested(); // have the primary window do it's thing before leaving
                super.exit();
            }
        };
    }

}

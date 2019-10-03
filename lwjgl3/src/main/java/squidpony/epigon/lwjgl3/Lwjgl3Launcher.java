package squidpony.epigon.lwjgl3;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import squidpony.epigon.Epigon;
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

        System.out.println("Files loaded!");

        //start independent listeners
        //load and initialize resources
        //initialize the display
        //initialize the world
        //start dependent creators
        //start dependent listeners
        //hand control over to the display
        Lwjgl3ApplicationConfiguration appConfig = new Lwjgl3ApplicationConfiguration();

        if (config.displayConfig.maximized) {
            appConfig.setMaximized(true);
        } else if (config.displayConfig.fullscreen) {
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
            appConfig.setFullscreenMode(display);
        } else {
            appConfig.setWindowedMode(config.displayConfig.windowWidth, config.displayConfig.windowHeight);
        }

        appConfig.setTitle(config.gameTitle);
        //uncomment if testing FPS
        appConfig.useVsync(false);
        appConfig.setWindowIcon(Files.FileType.Internal, "images/icons/libgdx128.png", "images/icons/libgdx64.png", "images/icons/libgdx32.png", "images/icons/libgdx16.png");
        new Lwjgl3Application(new Epigon(), appConfig);
    }

}

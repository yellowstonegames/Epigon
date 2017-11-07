package squidpony.epigon.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import squidpony.epigon.Prefs;
import squidpony.epigon.Epigon;

public class DesktopLauncher {

    public static void main(String... args) {
        //start independent creators
        System.out.println("Loading...");
        
        //read in all external data files
        System.out.println("Files loaded!");

        //start independent listeners

        //load and initialize resources

        //initialize the display

        //initialize the world

        //start dependent creators

        //start dependent listeners

        //hand control over to the display
        /*
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(Prefs.getScreenWidth(), Prefs.getScreenHeight());
        config.setTitle(Prefs.getGameTitle());
        //uncomment if testing FPS
        //config.useVsync(false);
        config.setWindowIcon(Files.FileType.Internal, "libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        new Lwjgl3Application(new Epigon(), config);
        */
        // this is the LWJGL 2 ("desktop") configuration, which is slightly different.
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = Prefs.getScreenWidth();
        config.height = Prefs.getScreenHeight();
        config.title = Prefs.getGameTitle();
        //config.vSyncEnabled = true; // true by default

        config.addIcon("images/icons/logo128.png", Files.FileType.Internal);
        config.addIcon("images/icons/logo32.png", Files.FileType.Internal);
        config.addIcon("images/icons/logo16.png", Files.FileType.Internal);

        new LwjglApplication(new Epigon(), config);

    }
}

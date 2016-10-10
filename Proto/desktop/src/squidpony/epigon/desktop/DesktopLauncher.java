package squidpony.epigon.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import squidpony.epigon.EnvironmentalVariables;
import squidpony.epigon.Epigon;

public class DesktopLauncher {

    public static void main(String... args) {
        //start independant creators
        System.out.println("Loading...");
        
        //read in all external data files
        System.out.println("Files loaded!");

        //start independant listeners

        //load and initialize resources

        //initialize the display

        //initialize the world

        //start dependant creators

        //start dependant listeners

        //hand control over to the display
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = EnvironmentalVariables.getScreenWidth();
        config.height = EnvironmentalVariables.getScreenHeight();
        config.title = EnvironmentalVariables.getGameTitle();
        
        config.addIcon("images/icons/logo128.png", Files.FileType.Internal);
        config.addIcon("images/icons/logo32.png", Files.FileType.Internal);
        config.addIcon("images/icons/logo16.png", Files.FileType.Internal);
        
        new LwjglApplication(new Epigon(), config);
    }
}

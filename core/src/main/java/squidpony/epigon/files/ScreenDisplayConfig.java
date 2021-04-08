package squidpony.epigon.files;

/**
 * Hardware specific settings for presenting the display.
 */
public class ScreenDisplayConfig {

    public int windowWidth;
    public int windowHeight;
    public int windowXPosition = -1;
    public int windowYPosition = -1;
    public int monitorIndex; // looks like for libgdx tracking by name might be better
    public boolean maximized;
    public boolean fullscreen;
    public String monitorName;

}

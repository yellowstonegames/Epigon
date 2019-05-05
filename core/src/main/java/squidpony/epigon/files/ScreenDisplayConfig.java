package squidpony.epigon.files;

import squidpony.epigon.Epigon;

public class ScreenDisplayConfig {

    public int windowWidth = Epigon.totalPixelWidth();
    public int windowHeight = Epigon.totalPixelHeight();
    public int windowXPosition;
    public int windowYPosition;
    public int monitorIndex;
    public boolean maximized;
    public boolean fullscreen;
    public String monitorName;

}

package squidpony.epigon;

import java.util.prefs.Preferences;

/**
 * A static class that holds player adjustable environment wide variables.
 */
public class Prefs {

    static private Preferences prefs = Preferences.userRoot().node("/squidpony");
    static private int screenWidth = 1800;
    static private int screenHeight = 1020;
    static private String title = "Epigon - The Expected Beginning";
    static private boolean debug = true;

    /**
     * No instances of this class should be made.
     */
    private Prefs() {
    }

    public static String getGameTitle() {
        return title;
    }

    public static boolean isDebugMode() {
        return debug;
    }

    public static int getScreenWidth() {
        return prefs.getInt("screenWidth", screenWidth);
    }

    public static void setScreenWidth(int width) {
        prefs.putInt("screenWidth", width);
    }

    public static int getScreenHeight() {
        return prefs.getInt("screenHeight", screenHeight);
    }

    public static void setScreenHeight(int height) {
        prefs.putInt("screenHeight", height);
    }

    public static int getScreenXLocation() {
        return prefs.getInt("screenXLocation", 0);
    }

    public static void setScreenXLocation(int x) {
        prefs.putInt("screenXLocation", x);
    }

    public static int getScreenYLocation() {
        return prefs.getInt("screenYLocation", 0);
    }

    public static void setScreenYLoaction(int y) {
        prefs.putInt("screenYLocation", y);
    }

    public static boolean isSoundfxOn() {
        return prefs.getBoolean("soundfxOn", true);
    }

    public static void setSoundfxOn(boolean soundfxOn) {
        prefs.putBoolean("soundfxOn", soundfxOn);
    }

    public static boolean isMusicOn() {
        return prefs.getBoolean("musicOn", true);
    }

    public static void setMusicOn(boolean musicOn) {
        prefs.putBoolean("musicOn", musicOn);
    }

    public static float getSoundfxVolume() {
        return prefs.getFloat("soundfxVolume", 0.5f);
    }

    public static void setSoundfxVolume(float soundfxVolume) {
        prefs.putFloat("soundfxVolume", soundfxVolume);
    }

    public static float getMusicVolume() {
        return prefs.getFloat("musicVolume", 0.7f);
    }

    public static void setMusicVolume(float musicVolume) {
        prefs.putFloat("musicVolume", musicVolume);
    }
}

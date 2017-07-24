package squidpony.epigon;

import com.badlogic.gdx.Gdx;

/**
 * A static class that holds player adjustable environment wide variables.
 */
public class Prefs {

    static private int screenWidth = Epigon.totalPixelWidth();
    static private int screenHeight = Epigon.totalPixelHeight();
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
        return screenWidth;
        //return Gdx.app.getPreferences("Epigon").getInteger("screenWidth", screenWidth);
    }

    public static void setScreenWidth(int width) {
        Gdx.app.getPreferences("Epigon").putInteger("screenWidth", width).flush();
    }

    public static int getScreenHeight() {
        return screenHeight;
        //return Gdx.app.getPreferences("Epigon").getInteger("screenHeight", screenHeight);
    }

    public static void setScreenHeight(int height) {
        Gdx.app.getPreferences("Epigon").putInteger("screenHeight", height).flush();
    }

    public static int getScreenXLocation() {
        return Gdx.app.getPreferences("Epigon").getInteger("screenXLocation", 0);
    }

    public static void setScreenXLocation(int x) {
        Gdx.app.getPreferences("Epigon").putInteger("screenXLocation", x).flush();
    }

    public static int getScreenYLocation() {
        return Gdx.app.getPreferences("Epigon").getInteger("screenYLocation", 0);
    }

    public static void setScreenYLoaction(int y) {
        Gdx.app.getPreferences("Epigon").putInteger("screenYLocation", y).flush();
    }

    public static boolean isSoundfxOn() {
        return Gdx.app.getPreferences("Epigon").getBoolean("soundfxOn", true);
    }

    public static void setSoundfxOn(boolean soundfxOn) {
        Gdx.app.getPreferences("Epigon").putBoolean("soundfxOn", soundfxOn).flush();
    }

    public static boolean isMusicOn() {
        return Gdx.app.getPreferences("Epigon").getBoolean("musicOn", true);
    }

    public static void setMusicOn(boolean musicOn) {
        Gdx.app.getPreferences("Epigon").putBoolean("musicOn", musicOn).flush();
    }

    public static float getSoundfxVolume() {
        return Gdx.app.getPreferences("Epigon").getFloat("soundfxVolume", 0.5f);
    }

    public static void setSoundfxVolume(float soundfxVolume) {
        Gdx.app.getPreferences("Epigon").putFloat("soundfxVolume", soundfxVolume).flush();
    }

    public static float getMusicVolume() {
        return Gdx.app.getPreferences("Epigon").getFloat("musicVolume", 0.7f);
    }

    public static void setMusicVolume(float musicVolume) {
        Gdx.app.getPreferences("Epigon").putFloat("musicVolume", musicVolume).flush();
    }
}

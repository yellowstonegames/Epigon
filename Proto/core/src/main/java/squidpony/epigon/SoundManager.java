package squidpony.epigon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import squidpony.Maker;
import squidpony.squidmath.OrderedSet;

import java.util.Map;
import java.util.TreeMap;

/**
 * Singleton class which controls sound output.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class SoundManager {

    private static final String footstep = "footstep0";
    private static final int totalFootsteps = 9;
    private int lastFootstep = 0;

    private boolean musicLoaded = false, soundfxLoaded = false;
    private final Map<String, Sound> soundMap = new TreeMap<>();
    private final Map<String, Music> musicMap = new TreeMap<>();
    private Music nowPlaying;
    private final OrderedSet<String> readableFileTypes = Maker.makeOS("wav", "mp3", "ogg");

    public SoundManager() {
        if (Prefs.isSoundfxOn()) {
            System.out.println("Loading Sound FX");
            loadSoundFXResources();
        }

        if (Prefs.isMusicOn()) {
            System.out.println("Loading Music");
            loadMusicResources();
        }
    }

    private void loadSoundFXResources() {

        for (FileHandle f : Gdx.files.internal("sound fx").list()) {
            String name = f.name();
            if (readableFileTypes.contains(name.substring(name.lastIndexOf('.') + 1))) {
                //System.out.println("Loading sound fx: " + name);
                Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound fx/" + name));
                soundMap.put(f.nameWithoutExtension(), sound);
            }
        }

        soundfxLoaded = true;
    }

    private void unloadSoundFXResources() {
        if (!soundfxLoaded) {
            return;//no sound fx loaded so nothing to unload
        }

        for (Sound s : soundMap.values()) {
            s.stop();
            s.dispose();
        }

        soundfxLoaded = false;
    }

    /**
     * Plays the sound fx associated with the provided key. The key is the filename of the sound
     * without its extension.
     *
     * @param key
     */
    public void playSoundFX(String key) {
        if (!Prefs.isSoundfxOn()) {
            return;//don't do anything if the sound effects are off
        }

        if (!soundfxLoaded) {
            loadSoundFXResources();
        }

        Sound temp = soundMap.get(key);
        if (temp == null) {
            return;
        }

        temp.play(Prefs.getSoundfxVolume());
    }

    private void loadMusicResources() {
        for (FileHandle f : Gdx.files.internal("music").list()) {
            String name = f.name();
            if (readableFileTypes.contains(name.substring(name.lastIndexOf('.') + 1))) {
                Music music = Gdx.audio.newMusic(Gdx.files.internal("music/" + name));
                music.setLooping(true);
                musicMap.put(f.nameWithoutExtension(), music);
            }
        }

        musicLoaded = true;
    }

    private void unloadMusicResources() {
        if (!musicLoaded) {
            return;//no music loaded so nothing to unload
        }

        if (nowPlaying != null) {
            nowPlaying = null;
        }

        for (Music m : musicMap.values()) {
            m.stop();
            m.dispose();
        }

        musicLoaded = false;
    }

    /**
     * Plays the music associated with the key. Keys for music are the filenames of the tracks
     * without their extension.
     *
     * @param key
     */
    public void playMusic(String key) {
        if (!Prefs.isMusicOn()) {
            stopMusic();
            return;//don't do anything if the music is off
        }

        if (!musicLoaded) {
            loadMusicResources();
        }

        Music temp = (musicMap.get(key));
        if (temp == null) {
            return;//track not found, continue current music selection
        }

        if (nowPlaying != temp) {
            if (nowPlaying != null) {
                nowPlaying.stop();
            }
            nowPlaying = temp;
        }

        nowPlaying.setVolume(Prefs.getMusicVolume());
        nowPlaying.setLooping(true);
        if (!nowPlaying.isPlaying()) {
            nowPlaying.play();
        }
    }

    /**
     * Stops the currently playing music.
     */
    public void stopMusic() {
        if (nowPlaying != null && nowPlaying.isPlaying()) {
            nowPlaying.stop();
        }
    }

    /**
     * Sets the music to play at the provided volume, with 0 being off and 1 being full volume.
     *
     * @param volume
     */
    public void setMusicVolume(float volume) {
        volume = Math.max(volume, 0f);
        volume = Math.min(volume, 1.0f);

        if (musicLoaded) {
            if (volume < 0.001) {
                unloadMusicResources();//unload if volume set to effectively zero
                Prefs.setMusicOn(false);
            } else if (nowPlaying != null) {
                nowPlaying.setVolume(volume);
            }
        }

        Prefs.setMusicVolume(volume);
    }

    public void dispose() {
        unloadMusicResources();
        unloadSoundFXResources();
    }

    /**
     * Plays a random footstep sound.
     */
    public void playFootstep(){
        int step = GauntRNG.nextInt( System.currentTimeMillis(),totalFootsteps - 1) + 1;
        playSoundFX(footstep + step);
    }
}

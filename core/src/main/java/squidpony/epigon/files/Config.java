package squidpony.epigon.files;

/**
 * A class to hold the game's launch configurations.
 */
public class Config {

    public static final String gameTitle = "Epigon";

    private static final String configPath = "Settings";
    private static final String debugConfigFilename = "debug.json";
    private static final String settingsConfigFilename = "settings.json";
    private static final String displayConfigFilename = "display.json";
    private static final String audioConfigFilename = "audio.json";

    private static Config instance;

    public DebugConfig debugConfig;
    public Settings settings;
    public ScreenDisplayConfig displayConfig = new ScreenDisplayConfig();
    public AudioConfig audioConfig = new AudioConfig();

    private Config() {
    }

    public static Config instance() {
        if (instance != null) {
            return instance;
        }

        instance = new Config();
        instance.loadAll();
        return instance;
    }

    /**
     * Saves all of the configs at their default location with their current information.
     */
    public void saveAll() {
        FileManager fileManager = FileManager.instance();

        String text = fileManager.json().prettyPrint(debugConfig);
        fileManager.writeFile(debugConfigFilename, configPath, text);

        text = fileManager.json().prettyPrint(settings);
        fileManager.writeFile(settingsConfigFilename, configPath, text);

        saveDisplay();

        text = fileManager.json().prettyPrint(audioConfig);
        fileManager.writeFile(audioConfigFilename, configPath, text);
    }

    /**
     * Saves out just the Display configs, at the default location.
     */
    public void saveDisplay() {
        FileManager fileManager = FileManager.instance();
        String text = fileManager.json().prettyPrint(displayConfig);
        fileManager.writeFile(displayConfigFilename, configPath, text);
    }

    /**
     * Loads all of the configs from their default location
     */
    public void loadAll() {
        String text;
        FileManager fileManager = FileManager.instance();

        text = fileManager.readFile(debugConfigFilename, configPath);
        if (text.isEmpty()) {
            System.out.println("No debug config file found, writing new default config file.");
            debugConfig = new DebugConfig();
        } else {
            debugConfig = fileManager.json().fromJson(DebugConfig.class, text);
        }
        text = fileManager.json().prettyPrint(debugConfig);
        fileManager.writeFile(debugConfigFilename, configPath, text);

        text = fileManager.readFile(settingsConfigFilename, configPath);
        if (text.isEmpty()) {
            System.out.println("No settings file found, writing new default config file.");
            settings = new Settings();
        } else {
            settings = fileManager.json().fromJson(Settings.class, text);
        }
        settings.calcSeed();
        text = fileManager.json().prettyPrint(settings);
        fileManager.writeFile(settingsConfigFilename, configPath, text);

        text = fileManager.readFile(displayConfigFilename, configPath);
        if (text.isEmpty()) {
            System.out.println("No display config file found, writing new default config file.");
            displayConfig = new ScreenDisplayConfig();
        } else {
            displayConfig = fileManager.json().fromJson(ScreenDisplayConfig.class, text);
        }
        displayConfig.init();
        displayConfig.adjustSecondaryToMaxHeight();
        text = fileManager.json().prettyPrint(displayConfig);
        fileManager.writeFile(displayConfigFilename, configPath, text);

        text = fileManager.readFile(audioConfigFilename, configPath);
        if (text.isEmpty()) {
            System.out.println("No audio config file found, writing new default config file.");
            audioConfig = new AudioConfig();
        } else {
            audioConfig = fileManager.json().fromJson(AudioConfig.class, text);
        }
        text = fileManager.json().prettyPrint(audioConfig);
        fileManager.writeFile(audioConfigFilename, configPath, text);
    }
}

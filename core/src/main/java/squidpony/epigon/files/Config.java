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
        instance.load();
        return instance;
    }

    public void save() {
        FileManager fileManager = FileManager.instance();

        String text = fileManager.json().prettyPrint(debugConfig);
        fileManager.writeFile(debugConfigFilename, configPath, text);

        text = fileManager.json().prettyPrint(settings);
        fileManager.writeFile(settingsConfigFilename, configPath, text);

        text = fileManager.json().prettyPrint(displayConfig);
        fileManager.writeFile(displayConfigFilename, configPath, text);

        text = fileManager.json().prettyPrint(audioConfig);
        fileManager.writeFile(audioConfigFilename, configPath, text);
    }

    public void load() {
        String text;
        FileManager fileManager = FileManager.instance();

        text = fileManager.readFile(debugConfigFilename, configPath);
        if (text.isEmpty()) {
            System.out.println("No debug config file found, writing new default config file.");
            debugConfig = new DebugConfig();
            text = fileManager.json().prettyPrint(debugConfig);
            fileManager.writeFile(debugConfigFilename, configPath, text);
        } else {
            System.out.println("Found debug config file, loaded.");
            debugConfig = fileManager.json().fromJson(DebugConfig.class, text);
        }

        text = fileManager.readFile(settingsConfigFilename, configPath);
        if (text.isEmpty()) {
            System.out.println("No settings file found, writing new default config file.");
            settings = new Settings();
            text = fileManager.json().prettyPrint(settings);
            fileManager.writeFile(settingsConfigFilename, configPath, text);
        } else {
            System.out.println("Found settings file, loaded.");
            settings = fileManager.json().fromJson(Settings.class, text);
        }

        text = fileManager.readFile(displayConfigFilename, configPath);
        if (text.isEmpty()) {
            System.out.println("No display config file found, writing new default config file.");
            displayConfig = new ScreenDisplayConfig();
            text = fileManager.json().prettyPrint(displayConfig);
            fileManager.writeFile(displayConfigFilename, configPath, text);
        } else {
            System.out.println("Found display config file, loaded.");
            displayConfig = fileManager.json().fromJson(ScreenDisplayConfig.class, text);
        }

        text = fileManager.readFile(audioConfigFilename, configPath);
        if (text.isEmpty()) {
            System.out.println("No audio config file found, writing new default config file.");
            audioConfig = new AudioConfig();
            text = fileManager.json().prettyPrint(audioConfig);
            fileManager.writeFile(audioConfigFilename, configPath, text);
        } else {
            System.out.println("Found audio config file, loaded.");
            audioConfig = fileManager.json().fromJson(AudioConfig.class, text);
        }
    }
}

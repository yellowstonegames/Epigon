package squidpony.epigon.files;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Config {

    private static final String configFilename = "config.json";

    private static Config instance;

    public boolean debugOn;

    public String gameTitle = "Epigon";

    public ScreenDisplayConfig displayConfig = new ScreenDisplayConfig();

    public AudioConfig audioConfig = new AudioConfig();

    private static final ObjectMapper mapper = (new ObjectMapper()).enable(SerializationFeature.INDENT_OUTPUT);

    public static Config instance() {
        if (instance != null) {
            return instance;
        }

        try {
            FileHandle file = Gdx.files.local(configFilename);
            if (file == null) {
                instance = new Config();
            }
            instance = mapper.readValue(file.readString(), Config.class);
        } catch (IOException e) {
            System.out.println("Config file did not load");
            instance = new Config(); // use default if file fails to load
        }

        return instance;
    }

    public void save() {
        try {
            mapper.writeValue(Gdx.files.local(configFilename).file(), instance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

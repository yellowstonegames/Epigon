package squidpony.epigon.files;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Collectors;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * Interaction point for reading and writing files.
 */
public class FileManager {

    private static FileManager instance;

    private Json json;

    public static FileManager instance() {
        if (instance != null) {
            return instance;
        }

        instance = new FileManager();
        return instance;
    }

    private FileManager() {
        json = new Json();
        json.setIgnoreUnknownFields(true);
        json.setOutputType(JsonWriter.OutputType.json);
        json.setQuoteLongValues(true);
        json.setSortFields(true);
        json.setUsePrototypes(false);
    }

    /**
     * Reads the file in and returns it as a single string, with \\n at line breaks.
     *
     * @param fileName
     * @param path
     * @return
     */
    public String readFile(String fileName, String path) {
        String localPath = "";
        if (path != null && !path.isEmpty()) {
            localPath = path + "/";
        }
        localPath += fileName;

        Path foundPath = Paths.get(localPath);

        try {
            return Files.readAllLines(foundPath).stream().collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            System.out.println("Could not read path: " + localPath + "\nException: " + ex.getLocalizedMessage());
            return "";
        }
    }

    /**
     * Writes the string directly into the given file.
     *
     * @param fileName
     * @param path
     * @param contents
     */
    public void writeFile(String fileName, String path, String contents) {
        String localPath = "";
        if (path != null && !path.isEmpty()) {
            localPath = path + "/";
        }
        localPath += fileName;

        Path foundPath = Paths.get(localPath);
        try {
            Files.createDirectories(Paths.get(path));
            Files.write(foundPath, Collections.singletonList(contents), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("Could not write path: " + localPath + "\nException: " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    /**
     * A Json object with configurations set.
     *
     * @return
     */
    public Json json() {
        return json;
    }

}

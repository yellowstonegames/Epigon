package squidpony.epigon.data.blueprint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import squidpony.epigon.data.EpiData;

/**
 * Contains the information related to a prebuilt map. The map is intended to represent a small area
 * rather than an entire region.
 *
 * @author SquidPony
 */
public class RoomBlueprint extends EpiData {

    public List<RoomConnection> connections = new ArrayList<>();//can mark where stairs should lead
    public boolean repeatLeft, repeatRight, repeatTop, repeatBottom,
        mirrorLeft, mirrorRight, mirrorTop, mirrorBottom;
    public Map<Character, PhysicalBlueprint> replacements;//maps characters to the physical objects that should replace them
    public boolean outside = false;
    public boolean landscape = false;//landscape is the middle layer between overworld and encounter level maps
    public boolean generic = false;
    public char[][] blueprint = null;//holds the character representation blueprint

    public char[][] getBlueprint() {
        return blueprint;
    }

    /**
     * Reads an array of strings and turns it into a 2D char array, ensuring that the resulting
     * blueprint is in the same coordinate system as it would appear when the string array was
     * printed out.
     *
     * @param layout
     */
    private void setLayout(String[] layout) {
        int width = layout[0].length();
        int height = layout.length;
        blueprint = new char[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                blueprint[x][y] = layout[y].charAt(x);
            }
        }
    }

    /**
     * Adds the given pair to the list of things that will be replaced upon constructions.
     *
     * @param key
     * @param contents
     */
    private void addReplacement(char key, PhysicalBlueprint contents) {
        replacements.put(key, contents);
    }

    /**
     * Sets all repeat parameters to the input value.
     *
     * @param repeat
     */
    private void setRepeat(boolean repeat) {
        repeatLeft = repeat;
        repeatRight = repeat;
        repeatTop = repeat;
        repeatBottom = repeat;
    }

    /**
     * Sets all mirror parameters to the input value.
     *
     * @param mirror
     */
    private void setMirror(boolean mirror) {
        mirrorLeft = mirror;
        mirrorRight = mirror;
        mirrorTop = mirror;
        mirrorBottom = mirror;
    }

    /**
     * Data class to hold tuple for marking connections between levels/rooms
     */
    public class RoomConnection {

        public String key, connectionType, connectedTo, connectedKey;
    }
}

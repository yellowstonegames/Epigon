package squidpony.epigon.display;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidLayers;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidmath.Coord;

/**
 * Handles the contents relevant to the current context mode, switching modes as needed.
 *
 * @author Eben Howard
 */
public class ContextHandler {

    public enum ContextMode {
        TILE_CONTENTS, INVENTORY, STAT_DETAILS, MINI_MAP, MESSAGE;

        private final String name;

        private ContextMode() {
            name = Arrays.stream(name().split("_"))
                .map(s -> s.substring(0, 1) + s.substring(1))
                .collect(Collectors.joining(" "));
        }

        private int position() {
            for (int i = 0; i < values().length; i++) {
                if (values()[i] == this) {
                    return i;
                }
            }
            return -1;
        }

        public ContextMode next() {
            return values()[(position() + 1) % values().length];
        }

        public ContextMode prior() {
            return values()[(position() + (values().length - 1)) % values().length];
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private SquidPanel back;
    private SquidPanel front;
    private int width;
    private int height;
    private ContextMode contextMode = ContextMode.TILE_CONTENTS;
    private EnumMap<ContextMode, char[][]> cachedTexts = new EnumMap<>(ContextMode.class);

    public Coord arrowLeft;
    public Coord arrowRight;

    public ContextHandler(SquidLayers layers) {
        width = layers.getGridWidth();
        height = layers.getGridHeight();
        back = layers.getBackgroundLayer();
        front = layers.getForegroundLayer();
        arrowLeft = Coord.get(1, 0);
        arrowRight = Coord.get(layers.getGridWidth() - 2, 0);
        for (ContextMode mode : ContextMode.values()) {
            cachedTexts.put(mode, new char[width][height]);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                back.put(x, y, back.getDefaultForegroundColor());
                front.put(x, y, ' ');
            }
        }
    }

    private void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                front.clear(x, y);
            }
        }

        String title = contextMode.toString();
        int x = width / 2 - title.length() / 2;
        front.put(x, 0, title);

        front.put(arrowLeft.x, arrowLeft.y, '◀', SColor.KIMONO_STORAGE);
        front.put(arrowRight.x, arrowRight.y, '▶', SColor.KIMONO_STORAGE);
    }

    private void put(String[] text) {
        clear();
        for (int y = 0; y < text.length && y < height - 2; y++) {
            for (int x = 0; x < text.length && x < width - 2; x++) {
                put(x + 1, y + 1, text[x].charAt(y));
            }
        }
    }

    private void put(char[][] chars) {
        if (chars == null) {
            clear();
        } else {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    put(x, y, chars[x][y]);
                }
            }
        }
    }

    private void put(int x, int y, char c) {
        front.put(x, y, c);
    }

    private void cache() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cachedTexts.get(contextMode)[x][y] = front.getAt(x, y);
            }
        }
    }

    public void next() {
        cache();
        front.wiggle(arrowRight.x, arrowRight.y, 0.3f);
        contextMode = contextMode.next();
        put(cachedTexts.get(contextMode));
    }

    public void prior() {
        cache();
        front.wiggle(arrowLeft.x, arrowLeft.y, 0.3f);
        contextMode = contextMode.prior();
        put(cachedTexts.get(contextMode));
    }

    public void contextStatDetails(Stat stat, LiveValue lv) {
        contextMode = ContextMode.STAT_DETAILS;
        clear();
        put(new String[]{
            stat.toString() + " (" + stat.nick() + ")",
            "Base:  " + lv.base,
            "Max:   " + lv.max,
            "Delta: " + lv.delta
        });
    }

    public void contextMiniMap() {
        contextMode = ContextMode.MINI_MAP;
        clear();
    }

    public void contextInventory(List<Physical> inventory) {
        contextMode = ContextMode.INVENTORY;
        clear();
        put(inventory.stream()
            .map(i -> i.name)
            .collect(Collectors.toList())
            .toArray(new String[]{}));
    }

    public void tileContents(Coord location, EpiTile tile) {
        contextMode = ContextMode.TILE_CONTENTS;
        String tileDescription = "[" + location.x + ", " + location.y + "] ";
        if (tile.floor != null) {
            tileDescription += tile.floor.name + " floor";
        } else {
            tileDescription += "empty space";
        }
        if (!tile.contents.isEmpty()) {
            tileDescription = tile.contents.stream()
                .map(p -> p.name)
                .collect(Collectors.joining("\n", tileDescription + "\n", ""));
        }
        put(tileDescription.split("\n"));
    }

    public void message(String[] text) {
        contextMode = ContextMode.MESSAGE;
        put(text);
    }

}

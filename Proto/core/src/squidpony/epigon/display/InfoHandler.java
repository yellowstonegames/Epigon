package squidpony.epigon.display;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Color;

import squidpony.ArrayTools;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;
import squidpony.squidgrid.gui.gdx.SquidLayers;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidmath.Coord;

import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.EquippedData;
import squidpony.epigon.universe.ClothingSlot;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;

import static squidpony.epigon.Epigon.infoSize;
import static squidpony.epigon.Epigon.rng;

/**
 * Handles the content relevant to the current stat mode.
 *
 * @author Eben Howard
 */
public class InfoHandler {

    public enum InfoMode {
        FULL_STATS, HEALTH_AND_ARMOR, SKILLS, TARGET_FULL_STATS, TARGET_HEALTH_AND_ARMOR;

        private final String name;

        private InfoMode() {
            name = Arrays.stream(name().split("_"))
                .map(s -> s.substring(0, 1) + s.substring(1).toLowerCase())
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

        public InfoMode next() {
            return values()[(position() + 1) % values().length];
        }

        public InfoMode prior() {
            return values()[(position() + (values().length - 1)) % values().length];
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final char[] eighthBlocks = new char[]{' ', '▁', '▂', '▃', '▄', '▅', '▆', '▇', '█'};
    private final int widestStatSize = Arrays.stream(Stat.values()).mapToInt(s -> s.toString().length()).max().getAsInt();

    private SquidPanel back;
    private SquidPanel front;
    private int width;
    private int height;
    private InfoMode infoMode = InfoMode.HEALTH_AND_ARMOR;
    private SquidColorCenter colorCenter;
    private Physical player;
    private Physical target;

    public Coord arrowLeft;
    public Coord arrowRight;

    public InfoHandler(SquidLayers layers, SquidColorCenter colorCenter) {
        this.colorCenter = colorCenter;
        width = layers.getGridWidth();
        height = layers.getGridHeight();
        back = layers.getBackgroundLayer();
        front = layers.getForegroundLayer();

        arrowLeft = Coord.get(1, 0);
        arrowRight = Coord.get(layers.getGridWidth() - 2, 0);

        ArrayTools.fill(back.colors, back.getDefaultForegroundColor().toFloatBits());
        ArrayTools.fill(back.contents, '\0');
        ArrayTools.fill(front.colors, front.getDefaultForegroundColor().toFloatBits());
        ArrayTools.fill(front.contents, ' ');
    }

    public void setPlayer(Physical player) {
        this.player = player;
    }

    public void setTarget(Physical target) {
        this.target = target;
    }

    private void clear() {
        ArrayTools.fill(front.contents, ' ');

        int w = width;
        int h = height;
        // all box drawing chars we know we can use:
        // ┼├┤┴┬┌┐└┘│─
        // ┌───┐
        // │┌┐ │
        // ├┴┼┬┤
        // │ └┘│
        // └───┘
        for (int x = 0; x < w; x++) {
            put(x, 0, '─');
            put(x, h - 1, '─');
        }
        for (int y = 0; y < h; y++) {
            put(0, y, '│');
            put(w - 1, y, '│');
        }
        put(0, 0, '┌');
        put(w - 1, 0, '┐');
        put(0, h - 1, '└');
        put(w - 1, h - 1, '┘');

        String title = infoMode.toString();
        switch (infoMode) {
            case FULL_STATS:
            case HEALTH_AND_ARMOR:
                title = "Player " + title;
                break;
            case TARGET_FULL_STATS:
            case TARGET_HEALTH_AND_ARMOR:
                if (target != null) {
                    title = target.name + " " + title;
                }
                break;
        }
        int x = width / 2 - title.length() / 2;
        put(x, 0, title);

        put(arrowLeft.x, arrowLeft.y, '◀');
        put(arrowRight.x, arrowRight.y, '▶');
    }

    private void put(int x, int y, String s) {
        for (int sx = 0; sx < s.length() && sx + x < width; sx++) {
            put(sx + x, y, s.charAt(sx));
        }
    }

    private void put(int x, int y, String s, Color color) {
        for (int sx = 0; sx < s.length() && sx + x < width; sx++) {
            put(sx + x, y, s.charAt(sx), color);
        }
    }

    private void put(int x, int y, char c) {
        front.put(x, y, c);
    }

    private void put(int x, int y, char c, Color color) {
        front.put(x, y, c, color);
    }

    public void next() {
        front.summon(arrowRight.x, arrowRight.y, arrowRight.x + 1, arrowRight.y - 2, '✔', SColor.CW_HONEYDEW,
            SColor.CW_RICH_HONEYDEW.cpy().sub(0f, 0f, 0f, 0.8f), 0f, 0.6f);
        infoMode = infoMode.next();
        updateDisplay();
    }

    public void prior() {
        front.summon(arrowLeft.x, arrowLeft.y, arrowLeft.x + 1, arrowLeft.y - 2, '✔', SColor.CW_HONEYDEW,
            SColor.CW_RICH_HONEYDEW.cpy().sub(0f, 0f, 0f, 0.8f), 0f, 0.6f);
        infoMode = infoMode.prior();
        updateDisplay();
    }

    public void showPlayerFullStats() {
        infoMode = InfoMode.FULL_STATS;
        infoFullStats(player);
    }

    public void showPlayerHealthAndArmor() {
        infoMode = InfoMode.HEALTH_AND_ARMOR;
        infoHealthAndArmor(player);
    }

    public void showPlayerSkills() {
        infoMode = InfoMode.SKILLS;
        infoSkills(player);
    }

    public void updateDisplay() {
        switch (infoMode) {
            case FULL_STATS:
                infoFullStats(player);
                break;
            case HEALTH_AND_ARMOR:
                infoHealthAndArmor(player);
                break;
            case SKILLS:
                infoSkills(player);
                break;
            case TARGET_FULL_STATS:
                infoFullStats(target);
                break;
            case TARGET_HEALTH_AND_ARMOR:
                infoHealthAndArmor(target);
                break;
        }
    }

    private void infoFullStats(Physical physical) {
        clear();
        if (physical == null) {
            return;
        }
        int offset = 1;
        showStats(offset, Stat.values(), physical);
    }

    private void infoHealthAndArmor(Physical physical) {
        clear();
        if (physical == null) {
            return;
        }
        int offset = 1;
        showStats(offset, Stat.healths, physical);

        offset += Stat.healths.length + 1;
        showStats(offset, Stat.needs, physical);

        if (physical.creatureData != null) {

            /*
            Ω
          ╭┬╨┬╮
          ││#││
          ╽╞═╡╽
           │ │
           ┙ ┕
             */
            offset += Stat.healths.length + 1;
            EquippedData ed = physical.creatureData.equippedData;
            EnumMap<ClothingSlot, Physical> armor = ed == null ? new EnumMap<>(ClothingSlot.class) : ed.getArmor();
            // left and right are when viewed from behind, i.e. with an over-the-shoulder camera
            double actual = 0;
            double base = 0;
            Physical p = armor.get(ClothingSlot.HEAD);
            if (p != null) {
                LiveValue lv = p.stats.get(Stat.STRUCTURE);
                if (lv != null) {
                    actual += lv.actual();
                    base += lv.base();
                }
            }
            p = armor.get(ClothingSlot.FACE);
            if (p != null) {
                LiveValue lv = p.stats.get(Stat.STRUCTURE);
                if (lv != null) {
                    actual += lv.actual();
                    base += lv.base();
                }
            }

            put(5, offset + 0, 'Ω', base <= 0 ? Rating.NONE.color() : percentColor(actual, base)); // head
            put(4, offset + 1, '┬', rng.getRandomElement(Rating.values()).color()); // left shoulder
            put(5, offset + 1, '╨', rng.getRandomElement(Rating.values()).color()); // neck
            put(6, offset + 1, '┬', rng.getRandomElement(Rating.values()).color()); // right shoulder
            put(4, offset + 2, '│', rng.getRandomElement(Rating.values()).color()); // chest
            put(5, offset + 2, '#', rng.getRandomElement(Rating.values()).color()); // chest
            put(6, offset + 2, '│', rng.getRandomElement(Rating.values()).color()); // chest
            put(4, offset + 3, '╞', rng.getRandomElement(Rating.values()).color()); // left hip, part of waist
            put(5, offset + 3, '═', rng.getRandomElement(Rating.values()).color()); // waist/groin
            put(6, offset + 3, '╡', rng.getRandomElement(Rating.values()).color()); // right hip, part of waist
            put(4, offset + 4, '│', rng.getRandomElement(Rating.values()).color()); // left leg
            put(6, offset + 4, '│', rng.getRandomElement(Rating.values()).color()); // right leg
            put(4, offset + 5, '┙', rng.getRandomElement(Rating.values()).color()); // left foot
            put(6, offset + 5, '┕', rng.getRandomElement(Rating.values()).color()); // right foot
            put(3, offset + 1, '╭', rng.getRandomElement(Rating.values()).color()); // left arm
            put(3, offset + 2, '│', rng.getRandomElement(Rating.values()).color()); // left arm
            put(3, offset + 3, '╽', rng.getRandomElement(Rating.values()).color()); // left arm/hand
            put(7, offset + 1, '╮', rng.getRandomElement(Rating.values()).color()); // right arm
            put(7, offset + 2, '│', rng.getRandomElement(Rating.values()).color()); // right arm
            put(7, offset + 3, '╽', rng.getRandomElement(Rating.values()).color()); // right arm/hand
            put(3, offset + 6, "Armor");
        }
    }

    private void infoSkills(Physical physical) {
        clear();
        if (physical == null) {
            return;
        }
        int offset = 1;
        showSkills(offset, physical);
    }

    private Color percentColor(double actual, double base) {
        double filling = actual / base;
        if (filling <= 1) {
            return colorCenter.lerp(SColor.RED, SColor.BRIGHT_GREEN, filling);
        } else {
            return colorCenter.lerp(SColor.BRIGHT_GREEN, SColor.BABY_BLUE, filling - 1);
        }
    }

    private void showStats(int offset, Stat[] stats, Physical physical) {
        int biggest = Arrays.stream(stats)
            .map(s -> physical.stats.get(s))
            .filter(s -> s != null)
            .mapToInt(s -> (int) Math.ceil(Math.max(s.base(), s.actual())))
            .max()
            .getAsInt();
        int biggestLength = Integer.toString(biggest).length();
        String format = "%0" + biggestLength + "d / %0" + biggestLength + "d";

        for (int s = 0; s < stats.length && s < infoSize.gridHeight - 2; s++) {
            Color color = physical.statProgression.getOrDefault(stats[s], Rating.NONE).color();
            put(1, s + offset, stats[s].toString(), color);

            double actual = physical.stats.getOrDefault(stats[s], LiveValue.ZERO).actual();
            double base = physical.stats.getOrDefault(stats[s], LiveValue.ZERO).base();
            String numberText = String.format(format, (int) Math.ceil(actual), (int) Math.ceil(base));
            color = percentColor(actual, base);
            put(widestStatSize + 2, s + offset, numberText, color);

            int blockValue = width - 2 - widestStatSize - 2 - numberText.length() - 1; // Calc how much horizontal space is left
            double filling = actual / biggest;
            int fullBlocks = (int) (filling * blockValue);
            double remainder = (filling * blockValue) % 1;
            remainder *= 7;
            String blockText = "";
            for (int i = 0; i < fullBlocks; i++) {
                blockText += eighthBlocks[7];
            }
            remainder = Math.max(remainder, 0);
            blockText += eighthBlocks[(int) Math.ceil(remainder)];
            put(widestStatSize + 2 + numberText.length() + 1, s + offset, blockText, color);
        }
    }

    private void showSkills(int offset, Physical physical) {
        int widestSkillSize = physical.creatureData.skills.keySet().stream()
            .mapToInt(s -> s.name.length())
            .max()
            .getAsInt();
        int y = 0;
        for (Entry<Skill, Rating> entry : physical.creatureData.skills.entrySet()) {
            Color color = entry.getValue().color();
            put(1, y + offset, caps(entry.getKey().name));

            put(widestSkillSize + 2, y + offset, caps(entry.getValue().toString()), color);

            y++;
        }
    }

    private String caps(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return Arrays.stream(input.split(" "))
            .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
            .collect(Collectors.joining(" "));
    }
}

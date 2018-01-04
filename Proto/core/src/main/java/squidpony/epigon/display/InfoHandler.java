package squidpony.epigon.display;

import com.badlogic.gdx.graphics.Color;
import squidpony.ArrayTools;
import squidpony.epigon.Utilities;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.*;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidmath.Coord;
import squidpony.squidmath.NumberTools;
import squidpony.squidmath.SeededNoise;
import squidpony.squidmath.StatefulRNG;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static squidpony.epigon.Epigon.infoSize;

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
    private final String sparkles = "#$%&";
    private final int widestStatSize = Arrays.stream(Stat.values()).mapToInt(s -> s.toString().length()).max().getAsInt();

    private SquidPanel back;
    private SquidPanel front;
    private SquidPanel fxBack;
    private SquidPanel fx;
    private int width;
    private int height;
    private InfoMode infoMode = InfoMode.HEALTH_AND_ARMOR;
    private SquidColorCenter colorCenter;
    private Physical player;
    private Physical target;
    private Map<Stat, Double> changes = Collections.emptyMap();

    public Coord arrowLeft;
    public Coord arrowRight;

    public InfoHandler(SquidLayers layers, SquidColorCenter colorCenter) {
        this.colorCenter = colorCenter;
        width = layers.getGridWidth();
        height = layers.getGridHeight();
        back = layers.getBackgroundLayer();
        front = layers.getForegroundLayer();
        fxBack = layers.addExtraLayer().getLayer(3);
        fx = layers.addExtraLayer().getLayer(4);

        arrowLeft = Coord.get(1, 0);
        arrowRight = Coord.get(layers.getGridWidth() - 2, 0);

        ArrayTools.fill(back.colors, back.getDefaultForegroundColor().toFloatBits());
        ArrayTools.fill(back.contents, '\0');
        ArrayTools.fill(front.colors, front.getDefaultForegroundColor().toFloatBits());
        ArrayTools.fill(front.contents, ' ');
        fxBack.erase();
        fx.erase();
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
        updateDisplay(null, Collections.emptyMap());
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

    /**
     * Shows the changes passed in on the current stat display. Expected that the displayed target has already had its
     * values adjusted prior to calling this.
     *
     * @param changes a Map of the changes to stats
     */
    public void updateDisplay(Physical physical, Map<Stat, Double> changes) {
        this.changes = Collections.emptyMap();
        switch (infoMode) {
            case FULL_STATS:
                if (player == physical) {
                    this.changes = changes;
                }
                infoFullStats(player);
                break;
            case HEALTH_AND_ARMOR:
                if (player == physical) {
                    this.changes = changes;
                }
                infoHealthAndArmor(player);
                break;
            case SKILLS:
                infoSkills(player);
                break;
            case TARGET_FULL_STATS:
                if (target == physical) {
                    this.changes = changes;
                }
                infoFullStats(target);
                break;
            case TARGET_HEALTH_AND_ARMOR:
                if (target == physical) {
                    this.changes = changes;
                }
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
        int yOffset = 1;
        showStats(yOffset, Stat.healths, physical);

        yOffset += Stat.healths.length + 1;
        showStats(yOffset, Stat.needs, physical);

        yOffset += Stat.needs.length + 1;

        if (physical.creatureData != null) {
            drawFigure(physical.creatureData, yOffset);

            yOffset += ClothingSlot.height + 4;

            // Equipped items
            Physical equippedRight = physical.creatureData.equipment.get(WieldSlot.RIGHT_HAND);
            Physical equippedLeft = physical.creatureData.equipment.get(WieldSlot.LEFT_HAND);
            put(3, yOffset + 0, "RH:");
            if (equippedRight != null) {
                put(8, yOffset + 0, equippedRight.name, equippedRight.rarity.color());
            } else {
                put(8, yOffset + 0, "empty", Rating.NONE.color());
            }

            put(3, yOffset + 1, "LH:");
            if (equippedLeft != null) {
                put(8, yOffset + 1, equippedLeft.name, equippedLeft.rarity.color());
            } else {
                put(8, yOffset + 1, "empty", Rating.NONE.color());
            }
            if (equippedLeft == null && equippedRight == null) {
                put(3, yOffset + 2, "Fighting unarmed using " + physical.weaponData.rawWeapon.name);
            }
        }
    }

    private void drawFigure(Creature data, int startY) {
        // left and right are when viewed from behind, i.e. with an over-the-shoulder camera
        int yOffset = startY + 2;
        int titleOffset = startY;

        int x = 3;
        for (ClothingSlot cs : ClothingSlot.values()) {
            Physical p = data.armor.get(cs);
            put(x + cs.location.x, yOffset + cs.location.y, cs.drawn, p == null ? Rating.NONE.color() : p.rarity.color());
        }
        put(x, titleOffset, "Armor");
        put(x, titleOffset + 1, "Rarity");

        x += ClothingSlot.width + 4;
        for (ClothingSlot cs : ClothingSlot.values()) {
            Physical p = data.armor.get(cs);
            Color color = Rating.NONE.color();
            if (p != null) {
                LiveValue lv = p.stats.get(Stat.STRUCTURE);
                if (lv != null) {
                    color = percentColor(lv.actual(), lv.base());
                }
            }
            put(x + cs.location.x, yOffset + cs.location.y, cs.drawn, color);
        }
        put(x, titleOffset, "Armor");
        put(x, titleOffset + 1, "Health");

        x += ClothingSlot.width + 4;
        for (ClothingSlot cs : ClothingSlot.values()) {
            Physical p = data.clothing.get(cs);
            put(x + cs.location.x, yOffset + cs.location.y, cs.drawn, p == null ? Rating.NONE.color() : p.rarity.color());
        }
        put(x, titleOffset, "Clothes");
        put(x, titleOffset + 1, "Rarity");

        x += ClothingSlot.width + 4;
        for (ClothingSlot cs : ClothingSlot.values()) {
            Physical p = data.clothing.get(cs);
            Color color = Rating.NONE.color();
            if (p != null) {
                LiveValue lv = p.stats.get(Stat.STRUCTURE);
                if (lv != null) {
                    color = percentColor(lv.actual(), lv.base());
                }
            }
            put(x + cs.location.x, yOffset + cs.location.y, cs.drawn, color);
        }
        put(x, titleOffset, "Clothes");
        put(x, titleOffset + 1, "Health");
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
        double biggest = 0;
        for (Stat s : stats) {
            LiveValue lv = physical.stats.get(s);
            if (lv != null) {
                biggest = Math.max(biggest, Math.max(lv.base(), lv.actual()));
                biggest = Math.max(biggest, lv.actual() + changes.getOrDefault(s, 0.0));
            }
        }
        int biggestLength = Integer.toString((int) Math.ceil(biggest)).length();
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

            Double change = changes.get(stats[s]);
            if (change != null && change != 0) {
                double startValue = actual - change; // minus because looking for previous value
                filling = startValue / biggest;
                int priorBlocks = (int) Math.ceil(filling * blockValue);
                int changeBlocks = priorBlocks - blockText.length();
                int startX = widestStatSize + 2 + numberText.length() + 1 + priorBlocks - 1; // left both 1s in to show that it's the prior length but bumped into the final block of the prior size
                int endX = startX - changeBlocks;
                color = SColor.CW_RICH_JADE;
                if (change < 0) {
                    int temp = startX;
                    startX = endX;
                    endX = temp;
                    color = SColor.CW_RED;
                }
                //System.out.println(stats[s].toString() + ": " + change + " " + startX + ", " + endX);
                for (int x = startX; x <= endX; x++) {
                    //front.summon(x, s + offset, x, change > 0 ? s + offset - 1 : s + offset + 1, rng.getRandomElement(sparkles), color, SColor.TRANSPARENT, 800f, 1f);
                    damage(Coord.get(x, s + offset), color, physical.chaos);
                }
            }
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
            put(1, y + offset, Utilities.caps(entry.getKey().name));
            put(widestSkillSize + 2, y + offset, Utilities.caps(entry.getValue().toString()), color);
            y++;
        }
    }

    private void damage(Coord origin, Color color, StatefulRNG chaos) {
        fx.addAction(new DamageEffect(chaos.nextFloat(1.9f) +  1.2f, chaos.between(2, 4), origin,
            new float[]{
                    SColor.toEditedFloat(color, 0f, -0.6f, -0.2f, -0.3f),
                    SColor.toEditedFloat(color, 0f, -0.3f, 0f, -0.2f),
                    SColor.toEditedFloat(color, 0f, 0.3f, 0f, -0.1f),
                    SColor.toEditedFloat(color, 0f, 0.15f, 0.1f, 0f),
                    SColor.toEditedFloat(color, 0f, 0f, 0.7f, 0f),
                    SColor.toEditedFloat(color, 0f, -0.15f, 0.3f, 0f),
                    SColor.toEditedFloat(color, 0f, -0.3f, 0f, 0f),
                    SColor.toEditedFloat(color, 0f, -0.45f, -0.1f, 0.15f),
                    SColor.toEditedFloat(color, 0f, -0.6f, -0.2f, -0.3f),
            }));
    }

    public class DamageEffect extends PanelEffect {

        public int cycles;
        public float[] colors;
        public Coord c;

        public DamageEffect(float duration, int cycles, Coord center, float[] coloring) {
            super(front, duration);
            this.cycles = cycles;
            c = center;
            colors = coloring;
        }

        @Override
        protected void end() {
            super.end();
            fxBack.clear(c.x, c.y);
            fx.clear(c.x, c.y);
        }

        @Override
        protected void update(float percent) {
            float f, color;
            int idx, seed = System.identityHashCode(this);
            f = (float) SeededNoise.noise(c.x * 1.5, c.y * 1.5, percent * 0.015, seed) * 0.125f + percent;
            idx = (int) (f * colors.length);
            if (idx >= colors.length - 1) {
                color = SColor.lerpFloatColors(colors[colors.length - 1], NumberTools.setSelectedByte(colors[colors.length - 1], 3, (byte) 0), (Math.min(0.99f, f) * colors.length) % 1f);
            } else {
                color = SColor.lerpFloatColors(colors[idx], colors[idx + 1], (f * colors.length) % 1f);
            }
            fxBack.put(c.x, c.y, '█', SColor.translucentColor(back.getDefaultForegroundColor().toFloatBits(), 0.5f));
            fx.put(c.x, c.y, sparkles.charAt((int) Math.floor(percent * (sparkles.length() * cycles + 1)) % cycles), color);
        }
    }
}

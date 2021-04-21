package squidpony.epigon.display;

import com.badlogic.gdx.graphics.Color;
import squidpony.StringKit;
import squidpony.epigon.util.ConstantKey;
import squidpony.epigon.util.Utilities;
import squidpony.epigon.data.*;
import squidpony.epigon.data.slot.ClothingSlot;
import squidpony.epigon.data.slot.WieldSlot;
import squidpony.epigon.data.trait.Creature;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidmath.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import squidpony.epigon.game.Epigon;

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

    private final int widestStatSize = Arrays.stream(Stat.values()).mapToInt(s -> s.toString().length()).max().getAsInt();

    private SparseLayers layers;
    private int width;
    private int height;
    private InfoMode infoMode = InfoMode.HEALTH_AND_ARMOR;
    private SquidColorCenter colorCenter;
    private Physical player;
    private Physical target;
    private OrderedMap<ConstantKey, Double> changes = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
    private Epigon game;

    public Coord arrowLeft;
    public Coord arrowRight;

    public InfoHandler(SparseLayers layers, SquidColorCenter colorCenter, Epigon game) {
        this.layers = layers;
        this.colorCenter = colorCenter;
        this.game = game;

        width = layers.gridWidth;
        height = layers.gridHeight;
        layers.addLayer();
        layers.addLayer();

        arrowLeft = Coord.get(1, 0);
        arrowRight = Coord.get(width - 2, 0);

        layers.fillBackground(layers.defaultPackedBackground);
    }

    public void setPlayer(Physical player) {
        this.player = player;
    }

    public void setTarget(Physical target) {
        if (this.target == target) {
            return;
        }
        this.target = target;
        updateDisplay();
    }

    private void clear() {
        layers.clear();
        int w = width;
        int h = height;
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
                    title = title.replace("Target", Utilities.caps(target.name));
                }
                break;
        }
        int x = width / 2 - title.length() / 2;
        put(x, 0, title);

        put(arrowLeft.x + 2, arrowLeft.y, '{');
        put(arrowLeft.x, arrowLeft.y, '◀');
        put(arrowRight.x - 2, arrowRight.y, '}');
        put(arrowRight.x, arrowRight.y, '▶');
    }

    private void put(int x, int y, String s) {
        layers.put(x, y, GDXMarkup.instance.colorString(s));
    }

    private void put(int x, int y, String s, Color color) {
        layers.put(x, y, s, color, null);
    }

    private void put(int x, int y, String s, float color) {
        layers.put(x, y, s, color, 0f);
    }

    private void put(int x, int y, char c) {
        layers.put(x, y, c, layers.defaultPackedForeground);
    }

    private void put(int x, int y, char c, Color color) {
        layers.put(x, y, c, color, null);
    }

    private void put(int x, int y, char c, float color) {
        layers.put(x, y, c, color, 0f);
    }

    public void next() {
        layers.summon(arrowRight.x, arrowRight.y, arrowRight.x + 1, arrowRight.y - 2, '✔', -0x1.abed4ap125F,//SColor.CW_HONEYDEW,
            SColor.translucentColor(-0x1.abed4ap125F, 0.2f), 0.6f);
        infoMode = infoMode.next();
        updateDisplay();
    }

    public void prior() {
        layers.summon(arrowLeft.x, arrowLeft.y, arrowLeft.x + 1, arrowLeft.y - 2, '✔', -0x1.abed4ap125F,//SColor.CW_HONEYDEW,
            SColor.translucentColor(-0x1.abed4ap125F, 0.2f), 0.6f);
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
        updateDisplay(null, new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance));
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
     * @param physical
     * @param changes  a Map of the changes to stats
     */
    public void updateDisplay(Physical physical, OrderedMap<ConstantKey, Double> changes) {
        this.changes.clear();// = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
        switch (infoMode) {
            case FULL_STATS:
                if (player == physical) {
                    this.changes.putAll(changes);
                }
                infoFullStats(player);
                break;
            case HEALTH_AND_ARMOR:
                if (player == physical) {
                    this.changes.putAll(changes);
                }
                infoHealthAndArmor(player);
                break;
            case SKILLS:
                infoSkills(player);
                break;
            case TARGET_FULL_STATS:
                if (target == physical) {
                    this.changes.putAll(changes);
                }
                infoFullStats(target);
                break;
            case TARGET_HEALTH_AND_ARMOR:
                if (target == physical) {
                    this.changes.putAll(changes);
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
        for (String key : Stat.groups.keySet()) { // TODO - it appear that getting the entry pairs is not typed, should they be?
            put(width / 2, offset, Utilities.capitalizeFirst(key), SColor.FLAX);
            offset++;
            showStats(offset, Stat.groups.get(key), physical);
            offset += Stat.groups.get(key).length + 1;
        }
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

            boolean offenseFound = false;
            for (WieldSlot slot : WieldSlot.ALL) {
                Physical equipped = physical.creatureData.equippedBySlot.get(slot);
                if (equipped == null) {
                    continue;
                }
                put(3, yOffset, slot.shortCode());
                putWeaponInfo(8, yOffset, physical, equipped);

                yOffset++;
                offenseFound = true;
            }

            int xOffset = 3;
            if (physical.creatureData.weaponChoices != null && physical.creatureData.weaponChoices.items() != null && !physical.creatureData.weaponChoices.items().isEmpty()) {
                Weapon currentWeapon = physical.creatureData.weaponChoices.items().first();
                String text = "BARE " + currentWeapon.rawWeapon.name + Utilities.getRangeText(currentWeapon) + " ₩" + physical.creatureData.skillWithWeapon(currentWeapon);
                int workingLength = width - 2; //inside the border width
                workingLength -= xOffset;
                if (text.length() > workingLength) { // TODO - refactor to be in string-based put operation
                    text = text.substring(0, workingLength - 1);
                    text += "…";
                }
                put(xOffset, yOffset++, text);
            } else if (!offenseFound) {
                put(xOffset, yOffset++, "Offenseless");
            }
            yOffset++;
            if (physical.conditions.isEmpty()) {
                put(xOffset, yOffset, "Condition: healthy ([/]for now...[/])");
            } else if (physical.conditions.size() == 1) {
                put(xOffset, yOffset, "Condition: " + physical.conditions.getAt(0).parent.adjective);
            } else {
                StringBuilder sb = new StringBuilder("Conditions: ").append(physical.conditions.getAt(0).parent.adjective);
                for (int i = 1; i < physical.conditions.size(); i++) {
                    sb.append(", ").append(physical.conditions.getAt(i).parent.adjective);
                }
                ArrayList<String> wrapped = new ArrayList<>();
                StringKit.wrap(wrapped, sb, width - 6);
                for (int i = 0; i < wrapped.size(); i++) {
                    put(xOffset, yOffset++, wrapped.get(i));
                }

            }
        }
    }

    private void putWeaponInfo(int x, int y, Physical physical, Physical weapon) {
        if (weapon == null || weapon.weaponData == null) {
            put(x, y, "empty", Rating.NONE.color());
            return;
        }

        //₩ - for skill annotation
        Weapon weaponData = weapon.weaponData;
        // TODO - adjust for width available
        String rangeText = Utilities.getRangeText(weaponData);
        String text = weapon.name + rangeText + " ₩" + physical.creatureData.skillWithWeapon(weaponData);
        put(x, y, text, Utilities.progressiveLighten(weapon.color));
    }

    private void drawFigure(Creature data, int startY) {
        // left and right are when viewed from behind, i.e. with an over-the-shoulder camera
        int yOffset = startY + 2;
        int titleOffset = startY;

        int x = 3;
        for (ClothingSlot cs : ClothingSlot.ALL) {
            Physical p = data.equippedBySlot.get(cs);
            put(x + cs.location.x, yOffset + cs.location.y, cs.drawn, p == null ? Rating.NONE.color() : p.rarity.color());
        }
        put(x, titleOffset, "Worn");
        put(x, titleOffset + 1, "Rarity");

        x += ClothingSlot.width + 4;
        for (ClothingSlot cs : ClothingSlot.ALL) {
            Physical p = data.equippedBySlot.get(cs);
            Color color = Rating.NONE.color();
            if (p != null) {
                LiveValue lv = p.stats.get(Stat.STRUCTURE);
                if (lv != null) {
                    color = percentColor(lv.actual(), lv.base());
                }
            }
            put(x + cs.location.x, yOffset + cs.location.y, cs.drawn, color);
        }
        put(x, titleOffset, "Worn");
        put(x, titleOffset + 1, "Health");

        x += ClothingSlot.width + 4;
        for (ClothingSlot cs : ClothingSlot.ALL) {
            Physical p = data.equippedBySlot.get(cs);
            float color = p == null ? player.color : p.color;
            put(x + cs.location.x, yOffset + cs.location.y, cs.drawn, color);
        }
        put(x, titleOffset, "Worn");
        put(x, titleOffset + 1, "Appearance");
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

    private void showStats(int offset, ConstantKey[] stats, Physical physical) {
        double biggest = 0;
        for (ConstantKey s : stats) {
            LiveValue lv = physical.stats.get(s);
            if (lv != null) {
                biggest = Math.max(biggest, Math.max(lv.base(), lv.actual()));
                biggest = Math.max(biggest, lv.actual() + changes.getOrDefault(s, 0.0));
            }
        }
        int biggestLength = Integer.toString((int) Math.ceil(biggest)).length();
        String format = "%" + biggestLength + "d / %" + biggestLength + "d";

        for (int s = 0; s < stats.length && s < game.infoSize.gridHeight - 2; s++) {
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
                blockText += Utilities.eighthBlocks[7];
            }
            remainder = Math.max(remainder, 0);
            blockText += Utilities.eighthBlocks[(int) Math.ceil(remainder)];
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
                for (int x = startX; x <= endX; x++) {
                    //front.summon(x, s + offset, x, change > 0 ? s + offset - 1 : s + offset + 1, rng.getRandomElement(sparkles), color, SColor.TRANSPARENT, 800f, 1f);
                    damage(x, s + offset, color, physical);
                }
            }
        }
    }

    private void showSkills(int offset, Physical physical) {
        int widestSkillSize = physical.creatureData.skills.keySet().stream()
            .mapToInt(s -> s.toString().length())
            .max()
            .getAsInt();
        int y = 0;
        for (Entry<Skill, Rating> entry : physical.creatureData.skills.entrySet()) {
            Color color = entry.getValue().color();
            put(1, y + offset, Utilities.caps(entry.getKey().toString()));
            put(widestSkillSize + 2, y + offset, Utilities.caps(entry.getValue().toString()), color);
            y++;
        }
    }

    private void damage(int originX, int originY, Color color, IRNG rng) {
        layers.addAction(new DamageEffect(rng.nextFloat() * 1.9f + 1.2f, rng.between(2, 4), originX, originY, new float[]{
                SColor.toEditedFloat(color, 0f, -0.6f, -0.2f, -0.3f),
                SColor.toEditedFloat(color, 0f, -0.3f, 0f, -0.2f),
                SColor.toEditedFloat(color, 0f, 0.3f, 0f, -0.1f),
                SColor.toEditedFloat(color, 0f, 0.15f, 0.1f, 0f),
                SColor.toEditedFloat(color, 0f, 0f, 0.7f, 0f),
                SColor.toEditedFloat(color, 0f, -0.15f, 0.3f, 0f),
                SColor.toEditedFloat(color, 0f, -0.3f, 0f, 0f),
                SColor.toEditedFloat(color, 0f, -0.45f, -0.1f, 0.15f),
                SColor.toEditedFloat(color, 0f, -0.6f, -0.2f, -0.3f),
                }
            )
        );
    }

    public class DamageEffect extends PanelEffect {

        public int cycles;
        public float[] colors;
        public int x, y;

        public DamageEffect(float duration, int cycles, int centerX, int centerY, float[] coloring) {
            super(layers, duration);
            this.cycles = cycles;
            x = centerX;
            y = centerY;
            colors = coloring;
        }

        @Override
        protected void end() {
            super.end();
            layers.clear(x, y, 1);
            layers.clear(x, y, 2);
        }

        @Override
        protected void update(float percent) {
            float f, color;
            int idx, seed = System.identityHashCode(this);
            f = (float) WhirlingNoise.noise(x * 1.5, y * 1.5, percent * 0.015, seed) * 0.125f + 0.126f + percent * 0.875f;
            idx = (int) (f * colors.length);
            if (idx >= colors.length - 1) {
                color = SColor.lerpFloatColors(colors[colors.length - 1], NumberTools.setSelectedByte(colors[colors.length - 1], 3, (byte) 0), (Math.min(0.99f, f) * colors.length) % 1f);
            } else {
                color = SColor.lerpFloatColors(colors[idx], colors[idx + 1], (f * colors.length) % 1f);
            }
            layers.put(x, y, '█', SColor.translucentColor(layers.defaultPackedBackground, 0.5f), 1);
            layers.put(x, y, Utilities.sparkles.charAt((int) (percent * (Utilities.sparkles.length() * cycles + 1)) % cycles), color, 2);
        }
    }
}

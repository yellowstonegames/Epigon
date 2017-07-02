package squidpony.epigon.playground;

import squidpony.Maker;
import squidpony.epigon.data.blueprint.Inclusion;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static squidpony.epigon.Epigon.rng;

/**
 * Contains objects to use to test out connections.
 */
public class HandBuilt {

    public List<Stone> wallList = new ArrayList<>(),
        sedimentaryList = new ArrayList<>(),
        intrusiveList = new ArrayList<>(),
        extrusiveList = new ArrayList<>(),
        metamorphicList = new ArrayList<>();

    public List<Inclusion> gemList = new ArrayList<>(),
        sedimentaryGemList = new ArrayList<>(),
        intrusiveGemList = new ArrayList<>(),
        extrusiveGemList = new ArrayList<>(),
        metamorphicGemList = new ArrayList<>();

    public Physical playerBlueprint;
    public Physical swordBlueprint;
    public Physical doorBlueprint;

    public Modification makeWall;
    public Modification makeDoor;
    public Condition openDoor;

    public HandBuilt() {
        initWallLists();

        playerBlueprint = new Physical();
        playerBlueprint.name = "Plae Haa";
        playerBlueprint.description = "It's you!";
        playerBlueprint.notes = "Voted most likely to die in Adventurer's Middle School.";
        playerBlueprint.symbol = '@';
        playerBlueprint.color = SColor.BRIGHT_PINK;
        playerBlueprint.large = true;
        playerBlueprint.unique = true;
        playerBlueprint.possibleAliases = Maker.makeList("Mario", "Link", "Sam");
        Arrays.stream(Stat.values()).forEach(s -> {
            LiveValue lv = new LiveValue(rng.between(20, 100));
            lv.actual = lv.base * (rng.nextDouble() + 0.1);
            playerBlueprint.stats.put(s, lv);
        });
        playerBlueprint.stats.put(Stat.SIGHT, new LiveValue(8));
        Creature cb = new Creature();
        playerBlueprint.creatureData = cb;
        cb.skills = new OrderedMap<>();
        Skill skill = new Skill();
        skill.name = "kendo";
        cb.skills.put(skill, Rating.HIGH);
        skill = new Skill();
        skill.name = "akido";
        cb.skills.put(skill, Rating.SLIGHT);

        swordBlueprint = new Physical();
        swordBlueprint.name = "sword";
        swordBlueprint.color = SColor.SILVER;
        swordBlueprint.symbol = '/';

        doorBlueprint = new Physical();
        doorBlueprint.name = "door";
        doorBlueprint.symbol = '+';
        doorBlueprint.color = SColor.WALNUT;
        doorBlueprint.large = true;

        makeWall = new Modification();
        Collections.addAll(makeWall.possiblePrefix, "solid", "shaped");
        makeWall.possiblePostfix.add("wall");
        makeWall.symbol = '#';
        makeWall.large = true;

        makeDoor = new Modification();
        makeDoor.possiblePrefix.add("door of");
        makeDoor.symbol = '+';
        makeDoor.large = true;
    }

    private void initWallLists() {
        for (Stone stone : Stone.values()) {
            wallList.add(stone);
            if (stone.sedimentary) {
                sedimentaryList.add(stone);
            }
            if (stone.intrusive) {
                intrusiveList.add(stone);
            }
            if (stone.extrusive) {
                extrusiveList.add(stone);
            }
            if (stone.metamorphic) {
                metamorphicList.add(stone);
            }
        }

        for (Inclusion inclusion : Inclusion.values()) {
            gemList.add(inclusion);
            if (inclusion.sedimentary) {
                sedimentaryGemList.add(inclusion);
            }
            if (inclusion.intrusive) {
                intrusiveGemList.add(inclusion);
            }
            if (inclusion.extrusive) {
                extrusiveGemList.add(inclusion);
            }
            if (inclusion.metamorphic) {
                metamorphicGemList.add(inclusion);
            }
        }
    }
}

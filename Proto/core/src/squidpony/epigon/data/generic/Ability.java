package squidpony.epigon.data.generic;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.Rating;

/**
 * An ability is an action choice a creature, item, or condition can make.
 *
 * It represents an attempt to make a change to the game world in some way during normal game play.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Ability extends EpiData {

    //AOE
    //range
    //vs creatures / items / open space
    //

    public boolean appliesToAllInTile;
    public boolean appliesToAllInArea;
    public boolean randomChoiceInRange;
    public boolean requiresConfirmation;

    public OrderedMap<Skill, Rating> mustHaveSkillRatings;
    public List<String> effectsString = new ArrayList<>();
    public List<Effect> effects = new ArrayList<>();

    //preconditions that must be satisfied
//    public EnumMap<ActionParticipantType, List<Map<Physical, Integer>>> mustPossess = new EnumMap<>(ActionParticipantType.class);
//    public EnumMap<ActionParticipantType, List<Physical>> mustHaveEquipped = new EnumMap<>(ActionParticipantType.class);
//    public EnumMap<ActionParticipantType, List<Map<Physical, Integer>>> consumes = new EnumMap<>(ActionParticipantType.class);
//    public EnumMap<ActionParticipantType, List<Map<Physical, Integer>>> consumesEquipped = new EnumMap<>(ActionParticipantType.class);
//    public EnumMap<ActionParticipantType, List<ConditionBlueprint>> mustHaveCondition = new EnumMap<>(ActionParticipantType.class);
//    public EnumMap<ActionParticipantType, List<ConditionBlueprint>> mustNotHaveCondition = new EnumMap<>(ActionParticipantType.class);

}

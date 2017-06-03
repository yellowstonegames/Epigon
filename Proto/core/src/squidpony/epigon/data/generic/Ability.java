package squidpony.epigon.data.generic;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import squidpony.epigon.actions.ActionParticipantType;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.universe.Rating;
import squidpony.squidmath.OrderedMap;

/**
 * An ability is an action choice a creature, item, or condition can make.
 *
 * It represents an attempt to make a change to the game world in some way during normal game play.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Ability extends EpiData {

    public Map<String, Integer> contribution;
    //AOE
    //range
    //vs creatures / items / open space
    //

    public List<String> effectsString = new ArrayList<>();
    public OrderedMap<Skill, Rating> mustHaveSkillRatings;
    EnumMap<ActionParticipantType, Effect> effects = new EnumMap<>(ActionParticipantType.class);

    //preconditions that must be satisfied
    public EnumMap<ActionParticipantType, List<Map<PhysicalBlueprint, Integer>>> mustPossess = new EnumMap<>(ActionParticipantType.class);
    public EnumMap<ActionParticipantType, List<PhysicalBlueprint>> mustHaveEquipped = new EnumMap<>(ActionParticipantType.class);
    public EnumMap<ActionParticipantType, List<Map<PhysicalBlueprint, Integer>>> consumes = new EnumMap<>(ActionParticipantType.class);
    public EnumMap<ActionParticipantType, List<Map<PhysicalBlueprint, Integer>>> consumesEquipped = new EnumMap<>(ActionParticipantType.class);
    public EnumMap<ActionParticipantType, List<ConditionBlueprint>> mustHaveCondition = new EnumMap<>(ActionParticipantType.class);
    public EnumMap<ActionParticipantType, List<ConditionBlueprint>> mustNotHaveCondition = new EnumMap<>(ActionParticipantType.class);

    /**
     * Determines that all needed prerequisites are taken care of.
     *
     * @param creature
     * @return
     */
    public boolean fufillsPrerequisites(Creature source, Creature target) {//TODO -- handle creatureless target spaces
        //TODO - 

        //check possesions
//        //self
//        for (TreeMap<ItemBlueprint, Integer> map : mustPossess.get(ActionParticipantType.SOURCE)) {
//            if (!source.hasItems(map)) {//default to not exclusive mode
//                return false;//didn't have something that was needed
//            }
//        }
//        //target
//        for (TreeMap<ItemBlueprint, Integer> map : mustPossess.get(ActionParticipantType.TARGET)) {
//            if (!target.hasItems(map)) {//default to not exclusive mode
//                return false;//didn't have something that was needed
//            }
//        }
//
//        //check for self equipped items
//        for (ItemBlueprint item : mustHaveEquipped.get(ActionParticipantType.SOURCE)) {
//            if (!source.hasEquipped(item)) {//default to not exclusive mode
//                return false;//didn't have something that was needed
//            }
//        }
//
//        //check for target equipped items
//        for (ItemBlueprint item : mustHaveEquipped.get(ActionParticipantType.TARGET)) {
//            if (!target.hasEquipped(item)) {//default to not exclusive mode
//                return false;//didn't have something that was needed
//            }
//        }

//        //check for must have conditions of the source
//        for (ConditionBlueprint condition : mustHaveCondition.get(ActionParticipantType.SOURCE)) {//TODO -- make it deal with multiple similar items
//            if (!source.hasCondition(condition)) {
//                return false;//didn't have the needed condition
//            }
//        }
//
//        //check for must have conditions of the target
//        for (ConditionBlueprint condition : mustHaveCondition.get(ActionParticipantType.TARGET)) {//TODO -- make it deal with multiple similar items
//            if (!target.hasCondition(condition)) {
//                return false;//didn't have the needed condition
//            }
//        }
//
//        //check for must not have conditions of the source
//        for (ConditionBlueprint condition : mustHaveCondition.get(ActionParticipantType.SOURCE)) {//TODO -- make it deal with multiple similar items
//            if (source.hasCondition(condition)) {
//                return false;//had a contraindicated condition
//            }
//        }
//
//        //check for must not have conditions of the target
//        for (ConditionBlueprint condition : mustHaveCondition.get(ActionParticipantType.TARGET)) {//TODO -- make it deal with multiple similar items
//            if (target.hasCondition(condition)) {
//                return false;//had a contraindicated condition
//            }
//        }

        return true;//if it made it here then everything needed was found and nothing counter was found
    }
}

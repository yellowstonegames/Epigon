package squidpony.epigon.data.generic;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import squidpony.epigon.ActionParticipantType;
import squidpony.epigon.actions.AttackAction;
import squidpony.epigon.actions.ConditionAddAction;
import squidpony.epigon.actions.ConditionRemoveAction;
import squidpony.epigon.actions.CreateObjectAction;
import squidpony.epigon.actions.DestroyObjectAction;
import squidpony.epigon.actions.InsertIntoAction;
import squidpony.epigon.actions.MovementAction;
import squidpony.epigon.actions.RemoveFromAction;
import squidpony.epigon.actions.StatChangeAction;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.ConditionBlueprint;
import squidpony.epigon.data.blueprints.PhysicalBlueprint;
import squidpony.epigon.data.interfaces.Creature;
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
    //preconditions that must be satisfied
    public List<String> effectsString = new ArrayList<>();
    public OrderedMap<Skill, Rating> mustHaveSkillRatings;
    EnumMap<ActionParticipantType, Effect> effects = new EnumMap<>(ActionParticipantType.class);
    //
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

    /**
     * Returns true if this ability maps attempted damage to another object.
     *
     * @return
     */
    public boolean isAttack() {
        for (Effect effect : effects.values()) {
            if (effect.action instanceof AttackAction) {
                return true;
            }
        }
        return false;//no actions found
    }

    public boolean isMovement() {
        for (Effect effect : effects.values()) {
            if (effect.action instanceof MovementAction) {
                return true;
            }
        }
        return false;//no actions found
    }

    public boolean changesInventory() {
        for (Effect effect : effects.values()) {
            if (effect.action instanceof InsertIntoAction || effect.action instanceof RemoveFromAction) {
                return true;
            }
        }
        return false;//no actions found
    }

    public boolean changesAura() {
        for (Effect effect : effects.values()) {
            if (effect.action instanceof ConditionAddAction || effect.action instanceof ConditionRemoveAction) {
                return true;
            }
        }
        return false;//no actions found
    }

    public boolean createsObject() {
        for (Effect effect : effects.values()) {
            if (effect.action instanceof CreateObjectAction) {
                return true;
            }
        }
        return false;//no actions found
    }

    public boolean destroysObject() {
        for (Effect effect : effects.values()) {
            if (effect.action instanceof DestroyObjectAction) {
                return true;
            }
        }
        return false;//no actions found
    }

    public boolean changesStats() {
        for (Effect effect : effects.values()) {
            if (effect.action instanceof StatChangeAction) {
                return true;
            }
        }
        return false;//no actions found
    }
}

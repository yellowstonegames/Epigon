package squidpony.epigon.data.generic;

import java.util.ArrayList;
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
    public boolean appliesToAllInTile; // All objects in a target tile are affected
    public boolean appliesToAllTilesInArea;
    public boolean randomChoiceInRange;
    public boolean requiresConfirmation;
    public int maxTargets = Integer.MAX_VALUE;

    public OrderedMap<Skill, Rating> mustHaveSkillRatings = new OrderedMap<>();
    public List<String> effectsString = new ArrayList<>();
    public List<Effect> effects = new ArrayList<>();

    // Preconditions for user of ability
    public List<Map<Physical, Integer>> mustPossess = new ArrayList<>();
    public List<Physical> mustHaveEquipped = new ArrayList<>();
    public List<Map<Physical, Integer>> consumes = new ArrayList<>();
    public List<Map<Physical, Integer>> consumesEquipped = new ArrayList<>();
    public List<ConditionBlueprint> mustHaveCondition = new ArrayList<>();
    public List<ConditionBlueprint> mustNotHaveCondition = new ArrayList<>();

    public List<Map<Physical, Integer>> targetMustPossess = new ArrayList<>();
    public List<Physical> targetMustHaveEquipped = new ArrayList<>();
    public List<ConditionBlueprint> targetMustHaveCondition = new ArrayList<>();
    public List<ConditionBlueprint> targetMustNotHaveCondition = new ArrayList<>();

}

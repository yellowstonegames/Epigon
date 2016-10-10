package squidpony.data.generic;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import squidpony.data.EpiData;
import squidpony.data.Stat;
import squidpony.data.blueprints.ConditionBlueprint;
import squidpony.data.blueprints.ItemBlueprint;
import squidpony.data.blueprints.RecipeBlueprint;
import squidpony.universe.Rating;

/**
 * A grouping of starting skills and stats plus an aptitude towards increasing
 * them, starting equipment, recipes known, starting perks, starting conditions,
 * and starting abilities.
 *
 * Creatures should have a list of what kinds of professions they can have.
 * Modifications may change their list.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Profession extends EpiData {

    private HashMap<Skill, Rating> initialSkillRatings = new HashMap<>();
    private HashMap<Skill, Rating> skillProgress = new HashMap<>();
    private EnumMap<Stat, Integer> initialStatLevels = new EnumMap<>(Stat.class);
    private EnumMap<Stat, Rating> statProgress = new EnumMap<>(Stat.class);
    private EnumMap<Energy, Integer> initialEnergyLevels = new EnumMap<>(Energy.class);
    private EnumMap<Energy, Rating> energyLevelProgress = new EnumMap<>(Energy.class);
    private ArrayList<ItemBlueprint> items = new ArrayList<>();
    private ArrayList<RecipeBlueprint> recipes = new ArrayList<>();
    private ArrayList<ConditionBlueprint> perks = new ArrayList<>();
    private ArrayList<ConditionBlueprint> condistions = new ArrayList<>();
    private ArrayList<Ability> abilities = new ArrayList<>();

    public HashMap<Skill, Rating> getInitialSkillRatings() {
        return initialSkillRatings;
    }

    public void setInitialSkillRatings(HashMap<Skill, Rating> initialSkillRatings) {
        this.initialSkillRatings = initialSkillRatings;
    }

    public HashMap<Skill, Rating> getSkillProgress() {
        return skillProgress;
    }

    public void setSkillProgress(HashMap<Skill, Rating> skillProgress) {
        this.skillProgress = skillProgress;
    }

    public EnumMap<Stat, Rating> getStatProgress() {
        return statProgress;
    }

    public void setStatProgress(EnumMap<Stat, Rating> statProgress) {
        this.statProgress = statProgress;
    }

    public ArrayList<ItemBlueprint> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemBlueprint> items) {
        this.items = items;
    }

    public ArrayList<RecipeBlueprint> getRecipes() {
        return recipes;
    }

    public void setRecipes(ArrayList<RecipeBlueprint> recipes) {
        this.recipes = recipes;
    }

    public ArrayList<ConditionBlueprint> getPerks() {
        return perks;
    }

    public void setPerks(ArrayList<ConditionBlueprint> perks) {
        this.perks = perks;
    }

    public ArrayList<ConditionBlueprint> getCondistions() {
        return condistions;
    }

    public void setCondistions(ArrayList<ConditionBlueprint> condistions) {
        this.condistions = condistions;
    }

    public ArrayList<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(ArrayList<Ability> abilities) {
        this.abilities = abilities;
    }

    public EnumMap<Stat, Integer> getInitialStatLevels() {
        return initialStatLevels;
    }

    public void setInitialStatLevels(EnumMap<Stat, Integer> initialStatLevels) {
        this.initialStatLevels = initialStatLevels;
    }

    public EnumMap<Energy, Integer> getInitialEnergyLevels() {
        return initialEnergyLevels;
    }

    public void setInitialEnergyLevels(EnumMap<Energy, Integer> initialEnergyLevels) {
        this.initialEnergyLevels = initialEnergyLevels;
    }

    public EnumMap<Energy, Rating> getEnergyLevelProgress() {
        return energyLevelProgress;
    }

    public void setEnergyLevelProgress(EnumMap<Energy, Rating> energyLevelProgress) {
        this.energyLevelProgress = energyLevelProgress;
    }
}

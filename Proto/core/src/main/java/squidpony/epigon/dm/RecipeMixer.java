package squidpony.epigon.dm;

import squidpony.epigon.data.blueprint.*;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.mixin.*;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Recipe;
import squidpony.epigon.data.specific.Weapon;
import squidpony.epigon.universe.*;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static squidpony.epigon.Epigon.rootChaos;

/**
 * This class does all the recipe mixing. It has methods for creating objects based on recipes in
 * various categories.
 *
 * There is some danger in using the construction bits as having an object A that creates B that create a new A will cause an infinite loop.
 * TODO - figure out a way to check for and prevent that loop...
 * TODO - solve halting problem...
 *
 * Results may be based on using a specific recipe with specific items, or by looking for a result
 * in a recipe and then building it with that recipe.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class RecipeMixer {

    public List<RecipeBlueprint> recipes;

    private Map<Material, Physical> materials = new HashMap<>();

    public StatefulRNG chaos, rng = new StatefulRNG(123456789L);
    public RecipeMixer()
    {
        chaos = new StatefulRNG(rootChaos.nextLong());
    }
    public Stream<RecipeBlueprint> blueprintsContainingIngredient(Physical ingredient) {
        return recipes.stream().filter(r -> r.uses(ingredient));
    }

    public Recipe createRecipe(RecipeBlueprint blueprint) {
        Recipe recipe = new Recipe();
        recipe.consumed = new OrderedMap<>(blueprint.requiredConsumed);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalConsumed != null && !blueprint.optionalConsumed.isEmpty()) {
            Entry<Physical, Integer> entry = blueprint.optionalConsumed.randomEntry(chaos);
            recipe.consumed.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        recipe.catalyst = new OrderedMap<>(blueprint.requiredCatalyst);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalCatalyst != null && !blueprint.optionalCatalyst.isEmpty()) {
            Entry<Physical, Integer> entry = blueprint.optionalCatalyst.randomEntry(chaos);
            recipe.catalyst.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        recipe.result = new OrderedMap<>();

        // TODO - modify results based on chosen optionals
        recipe.result.putAll(blueprint.result);

        return recipe;
    }

    public List<Physical> mix(Recipe recipe, List<Physical> consumed, List<Physical> catalyst) {
        return mix(recipe, consumed, catalyst, rng.nextLong());
    }

    public List<Physical> mix(Recipe recipe, List<Physical> consumed, List<Physical> catalyst, long state) {
        List<Physical> result = new ArrayList<>();
        long prevState = rng.getState();
        rng.setState(state);
        for (int i = 0; i < recipe.result.size(); i++) {
            Physical physical = buildPhysical(recipe.result.keyAt(i));
            Stream.of(consumed.stream(), catalyst.stream())
                    .flatMap(m -> m)
                    .map(m -> m.whenUsedAsMaterial)
                    .flatMap(Collection::stream)
                    .forEach(modification -> applyModification(physical, modification));
            physical.stats.values().forEach(lv -> lv.actual(lv.base()));// Make sure actual is set to base value on first creation
            physical.calculateStats();
            if(physical.groupingData != null) {
                for (int j = 0; j < physical.groupingData.quantity; j++) {
                    result.add(physical);
                }
            }
            else
                result.add(physical);
        }
        rng.setState(prevState);
        return result;
    }

    public Physical buildWeapon(Weapon weapon, long state)
    {
        OrderedSet<Material> materials = Weapon.makes.get(weapon.materialTypes[0]);
        Material mat = materials.getAt(ThrustAltRNG.determineBounded(state--, materials.size()));
        return mix(createRecipe(weapon.recipeBlueprint), Collections.emptyList(), Collections.singletonList(buildMaterial(mat)), state).get(0);
    }

    public Physical buildPhysical(Stone stone) {
        Physical blueprint = materials.get(stone);
        if (blueprint != null) {
            return blueprint;
        }

        blueprint = new Physical();
        blueprint.color = stone.front.toFloatBits();
        blueprint.name = stone.toString();
        blueprint.baseValue = stone.value;
        blueprint.symbol = '.';
        blueprint.stats.put(Stat.STRUCTURE, new LiveValue(stone.hardness * 0.01));

        Modification stoneMod = new Modification();
        stoneMod.baseValueMultiplier = stone.value * 0.01;
        stoneMod.color = stone.front;
        stoneMod.possiblePrefix = Collections.singletonList(stone.toString());
        LiveValueModification lvm = new LiveValueModification();
        lvm.baseOverwrite = stone.hardness * 0.01;
        lvm.actualOverwrite = stone.getHardness() * 0.01;
        stoneMod.statChanges.put(Stat.STRUCTURE, lvm);
        blueprint.whenUsedAsMaterial.add(stoneMod);

        Terrain terrain = new Terrain();
        terrain.background = stone.back;
        terrain.stone = stone;
        terrain.extrusive = stone.extrusive;
        terrain.intrusive = stone.intrusive;
        terrain.metamorphic = stone.metamorphic;
        terrain.sedimentary = stone.sedimentary;
        blueprint.terrainData = terrain;

        materials.put(stone, blueprint);
        return blueprint;
    }

    public Physical buildPhysical(Inclusion inclusion) {
        Physical blueprint = materials.get(inclusion);
        if (blueprint != null) {
            return blueprint;
        }

        blueprint = new Physical();
        blueprint.color = inclusion.front.toFloatBits();//toRandomizedFloat(rng, 0.05f, 0f, 0.15f);
        blueprint.name = inclusion.toString();
        blueprint.baseValue = inclusion.value;
        blueprint.symbol = '.';
        blueprint.stats.put(Stat.STRUCTURE, new LiveValue(inclusion.hardness * 0.01));

        Modification inclusionMod = new Modification();
        inclusionMod.baseValueMultiplier = inclusion.value * 0.01;
        inclusionMod.color = inclusion.front;
        inclusionMod.possiblePrefix = Collections.singletonList(inclusion.toString());
        LiveValueModification lvm = new LiveValueModification();
        lvm.baseOverwrite = inclusion.hardness * 0.01;
        lvm.actualOverwrite = inclusion.getHardness() * 0.01;
        inclusionMod.statChanges.put(Stat.STRUCTURE, lvm);
        blueprint.whenUsedAsMaterial.add(inclusionMod);

        Terrain terrain = new Terrain();
        terrain.background = inclusion.back;
        terrain.extrusive = inclusion.extrusive;
        terrain.intrusive = inclusion.intrusive;
        terrain.metamorphic = inclusion.metamorphic;
        terrain.sedimentary = inclusion.sedimentary;
        blueprint.terrainData = terrain;

        return blueprint;
    }

    public Physical buildMaterial(Material material) {
        Physical blueprint = materials.get(material);
        if (blueprint != null) {
            return blueprint;
        }

        blueprint = new Physical();
        blueprint.color = material.getMaterialColor().toFloatBits();
        blueprint.name = material.toString();
        blueprint.baseValue = material.getValue();
        blueprint.symbol = material.getGlyph();
        blueprint.stats.put(Stat.STRUCTURE, new LiveValue(material.getHardness() * 0.01));

        Modification inclusionMod = new Modification();
        inclusionMod.baseValueMultiplier = material.getValue() * 0.01;
        inclusionMod.color = material.getMaterialColor();
        inclusionMod.possiblePrefix = Collections.singletonList(material.toString());
        LiveValueModification lvm = new LiveValueModification();
        lvm.baseOverwrite = material.getHardness() * 0.01;
        lvm.actualOverwrite = material.getHardness() * 0.01;
        inclusionMod.statChanges.put(Stat.STRUCTURE, lvm);
        blueprint.whenUsedAsMaterial.add(inclusionMod);

        return blueprint;
    }


    /**
     * Builds a new Physical based on the passed in one as exactly as possible.
     *
     * @param blueprint
     * @return
     */
    public Physical buildPhysical(Physical blueprint) {
        return buildPhysical(blueprint, blueprint.rarity == null ? Rating.NONE : blueprint.rarity, false);
    }

    /**
     * Builds a new Physical based on the passed in one and applies the given rarity. Applies all rarity
     * modifications up to the provided rarity.
     *
     * @param blueprint
     * @param rarity
     * @return
     */
    public Physical buildPhysical(Physical blueprint, Rating rarity) {
        return buildPhysical(blueprint, rarity, true);
    }

    /**
     * Builds a new Physical based on the passed in one and applies the given rarity only if the
     * boolean passed in is true.
     * 
     * @param blueprint
     * @param rarity
     * @param applyRatingModifications
     * @return
     */
    public Physical buildPhysical(Physical blueprint, Rating rarity, boolean applyRatingModifications){
        if (blueprint.generic) {
            // TODO - figure out how to allow sub instances of generics to be used without using generics
        }

        if (blueprint.unique) {
            // TODO - check for whether one has been created
        }

        Physical physical = new Physical();

        physical.description = blueprint.description;
        physical.parent = blueprint;

        physical.attached = blueprint.attached;

        List<String> possibleNames = new ArrayList<>(blueprint.possibleAliases);
        possibleNames.add(blueprint.name);
        physical.name = rng.getRandomElement(possibleNames);
        physical.possibleAliases.addAll(blueprint.possibleAliases); // TODO - lock it to the one made once it's made?

        if (!blueprint.countsAs.isEmpty()) {
            physical.countsAs.addAll(blueprint.countsAs);
        }
        physical.createdFrom.add(blueprint); // TODO - limit to "important" items
        physical.generic = blueprint.generic;
        physical.unique = blueprint.unique;
        physical.buildingBlock = blueprint.buildingBlock;

        physical.symbol = blueprint.symbol;
        physical.color = blueprint.color == 0f ? SColor.GRAY.toRandomizedFloat(rng, 0.05f, 0f, 0.15f) : blueprint.color;
        physical.baseValue = blueprint.baseValue;
        physical.blocking = blueprint.blocking;

        physical.lightEmitted = blueprint.lightEmitted;
        physical.lightEmittedStrength = blueprint.lightEmittedStrength;

        physical.whenUsedAsMaterial.addAll(blueprint.whenUsedAsMaterial);

        physical.elementalDamageMultiplier = new OrderedMap<>(blueprint.elementalDamageMultiplier);

        // TODO - figure out whether conditions should be copied or only come from modifications
//        for (ConditionBlueprint c : blueprint.conditions) {
//            physical.applyCondition(createCondition(c));
//        }
        blueprint.stats.entrySet().stream().forEach(kvp -> {
            physical.stats.put(kvp.getKey(), new LiveValue(kvp.getValue()));
        });

        physical.statProgression.putAll(blueprint.statProgression);

        physical.calculateStats();

        blueprint.inventory.stream().forEach(i -> {
            physical.inventory.add(buildPhysical(i));
        });

        physical.physicalDrops = blueprint.physicalDrops;
        physical.elementDrops = blueprint.elementDrops;

        physical.identification.putAll(blueprint.identification);

        physical.creatureData = createCreature(blueprint.creatureData);

        physical.unarmedData = blueprint.unarmedData;
        physical.weaponData = blueprint.weaponData;

        physical.terrainData = blueprint.terrainData;
        physical.groupingData = blueprint.groupingData;
        // TODO - add rest of mixins

        // finally work any modifications
        for (Modification m : blueprint.requiredModifications) {
            applyModification(physical, m);
        }

        int count = rng.nextInt(blueprint.optionalModifications.size());
        int[] ints = rng.randomOrdering(count);
        for (int i = 0; i < count; i++) {
            applyModification(physical, blueprint.optionalModifications.get(ints[i]));
        }

        physical.rarity = rarity;
        for (Rating rating : Rating.values()) {
            List<Modification> mods = blueprint.rarityModifications.get(rating);
            if (mods != null) {
                for (Modification m : mods) {
                    applyModification(physical, m);
                }
            }
            if (rarity == rating) { // Only process up to expected rarity level
                break;
            }
        }

        return physical;
    }

    /**
     * Creates a specific Condition from a blueprint.
     */
    public Condition createCondition(ConditionBlueprint blueprint) {
        // TODO - createRecipe condition
        return new Condition();
    }

    public Creature createCreature(Creature other) {
        if (other == null) {
            return null;
        }

        Creature creature = new Creature();
        creature.parent = other.parent;
        creature.skills.putAll(other.skills);
        creature.abilities.addAll(other.abilities); // TODO - copy into new abilities

        for (Entry<ClothingSlot, Physical> entry : other.armor.entrySet()){
            if (entry.getValue() != null){
                creature.armor.put(entry.getKey(), buildPhysical(entry.getValue()));
            }
        }

        for (Entry<ClothingSlot, Physical> entry : other.clothing.entrySet()){
            if (entry.getValue() != null){
                creature.clothing.put(entry.getKey(), buildPhysical(entry.getValue()));
            }
        }

        for (Entry<WieldSlot, Physical> entry : other.equipment.entrySet()){
            if (entry.getValue() != null){
                creature.equipment.put(entry.getKey(), buildPhysical(entry.getValue()));
            }
        }

        for (Entry<JewelrySlot, Physical> entry : other.jewelry.entrySet()){
            if (entry.getValue() != null){
                creature.jewelry.put(entry.getKey(), buildPhysical(entry.getValue()));
            }
        }

        for (Entry<OverArmorSlot, Physical> entry : other.overArmor.entrySet()){
            if (entry.getValue() != null){
                creature.overArmor.put(entry.getKey(), buildPhysical(entry.getValue()));
            }
        }

        return creature;
    }

    /**
     * Applies the provided modification to the provided physical in place.
     */
    public Physical applyModification(Physical physical, Modification modification) {
        physical.modifications.add(modification);

        if (modification.possibleAliases != null) {
            physical.possibleAliases = new ArrayList<>(modification.possibleAliases);
        }

        physical.possibleAliases.addAll(modification.possibleAliasesAdd);

        int count = modification.possiblePrefix.size() + modification.possibleSuffix.size();
        if (count > 0) {
            int i = rng.nextInt(count);
            if (i < modification.possiblePrefix.size()) {
                physical.name = modification.possiblePrefix.get(i) + " " + physical.name;
            } else {
                i -= modification.possiblePrefix.size();
                physical.name += " " + modification.possibleSuffix.get(i);
            }
        }

        if (modification.parent != null) {
            if (modification.retainPreviousParent != null && modification.retainPreviousParent && physical.parent != null) {
                physical.countsAs.add(physical.parent);
            }
            physical.parent = modification.parent;
        } else if (modification.parentBecomesNull != null && modification.parentBecomesNull) {
            physical.parent = null;
        }

        if (modification.countsAs != null) {
            physical.countsAs = new UnorderedSet<>(modification.countsAs);
        } else {
            if (modification.countsAsGained != null) {
                physical.countsAs.addAll(modification.countsAsGained);
            }
            if (modification.countsAsLost != null) {
                physical.countsAs.removeAll(modification.countsAsLost);
            }
        }
        if(modification.contents != null)
        {
            if(physical.containerData == null) physical.containerData = new Container();
            physical.containerData.contents = new ArrayList<>(modification.contents);
        }
        else {
            if(modification.contentsAdditive != null)
            {
                if(physical.containerData == null) physical.containerData = new Container(modification.contentsAdditive.size(), modification.contentsAdditive);
                else physical.containerData.contents.addAll(modification.contentsAdditive);
            }
            if(modification.contentsSubtractive != null)
            {
                if(physical.containerData != null) physical.containerData.contents.removeAll(modification.contentsSubtractive);
            }
        }

        if (modification.attached != null) {
            physical.attached = modification.attached;
        }

        if (modification.generic != null) {
            physical.generic = modification.generic;
        }

        if (modification.unique != null) {
            physical.unique = modification.unique;
        }

        if (modification.buildingBlock != null) {
            physical.buildingBlock = modification.buildingBlock;
        }

        if (modification.symbol != null) {
            physical.symbol = modification.symbol;
        }

        if (modification.color != null) {
            physical.color = SColor.toRandomizedFloat(modification.color, rng, 0.05f, 0f, 0.15f);
        }

        if (modification.baseValue != null) {
            physical.baseValue = modification.baseValue;
        }

        if (modification.baseValueMultiplier != null) {
            physical.baseValue *= modification.baseValueMultiplier;
        }

        if (modification.large != null) {
            physical.blocking = modification.large;
        }

        if (modification.lightEmitted != 0f) {
            physical.lightEmitted = modification.lightEmitted;
        }

        if (modification.lightEmittedStrengthChange != null) {
            physical.lightEmittedStrength.modify(modification.lightEmittedStrengthChange);
        }

        if (modification.whenUsedAsMaterial != null) {
            physical.whenUsedAsMaterial = new ArrayList<>(modification.whenUsedAsMaterial);
        }
        if (modification.whenUsedAsMaterialAdditive != null) {
            physical.whenUsedAsMaterial.addAll(modification.whenUsedAsMaterialAdditive);
        }

        // TODO - required and optional modifications

        physical.elementalDamageMultiplier.putAll(modification.elementalDamageMultiplier);
        modification.elementDamageMultiplierChanges.entrySet()
            .stream()
            .forEach(e -> {
                LiveValue lv = physical.elementalDamageMultiplier.getOrDefault(e.getKey(), new LiveValue(1));
                lv.modify(e.getValue());
                physical.elementalDamageMultiplier.put(e.getKey(), lv);
            });

        // TODO - Conditions

        physical.stats.putAll(modification.stats);
        modification.statChanges.entrySet()
            .stream()
            .forEach(e -> {
                LiveValue lv = physical.stats.getOrDefault(e.getKey(), new LiveValue(1));
                lv.modify(e.getValue());
                physical.stats.put(e.getKey(), lv);
            });
        if (modification.calcStats != null && modification.calcStats.length == 11) {
            System.arraycopy(modification.calcStats, 0, physical.calcStats, 0, 11);
        } else {
            physical.calculateStats();
        }
        for (int i = 0; i < modification.calcStatChanges.size(); i++) {
            physical.calcStats[modification.calcStatChanges.keyAt(i).ordinal()] += modification.calcStatChanges.getAt(i);
        }
        physical.statProgression.putAll(modification.statProgression);
        modification.statProgressionChanges.entrySet()
            .stream()
            .forEach(e -> {
                Rating r = physical.statProgression.getOrDefault(e.getKey(), Rating.NONE);
                r = r.applyRatingValueModification(e.getValue());
                physical.statProgression.put(e.getKey(), r);
            });

        if (modification.inventory != null) {
            physical.inventory = modification.inventory
                .stream()
                .map(i -> buildPhysical(i))
                .collect(Collectors.toList());
        }
        if (modification.inventoryAdditive != null) {
            physical.inventory.addAll(modification.inventoryAdditive
                .stream()
                .map(i -> buildPhysical(i))
                .collect(Collectors.toList()));
        }
        if (modification.inventorySubtractive != null) {
            for (Physical subtract : modification.inventorySubtractive) {
                physical.inventory.removeIf(p -> p.countsAs(subtract));
            }
        }

        if (modification.optionalInventory != null) {
            physical.optionalInventory = modification.optionalInventory
                .stream()
                .map(i -> buildPhysical(i))
                .collect(Collectors.toList());
        }
        if (modification.optionalInventoryAdditive != null) {
            physical.optionalInventory.addAll(modification.optionalInventoryAdditive
                .stream()
                .map(i -> buildPhysical(i))
                .collect(Collectors.toList()));
        }
        if (modification.optionalInventorySubtractive != null) {
            for (Physical subtract : modification.optionalInventorySubtractive) {
                physical.optionalInventory.removeIf(p -> p.countsAs(subtract));
            }
        }

        if (modification.physicalDrops != null){
            physical.physicalDrops = modification.physicalDrops;
        }
        if (modification.elementDrops != null){
            physical.elementDrops = modification.elementDrops;
        }

        // TODO - identification

        // TODO - rarity Modifications

        if (modification.creature != null) {
            physical.creatureData = createCreature(modification.creature);
        }

        if (modification.removeCreature != null && modification.removeCreature == true){
            physical.creatureData = null;
        }

        if (physical.creatureData != null) {
            modification.skillChanges.entrySet()
                .stream()
                .forEach(e -> {
                    Rating rating = physical.creatureData.skills.getOrDefault(e.getKey(), Rating.NONE);
                    rating = rating.applyRatingValueModification(e.getValue());
                    physical.creatureData.skills.put(e.getKey(), rating);
                });

            modification.skillProgressionChanges.entrySet()
                .stream()
                .forEach(e -> {
                    Rating rating = physical.creatureData.skillProgression.getOrDefault(e.getKey(), Rating.NONE);
                    rating = rating.applyRatingValueModification(e.getValue());
                    physical.creatureData.skillProgression.put(e.getKey(), rating);
                });

            if (modification.abilities != null) {
                physical.creatureData.abilities = new HashSet(modification.abilities);
            }

            if (modification.abiliitiesAdditive != null) {
                physical.creatureData.abilities.addAll(modification.abiliitiesAdditive);
            }

            if (modification.abilitiesSubtractive != null) {
                physical.creatureData.abilities.removeAll(modification.abilitiesSubtractive);
            }

            if (modification.knownRecipesAdditive != null) {
                for (RecipeBlueprint rb : modification.knownRecipesAdditive) {
                    physical.creatureData.knownRecipes.add(createRecipe(rb));
                }
            }
        }

        // TODO - Ammunition data

        // TODO - Container data

        if (modification.quantity != null){
            if (physical.groupingData == null){
                physical.groupingData = new Grouping();
            }
            physical.groupingData.quantity = modification.quantity;
        }
        if (modification.quantityDelta != null){
            if (physical.groupingData == null){
                physical.groupingData = new Grouping();
            }
            physical.groupingData.quantity += modification.quantityDelta;
        }

        // TODO - Wearable changes

        if (modification.weaponData != null) {
            physical.weaponData = modification.weaponData.copy();
        } else if (physical.weaponData == null && (modification.weaponCalcDelta != null
            || modification.weaponStatusesAdditive != null
            || modification.weaponStatusesSubtractive != null
            || modification.weaponManeuversAdditive != null
            || modification.weaponManeuversSubtractive != null
            || modification.weaponElementsAdditive != null
            || modification.weaponElements != null)) {
            physical.weaponData = new Weapon();
        }
        if (modification.weaponStatusesAdditive != null) {
            physical.weaponData.statuses.addAll(modification.weaponStatusesAdditive);
        }
        if (modification.weaponStatusesSubtractive != null) {
            physical.weaponData.statuses.removeAll(modification.weaponStatusesSubtractive);
        }
        if (modification.weaponManeuversAdditive != null) {
            physical.weaponData.maneuvers.addAll(modification.weaponManeuversAdditive);
        }
        if (modification.weaponManeuversSubtractive != null) {
            physical.weaponData.maneuvers.addAll(modification.weaponManeuversSubtractive);
        }
        if (modification.weaponElements != null) {
            physical.weaponData.elements = modification.weaponElements.copy();
        }
        if (modification.weaponElementsAdditive != null) {
            physical.weaponData.elements.addAll(modification.weaponElementsAdditive.keySet(), modification.weaponElementsAdditive.values());
//            for (int i = 0; i < modification.weaponElementsAdditive.size(); i++) {
//                Element e = modification.weaponElementsAdditive.keyAt(i);
//                int idx;
//                if ((idx = physical.weaponData.elements.table.getInt(e)) >= 0)
//                    physical.weaponData.elements.weights.incr(idx, modification.weaponElementsAdditive.getAt(i));
//                else
//                    physical.weaponData.elements.add(e, modification.weaponElementsAdditive.getAt(i));
//            }
        }

        if (modification.weaponCalcDelta != null) {
            for (int i = 0; i < 12; i++) {
                physical.weaponData.calcStats[i] = Math.max(0, physical.weaponData.calcStats[i] + modification.weaponCalcDelta[i]);
            }
        }

        return physical;
    }

    public void addProfession(Profession profession, Physical physical) {
        if (physical.creatureData == null) {
            System.err.println("Tried to add profession " + profession.name + " with no creature data to " + physical.name);
            return;
        }

        if (physical.creatureData.professions.keySet().contains(profession)) {
            System.err.println("Tried to duplicate add profession " + profession.name + " on " + physical.name);
            return;
        }

        if (profession.initialStatRequirements.entrySet().stream().anyMatch(e -> physical.stats.getOrDefault(e.getKey(), LiveValue.ZERO).base() < e.getValue())) {
            System.err.println("Physical " + physical.name + " does not have required stats for " + profession.name);
            return;
        }

        if (profession.initialSkillRequirements.entrySet().stream().anyMatch(e -> physical.creatureData.skills.getOrDefault(e.getKey(), Rating.NONE).lessThan(e.getValue()))) {
            System.err.println("Physical " + physical.name + " does not have required skills for " + profession.name);
            return;
        }

        //System.out.println("Adding profession " + profession.name + " to " + physical.name + " at rating " + Rating.SLIGHT.toString());
        physical.creatureData.professions.put(profession, Rating.SLIGHT);
        Modification mod = profession.improvements.get(Rating.SLIGHT);
        if (mod != null) {
            //System.out.println("Profession applying modification " + mod.name + " to " + physical.name);
            applyModification(physical, mod);
        }
    }
}

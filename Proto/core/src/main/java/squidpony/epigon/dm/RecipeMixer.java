package squidpony.epigon.dm;

import squidpony.epigon.data.blueprint.*;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Profession;
import squidpony.epigon.data.mixin.Terrain;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Recipe;
import squidpony.epigon.data.specific.Weapon;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.LiveValueModification;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;
import squidpony.squidmath.OrderedMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static squidpony.epigon.Epigon.rng;

/**
 * This class does all the recipe mixing. It has methods for creating objects based on recipes in
 * various categories.
 *
 * Results may be based on using a specific recipe with specific items, or by looking for a result
 * in a recipe and then building it with that recipe.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class RecipeMixer {

    public List<RecipeBlueprint> recipes;

    private Map<Material, Physical> materials = new HashMap<>();

    public Stream<RecipeBlueprint> blueprintsContainingIngredient(Physical ingredient) {
        return recipes.stream().filter(r -> r.uses(ingredient));
    }

    public Recipe createRecipe(RecipeBlueprint blueprint) {
        Recipe recipe = new Recipe();
        recipe.consumed = new OrderedMap<>(blueprint.requiredConsumed);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalConsumed != null && !blueprint.optionalConsumed.isEmpty()) {
            Entry<Physical, Integer> entry = blueprint.optionalConsumed.randomEntry(rng);
            recipe.consumed.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        recipe.catalyst = new OrderedMap<>(blueprint.requiredCatalyst);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalCatalyst != null && !blueprint.optionalCatalyst.isEmpty()) {
            Entry<Physical, Integer> entry = blueprint.optionalCatalyst.randomEntry(rng);
            recipe.catalyst.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        recipe.result = new OrderedMap<>();

        // TODO - modify results based on chosen optionals
        recipe.result.putAll(blueprint.result);

        return recipe;
    }

    public List<Physical> mix(Recipe recipe, List<Physical> consumed, List<Physical> catalyst) {
        List<Physical> result = new ArrayList<>();
        recipe.result.entrySet().stream()
            .forEach(e -> IntStream.range(0, e.getValue())
            .forEach(i -> {
                Physical physical = RecipeMixer.this.buildPhysical(e.getKey());
                Stream.of(consumed.stream(), catalyst.stream())
                    .flatMap(m -> m)
                    .map(m -> m.whenUsedAsMaterial)
                    .flatMap(Collection::stream)
                    .forEach(modification -> applyModification(physical, modification));
                result.add(physical);
            }));
        return result;
    }

    public Physical buildWeapon(Weapon weapon)
    {
        Material mat = Weapon.makes.get(weapon.materialTypes[0]).randomItem(rng);
        return mix(weapon.recipe, Collections.emptyList(), Collections.singletonList(buildMaterial(mat))).get(0);
    }

    public Physical buildPhysical(Stone stone) {
        Physical blueprint = materials.get(stone);
        if (blueprint != null) {
            return blueprint;
        }

        blueprint = new Physical();
        blueprint.color = stone.front.toFloatBits();//.toRandomizedFloat(rng, 0.05f, 0f, 0.15f);
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
        blueprint.color = material.getMaterialColor().toFloatBits();//toRandomizedFloat(rng, 0.05f, 0f, 0.15f);
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
        inclusionMod.statChanges.put(Stat.STRUCTURE, lvm);
        blueprint.whenUsedAsMaterial.add(inclusionMod);

        return blueprint;
    }


    public Physical buildPhysical(Physical blueprint) {
        return RecipeMixer.this.buildPhysical(blueprint, Rating.NONE);
    }

    public Physical buildPhysical(Physical blueprint, Rating rarity) {
        if (blueprint.generic) {
            // TODO - figure out how to allow sub instances of generics to be used without using generics
        }

        if (blueprint.unique) {
            // TODO - check for whether one has been created
        }

        Physical physical = new Physical();

        physical.description = blueprint.description;
        physical.notes = blueprint.notes; // TODO - probably don't need these transferred
        physical.parent = blueprint;

        physical.attached = blueprint.attached;

        List<String> possibleNames = new ArrayList<>();
        possibleNames.addAll(blueprint.possibleAliases);
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
        physical.large = blueprint.large;

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

        blueprint.inventory.stream().forEach(i -> {
            physical.inventory.add(RecipeMixer.this.buildPhysical(i));
        });

        physical.physicalDrops = blueprint.physicalDrops;
        physical.elementDrops = blueprint.elementDrops;

        physical.identification.putAll(blueprint.identification);

        physical.creatureData = createCreature(blueprint.creatureData);

        physical.terrainData = blueprint.terrainData;

        // TODO - add rest of mixins
        // finally work any modifications
        for (Modification m : blueprint.requiredModifications) {
            applyModification(physical, m);
        }

        int count = rng.nextInt(blueprint.optionalModifications.size());
        Set<Integer> ints = new HashSet<>();
        for (int i = 0; i < count; i++) {
            int n;
            do {
                n = rng.nextInt(count);
            } while (ints.contains(n));
            ints.add(n);
            applyModification(physical, blueprint.optionalModifications.get(n));
        }

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
        creature.equippedData = other.equippedData; // TODO - copy into new equippedData

        return creature;
    }

    /**
     * Applies the provided modification to the provided physical in place.
     */
    public void applyModification(Physical physical, Modification modification) {
        SquidColorCenter colorCenter = new SquidColorCenter();
        physical.modifications.add(modification);

        if (modification.possibleAliases != null) {
            physical.possibleAliases = new ArrayList<>(modification.possibleAliases);
        }

        physical.possibleAliases.addAll(modification.possibleAliasesAdd);

        int count = modification.possiblePrefix.size() + modification.possiblePostfix.size();
        if (count > 0) {
            int i = rng.nextInt(count);
            if (i < modification.possiblePrefix.size()) {
                physical.name = modification.possiblePrefix.get(i) + " " + physical.name;
            } else {
                i -= modification.possiblePrefix.size();
                physical.name += " " + modification.possiblePostfix.get(i);
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

        if (modification.contentsOverwrite != null) {
            physical.countsAs = new HashSet<>(modification.countsAs);
        } else {
            if (modification.countsAsGained != null) {
                physical.countsAs.addAll(modification.countsAsGained);
            }
            if (modification.countsAsLost != null) {
                physical.countsAs.removeAll(modification.countsAsLost);
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
            physical.large = modification.large;
        }

        if (modification.lightEmitted != 0f) {
            physical.lightEmitted = modification.lightEmitted;
        }

        if (modification.lightEmittedStrengthChange != null) {
            physical.lightEmittedStrength.modify(modification.lightEmittedStrengthChange);
        }

        physical.elementalDamageMultiplier.putAll(modification.elementalDamageMultiplier);

        physical.stats.putAll(modification.stats);

        modification.statChanges.entrySet()
            .stream()
            .forEach(e -> {
                LiveValue lv = physical.stats.getOrDefault(e.getKey(), new LiveValue(1));
                lv.modify(e.getValue());
                physical.stats.put(e.getKey(), lv);
            });

        if (modification.whenUsedAsMaterial != null) {
            physical.whenUsedAsMaterial = new ArrayList<>(modification.whenUsedAsMaterial);
        }

        if (modification.creatureOverwrite != null) {
            physical.creatureData = createCreature(modification.creatureOverwrite);
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
        }
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

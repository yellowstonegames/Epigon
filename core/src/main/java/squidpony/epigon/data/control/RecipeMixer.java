package squidpony.epigon.data.control;

import com.badlogic.gdx.graphics.Colors;
import squidpony.Maker;
import squidpony.Messaging;
import squidpony.epigon.data.*;
import squidpony.epigon.data.quality.*;
import squidpony.epigon.data.raw.RawCreature;
import squidpony.epigon.data.trait.*;
import squidpony.squidgrid.gui.gdx.GDXMarkup;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class does all the recipe mixing. It has methods for creating objects based on recipes in
 * various categories.
 *
 * There is some danger in using the construction bits as having an object A that creates B that create a new A will cause an infinite loop.
 * NOTE - preventing such a loop was the intention behind blueprints that could be instantiated only when needed, but that structure no longer exists that way
 *
 * Results may be based on using a specific recipe with specific items, or by looking for a result
 * in a recipe and then building it with that recipe.
 */
public class RecipeMixer {

    public List<RecipeBlueprint> recipes;
    
    public RecipeMixer()
    {
    }
    public Stream<RecipeBlueprint> blueprintsContainingIngredient(Physical ingredient) {
        return recipes.stream().filter(r -> r.uses(ingredient));
    }

    public static Recipe createRecipe(RecipeBlueprint blueprint) {
        Recipe recipe = new Recipe();
        recipe.consumed = new OrderedMap<>(blueprint.requiredConsumed);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalConsumed != null && !blueprint.optionalConsumed.isEmpty()) {
            int idx = recipe.nextInt(blueprint.optionalConsumed.size());
            recipe.consumed.merge(blueprint.optionalConsumed.keyAt(idx), blueprint.optionalConsumed.getAt(idx), Integer::sum);
        }

        recipe.catalyst = new OrderedMap<>(blueprint.requiredCatalyst);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalCatalyst != null && !blueprint.optionalCatalyst.isEmpty()) {
            int idx = recipe.nextInt(blueprint.optionalCatalyst.size());
            recipe.catalyst.merge(blueprint.optionalCatalyst.keyAt(idx), blueprint.optionalCatalyst.getAt(idx), Integer::sum);
        }

        // TODO - modify results based on chosen optionals
        recipe.result = new OrderedMap<>(blueprint.result);

        return recipe;
    }

    public static ArrayList<Physical> mix(Recipe recipe, List<Physical> consumed, List<Physical> catalyst) {
        return mix(recipe, consumed, catalyst, recipe);
    }

    public static ArrayList<Physical> mix(Recipe recipe, List<Physical> consumed, List<Physical> catalyst, IRNG rng) {
        ArrayList<Physical> result = new ArrayList<>();
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
        return result;
    }

    public static ArrayList<Physical> mix(Recipe recipe, Material material) {
        return mix(recipe, material, recipe);
    }

    public static ArrayList<Physical> mix(Recipe recipe, Material material, IRNG rng) {
        ArrayList<Physical> result = new ArrayList<>();
        for (int i = 0; i < recipe.result.size(); i++) {
            Physical physical = buildPhysical(recipe.result.keyAt(i));

            Modification materialMod = new Modification();
            materialMod.baseValueMultiplier = material.getValue() * 0.01;
            materialMod.color = material.getColor();
            materialMod.possiblePrefix = Maker.makeList(material.toString());
            LiveValueModification lvm = new LiveValueModification();
            lvm.baseOverwrite = material.getHardness() * 0.01;
            lvm.actualOverwrite = material.getHardness() * 0.01;
            materialMod.statChanges.put(Stat.STRUCTURE, lvm);

            applyModification(physical, materialMod);
            physical.mainMaterial = material;

            physical.stats.values().forEach(lv -> lv.actual(lv.base()));// Make sure actual is set to base value on first creation
            physical.calculateStats();
            if (physical.groupingData != null) {
                for (int j = 0; j < physical.groupingData.quantity; j++) {
                    result.add(physical);
                }
            } else {
                result.add(physical);
            }
        }
        return result;
    }

    public static Physical buildWeapon(Weapon weapon, IRNG rng) {
        if (weapon.materialTypes != null && weapon.materialTypes.length > 0) {
            OrderedSet<Material> materials = Weapon.makes.get(weapon.materialTypes[0]);
            Material mat = materials.randomItem(rng);
            return mix(createRecipe(weapon.recipeBlueprint), mat, rng).get(0);
        } else {
            return createRecipe(weapon.recipeBlueprint).result.firstKey();
        }
    }

    public static Physical buildPhysical(Stone stone) {

        Physical blueprint = new Physical();
        blueprint.color = stone.front.toFloatBits();
        blueprint.name = stone.toString();
        blueprint.baseValue = stone.value;
        blueprint.symbol = '.';
        blueprint.stats.put(Stat.STRUCTURE, new LiveValue(stone.hardness * 0.01));

        Modification stoneMod = new Modification();
        stoneMod.baseValueMultiplier = stone.value * 0.01;
        stoneMod.color = stone.front;
        stoneMod.possiblePrefix = Maker.makeList(stone.toString());
        LiveValueModification lvm = new LiveValueModification();
        lvm.baseOverwrite = stone.hardness * 0.01;
        lvm.actualOverwrite = stone.getHardness() * 0.01;
        stoneMod.statChanges.put(Stat.STRUCTURE, lvm);
        blueprint.whenUsedAsMaterial.add(stoneMod);

        Terrain terrain = new Terrain();
        terrain.background = SColor.toEditedFloat(stone.back, 0f, -0.4f, -0.4f, 0f);
        terrain.stone = stone;
        terrain.extrusive = stone.extrusive;
        terrain.intrusive = stone.intrusive;
        terrain.metamorphic = stone.metamorphic;
        terrain.sedimentary = stone.sedimentary;
        blueprint.terrainData = terrain;

        return blueprint;
    }

    public static Physical buildPhysical(Inclusion inclusion) {
        Physical blueprint = new Physical();
        blueprint.color = inclusion.front.toFloatBits();
        blueprint.name = inclusion.toString();
        blueprint.baseValue = inclusion.value;
        blueprint.symbol = '.';
        blueprint.stats.put(Stat.STRUCTURE, new LiveValue(inclusion.hardness * 0.01));

        Modification inclusionMod = new Modification();
        inclusionMod.baseValueMultiplier = inclusion.value * 0.01;
        inclusionMod.color = inclusion.front;
        inclusionMod.possiblePrefix = Maker.makeList(inclusion.toString());
        LiveValueModification lvm = new LiveValueModification();
        lvm.baseOverwrite = inclusion.hardness * 0.01;
        lvm.actualOverwrite = inclusion.getHardness() * 0.01;
        inclusionMod.statChanges.put(Stat.STRUCTURE, lvm);
        blueprint.whenUsedAsMaterial.add(inclusionMod);

        Terrain terrain = new Terrain();
        terrain.background = SColor.toEditedFloat(inclusion.back, 0f, -0.4f, -0.4f, 0f);
        terrain.extrusive = inclusion.extrusive;
        terrain.intrusive = inclusion.intrusive;
        terrain.metamorphic = inclusion.metamorphic;
        terrain.sedimentary = inclusion.sedimentary;
        blueprint.terrainData = terrain;

        return blueprint;
    }

    public static Physical buildMaterial(Material material) {
        Physical blueprint = new Physical();
        blueprint.color = material.getColor().toFloatBits();
        blueprint.name = "Bit of " + material.toString();
        blueprint.baseValue = material.getValue();
        blueprint.symbol = material.getGlyph();
        blueprint.mainMaterial = material;
        blueprint.stats.put(Stat.STRUCTURE, new LiveValue(material.getHardness() * 0.01));

        Modification materialMod = new Modification();
        materialMod.baseValueMultiplier = material.getValue() * 0.01;
        materialMod.color = material.getColor();
        materialMod.baseValue = (double)material.getValue();
        materialMod.possiblePrefix = Maker.makeList(material.toString());
        LiveValueModification lvm = new LiveValueModification();
        lvm.baseOverwrite = material.getHardness() * 0.01;
        lvm.actualOverwrite = material.getHardness() * 0.01;
        materialMod.statChanges.put(Stat.STRUCTURE, lvm);

        if(material.getFlammability() >= 0)
        {
            blueprint.stats.put(Stat.HYDRATION, new LiveValue(material.getFlammability()));
            LiveValueModification fm = new LiveValueModification();
            fm.baseOverwrite = (double) material.getFlammability();
            fm.actualOverwrite = (double) material.getFlammability();
            materialMod.statChanges.put(Stat.HYDRATION, fm);
        }

        blueprint.whenUsedAsMaterial.add(materialMod);

        return blueprint;
    }

    public static Physical buildVegetable(Vegetable material) {
        Physical blueprint = new Physical();
        blueprint.color = material.getColor().toFloatBits();
        blueprint.name = material.prettyName();
        blueprint.rarity = Rating.SLIGHT;
        blueprint.symbol = material.getGlyph();
        blueprint.baseValue = material.getValue();
        blueprint.stats.put(Stat.STRUCTURE, new LiveValue(material.getHardness()));
        if(material.getFlammability() >= 0) {
            blueprint.stats.put(Stat.HYDRATION, new LiveValue(material.getFlammability()));
        }
        if(material.interactables().length > 0)
            blueprint.interactableData = Maker.makeList(material.interactables());
        return blueprint;
    }

    public static Physical buildTree(Tree material) {
        Physical blueprint = new Physical();
        blueprint.color = material.color().toFloatBits();
        blueprint.name = material.prettyName();
        blueprint.baseValue = material.lumber() == null ? 50 : material.lumber().value * 10;
        blueprint.rarity = Rating.SLIGHT;
        blueprint.symbol = material.symbol();
        blueprint.attached = true;
        blueprint.stats.put(Stat.STRUCTURE, new LiveValue(100));
        blueprint.stats.put(Stat.TOUGHNESS, new LiveValue(20 + (material.lumber() == null ? 100 : material.lumber().hardness)));
        if(material.lumber() != null && material.lumber().flammability >= 0)
            blueprint.stats.put(Stat.HYDRATION, new LiveValue(material.lumber().flammability * (1.5 + blueprint.nextDouble())));
        if(material.fruit() != null) {
            for (int i = blueprint.next(3); i < 4; i++) {
                blueprint.inventory.add(buildVegetable(material.fruit()));
            }
        }
        return blueprint;
    }

    /**
     * Builds a new Physical based on the passed in one as exactly as possible.
     *
     * @param blueprint
     * @return
     */
    public static Physical buildPhysical(Physical blueprint) {
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
    public static Physical buildPhysical(Physical blueprint, Rating rarity) {
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
    public static Physical buildPhysical(Physical blueprint, Rating rarity, boolean applyRatingModifications){
        if (blueprint.generic) {
            // TODO - figure out how to allow sub instances of generics to be used without using generics
            // Maybe we need an exorcist.
            // "May my steel steal Guy Steele's power and constrain your generics back to the Turing Tarpit of Hell!"
        }

        if (blueprint.unique) {
            // TODO - check for whether one has been created
            // also needs a definition of what makes two items different
        }

        Physical physical = new Physical();

        physical.description = blueprint.description;
        physical.parent = blueprint;

        physical.attached = blueprint.attached;

        ArrayList<String> possibleNames = new ArrayList<>(blueprint.possibleAliases);
        possibleNames.add(blueprint.name);
        physical.name = physical.getRandomElement(possibleNames);
        physical.possibleAliases.addAll(blueprint.possibleAliases); // TODO - lock it to the one made once it's made?

        if (!blueprint.countsAs.isEmpty()) {
            physical.countsAs.addAll(blueprint.countsAs);
        }
        physical.createdFrom.add(blueprint); // TODO - limit to "important" items
        physical.generic = blueprint.generic;
        physical.unique = blueprint.unique;
        physical.buildingBlock = blueprint.buildingBlock;

        physical.symbol = blueprint.symbol;
        physical.color = blueprint.color == 0f ? SColor.toRandomizedFloat(SColor.GRAY, physical, 1f, 0.1f, 0.15f) : blueprint.color;
        if(blueprint.radiance != null)
            physical.radiance = new Radiance(blueprint.radiance);
        physical.baseValue = blueprint.baseValue;
        physical.blocking = blueprint.blocking;
        
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

        if (blueprint.creatureData != null) {
            physical.creatureData = createCreature(blueprint.creatureData);
            for (Physical p : blueprint.creatureData.equippedDistinct) {
                physical.equipItem(p);
            }
        }

        if (blueprint.wearableData != null){
            physical.wearableData = new Wearable();
            physical.wearableData.slotsUsed = blueprint.wearableData.slotsUsed; // same backing list, if modification happens, that modification should split reference
            physical.wearableData.parent = blueprint.wearableData.parent;
        }

        physical.weaponData = blueprint.weaponData;

        physical.terrainData = blueprint.terrainData;
        physical.groupingData = blueprint.groupingData;
        if (blueprint.interactableData != null) {
            physical.interactableData = new ArrayList<>(blueprint.interactableData);
        }
        
        if(blueprint.mainMaterial != null)
            physical.mainMaterial = blueprint.mainMaterial;
        
        // TODO - add rest of mixins

        // finally work any modifications
        for (Modification m : blueprint.requiredModifications) {
            applyModification(physical, m);
        }

        int count = physical.nextInt(blueprint.optionalModifications.size());
        int[] ints = physical.randomOrdering(count);
        for (int i = 0; i < count; i++) {
            applyModification(physical, blueprint.optionalModifications.get(ints[i]));
        }

        physical.rarity = rarity;
        for (Rating rating : Rating.values()) {
            ArrayList<Modification> mods = blueprint.rarityModifications.get(rating);
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
    public static Condition createCondition(ConditionBlueprint blueprint) {
        return new Condition(blueprint);
    }

    public static Creature createCreature(Creature other) {
        if (other == null) {
            return null;
        }

        Creature creature = new Creature();
        creature.parent = other.parent;
        creature.skills.putAll(other.skills);
        creature.abilities.addAll(other.abilities); // TODO - copy into new abilities

        creature.weaponChoices = other.weaponChoices.copy(); // TODO - this needs to be here because the equipping doesn't include innate choices

        creature.lastUsedItem = other.lastUsedItem;
        creature.lastWieldedWeapon = other.lastWieldedWeapon;
        return creature;
    }
    public static Physical buildCreature(RawCreature raw) {
        Physical blueprint = new Physical();
        blueprint.color = Colors.get(raw.color).toFloatBits();
        blueprint.name = raw.name;
        blueprint.blocking = true;
        switch (raw.symbol.length())
        {
            case 2: blueprint.symbol = GDXMarkup.instance.styleChar(raw.symbol.charAt(0),
                    raw.symbol.charAt(1) == '*', raw.symbol.charAt(1) == '/');
            break;
            case 3: blueprint.symbol = GDXMarkup.instance.styleChar(raw.symbol.charAt(0), true, true);
            break;
            default: blueprint.symbol = raw.symbol.charAt(0);             
            break;
        }
//        blueprint.symbol = raw.symbol.charAt(0);
        blueprint.stats.put(Stat.STRUCTURE, new LiveValue(raw.vigor * 5.0));
        blueprint.stats.put(Stat.VIGOR, new LiveValue(raw.vigor));
        blueprint.stats.put(Stat.ENDURANCE, new LiveValue(raw.endurance));
        blueprint.stats.put(Stat.SPIRIT, new LiveValue(raw.spirit));
        blueprint.stats.put(Stat.SANITY, new LiveValue(raw.sanity));
        blueprint.stats.put(CalcStat.PRECISION, new LiveValue(raw.precision));
        blueprint.stats.put(CalcStat.DAMAGE, new LiveValue(raw.damage));
        blueprint.stats.put(CalcStat.CRIT, new LiveValue(raw.crit));
        blueprint.stats.put(CalcStat.INFLUENCE, new LiveValue(raw.influence));
        blueprint.stats.put(CalcStat.EVASION, new LiveValue(raw.evasion));
        blueprint.stats.put(CalcStat.DEFENSE, new LiveValue(raw.defense));
        blueprint.stats.put(CalcStat.LUCK, new LiveValue(raw.luck));
        blueprint.stats.put(CalcStat.STEALTH, new LiveValue(raw.stealth));
        blueprint.stats.put(CalcStat.RANGE, new LiveValue(raw.range));
        blueprint.stats.put(CalcStat.AREA, new LiveValue(raw.area));
        blueprint.stats.put(CalcStat.QUICKNESS, new LiveValue(raw.quickness));
        
        blueprint.stats.put(Stat.MOBILITY, new LiveValue(100));
        blueprint.stats.put(Stat.SIGHT, new LiveValue(9));
        blueprint.creatureData = new Creature();
        for (int i = 0; i < raw.training.size(); i++) {
            blueprint.creatureData.skills.put(Skill.skillsByName.getOrDefault(((OrderedMap<String, Integer>)raw.training).keyAt(i), Skill.BRAWLER),
                    Rating.allRatings[((OrderedMap<String, Integer>)raw.training).getAt(i)]);
        }
        blueprint.creatureData.weaponChoices = new ProbabilityTable<>(blueprint.nextLong());
        blueprint.weaponData = Weapon.getWeapons().get(raw.baseWeapon.name);
//        blueprint.weaponData = weapon.weaponData;
        for (int i = 0, idx = blueprint.nextInt(raw.weapons.length); i < raw.weapons.length && idx < raw.weapons.length; i++, idx++) {
//            blueprint.equipItem(buildWeapon(Weapon.getWeapons().get("holy symbol"), blueprint));
            Physical weapon = buildWeapon(Weapon.getWeapons().get(blueprint.getRandomElement(raw.weapons).name), blueprint);
            blueprint.inventory.add(weapon);
            blueprint.equipItem(weapon);
        }
        blueprint.creatureData.culture = Culture.cultures.getOrDefault(blueprint.getRandomElement(raw.culture), blueprint.creatureData.culture);
        if(raw.sayings == null || raw.sayings.length == 0)
            blueprint.creatureData.sayings = new String[]{"The @name stare$ blankly..."};
        else 
            blueprint.creatureData.sayings = raw.sayings;
        blueprint.creatureData.genderPronoun = blueprint.nextBoolean()
                ? Messaging.NounTrait.MALE_GENDER
                : Messaging.NounTrait.FEMALE_GENDER;
        return blueprint;
    }

    /**
     * Applies the provided modification to the provided physical in place.
     */
    public static Physical applyModification(Physical physical, Modification modification) {
        physical.modifications.add(modification);

        if (modification.possibleAliases != null) {
            physical.possibleAliases = new ArrayList<>(modification.possibleAliases);
        }

        physical.possibleAliases.addAll(modification.possibleAliasesAdd);

        int count = modification.possiblePrefix.size() + modification.possibleSuffix.size();
        if (count > 0) {
            int i = physical.nextInt(count);
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

        if (modification.symbol != '\uffff') {
            physical.symbol = modification.symbol;
        }

        if (modification.color != null) {
            physical.color = SColor.toRandomizedFloat(modification.color, physical, 0.05f, 0f, 0.15f);
        }
        if(modification.radiance != null)
        {
            physical.radiance = new Radiance(modification.radiance);
        }
        if((modification.symbol != '\uffff' || modification.color != null) && physical.appearance != null)
        {
            physical.appearance.setPackedColor(physical.color);
            physical.appearance.shown = physical.symbol;
        }

        if (modification.overlayColor != null) {
            physical.overlayColor = SColor.toRandomizedFloat(modification.overlayColor, physical, 0.05f, 0f, 0.15f);
        }
        if (modification.overlaySymbol != '\uffff') {
            physical.overlaySymbol = modification.overlaySymbol;
        }
        if(modification.overlaySymbol != '\uffff' || modification.overlayColor != null && physical.overlayAppearance != null)
        {
            physical.overlayAppearance.setPackedColor(physical.overlayColor);
            physical.overlayAppearance.shown = physical.overlaySymbol;
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
        physical.calculateStats();
        physical.statProgression.putAll(modification.statProgression);
        modification.statProgressionChanges.entrySet()
            .stream()
            .forEach(e -> {
                Rating r = physical.statProgression.getOrDefault(e.getKey(), Rating.NONE);
                r = r.applyRatingValueModification(e.getValue());
                physical.statProgression.put(e.getKey(), r);
            });

        physical.statEffects.addAll(modification.statEffectsAdditive);

        physical.statEffects.removeAll(modification.statEffectsSubtractive);

        if (modification.inventory != null) {
            physical.inventory = modification.inventory
                .stream()
                .map(RecipeMixer::buildPhysical)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        if (modification.inventoryAdditive != null) {
            physical.inventory.addAll(modification.inventoryAdditive
                .stream()
                .map(RecipeMixer::buildPhysical)
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
                .map(RecipeMixer::buildPhysical)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        if (modification.optionalInventoryAdditive != null) {
            physical.optionalInventory.addAll(modification.optionalInventoryAdditive
                .stream()
                .map(RecipeMixer::buildPhysical)
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

        if (modification.removeCreature != null && modification.removeCreature){
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
                physical.creatureData.abilities = new OrderedSet<>(modification.abilities);
            }
            if(physical.creatureData.abilities == null && (modification.abilitiesAdditive != null || modification.abilitiesSubtractive != null))
                physical.creatureData.abilities = new OrderedSet<>();

            if (modification.abilitiesAdditive != null) {
                physical.creatureData.abilities.addAll(modification.abilitiesAdditive);
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

        if (modification.interactable != null){
            physical.interactableData = new ArrayList<>(modification.interactable);
        }
        if (modification.interactableAdditive != null){
            if (physical.interactableData == null){
                physical.interactableData = new ArrayList<>();
            }
            physical.interactableData.addAll(modification.interactableAdditive);
        }
        if (modification.interactableSubtractive != null && physical.interactableData != null){
            // TODO - make Interactable have a countsAs property?
            physical.interactableData.removeAll(modification.interactableSubtractive);
        }

        // TODO - Wearable changes

        if (modification.weaponData != null) {
            physical.weaponData = modification.weaponData.copy();
        } else if (physical.weaponData == null && (
               modification.weaponStatusesAdditive != null
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

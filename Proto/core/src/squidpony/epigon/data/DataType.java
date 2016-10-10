package squidpony.epigon.data;

import squidpony.epigon.data.blueprints.BiomeBlueprint;
import squidpony.epigon.data.blueprints.BodyBlueprint;
import squidpony.epigon.data.blueprints.ConditionBlueprint;
import squidpony.epigon.data.blueprints.CreatureBlueprint;
import squidpony.epigon.data.blueprints.DungeonBlueprint;
import squidpony.epigon.data.blueprints.ItemBlueprint;
import squidpony.epigon.data.blueprints.ModificationBlueprint;
import squidpony.epigon.data.blueprints.RecipeBlueprint;
import squidpony.epigon.data.blueprints.RoomBlueprint;
import squidpony.epigon.data.generic.Ability;
import squidpony.epigon.data.generic.Element;
import squidpony.epigon.data.generic.Profession;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.generic.Strategy;
import squidpony.epigon.data.generic.TerrainBlueprint;
import squidpony.epigon.data.specific.Biome;
import squidpony.epigon.data.specific.Body;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Creature;
import squidpony.epigon.data.specific.Dungeon;
import squidpony.epigon.data.specific.Item;
import squidpony.epigon.data.specific.Recipe;
import squidpony.epigon.data.specific.Room;

/**
 * Lists the user editable data types.
 */
public enum DataType {

    AURA_BLUEPRINT(ConditionBlueprint.class),
    BIOME_BLUEPRINT(BiomeBlueprint.class),
    BODY_BLUEPRINT(BodyBlueprint.class),
    CREATURE_BLUEPRINT(CreatureBlueprint.class),
    DUNGEON_BLUEPRINT(DungeonBlueprint.class),
    MODIFICATION_BLUEPRINT(ModificationBlueprint.class),
    ITEM_BLUEPRINT(ItemBlueprint.class),
    RECIPE_BLUEPRINT(RecipeBlueprint.class),
    ROOM_BLUEPRINT(RoomBlueprint.class),
    ABILITY(Ability.class),
    ELEMENT(Element.class),
    PROFESSION(Profession.class),
    SKILL(Skill.class),
    STRATEGY(Strategy.class),
    TERRAIN(TerrainBlueprint.class),
    AURA(Condition.class),
    BIOME(Biome.class),
    BODY(Body.class),
    CREATURE(Creature.class),
    DUNGEON(Dungeon.class),
    ITEM(Item.class),
    RECIPE(Recipe.class),
    ROOM(Room.class);

    public Class<? extends EpiData> base;

    private DataType(Class<? extends EpiData> base) {
        this.base = base;
    }
}

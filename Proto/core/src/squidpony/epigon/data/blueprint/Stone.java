package squidpony.epigon.data.blueprint;

import squidpony.squidgrid.gui.gdx.SColor;

/**
 * All possible types of stone.
 *
 * @author Eben Howard - http(//squidpony.com
 */
public enum Stone {

    AMPHIBOLITE(SColor.BROWN, SColor.GORYEO_STOREROOM, true, false, false, false),
    ANDESITE(SColor.DARK_GRAY, SColor.GORYEO_STOREROOM, false, false, true, true),
    ANORTHOSITE(SColor.BUFF, SColor.GORYEO_STOREROOM, false, false, true, false),
    ANTHRACITE(SColor.GORYEO_STOREROOM, SColor.GRAY, true, false, false, false, 400, 1000),
    APLITE(SColor.LIGHT_GRAY, SColor.GORYEO_STOREROOM, false, false, true, false),
    ARGILLITE(SColor.ORANGE, SColor.GORYEO_STOREROOM, false, true, false, false, 200, 1000),
    ARKOSE(SColor.BUFF, SColor.GORYEO_STOREROOM, false, true, false, false),
    BANDED_IRON_FORMATIONS(SColor.PURPLE, SColor.GRAY, false, true, false, false),
    BASALT(SColor.DARK_GRAY, SColor.GORYEO_STOREROOM, false, false, false, true, 150, 1000),
    BLUESCHIST(SColor.SLATE_GRAY, SColor.GORYEO_STOREROOM, true, false, false, false),
    BONINITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, true),
    BRECCIA(SColor.GRAY, SColor.GORYEO_STOREROOM, false, true, false, false, 500, 1000),
    CARBONATITE(SColor.WHITE, SColor.GRAY, false, false, true, true),
    CATACLASITE(SColor.GRAY, SColor.GORYEO_STOREROOM, true, false, false, false, 150, 1000),
    CHALK(SColor.WHITE, SColor.LIGHT_GRAY, false, true, false, false, 250, 1000),
    CHARNOCKITE(SColor.LIGHT_GRAY, SColor.GRAY, true, false, true, true),
    CHERT(SColor.LIGHT_GRAY, SColor.DARK_GRAY, false, true, false, false, 1000, 1000),
    CLAYSTONE(SColor.BROWN, SColor.BROWNER, false, true, false, false),
    COAL(SColor.GORYEO_STOREROOM, SColor.DARK_GRAY, false, true, false, false, 300, 1000),
    CONGLOMERATE(SColor.BROWNER, SColor.GRAY, false, true, false, false, 100, 1000),
    COQUINA(SColor.TAN, SColor.KHAKI, false, true, false, false, 300, 1000),
    DACITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, true, true, 100, 1000),
    DIABASE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, true, false, 100, 1000),
    DIATOMACEOUS_EARTH(SColor.LIGHT_GRAY, SColor.GRAY, false, true, false, false, 250, 1000),
    DIAMICTITE(SColor.GRAY, SColor.DARK_KHAKI, false, true, false, false, 100, 1000),
    DIORITE(SColor.WHITE, SColor.GRAY, false, false, true, false, 350, 1000),
    DOLOMITE(SColor.LIGHT_PINK, SColor.WHITE, false, true, false, false, 200, 1000),
    DUNITE(SColor.GRAY_ASPARAGUS, SColor.DARK_GREEN, false, false, true, true, 100, 1000),
    ECLOGITE(SColor.RED_PIGMENT, SColor.GRAY, true, false, false, false, 100, 1000),
    ENDERBITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, true, true, 100, 1000),
    ESSEXITE(SColor.DARK_GRAY, SColor.GORYEO_STOREROOM, false, false, true, true, 100, 1000),
    EVAPORITE(SColor.KHAKI, SColor.DARK_TAN, false, true, false, false, 200, 1000),
    FELSITE(SColor.LIGHT_MAROON, SColor.LIGHT_GRAY, false, false, true, true, 250, 1000),
    FLINT(SColor.DARK_GREEN, SColor.GRAY, false, true, false, false, 800, 1000),
    FOIDOLITE(SColor.GRAY, SColor.DARK_GRAY, false, false, true, false, 100, 1000),
    GABBRO(SColor.DARK_GREEN, SColor.DARK_GRAY, false, false, true, true, 350, 1000),
    GANISTER(SColor.GRAY, SColor.DARK_GRAY, false, true, false, false, 200, 1000),
    GNEISS(SColor.GRAY, SColor.DARK_GRAY, true, false, false, false, 100, 1000),
    GOSSAN(SColor.RUST, SColor.DARK_RED, false, true, false, false, 100, 1000),
    GRANITE(SColor.DEEP_PINK, SColor.DARK_GRAY, false, false, true, false, 600, 1000),
    GRANODIORITE(SColor.GRAY_ASPARAGUS, SColor.DARK_GRAY, false, false, true, false, 150, 1000),
    GRANOPHYRE(SColor.GRAY, SColor.GRAY_ASPARAGUS, false, false, true, true, 100, 1000),
    GRANULITE(SColor.LIGHT_GRAY, SColor.BEIGE, true, false, false, false, 100, 1000),
    GREYWACKE(SColor.DEEP_CHESTNUT, SColor.DARK_BROWN, false, true, false, false, 100, 1000),
    GREENSCHIST(SColor.PINE_GREEN, SColor.DARK_SPRING_GREEN, true, false, false, false, 250, 1000),
    GRITSTONE(SColor.TAN, SColor.DARK_TAN, false, true, false, false, 300, 1000),
    GYPSUM(SColor.TAWNY, SColor.TAN, false, true, false, false, 400, 1000),
    HORNFELS(SColor.GRAY, SColor.DARK_GRAY, true, false, false, false, 100, 1000),
    HYALOCLASTITE(SColor.DARK_GRAY, SColor.GORYEO_STOREROOM, false, false, true, true, 1000, 1000),
    IGNIMBRITE(SColor.TAN, SColor.TAWNY, false, false, true, true, 150, 1000),
    IJOLITE(SColor.YELLOW_GREEN, SColor.LIGHT_GRAY, false, false, true, true, 100, 1000),
    JADEITITE(SColor.DARK_GREEN, SColor.FOREST_GREEN, true, false, false, false, 100, 1000),
    JASPEROID(SColor.DARK_GREEN, SColor.SLATE_GRAY, true, false, false, false, 180, 1000),
    JASPILLITE(SColor.SLATE_GRAY, SColor.DARK_RED, false, true, false, true, 100, 1000),
    KIMBERLITE(SColor.DARK_GREEN, SColor.DARK_RED, false, false, false, true, 100, 1000),
    KOMATIITE(SColor.GRAY, SColor.STEEL_BLUE, false, false, true, true, 100, 1000),
    LAMPROITE(SColor.GRAY, SColor.DARK_GRAY, false, false, true, true, 100, 1000),
    LAMPROPHYRE(SColor.DARK_GRAY, SColor.GRAY, false, false, true, true, 100, 1000),
    LARVIKITE(SColor.WHITE, SColor.SILVER, false, false, true, true, 800, 1000),
    LATITE(SColor.GRAY, SColor.LIGHT_GRAY, false, false, true, true, 100, 1000),
    LIGNITE(SColor.BROWNER, SColor.DARK_GRAY, false, true, false, false, 210, 1000),
    LITCHEFIELDITE(SColor.GORYEO_STOREROOM, SColor.TAN, true, false, false, false, 230, 1000),
    LUXULLIANITE(SColor.GRAY, SColor.DARK_GRAY, false, false, true, true, 760, 1000),
    MANGERITE(SColor.DARK_GRAY, SColor.GRAY, false, false, true, false, 100, 1000),
    MARBLE(SColor.LINEN, SColor.OLD_LACE, true, false, false, false, 1000, 1000),
    MARL(SColor.GORYEO_STOREROOM, SColor.DARK_GRAY, false, true, false, false, 180, 1000),
    MIGMATITE(SColor.GRAY, SColor.STEEL_BLUE, true, false, true, true, 100, 1000),
    MONZOGRANITE(SColor.DARK_CHESTNUT, SColor.GRAY, false, false, true, true, 100, 1000),
    MONZONITE(SColor.GRAY, SColor.DARK_GRAY, false, false, false, true, 100, 1000),
    MUDSTONE(SColor.RED_PIGMENT, SColor.BROWNER, false, true, false, false, 100, 1000),
    MYLONITE(SColor.GRAY, SColor.LIGHT_GRAY, true, false, false, false, 100, 1000),
    NEPHELINE_SYENITE(SColor.PALE_PINK, SColor.DARK_GREEN, false, false, true, false, 100, 1000),
    NEPHELINITE(SColor.DARK_GRAY, SColor.DARK_GREEN, false, false, true, true, 100, 1000),
    NORITE(SColor.DARK_GREEN, SColor.DARK_GRAY, false, false, true, false, 100, 1000),
    NOVACULITE(SColor.LIGHT_GRAY, SColor.DARK_GRAY, false, true, false, false, 460, 1000),
    OBSIDIAN(SColor.DARK_SLATE_GRAY, SColor.GORYEO_STOREROOM, false, false, false, true, 1340, 1000),
    OIL_SHALE(SColor.BROWNER, SColor.DARK_BROWN, false, true, false, false, 140, 1000),
    OOLITE(SColor.GOLDEN_YELLOW, SColor.GOLDEN_BROWN, false, true, false, false, 190, 1000),
    PEGMATITE(SColor.GRAY_ASPARAGUS, SColor.TAN, false, false, true, true, 100, 1000),
    PELITE(SColor.TAN, SColor.GRAY, true, false, false, false, 100, 1000),
    PERIDOTITE(SColor.GREEN_YELLOW, SColor.YELLOW_GREEN, false, false, true, true, 100, 1000),
    PHONOLITE(SColor.YELLOW_GREEN, SColor.GRAY_ASPARAGUS, false, false, false, true, 100, 1000),
    PHYLLITE(SColor.GRAY, SColor.RUSSET, true, false, false, false, 100, 1000),
    PICRITE_BASALT(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    PORPHYRY(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    PSAMMITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    PSEUDOTACHYLITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 100),
    PUMICE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    PYROXENITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    QUARTZITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    QUARTZ_DIORITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    QUARTZ_MONZONITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    RAPAKIVI(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    RHYODACITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    RHYOLITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    RHOMB_PORPHYRY(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SANDSTONE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SCHIST(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SCORIA(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SERPENTINITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SHALE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SILTSTONE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SKARN(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SLATE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SOAPSTONE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    SYENITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    TACHYLITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    TACONITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    TEPHRITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    TEZONTLE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    THERALITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    TONALITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    TRACHYTE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    TROCTOLITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    TRONDHJEMITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    TUFF(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    UNAKITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    VARIOLITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000),
    WACKESTONE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100, 1000);

    public SColor front, back;
    public int value;//base material is 100
    public int hardness;//average hardness
    public boolean metamorphic, sedimentary, intrusive, extrusive;

    private Stone(SColor front, SColor back, boolean metamorphic, boolean sedimentary, boolean intrusive, boolean extrusive) {
        this(front, back, metamorphic, sedimentary, intrusive, extrusive, 100, 1000);
    }

    private Stone(SColor front, SColor back, boolean metamorphic, boolean sedimentary, boolean intrusive, boolean extrusive, int value, int hardness) {
        this.front = front;
        this.back = back;
        this.metamorphic = metamorphic;
        this.sedimentary = sedimentary;
        this.intrusive = intrusive;
        this.extrusive = extrusive;
        this.value = value;
        this.hardness = hardness;
    }

    @Override
    public String toString() {
        return name().toLowerCase().replace('_', ' ');
    }
}

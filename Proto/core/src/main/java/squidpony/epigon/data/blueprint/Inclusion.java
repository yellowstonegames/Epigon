package squidpony.epigon.data.blueprint;

import squidpony.squidgrid.gui.gdx.SColor;

/**
 * All possible types of mineral inclusions.
 *
 * @author Eben Howard - http(//squidpony.com
 */
public enum Inclusion {

    ABELSONITE(SColor.PURPLE_TAUPE, SColor.RUSSET, false, false, false, false),
    ABERNATHYITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false),
    ACANTHITE(SColor.DARK_SLATE_GRAY, SColor.SILVER, true, false, false, false),
    ACETAMIDE(SColor.ALICE_BLUE, SColor.WHITE, false, false, false, false),
    ACTINOLITE(SColor.GREEN_YELLOW, SColor.DARK_GREEN, true, false, false, false),
    ADAMITE(SColor.YELLOW_GREEN, SColor.YELLOW, false, false, false, false, 300),
    AEGIRINE(SColor.DARK_GREEN, SColor.RUST, false, false, true, true, 50),
    AENIGMATITE(SColor.BROWNER, SColor.GORYEO_STOREROOM, false, false, true, true, 50),
    AERINITE(SColor.GRAY, SColor.GORYEO_STOREROOM, true, false, false, false, 50),
    AFGHANITE(SColor.BLUE, SColor.BLUE_VIOLET, true, false, false, false, 60),
    AFWILLITE(SColor.LIGHT_KHAKI, SColor.WHITE, true, false, false, false, 20),
    AGATE(SColor.WHEAT, SColor.FLAX, true, true, false, true, 250),
    AGRELLITE(SColor.OLD_LACE, SColor.WHEAT, false, false, true, true, 40),
    AGRINIERITE(SColor.WHEAT, SColor.GOLD, false, false, false, false, 100),
    AGUILARITE(SColor.GRAY, SColor.DARK_GRAY, false, false, false, false, 100),
    AHEYLITE(SColor.BLUE_GREEN, SColor.ASPARAGUS, false, false, false, false, 100),
    AIKINITE(SColor.DARK_GRAY, SColor.RUSSET, false, false, false, false, 30),
    AJOITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    AKAGANEITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    AKATOREITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    ALEXANDRITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    ALMANDINE(SColor.DARK_PINK, SColor.DARK_RED, true, false, false, false, 430),
    AMAZONITE(SColor.BRIGHT_GREEN, SColor.PASTEL_GREEN, true, false, false, true, 380),
    AMBER(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 180),
    AMETHYST(SColor.PURPLE_TAUPE, SColor.PURPLE, true, true, true, true, 835),
    AMETRINE(SColor.ORANGE_PEEL, SColor.PURPLE, false, false, false, false, 1000),
    ANDALUSITE(SColor.GRAY, SColor.GORYEO_STOREROOM, true, false, false, false, 28),
    ANTIMONY(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    AQUAMARINE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    AZURITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    BENITOITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    BIXBITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    BLOODSTONE(SColor.SCARLET, SColor.DARK_GREEN, true, true, false, true, 350),
    BLUE_GARNET(SColor.SAPPHIRE, SColor.PIGMENT_BLUE, true, false, false, false, 1230),
    CARNELIAN(SColor.RUSSET, SColor.ORANGE_RED, true, true, false, true, 320),
    CATS_EYE(SColor.YELLOW, SColor.BROWNER, false, false, true, false, 260), //myName = "cat's eye",
    CHALCEDONY(SColor.SLATE_GRAY, SColor.GRAY, true, true, false, true, 140),
    CHRYSOBERYL(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    CHRYSOCOLLA(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    CHRYSOPRASE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    CITRINE(SColor.SCHOOL_BUS_YELLOW, SColor.PUMPKIN, true, true, false, true, 1100),
    COBALT_SPINEL(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    DEMANTOID(SColor.YELLOW_GREEN, SColor.DARK_GREEN, true, false, true, true, 9000),
    DIAMOND(SColor.ALICE_BLUE, SColor.WHITE, true, false, false, false, 7000),
    DIOPSIDE(SColor.DARK_GREEN, SColor.FOREST_GREEN, false, false, false, true, 650),
    EMERALD(SColor.EMERALD, SColor.DARK_GREEN, false, true, false, false, 4500),
    EUDIALYTE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    GOSHENITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    HACKMANITE(SColor.LIGHT_PINK, SColor.DARK_PINK, false, false, false, true, 600),
    HELIODOR(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    IOLITE(SColor.SLATE_GRAY, SColor.PIGMENT_BLUE, true, false, true, false, 180),
    JADEITE(SColor.KELLY_GREEN, SColor.PEACH_ORANGE, true, false, false, false, 180),
    JASPER(SColor.FIREBRICK, SColor.ORANGE_RED, true, true, true, true, 75),
    JET(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    KUNZITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    KYANITE(SColor.MIDNIGHT_BLUE, SColor.SLATE_GRAY, true, false, false, false, 60),
    LABRADORITE(SColor.LIGHT_BLUE, SColor.MIDNIGHT_BLUE, true, false, false, true, 360),
    LAPIS_LAZULI(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    MALACHITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    MALAYA(SColor.ORANGE_RED, SColor.SANGRIA, true, false, false, false, 1650),
    MAW_SIT_SIT(SColor.FERN_GREEN, SColor.DARK_GREEN, true, false, false, false, 730),
    MELANITE(SColor.GORYEO_STOREROOM, SColor.MIDNIGHT_BLUE, true, false, false, false, 910),
    MOONSTONE(SColor.PALE_CORNFLOWER_BLUE, SColor.CAROLINA_BLUE, true, false, false, true, 850),
    MORGANITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    MUSCOVITE(SColor.BURNT_SIENNA, SColor.SILVER, false, false, true, false, 70),
    NEPHRITE_JADE(SColor.JADE, SColor.TEA_GREEN, true, false, false, false, 2300),
    ONYX(SColor.GORYEO_STOREROOM, SColor.WHITE, true, true, true, true, 3820),
    OPAL(SColor.OLD_LACE, SColor.WHITE, false, true, false, true, 1530),
    PERIDOT(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    PRUDENT_MAN_AGATE(SColor.CARDINAL, SColor.SEA_GREEN, true, true, false, true, 250),
    PYROPE(SColor.DARK_RED, SColor.RED_PIGMENT, true, false, false, false, 4820),
    QUARTZ(SColor.SILVER, SColor.WHITE, true, true, true, true, 960),
    RHODOLITE(SColor.VIOLET, SColor.VERMILION, true, false, false, false, 4820),
    RIESLING_BERYL(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    ROSOLITE(SColor.LIGHT_PINK, SColor.PALE_PINK, true, false, false, false, 750),
    RUBY(SColor.RUBY, SColor.RED_PIGMENT, false, false, true, true, 7840),
    SAPPHIRE(SColor.INDIGO_DYE, SColor.SAPPHIRE, false, false, true, true, 6870),
    SAUROLITE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    SPECTROLITE(SColor.GOLDEN, SColor.GRAY, true, false, false, true, 680),
    SPESSARTITE(SColor.ORANGE_RED, SColor.BROWNER, true, false, false, false, 100),
    SPINEL(SColor.MAUVE_TAUPE, SColor.STEEL_BLUE, false, false, false, true, 1970),
    SUNSTONE(SColor.GOLDEN_YELLOW, SColor.ORANGE, true, false, false, true, 1180),
    TANZANITE(SColor.BURNT_ORANGE, SColor.BROWNER, true, false, false, false, 3270),
    TIGER_EYE(SColor.BROWNER, SColor.BROWN, true, true, false, false, 830),
    TOPAZ(SColor.CHERRY_BLOSSOM, SColor.CARNATION_PINK, false, false, true, false, 6290),
    TOURMALINE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    TRANSVAAL_JADE(SColor.KELLY_GREEN, SColor.DARK_GREEN, true, false, false, false, 1180),
    TSAVORITE(SColor.ELECTRIC_GREEN, SColor.EMERALD, true, false, false, false, 4290),
    TURQUOISE(SColor.GRAY, SColor.GORYEO_STOREROOM, false, false, false, false, 100),
    ULEXITE(SColor.WHITE, SColor.IVORY, true, false, false, false, 180),
    UVAROVITE(SColor.GREEN, SColor.DARK_GREEN, true, false, false, false, 820),
    ZIRCON(SColor.LIGHT_BLUE, SColor.BURNT_SIENNA, true, false, true, false, 910);

    public SColor front, back;
    public int value;//base material is 100
    public int hardness;//average hardness
    public boolean metamorphic, sedimentary, intrusive, extrusive;

    private Inclusion(SColor front, SColor back, boolean metamorphic, boolean sedimentary, boolean intrusive, boolean extrusive) {
        this(front, back, metamorphic, sedimentary, intrusive, extrusive, 100, 1000);
    }

    private Inclusion(SColor front, SColor back, boolean metamorphic, boolean sedimentary, boolean intrusive, boolean extrusive, int value) {
        this(front, back, metamorphic, sedimentary, intrusive, extrusive, value, 1000);
    }

    private Inclusion(SColor front, SColor back, boolean metamorphic, boolean sedimentary, boolean intrusive, boolean extrusive, int value, int hardness) {
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

package squidpony.epigon.data.blueprint;

import squidpony.squidgrid.gui.gdx.SColor;


/**
 * All possible types of mineral inclusions.
 *
 * @author Eben Howard - http://squidpony.com
 */
public enum Inclusion {

    ABELSONITE(SColor.PURPLE_TAUPE, SColor.RUSSET, false, false, false, false),
    ABERNATHYITE(SColor.GRAY, SColor.BLACK, false, false, false, false),
    ACANTHITE(SColor.DARK_SLATE_GRAY, SColor.SILVER, true, false, false, false),
    ACETAMIDE(SColor.ALICE_BLUE, SColor.WHITE, false, false, false, false),
    ACTINOLITE(SColor.GREEN_YELLOW, SColor.DARK_GREEN, true, false, false, false),
    ADAMITE(SColor.YELLOW_GREEN, SColor.YELLOW, false, false, false, false, 300),
    AEGIRINE(SColor.DARK_GREEN, SColor.RUST, false, false, true, true, 50),
    AENIGMATITE(SColor.BROWNER, SColor.BLACK, false, false, true, true, 50),
    AERINITE(SColor.GRAY, SColor.BLACK, true, false, false, false, 50),
    AFGHANITE(SColor.BLUE, SColor.BLUE_VIOLET, true, false, false, false, 60),
    AFWILLITE(SColor.LIGHT_KHAKI, SColor.WHITE, true, false, false, false, 20),
    AGATE(SColor.WHEAT, SColor.FLAX, true, true, false, true, 250);

    /*      
     case AGRELLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.OLD_LACE;
     backColor = SColor.WHEAT;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 40;
     break;
     case AGRINIERITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.WHEAT;
     backColor = SColor.GOLD;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case AGUILARITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case AHEYLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BLUE_GREEN;
     backColor = SColor.ASPARAGUS;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case AIKINITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GRAY;
     ;
     backColor = SColor.RUSSET;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 30;
     break;
     case AJOITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case AKAGANEITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case AKATOREITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case ALEXANDRITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case ALMANDINE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_SCARLET;
     backColor = SColor.DARK_RED;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 430;
     break;
     case AMAZONITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BRIGHT_GREEN;
     backColor = SColor.PASTEL_GREEN;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 380;
     break;
     case AMBER:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 180;
     break;
     case AMETHYST:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.PURPLE_TAUPE;
     backColor = SColor.PURPLE;
     metamorphic = true;//uncommon
     sedimentary = true;//very rarely
     intrusive = true;
     extrusive = true;
     value = 835;
     break;
     case AMETRINE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.ORANGE_PEEL;
     backColor = SColor.PURPLE;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 1000;
     break;
     case ANDALUSITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 28;
     break;
     case ANTIMONY:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case AQUAMARINE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case AZURITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case BENITOITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case BIXBITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case BLOODSTONE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.SCARLET;
     backColor = SColor.DARK_GREEN;
     metamorphic = true;
     sedimentary = true;
     intrusive = false;
     extrusive = true;
     value = 350;
     break;
     case BLUE_GARNET:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.SAPPHIRE;
     backColor = SColor.PIGMENT_BLUE;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 1230;
     break;
     case CARNELIAN:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.RUSSET;
     backColor = SColor.ORANGE_RED;
     metamorphic = true;
     sedimentary = true;
     intrusive = false;
     extrusive = true;
     value = 320;
     break;
     case CATS_EYE:
     myName = "cat's eye";
     frontColor = SColor.YELLOW;
     backColor = SColor.BROWNER;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 260;
     break;
     case CHALCEDONY:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.SLATE_GRAY;
     backColor = SColor.GRAY;
     metamorphic = true;
     sedimentary = true;
     intrusive = false;
     extrusive = true;
     value = 140;
     break;
     case CHRYSOBERYL:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case CHRYSOCOLLA:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case CHRYSOPRASE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case CITRINE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.SCHOOL_BUS_YELLOW;
     backColor = SColor.PUMPKIN;
     metamorphic = true;
     sedimentary = true;
     intrusive = false;
     extrusive = true;
     value = 1100;
     break;
     case COBALT_SPINEL:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case DEMANTOID:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.YELLOW_GREEN;
     backColor = SColor.DARK_GREEN;
     metamorphic = true;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 9000;
     break;
     case DIAMOND:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.ALICE_BLUE;
     backColor = SColor.WHITE;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 7000;
     break;
     case DIOPSIDE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GREEN;
     backColor = SColor.FOREST_GREEN;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 650;
     break;
     case EMERALD:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.EMERALD;
     backColor = SColor.DARK_GREEN;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 4500;
     break;
     case EUDIALYTE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case GOSHENITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case HACKMANITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LIGHT_PINK;
     backColor = SColor.DARK_PINK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 600;
     break;
     case HELIODOR:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case IOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.SLATE_GRAY;
     backColor = SColor.PIGMENT_BLUE;
     metamorphic = true;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 180;
     break;
     case JADEITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.KELLY_GREEN;
     backColor = SColor.PEACH_ORANGE;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 180;
     break;
     case JASPER:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.FIREBRICK;
     backColor = SColor.ORANGE_RED;
     metamorphic = true;
     sedimentary = true;
     intrusive = true;
     extrusive = true;
     value = 75;
     break;
     case JET:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case KUNZITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case KYANITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.MIDNIGHT_BLUE;
     backColor = SColor.SLATE_GRAY;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 60;
     break;
     case LABRADORITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LIGHT_BLUE;
     backColor = SColor.MIDNIGHT_BLUE;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 360;
     break;
     case LAPIS_LAZULI:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case MALACHITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case MALAYA:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.ORANGE_RED;
     backColor = SColor.SANGRIA;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 1650;
     break;
     case MAW_SIT_SIT:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.FERN_GREEN;
     backColor = SColor.DARK_GREEN;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 730;
     break;
     case MELANITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BLACK;
     backColor = SColor.MIDNIGHT_BLUE;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 910;
     break;
     case MOONSTONE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.PALE_CORNFLOWER_BLUE;
     backColor = SColor.CAROLINA_BLUE;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 850;
     break;
     case MORGANITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case MUSCOVITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BURNT_SIENNA;
     backColor = SColor.SILVER;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 70;
     break;
     case NEPHRITE_JADE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.JADE;
     backColor = SColor.TEA_GREEN;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 2300;
     break;
     case ONYX:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BLACK;
     backColor = SColor.WHITE;
     metamorphic = true;
     sedimentary = true;
     intrusive = true;
     extrusive = true;
     value = 3820;
     break;
     case OPAL:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.OLD_LACE;
     backColor = SColor.WHITE;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = true;
     value = 1530;
     break;
     case PERIDOT:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case PYROPE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_RED;
     backColor = SColor.RED_PIGMENT;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 4820;
     break;
     case QUARTZ:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.SILVER;
     backColor = SColor.WHITE;
     metamorphic = true;
     sedimentary = true;
     intrusive = true;
     extrusive = true;
     value = 960;
     break;
     case RHODOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.VIOLET;
     backColor = SColor.VERMILION;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 4820;
     break;
     case RIESLING_BERYL:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case ROSOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LIGHT_PINK;
     backColor = SColor.PALE_PINK;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 750;
     break;
     case RUBY:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.RUBY;
     backColor = SColor.RED_PIGMENT;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 7840;
     break;
     case SAPPHIRE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.INDIGO_DYE;
     backColor = SColor.SAPPHIRE;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 6870;
     break;
     case SAUROLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case SPECTROLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GOLDEN;
     backColor = SColor.GRAY;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 680;
     break;
     case SPESSARTITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.ORANGE_RED;
     backColor = SColor.BROWNER;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case SPINEL:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.MAUVE_TAUPE;
     backColor = SColor.STEEL_BLUE;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 1970;
     break;
     case SUNSTONE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GOLDEN_YELLOW;
     backColor = SColor.ORANGE;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 1180;
     break;
     case TANZANITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BURNT_ORANGE;
     backColor = SColor.BROWNER;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 3270;
     break;
     case TIGER_EYE:
     myName = "tiger-eye";
     frontColor = SColor.BROWNER;
     backColor = SColor.BROWN;
     metamorphic = true;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 830;
     break;
     case TOPAZ:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.CHERRY_BLOSSOM;
     backColor = SColor.CARNATION_PINK;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 6290;
     break;
     case TOURMALINE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case TRANSVAAL_JADE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.KELLY_GREEN;
     backColor = SColor.DARK_GREEN;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 1180;
     break;
     case TSAVORITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.ELECTRIC_GREEN;
     backColor = SColor.EMERALD;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 4290;
     break;
     case TURQUOISE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     break;
     case ULEXITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.WHITE;
     backColor = SColor.IVORY;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 180;
     break;
     case UVAROVITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GREEN;
     backColor = SColor.DARK_GREEN;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 820;
     break;
     case ZIRCON:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LIGHT_BLUE;
     backColor = SColor.BURNT_SIENNA;
     metamorphic = true;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 910;
     break;
     }
     }
     * 
     * */
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

    private String getName() {
        return toString().toLowerCase().replace('_', ' ');
    }
}

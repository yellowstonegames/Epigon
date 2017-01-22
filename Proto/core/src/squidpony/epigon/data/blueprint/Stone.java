package squidpony.epigon.data.blueprint;

import squidpony.squidgrid.gui.gdx.SColor;


/**
 * All possible types of stone.
 *
 * @author Eben Howard - http://squidpony.com
 */
public enum Stone {

    AMPHIBOLITE(SColor.BROWN, SColor.BLACK, true, false, false, false),
    ANDESITE(SColor.DARK_GRAY, SColor.BLACK, false, false, true, true),
    ANORTHOSITE(SColor.BUFF, SColor.BLACK, false, false, true, false),
    ANTHRACITE(SColor.BLACK, SColor.GRAY, true, false, false, false, 400, 1000),
    APLITE(SColor.LIGHT_GRAY, SColor.BLACK, false, false, true, false),
    ARGILLITE(SColor.ORANGE, SColor.BLACK, false, true, false, false, 200, 1000),
    ARKOSE(SColor.BUFF, SColor.BLACK, false, true, false, false),
    BANDED_IRON_FORMATIONS(SColor.PURPLE, SColor.GRAY, false, true, false, false),
    BASALT(SColor.DARK_GRAY, SColor.BLACK, false, false, false, true, 150, 1000),
    BLUESCHIST(SColor.SLATE_GRAY, SColor.BLACK, true, false, false, false),
    BONINITE(SColor.GRAY, SColor.BLACK, false, false, false, true),
    BRECCIA(SColor.GRAY, SColor.BLACK, false, true, false, false, 500, 1000),
    CARBONATITE(SColor.WHITE, SColor.GRAY, false, false, true, true),
    CATACLASITE(SColor.GRAY, SColor.BLACK, true, false, false, false, 150, 1000),
    CHALK(SColor.WHITE, SColor.LIGHT_GRAY, false, true, false, false, 250, 1000),
    CHARNOCKITE(SColor.LIGHT_GRAY, SColor.GRAY, true, false, true, true),
    CHERT(SColor.LIGHT_GRAY, SColor.DARK_GRAY, false, true, false, false, 1000, 1000),
    CLAYSTONE(SColor.BROWN, SColor.BROWNER, false, true, false, false),
    COAL(SColor.BLACK, SColor.DARK_GRAY, false, true, false, false, 300, 1000);
    /* TODO -- finish adding these    
     * case CONGLOMERATE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BROWNER;
     backColor = SColor.GRAY;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case COQUINA:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.TAN;
     backColor = SColor.KHAKI;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 300;
     hardness = 1000;
     break;
     case DACITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case DIABASE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case DIATOMACEOUS_EARTH:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LIGHT_GRAY;
     backColor = SColor.GRAY;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 250;
     hardness = 1000;
     break;
     case DIAMICTITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.DARK_KHAKI;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case DIORITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.WHITE;
     backColor = SColor.GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 350;
     hardness = 1000;
     break;
     case DOLOMITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LIGHT_PINK;
     backColor = SColor.WHITE;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 200;
     hardness = 1000;
     break;
     case DUNITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY_ASPARAGUS;
     backColor = SColor.DARK_GREEN;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case ECLOGITE://may contain diamonds or garnets
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.RED_PIGMENT;
     backColor = SColor.GRAY;
     metamorphic = true;//basalt only
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case ENDERBITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case ESSEXITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case EVAPORITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.KHAKI;
     backColor = SColor.DARK_TAN;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 200;
     hardness = 1000;
     break;
     case FELSITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LIGHT_MAROON;
     backColor = SColor.LIGHT_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 250;
     hardness = 1000;
     break;
     case FLINT:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GREEN;
     backColor = SColor.GRAY;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 800;
     hardness = 1000;
     break;
     case FOIDOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case GABBRO:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GREEN;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 350;
     hardness = 1000;
     break;
     case GANISTER:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 200;
     hardness = 1000;
     break;
     case GNEISS:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.DARK_GRAY;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case GOSSAN:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.RUST;
     backColor = SColor.DARK_RED;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case GRANITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DEEP_PINK;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 600;
     hardness = 1000;
     break;
     case GRANODIORITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY_ASPARAGUS;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 150;
     hardness = 1000;
     break;
     case GRANOPHYRE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.GRAY_ASPARAGUS;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case GRANULITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LIGHT_GRAY;
     backColor = SColor.BEIGE;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case GREYWACKE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DEEP_CHESTNUT;
     backColor = SColor.DARK_BROWN;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case GREENSCHIST:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.PINE_GREEN;
     backColor = SColor.DARK_SPRING_GREEN;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 250;
     hardness = 1000;
     break;
     case GRITSTONE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.TAN;
     backColor = SColor.DARK_TAN;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 300;
     hardness = 1000;
     break;
     case GYPSUM:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.TAWNY;
     backColor = SColor.TAN;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 400;
     hardness = 1000;
     break;
     case HORNFELS:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.DARK_GRAY;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case HYALOCLASTITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 1000;
     hardness = 1000;
     break;
     case IGNIMBRITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.TAN;
     backColor = SColor.TAWNY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 150;
     hardness = 1000;
     break;
     case IJOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.YELLOW_GREEN;
     backColor = SColor.LIGHT_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case JADEITITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GREEN;
     backColor = SColor.FOREST_GREEN;
     metamorphic = true; //blueschist only
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case JASPEROID:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GREEN;
     backColor = SColor.SLATE_GRAY;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 180;
     hardness = 1000;
     break;
     case JASPILLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.SLATE_GRAY;
     backColor = SColor.DARK_RED;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case KIMBERLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GREEN;
     backColor = SColor.DARK_RED;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case KOMATIITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.STEEL_BLUE;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case LAMPROITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case LAMPROPHYRE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GRAY;
     backColor = SColor.GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case LARVIKITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.WHITE;
     backColor = SColor.SILVER;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 800;
     hardness = 1000;
     break;
     case LATITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.LIGHT_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case LIGNITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BROWNER;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 210;
     hardness = 1000;
     break;
     case LITCHEFIELDITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BLACK;
     backColor = SColor.TAN;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 230;
     hardness = 1000;
     break;
     case LUXULLIANITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 760;
     hardness = 1000;
     break;
     case MANGERITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GRAY;
     backColor = SColor.GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case MARBLE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LINEN;
     backColor = SColor.OLD_LACE;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 1000;
     hardness = 1000;
     break;
     case MARL:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BLACK;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 180;
     hardness = 1000;
     break;
     case MIGMATITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.STEEL_BLUE;
     metamorphic = true;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case MONZOGRANITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_CHESTNUT;
     backColor = SColor.GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case MONZONITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case MUDSTONE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.RED_PIGMENT;
     backColor = SColor.BROWNER;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case MYLONITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.LIGHT_GRAY;
     metamorphic = true;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case NEPHELINE_SYENITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.PALE_PINK;
     backColor = SColor.DARK_GREEN;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case NEPHELINITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GRAY;
     backColor = SColor.DARK_GREEN;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case NORITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_GREEN;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case NOVACULITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.LIGHT_GRAY;
     backColor = SColor.DARK_GRAY;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 460;
     hardness = 1000;
     break;
     case OBSIDIAN:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.DARK_SLATE_GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 1340;
     hardness = 1000;
     break;
     case OIL_SHALE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.BROWNER;
     backColor = SColor.DARK_BROWN;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 140;
     hardness = 1000;
     break;
     case OOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GOLDEN_YELLOW;
     backColor = SColor.GOLDEN_BROWN;
     metamorphic = false;
     sedimentary = true;
     intrusive = false;
     extrusive = false;
     value = 190;
     hardness = 1000;
     break;
     case PEGMATITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY_ASPARAGUS;
     backColor = SColor.TAN;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case PELITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.TAN;
     backColor = SColor.GRAY;
     metamorphic = true;//sedimentary only
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case PERIDOTITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GREEN_YELLOW;
     backColor = SColor.YELLOW_GREEN;
     metamorphic = false;
     sedimentary = false;
     intrusive = true;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case PHONOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.YELLOW_GREEN;
     backColor = SColor.GRAY_ASPARAGUS;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = true;
     value = 100;
     hardness = 1000;
     break;
     case PHYLLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.RUSSET;
     metamorphic = true; //slate only
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case PICRITE_BASALT:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case PORPHYRY:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case PSAMMITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case PSEUDOTACHYLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case PUMICE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case PYROXENITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case QUARTZITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case QUARTZ_DIORITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case QUARTZ_MONZONITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case RAPAKIVI:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case RHYODACITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case RHYOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case RHOMB_PORPHYRY:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SANDSTONE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SCHIST:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SCORIA:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SERPENTINITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SHALE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SILTSTONE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SKARN:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SLATE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SOAPSTONE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case SYENITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case TACHYLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case TACONITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case TEPHRITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case TEZONTLE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case THERALITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case TONALITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case TRACHYTE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case TROCTOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case TRONDHJEMITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case TUFF:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case UNAKITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case VARIOLITE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     break;
     case WACKESTONE:
     myName = type.toString().toLowerCase().replace('_', ' ');
     frontColor = SColor.GRAY;
     backColor = SColor.BLACK;
     metamorphic = false;
     sedimentary = false;
     intrusive = false;
     extrusive = false;
     value = 100;
     hardness = 1000;
     */
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

    private String getName() {
        return toString().toLowerCase().replace('_', ' ');
    }
}

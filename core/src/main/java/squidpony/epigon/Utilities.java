package squidpony.epigon;

import com.badlogic.gdx.graphics.Color;
import regexodus.Matcher;
import regexodus.Pattern;
import squidpony.StringKit;
import squidpony.epigon.data.Weapon;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.LightRNG;
import squidpony.squidmath.RNG;

import java.util.Collection;

/**
 * Created by Tommy Ettinger on 9/28/2017.
 */
public class Utilities {

    public static char randomBraille(long state) {
        return (char) GauntRNG.between(state, 0x2801, 0x2900);
    }

    public static final String sparkles = "#$%&";
    public static final String twinkles = "+※+¤";
    public static final char[] eighthBlocks = new char[]{' ', '▁', '▂', '▃', '▄', '▅', '▆', '▇', '█'};
    public static final String[] brailleByDots = {
        "⠀",
        "⠁⠂⠄⠈⠐⠠⡀⢀",
        "⠃⠅⠆⠉⠊⠌⠑⠒⠔⠘⠡⠢⠤⠨⠰⡁⡂⡄⡈⡐⡠⢁⢂⢄⢈⢐⢠⣀",
        "⠇⠋⠍⠎⠓⠕⠖⠙⠚⠜⠣⠥⠦⠩⠪⠬⠱⠲⠴⠸⡃⡅⡆⡉⡊⡌⡑⡒⡔⡘⡡⡢⡤⡨⡰⢃⢅⢆⢉⢊⢌⢑⢒⢔⢘⢡⢢⢤⢨⢰⣁⣂⣄⣈⣐⣠",
        "⠏⠗⠛⠝⠞⠧⠫⠭⠮⠳⠵⠶⠹⠺⠼⡇⡋⡍⡎⡓⡕⡖⡙⡚⡜⡣⡥⡦⡩⡪⡬⡱⡲⡴⡸⢇⢋⢍⢎⢓⢕⢖⢙⢚⢜⢣⢥⢦⢩⢪⢬⢱⢲⢴⢸⣃⣅⣆⣉⣊⣌⣑⣒⣔⣘⣡⣢⣤⣨⣰",
        "⠟⠯⠷⠻⠽⠾⡏⡗⡛⡝⡞⡧⡫⡭⡮⡳⡵⡶⡹⡺⡼⢏⢗⢛⢝⢞⢧⢫⢭⢮⢳⢵⢶⢹⢺⢼⣇⣋⣍⣎⣓⣕⣖⣙⣚⣜⣣⣥⣦⣩⣪⣬⣱⣲⣴⣸",
        "⠿⡟⡯⡷⡻⡽⡾⢟⢯⢷⢻⢽⢾⣏⣗⣛⣝⣞⣧⣫⣭⣮⣳⣵⣶⣹⣺⣼",
        "⡿⢿⣟⣯⣷⣻⣽⣾", "⣿"
    };

    public static char randomBraille(long seed, int dots) {
        String s = brailleByDots[dots % 9];
        return s.charAt(LightRNG.determineBounded(seed, s.length()));
    }

    public static char brailleFor(Collection<Coord> coords) {
        char b = 0x2800;
        for (Coord c : coords) {
            if (c.x == 0) {
                switch (c.y) {
                    case 0:
                        b += 0x1;
                        break;
                    case 1:
                        b += 0x2;
                        break;
                    case 2:
                        b += 0x4;
                        break;
                    case 3:
                        b += 0x40;
                        break;
                }
            } else if (c.x == 1) {
                switch (c.y) {
                    case 0:
                        b += 0x8;
                        break;
                    case 1:
                        b += 0x10;
                        break;
                    case 2:
                        b += 0x20;
                        break;
                    case 3:
                        b += 0x80;
                        break;
                }
            }
        }
        return b;
    }

    public static String colorize(String text, Color color) {
        return colorize(text, color, null);
    }

    public static String colorize(String text, Color color, Color defaultColor) {
        if (text == null || text.isEmpty() || color == null) {
            return "";
        }

        String ret = "[#" + StringKit.hex(Color.rgba8888(color)) + "]" + text;
        if (defaultColor != null) {
            ret += "[#" + StringKit.hex(Color.rgba8888(defaultColor)) + "]";
        } else {
            ret += "[]";
        }

        return ret;
    }

    public static String capitalizeFirst(final CharSequence original)
    {
        if (original == null || original.length() <= 0) {
            return "";
        }
        sb.setLength(0);
        sb.append(original);
        sb.setCharAt(0, Character.toUpperCase(original.charAt(0)));
        return sb.toString();
    }

    private static final Matcher capitalizeMatcher = Pattern.compile("(?<!\\pL)(\\pL)(\\pL*)(\\PL*)").matcher();
    private static final StringBuilder sb = new StringBuilder(64);
    public static String caps(final CharSequence original)
    {
        if (original == null || original.length() <= 0) {
            return "";
        }
        sb.setLength(0);
        capitalizeMatcher.setTarget(original);
        while (capitalizeMatcher.find()) {
            sb.append(capitalizeMatcher.group(1).toUpperCase());
            capitalizeMatcher.getGroup(2, sb, 1); // mode 1 is case-insensitive, which lower-cases result
            sb.append(capitalizeMatcher.group(3));
        }
        return sb.toString();
    }
    public static String caps(final CharSequence original,
                              final CharSequence oldDelimiter) {
        if (original == null || original.length() <= 0) {
            return "";
        }
        sb.setLength(0);
        capitalizeMatcher.setTarget(original);
        while (capitalizeMatcher.find()) {
            sb.append(capitalizeMatcher.group(1).toUpperCase());
            capitalizeMatcher.getGroup(2, sb, 1); // mode 1 is case-insensitive, which lower-cases result
            sb.append(capitalizeMatcher.group(3).replace(oldDelimiter, " "));
        }
        return sb.toString();
    }
    public static String caps(final CharSequence original,
                                    final CharSequence oldDelimiter, final CharSequence newDelimiter) {
        if (original == null || original.length() <= 0) {
            return "";
        }
        sb.setLength(0);
        capitalizeMatcher.setTarget(original);
        while (capitalizeMatcher.find()) {
            sb.append(capitalizeMatcher.group(1).toUpperCase());
            capitalizeMatcher.getGroup(2, sb, 1); // mode 1 is case-insensitive, which lower-cases result
            sb.append(capitalizeMatcher.group(3).replace(oldDelimiter, newDelimiter));
        }
        return sb.toString();
    }
    public static String lower(final CharSequence original,
                              final CharSequence oldDelimiter) {
        if (original == null || original.length() <= 0) {
            return "";
        }
        sb.setLength(0);
        capitalizeMatcher.setTarget(original);
        while (capitalizeMatcher.find()) {
            capitalizeMatcher.getGroup(1, sb, 1); // mode 1 is case-insensitive, which lower-cases result
            capitalizeMatcher.getGroup(2, sb, 1); // mode 1 is case-insensitive, which lower-cases result
            sb.append(capitalizeMatcher.group(3).replace(oldDelimiter, " "));
        }
        return sb.toString();
    }

    /**
     * Provides a String full of lines appropriate for the direction. If a stable set is desired, using the first
     * character from the set returned will work nicely.
     */
    public static String linesFor(Direction dir) {
        switch (dir) {
            case DOWN:
            case UP:
                return "|｜∣ǀ";
            case DOWN_LEFT:
            case UP_RIGHT:
                return "/／╱⁄";
            case DOWN_RIGHT:
            case UP_LEFT:
                return "\\＼╲";
            case LEFT:
            case RIGHT:
                return "-－−‐‑‒–—―";
            case NONE:
            default:
                return "+＋✚✕✖✗";
        }
    }

    /**
     * Provides a String full of arrows appropriate for the direction. If a stable set is desired, using the first
     * character from the set returned will work nicely.
     */
    public static String arrowsFor(Direction dir) {
        switch (dir) {
            case DOWN:
                return "↓↡";
            case DOWN_LEFT:
                return "↙";
            case DOWN_RIGHT:
                return "↘";
            case LEFT:
                return "←↞↢";
            case UP:
                return "↑↟";
            case UP_LEFT:
                return "↖";
            case UP_RIGHT:
                return "↗";
            case RIGHT:
                return "→↠↣";
            case NONE:
            default:
                return "⊙⊛";
        }
    }

    public static Color[][] randomColors(int innerSize, RNG rng) {
        Color[][] cs = new Color[8][innerSize];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < innerSize; j++) {
                cs[i][j] = SColor.randomColorWheel(rng);
            }
        }
        return cs;
    }
    
    public static float progressiveLighten(float color)
    {
        return SColor.toEditedFloat(color, 0f, 0f, (1.0f - SColor.lumaOfFloat(color)) * 0.4f, 1f);
    }
    
    // I don't think range is ever a non-integer currently...
    public static String getRangeText(Weapon weapon) {
        int intRange = (int) Math.round(weapon.rawWeapon.range) + 1;
        return  " " + Weapon.shapes.keyAt(weapon.shape) + " " + intRange;
    }

    /*
    all box drawing chars we know we can use:
    ┼├┤┴┬┌┐└┘│─
    ┌───┐
    │┌┐ │
    ├┴┼┬┤
    │ └┘│
    └───┘
     */

 /*
    Iosevka Slab contents
    ABCDEFGHIJKLMNOPQRSTUVWXYZ
    abcdefghijklmnopqrstuvwxyz
    //ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ // these no longer are valid; they aren't fixed-width anyway
    //ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ // these no longer are valid; they aren't fixed-width anyway
    ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ
    ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ
    ⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵
    0123456789
    ₀₁₂₃₄₅₆₇₈₉
    ⁰¹²³⁴⁵⁶⁷⁸⁹
    //０１２３４５６７８９ // these no longer are valid; they aren't fixed-width anyway
    ⓪①②③④⑤⑥⑦⑧⑨
    ⑴⑵⑶⑷⑸⑹⑺⑻⑼
    ¼½¾⅐⅑⅒⅓⅔⅕⅖⅗⅘⅙⅚⅛⅜⅝⅞↉‰‱℅℆
    ₊₋₌₍₎ₐₑₔₕᵢₖₗₘₙₒₚₛₜₓᵣᵤᵥᵦᵧᵨᵩᵪ
    ⁺⁻⁼⁽⁾ⁱⁿᴬᴭᴮᴯᴰᴱᴲᴳᴴᴵᴶᴷᴸᴹᴺᴻᴼᴽᴾᴿᵀᵁᵂᵃᵄᵅᵆᵇᵈᵉᵊᵋᵌᵍᵸᵎᵏᵐᵑᵒᵓᵔᵕᵖᵗᵘᵚᵛᵜᵝᵞᵟᵠᵡᶛᶜᶝᶞᶟᶠᶡᶢᶣᶤᶥᶦᶧᶨᶩᶫᶬᶭᶮᶯᶰᶱᶲᶳᶴᶵᶶᶷᶸᶹᶺᶻᶼᶽᶾʰʱʲʳʴʵʶʷʸ˟ˠˡˢˣˤ᾿῀῁

    ´῾‎‘’‚‛“”„‟•․‥…‧′″‴‵‶‷ʹʺʻʼʽˀˁˆˇˈˉ˭ˊˋ˘˙˚˜˝ˍˎˏ˒˓˔˕˖˗˛˳˷ͺˬ、。
    !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~¡¢£¤¥¦§¨©ª«¬®¯°±´µ¶·¸º»¿
    //！＂＃＄％＆＇（）＊＋，－．／：；＜＝＞？＠［＼］＾＿｀｛｜｝～｡｢｣､￠￡￥「」『』 // these no longer are valid; they aren't fixed-width anyway

    ─━╴╵╶╷╸╹╺╻╼╽╾╿│┃├┝┞┟┠┡┢┣┤┥┦┧┨┩┪┫┼┽┾┿╀╁╂╃╄╅╆╇╈╉╊╋
    ┌┍┎┏┐┑┒┓└┕┖┗┘┙┚┛┬┭┮┯┰┱┲┳┴┵┶┷┸┹┺┻
    ╌╍╎╏┄┅┆┇┈┉┊┋
    ═║╒╓╔╕╖╗╘╙╚╛╜╝╞╟╠╡╢╣╤╥╦╧╨╩╪╫╬
    ╭╮╯╰
    ▁▂▃▄▅▆▇█▉▊▋▌▍▎▏▐░▒▓
    ▀■□▬▭▮▯▲△▴▵▶▷▸▹▼▽▾▿◀◁◂◃◆◇○◌●◢◣◤◥◦★⋆∗∘∙⬟⬠⬡⬢⬣⭓⭔
    // ◯           // Not single width
    −‐‑‒–—―‾∼∽≁≈≉≠≡≢⌈⌉⌊⌋╳╱⁄⌿╲⍀∣ǀǁǂ†‡∤⍭⍧⍦∫∬∮˥˦˧˨˩
    ←↑→↓↔↕↖↗↘↙⤡⤢↞↟↠↡↢↣ːˑ
    ⊄⊂ bite
    Шщ claws
    Ѫѫ tentacles
    ŎƠ horns
    ӁӜ wings
    ζφ tail
    
    〈〉❬❭❮❯❰❱⟨⟩⟪⟫‹›≤≥⩽⩾≮≯≰≱⊂⊃⊄⊅∈∉∋∌⋀⋀∧⍲⋁⋁∨⍱⋂⋂∩⋃⋃∪∏∐⨿
    ♀♁♂♠♣♥♦♪⚐⚑⚡√✓✔✕✖✗✘✚∝∞⊕⊖⊙⊛
    ⌶⌷⌸⌹⌺⌻⌼⌽⌾⍁⍂⍃⍄⍅⍆⍇⍈⍉⍊⍋⍌⍍⍎⍏⍐⍑⍒⍓⍔⍕⍖⍗⍘⍙⍚⍛⍜⍝⍞⍟⍠⍡⍢⍣⍤⍥⍨⍩⍪⍫⍬⍮⍯⍰
    ∀∂∃∄∅∆∇∑∓⊢⊣⊤⊥⊦⊧⊨⊬⊭⋸※‼‽₣₤₧₨₩€₽℃℉ℓ№℗℠™Ω℧℩Å℮ﬁﬂ⍳⍴⍵⍶⍷⍸⍹⍺ⱫⱬⱭⱯⱰⱱⱲⱳⱷⱹⱻⱼⱽⱾⱿꜧꝚꝛꞀꞁꞎꞒꞓꞰꞱꬰꬵꭓꭤꭥ?ͻͼͽ
    ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΣΤΥΦΧΨΩ
    αβγδεζηθικλμνξοπρςστυφχψω
    АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ
    абвгдежзийклмнопрстуфхцчшщъыьэюя
    ᴀᴁᴂᴃᴄᴅᴆᴇᴈᴉᴊᴋᴌᴍᴎᴏᴐᴔᴕᴖᴗᴘᴙᴚᴛᴜᴠᴡᴢᴣᴤᴥᴦᴧᴨᴩᴪᴫᵷᵻᵼᵽᵿ


    ⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠐⠑⠒⠓⠔⠕⠖⠗⠘⠙⠚⠛⠜⠝⠞⠟⠠⠡⠢⠣⠤⠥⠦⠧⠨⠩⠪⠫⠬⠭⠮⠯⠰⠱⠲⠳⠴⠵⠶⠷⠸⠹⠺⠻⠼⠽⠾⠿⡀⡁⡂⡃⡄⡅⡆⡇⡈⡉⡊⡋⡌⡍⡎⡏⡐⡑⡒⡓⡔⡕⡖⡗⡘⡙⡚⡛
    ⡜⡝⡞⡟⡠⡡⡢⡣⡤⡥⡦⡧⡨⡩⡪⡫⡬⡭⡮⡯⡰⡱⡲⡳⡴⡵⡶⡷⡸⡹⡺⡻⡼⡽⡾⡿⢀⢁⢂⢃⢄⢅⢆⢇⢈⢉⢊⢋⢌⢍⢎⢏⢐⢑⢒⢓⢔⢕⢖⢗⢘⢙⢚⢛⢜⢝⢞⢟⢠⢡⢢⢣⢤⢥⢦⢧⢨⢩⢪⢫⢬⢭⢮⢯⢰⢱⢲⢳⢴⢵⢶⢷
    ⢸⢹⢺⢻⢼⢽⢾⢿⣀⣁⣂⣃⣄⣅⣆⣇⣈⣉⣊⣋⣌⣍⣎⣏⣐⣑⣒⣓⣔⣕⣖⣗⣘⣙⣚⣛⣜⣝⣞⣟⣠⣡⣢⣣⣤⣥⣦⣧⣨⣩⣪⣫⣬⣭⣮⣯⣰⣱⣲⣳⣴⣵⣶⣷⣸⣹⣺⣻⣼⣽⣾⣿
    
    
    ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿĀāĂăĄ
    ąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧ
    ŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿǃǄǅǆǇǈǉǊǋǌ
    ǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȠȡȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱ
    ȲȳȴȵȶȷȸȹȺȻȼȽȾȿɀɁɂɃɄɅɆɇɈɉɊɋɌɍɎɏɐɑɒɓɔɕɖɗɘəɚɛɜɝɞɟɠɡɢɣɤɥɦɧɨɩɪɫɬɭɮɯɰɱɲɳɴɵɶɷɸɹɺɻɼɽɾɿʀʁʂʃʄʅʆʇʈʉʊʋʌʍʎʏʐʑʒʓʔʕʖʗ
    ʘʙʚʛʜʝʞʟʠʡʢʣʤʥʦʧʨʩʪʫʬʭʮʯˌ˞;Ϳ΄΅Ά·ΈΉΊΌΎΏΐΪΫάέήίΰϊϋόύώϕϖϲϳϴϷϸϹϽϾϿЀЁЂЃЄЅІЇЈЉЊЋЌЍЎЏ
    ЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяѐёђѓєѕіїјљњћќѝўџѦѧѪѫѴѵѶѷѸѹҐґҒғҖҗҘҙҚқҢңҪҫҬҭҮүҰұҲҳҶҷҸҹҺһӀӁӂ
    ӏӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵӸӹԚԛԜԝ᪲ᶏᶐᶑᶙ
    ᶿᷧᷨᷩᷪᷫᷮᷯᷰᷱᷲᷳᷴḀḁḂḃḄḅḆḇḈḉḊḋḌḍḎḏḐḑḒḓḔḕḖḗḘḙḚḛḜḝḞḟḠḡḢḣḤḥḦḧḨḩḪḫḬḭḮḯḰḱḲḳḴḵḶḷḸḹḺḻḼḽḾḿṀṁṂṃṄṅṆṇṈṉṊṋṌṍṎṏṐṑṒṓ
    ṔṕṖṗṘṙṚṛṜṝṞṟṠṡṢṣṤṥṦṧṨṩṪṫṬṭṮṯṰṱṲṳṴṵṶṷṸṹṺṻṼṽṾṿẀẁẂẃẄẅẆẇẈẉẊẋẌẍẎẏẐẑẒẓẔẕẖẗẘẙẚẛẜẝẞẟẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặ
    ẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹἀἁἂἃἄἅἆἇἈἉἊἋἌἍἎἏἐἑἒἓἔἕἘἙἚἛἜἝ
    ἠἡἢἣἤἥἦἧἨἩἪἫἬἭἮἯἰἱἲἳἴἵἶἷἸἹἺἻἼἽἾἿὀὁὂὃὄὅὈὉὊὋὌὍὐὑὒὓὔὕὖὗὙὛὝὟὠὡὢὣὤὥὦὧὨὩὪὫὬὭὮὯὰάὲέὴήὶίὸόὺύὼώᾀᾁᾂᾃᾄᾅᾆᾇᾈᾉᾊᾋᾌᾍᾎᾏ
    ᾐᾑᾒᾓᾔᾕᾖᾗᾘᾙᾚᾛᾜᾝᾞᾟᾠᾡᾢᾣᾤᾥᾦᾧᾨᾩᾪᾫᾬᾭᾮᾯᾰᾱᾲᾳᾴᾶᾷᾸᾹᾺΆᾼ᾽ιῂῃῄῆῇῈΈῊΉῌ῍῎῏ῐῑῒΐῖῗῘῙῚΊ῝῞῟ῠῡῢΰῤῥῦῧῨῩῪΎῬ῭΅`ῲῳῴῶῷῸΌῺΏῼ
  
  */
}

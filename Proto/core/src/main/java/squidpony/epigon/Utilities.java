package squidpony.epigon;

import com.badlogic.gdx.graphics.Color;

import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;
import squidpony.squidmath.StatefulRNG;
import squidpony.squidmath.ThrustRNG;

/**
 * Created by Tommy Ettinger on 9/28/2017.
 */
public class Utilities {

    public static char randomBraille(StatefulRNG rng) {
        return (char) rng.between(0x2801, 0x2900);
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
        return s.charAt(ThrustRNG.determineBounded(seed, s.length()));
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

    public static String caps(String input, String delimiter) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return Arrays.stream(input.split(" "))
            .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
            .collect(Collectors.joining(" "));
    }

    public static String caps(String input) {
        return caps(input, " ");
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
    ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ
    ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ
    ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ
    ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ
    ⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵
    0123456789
    ₀₁₂₃₄₅₆₇₈₉
    ⁰¹²³⁴⁵⁶⁷⁸⁹
    ０１２３４５６７８９
    ⓪①②③④⑤⑥⑦⑧⑨
    ⑴⑵⑶⑷⑸⑹⑺⑻⑼
    ¼½¾⅐⅑⅒⅓⅔⅕⅖⅗⅘⅙⅚⅛⅜⅝⅞↉‰‱℅℆
    ₊₋₌₍₎ₐₑₔₕᵢₖₗₘₙₒₚₛₜₓᵣᵤᵥᵦᵧᵨᵩᵪ
    ⁺⁻⁼⁽⁾ⁱⁿᴬᴭᴮᴯᴰᴱᴲᴳᴴᴵᴶᴷᴸᴹᴺᴻᴼᴽᴾᴿᵀᵁᵂᵃᵄᵅᵆᵇᵈᵉᵊᵋᵌᵍᵸᵎᵏᵐᵑᵒᵓᵔᵕᵖᵗᵘᵚᵛᵜᵝᵞᵟᵠᵡᶛᶜᶝᶞᶟᶠᶡᶢᶣᶤᶥᶦᶧᶨᶩᶫᶬᶭᶮᶯᶰᶱᶲᶳᶴᶵᶶᶷᶸᶹᶺᶻᶼᶽᶾʰʱʲʳʴʵʶʷʸ˟ˠˡˢˣˤ᾿῀῁

    ´῾‎‘’‚‛“”„‟•․‥…‧′″‴‵‶‷ʹʺʻʼʽˀˁˆˇˈˉ˭ˊˋ˘˙˚˜˝ˍˎˏ˒˓˔˕˖˗˛˳˷ͺˬ
    !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~¡¢£¤¥¦§¨©ª«¬®¯°±´µ¶·¸º»¿
    ！＂＃＄％＆＇（）＊＋，－．／：；＜＝＞？＠［＼］＾＿｀｛｜｝～｡｢｣､￠￡￥、。「」『』

    ─━╴╵╶╷╸╹╺╻╼╽╾╿│┃├┝┞┟┠┡┢┣┤┥┦┧┨┩┪┫┼┽┾┿╀╁╂╃╄╅╆╇╈╉╊╋
    ┌┍┎┏┐┑┒┓└┕┖┗┘┙┚┛┬┭┮┯┰┱┲┳┴┵┶┷┸┹┺┻
    ╌╍╎╏┄┅┆┇┈┉┊┋
    ═║╒╓╔╕╖╗╘╙╚╛╜╝╞╟╠╡╢╣╤╥╦╧╨╩╪╫╬
    ╭╮╯╰
    ▁▂▃▄▅▆▇█▉▊▋▌▍▎▏▐░▒▓
    ▀■□▬▭▮▯▲△▴▵▶▷▸▹▼▽▾▿◀◁◂◃◆◇○◌●◢◣◤◥◦◯★⋆∗∘∙⬟⬠⬡⬢⬣⭓⭔
    −‐‑‒–—―‾∼∽≁≈≉≠≡≢⌈⌉⌊⌋╳╱⁄⌿╲⍀∣ǀǁǂ†‡∤⍭⍧⍦∫∬∮˥˦˧˨˩
    ←↑→↓↔↕↖↗↘↙⤡⤢↞↟↠↡↢↣ːˑ
    〈〉❬❭❮❯❰❱⟨⟩⟪⟫‹›≤≥⩽⩾≮≯≰≱⊂⊃⊄⊅∈∉∋∌⋀⋀∧⍲⋁⋁∨⍱⋂⋂∩⋃⋃∪∏∐⨿
    ♀♁♂♠♣♥♦♪⚐⚑⚡√✓✔✕✖✗✘✚∝∞⊕⊖⊙⊛
    ⌶⌷⌸⌹⌺⌻⌼⌽⌾⍁⍂⍃⍄⍅⍆⍇⍈⍉⍊⍋⍌⍍⍎⍏⍐⍑⍒⍓⍔⍕⍖⍗⍘⍙⍚⍛⍜⍝⍞⍟⍠⍡⍢⍣⍤⍥⍨⍩⍪⍫⍬⍮⍯⍰
    ∀∂∃∄∅∆∇∑∓⊢⊣⊤⊥⊦⊧⊨⊬⊭⋸※‼‽₣₤₧₨₩€₽℃℉ℓ№℗℠™Ω℧℩Å℮ﬁﬂ⍳⍴⍵⍶⍷⍸⍹⍺ⱫⱬⱭⱯⱰⱱⱲⱳⱷⱹⱻⱼⱽⱾⱿꜧꝚꝛꞀꞁꞎꞒꞓꞰꞱꬰꬵꭓꭤꭥ?ͻͼͽ
    ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩ
    АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩ
    αβγδεζηθικλμνξοπρςστυφχψω
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

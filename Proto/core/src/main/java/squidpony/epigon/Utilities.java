package squidpony.epigon;

import regexodus.Matcher;
import regexodus.Pattern;

/**
 * Created by Tommy Ettinger on 9/28/2017.
 */
public class Utilities {
    private static final Matcher capitalizeMatcher = Pattern.compile("(?<!\\pL)(\\pL)(\\pL*)(\\PL*)").matcher();
    private static final StringBuilder sb = new StringBuilder(64);

    public static String capitalize(final String original)
    {
        sb.setLength(0);
        capitalizeMatcher.setTarget(original);
        while (capitalizeMatcher.find())
        {
            sb.append(capitalizeMatcher.group(1).toUpperCase());
            capitalizeMatcher.getGroup(2, sb, 1);
            sb.append(capitalizeMatcher.group(3).replace('_', ' '));
        }
        return sb.toString();
    }
}

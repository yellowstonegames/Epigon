package squidpony.epigon;

import regexodus.Matcher;
import regexodus.Pattern;

/**
 * Created by Tommy Ettinger on 9/28/2017.
 */
public class Utilities {
    private static final Matcher capitalizeMatcher = Pattern.compile("(?<!\\pL)(\\pL)(\\pL*)(\\PL*)").matcher();
    public static String capitalize(final String original)
    {
        StringBuilder sb = new StringBuilder(original.length());
        capitalizeMatcher.setTarget(original);
        while (capitalizeMatcher.find())
        {
            sb.append(capitalizeMatcher.group(1).toUpperCase())
                    .append(capitalizeMatcher.group(2).toLowerCase())
                    .append(capitalizeMatcher.group(3).replace('_', ' '));
        }
        return sb.toString();
    }
}

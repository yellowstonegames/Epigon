package squidpony.epigon.data.specific;

/**
 * Helper class to contain the information needed to present a name of an object.
 */
public class Name {

    public String name;
    public String plural; // defaults to add 's'
    public String zero; // defaults to plural if not specified

    public Name() {
    }

    /**
     * Uses default pluralization of adding an 's' to the end of the word and uses plural for zero.
     * @param name
     */
    public Name(String name) {
        this.name = name;
        plural = name + 's';
        zero = plural;
    }
}

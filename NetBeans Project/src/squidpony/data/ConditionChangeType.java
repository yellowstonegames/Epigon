
package squidpony.data;

/**
 * Marks what kind of change has happened to a condition.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public enum ConditionChangeType {
    ADDED, REMOVED, OVERRIDDEN, OVERRIDING, SUPPRESED, SUPPRESING, CANCELLED;//TODO -- determine if Overriding and Suppressing are needed or if Added will suffice
}

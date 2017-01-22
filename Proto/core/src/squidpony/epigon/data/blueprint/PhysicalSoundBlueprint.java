package squidpony.epigon.data.blueprint;

/**
 * Contains the information needed to load sound effects for in-game objects.
 */
public class PhysicalSoundBlueprint {
    public String name; // Identifier to be used for things sharing this sound
    public String destructionSound;
    public String idleSound;
    public String movementSound;
    public int destructionVolume;
    public int idleVolume;
    public int movementVolume;
}

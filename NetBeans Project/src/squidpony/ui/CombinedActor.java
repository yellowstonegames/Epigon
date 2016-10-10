package squidpony.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import squidpony.data.specific.Item;

/**
 * Combines a libGDX Actor, a Box2d Body, and an Epigon Item to tie
 * together the three representations of each game piece
 *
 * @author Eben Howard - http://squidpony.com - eben@squidpony.com
 */
public class CombinedActor {

    private Actor actor;
    private Item object;
    private com.badlogic.gdx.physics.box2d.Body body;
    private boolean center;//marks if this has all origins at the center, should probably always be the case

    /**
     * Creates a new CombinedActor with the three elements combined. Defaults to
     * centering the origins. Does not add the Body to a box2d World.
     *
     * @param actor
     * @param object
     * @param body
     */
    public CombinedActor(Actor actor, Item object, Body body) {
        this.actor = actor;
        this.object = object;
        this.body = body;
            actor.setOrigin(actor.getWidth() / 2, actor.getHeight() / 2);
    }

    /**
     * Sets the graphic for the actor to match the Box2d location and
     * orientation
     */
    public void rectifyToBox2d() {
        Vector2 vector = body.getPosition();
        float angle = body.getAngle();
        
        actor.setPosition(vector.x * EnvironmentalVariables.getPixelsPerMeter(), vector.y * EnvironmentalVariables.getPixelsPerMeter());
        actor.setRotation((float) Math.toDegrees(angle));
    }

    /**
     * Releases the appropriate resources attached to this CombinedActor. If the
     * world parameter is null, then the actor is removed from it's parent but
     * the associated Box2d body is not removed from the world.
     */
    public void dispose(World world) {
        for (EventListener listener : actor.getListeners()) {
            actor.removeListener(listener);
        }
        actor.remove();

        if (world != null) {
            world.destroyBody(body);
        }
    }
}

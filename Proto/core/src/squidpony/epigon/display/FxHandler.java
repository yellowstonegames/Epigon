package squidpony.epigon.display;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import static squidpony.epigon.Epigon.rng;
import squidpony.epigon.universe.Element;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.ColorChangeImage;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;
import squidpony.squidgrid.gui.gdx.SquidLayers;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidmath.Coord;

/**
 * Controls what happens on the full map overlay panel.
 *
 * @author Eben Howard
 */
public class FxHandler {

    private static Character[] explosionChars = new Character[]{'%', '$', '&', '!'};// rng.getRandomElement can't work on primitive array of char

    private SquidLayers sLayers;
    private SquidPanel fx;
    private int width;
    private int height;
    private SquidColorCenter colorCenter;

    public FxHandler(SquidLayers sLayers, SquidPanel panel, SquidColorCenter colorCenter) {
        this.sLayers = sLayers;
        fx = panel;
        width = panel.gridWidth();
        height = panel.gridHeight();
        this.colorCenter = colorCenter;
    }

    public void elementBurst(Coord origin, Element element, int size, Radius radius) {
        for (int x = origin.x - size; x <= origin.x + size; x++) {
            for (int y = origin.y - size; y <= origin.y + size; y++) {
                if (radius.inRange(origin.x, origin.y, x, y, 0, size)) {
                    doBurst(x, y, element.color);
                }
            }
        }
    }

    private void doBurst(int x, int y, Color color) {
        double tint = 0.5 + rng.nextDouble() / 4;
        char c = rng.getRandomElement(explosionChars);
        float timing = rng.nextFloat() / 2;
        fx.summon(timing, x, y, x, y, c, SColor.TRANSPARENT, color, false, 0, 0, 0.5f); // this has a ghosting bounce-back in it :(
//        timing += 2f;
//        fx.summon(timing, x, y, x, y, c, color, colorCenter.desaturate(color, tint), false, 0, 0, 0.2f);
    }
}

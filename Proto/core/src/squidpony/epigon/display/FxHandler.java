package squidpony.epigon.display;

import com.badlogic.gdx.graphics.Color;
import squidpony.Maker;
import squidpony.epigon.universe.Element;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;

import static squidpony.epigon.Epigon.rng;

/**
 * Controls what happens on the full map overlay panel.
 *
 * @author Eben Howard
 */
public class FxHandler {

    private static char[] explosionChars = new char[]{'%', '$', '&', '!'};

    private SquidPanel fx;
    private int width;
    private int height;
    private SquidColorCenter colorCenter;
    private GreasedRegion viable;
    double[][] seen;

    public FxHandler(SquidPanel panel, SquidColorCenter colorCenter, double[][] visible) {
        fx = panel;
        width = panel.gridWidth();
        height = panel.gridHeight();
        this.colorCenter = colorCenter;
        viable = new GreasedRegion(visible.length, visible[0].length);
        seen = visible;
    }

    public void elementBurst(Coord origin, Element element, int size, Radius radius) {
//        for (int x = origin.x - size; x <= origin.x + size; x++) {
//            for (int y = origin.y - size; y <= origin.y + size; y++) {
//                if (radius.inRange(origin.x, origin.y, x, y, 0, size)) {
//                    doBurst(x, y, element.color);
//                }
//            }
//        }
        // uncomment above to see fix to summon(), comment out below if you do that.
        // this section uses the recent PanelEffect code to handle explosions in various colors.

        fx.addAction(new PanelEffect.ExplosionEffect(fx, 0.85f, viable.refill(seen, 0.001, 999.0),
                origin, size,
                Maker.makeList(colorCenter.saturate(element.color, 0.3),
                        colorCenter.light(colorCenter.saturate(element.color, 0.15)),
                        colorCenter.lightest(element.color),
                        colorCenter.lighter(colorCenter.desaturate(element.color, 0.15)),
                        colorCenter.desaturate(element.color, 0.3),
                        colorCenter.dim(colorCenter.desaturate(element.color, 0.45)).sub(0, 0, 0, 0.35f),
                        colorCenter.dimmer(colorCenter.desaturate(element.color, 0.6)).sub(0,0,0,0.85f))));
    }

    private void doBurst(int x, int y, Color color) {
        //double tint = 0.5 + rng.nextDouble() / 4;
        char c = explosionChars[rng.nextIntHasty(explosionChars.length)]; // this is pretty much all rng.getRandomElement does
        float timing = rng.nextFloat() * 0.5f;
        fx.summon(timing, x, y, x, y, c, SColor.TRANSPARENT.cpy().sub(0, 0, 0, 1f), color, false, 0, 0, 0.5f);
//        timing += 2f;
//        fx.summon(timing, x, y, x, y, c, color, colorCenter.desaturate(color, tint), false, 0, 0, 0.2f);
    }
}

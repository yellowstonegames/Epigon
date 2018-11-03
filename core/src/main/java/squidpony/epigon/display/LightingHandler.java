package squidpony.epigon.display;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidmath.Coord;
import squidpony.squidmath.NumberTools;
import squidpony.squidmath.OrderedMap;

import java.io.Serializable;

import static squidpony.squidgrid.gui.gdx.SColor.FLOAT_WHITE;
import static squidpony.squidgrid.gui.gdx.SColor.lerpFloatColorsBlended;

/**
 * Created by Tommy Ettinger on 11/2/2018.
 */
public class LightingHandler implements Serializable {
    private static final long serialVersionUID = 0L;
    
    public Radius radiusStrategy;
    
    public double[][] resistances;
    public double[][] fovResult;
    public double[][] losResult;
    protected transient double[][] tempFOV;
    public float[][][] colorLighting;
    protected transient float[][][] tempColorLighting;
    public int width;
    public int height;
    public float backgroundColor;
    public double playerVision;
    public OrderedMap<Coord, Radiance> lights;
    
    /**
     * Unlikely to be used except during serialization; makes a LightingHandler for a 20x20 fully visible level.
     */
    public LightingHandler()
    {
        this(new double[20][20], SColor.FLOAT_BLACK, Radius.CIRCLE, 4.0);
    }

    /**
     * Given a resistance array as produced by {@link squidpony.squidgrid.mapping.DungeonUtility#generateResistances(char[][])}
     * or {@link squidpony.squidgrid.mapping.DungeonUtility#generateSimpleResistances(char[][])}, makes a
     * LightingHandler that can have {@link Radiance} objects added to it in various locations. This will use a solid
     * black background when it casts light on cells without existing lighting.
     * @param resistance a resistance array as produced by DungeonUtility
     */
    public LightingHandler(double[][] resistance)
    {
        this(resistance, SColor.FLOAT_BLACK, Radius.CIRCLE, 4.0);
    }
    /**
     * Given a resistance array as produced by {@link squidpony.squidgrid.mapping.DungeonUtility#generateResistances(char[][])}
     * or {@link squidpony.squidgrid.mapping.DungeonUtility#generateSimpleResistances(char[][])}, makes a
     * LightingHandler that can have {@link Radiance} objects added to it in various locations.
     * @param resistance a resistance array as produced by DungeonUtility
     * @param backgroundColor the background color to use, as a libGDX color
     * @param radiusStrategy the shape lights should take, typically {@link Radius#CIRCLE} for "realistic" lights or one
     *                       of {@link Radius#DIAMOND} or {@link Radius#SQUARE} to match game rules for distance
     * @param playerVisionRange how far the player can see without light, in cells
     */
    public LightingHandler(double[][] resistance, Color backgroundColor, Radius radiusStrategy, double playerVisionRange)
    {
        this(resistance, backgroundColor.toFloatBits(), radiusStrategy, playerVisionRange);
    }
    /**
     * Given a resistance array as produced by {@link squidpony.squidgrid.mapping.DungeonUtility#generateResistances(char[][])}
     * or {@link squidpony.squidgrid.mapping.DungeonUtility#generateSimpleResistances(char[][])}, makes a
     * LightingHandler that can have {@link Radiance} objects added to it in various locations.
     * @param resistance a resistance array as produced by DungeonUtility
     * @param backgroundColor the background color to use, as a packed float (produced by {@link Color#toFloatBits()})
     * @param radiusStrategy the shape lights should take, typically {@link Radius#CIRCLE} for "realistic" lights or one
     *                       of {@link Radius#DIAMOND} or {@link Radius#SQUARE} to match game rules for distance
     * @param playerVisionRange how far the player can see without light, in cells
     */
    public LightingHandler(double[][] resistance, float backgroundColor, Radius radiusStrategy, double playerVisionRange)
    {
        this.radiusStrategy = radiusStrategy;
        playerVision = playerVisionRange;
        this.backgroundColor = backgroundColor;
        resistances = resistance;
        width = resistances.length;
        height = resistances[0].length;
        fovResult = new double[width][height];
        tempFOV = new double[width][height];
        losResult = new double[width][height];
        colorLighting = new float[2][width][height];
        tempColorLighting = new float[2][width][height];
        Coord.expandPoolTo(width, height);
        lights = new OrderedMap<>(32);
    }

    /**
     * Adds a Radiance as a light source at the given position. Overwrites any existing Radiance at the same position.
     * @param x the x-position to add the Radiance at
     * @param y the y-position to add the Radiance at
     * @param light a Radiance object that can have a changing radius, color, and various other effects on lighting
     * @return this for chaining
     */
    public LightingHandler addLight(int x, int y, Radiance light)
    {
        return addLight(Coord.get(x, y), light);
    }
    /**
     * Adds a Radiance as a light source at the given position. Overwrites any existing Radiance at the same position.
     * @param position the position to add the Radiance at
     * @param light a Radiance object that can have a changing radius, color, and various other effects on lighting
     * @return this for chaining
     */
    public LightingHandler addLight(Coord position, Radiance light)
    {
        lights.put(position, light);
        return this;
    }
    /**
     * If a Radiance is present at oldX,oldY, this will move it to newX,newY and overwrite any existing Radiance at
     * newX,newY. If no Radiance is present at oldX,oldY, this does nothing.
     * @param oldX the x-position to move a Radiance from
     * @param oldY the y-position to move a Radiance from
     * @param newX the x-position to move a Radiance to
     * @param newY the y-position to move a Radiance to
     * @return this for chaining
     */
    public LightingHandler moveLight(int oldX, int oldY, int newX, int newY)
    {
        return moveLight(Coord.get(oldX, oldY), Coord.get(newX, newY));
    }
    /**
     * If a Radiance is present at oldPosition, this will move it to newPosition and overwrite any existing Radiance at
     * newPosition. If no Radiance is present at oldPosition, this does nothing.
     * @param oldPosition the Coord to move a Radiance from
     * @param newPosition the Coord to move a Radiance to
     * @return this for chaining
     */
    public LightingHandler moveLight(Coord oldPosition, Coord newPosition)
    {
        Radiance old = lights.get(oldPosition);
        if(old == null) return this;
        lights.alter(oldPosition, newPosition);
        return this;
    }

    /**
     * Gets the Radiance at the given position, if present, or null if there is no light source there.
     * @param x the x-position to look up
     * @param y the y-position to look up
     * @return the Radiance at the given position, or null if none is present there
     */
    public Radiance get(int x, int y)
    {
        return lights.get(Coord.get(x, y));
    }
    /**
     * Gets the Radiance at the given position, if present, or null if there is no light source there.
     * @param position the position to look up
     * @return the Radiance at the given position, or null if none is present there
     */
    public Radiance get(Coord position)
    {
        return lights.get(position);
    }
    
    public void mixColoredLighting(float flare)
    {
        float[][][] basis = colorLighting, other = tempColorLighting;
        flare += 1f;
        float b0, b1, o0, o1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                b0 = basis[0][x][y];
                b1 = basis[1][x][y];
                o0 = other[0][x][y];
                o1 = other[1][x][y];
                if (b1 == FLOAT_WHITE) {
                    basis[1][x][y] = o1;
                    basis[0][x][y] = Math.min(1.0f, b0 + o0 * flare);
                } else {
                    if (o1 != FLOAT_WHITE) {
                        float change = (o0 - b0) * 0.5f + 0.5f;
                        final int s = NumberTools.floatToIntBits(b1), e = NumberTools.floatToIntBits(o1),
                                rs = (s & 0xFF), gs = (s >>> 8) & 0xFF, bs = (s >>> 16) & 0xFF, as = s & 0xFE000000,
                                re = (e & 0xFF), ge = (e >>> 8) & 0xFF, be = (e >>> 16) & 0xFF, ae = (e >>> 25);
                        change *= ae * 0.007874016f;
                        basis[1][x][y] = NumberTools.intBitsToFloat(((int) (rs + change * (re - rs)) & 0xFF)
                                | ((int) (gs + change * (ge - gs)) & 0xFF) << 8
                                | (((int) (bs + change * (be - bs)) & 0xFF) << 16)
                                | as);
                        basis[0][x][y] = Math.min(1.0f, b0 + o0 * change * flare);
                    } else {
                        basis[0][x][y] = Math.min(1.0f, b0 + o0 * flare);
                    }
                }
            }
        }
    }
    /**
     * Given a SparseLayers and a position for the viewer (typically the player), fills the SparseLayers with different
     * colors based on what lights are present in line of sight of the viewer and the various flickering or pulsing
     * effects that Radiance light sources can do. 
     * @param layers a SquidPanel used as a background, such as the back Panel of a SquidLayers
     * @param viewerPosition the position of the player or other viewer
     */
    public void draw(SparseLayers layers, Coord viewerPosition)
    {
        draw(layers.backgrounds, viewerPosition.x, viewerPosition.y);
    }

    /**
     * Given a SparseLayers and a position for the viewer (typically the player), fills the SparseLayers with different
     * colors based on what lights are present in line of sight of the viewer and the various flickering or pulsing
     * effects that Radiance light sources can do. 
     * @param layers a SquidPanel used as a background, such as the back Panel of a SquidLayers
     * @param viewerX the x-position of the player or other viewer
     * @param viewerY the y-position of the player or other viewer
     */
    public void draw(SparseLayers layers, int viewerX, int viewerY)
    {
        draw(layers.backgrounds, viewerX, viewerY);
    }
    /**
     * Given a SquidPanel that should be only solid blocks (such as the background of a SquidLayers) and a position for
     * the viewer (typically the player), fills the SquidPanel with different colors based on what lights are present in
     * line of sight of the viewer and the various flickering or pulsing effects that Radiance light sources can do. 
     * @param background a SquidPanel used as a background, such as the back Panel of a SquidLayers
     * @param viewerPosition the position of the player or other viewer
     */
    public void draw(SquidPanel background, Coord viewerPosition)
    {
        draw(background.colors, viewerPosition.x, viewerPosition.y);
    }
    /**
     * Given a SquidPanel that should be only solid blocks (such as the background of a SquidLayers) and a position for
     * the viewer (typically the player), fills the SquidPanel with different colors based on what lights are present in
     * line of sight of the viewer and the various flickering or pulsing effects that Radiance light sources can do. 
     * @param background a SquidPanel used as a background, such as the back Panel of a SquidLayers
     * @param viewerX the x-position of the player or other viewer
     * @param viewerY the y-position of the player or other viewer
     */
    public void draw(SquidPanel background, int viewerX, int viewerY)
    {
        draw(background.colors, viewerX, viewerY);
    }

    /**
     * Given a 2D array of packed float colors and a position for the viewer (typically the player), fills the 2D array
     * with different colors based on what lights are present in line of sight of the viewer and the various flickering
     * or pulsing effects that Radiance light sources can do. 
     * @param backgrounds a 2D float array, typically obtained from {@link squidpony.squidgrid.gui.gdx.SquidPanel#colors} or {@link squidpony.squidgrid.gui.gdx.SparseLayers#backgrounds}
     * @param viewerPosition the position of the player or other viewer
     */
    public void draw(float[][] backgrounds, Coord viewerPosition)
    {
        draw(backgrounds, viewerPosition.x, viewerPosition.y);
    }

    /**
     * Given a 2D array of packed float colors and a position for the viewer (typically the player), fills the 2D array
     * with different colors based on what lights are present in line of sight of the viewer and the various flickering
     * or pulsing effects that Radiance light sources can do. 
     * @param backgrounds a 2D float array, typically obtained from {@link squidpony.squidgrid.gui.gdx.SquidPanel#colors} or {@link squidpony.squidgrid.gui.gdx.SparseLayers#backgrounds}
     * @param viewerX the x-position of the player or other viewer
     * @param viewerY the y-position of the player or other viewer
     */
    public void draw(float[][] backgrounds, int viewerX, int viewerY)
    {
        Radiance radiance;
        FOV.reuseFOV(resistances, fovResult, viewerX, viewerY, playerVision, radiusStrategy);
        FOV.reuseLOS(resistances, losResult, viewerX, viewerY);
        SColor.eraseColoredLighting(colorLighting);
        final int sz = lights.size();
        Coord pos;
        for (int i = 0; i < sz; i++) {
            pos = lights.keyAt(i);
            radiance = lights.getAt(i);
            FOV.reuseFOV(resistances, tempFOV, pos.x, pos.y, radiance.currentRange());
            SColor.colorLightingInto(tempColorLighting, tempFOV, radiance.color);
            mixColoredLighting(radiance.flare);
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (losResult[x][y] > 0.0) {
                    fovResult[x][y] = MathUtils.clamp(fovResult[x][y] + colorLighting[0][x][y], 0, 1);
                }
            }
        }

        float current;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if(fovResult[x][y] > 0.0)
                {
                    current = backgrounds[x][y];
                    if(current == 0f)
                        current = backgroundColor;
                    backgrounds[x][y] = lerpFloatColorsBlended(current,
                            colorLighting[1][x][y], colorLighting[0][x][y] * 0.4f);
                }
            }
        }
    }
}

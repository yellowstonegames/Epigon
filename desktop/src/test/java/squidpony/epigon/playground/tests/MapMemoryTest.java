package squidpony.epigon.playground.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.ObjectMap;

public class MapMemoryTest extends ApplicationAdapter {
    private static final int width = 180, height = 180;

    private static final int cellWidth = 1, cellHeight = 1;
    // the initial bug was reported on ObjectMap, so that's what this uses (even though ObjectIntMap would be better)
    private ObjectMap<GridPoint2, Integer> theMap;

    @Override
    public void create() {
        theMap = new ObjectMap<>();
        generate();
    }

    public void generate()
    {
        System.out.println("Initial heap memory used: " + Gdx.app.getJavaHeap());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final GridPoint2 gp = new GridPoint2(x, y);
                final int gpHash = gp.hashCode(); // uses the updated GridPoint2 hashCode(), not the current GDX code
                theMap.put(gp, gpHash | 0xFF000000); //value doesn't matter; this was supposed to test ObjectMap
                //theMap.put(gp, (53 * 53 + x + 53 * y) | 0xFF000000); //this is what the hashCodes would look like for the current code
                
                //final int gpHash = x * 0xC13F + y * 0x91E1; // updated hashCode()
                //// In the updated hashCode(), numbers are based on the plastic constant, which is
                //// like the golden ratio but with better properties for 2D spaces. These don't need to be prime.
                
                //final int gpHash = 53 * 53 + x + 53 * y; // equivalent to current hashCode()
            }
        }
        System.out.println("Post-assign memory used: " + Gdx.app.getJavaHeap());
    }

    @Override
    public void render() {
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("LibGDX Test: ObjectMap<GridPoint2, Integer> memory usage");
        config.setWindowedMode(width * cellWidth, height * cellHeight);
        config.setIdleFPS(1);
        new Lwjgl3Application(new MapMemoryTest(), config);
    }
}
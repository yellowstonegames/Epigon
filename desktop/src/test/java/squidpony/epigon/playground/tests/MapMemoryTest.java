package squidpony.epigon.playground.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MapMemoryTest extends ApplicationAdapter {
    private static final int width = 500, height = 500;

    private SpriteBatch batch;
    private static final int cellWidth = 1, cellHeight = 1;
    private InputAdapter input;
    private Viewport view;

    private Pixmap pm;
    private Texture pt;
    // the initial bug was reported on ObjectMap, so that's what this uses (even though ObjectIntMap would be better)
    private ObjectMap<GridPoint2, Integer> theMap;

    @Override
    public void create() {
        theMap = new ObjectMap<>();
        batch = new SpriteBatch();
        view = new StretchViewport(width*cellWidth, height*cellHeight);
        pm = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pm.setBlending(Pixmap.Blending.None);
        pm.setColor(-1); // opaque white
        pm.fill();
        pt = new Texture(pm);
        pt.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pt.draw(pm, 0, 0);

        input = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.Q:
                    case Input.Keys.ESCAPE:
                        Gdx.app.exit();
                }
                return true;
            }
        };
        generate();
        Gdx.input.setInputProcessor(input);
    }

    public void generate()
    {
        IntSet[] hashes = new IntSet[26];
        for (int i = 0; i < hashes.length; i++) {
            hashes[i] = new IntSet(width * height);
        }
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

                for (int i = 0; i < hashes.length; i++) {
                    hashes[i].add(gpHash & ((1 << i) - 1)); // checks if bottom bits of gpHash are already used
                }
            }
        }
        System.out.println("Post-assign memory used: " + Gdx.app.getJavaHeap());
        for (int i = 0; i < hashes.length; i++) {
            System.out.println((width * height - hashes[i].size) + " collisions with mask " + ((1 << i) - 1));
        }
    }

    @Override
    public void render() {
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        for(ObjectMap.Entry<GridPoint2, Integer> ent : theMap)
        {
            // what we display here doesn't matter; it just verifies that each GridPoint2 is present.
            // the colors are really ugly, though, just by chance.
            batch.setColor(NumberUtils.intToFloatColor(ent.value));
            batch.draw(pt, ent.key.x, ent.key.y, cellWidth, cellHeight);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        view.update(width, height, true);
        view.apply(true);
    }


    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("LibGDX Test: ObjectMap<GridPoint2, Integer> memory usage");
        config.setWindowedMode(width * cellWidth, height * cellHeight);
        config.setIdleFPS(5);
        new Lwjgl3Application(new MapMemoryTest(), config);
    }
}
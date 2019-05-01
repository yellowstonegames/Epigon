package squidpony.epigon.playground.tests;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
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
    private RandomXS128 rng;
    private long seed;

    private Pixmap pm;
    private Texture pt;
    private ObjectMap<GridPoint2, Integer> theMap;

    @Override
    public void create() {
        theMap = new ObjectMap<>();
        batch = new SpriteBatch();
//        display = new SquidPanel(width, height, cellWidth, cellHeight);
        //display.getTextCellFactory().font().getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        view = new StretchViewport(width*cellWidth, height*cellHeight);
        pm = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pm.setBlending(Pixmap.Blending.None);
        pm.setColor(-1);
        pm.fill();
        pt = new Texture(pm);
        pt.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pt.draw(pm, 0, 0);

//        stage = new Stage(view, batch);
        seed = 0xca576f8f22345368L;//0x9987a26d1e4d187dL;//0xDEBACL;
        rng = new RandomXS128(seed);
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
        rng.setSeed(seed);
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
                theMap.put(gp, rng.nextInt() | 0xFF000000);
                final int gpHash = gp.hashCode();
//                final int gpHash = gp.x * 0xC13F + gp.y * 0x91E1;
                for (int i = 0; i < hashes.length; i++) {
                    hashes[i].add(gpHash & ~(-1 << i)); // checks if bottom bits of gpHash are already used
                }
            }
        }
        System.out.println("Post-assign memory used: " + Gdx.app.getJavaHeap());
        for (int i = 0; i < hashes.length; i++) {
            System.out.println((width * height - hashes[i].size) + " collisions with mask " + ((i << i) - 1));
        }
    }

    public void putMap() {
        batch.begin();
        for(ObjectMap.Entry<GridPoint2, Integer> ent : theMap)
        {
            batch.setColor(NumberUtils.intToFloatColor(ent.value));
            batch.draw(pt, ent.key.x, ent.key.y, cellWidth, cellHeight);
        }
        batch.end();
    }

    @Override
    public void render() {
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        putMap();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        view.update(width, height, true);
        view.apply(true);
    }


    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("SquidLib Demo: Detailed World Map");
        config.setWindowedMode(width * cellWidth, height * cellHeight);
        config.setIdleFPS(5);
        config.setWindowIcon(Files.FileType.Internal, "libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        new Lwjgl3Application(new MapMemoryTest(), config);
    }
}
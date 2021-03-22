package squidpony.epigon.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.epigon.Epigon;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.VisualCondition;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.TextCellFactory;

public class SquareSparseLayers extends SparseLayers {
    /**
     * The alpha and omega.
     * We really need to come to terms with this being a huge anti-pattern, but it works, Epigon.java dammit.
     */
    public final Epigon god;
    public SquareSparseLayers(int gridWidth, int gridHeight, float cellWidth, float cellHeight, TextCellFactory font, Epigon god) {
        super(gridWidth, gridHeight, cellWidth, cellHeight, font);
        this.god = god;

    }

    //we override draw() to center chars in their cells
    @Override
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);
        float xo = getX(), yo = getY(), yOff = yo + 1f + gridHeight * font.actualCellHeight, gxo, gyo,
                conditionY = 1f, conditionCw = 1f, conditionCm = 1f,
                conditionYAdd = 0f, conditionCwAdd = 0f, conditionCmAdd = 0f;
        Physical player = god.player;
        float[][] walls = god.walls;
        if(player.visualCondition == null) {
            final int clen = player.conditions.size();
            for (int i = clen - 1; i >= 0; i--) {
                VisualCondition vis = player.conditions.getAt(i).parent.visual;
                if (vis != null) {
                    vis.update();
                    conditionY *= vis.lumaMul;
                    conditionCw *= vis.warmMul;
                    conditionCm *= vis.mildMul;
                    conditionYAdd += vis.lumaAdd;
                    conditionCwAdd += vis.warmAdd;
                    conditionCmAdd += vis.mildAdd;
                    break;
                }
            }
        }
        else
        {
            VisualCondition vis = player.visualCondition;
            vis.update();
            conditionY *= vis.lumaMul;
            conditionCw *= vis.warmMul;
            conditionCm *= vis.mildMul;
            conditionYAdd += vis.lumaAdd;
            conditionCwAdd += vis.warmAdd;
            conditionCmAdd += vis.mildAdd;
        }
        Epigon.filter.yMul  = 0.7f  * conditionY;
        Epigon.filter.cwMul = 0.65f * conditionCw;
        Epigon.filter.cmMul = 0.65f * conditionCm;
        Epigon.filter.yAdd  = 0.7f  * conditionYAdd;
        Epigon.filter.cwAdd = 0.65f * conditionCwAdd;
        Epigon.filter.cmAdd = 0.65f * conditionCmAdd;
//                font.draw(batch, backgrounds, xo, yo);
        font.draw(batch, backgrounds, xo - font.actualCellWidth * 0.25f, yo);
        int len = layers.size();
        Frustum frustum = null;
        Stage stage = getStage();
        if(stage != null) {
            Viewport viewport = stage.getViewport();
            if(viewport != null)
            {
                Camera camera = viewport.getCamera();
                if(camera != null)
                {
                    if(
                            camera.frustum != null &&
                                    (!camera.frustum.boundsInFrustum(xo, yOff - font.actualCellHeight - 1f, 0f, font.actualCellWidth, font.actualCellHeight, 0f) ||
                                            !camera.frustum.boundsInFrustum(xo + font.actualCellWidth * (gridWidth-1), yo, 0f, font.actualCellWidth, font.actualCellHeight, 0f))
                    )
                        frustum = camera.frustum;
                }
            }
        }
        Epigon.filter.yMul  = 0.9f  * conditionY;
        Epigon.filter.cwMul = 0.95f * conditionCw;
        Epigon.filter.cmMul = 0.95f * conditionCm;
        Epigon.filter.yAdd  = 0.9f  * conditionYAdd;
        Epigon.filter.cwAdd = 0.95f * conditionCwAdd;
        Epigon.filter.cmAdd = 0.95f * conditionCmAdd;

        font.draw(batch, walls, xo - font.actualCellWidth * 0.25f, yo, 3, 3);
//                font.draw(batch, walls, xo - font.actualCellWidth * 0.25f, yo, 3, 3);

        font.configureShader(batch);
        if(frustum == null) {
            for (int i = 0; i < len; i++) {
                layers.get(i).draw(batch, font, xo, yOff);
            }

        }
        else
        {
            for (int i = 0; i < len; i++) {
                layers.get(i).draw(batch, font, frustum, xo, yOff);
            }
        }
        Epigon.filter.yMul  = 1.05f * conditionY;
        Epigon.filter.cwMul = 1.4f  * conditionCw;
        Epigon.filter.cmMul = 1.4f  * conditionCm;
        Epigon.filter.yAdd  = 1.05f * conditionYAdd;
        Epigon.filter.cwAdd = 1.4f  * conditionCwAdd;
        Epigon.filter.cmAdd = 1.4f  * conditionCmAdd;
        int x, y;
        for (int i = 0; i < glyphs.size(); i++) {
            TextCellFactory.Glyph glyph = glyphs.get(i);
            if(glyph == null)
                continue;
            glyph.act(Gdx.graphics.getDeltaTime());
            if(
                    !glyph.isVisible() ||
                            (x = Math.round((gxo = glyph.getX() - xo) / font.actualCellWidth)) < 0 || x >= gridWidth ||
                            (y = Math.round((gyo = glyph.getY() - yo)  / -font.actualCellHeight + gridHeight)) < 0 || y >= gridHeight ||
                            backgrounds[x][y] == 0f || (frustum != null && !frustum.boundsInFrustum(gxo, gyo, 0f, font.actualCellWidth, font.actualCellHeight, 0f)))
                continue;
            glyph.draw(batch, 1f);
        }
    }

}

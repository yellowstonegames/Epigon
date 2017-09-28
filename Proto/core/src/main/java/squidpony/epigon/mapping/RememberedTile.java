package squidpony.epigon.mapping;

import squidpony.epigon.data.specific.Physical;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Contains the player's memory of the tile. May eventually have more detail for "look" commands,
 * but right now is focused purely on drawing maps.
 *
 * @author Eben Howard
 */
public class RememberedTile {

    public char symbol = ' ';
    public float front = 0f;
    public float back = 0f;
    public float miniMapColor = 0f;

    public RememberedTile(EpiTile tile) {
        remake(tile);
    }

    public void remake(EpiTile tile) {
        symbol = tile.getSymbolUninhabited();
        front = tile.getForegroundColor();
        front = front == 0f ? 0f : SColor.lerpFloatColors(front, SColor.FLOAT_BLACK, 0.8f);
        back = tile.getBackgroundColor();
        back = back == 0f ? 0f : SColor.lerpFloatColors(back, SColor.FLOAT_BLACK, 0.8f); //0x1.fffffep-126f
        if (tile.getCreature() != null){
            miniMapColor = SColor.SCARLET.toFloatBits();
        } else {
            Physical p = tile.getLargeNonCreature();
            if (p == null){
                miniMapColor = SColor.DARK_INDIGO.toFloatBits();
            } else {
                switch (p.symbol){
                    case '#':
                        miniMapColor = SColor.SILVER_GREY.toFloatBits();
                        break;
                    case '+':
                        miniMapColor = SColor.TAWNY.toFloatBits();
                        break;
                    default:
                        miniMapColor = SColor.FLAX.toFloatBits();
                        break;
                }
            }
        }
    }
}

package squidpony.epigon.mapping;

import com.badlogic.gdx.graphics.Color;
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
    public Color front = SColor.TRANSPARENT;
    public Color back = SColor.TRANSPARENT;
    public Color miniMapColor = SColor.TRANSPARENT;

    public RememberedTile(EpiTile tile) {
        remake(tile);
    }

    public void remake(EpiTile tile) {
        symbol = tile.getSymbolUninhabited();
        front = tile.getForegroundColor() == null ? SColor.TRANSPARENT : tile.getForegroundColor().cpy().lerp(Color.BLACK, 0.65f);
        back = tile.getBackgroundColor() == null ? SColor.TRANSPARENT : tile.getBackgroundColor().cpy().lerp(Color.BLACK, 0.65f);
        if (tile.getCreature() != null){
            miniMapColor = SColor.SCARLET;
        } else {
            Physical p = tile.getLargeNonCreature();
            if (p == null){
                miniMapColor = SColor.DARK_INDIGO;
            } else {
                switch (p.symbol){
                    case '#':
                        miniMapColor = SColor.SILVER_GREY;
                        break;
                    case '+':
                        miniMapColor = SColor.TAWNY;
                        break;
                    default:
                        miniMapColor = SColor.FLAX;
                        break;
                }
            }
        }
    }
}

package squidpony.epigon.mapping;

import com.badlogic.gdx.graphics.Color;
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

    public RememberedTile(EpiTile tile) {
        remake(tile);
    }

    public void remake(EpiTile tile)
    {
        symbol = tile.getSymbol();
        front = tile.getForegroundColor() == null ? SColor.TRANSPARENT : tile.getForegroundColor().cpy().lerp(Color.BLACK, 0.75f);
        back = tile.getBackgroundColor() == null ? SColor.TRANSPARENT : tile.getBackgroundColor().cpy().lerp(Color.BLACK, 0.75f);
    }
}

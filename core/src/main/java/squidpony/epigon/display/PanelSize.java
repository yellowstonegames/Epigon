package squidpony.epigon.display;

/**
 * Contains the information to create display panels the size desired.
 *
 * @author Eben Howard
 */
public class PanelSize {

    public int gridWidth;
    public int gridHeight;
    public int cellWidth;
    public int cellHeight;

    public PanelSize(){}

    public PanelSize(int gridWidth, int gridHeight, int cellWidth, int cellHeight){
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public int pixelWidth() {
        return gridWidth * cellWidth;
    }

    public int pixelHeight() {
        return gridHeight * cellHeight;
    }

    /**
     * Adjust the cell width so that the grid with stays the same but the cell width grows as
     * needed, rounded down.
     *
     * @param width the exact pixels to grow to as closely as possible
     */
    public void setPixelWidth(int width) {
        cellWidth = width / gridWidth;
    }

    /**
     * Adjust the cell height so that the grid height stays the same but the cell height grows as
     * needed, rounded down.
     *
     * @param height
     */
    public void setPixelHeight(int height) {
        cellHeight = height / gridHeight;
    }
}

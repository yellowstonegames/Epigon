package squidpony.epigon.mapping;

import squidpony.squidmath.GreasedRegion;

/**
 * Holds a set of structures for creating a castle.
 *
 * @author Eben Howard
 */
public class Castle {
    /* Castle notes from real castles
    Average thickness of stone wall: 2m
    Thickness of Chepstow Castle in Wales: 6m
    Thickness of some walls at Borl Castle in Coratia: 12m
    Length of wall at Conwy Castle in Wales: 1280m

    Standard motte and baily area: 3 acres
    Malbork Castle total area: 12 square km

    Approximate largest keeps: 31m x 31m
     */
    
    public EpiMap[] buildZone;
    public EpiMap groundLevel;
    public int sky; // how high you can go
    public int width;
    public int height;
    public int mapEdging = 2; // space between the edge of the map and the generation area
    
    GreasedRegion region,
        moat,
        moatBank,
        insideMoat,
        outerWall,
        holes,
        courtyard,
        keepWall,
        insideKeep,
        garden,
        pond,
        pondBank;
    
    public Castle(EpiMap[] buildZone){
        this.buildZone = buildZone;
        sky = buildZone.length;
        groundLevel = buildZone[sky - 1];
        width = buildZone[0].width;
        height = buildZone[1].height;
        
                region = new GreasedRegion(width, height);
        region.allOn();
        for (int x = 0; x < mapEdging; x++) {
            for (int y = 0; y < height; y++) {
                region.set(false, x, y);
                region.set(false, width - 1 - x, y);
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < mapEdging; y++) {
                region.set(false, x, y);
                region.set(false, x, height - 1 - y);
            }
        }

        moat = region.copy();
    }
}

package squidpony.data.specific;

import squidpony.data.blueprints.Stone;

/**
 * A specific instance of a terrain unit.
 *
 * Should only be created if a generic instance was interacted with in a way
 * that caused it to become different than others of it's type, such as damaged.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Terrain extends Item {
    
    public Terrain(){
        super();
    }

    public Terrain(Stone stone) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

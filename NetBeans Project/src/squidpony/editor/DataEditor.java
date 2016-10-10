package squidpony.editor;

import squidpony.data.DataMaster;

/**
 * Indicates that this class can save and load data for Epigon.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public interface DataEditor {

    /**
     * Sets the DataMaster to be used by this object. All saving and loading
     * will be performed through it.
     *
     * @param master
     */
    public void setDataMaster(DataMaster master);

    /**
     * Loads the data appropriate to this editor from the provided DataMaster
     * and sets that master to be the one the editor will send save requests to.
     *
     * This will replace whatever data was previously in the editor, so that
     * data should be saved if the user desires.
     *
     */
    public void loadData();
}

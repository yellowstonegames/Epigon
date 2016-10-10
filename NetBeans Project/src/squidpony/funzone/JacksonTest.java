package squidpony.funzone;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import squidpony.data.DataMaster;
import squidpony.data.DataType;
import squidpony.data.EpiData;
import squidpony.data.generic.Element;
import squidpony.squidcolor.SColor;

/**
 * This tests out the Jackson serialization and deserialization.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class JacksonTest {

    private static HashSet<Element> elements = new HashSet<>();
    private static ObjectMapper mapper;

    public static void main(String... args) throws IOException {
        elements.add(new Element(null, "Element Zero", "E-zee", "E-zee Units", "Tests out Jackson", "Hope it works", SColor.FINCH_BROWN));
        elements.add(new Element(null, "Fire_Element", "Fire", "", "Second test of Jackson", "", null));

        File file = new File("./test.epi");
        DataMaster master = new DataMaster();
        master.addAll(elements, DataType.ELEMENT);

        master.saveToFile(file);

        DataMaster master2 = DataMaster.loadFromFile(file);
        HashSet<EpiData> objects = master2.getList(DataType.ELEMENT);

        for (EpiData ed : objects) {
            System.out.println(ed);
            System.out.println("");
        }
    }
}

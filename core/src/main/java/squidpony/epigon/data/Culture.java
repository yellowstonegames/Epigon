package squidpony.epigon.data;

import squidpony.FakeLanguageGen;
import squidpony.ProceduralMessaging;
import squidpony.epigon.data.raw.RawCulture;
import squidpony.squidmath.OrderedMap;

import java.io.Serializable;

/**
 * Created by Tommy Ettinger on 10/4/2018.
 */
public class Culture implements Serializable {
    private static final long serialVersionUID = 1L;
    public RawCulture raw;
    public String name;
    public ProceduralMessaging messaging;
    public Culture()
    {
        this(RawCulture.ENTRIES[0]);
    }
    public Culture(RawCulture raw)
    {
        this.raw = raw;
        name = raw.name;
        messaging = new ProceduralMessaging(raw.hash64(), FakeLanguageGen.get(raw.language));
    }
    public static final OrderedMap<String, Culture> cultures = new OrderedMap<>(RawCulture.ENTRIES.length);
    static {
        for (int i = 0; i < RawCulture.ENTRIES.length; i++) {
            cultures.put(RawCulture.ENTRIES[i].name, new Culture(RawCulture.ENTRIES[i])); 
        }
    }
}

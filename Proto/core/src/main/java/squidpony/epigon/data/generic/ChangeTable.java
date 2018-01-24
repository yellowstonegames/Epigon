package squidpony.epigon.data.generic;

import com.badlogic.gdx.utils.CharArray;
import com.badlogic.gdx.utils.LongArray;
import squidpony.epigon.ImmutableKey;
import squidpony.squidmath.Arrangement;
import squidpony.squidmath.OrderedMap;

import java.util.Iterator;

/**
 * Created by Tommy Ettinger on 1/23/2018.
 */
public class ChangeTable implements Iterable<ImmutableKey> {
    public Arrangement<ImmutableKey> indexer;
    public LongArray longValues;
    public CharArray changeSymbols;
    public ChangeTable()
    {
        this(12);
    }
    public ChangeTable(int expectedSize)
    {
        indexer = new Arrangement<>(expectedSize, 0.5f, ImmutableKey.ImmutableKeyHasher.instance);
        longValues = new LongArray(expectedSize);
        changeSymbols = new CharArray(expectedSize);
    }

    /**
     * Puts a new triplet of ImmutableKey, char, and long into this ChangeTable. Returns true if everything went normally,
     * or false if an existing key was changed (usually a sign of something used incorrectly).
     * @param key
     * @param symbol
     * @param value
     * @return true if the triplet was added normally, or false if it overwrote an existing triplet
     */
    public boolean put(ImmutableKey key, char symbol, long value) {
        int index = indexer.add(key);
        if (index < 0) {
            changeSymbols.add(symbol);
            longValues.add(value);
            return true;
        }
        else
        {
            changeSymbols.set(index, symbol);
            longValues.set(index, value);
            return false;
        }

    }

    public void clear() {
        indexer.clear();
        longValues.clear();
        changeSymbols.clear();
    }

    public int size() {
        return longValues.size;
    }

    public boolean isEmpty() {
        return indexer.isEmpty();
    }

    @Override
    public Iterator<ImmutableKey> iterator() {
        return indexer.iterator();
    }

    /**
     * Edits the existing OrderedMap of ImmutableKey keys to Long values using the changes in this ChangeTable.
     * @param changing a non-null OrderedMap of ImmutableKey keys to Long values
     * @return the parameter this was given, after modifications
     */
    public OrderedMap<ImmutableKey, Long> change(OrderedMap<ImmutableKey, Long> changing)
    {
        int mySize = longValues.size;
        ImmutableKey k;
        char op = '=';
        Long e;
        for (int i = 0; i < mySize; i++) {
            k = indexer.keyAt(i);
            op = changeSymbols.get(i);
            if((e = changing.get(k)) != null)
            {
                switch (op)
                {
                    case '=':
                        changing.put(k, longValues.get(i));
                        break;
                    case '+':
                        changing.put(k, e + longValues.get(i));
                        break;
                    case '-':
                        changing.put(k, e - longValues.get(i));
                        break;
                    case '*':
                        changing.put(k, e * longValues.get(i));
                        break;
                }
            }
            else
            {
                switch (op)
                {
                    case '=':
                        changing.put(k, longValues.get(i));
                        break;
                    case '+':
                        changing.put(k, longValues.get(i));
                        break;
                    case '-':
                        changing.put(k, -longValues.get(i));
                        break;
                    case '*':
                        changing.put(k, 0L);
                        break;
                }

            }
        }
        return changing;
    }

    public static ChangeTable makeCT(Object... rest)
    {
        if(rest == null || rest.length == 0)
        {
            return new ChangeTable();
        }
        ChangeTable am = new ChangeTable(rest.length / 3);
        for (int i = 0; i < rest.length - 2; i += 3) {
            try {
                am.put((ImmutableKey)rest[i], (Character) rest[i + 1], (Long)rest[i+2]);
            }catch (ClassCastException ignored) {
            }
        }
        return am;
    }

}

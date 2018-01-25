package squidpony.epigon.data.generic;

import com.badlogic.gdx.utils.CharArray;
import com.badlogic.gdx.utils.FloatArray;
import squidpony.epigon.ImmutableKey;
import squidpony.epigon.universe.LiveValue;
import squidpony.squidmath.Arrangement;
import squidpony.squidmath.OrderedMap;

import java.util.Iterator;

/**
 * Created by Tommy Ettinger on 1/23/2018.
 */
public class ChangeTable implements Iterable<ImmutableKey> {
    public Arrangement<ImmutableKey> indexer;
    public FloatArray values;
    public CharArray changeSymbols;
    public ChangeTable()
    {
        this(12);
    }
    public ChangeTable(int expectedSize)
    {
        indexer = new Arrangement<>(expectedSize, 0.5f, ImmutableKey.ImmutableKeyHasher.instance);
        values = new FloatArray(expectedSize);
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
    public boolean put(ImmutableKey key, char symbol, double value) {
        int index = indexer.add(key);
        if (index < 0) {
            changeSymbols.add(symbol);
            values.add((float) value);
            return true;
        }
        else
        {
            changeSymbols.set(index, symbol);
            values.set(index, (float)value);
            return false;
        }

    }

    public void clear() {
        indexer.clear();
        values.clear();
        changeSymbols.clear();
    }

    public int size() {
        return values.size;
    }

    public boolean isEmpty() {
        return indexer.isEmpty();
    }

    @Override
    public Iterator<ImmutableKey> iterator() {
        return indexer.iterator();
    }

    /**
     * Edits the existing OrderedMap of ImmutableKey keys to Double values using the changes in this ChangeTable.
     * @param changing a non-null OrderedMap of ImmutableKey keys to Double values; will be modified
     * @return the parameter this was given, after modifications
     */
    public OrderedMap<ImmutableKey, Double> changeDoubles(OrderedMap<ImmutableKey, Double> changing)
    {
        int mySize = values.size;
        ImmutableKey k;
        char op = '=';
        Double e;
        for (int i = 0; i < mySize; i++) {
            k = indexer.keyAt(i);
            op = changeSymbols.get(i);
            if((e = changing.get(k)) != null)
            {
                switch (op)
                {
                    case '=':
                        changing.put(k, (double)values.get(i));
                        break;
                    case '+':
                        changing.put(k, e + values.get(i));
                        break;
                    case '-':
                        changing.put(k, e - values.get(i));
                        break;
                    case '*':
                        changing.put(k, e * values.get(i));
                        break;
                }
            }
            else
            {
                switch (op)
                {
                    case '=':
                        changing.put(k, (double)values.get(i));
                        break;
                    case '+':
                        changing.put(k, (double)values.get(i));
                        break;
                    case '-':
                        changing.put(k, -(double)values.get(i));
                        break;
                    case '*':
                        changing.put(k, 0.0);
                        break;
                }

            }
        }
        return changing;
    }
    /**
     * Edits the existing OrderedMap of ImmutableKey keys to LiveValue values using the changes in this ChangeTable.
     * If a key is not present in changing but this ChangeTable has an instruction to change that key, that instruction
     * will be ignored without affecting the rest of the changes.
     * @param changing a non-null OrderedMap of ImmutableKey keys to LiveValue values; will be modified
     * @return the parameter this was given, after modifications
     */
    public OrderedMap<ImmutableKey, LiveValue> changeLiveValues(OrderedMap<ImmutableKey, LiveValue> changing)
    {
        int mySize = values.size;
        ImmutableKey k;
        char op = '=';
        LiveValue e;
        for (int i = 0; i < mySize; i++) {
            k = indexer.keyAt(i);
            op = changeSymbols.get(i);
            if((e = changing.get(k)) != null)
            {
                switch (op)
                {
                    case '=':
                        e.set(values.get(i));
                        break;
                    case '+':
                        e.addActual(values.get(i));
                        break;
                    case '-':
                        e.addActual(-values.get(i));
                        break;
                    case '*':
                        e.multiplyActual(values.get(i));
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
                am.put((ImmutableKey)rest[i], (Character) rest[i + 1], (Double) rest[i+2]);
            }catch (ClassCastException ignored) {
            }
        }
        return am;
    }

}

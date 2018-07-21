package squidpony.epigon.data;

import com.badlogic.gdx.utils.FloatArray;
import squidpony.epigon.ConstantKey;
import squidpony.squidmath.Arrangement;
import squidpony.squidmath.IntVLA;
import squidpony.squidmath.OrderedMap;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * Created by Tommy Ettinger on 1/23/2018.
 */
public class ChangeTable extends AbstractCollection<ConstantKey> {
    public Arrangement<ConstantKey> indexer;
    public FloatArray values;
    public IntVLA changeSymbols, unrelatedSymbols;
    public ChangeTable()
    {
        this(12);
    }
    public ChangeTable(int expectedSize)
    {
        indexer = new Arrangement<>(expectedSize, 0.5f, ConstantKey.ConstantKeyHasher.instance);
        values = new FloatArray(expectedSize);
        changeSymbols = new IntVLA(expectedSize);
        unrelatedSymbols = new IntVLA(4);
    }
    public ChangeTable(ChangeTable other)
    {
        indexer = new Arrangement<>(other.size(), 0.5f, ConstantKey.ConstantKeyHasher.instance);
        indexer.putAll(other.indexer);
        values = new FloatArray(other.values);
        changeSymbols = new IntVLA(other.changeSymbols);
        unrelatedSymbols = new IntVLA(other.unrelatedSymbols);
    }

    /**
     * Puts a new triplet of ConstantKey, char, and long into this ChangeTable. Returns true if everything went normally,
     * or false if an existing key was changed (usually a sign of something used incorrectly).
     * @param key a ConstantKey that usually is a stat of a creature or object
     * @param symbol an int that marks what operation should be performed on the given key; negative means destructive 
     * @param value how much change to apply with the operation specified by symbol to key
     * @return true if the triplet was added normally, or false if it overwrote an existing triplet
     */
    public boolean put(ConstantKey key, int symbol, double value) {
        if(key == null)
        {
            unrelatedSymbols.add(symbol);
            return true;
        }
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

    @Override
    public void clear() {
        indexer.clear();
        values.clear();
        changeSymbols.clear();
        unrelatedSymbols.clear();
    }

    @Override
    public int size() {
        return values.size;
    }

    @Override
    public boolean isEmpty() {
        return indexer.isEmpty();
    }

    @Override
    public Iterator<ConstantKey> iterator() {
        return indexer.iterator();
    }

//    /**
//     * Edits the existing OrderedMap of ConstantKey keys to Double values using the changes in this ChangeTable.
//     * Treats all changes as if they are destructive, which can be useful if non-destructive changes don't need to be
//     * tracked for later removal.
//     * @param changing a non-null OrderedMap of ConstantKey keys to Double values; will be modified
//     * @return the parameter this was given, after modifications
//     */
//    public OrderedMap<ConstantKey, Double> changeDoubles(OrderedMap<ConstantKey, Double> changing)
//    {
//        int mySize = values.size;
//        ConstantKey k;
//        int op;
//        Double e;
//        for (int i = 0; i < mySize; i++) {
//            k = indexer.keyAt(i);
//            op = changeSymbols.get(i);
//            if((e = changing.get(k)) != null)
//            {
//                switch (op)
//                {
//                    case '=':
//                    case ~'=':
//                        changing.put(k, (double)values.get(i));
//                        break;
//                    case '+':
//                    case ~'+':
//                        changing.put(k, e + values.get(i));
//                        break;
//                    case '-':
//                    case ~'-':
//                        changing.put(k, e - values.get(i));
//                        break;
//                    case '*':
//                    case ~'*':
//                        changing.put(k, e * values.get(i));
//                        break;
//                }
//            }
//            else
//            {
//                switch (op)
//                {
//                    case '=':
//                    case ~'=':
//                        changing.put(k, (double)values.get(i));
//                        break;
//                    case '+':
//                    case ~'+':
//                        changing.put(k, (double)values.get(i));
//                        break;
//                    case '-':
//                    case ~'-':
//                        changing.put(k, -(double)values.get(i));
//                        break;
//                    case '*':
//                    case ~'*':
//                        changing.put(k, 0.0);
//                        break;
//                }
//
//            }
//        }
//        return changing;
//    }
//    /**
//     * Edits the existing OrderedMap of ConstantKey keys to LiveValue values using the changes in this ChangeTable.
//     * If a key is not present in changing but this ChangeTable has an instruction to change that key, that instruction
//     * will be ignored without affecting the rest of the changes. Treats all changes as if they are destructive, which
//     * can be useful if non-destructive changes don't need to be tracked for later removal.
//     * @param changing a non-null OrderedMap of ConstantKey keys to LiveValue values; will be modified
//     * @return the parameter this was given, after modifications
//     */
//    public OrderedMap<ConstantKey, LiveValue> changeLiveValues(OrderedMap<ConstantKey, LiveValue> changing)
//    {
//        int mySize = values.size;
//        ConstantKey k;
//        int op;
//        LiveValue e;
//        for (int i = 0; i < mySize; i++) {
//            k = indexer.keyAt(i);
//            op = changeSymbols.get(i);
//            if((e = changing.get(k)) != null)
//            {
//                switch (op)
//                {
//                    case '=':
//                    case ~'=':
//                        e.set(values.get(i));
//                        break;
//                    case '+':
//                    case ~'+':
//                        e.addActual(values.get(i));
//                        break;
//                    case '-':
//                    case ~'-':
//                        e.addActual(-values.get(i));
//                        break;
//                    case '*':
//                    case ~'*':
//                        e.multiplyActual(values.get(i));
//                        break;
//                }
//            }
//        }
//        return changing;
//    }
//    /**
//     * Edits the existing OrderedMap of ConstantKey keys to LiveValue values using the changes in the given Iterable of
//     * ChangeTable values. If a key is not present in changing but a ChangeTable in tables has an instruction to change
//     * that key, that instruction will be ignored without affecting the other changes. Treats all changes as if they are
//     * destructive, which can be useful if non-destructive changes don't need to be tracked for later removal.
//     * @param changing a non-null OrderedMap of ConstantKey keys to LiveValue values; will be modified
//     * @param tables an Iterable of ChangeTable values, such as an ArrayList or OrderedSet
//     * @return the parameter this was given, after modifications
//     */
//    public static OrderedMap<ConstantKey, LiveValue> changeManyLiveValues(OrderedMap<ConstantKey, LiveValue> changing, Iterable<ChangeTable> tables)
//    {
//        int originalSize = changing.size();
//        ConstantKey k;
//        int op;
//        LiveValue e;
//        int index;
//        double v;
//        for (int i = 0; i < originalSize; i++) {
//            k = changing.keyAt(i);
//            e = changing.getAt(i);
//            v = e.actual();
//            for (ChangeTable ct : tables) {
//                if ((index = ct.indexer.getInt(k)) < 0)
//                    continue;
//                op = ct.changeSymbols.get(index);
//                switch (op) {
//                    case '=':
//                    case ~'=':
//                        v = ct.values.get(index);
//                        break;
//                    case '+':
//                    case ~'+':
//                        v += ct.values.get(index);
//                        break;
//                    case '-':
//                    case ~'-':
//                        v -= ct.values.get(index);
//                        break;
//                    case '*':
//                    case ~'*':
//                        v *= ct.values.get(index);
//                        break;
//                }
//            }
//            e.actual(v);
//        }
//        return changing;
//    }
//    /**
//     * Edits the existing OrderedMap of ConstantKey keys to LiveValue values using only the destructive changes in the
//     * given Iterable of ChangeTable values ("strike" is used to mean a destructive change, while "hold" is a
//     * non-destructive change; non-destructive changes will be ignored here). If a key is not present in changing but a
//     * ChangeTable in tables has an instruction to change that key, that instruction will be ignored without affecting
//     * the other changes.
//     * @param changing a non-null OrderedMap of ConstantKey keys to LiveValue values; will be modified
//     * @param tables an Iterable of ChangeTable values, such as an ArrayList or OrderedSet
//     * @return the parameter this was given, after modifications
//     */
//    public static OrderedMap<ConstantKey, LiveValue> strikeManyLiveValues(OrderedMap<ConstantKey, LiveValue> changing, Iterable<ChangeTable> tables)
//    {
//        int originalSize = changing.size();
//        ConstantKey k;
//        int op;
//        LiveValue e;
//        int index;
//        double v;
//        for (int i = 0; i < originalSize; i++) {
//            k = changing.keyAt(i);
//            e = changing.getAt(i);
//            v = e.actual();
//            for (ChangeTable ct : tables) {
//                if ((index = ct.indexer.getInt(k)) < 0)
//                    continue;
//                op = ct.changeSymbols.get(index);
//                switch (op) {
//                    case ~'=':
//                        v = ct.values.get(index);
//                        break;
//                    case ~'+':
//                        v += ct.values.get(index);
//                        break;
//                    case ~'-':
//                        v -= ct.values.get(index);
//                        break;
//                    case ~'*':
//                        v *= ct.values.get(index);
//                        break;
//                }
//            }
//            e.actual(v);
//        }
//        return changing;
//    }
//    /**
//     * Edits the existing OrderedMap of ConstantKey keys to LiveValue values using only the non-destructive changes in
//     * the given Iterable of ChangeTable values ("strike" is used to mean a destructive change, while "hold" is a
//     * non-destructive change; destructive changes will be ignored here). If a key is not present in changing but a
//     * ChangeTable in tables has an instruction to change that key, that instruction will be ignored without affecting
//     * the other changes. The changes applied by this method can be reversed, mostly, by 
//     * {@link #releaseManyLiveValues(OrderedMap, Iterable)}.
//     * @param changing a non-null OrderedMap of ConstantKey keys to LiveValue values; will be modified
//     * @param tables an Iterable of ChangeTable values, such as an ArrayList or OrderedSet
//     * @return the parameter this was given, after modifications
//     */
//    public static OrderedMap<ConstantKey, LiveValue> holdManyLiveValues(OrderedMap<ConstantKey, LiveValue> changing, Iterable<ChangeTable> tables)
//    {
//        int originalSize = changing.size();
//        ConstantKey k;
//        int op;
//        LiveValue e;
//        int index;
//        double v;
//        for (int i = 0; i < originalSize; i++) {
//            k = changing.keyAt(i);
//            e = changing.getAt(i);
//            v = e.actual();
//            for (ChangeTable ct : tables) {
//                if ((index = ct.indexer.getInt(k)) < 0)
//                    continue;
//                op = ct.changeSymbols.get(index);
//                switch (op) {
//                    case '=':
//                        v = ct.values.get(index);
//                        break;
//                    case '+':
//                        v += ct.values.get(index);
//                        break;
//                    case '-':
//                        v -= ct.values.get(index);
//                        break;
//                    case '*':
//                        v *= ct.values.get(index);
//                        break;
//                }
//            }
//            e.actual(v);
//        }
//        return changing;
//    }
//    /**
//     * Edits the existing OrderedMap of ConstantKey keys to LiveValue values using only the reversed non-destructive
//     * changes in the given Iterable of ChangeTable values ("strike" is used to mean a destructive change, while "hold"
//     * is a non-destructive change that is reversed by a "release" operation; destructive changes will be ignored here).
//     * If a key is not present in changing but a ChangeTable in tables has an instruction to change that key, that
//     * instruction will be ignored without affecting the other changes.
//     * @param changing a non-null OrderedMap of ConstantKey keys to LiveValue values; will be modified
//     * @param tables an Iterable of ChangeTable values, such as an ArrayList or OrderedSet; the opposite of its non-destructive instructions will be applied
//     * @return the parameter this was given, after modifications
//     */
//    public static OrderedMap<ConstantKey, LiveValue> releaseManyLiveValues(OrderedMap<ConstantKey, LiveValue> changing, Iterable<ChangeTable> tables)
//    {
//        int originalSize = changing.size();
//        ConstantKey k;
//        int op;
//        LiveValue e;
//        int index;
//        double v;
//        for (int i = 0; i < originalSize; i++) {
//            k = changing.keyAt(i);
//            e = changing.getAt(i);
//            v = e.actual();
//            for (ChangeTable ct : tables) {
//                if ((index = ct.indexer.getInt(k)) < 0)
//                    continue;
//                op = ct.changeSymbols.get(index);
//                switch (op) {
//                    case '=': // not yet sure how non-destructive assignment can even work
//                        //v = ct.values.get(index);
//                        break;
//                    case '+':
//                        v -= ct.values.get(index);
//                        break;
//                    case '-':
//                        v += ct.values.get(index);
//                        break;
//                    case '*':
//                        v /= ct.values.get(index);
//                        break;
//                }
//            }
//            e.actual(v);
//        }
//        return changing;
//    }

    /**
     * Edits the existing Physical using only the destructive changes in the given Iterable of ChangeTable values
     * ("strike" is used to mean a destructive change, while "hold" is a non-destructive change; non-destructive changes
     * will be ignored here). The methods in this class that operate on Physical parameters can perform extra operations
     * on those Physical values, like changing their equipment. If a key is not present in changing but a ChangeTable in
     * tables has an instruction to change that key, that instruction will be ignored without affecting the others.
     * @param physical a Physical that will be modified, including by some operations that do nothing on plain Maps
     * @param ct a ChangeTable value
     * @return the parameter this was given, after modifications
     */
    public static Physical strikePhysical(Physical physical, ChangeTable ct)
    {
        OrderedMap<ConstantKey, LiveValue> changing = physical.stats;
        int originalSize = changing.size();
        ConstantKey k;
        int op;
        LiveValue e;
        int index;
        double v;
        for (int i = 0; i < originalSize; i++) {
            k = changing.keyAt(i);
            e = changing.getAt(i);
            v = e.actual();
            if ((index = ct.indexer.getInt(k)) < 0)
                continue;
            op = ct.changeSymbols.get(index);
            switch (op) {
                case ~'=':
                    v = ct.values.get(index);
                    break;
                case ~'+':
                    v += ct.values.get(index);
                    break;
                case ~'-':
                    v -= ct.values.get(index);
                    break;
                case ~'*':
                    v *= ct.values.get(index);
                    break;
                    // be careful of these delta-related effects in strikes; they must be reversed with another ChangeTable, if at all
                // set delta
                case ~':':
                    e.delta(ct.values.get(index));
                    break;
                // lessen, reduces LiveValue delta
                case ~'<':
                    e.delta(e.delta() - ct.values.get(index));
                    break;
                // raise, increases LiveValue delta
                case ~'>':
                    e.delta(e.delta() + ct.values.get(index));
                    break;
            }
            e.actual(v);
        }         
        for (int i = 0; i < ct.unrelatedSymbols.size; i++) {
            switch (ct.unrelatedSymbols.get(i)) {
                case ~'d':
                    physical.disarm();
                    break;
                case ~'s':
                    physical.sunder(4.0);
                    break;
                case ~'S':
                    physical.sunder(8.0);
                    break;
            }
        }

        return physical;
    }
    /**
     * Edits the existing OrderedMap of ConstantKey keys to LiveValue values using only the non-destructive changes in
     * the given Iterable of ChangeTable values ("strike" is used to mean a destructive change, while "hold" is a
     * non-destructive change; destructive changes will be ignored here). If a key is not present in changing but a
     * ChangeTable in tables has an instruction to change that key, that instruction will be ignored without affecting
     * the other changes. The changes applied by this method can be reversed, mostly, by 
     * {@link #releasePhysical(Physical, Iterable)}.
     * @param physical a Physical that will be modified, including by some operations that do nothing on plain Maps
     * @param ct a ChangeTable value
     * @return the parameter this was given, after modifications
     */
    public static Physical holdPhysical(Physical physical, ChangeTable ct)
    {
        OrderedMap<ConstantKey, LiveValue> changing = physical.stats;
        int originalSize = changing.size();
        ConstantKey k;
        int op;
        LiveValue e;
        int index;
        double v;
        for (int i = 0; i < originalSize; i++) {
            k = changing.keyAt(i);
            e = changing.getAt(i);
            v = e.actual();
            if ((index = ct.indexer.getInt(k)) < 0)
                continue;
            op = ct.changeSymbols.get(index);
            switch (op) {
                case '=':
                    v = ct.values.get(index);
                    break;
                case '+':
                    v += ct.values.get(index);
                    break;
                case '-':
                    v -= ct.values.get(index);
                    break;
                case '*':
                    v *= ct.values.get(index);
                    break;
                // set delta
                case ':':
                    e.delta(ct.values.get(index));
                    break;
                // lessen, reduces LiveValue delta
                case '<':
                    e.delta(e.delta() - ct.values.get(index));
                    break;
                // raise, increases LiveValue delta
                case '>':
                    e.delta(e.delta() + ct.values.get(index));
                    break;
            }
            e.actual(v);
        }
        return physical;
    }
    /**
     * Edits the existing OrderedMap of ConstantKey keys to LiveValue values using only the reversed non-destructive
     * changes in the given Iterable of ChangeTable values ("strike" is used to mean a destructive change, while "hold"
     * is a non-destructive change that is reversed by a "release" operation; destructive changes will be ignored here).
     * If a key is not present in changing but a ChangeTable in tables has an instruction to change that key, that
     * instruction will be ignored without affecting the other changes.
     * @param physical a Physical that will be modified, including by some operations that do nothing on plain Maps
     * @param ct a ChangeTable value; the opposite of its non-destructive instructions will be applied
     * @return the parameter this was given, after modifications
     */
    public static Physical releasePhysical(Physical physical, ChangeTable ct)
    {
        OrderedMap<ConstantKey, LiveValue> changing = physical.stats;
        int originalSize = changing.size();
        ConstantKey k;
        int op;
        LiveValue e;
        int index;
        double v;
        for (int i = 0; i < originalSize; i++) {
            k = changing.keyAt(i);
            e = changing.getAt(i);
            v = e.actual();

            if ((index = ct.indexer.getInt(k)) < 0)
                continue;
            op = ct.changeSymbols.get(index);
            switch (op) {
                case '=': // not yet sure how non-destructive assignment can even work
                    //v = ct.values.get(index);
                    break;
                case '+':
                    v -= ct.values.get(index);
                    break;
                case '-':
                    v += ct.values.get(index);
                    break;
                case '*':
                    v /= ct.values.get(index);
                    break;
                // set delta
                case ':': // again, not sure how non-destructive assignment works
                    //e.delta(ct.values.get(index));
                    break;
                // lessen, reduces LiveValue delta
                case '<':
                    e.delta(e.delta() + ct.values.get(index));
                    break;
                // raise, increases LiveValue delta
                case '>':
                    e.delta(e.delta() - ct.values.get(index));
                    break;
            }

            e.actual(v);
        }
        return physical;
    }
    /**
     * Edits the existing Physical using only the destructive changes in the given Iterable of ChangeTable values
     * ("strike" is used to mean a destructive change, while "hold" is a non-destructive change; non-destructive changes
     * will be ignored here). The methods in this class that operate on Physical parameters can perform extra operations
     * on those Physical values, like changing their equipment. If a key is not present in changing but a ChangeTable in
     * tables has an instruction to change that key, that instruction will be ignored without affecting the others.
     * @param physical a Physical that will be modified, including by some operations that do nothing on plain Maps
     * @param tables an Iterable of ChangeTable values, such as an ArrayList or OrderedSet
     * @return the parameter this was given, after modifications
     */
    public static Physical strikePhysical(Physical physical, Iterable<ChangeTable> tables)
    {
        OrderedMap<ConstantKey, LiveValue> changing = physical.stats;
        int originalSize = changing.size();
        ConstantKey k;
        int op;
        LiveValue e;
        int index;
        double v;
        for (int i = 0; i < originalSize; i++) {
            k = changing.keyAt(i);
            e = changing.getAt(i);
            v = e.actual();
            for (ChangeTable ct : tables) {
                if ((index = ct.indexer.getInt(k)) < 0)
                    continue;
                op = ct.changeSymbols.get(index);
                switch (op) {
                    case ~'=':
                        v = ct.values.get(index);
                        break;
                    case ~'+':
                        v += ct.values.get(index);
                        break;
                    case ~'-':
                        v -= ct.values.get(index);
                        break;
                    case ~'*':
                        v *= ct.values.get(index);
                        break;
                    // be careful of these delta-related effects in strikes; they must be reversed with another ChangeTable, if at all
                    // set delta
                    case ~':':
                        e.delta(ct.values.get(index));
                        break;
                    // lessen, reduces LiveValue delta
                    case ~'<':
                        e.delta(e.delta() - ct.values.get(index));
                        break;
                    // raise, increases LiveValue delta
                    case ~'>':
                        e.delta(e.delta() + ct.values.get(index));
                        break;

                }
            }
            e.actual(v);
        }
        for(ChangeTable ct : tables) {
            for (int i = 0; i < ct.unrelatedSymbols.size; i++) {
                switch (ct.unrelatedSymbols.get(i))
                {
                    case ~'d':
                        physical.disarm();
                        break;
                    case ~'s':
                        physical.sunder(4.0);
                        break;
                    case ~'S':
                        physical.sunder(8.0);
                        break;
                }
            }
        }
        return physical;
    }
    /**
     * Edits the existing OrderedMap of ConstantKey keys to LiveValue values using only the non-destructive changes in
     * the given Iterable of ChangeTable values ("strike" is used to mean a destructive change, while "hold" is a
     * non-destructive change; destructive changes will be ignored here). If a key is not present in changing but a
     * ChangeTable in tables has an instruction to change that key, that instruction will be ignored without affecting
     * the other changes. The changes applied by this method can be reversed, mostly, by 
     * {@link #releasePhysical(Physical, Iterable)}.
     * @param physical a Physical that will be modified, including by some operations that do nothing on plain Maps
     * @param tables an Iterable of ChangeTable values, such as an ArrayList or OrderedSet
     * @return the parameter this was given, after modifications
     */
    public static Physical holdPhysical(Physical physical, Iterable<ChangeTable> tables)
    {
        OrderedMap<ConstantKey, LiveValue> changing = physical.stats;
        int originalSize = changing.size();
        ConstantKey k;
        int op;
        LiveValue e;
        int index;
        double v;
        for (int i = 0; i < originalSize; i++) {
            k = changing.keyAt(i);
            e = changing.getAt(i);
            v = e.actual();
            for (ChangeTable ct : tables) {
                if ((index = ct.indexer.getInt(k)) < 0)
                    continue;
                op = ct.changeSymbols.get(index);
                switch (op) {
                    case '=':
                        v = ct.values.get(index);
                        break;
                    case '+':
                        v += ct.values.get(index);
                        break;
                    case '-':
                        v -= ct.values.get(index);
                        break;
                    case '*':
                        v *= ct.values.get(index);
                        break;
                    // set delta
                    case ':':
                        e.delta(ct.values.get(index));
                        break;
                    // lessen, reduces LiveValue delta
                    case '<':
                        e.delta(e.delta() - ct.values.get(index));
                        break;
                    // raise, increases LiveValue delta
                    case '>':
                        e.delta(e.delta() + ct.values.get(index));
                        break;

                }
            }
            e.actual(v);
        }
        return physical;
    }
    /**
     * Edits the existing OrderedMap of ConstantKey keys to LiveValue values using only the reversed non-destructive
     * changes in the given Iterable of ChangeTable values ("strike" is used to mean a destructive change, while "hold"
     * is a non-destructive change that is reversed by a "release" operation; destructive changes will be ignored here).
     * If a key is not present in changing but a ChangeTable in tables has an instruction to change that key, that
     * instruction will be ignored without affecting the other changes.
     * @param physical a Physical that will be modified, including by some operations that do nothing on plain Maps
     * @param tables an Iterable of ChangeTable values, such as an ArrayList or OrderedSet; the opposite of its non-destructive instructions will be applied
     * @return the parameter this was given, after modifications
     */
    public static Physical releasePhysical(Physical physical, Iterable<ChangeTable> tables)
    {
        OrderedMap<ConstantKey, LiveValue> changing = physical.stats;
        int originalSize = changing.size();
        ConstantKey k;
        int op;
        LiveValue e;
        int index;
        double v;
        for (int i = 0; i < originalSize; i++) {
            k = changing.keyAt(i);
            e = changing.getAt(i);
            v = e.actual();
            for (ChangeTable ct : tables) {
                if ((index = ct.indexer.getInt(k)) < 0)
                    continue;
                op = ct.changeSymbols.get(index);
                switch (op) {
                    case '=': // not yet sure how non-destructive assignment can even work
                        //v = ct.values.get(index);
                        break;
                    case '+':
                        v -= ct.values.get(index);
                        break;
                    case '-':
                        v += ct.values.get(index);
                        break;
                    case '*':
                        v /= ct.values.get(index);
                        break;
                    // set delta
                    case ':': // again, not sure how non-destructive assignment works
                        //e.delta(ct.values.get(index));
                        break;
                    // lessen, reduces LiveValue delta
                    case '<':
                        e.delta(e.delta() + ct.values.get(index));
                        break;
                    // raise, increases LiveValue delta
                    case '>':
                        e.delta(e.delta() - ct.values.get(index));
                        break;
                }
            }
            e.actual(v);
        }
        return physical;
    }

    /**
     * Takes parameters in groups of three, first a ConstantKey (or null), then an Integer, then a Double.
     * For example, {@code ChangeTable.makeCT(CalcStat.DAMAGE, (int)'-', 2.0, null, ~'d', 2.0)} would penalize
     * damage by 2.0 and have a chance to instantly disarm the target.
     * @param rest vararg made of groups of three consisting of ConstantKey, Integer, Double
     * @return a new ChangeTable
     */
    public static ChangeTable makeCT(Object... rest)
    {
        if(rest == null || rest.length < 3)
        {
            return new ChangeTable();
        }
        ChangeTable am = new ChangeTable(rest.length / 3);
        for (int i = 0; i < rest.length - 2; i += 3) {
            try {
                if(rest[i] == null)
                    am.put(null, (Integer) rest[i + 1], (Double) rest[i+2]);
                else 
                    am.put((ConstantKey)rest[i], (Integer) rest[i + 1], (Double) rest[i+2]);
            }catch (ClassCastException uhh) {
                uhh.printStackTrace();
            }
        }
        return am;
    }

    public ChangeTable putMany(Object... rest)
    {
        if(rest == null || rest.length < 3)
        {
            return this;
        }
        for (int i = 0; i < rest.length - 2; i += 3) {
            try {
                put((ConstantKey)rest[i], (Integer) rest[i + 1], (Double) rest[i+2]);
            }catch (ClassCastException ignored) {
            }
        }
        return this;
    }

}

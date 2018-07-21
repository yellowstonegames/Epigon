package squidpony.epigon.data;

import squidpony.Maker;
import squidpony.epigon.GauntRNG;
import squidpony.squidmath.IntVLA;
import squidpony.squidmath.WeightedTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Tommy Ettinger on 1/5/2018.
 */
public class WeightedTableWrapper<T> {
    public WeightedTable table;
    public ArrayList<T> items;
    public double[] originalWeights;
    public IntVLA minAmounts, maxAmounts;
    public long chaos;

    private WeightedTableWrapper()
    {
    }
    public WeightedTableWrapper(long chaos, T[] items, double[] weights) {
        if (items == null || weights == null)
            throw new NullPointerException("WeightedTableWrapper cannot be given null items or weights");
        this.chaos = chaos;
        this.items = Maker.makeList(items);
        int len = items.length;
        table = new WeightedTable(originalWeights = Arrays.copyOf(weights, len));
        minAmounts = new IntVLA(len);
        maxAmounts = new IntVLA(len);
        for (int i = 0; i < len; i++) {
            minAmounts.add(1);
            maxAmounts.add(1);
        }
    }
    public WeightedTableWrapper(long chaos, T[] items, double[] weights, int[] mins, int[] maxes) {
        if (items == null || weights == null)
            throw new NullPointerException("WeightedTableWrapper cannot be given null items or weights");
        this.chaos = chaos;
        this.items = Maker.makeList(items);
        int len = items.length;
        table = new WeightedTable(originalWeights = Arrays.copyOf(weights, len));
        if (mins == null || maxes == null) {
            minAmounts = new IntVLA(len);
            maxAmounts = new IntVLA(len);
            for (int i = 0; i < len; i++) {
                minAmounts.add(1);
                maxAmounts.add(1);
            }
        } else {
            minAmounts = new IntVLA(mins);
            maxAmounts = new IntVLA(maxes);
        }
    }
    public WeightedTableWrapper(long chaos, T item, double weight) {
        this(chaos, item, weight, 1, 1);
    }
    public WeightedTableWrapper(long chaos, T item, double weight, int min, int max) {
        this.chaos = chaos;
        items = new ArrayList<>(1);
        items.add(item);
        table = new WeightedTable(originalWeights = new double[]{weight});
        minAmounts = new IntVLA(1);
        maxAmounts = new IntVLA(1);
        minAmounts.add(min);
        maxAmounts.add(max);
    }
    public WeightedTableWrapper(long chaos, Collection<T> items, double[] weights) {
        if (items == null || weights == null)
            throw new NullPointerException("WeightedTableWrapper cannot be given null items or weights");
        this.chaos = chaos;
        this.items = new ArrayList<>(items);
        int len = items.size();
        table = new WeightedTable(originalWeights = Arrays.copyOf(weights, len));
        minAmounts = new IntVLA(len);
        maxAmounts = new IntVLA(len);
        for (int i = 0; i < len; i++) {
            minAmounts.add(1);
            maxAmounts.add(1);
        }
    }
    public WeightedTableWrapper(long chaos, Collection<T> items, double[] weights, IntVLA minAmounts, IntVLA maxAmounts) {
        if (items == null || weights == null)
            throw new NullPointerException("WeightedTableWrapper cannot be given null items or weights");
        this.chaos = chaos;
        this.items = new ArrayList<>(items);
        int len = items.size();
        table = new WeightedTable(originalWeights = Arrays.copyOf(weights, len));
        if (minAmounts == null || maxAmounts == null) {
            this.minAmounts = new IntVLA(len);
            this.maxAmounts = new IntVLA(len);
            for (int i = 0; i < len; i++) {
                this.minAmounts.add(1);
                this.maxAmounts.add(1);
            }
        } else {
            this.minAmounts = new IntVLA(minAmounts);
            this.maxAmounts = new IntVLA(maxAmounts);
        }
    }
    public WeightedTableWrapper<T> copy()
    {
        return new WeightedTableWrapper<>(chaos, items, originalWeights, minAmounts, maxAmounts);
    }
    public void add(T item, double weight)
    {
        items.add(item);
        originalWeights = Arrays.copyOf(originalWeights, originalWeights.length + 1);
        originalWeights[originalWeights.length - 1] = weight;
        table = new WeightedTable(originalWeights);
        minAmounts.add(1);
        maxAmounts.add(1);
    }
    public void add(T item, double weight, int minAmount, int maxAmount)
    {
        items.add(item);
        originalWeights = Arrays.copyOf(originalWeights, originalWeights.length + 1);
        originalWeights[originalWeights.length - 1] = weight;
        table = new WeightedTable(originalWeights);
        minAmounts.add(minAmount);
        maxAmounts.add(maxAmount);
    }
    public void addAll(T[] more, double[] weights)
    {
        Collections.addAll(items, more);
        originalWeights = Arrays.copyOf(originalWeights, originalWeights.length + weights.length);
        System.arraycopy(weights, 0, originalWeights, originalWeights.length - weights.length, weights.length);
        table = new WeightedTable(originalWeights);
        for (int i = 0; i < weights.length; i++) {
            minAmounts.add(1);
            maxAmounts.add(1);
        }
    }
    public void addAll(T[] more, double[] weights, int[] mins, int[] maxes)
    {
        Collections.addAll(items, more);
        originalWeights = Arrays.copyOf(originalWeights, originalWeights.length + weights.length);
        System.arraycopy(weights, 0, originalWeights, originalWeights.length - weights.length, weights.length);
        table = new WeightedTable(originalWeights);
        minAmounts.addAll(mins);
        maxAmounts.addAll(maxes);
    }
    public void addAll(Collection<T> more, double[] weights)
    {
        items.addAll(more);
        originalWeights = Arrays.copyOf(originalWeights, originalWeights.length + weights.length);
        System.arraycopy(weights, 0, originalWeights, originalWeights.length - weights.length, weights.length);
        table = new WeightedTable(originalWeights);
        for (int i = 0; i < weights.length; i++) {
            minAmounts.add(1);
            maxAmounts.add(1);
        }
    }
    public void addAll(Collection<T> more, double[] weights, int[] mins, int[] maxes)
    {
        items.addAll(more);
        originalWeights = Arrays.copyOf(originalWeights, originalWeights.length + weights.length);
        System.arraycopy(weights, 0, originalWeights, originalWeights.length - weights.length, weights.length);
        table = new WeightedTable(originalWeights);
        minAmounts.addAll(mins);
        maxAmounts.addAll(maxes);
    }
    public void addAll(Collection<T> more, Collection<Double> weights)
    {
        items.addAll(more);
        int start = originalWeights.length, len = weights.size();
        originalWeights = Arrays.copyOf(originalWeights, start + len);
        for(Double d : weights)
        {
            originalWeights[start++] = d;
        }
        table = new WeightedTable(originalWeights);
        for (int i = 0; i < len; i++) {
            minAmounts.add(1);
            maxAmounts.add(1);
        }
    }
    public void addAll(Collection<T> more, Collection<Double> weights, IntVLA mins, IntVLA maxes)
    {
        items.addAll(more);
        int start = originalWeights.length, len = weights.size();
        originalWeights = Arrays.copyOf(originalWeights, start + len);
        for(Double d : weights)
        {
            originalWeights[start++] = d;
        }
        table = new WeightedTable(originalWeights);
        minAmounts.addAll(mins);
        maxAmounts.addAll(maxes);
    }

    /**
     * Call this before calling {@link #random()} if you want to know how many of that item should be produced.
     * This does not advance the RNG state; this allows a subsequent call to random() to use the same state and thus
     * refer to the same type of item (giving an appropriate quantity).
     * @return an amount appropriate for an immediately-following call to {@link #random()}
     */
    public int quantity()
    {
        int idx = table.random(chaos + 1); // intentionally not assigning here
        return GauntRNG.between(chaos, minAmounts.get(idx), maxAmounts.get(idx)+1);
    }
    public T random()
    {
        return items.get(table.random(++chaos));
    }
}

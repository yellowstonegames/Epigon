package squidpony.epigon.data;

import squidpony.Maker;
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
    public long chaos;

    private WeightedTableWrapper()
    {
    }
    public WeightedTableWrapper(long chaos, T[] items, double[] weights) {
        if (items == null || weights == null)
            throw new NullPointerException("WeightedTableWrapper cannot be given null items or weights");
        this.chaos = chaos;
        this.items = Maker.makeList(items);
        table = new WeightedTable(originalWeights = Arrays.copyOf(weights, items.length));
    }
    public WeightedTableWrapper(long chaos, Collection<T> items, double[] weights) {
        if (items == null || weights == null)
            throw new NullPointerException("WeightedTableWrapper cannot be given null items or weights");
        this.chaos = chaos;
        this.items = new ArrayList<>(items);
        table = new WeightedTable(originalWeights = Arrays.copyOf(weights, items.size()));
    }
    public WeightedTableWrapper<T> copy()
    {
        return new WeightedTableWrapper<>(chaos, items, originalWeights);
    }
    public void add(T item, double weight)
    {
        items.add(item);
        originalWeights = Arrays.copyOf(originalWeights, originalWeights.length + 1);
        originalWeights[originalWeights.length - 1] = weight;
        table = new WeightedTable(originalWeights);
    }
    public void addAll(T[] more, double[] weights)
    {
        Collections.addAll(items, more);
        originalWeights = Arrays.copyOf(originalWeights, originalWeights.length + weights.length);
        System.arraycopy(weights, 0, originalWeights, originalWeights.length - weights.length, weights.length);
        table = new WeightedTable(originalWeights);
    }
    public void addAll(Collection<T> more, double[] weights)
    {
        items.addAll(more);
        originalWeights = Arrays.copyOf(originalWeights, originalWeights.length + weights.length);
        System.arraycopy(weights, 0, originalWeights, originalWeights.length - weights.length, weights.length);
        table = new WeightedTable(originalWeights);
    }
    public void addAll(Collection<T> more, Collection<Double> weights)
    {
        items.addAll(more);
        int start = originalWeights.length;
        originalWeights = Arrays.copyOf(originalWeights, start + weights.size());
        for(Double d : weights)
        {
            originalWeights[start++] = d;
        }
        table = new WeightedTable(originalWeights);
    }
    public T random()
    {
        return items.get(table.random(++chaos));
    }
}

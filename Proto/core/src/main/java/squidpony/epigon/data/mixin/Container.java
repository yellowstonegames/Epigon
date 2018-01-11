package squidpony.epigon.data.mixin;

import squidpony.epigon.data.specific.Physical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Container {

    public double capacity;

    public List<Physical> contents;
    public Container()
    {
        capacity = 1.0;
    }
    public Container(double capacity, Collection<Physical> contents)
    {
        this.capacity = capacity;
        this.contents = new ArrayList<>(contents);
    }
}

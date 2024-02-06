package io.parsingdata.metal.data;

import java.util.LinkedList;
import java.util.List;

public class ReversedImmutableList<T> extends ImmutableList<T> {
    private final ImmutableList<T> originalList;

    public ReversedImmutableList(ImmutableList<T> originalList) {
        super(originalList);
        this.originalList = originalList;
    }

    public ReversedImmutableList(List<T> originalList) {
        super(originalList);
        this.originalList = new ImmutableList<>(originalList);
    }

    @Override
    public T get(int index) {
        return originalList.get(size() - index - 1);
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size()];
        for (int i = 0; i < size(); i++)
            result[size() - i - 1] = originalList.get(i);
        return result;
    }

    @Override
    public ImmutableList<T> addHead(final T head) {
        final LinkedList<T> ts2 = new LinkedList<>(originalList);
        ts2.addLast(head);
        return new ReversedImmutableList<>(ts2);
    }

    @Override
    public ImmutableList<T> addList(final ImmutableList<T> list) {
        final LinkedList<T> ts2 = new LinkedList<>(list.reverse());
        ts2.addAll(originalList);
        return new ReversedImmutableList<>(ts2);
    }

    @Override
    public T head() {
        if (isEmpty()) {
            return null;
        }
        return originalList.get(originalList.size() - 1);
    }

    @Override
    public ImmutableList<T> tail() {
        return new ReversedImmutableList<>(originalList.subList(0, size() - 1));
    }

    @Override
    public ImmutableList<T> reverse() {
        return originalList;
    }
}

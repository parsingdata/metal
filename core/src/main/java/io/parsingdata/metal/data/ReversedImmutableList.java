package io.parsingdata.metal.data;

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
    public T getFirst() {
        return originalList.getLast();
    }

    @Override
    public T getLast() {
        return originalList.getFirst();
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
        final ImmutableList<T> ts2 = new ImmutableList<>(originalList);
        ts2.addLast(head);
        return new ReversedImmutableList<>(ts2);
    }

    @Override
    public ImmutableList<T> addList(final ImmutableList<T> list) {
        final ImmutableList<T> ts2 = new ImmutableList<>(originalList);
        ts2.addAll(list.reverse());
        return new ReversedImmutableList<>(ts2);
    }

    @Override
    public T head() {
        if (isEmpty()) {
            return null;
        }
        return originalList.getLast();
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

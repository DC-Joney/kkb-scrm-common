package com.kkb.common.tools.concurrent.validate;


import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;


/**
 * @author zhangyang
 */
public class ListWrapper<T> extends CollectionWrapper<List<T>, T> implements List<T> {

    public ListWrapper() {
        super(new ArrayList<>());
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        return instance.addAll(index, c);
    }

    @Override
    public T get(int index) {
        return instance.get(index);
    }

    @Override
    public T set(int index, T element) {
        return instance.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        instance.add(index, element);
    }

    @Override
    public T remove(int index) {
        return instance.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return instance.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return instance.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return instance.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return instance.subList(fromIndex, toIndex);
    }
}

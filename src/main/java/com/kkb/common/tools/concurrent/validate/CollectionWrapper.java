package com.kkb.common.tools.concurrent.validate;

import lombok.Getter;
import org.springframework.lang.NonNull;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Iterator;

/**
 * 当前台传入list类型时，默认spring不会校验 list内部的 obj 对象
 *
 * @author zhangyang
 */
public abstract class CollectionWrapper<T extends Collection<E>, E> implements Collection<E> {

    @Valid
    @Getter
    protected final T instance;


    CollectionWrapper(@NonNull T instance) {
        this.instance = instance;
    }


    @Override
    public int size() {
        return instance.size();
    }

    @Override
    public boolean isEmpty() {
        return instance.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return instance.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return instance.iterator();
    }

    @Override
    public Object[] toArray() {
        return instance.toArray();
    }

    @Override
    public <A> A[] toArray(@NonNull A[] a) {
        return instance.toArray(a);
    }


    @Override
    public boolean add(E t) {
        return instance.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return instance.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return instance.containsAll(collection);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> collection) {
        return instance.addAll(collection);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return instance.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return instance.retainAll(collection);
    }

    @Override
    public void clear() {
        instance.clear();
    }
}

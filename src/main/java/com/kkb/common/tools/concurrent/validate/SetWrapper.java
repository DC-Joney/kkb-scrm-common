package com.kkb.common.tools.concurrent.validate;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author zhangyang
 */
public class SetWrapper<T> extends CollectionWrapper<Set<T>, T> implements Set<T> {

    public SetWrapper() {
        super(new HashSet<>());
    }



}

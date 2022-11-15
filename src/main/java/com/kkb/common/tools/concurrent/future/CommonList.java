package com.kkb.common.tools.concurrent.future;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Delegate;

import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
public class CommonList<T> implements List<T>{

    @Delegate
    private List<T>  digest;

    public synchronized CommonList<T> addAllData(List<T> dataList){
        digest.addAll(dataList);
        return this;
    }

    public List<T> getActual(){
        return digest;
    }

    public static <E> CommonList<E> fromLinked(){
        return new CommonList<E>(new LinkedList<>());
    }


}

package com.votelimes.memtask.storageutils;

public class Tuple2<S, T> {
    public final S first;
    public final T second;

    public Tuple2(S first, T second) {
        this.first = first;
        this.second = second;
    }
}

package com.example.clock.storageutils;

public class Tuple3<S, T, U> {
    public final S first;
    public final T second;
    public final U third;

    public Tuple3(S first, T second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}

package com.example.clock.storageutils;

public class Tuple4<S, T, U, V> {
    public final S first;
    public final T second;
    public final U third;
    public final V fourth;

    public Tuple4(S first, T second, U third, V fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }
}

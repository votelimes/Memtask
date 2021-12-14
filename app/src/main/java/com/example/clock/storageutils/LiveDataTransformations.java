package com.example.clock.storageutils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;

import java.util.List;

public class LiveDataTransformations {
    private LiveDataTransformations() {}

    public static <S, T> LiveData<Tuple2<S,T>> ifNotNull(LiveData<S> first, LiveData<T> second) {
        MediatorLiveData<Tuple2<S, T>> mediator = new MediatorLiveData<>();

        mediator.addSource(first, (_first) -> {
            T _second = second.getValue();
            if(_first != null && _second != null) {
                mediator.setValue(new Tuple2(_first, _second));
            }
        });

        mediator.addSource(second, (_second) -> {
            S _first = first.getValue();
            if(_first != null && _second != null) {
                mediator.setValue(new Tuple2(_first, _second));
            }
        });

        return mediator;
    }

    public static <S, T, U> LiveData<Tuple3<S,T,U>> ifNotNull(LiveData<S> first, LiveData<T> second, LiveData<U> third) {
        MediatorLiveData<Tuple3<S, T, U>> mediator = new MediatorLiveData<>();

        mediator.addSource(first, (_first) -> {
            T _second = second.getValue();
            U _third = third.getValue();
            if(_first != null && _second != null && _third != null) {
                mediator.setValue(new Tuple3(_first, _second, _third));
            }
        });

        mediator.addSource(second, (_second) -> {
            S _first = first.getValue();
            U _third = third.getValue();
            if(_first != null && _second != null && _third != null) {
                mediator.setValue(new Tuple3(_first, _second, _third));
            }
        });

        mediator.addSource(third, (_third) -> {
            S _first = first.getValue();
            T _second = second.getValue();
            if(_first != null && _second != null && _third != null) {
                mediator.setValue(new Tuple3(_first, _second, _third));
            }
        });

        return mediator;
    }

    public static <S, T, U, V> LiveData<Tuple4<S,T,U, V>> ifNotNull(LiveData<S> first, LiveData<T> second, LiveData<U> third, LiveData<V> fourth) {
        MediatorLiveData<Tuple4<S, T, U, V>> mediator = new MediatorLiveData<>();

        mediator.addSource(first, (_first) -> {
            T _second = second.getValue();
            U _third = third.getValue();
            V _fourth = fourth.getValue();
            if(_first != null && _second != null && _third != null && _fourth != null) {
                mediator.setValue(new Tuple4(_first, _second, _third, _fourth));
            }
        });

        mediator.addSource(second, (_second) -> {
            S _first = first.getValue();
            U _third = third.getValue();
            V _fourth = fourth.getValue();
            if(_first != null && _second != null && _third != null && _fourth != null) {
                mediator.setValue(new Tuple4(_first, _second, _third, _fourth));
            }
        });

        mediator.addSource(third, (_third) -> {
            S _first = first.getValue();
            T _second = second.getValue();
            V _fourth = fourth.getValue();
            if(_first != null && _second != null && _third != null && _fourth != null) {
                mediator.setValue(new Tuple4(_first, _second, _third, _fourth));
            }
        });

        mediator.addSource(fourth, (_fourth) -> {
            S _first = first.getValue();
            T _second = second.getValue();
            U _third = third.getValue();
            if(_first != null && _second != null && _third != null && _fourth != null) {
                mediator.setValue(new Tuple4(_first, _second, _third, _fourth));
            }
        });

        return mediator;
    }
}

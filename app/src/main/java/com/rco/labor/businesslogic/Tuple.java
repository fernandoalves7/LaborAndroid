package com.rco.labor.businesslogic;

/**
 * Created by Fernando on 8/27/2018.
 */

public class Tuple<K, V> {
    private final K element0;
    private final V element1;

    public static <K, V> Tuple<K, V> create(K element0, V element1) {
        return new Tuple<K, V>(element0, element1);
    }

    public Tuple(K element0, V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public K getElement0() {
        return element0;
    }

    public V getElement1() {
        return element1;
    }
}
package org.apache.oozie.dependency.hcat;

public class SettableInteger {
    private int value;

    public SettableInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void increment() {
        value++;
    }

    public void decrement() {
        value--;
    }
}

package me.yz.yzjson;

public class ImmutableTypeHolder<T> {
    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }
}

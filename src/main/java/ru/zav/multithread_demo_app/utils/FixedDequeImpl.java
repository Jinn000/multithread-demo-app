package ru.zav.multithread_demo_app.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;

/**Своя реализация Очереди. но с фиксированным объемом.
 * Ограничил только для методов add и push, потому что использую только их*/
@Getter
@RequiredArgsConstructor
public class FixedDequeImpl<T> extends ArrayDeque<T> implements FixedDeque<T> {
    private final int maxSize;

    @Override
    public boolean add(@NotNull T item) {
        if(this.size()>= this.maxSize){
            throw new IllegalStateException("Can`t add element. Collection is full");
        }
        return super.add(item);
    }
    @Override
    public void push(@NotNull T item) {
        if(this.size()>= this.maxSize){
            throw new IllegalStateException("Can`t push element. Collection is full");
        }
        super.push(item);
    }

}

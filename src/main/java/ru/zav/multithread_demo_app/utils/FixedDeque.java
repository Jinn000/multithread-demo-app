package ru.zav.multithread_demo_app.utils;

import java.util.Deque;

/**Потребовалась своя реализация ограниченной очереди
 * Чтобы уперевшись в переполнение, приостанавливать потребителя или производителя*/
public interface FixedDeque<T> extends Deque<T> {
    int getMaxSize();
}

package ru.zav.multithread_demo_app.procon.testing;

public interface ProducerConsumerTestingService {
    Integer consumerCount = 5;
    Integer commandsQty = 500;

    void invoke();
    String invokeWithReport();
}

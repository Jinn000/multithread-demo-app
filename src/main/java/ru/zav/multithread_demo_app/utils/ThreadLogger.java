package ru.zav.multithread_demo_app.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**Сервис с методом логирования состояния потоков.
 * Работает в спринговом шедуллере (Необходимо проставлять разрешение на работу шедуллера)*/
@Component
public class ThreadLogger {

    @Scheduled(fixedRate = 2000)
    public void logThreadStates() {
        Thread.getAllStackTraces().forEach((thread, stackTrace) -> {
            System.out.println("Thread: " + thread.getName() + " State: " + thread.getState());
        });
    }
}
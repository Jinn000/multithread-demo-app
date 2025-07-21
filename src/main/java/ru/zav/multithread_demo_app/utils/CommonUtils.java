package ru.zav.multithread_demo_app.utils;

import java.time.LocalDateTime;

public class CommonUtils {

    @SuppressWarnings("SameParameterValue")
    public static void hardWorkEmulate(long timeoutMills){
        LocalDateTime endTime = LocalDateTime.now().plusNanos(timeoutMills * 1000_000);
        while(endTime.isAfter(LocalDateTime.now())){
            final double sin = Math.sin(Math.random() * 100);
            if(sin < 0) {continue;}
        }
    }
}

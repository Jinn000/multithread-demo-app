package ru.zav.multithread_demo_app.bank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
@Service(value = "bankThreadPoolTestServiceImpl")
public class BankThreadPoolTestServiceImpl extends BankTestServiceBase {
    @Getter
    private final String implementationName = "BankThreadPoolTestServiceImpl";

    @Override
    protected void doExecution(Bank bank) {
        List<Future<?>> futureList = new ArrayList<>();

        final ExecutorService executorService = Executors.newFixedThreadPool(25);

        for (int cnt = 0; cnt<ITERATIONS_QUANTITY; cnt++){
            var future = executorService.submit(super.getOperationRunnable(bank));
            futureList.add(future);
        }

        futureList.forEach(f-> {
            try {
                f.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("The main thread couldn't wait for the spawned ones\n");
            }
        });

        executorService.shutdown();
        try {
            if(executorService.awaitTermination(2, TimeUnit.MINUTES)){
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

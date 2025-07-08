package ru.zav.multithread_demo_app.testing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zav.multithread_demo_app.bank.Bank;
import ru.zav.multithread_demo_app.bank.factory.BankFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service(value = "bankMultiThreadTestServiceImpl")
public class BankMultiThreadTestServiceImpl extends BankTestServiceBase {
    @Getter
    private final String implementationName = "BankMultiThreadTestServiceImpl";

    @Override
    protected void doExecution(Bank bank) {
        List<Thread> threads = new ArrayList<>();

        for (int cnt = 0; cnt< ITERATIONS_QUANTITY; cnt++){
            final Runnable accountsOperation = super.getOperationRunnable(bank);
            final Thread iterationThread = new Thread(accountsOperation);
            threads.add(iterationThread);
            iterationThread.start();
        }

        threads.forEach(t-> {
            try {
                t.join(10_000);
            } catch (InterruptedException e) {
                log.error("The main thread couldn't wait for the spawned ones\n");
            }
        });
    }
}

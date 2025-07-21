package ru.zav.multithread_demo_app.bank.testing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zav.multithread_demo_app.bank.Bank;

@Slf4j
@RequiredArgsConstructor
@Service(value = "bankSingleThreadTestServiceImpl")
public class BankSingleThreadTestServiceImpl extends BankTestServiceBase {
    @Getter
    private final String implementationName = "BankSingleThreadTestServiceImpl";

    @Override
    protected void doExecution(Bank bank) {

        for (int cnt = 0; cnt< ITERATIONS_QUANTITY; cnt++){
            super.getOperationRunnable(bank).run();
        }
    }
}

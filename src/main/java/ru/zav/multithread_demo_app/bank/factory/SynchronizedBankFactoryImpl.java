package ru.zav.multithread_demo_app.bank.factory;

import ru.zav.multithread_demo_app.bank.Bank;
import ru.zav.multithread_demo_app.bank.BankSynchronizedImpl;
import ru.zav.multithread_demo_app.bank.account.AccountSynchronizedImpl;

import static ru.zav.multithread_demo_app.testing.BankTestingService.ACCOUNT_QUANTITY;
import static ru.zav.multithread_demo_app.testing.BankTestingService.DEFAULT_START_VALUE;

public class SynchronizedBankFactoryImpl extends BankFactory{
    @Override
    public Bank createBank() {
        final Bank bank = new BankSynchronizedImpl();

        // проинициализировать банк одинаковыми счетами
        for(int count = 0; count< ACCOUNT_QUANTITY; count++){
            bank.addAccount(new AccountSynchronizedImpl("ACC_%d".formatted(count), DEFAULT_START_VALUE));
        }
        return bank;
    }
}

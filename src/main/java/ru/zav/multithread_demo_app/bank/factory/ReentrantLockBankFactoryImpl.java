package ru.zav.multithread_demo_app.bank.factory;

import ru.zav.multithread_demo_app.bank.Bank;
import ru.zav.multithread_demo_app.bank.BankLockImpl;
import ru.zav.multithread_demo_app.bank.account.AccountLockImpl;

import static ru.zav.multithread_demo_app.bank.testing.BankTestingService.ACCOUNT_QUANTITY;
import static ru.zav.multithread_demo_app.bank.testing.BankTestingService.DEFAULT_START_VALUE;

public class ReentrantLockBankFactoryImpl extends BankFactory{
    @Override
    public Bank createBank() {
        final Bank bank = new BankLockImpl();

        // проинициализировать банк одинаковыми счетами
        for(int count = 0; count< ACCOUNT_QUANTITY; count++){
            bank.addAccount(new AccountLockImpl("ACC_%d".formatted(count), DEFAULT_START_VALUE));
        }
        return bank;
    }
}

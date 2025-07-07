package ru.zav.multithread_demo_app.bank;

import ru.zav.multithread_demo_app.bank.account.Account;

import java.math.BigInteger;

public interface Bank {
    void transferFromTo(Account source, Account target, BigInteger value);
    BigInteger getTotal();
    String getAccountsState();
    void addAccount(Account account);
    Account getRandomAccount();
}

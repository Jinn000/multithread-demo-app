package ru.zav.multithread_demo_app.bank.account;

import java.math.BigInteger;

public interface Account {
    // зачисление на счет
    boolean creditTo(BigInteger volume);
    // списание со счета
    boolean debitFrom(BigInteger volume);
    // получить текущий баланс
    BigInteger getBalance();
    String getAccountNumber();
}

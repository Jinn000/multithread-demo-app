package ru.zav.multithread_demo_app.bank.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@AllArgsConstructor
public class AccountLockImpl implements Account{
    @Getter
    private final String accountNumber;

    // Хранит состояние счета. Должен быть volatile
    private volatile BigInteger balance;

    private final Lock balanceLock = new ReentrantLock();


    // зачисление на счет
    @Override
    public boolean creditTo(@NotNull BigInteger volume) {
        if(volume.compareTo(BigInteger.ZERO)<0){
            log.warn("Поступление отрицательной суммы некорректно");
            return false;
        }

        try {
            balanceLock.lock();
            this.balance = this.balance.add(volume);
            return true;
        } catch (Exception e) {
            throw new IllegalStateException("Пополнение счета не удалось. Причина: %s".formatted(e.getLocalizedMessage()), e);
        } finally {
            balanceLock.unlock();
        }
    }

    // списание со счета
    @Override
    public boolean debitFrom(@NotNull BigInteger volume) {
        if(volume.compareTo(BigInteger.ZERO)<0){
            log.warn("Перевод отрицательной суммы некорректен");
            return false;
        }

        try{
            balanceLock.lock();
            if(getBalance().subtract(volume).compareTo(BigInteger.ZERO) >=0){
                this.balance = balance.subtract(volume);
                return true;
            }else throw new IllegalStateException("Средств на счете %s недостаточно".formatted(getAccountNumber()));

        }finally {
            balanceLock.unlock();
        }
    }

    /*Синхронизация не нужна, т.к. поле volatile*/
    @NotNull
    @Override
    public BigInteger getBalance() {
        return balance;
    }
}

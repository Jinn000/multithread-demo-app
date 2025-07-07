package ru.zav.multithread_demo_app.bank.account;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@AllArgsConstructor
public class AccountSynchronizedImpl implements Account{
    @Getter
    private final String accountNumber;

    // Хранит состояние счета. Должен быть volatile
    private volatile BigInteger balance;


    // зачисление на счет
    @Override
    public synchronized boolean creditTo(@NotNull BigInteger volume) {
        if(volume.compareTo(BigInteger.ZERO)<0){
            log.warn("Поступление отрицательной суммы некорректно");
            return false;
        }

        try {
            this.balance = this.balance.add(volume);
            return true;
        } catch (Exception e) {
            throw new IllegalStateException("Пополнение счета не удалось. Причина: %s".formatted(e.getLocalizedMessage()), e);
        }
    }

    // списание со счета
    @Override
    public synchronized boolean debitFrom(@NotNull BigInteger volume) {
        if(volume.compareTo(BigInteger.ZERO)<0){
            log.warn("Перевод отрицательной суммы некорректен");
            return false;
        }

        if(getBalance().subtract(volume).compareTo(BigInteger.ZERO) >=0){
            this.balance = balance.subtract(volume);
            return true;
        }else throw new IllegalStateException("Средств на счете %s недостаточно".formatted(getAccountNumber()));
    }

    @NotNull
    @Override
    public synchronized BigInteger getBalance() {
        return balance;
    }
}

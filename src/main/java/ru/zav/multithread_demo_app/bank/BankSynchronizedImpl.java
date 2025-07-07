package ru.zav.multithread_demo_app.bank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.zav.multithread_demo_app.bank.account.Account;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/*
* Примитивная реализация. С игнорированием перевода, при
* невозможности его осуществить*/
/*
* Блокировка synchronized на этом уровне не требуется, т.к.
* реализована на уровне Account
* */
@Slf4j
@RequiredArgsConstructor
@Service
public class BankSynchronizedImpl implements Bank{
    private final List<Account> accounts = new ArrayList<>();

    @Override
    public /*synchronized*/ void transferFromTo(@NotNull Account source,
                               @NotNull Account target,
                               @NotNull BigInteger value) {
        if(Objects.equals(source, target)){
            return;
        }

        boolean debitOperationSuccess = false;
        boolean creditOperationSuccess = false;
        try {
            debitOperationSuccess = source.debitFrom(value);
            if(debitOperationSuccess){
                creditOperationSuccess = target.creditTo(value);
            }
        }catch (IllegalStateException exception){
            log.debug(exception.getLocalizedMessage());
        }finally {
            if(debitOperationSuccess && !creditOperationSuccess){
                source.creditTo(value);
                log.warn("Debit revert on {}", source.getAccountNumber());
            }
        }
    }

    /* synchronized - необходимо, т.к. в процессе подсчета, другими потоками
     * могут быть внесены изменения в состояние счетов
     */
    @NotNull
    @Override
    public synchronized BigInteger getTotal() {
        return this.accounts.stream()
                .map(Account::getBalance)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    /* synchronized - необходимо, т.к. в процессе формирования отчета,
     * другими потоками могут быть внесены изменения в состояние счетов
     */
    @Override
    public synchronized String getAccountsState() {
        final String content = this.accounts.stream()
                .map(acc -> String.format("%s: %d", acc.getAccountNumber(), acc.getBalance()))
                .collect(Collectors.joining("\n"));
        return "\n" + content;
    }


    @Override
    public void addAccount(@NotNull Account account) {
        this.accounts.add(account);
    }

    @Nullable
    @Override
    public Account getRandomAccount() {
        if(!accounts.isEmpty()){
            final int randomAccountIndex = new Random().nextInt(this.accounts.size());
            return accounts.get(randomAccountIndex);
        }
        return null;
    }
}

package ru.zav.multithread_demo_app.testing;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.zav.multithread_demo_app.bank.Bank;
import ru.zav.multithread_demo_app.bank.account.Account;
import ru.zav.multithread_demo_app.bank.factory.BankFactory;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Random;

@Slf4j
public abstract class BankTestServiceBase implements BankTestingService {
    protected abstract String getImplementationName();

    @Override
    public boolean startTest(@NotNull BankFactory bankFactory) {
        var startTimestamp = LocalDateTime.now();
        var bank = bankFactory.createBank();

        doExecution(bank);

        final LocalDateTime endTime = LocalDateTime.now();
        final Duration duration = Duration.between(startTimestamp, endTime);
        log.info("{}. At the end of testing, TOTAL is {}. Elapsed time is {}s {}ms",
                getImplementationName(),
                bank.getTotal(),
                duration.get(ChronoUnit.SECONDS),
                duration.get(ChronoUnit.NANOS)/1000_000);
        log.info("{}. {}", getImplementationName(), bank.getAccountsState());
        return (DEFAULT_START_VALUE.multiply(BigInteger.valueOf(ACCOUNT_QUANTITY)).equals(bank.getTotal()));
    }

    protected abstract void doExecution(Bank bank);

    @NotNull
    protected Runnable getOperationRunnable(@NotNull Bank bank) {
        return ()-> {
            final Account sourceAcc = bank.getRandomAccount();
            final Account targetAcc = bank.getRandomAccount();

            if(Objects.isNull(sourceAcc) || Objects.isNull(targetAcc)){
                throw new IllegalStateException("Не удалось получить рандомный счет");
            }

            bank.transferFromTo(
                    sourceAcc,
                    targetAcc,
                    DEFAULT_TRANSFER_BASE_VALUE.multiply(BigInteger.valueOf(new Random().nextInt(10)))
            );

            try {
                hardWorkEmulate(1000);
                Thread.sleep(10 * new Random().nextInt(50));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            final BigInteger total = bank.getTotal();
            if(total.compareTo(DEFAULT_START_VALUE.multiply(BigInteger.valueOf(ACCOUNT_QUANTITY))) != 0){
                log.error("{}. Total not consistent: {}", getImplementationName(), total);
            }else {
                log.info("{}. Total - Ok", getImplementationName());
            }
        };
    }

    @SuppressWarnings("SameParameterValue")
    private static void hardWorkEmulate(long timeoutMills){
        LocalDateTime endTime = LocalDateTime.now().plusNanos(timeoutMills * 1000_000);
        while(endTime.isAfter(LocalDateTime.now())){
            final double sin = Math.sin(Math.random() * 100);
            if(sin < 0) {continue;}
        }
    }
}

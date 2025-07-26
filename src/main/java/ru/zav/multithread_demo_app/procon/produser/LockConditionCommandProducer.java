package ru.zav.multithread_demo_app.procon.produser;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.zav.multithread_demo_app.procon.command.*;
import ru.zav.multithread_demo_app.procon.testing.OperationReportObject;
import ru.zav.multithread_demo_app.utils.FixedDeque;

import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
public class LockConditionCommandProducer implements Runnable{
    private final Integer commandsQty;
    private final FixedDeque<Command> commands;
    private final Lock lock;
    private final Condition commandNotEmptyCondition;
    private final Condition commandNotFullCondition;

    /*Для контроля целостности*/
    private final OperationReportObject reportObject;

    public LockConditionCommandProducer(Integer commandsQty,
                                        FixedDeque<Command> commands,
                                        OperationReportObject reportObject,
                                        Lock lock,
                                        Condition commandNotEmptyCondition,
                                        Condition commandNotFullCondition) {
        this.commandsQty = commandsQty;
        this.commands = commands;
        this.reportObject = reportObject;
        this.lock = lock;
        this.commandNotEmptyCondition = commandNotEmptyCondition;
        this.commandNotFullCondition = commandNotFullCondition;
    }

    @Override
    public void run() {
        log.info("Старт создания команд продюсером");
        for (int id = 1; id <= commandsQty; id++) {
            try {
                lock.lock();

                awaitIfFull();
                var emailSendCommand = new EmailSendCommandImpl(id, "test%d@zav.com".formatted(id), reportObject.getSentEmailIds());
                reportObject.getProducesEmailIds().put(id);
                commands.add(emailSendCommand);
                commandNotEmptyCondition.signalAll();

                awaitIfFull();
                var telegramMessageSendCommand = new TelegramMessageSendCommandImpl(id, "telegramUser%d".formatted(id), reportObject.getSentTelegramMessagesIds());
                reportObject.getProducesTelegramMessagesIds().put(id);
                commands.add(telegramMessageSendCommand);
                commandNotEmptyCondition.signalAll();

                awaitIfFull();
                reportObject.getProducesSmsIds().put(id);
                var smsSendCommand = new SmsSendCommandImpl(id, generatePhoneNumber(id), reportObject.getSentSmsIds());
                commands.add(smsSendCommand);
                commandNotEmptyCondition.signalAll();

                log.info("Созданы Id {}", id);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
                lock.unlock();
            }
        }

        try {
            lock.lock();
            /*Маркерная "последняя" команда*/
            awaitIfFull();
            commands.add(new PoisonPillCommandImpl());

            commandNotEmptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void awaitIfFull() {
        while (commands.size() >= commands.getMaxSize()){
            try {
                commandNotFullCondition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NotNull
    private static String generatePhoneNumber(int i) {
        return "+79062661" + String.format("%03d", i);
    }
}

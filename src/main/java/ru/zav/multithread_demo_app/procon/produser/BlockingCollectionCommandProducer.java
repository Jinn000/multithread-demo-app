package ru.zav.multithread_demo_app.procon.produser;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.zav.multithread_demo_app.procon.command.*;
import ru.zav.multithread_demo_app.procon.testing.OperationReportObject;

import java.util.concurrent.BlockingQueue;

@Slf4j
public class BlockingCollectionCommandProducer implements Runnable{
    private final Integer commandsQty;
    private final BlockingQueue<Command> commands;

    /*Для контроля целостности*/
    private final OperationReportObject reportObject;

    public BlockingCollectionCommandProducer(Integer commandsQty,
                                             BlockingQueue<Command> commands,
                                             OperationReportObject reportObject) {
        this.commandsQty = commandsQty;
        this.commands = commands;
        this.reportObject = reportObject;
    }

    @Override
    public void run() {
        log.info("Старт создания команд продюсером");
        for (int id = 1; id <= commandsQty; id++) {
            try {
                var emailSendCommand = new EmailSendCommandImpl(id, "test%d@zav.com".formatted(id), reportObject.getSentEmailIds());
                reportObject.getProducesEmailIds().put(id);
                commands.put(emailSendCommand);
                var telegramMessageSendCommand = new TelegramMessageSendCommandImpl(id, "telegramUser%d".formatted(id), reportObject.getSentTelegramMessagesIds());
                reportObject.getProducesTelegramMessagesIds().put(id);
                commands.put(telegramMessageSendCommand);
                reportObject.getProducesSmsIds().put(id);
                var smsSendCommand = new SmsSendCommandImpl(id, generatePhoneNumber(id), reportObject.getSentSmsIds());
                commands.put(smsSendCommand);

                log.info("Созданы Id {}", id);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /*Маркерная "последняя" команда*/
        try {
            /*!! поначалу, тут использовал с непривычки метод add. И получал иногда IllegalStateException.
            Из-за того что коллекция была заполнена.
            Потому и применяется в этой реализации производителя-потребителя, метод put. Он как раз сам
            отслеживает переполнение. И блокирует запись. И оповещает ожидающий поток при освобождении места*/
            commands.put(new PoisonPillCommandImpl());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @NotNull
    private static String generatePhoneNumber(int i) {
        return "+79062661" + String.format("%03d", i);
    }
}

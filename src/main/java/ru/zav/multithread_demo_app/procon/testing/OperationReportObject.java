package ru.zav.multithread_demo_app.procon.testing;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;


/**Контрольный обьект, инкапсулирующий контроль целостности: создано vs отправлено*/
@Data
public class OperationReportObject{
    public static final String COUNT_ERROR_F = "Количество созданных и обработанных заданий на отправку %s не сошлось. %d против %d";
    public static final String CONTENT_ERROR_F = "Не все созданные %s были обработаны";

    /*Коллекции с созданными. Нужны для контроля целостности*/
    private final BlockingQueue<Integer> producesEmailIds = new LinkedBlockingQueue<>();
    private final BlockingQueue<Integer> producesTelegramMessagesIds = new LinkedBlockingQueue<>();
    private final BlockingQueue<Integer> producesSmsIds = new LinkedBlockingQueue<>();

    /*Коллекции с отправленными. Нужны для контроля целостности*/
    private final BlockingQueue<Integer> sentEmailIds = new LinkedBlockingQueue<>();
    private final BlockingQueue<Integer> sentTelegramMessagesIds = new LinkedBlockingQueue<>();
    private final BlockingQueue<Integer> sentSmsIds = new LinkedBlockingQueue<>();

    @NotNull
    public String getProducerReport(){
        return String.format("Было создано: %d email, %d telegramMessages, %d Sms", producesEmailIds.size(), producesTelegramMessagesIds.size(), producesSmsIds.size());
    }

    /** Вернет строку с ошибкой. Либо пустую строку, если ошибок нет*/
    @NotNull
    public String getErrorReport(){
        var errors = new ArrayList<>(getErrors(producesEmailIds, sentEmailIds, "Email"));
        errors.addAll(getErrors(producesTelegramMessagesIds, sentTelegramMessagesIds, "TelegramMessages"));
        errors.addAll(getErrors(producesSmsIds, sentSmsIds, "Sms"));

        return errors.stream()
                .filter(StringUtils::isNoneBlank)
                .collect(Collectors.joining("\n"));
    }

    @NotNull
    private static ArrayList<String> getErrors(@NotNull BlockingQueue<Integer> producedIds, @NotNull BlockingQueue<Integer> sentIds, @NotNull String sourceName) {
        var errors = new ArrayList<String>();
        if(producedIds.size() != sentIds.size()){
            errors.add(COUNT_ERROR_F.formatted(sourceName, producedIds.size(), sentIds.size()));
        } else if(!sentIds.containsAll(producedIds)){
            errors.add(CONTENT_ERROR_F.formatted(sourceName));
        }
        return errors;
    }
}
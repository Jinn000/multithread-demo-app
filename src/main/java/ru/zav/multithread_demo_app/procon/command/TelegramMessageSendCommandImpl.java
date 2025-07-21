package ru.zav.multithread_demo_app.procon.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import static ru.zav.multithread_demo_app.utils.CommonUtils.hardWorkEmulate;

@AllArgsConstructor
@Data
@Slf4j
public class TelegramMessageSendCommandImpl implements Command{
    private final Integer id;
    private final String userId;
    private final Collection<Integer> sentTelegramMessagesIds;

    @Override
    public void execute() {
        hardWorkEmulate(10);
        sentTelegramMessagesIds.add(id);
        log.info("Telegram-сообщение с id {} отправлено пользователю {}.", id, userId);
    }
}

package ru.zav.multithread_demo_app.procon.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import static ru.zav.multithread_demo_app.utils.CommonUtils.hardWorkEmulate;

@AllArgsConstructor
@Data
@Slf4j
public class SmsSendCommandImpl implements Command{
    private final Integer id;
    private final String phoneNumber;
    private final Collection<Integer> sentSmsIds;

    @Override
    public void execute() {
        hardWorkEmulate(10);
        sentSmsIds.add(id);
        log.info("SMS с id {} отправлена на номер {}.", id, phoneNumber);
    }
}

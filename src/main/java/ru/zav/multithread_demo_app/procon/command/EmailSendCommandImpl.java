package ru.zav.multithread_demo_app.procon.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import static ru.zav.multithread_demo_app.utils.CommonUtils.hardWorkEmulate;

@AllArgsConstructor
@Data
@Slf4j
public class EmailSendCommandImpl implements Command{
    private final Integer id;
    private final String email;
    private final Collection<Integer> sentEmailIds;

    @Override
    public void execute() {
        hardWorkEmulate(10);
        sentEmailIds.add(id);
        log.info("Email с id {} отправлен на адрес {}.", id, email);
    }
}

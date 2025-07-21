package ru.zav.multithread_demo_app.procon.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Data
@Slf4j
public class PoisonPillCommandImpl implements Command{
    @Override
    public void execute() {
        log.info("Маркерная команда. Типа последняя");
    }
}

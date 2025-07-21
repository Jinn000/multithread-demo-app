package ru.zav.multithread_demo_app.procon.command;

import lombok.Data;

@FunctionalInterface
public interface Command {
    void execute();
}

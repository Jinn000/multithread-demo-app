package ru.zav.multithread_demo_app.procon.consumer;

import lombok.RequiredArgsConstructor;
import ru.zav.multithread_demo_app.procon.command.Command;
import ru.zav.multithread_demo_app.procon.command.PoisonPillCommandImpl;

import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public class BlockingCollectionCommandConsumer implements Runnable{
    private final BlockingQueue<Command> commands;

    @Override
    public void run() {
        try {
            while (true){
                var command = commands.take();
                command.execute();

                /*Проверка на финальную команду.
                * Увидели финальную - возвращаем ее обратно в очередь, и выходим из цикла*/
                if(command instanceof PoisonPillCommandImpl){
                    commands.put(command);
                    break;
                }
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}

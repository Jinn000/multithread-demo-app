package ru.zav.multithread_demo_app.procon.consumer;

import lombok.RequiredArgsConstructor;
import ru.zav.multithread_demo_app.procon.command.Command;
import ru.zav.multithread_demo_app.procon.command.PoisonPillCommandImpl;
import ru.zav.multithread_demo_app.utils.FixedDeque;

import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/** В реализации на основе непотокобезопасных коллекций,
 * требуется контроллировать обращения к общему состоянию
 * через обьект блокировки.
 * А также, необходим контроль переполнения и опустошения, с помощью
 * условий. Чтобы припарковывать и пробуждать потоки*/
@RequiredArgsConstructor
public class LockConditionCommandConsumer implements Runnable{
    private final FixedDeque<Command> commands;
    private final Lock lock;
    private final Condition commandNotEmptyCondition;
    private final Condition commandNotFullCondition;

    @Override
    public void run() {
        while (true){
            Command command = null;
            lock.lock();
            try{
                /*проверять в цикле оказалось важно.
                * Ведь при пробуждении, пробуждаются все потоки. И в каком-то другом, успевшем
                * получить блокировку, уже снова могло измениться состояние общего ресурса.
                * Поэтому снова надо проверять*/
                while(commands.isEmpty()){
                    try {
                        commandNotEmptyCondition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                command = commands.pop();
                commandNotFullCondition.signal();
                /*Проверка на финальную команду.
                 * Увидели финальную - возвращаем ее обратно в очередь (в то же место), и выходим из цикла*/
                if(command instanceof PoisonPillCommandImpl){
                    /*Проверять количество и опасаться переполнения не нужно. Это последняя, финишная, команда*/
                    commands.push(command);
                    command.execute();

                    break;
                }
            }finally {
                lock.unlock();
            }

            if(command !=null){
                command.execute();
            }
        }
    }
}

package ru.zav.multithread_demo_app.procon.testing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.zav.multithread_demo_app.procon.command.Command;
import ru.zav.multithread_demo_app.procon.consumer.LockConditionCommandConsumer;
import ru.zav.multithread_demo_app.procon.produser.LockConditionCommandProducer;
import ru.zav.multithread_demo_app.utils.FixedDeque;
import ru.zav.multithread_demo_app.utils.FixedDequeImpl;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * В качестве общего обьекта - потоконебезопасная коллекция.
 * Работа в многопоточной среде будет обеспечиваться объектом
 * ReentrantLock и кондишенами*/
@Slf4j
@RequiredArgsConstructor
@Service
public class LockConditionCommandTestingService implements ProducerConsumerTestingService{
    /*!! Ключевой элемент. Тот самый общий ресурс.
    * Сюда добавляет производитель. Отсюда забирают потребители.
    * Не потокобезопасный. Поэтому требуется организация внешней блокировки*/
    private final FixedDeque<Command> commands = new FixedDequeImpl<>(100);
    private final Lock commandsLock = new ReentrantLock();
    Condition commandNotEmptyCondition = commandsLock.newCondition();
    Condition commandNotFullCondition = commandsLock.newCondition();

    /*Контрольный обьект. Нужен для контроля целостности.
    * Передается продюсеру. Там заполнятся коллекции хранящие id созданных команд. И там же, он попадет
    * в объекты Команд, для заполнения коллекций отправок*/
    private final OperationReportObject operationReportObject = new OperationReportObject();


    @Override
    public void invoke() {
        // наверное и не нужен
    }

    @Nullable
    @Override
    public String invokeWithReport() {
        List<Future<?>> consumerFutureList = new ArrayList<>();
        final ExecutorService consumersExecutorService = Executors.newFixedThreadPool(10);

        /*Создаю потребителей. И запускаю в пуле на фиксированное кол-во потоков*/
        for (int i = 0; i < consumerCount; i++) {
            final Future<?> future = consumersExecutorService.submit(new LockConditionCommandConsumer(
                    commands,
                    commandsLock,
                    commandNotEmptyCondition,
                    commandNotFullCondition));
            consumerFutureList.add(future);
        }

        /*Создаю производителя. И запускаю в одном новом потоке*/
        var producer = new LockConditionCommandProducer(
                commandsQty,
                commands,
                operationReportObject,
                commandsLock,
                commandNotEmptyCondition,
                commandNotFullCondition);
        var producerThread = new Thread(producer);
        producerThread.setName("producer_thread");
        try {
            producerThread.start();
            producerThread.join(); // основному потоку, необходимо дождаться завершения потока-производителя
        } catch (InterruptedException e) {
            log.info("Основной поток был прерван во время ожидания порожденного");
            Thread.currentThread().interrupt();
        } catch (IllegalThreadStateException e){
            log.error("Была попытка повторного запуска потока продюсера");
        }

        log.info("Продюсер закончил работу");

        /*Теперь жду завершения работы всех потребителей*/
        consumerFutureList.forEach(future -> {
            try {
                future.get();
            } catch (NoSuchElementException e) {
                log.error("Нет результата работы потребителя в дочернем потоке");
            } catch (InterruptedException e) {
                log.error("Главный поток был прерван в процессе ожидания результатов дочерних");
            } catch (ExecutionException | CancellationException e) {
                log.error("В процессе ожидания результатов дочернего потока, в нем было брошено исключение");
            }
        });

        /*обязательно завершить пул потоков. Освободить системные ресурсы*/
        consumersExecutorService.shutdown();
        try {
            if(!consumersExecutorService.awaitTermination(30, TimeUnit.SECONDS)){
                consumersExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            consumersExecutorService.shutdownNow();
        }

        log.info(operationReportObject.getProducerReport());

        /*getErrorReport вернет строку с ошибкой. Либо пустую строку, если ошибок нет*/
        return operationReportObject.getErrorReport();
    }
}

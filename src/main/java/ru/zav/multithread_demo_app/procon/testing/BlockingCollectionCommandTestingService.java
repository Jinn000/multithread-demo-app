package ru.zav.multithread_demo_app.procon.testing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.zav.multithread_demo_app.procon.command.Command;
import ru.zav.multithread_demo_app.procon.consumer.BlockingCollectionCommandConsumer;
import ru.zav.multithread_demo_app.procon.produser.BlockingCollectionCommandProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlockingCollectionCommandTestingService implements ProducerConsumerTestingService{
    /*!! Ключевой элемент. Тот самый общий ресурс.
    * Сюда добавляет производитель. Отсюда забирают потребители*/
    private final BlockingQueue<Command> commands = new ArrayBlockingQueue<>(100);

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
            final Future<?> future = consumersExecutorService.submit(new BlockingCollectionCommandConsumer(commands));
            consumerFutureList.add(future);
        }

        /*Создаю производителя. И запускаю в одном новом потоке*/
        var producer = new BlockingCollectionCommandProducer(
                commandsQty,
                commands,
                operationReportObject);
        var producerThread = new Thread(producer);
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
            } catch (InterruptedException e) {
                log.error("Главный поток был прерван в процессе ожидания результатов дочерних");
            } catch (ExecutionException | CancellationException e) {
                log.error("В процессе ожидания результатов дочерних, в них было брошено исключение");
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

package ru.zav.multithread_demo_app.procon.testing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.zav.multithread_demo_app.utils.ThreadLogger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Тестирование Производителя-потребителя. Блокирующие коллекции")
@EnableScheduling
@SpringBootTest
class BlockingCollectionCommandTestingServiceTest {
    private final ProducerConsumerTestingService testingService = new BlockingCollectionCommandTestingService();

    @Autowired
    private ThreadLogger threadLogger;


    @Test
    void invoke() {
    }

    @DisplayName("Запуск теста, с выводом отчета")
    @Test
    void invokeWithReport() {
        var report = testingService.invokeWithReport();
        assertTrue(report.isBlank(), String.format("Отчет об ошибке: %s",report));
    }
}
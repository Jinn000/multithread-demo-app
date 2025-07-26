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
@EnableScheduling
@DisplayName("Тестирование Производителя-потребителя. Неблокирующая коллекция. Объекты блокировки с Условиями")
@SpringBootTest
class LockConditionCommandTestingServiceTest {
    private final ProducerConsumerTestingService testingService = new LockConditionCommandTestingService();

    @Autowired
    private ThreadLogger threadLogger;

    @Test
    void invokeWithReport() {
        final String report = testingService.invokeWithReport();
        assertTrue(report.isBlank(), String.format("Отчет об ошибке: %s",report));
    }
}
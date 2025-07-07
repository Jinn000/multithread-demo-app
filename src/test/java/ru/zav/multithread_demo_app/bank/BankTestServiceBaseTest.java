package ru.zav.multithread_demo_app.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.zav.multithread_demo_app.testing.BankTestingService;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование модели банка")
@ExtendWith(SpringExtension.class)
@SpringBootTest
class BankTestServiceBaseTest {
    @Qualifier(value = "bankSingleThreadTestServiceImpl")
    @Autowired
    private BankTestingService singleThreadService;

    @Qualifier(value = "bankMultiThreadTestServiceImpl")
    @Autowired
    private BankTestingService multipleThreadService;

    @Qualifier(value = "bankThreadPoolTestServiceImpl")
    @Autowired
    private BankTestingService threadPoolService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("SingleThreadTest")
    void startSingleThreadTest() {
        var isSuccess = singleThreadService.startTest();
        assertTrue(isSuccess, "Тест завершился неудачей. Баланс не сошелся");
    }

    @Test
    @DisplayName("MultiThreadTest")
    void startMultiThreadTest() {
        var isSuccess = multipleThreadService.startTest();
        assertTrue(isSuccess, "Тест завершился неудачей. Баланс не сошелся");
    }

    @Test
    @DisplayName("ThreadPoolTest")
    void startThreadPoolTest() {
        var isSuccess = threadPoolService.startTest();
        assertTrue(isSuccess, "Тест завершился неудачей. Баланс не сошелся");
    }
}
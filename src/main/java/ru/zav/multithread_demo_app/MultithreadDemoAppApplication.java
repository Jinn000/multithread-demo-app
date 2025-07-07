package ru.zav.multithread_demo_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.zav.multithread_demo_app.bank.BankMultiThreadTestServiceImpl;
import ru.zav.multithread_demo_app.bank.BankSingleThreadTestServiceImpl;
import ru.zav.multithread_demo_app.bank.BankThreadPoolTestServiceImpl;
import ru.zav.multithread_demo_app.testing.BankTestingService;

@ConfigurationPropertiesScan
@SpringBootApplication
public class MultithreadDemoAppApplication {

	public static void main(String[] args) {
		final ConfigurableApplicationContext ctx = SpringApplication.run(MultithreadDemoAppApplication.class, args);

/*		BankTestingService singleThreadService = ctx.getBean(BankSingleThreadTestServiceImpl.class);
		singleThreadService.startTest();*/

/*		BankTestingService multipleThreadService = ctx.getBean(BankMultiThreadTestServiceImpl.class);
		multipleThreadService.startTest();

		BankTestingService threadPoolService = ctx.getBean(BankThreadPoolTestServiceImpl.class);
		threadPoolService.startTest();*/

		ctx.close();
	}

}

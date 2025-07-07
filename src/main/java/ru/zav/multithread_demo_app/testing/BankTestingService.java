package ru.zav.multithread_demo_app.testing;

import java.math.BigInteger;

public interface BankTestingService {
    BigInteger DEFAULT_START_VALUE = BigInteger.valueOf(500);
    int ACCOUNT_QUANTITY = 5;
    int ITERATIONS_QUANTITY = 100;
    BigInteger DEFAULT_TRANSFER_BASE_VALUE = BigInteger.valueOf(35);

    boolean startTest();
}

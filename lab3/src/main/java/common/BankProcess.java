package common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 1 on 01.05.2017.
 */
public class BankProcess extends Thread implements Serializable {
    private Map<Long, BigDecimal> bankAccounts = new ConcurrentHashMap<>();

    public Map<Long, BigDecimal> getBankAccounts() {
        return bankAccounts;
    }

    @Override
    public void run() {
        try {
            System.out.println("Банк. Счета: " + bankAccounts.toString());
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

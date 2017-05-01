package client;

import common.BankProcess;
import common.ProcessInfo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import server.RMIService;

import java.math.BigDecimal;

/**
 * Created by 1 on 01.05.2017.
 */
public class LoadBalancer {

    public static void main(String[] args) throws InterruptedException {

        //RMI Client Application Context is started...
        ApplicationContext context = new ClassPathXmlApplicationContext("rmiLBAppContext.xml");

        RMIService rmiService1 = (RMIService) context.getBean("RMIService1");
        RMIService rmiService2 = (RMIService) context.getBean("RMIService2");

        BankProcess bankProcess = new BankProcess();
        bankProcess.getBankAccounts().put(1L, new BigDecimal(20000));
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setLoad(20);
        processInfo.setProcess(bankProcess);

        rmiService1.acceptProcess(processInfo);
        Thread.sleep(10000);
        ProcessInfo minProcessInfo = rmiService1.getMinProcess();
        rmiService1.removeProcess(minProcessInfo.getUuid());
        Thread.sleep(5000);
        rmiService2.acceptProcess(minProcessInfo);
    }


}

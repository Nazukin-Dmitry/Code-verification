package cc.lab2;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Nazukin
 */
public class Lab2 {
    public static void main(String[] args) throws InterruptedException {
        Map<Long, BigDecimal> clientsMoney = new ConcurrentHashMap<>();
        BlockingDeque<Message> toS = new LinkedBlockingDeque<Message>(1024);
        BlockingDeque<Message> toB = new LinkedBlockingDeque<Message>(1024);
        BlockingDeque<Message> toC = new LinkedBlockingDeque<Message>(1024);

        Customer customer = new Customer(1L, toS, toB, toC);
        Shop shop = new Shop(toC, toB, toS);
        clientsMoney.put(1l, new BigDecimal(20000));
        Bank bank = new Bank(clientsMoney, toS, toC, toB);

        customer.start();
        shop.start();
        bank.start();

        customer.BuyOnTheShop(new BigDecimal(10000));
        Thread.sleep(30000);

        customer.WithdrawMoney(new BigDecimal(8000));
        Thread.sleep(10000);

        customer.WithdrawMoney(new BigDecimal(3000));
        Thread.sleep(120000);

        customer.interrupt();
        shop.interrupt();
        bank.interrupt();
    }

}

class Customer extends Thread {
    Long id;
    private BlockingQueue<Message> toShopQueue;
    private BlockingQueue<Message> toBankQueue;
    private BlockingQueue<Message> inQueue;

    public Customer(Long id, BlockingQueue<Message> toShopQueue, BlockingQueue<Message> toBankQueue, BlockingQueue<Message> inQueue) {
        this.toShopQueue = toShopQueue;
        this.toBankQueue = toBankQueue;
        this.inQueue = inQueue;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message executeMessage = inQueue.take();
                if (executeMessage != null) {
                    processMessage(executeMessage);
                    Thread.sleep(11000);
                }

                if (this.isInterrupted()) {
                    break;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void processMessage(Message executeMessage) throws Exception {
        throw new Exception();
    }

    public void BuyOnTheShop(BigDecimal cost) {
//        System.out.println("Покупатель. ");
        try {
            BuyMessage buyMessage = new BuyMessage(id, cost);
            System.out.println("Покупатель.\n" +
                    buyMessage.time + ". Отправить сообщение на покупку в магазин:" + cost);
            toShopQueue.put(buyMessage);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void WithdrawMoney(BigDecimal cost) {
        try {
            WithdrawMessage withdrawMessage = new WithdrawMessage(id, cost);
            System.out.println("Покупатель.\n" +
                    withdrawMessage.time + ". Отправить сообщение на снятие денег со счета: " + cost);
            toBankQueue.put(withdrawMessage);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private Message getMinMessage() throws InterruptedException {
//        Message fromShop = fromShopQueue.peek();
//        Message fromBank = fromBankQueue.peek();
//        if (fromBank != null && fromShop != null) {
//            if (fromShop.time.isBefore(fromBank.time)) {
//                return fromShopQueue.take();
//            } else {
//                return fromBankQueue.take();
//            }
//        } else {
//            return null;
//        }
//    }
}

class Shop extends Thread {

    private BlockingQueue<Message> toCustomerQueue;
    private BlockingQueue<Message> toBankQueue;
    private BlockingQueue<Message> inQueue;

    public Shop(BlockingQueue<Message> toCustomerQueue, BlockingQueue<Message> toBankQueue, BlockingQueue<Message> inQueue) {
        this.toCustomerQueue = toCustomerQueue;
        this.toBankQueue = toBankQueue;
        this.inQueue = inQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message executeMessage = inQueue.take();
                if (executeMessage != null) {
                    processMessage(executeMessage);
                    Thread.sleep(9950);
                }


                if (this.isInterrupted()) {
                    break;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void processMessage(Message executeMessage) throws Exception {
        if (executeMessage instanceof BuyMessage) {
            WithdrawMessage withdrawMessage = new WithdrawMessage(((BuyMessage) executeMessage).customerId, ((BuyMessage) executeMessage).money);
            System.out.println("Магазин.\n" +
                    executeMessage.time + ". Получено: покупатель " + ((BuyMessage) executeMessage).customerId + " покупка.\n" +
                    withdrawMessage.time + ". Отправлено: сообщение банку на списание денег: " + ((BuyMessage) executeMessage).money);
            Thread.sleep(40000);
            toBankQueue.put(withdrawMessage);
        } else {
            throw new Exception();
        }
    }

//    private Message getMinMessage() throws InterruptedException {
//        Message fromCustomer = fromCustomerQueue.peek();
//        Message fromBank = fromBankQueue.peek();
//        if (fromBank != null && fromCustomer != null) {
//            if (fromCustomer.time.isBefore(fromBank.time)) {
//                return fromCustomerQueue.take();
//            } else {
//                return fromBankQueue.take();
//            }
//        } else {
//            return null;
//        }
//    }
}

class Bank extends Thread {

    List<TimeBackUp> backUps = new ArrayList<>();

    Map<Long, BigDecimal> clientsMoney;

    private BlockingDeque<Message> toShopQueue;
    private BlockingDeque<Message> toCustomerQueue;
    private BlockingDeque<Message> inQueue;

    public Bank(Map<Long, BigDecimal> clientsMoney, BlockingDeque<Message> toShopQueue, BlockingDeque<Message> toCustomerQueue, BlockingDeque<Message> inQueue) {
        this.toShopQueue = toShopQueue;
        this.toCustomerQueue = toCustomerQueue;
        this.inQueue = inQueue;
        this.clientsMoney = clientsMoney;
        TimeBackUp timeBackUp = new TimeBackUp(LocalDateTime.now(), clientsMoney, null);
        backUps.add(timeBackUp);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message executeMessage = inQueue.take();
                if (executeMessage != null) {
                    if (executeMessage instanceof AntiMessage) {
                        inQueue.removeIf(message -> message.id.equals(executeMessage.id));
                        continue;
                    }
                    if (!backUps.isEmpty() && executeMessage.time.isBefore(backUps.get(backUps.size() - 1).time)) {
                        rollBackTo(executeMessage);
                    }
                    processMessage(executeMessage);
                    Thread.sleep(9050);
                }

                if (this.isInterrupted()) {
                    break;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void rollBackTo(Message message) {
        List<TimeBackUp> rolledBackUps = backUps.stream().filter(b -> b.time.isAfter(message.time)).collect(Collectors.toList());
        backUps.removeIf(backUp -> backUp.time.isAfter(message.time));
        clientsMoney = backUps.get(backUps.size() - 1).clientsMoneyBackUp;
        rolledBackUps.stream().sorted(Collections.reverseOrder()).forEach(timeBackUp -> {
            try {
                inQueue.putFirst(timeBackUp.processedMessage);
                if (!timeBackUp.sendedToShop.isEmpty()) {
                    for (Message sendedMessage : timeBackUp.sendedToShop) {
                        toShopQueue.putFirst(createAntiMessage(sendedMessage));
                    }
                }
                if (!timeBackUp.sendedToCustomer.isEmpty()) {
                    for (Message sendedMessage : timeBackUp.sendedToCustomer) {
                        toCustomerQueue.putFirst(createAntiMessage(sendedMessage));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Банк.\n" +
                "Восстановление к временной метке " + message.time + ". Восстановленный баланс: " + clientsMoney.get(1L));
    }

    private Message createAntiMessage(Message message) {
        AntiMessage antiMessage = new AntiMessage();
        antiMessage.id = message.id;
        return antiMessage;
    }

    private void processMessage(Message executeMessage) throws Exception {
        if (executeMessage instanceof WithdrawMessage) {
            Long id = ((WithdrawMessage) executeMessage).customerId;
            BigDecimal moneyMinus = ((WithdrawMessage) executeMessage).money;
            BigDecimal currentMoney = clientsMoney.get(id);
            if (currentMoney.subtract(moneyMinus).intValue() < 0) {
                System.out.println("Банк.\n" +
                        executeMessage.time + ". " +
                        "Получено: покупатель " + id + ".Списание суммы " + moneyMinus + " невозможно. Текущий баланс: " + currentMoney);
            } else {
                clientsMoney.put(id, currentMoney.subtract(moneyMinus));
                System.out.println("Банк.\n" +
                        executeMessage.time + ". " +
                        "Получено: покупатель " + id + ".Списание суммы " + moneyMinus + ". Текущий баланс: " + currentMoney.subtract(moneyMinus));
            }
        } else {
            throw new Exception();
        }

        TimeBackUp timeBackUp = new TimeBackUp(executeMessage.time, clientsMoney, executeMessage);
        backUps.add(timeBackUp);

    }

//    private Message getMinMessage() throws InterruptedException {
//        Message fromCustomer = fromCustomerQueue.peek();
//        Message fromShop = fromShopQueue.peek();
//        if (fromShop != null && fromCustomer != null) {
//            if (fromCustomer.time.isBefore(fromShop.time)) {
//                return fromCustomerQueue.take();
//            } else {
//                return fromShopQueue.take();
//            }
//        } else {
//            if (fromCustomer != null) {
//                return fromCustomerQueue.take();
//            } else if (fromShop != null) {
//                return fromShopQueue.take();
//            } else {
//                return null;
//            }
//        }
//    }
}

class TimeBackUp {
    LocalDateTime time;
    Map<Long, BigDecimal> clientsMoneyBackUp;
    Message processedMessage;
    List<Message> sendedToShop;
    List<Message> sendedToCustomer;

    public TimeBackUp(LocalDateTime time, Map<Long, BigDecimal> clientsMoneyBackUp, Message processedMessage) {
        this.time = time;
        this.clientsMoneyBackUp = new HashMap<>(clientsMoneyBackUp);
        this.processedMessage = processedMessage;
        sendedToCustomer = new ArrayList<>();
        sendedToShop = new ArrayList<>();
    }

    public List<Message> getSendedToShop() {
        return sendedToShop;
    }

    public List<Message> getSendedToCustomer() {
        return sendedToCustomer;
    }
}
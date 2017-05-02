package cc.lab1;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dmitrii Nazukin
 */
public class Lab1 {
    public static void main(String[] args) throws InterruptedException {
        Map<Long, BigDecimal> clientsMoney = new ConcurrentHashMap<>();
        BlockingQueue<Message> C2S = new ArrayBlockingQueue<Message>(1024);
        BlockingQueue<Message> C2B = new ArrayBlockingQueue<Message>(1024);
        BlockingQueue<Message> B2C = new ArrayBlockingQueue<Message>(1024);
        BlockingQueue<Message> B2S = new ArrayBlockingQueue<Message>(1024);
        B2S.put(new NullMessage());
        BlockingQueue<Message> S2B = new ArrayBlockingQueue<Message>(1024);
        BlockingQueue<Message> S2C = new ArrayBlockingQueue<Message>(1024);
        Customer customer = new Customer(1l, C2S, C2B, S2C, B2C);
        Shop shop = new Shop(S2C, S2B, C2S, B2S);
        clientsMoney.put(1l, new BigDecimal(20000));
        Bank bank = new Bank(clientsMoney, B2S, B2C, S2B, C2B);

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
    private BlockingQueue<Message> fromShopQueue;
    private BlockingQueue<Message> fromBankQueue;

    public Customer(Long id, BlockingQueue<Message> toShopQueue, BlockingQueue<Message> toBankQueue, BlockingQueue<Message> fromShopQueue, BlockingQueue<Message> fromBankQueue) {
        this.toShopQueue = toShopQueue;
        this.toBankQueue = toBankQueue;
        this.fromShopQueue = fromShopQueue;
        this.fromBankQueue = fromBankQueue;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message executeMessage = getMinMessage();
                if (executeMessage != null) {
                    processMessage(executeMessage);
                    Thread.sleep(11000);
                    toShopQueue.put(new NullMessage());
                    toBankQueue.put(new NullMessage());
                }
//                Thread.sleep(1000);

                if (this.isInterrupted()) {
                    break;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void processMessage(Message executeMessage) throws Exception {
//        System.out.println("Покупатель.");
//        System.out.print(executeMessage.time + ". ");
        if (executeMessage instanceof NullMessage) {
            System.out.println("Покупатель.\n"+
                    executeMessage.time + ". Получено: Нулевое сообщение");
        } else {
            throw new Exception();
        }
    }

    public void BuyOnTheShop(BigDecimal cost) {
//        System.out.println("Покупатель. ");
        try {
            BuyMessage buyMessage = new BuyMessage(id, cost);
            System.out.println("Покупатель.\n"+
                    buyMessage.time + ". Отправить сообщение на покупку в магазин:" + cost);
            toShopQueue.put(buyMessage);
            Thread.sleep(1000);
            toShopQueue.put(new NullMessage());
            toBankQueue.put(new NullMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void WithdrawMoney(BigDecimal cost) {
        try {
            WithdrawMessage withdrawMessage = new WithdrawMessage(id, cost);
            System.out.println("Покупатель.\n"+
                    withdrawMessage.time + ". Отправить сообщение на снятие денег со счета: " + cost);
            toBankQueue.put(withdrawMessage);
            Thread.sleep(1000);
            toShopQueue.put(new NullMessage());
            toBankQueue.put(new NullMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Message getMinMessage() throws InterruptedException {
        Message fromShop = fromShopQueue.peek();
        Message fromBank = fromBankQueue.peek();
        if (fromBank != null && fromShop != null) {
            if (fromShop.time.isBefore(fromBank.time)) {
                return fromShopQueue.take();
            } else {
                return fromBankQueue.take();
            }
        } else {
            return null;
        }
    }
}

class Shop extends Thread {

    private BlockingQueue<Message> toCustomerQueue;
    private BlockingQueue<Message> toBankQueue;
    private BlockingQueue<Message> fromCustomerQueue;
    private BlockingQueue<Message> fromBankQueue;

    public Shop(BlockingQueue<Message> toCustomerQueue, BlockingQueue<Message> toBankQueue, BlockingQueue<Message> fromCustomerQueue, BlockingQueue<Message> fromBankQueue) {
        this.toCustomerQueue = toCustomerQueue;
        this.toBankQueue = toBankQueue;
        this.fromCustomerQueue = fromCustomerQueue;
        this.fromBankQueue = fromBankQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message executeMessage = getMinMessage();
                if (executeMessage != null) {
                    processMessage(executeMessage);
                    Thread.sleep(9950);
                    toCustomerQueue.put(new NullMessage());
                    toBankQueue.put(new NullMessage());
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
        if (executeMessage instanceof NullMessage) {
            System.out.println("Магазин.\n" +
                    executeMessage.time + ". " + "Получено: Нулевое сообщение");
        } else if (executeMessage instanceof BuyMessage) {
            WithdrawMessage withdrawMessage = new WithdrawMessage(((BuyMessage) executeMessage).customerId, ((BuyMessage) executeMessage).money);
            System.out.println("Магазин.\n" +
                    executeMessage.time + ". Получено: покупатель " + ((BuyMessage) executeMessage).customerId + " покупка.\n" +
                    withdrawMessage.time + ". Отправлено: сообщение банку на списание денег: " + ((BuyMessage) executeMessage).money);
            toBankQueue.put(withdrawMessage);
        } else {
            throw new Exception();
        }
    }

    private Message getMinMessage() throws InterruptedException {
        Message fromCustomer = fromCustomerQueue.peek();
        Message fromBank = fromBankQueue.peek();
        if (fromBank != null && fromCustomer != null) {
            if (fromCustomer.time.isBefore(fromBank.time)) {
                return fromCustomerQueue.take();
            } else {
                return fromBankQueue.take();
            }
        } else {
            return null;
        }
    }
}

class Bank extends Thread {

    Map<Long, BigDecimal> clientsMoney;

    private BlockingQueue<Message> toShopQueue;
    private BlockingQueue<Message> toCustomerQueue;
    private BlockingQueue<Message> fromShopQueue;
    private BlockingQueue<Message> fromCustomerQueue;

    public Bank(Map<Long, BigDecimal> clientsMoney, BlockingQueue<Message> toShopQueue, BlockingQueue<Message> toCustomerQueue, BlockingQueue<Message> fromShopQueue, BlockingQueue<Message> fromCustomerQueue) {
        this.toShopQueue = toShopQueue;
        this.toCustomerQueue = toCustomerQueue;
        this.fromShopQueue = fromShopQueue;
        this.fromCustomerQueue = fromCustomerQueue;
        this.clientsMoney = clientsMoney;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message executeMessage = getMinMessage();
                if (executeMessage != null) {
                    processMessage(executeMessage);
                    Thread.sleep(9050);
                    toCustomerQueue.put(new NullMessage());
                    toShopQueue.put(new NullMessage());
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
//        System.out.println("Банк. ");
//        System.out.print(executeMessage.time + ". ");
        if (executeMessage instanceof NullMessage) {
            System.out.println("Банк.\n" +
                    executeMessage.time + ". " +
                    "Получено: Нулевое сообщение");
        } else if (executeMessage instanceof WithdrawMessage) {
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

    }

    private Message getMinMessage() throws InterruptedException {
        Message fromCustomer = fromCustomerQueue.peek();
        Message fromShop = fromShopQueue.peek();
        if (fromShop != null && fromCustomer != null) {
            if (fromCustomer.time.isBefore(fromShop.time)) {
                return fromCustomerQueue.take();
            } else {
                return fromShopQueue.take();
            }
        } else {
            return null;
        }
    }
}
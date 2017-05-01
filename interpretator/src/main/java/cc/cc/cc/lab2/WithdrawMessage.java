package cc.cc.cc.lab2;

import java.math.BigDecimal;

/**
 * Created by 1 on 29.04.2017.
 */
public class WithdrawMessage extends Message {
    public Long customerId;
    public BigDecimal money;

    public WithdrawMessage(Long customerId, BigDecimal money) {
        this.customerId = customerId;
        this.money = money;
    }
}

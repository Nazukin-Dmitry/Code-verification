package com.codeverification;

/**
 * @author Dmitrii Nazukin
 */
public class BreakException extends RuntimeException {

    private int comNum;

    public BreakException() {
    }

    public BreakException(String message, int comNum) {
        super(message);
        this.comNum = comNum;
    }

    public int getComNum() {
        return comNum;
    }

    public void setComNum(int comNum) {
        this.comNum = comNum;
    }
}

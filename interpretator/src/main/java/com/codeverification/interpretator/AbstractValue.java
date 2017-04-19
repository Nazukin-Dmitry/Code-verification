package com.codeverification.interpretator;

/**
 * @author Dmitrii Nazukin
 */
public abstract class AbstractValue implements Value {

    private String raw;

    private boolean isConst = false;

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw1) {
        raw = raw1;
    }
}

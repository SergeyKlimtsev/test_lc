package com.price.processor;


import java.util.Objects;

public class PriceMessage {
    private final String ccyPair;
    private final double rate;

    public PriceMessage(String ccyPair, double rate) {
        this.ccyPair = ccyPair;
        this.rate = rate;
    }

    public String getCcyPair() {
        return ccyPair;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceMessage)) return false;
        PriceMessage that = (PriceMessage) o;
        return Double.compare(that.rate, rate) == 0 &&
                Objects.equals(ccyPair, that.ccyPair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ccyPair, rate);
    }
}

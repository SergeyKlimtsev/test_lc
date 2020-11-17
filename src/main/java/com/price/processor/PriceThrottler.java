package com.price.processor;


import java.util.HashMap;
import java.util.Map;

/**
 * Nothing was said about the concurrency environment of the PriceThrottler class
 * so let assume that is used by one thread.
 */
public class PriceThrottler implements PriceProcessor {
    private Map<PriceProcessor, PriceProcessorNotifier> notifiers;

    public PriceThrottler() {
        this.notifiers = new HashMap<>();
    }

    @Override
    public void onPrice(String ccyPair, double rate) {
        notifiers.values().forEach(p -> p.notifyProcessor(ccyPair, rate));
    }

    @Override
    public void subscribe(PriceProcessor priceProcessor) {
        PriceProcessorNotifier priceProcessorNotifier = new PriceProcessorNotifier(priceProcessor);
        notifiers.put(priceProcessor, priceProcessorNotifier);
        priceProcessorNotifier.startNotifying();
    }

    @Override
    public void unsubscribe(PriceProcessor priceProcessor) {
        notifiers.get(priceProcessor).stopNotifying();
        notifiers.remove(priceProcessor);
    }
}

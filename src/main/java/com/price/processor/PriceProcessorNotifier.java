package com.price.processor;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Since we know that is the max processor count is 200
 * we are able to provide a thread for each processor for notification.
 * However, we might solve the notification problem without involving thread hell
 * by using a non-blocking event loop if we could control all the PriceProcessor implementation,
 * but we can't so thread hell is the only option.
 */
public class PriceProcessorNotifier {

    public PriceProcessorNotifier(PriceProcessor priceProcessor) {
        this.executor = Executors.newSingleThreadExecutor();
        this.messages = new LinkedHashMap<>();
        this.priceProcessor = priceProcessor;
        this.isWorking = true;
    }

    private final ExecutorService executor;
    private final LinkedHashMap<String, PriceMessage> messages;
    private final PriceProcessor priceProcessor;
    private volatile boolean isWorking;

    public PriceProcessor getPriceProcessor() {
        return priceProcessor;
    }

    public void notifyProcessor(PriceMessage message) {
        synchronized (messages) {
            messages.put(message.getCcyPair(), message);
        }
    }

    public void notifyProcessor(String ccyPair, double rate) {
        notifyProcessor(new PriceMessage(ccyPair, rate));
    }


    private PriceMessage getNextMessage() {
        synchronized (messages) {
            Map.Entry<String, PriceMessage> entry = messages.entrySet().iterator().next();
            if (entry == null) {
                return null;
            } else {
                messages.remove(entry.getKey());
                return entry.getValue();
            }
        }
    }

    public void startNotifying() {
        Runnable task = () -> {
            while (isWorking) {
                PriceMessage message = getNextMessage();
                try {
                    if (message != null) {
                        priceProcessor.onPrice(message.getCcyPair(), message.getRate());
                    }
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
        };
        executor.submit(task);
    }

    public void stopNotifying() {
        this.isWorking = false;
        executor.shutdown();
    }
}

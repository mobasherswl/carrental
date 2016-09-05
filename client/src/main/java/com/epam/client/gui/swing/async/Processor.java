package com.epam.client.gui.swing.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Created by Ahmed_Khan on 4/27/2016.
 */
@Component
public class Processor {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    public <T, E extends Exception> void execute(Callable<T> callable, Consumer<T> successConsumer, Consumer<E> exceptionConsumer) {
        SwingWorker<T, Void> swingWorker = new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return callable.call();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    if (result != null) {
                        if(successConsumer != null) {
                            successConsumer.accept(result);
                        }
                    } else {
                        throw new IllegalArgumentException("Server returned null");
                    }
                } catch (Exception e) {
                    logger.error("", e);
                    if(exceptionConsumer != null) {
                        exceptionConsumer.accept((E) e);
                    }
                }
            }
        };

        swingWorker.execute();

    }
}

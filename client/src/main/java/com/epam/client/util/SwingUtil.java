package com.epam.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Created by Ahmed_Khan on 5/9/2016.
 */
public final class SwingUtil {

    private static final Logger logger = LoggerFactory.getLogger(SwingUtil.class);

    public static void loadLookAndFeel(Logger logger) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            if (logger == null) {
                logger = SwingUtil.logger;
            }
            logger.error("Platform specific look & feel failed to initialize", e);
        }

    }

}

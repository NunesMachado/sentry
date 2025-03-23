package org.scriptysentry;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {

    private static final Logger LOGGER = Logger.getLogger(LogUtil.class.getName());

    private LogUtil() {} // Evita instância

    public static void logInfo(String message, Object... args) {
        LOGGER.info(String.format(message, args));
    }

    public static void logWarning(String message, Object... args) {
        LOGGER.warning(String.format(message, args));
    }

    public static void logError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, String.format(message, e.getMessage()), e);
    }
}

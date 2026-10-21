package com.youfuns.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OutputLogger implements SimpleLogger {
    private final AndroidLogger logger;

    public OutputLogger(java.io.PrintStream console) {
        this.logger = new AndroidLogger();
    }

    public OutputLogger() {
        this(null);
    }


    @Override
    public void log(Class<?> clazz, String message, Level level) {
        logger.log(clazz, message, level);
    }

    @Override
    public void log(Class<?> clazz, String message, Level level, Throwable t) {
        logger.log(clazz, message, level);
    }

    @Override
    public Level getLogLevel() {
        return logger.getLogLevel();
    }

    @Override
    public void setLogLevel(Level logLevel) {
        logger.setLogLevel(logLevel);
    }
}
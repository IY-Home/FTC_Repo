package com.youfuns.logger;

import android.util.Log;

/**
 * Android implementation of {@link SimpleLogger} that delegates to {@link Log}.
 * <p>
 * Timestamps, prefixes, and formatting are handled by the Android logging system,
 * so this logger does not add any of its own.
 */
public class AndroidLogger implements SimpleLogger {

    private static final String DEFAULT_TAG = "App";
    private static final int MAX_TAG_LENGTH = 23;

    private volatile Level logLevel = Level.DEBUG;

    @Override
    public Level getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(Level logLevel) {
        if (logLevel != null) {
            this.logLevel = logLevel;
        }
    }

    @Override
    public void log(Class<?> clazz, String message, Level level) {
        if (message == null || !Level.aboveLevel(level, logLevel)) {
            return;
        }
        Log.println(toAndroidPriority(level), getTag(clazz), message);
    }

    @Override
    public void log(Class<?> clazz, String message, Level level, Throwable t) {
        if (message == null || !Level.aboveLevel(level, logLevel)) {
            return;
        }
        String tag = getTag(clazz);
        int priority = toAndroidPriority(level);
        if (t == null) {
            Log.println(priority, tag, message);
        } else {
            Log.println(priority, tag, message + "\n" + Log.getStackTraceString(t));
        }
    }

    /**
     * Derives a log tag from the given class. Falls back to {@link #DEFAULT_TAG}
     * when the class is {@code null} or has no usable name. Tags longer than
     * {@value #MAX_TAG_LENGTH} characters are truncated to satisfy Android's limit.
     */
    private static String getTag(Class<?> clazz) {
        String tag = DEFAULT_TAG;
        if (clazz != null) {
            String name = clazz.getSimpleName();
            if (name.isEmpty()) {
                name = clazz.getName();
                int dot = name.lastIndexOf('.');
                if (dot >= 0 && dot < name.length() - 1) {
                    name = name.substring(dot + 1);
                }
            }
            if (!name.isEmpty()) {
                tag = name;
            }
        }
        if (tag.length() > MAX_TAG_LENGTH) {
            tag = tag.substring(0, MAX_TAG_LENGTH);
        }
        return tag;
    }

    private static int toAndroidPriority(Level level) {
        switch (level) {
            case INFO:  return Log.DEBUG;
            case WARN:  return Log.WARN;
            case ERROR: return Log.ERROR;
            default:    return Log.VERBOSE;
        }
    }
}
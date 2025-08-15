package org.example.util;

import java.util.logging.*;

public enum Log {
    TRACE(Level.FINEST), DEBUG(Level.FINE), INFO(Level.INFO), WARN(Level.WARNING), ERROR(Level.SEVERE);

    private final Level level;
    private static final Logger LOGGER = Logger.getLogger("exam");
    static {
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        LOGGER.addHandler(ch);
        LOGGER.setLevel(Level.ALL);
    }

    Log(Level level){ this.level = level; }

    public void log(String msg){ LOGGER.log(level, msg); }
    public void log(String msg, Throwable t){ LOGGER.log(level, msg, t); }
}

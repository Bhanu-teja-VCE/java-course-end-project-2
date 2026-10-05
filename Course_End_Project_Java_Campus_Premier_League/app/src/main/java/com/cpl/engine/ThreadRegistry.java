package com.cpl.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread registry tracking thread lifecycle and activities for the Thread Monitor UI.
 * Demonstrates:
 * - Unit II: Thread lifecycle (NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED)
 * - Unit II: Thread priorities
 */
public class ThreadRegistry {

    public static class ThreadInfo {
        public final long id;
        public final String name;
        public final Thread.State state;
        public final int priority;
        public final boolean isDaemon;
        public final String activity;

        public ThreadInfo(Thread t, String activity) {
            this.id = t.getId();
            this.name = t.getName();
            this.state = t.getState();
            this.priority = t.getPriority();
            this.isDaemon = t.isDaemon();
            this.activity = activity;
        }
    }

    private static final List<Thread> registeredThreads = new ArrayList<Thread>();

    public static synchronized void register(Thread t) {
        if (!registeredThreads.contains(t)) {
            registeredThreads.add(t);
        }
    }

    public static synchronized List<ThreadInfo> snapshot() {
        List<ThreadInfo> list = new ArrayList<ThreadInfo>();
        for (Thread t : registeredThreads) {
            String activity = "Active";
            if (t.getState() == Thread.State.TERMINATED) {
                activity = "Finished";
            } else if (t.getState() == Thread.State.TIMED_WAITING) {
                activity = "Sleeping / Waiting on timeout";
            } else if (t.getState() == Thread.State.WAITING) {
                activity = "Waiting on monitor lock";
            } else if (t.getState() == Thread.State.RUNNABLE) {
                activity = "Executing bytecodes";
            }
            list.add(new ThreadInfo(t, activity));
        }
        return Collections.unmodifiableList(list);
    }
}

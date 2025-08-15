package org.example.service;

import org.example.model.Exam;
import java.util.concurrent.*;

class ExamCache {
    private static class Entry { final Exam exam; final long exp; Entry(Exam e, long exp){this.exam=e; this.exp=exp;} }
    private final ConcurrentHashMap<Integer, Entry> map = new ConcurrentHashMap<>();
    private final long ttlMillis;
    ExamCache(long ttlMillis){ this.ttlMillis = ttlMillis; }

    Exam getOrPut(int examId, java.util.function.Supplier<Exam> loader) {
        long now = System.currentTimeMillis();
        ExamCache.Entry e = map.get(examId);
        if (e != null && e.exp > now) return e.exam;

        ExamCache.Entry updated = map.compute(examId, (k, old) -> {
            long t = System.currentTimeMillis();
            if (old != null && old.exp > t) return old;
            Exam loaded = loader.get();
            return (loaded != null) ? new Entry(loaded, t + ttlMillis) : null;
        });

        return (updated != null) ? updated.exam : null;
    }

}

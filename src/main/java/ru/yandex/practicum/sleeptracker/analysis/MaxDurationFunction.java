package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.*;
import java.util.List;

public class MaxDurationFunction implements SleepAnalysisFunction<Long> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Максимальная продолжительность сессии", 0L);
        }

        long maxDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .max()
                .orElse(0);

        return new SleepAnalysisResult<>("Максимальная продолжительность сессии (минут)", maxDuration);
    }
}
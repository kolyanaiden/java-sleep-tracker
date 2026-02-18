package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.*;
import java.util.List;

public class MinDurationFunction implements SleepAnalysisFunction<Long> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Минимальная продолжительность сессии", 0L);
        }

        long minDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .min()
                .orElse(0);

        return new SleepAnalysisResult<>("Минимальная продолжительность сессии (минут)", minDuration);
    }
}
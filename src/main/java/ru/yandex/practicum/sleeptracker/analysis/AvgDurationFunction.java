package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.*;
import java.util.List;

public class AvgDurationFunction implements SleepAnalysisFunction<Double> {
    @Override
    public SleepAnalysisResult<Double> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Средняя продолжительность сессии", 0.0);
        }

        double avgDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .average()
                .orElse(0);

        return new SleepAnalysisResult<>("Средняя продолжительность сессии (минут)", avgDuration);
    }
}
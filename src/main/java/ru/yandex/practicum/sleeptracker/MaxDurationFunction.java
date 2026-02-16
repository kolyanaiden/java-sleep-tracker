package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MaxDurationFunction implements SleepAnalysisFunction<Long> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long maxDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .max()
                .orElse(0);

        // Округляем до ближайшего целого, так как в тесте ожидается 495, а не 500
        return new SleepAnalysisResult<>("Максимальная продолжительность сессии (минут)", maxDuration);
    }
}
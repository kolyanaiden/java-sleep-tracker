package ru.yandex.practicum.sleeptracker;
import java.util.List;

public class MinDurationFunction implements SleepAnalysisFunction<Long> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long minDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .min()
                .orElse(0);
        return new SleepAnalysisResult<>("Минимальная продолжительность сессии (минут)", minDuration);
    }
}
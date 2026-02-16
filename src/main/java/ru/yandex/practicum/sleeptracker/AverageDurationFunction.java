package ru.yandex.practicum.sleeptracker;
import java.util.List;

public class AverageDurationFunction implements SleepAnalysisFunction<Double> {
    @Override
    public SleepAnalysisResult<Double> apply(List<SleepingSession> sessions) {
        double avgDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .average()
                .orElse(0);
        return new SleepAnalysisResult<>("Средняя продолжительность сессии (минут)", avgDuration);
    }
}
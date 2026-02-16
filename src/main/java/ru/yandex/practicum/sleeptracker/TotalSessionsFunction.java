package ru.yandex.practicum.sleeptracker;
import java.util.List;

public class TotalSessionsFunction implements SleepAnalysisFunction<Long> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long count = sessions.stream().count();
        return new SleepAnalysisResult<>("Общее количество сессий сна", count);
    }
}
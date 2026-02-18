package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.*;
import java.util.List;

public class TotalSessionsFunction implements SleepAnalysisFunction<Long> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long count = sessions.size();
        return new SleepAnalysisResult<>("Общее количество сессий сна", count);
    }
}
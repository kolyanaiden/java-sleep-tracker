package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface SleepAnalysisFunction<T> extends Function<List<SleepingSession>, SleepAnalysisResult<T>> {}
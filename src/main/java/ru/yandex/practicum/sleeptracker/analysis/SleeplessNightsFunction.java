package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements SleepAnalysisFunction<Long> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        // Находим первую и последнюю даты
        LocalDate firstDate = sessions.get(0).getStartTime().toLocalDate();
        LocalDate lastDate = sessions.get(sessions.size() - 1).getEndTime().toLocalDate();

        System.out.println("Первая дата: " + firstDate);
        System.out.println("Последняя дата: " + lastDate);

        // Находим все ночи, когда был сон
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(session -> isNightSession(session))
                .map(session -> {
                    LocalDateTime start = session.getStartTime();
                    // Если заснул после 12 дня, это сон для следующей ночи
                    if (start.getHour() >= 12) {
                        return start.toLocalDate().plusDays(1);
                    } else if (start.getHour() < 6) {
                        return start.toLocalDate();
                    } else {
                        return start.toLocalDate();
                    }
                })
                .collect(Collectors.toSet());

        System.out.println("Ночи со сном: " + nightsWithSleep.stream()
                .sorted()
                .map(LocalDate::toString)
                .collect(Collectors.joining(", ")));

        // Вычисляем общее количество ночей в периоде
        long totalNights = ChronoUnit.DAYS.between(firstDate, lastDate) + 1;
        System.out.println("Всего ночей в периоде: " + totalNights);

        long sleeplessNights = totalNights - nightsWithSleep.size();
        System.out.println("Бессонных ночей: " + sleeplessNights);

        return new SleepAnalysisResult<>("Количество бессонных ночей", sleeplessNights);
    }

    private boolean isNightSession(SleepingSession session) {
        LocalDateTime start = session.getStartTime();
        LocalDateTime end = session.getEndTime();

        // Проверяем, пересекается ли сессия с интервалом 00:00-06:00
        int startHour = start.getHour();
        int endHour = end.getHour();

        // Сессия в пределах одного дня
        if (start.toLocalDate().equals(end.toLocalDate())) {
            // Сон считается ночным, если:
            // 1. Начался до 6 утра
            // 2. Или закончился после полуночи, но до 6 утра
            // 3. Или начался после полуночи и до 6 утра
            return (startHour < 6) || (endHour > 0 && endHour < 6);
        } else {
            // Сессия через полночь - всегда считается ночной
            return true;
        }
    }
}
package ru.yandex.practicum.sleeptracker;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements SleepAnalysisFunction<Long> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        // Определяем период логирования
        LocalDateTime firstSession = sessions.stream()
                .map(SleepingSession::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElseThrow();

        LocalDateTime lastSession = sessions.stream()
                .map(SleepingSession::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        // Получаем все ночи в периоде
        LocalDate startDate = firstSession.toLocalDate();
        LocalDate endDate = lastSession.toLocalDate();

        // Если первая сессия началась после 12 дня, первая ночь для проверки - следующая
        if (firstSession.getHour() >= 12) {
            startDate = startDate.plusDays(1);
        }

        long totalNights = Period.between(startDate, endDate).getDays() + 1;

        // Находим все даты, где был ночной сон
        List<LocalDate> nightsWithSleep = sessions.stream()
                .filter(s -> s.coversNightHours())
                .map(s -> {
                    LocalDateTime sessionTime = s.getStartTime();
                    // Если сессия началась после 12 дня, она относится к следующей ночи
                    if (sessionTime.getHour() >= 12) {
                        return sessionTime.plusDays(1).toLocalDate();
                    }
                    return sessionTime.toLocalDate();
                })
                .distinct()
                .collect(Collectors.toList());

        long nightsWithSleepCount = nightsWithSleep.size();
        long sleeplessNights = totalNights - nightsWithSleepCount;

        return new SleepAnalysisResult<>("Количество бессонных ночей", Math.max(0, sleeplessNights));
    }
}
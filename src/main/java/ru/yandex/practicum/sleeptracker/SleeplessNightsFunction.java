package ru.yandex.practicum.sleeptracker;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

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

        // Если первая сессия началась после 12 дня, начинаем со следующей ночи
        if (firstSession.getHour() >= 12) {
            startDate = startDate.plusDays(1);
        }

        // Если последняя сессия закончилась до 6 утра, это последняя ночь
        if (lastSession.getHour() < 6) {
            endDate = endDate.minusDays(1);
        }

        long totalNights = Period.between(startDate, endDate).getDays() + 1;

        // Находим ночи со сном
        long nightsWithSleep = sessions.stream()
                .filter(SleepingSession::coversNightHours)
                .map(session -> {
                    LocalDateTime sessionTime = session.getStartTime();
                    // Если сессия началась после 12 дня, она относится к следующей ночи
                    if (sessionTime.getHour() >= 12) {
                        sessionTime = sessionTime.plusDays(1);
                    }
                    return sessionTime.toLocalDate();
                })
                .distinct()
                .count();

        long sleeplessNights = Math.max(0, totalNights - nightsWithSleep);

        return new SleepAnalysisResult<>("Количество бессонных ночей", sleeplessNights);
    }
}
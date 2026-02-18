package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SleepingSession {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final SleepQuality quality;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public SleepingSession(LocalDateTime startTime, LocalDateTime endTime, SleepQuality quality) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public long getDurationMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public boolean isNightSession() {
        int startHour = startTime.getHour();
        int endHour = endTime.getHour();
        int endMinute = endTime.getMinute();

        // Проверяем, пересекается ли сессия с интервалом 0:00-6:00
        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            // Сессия в пределах одного дня
            return (startHour < 6) || (endHour < 6 && endHour > 0) ||
                    (startHour >= 0 && startHour < 6);
        } else {
            // Сессия переходит через полночь
            return true; // Любая сессия через полночь считается ночной
        }
    }

    public static SleepingSession parseFromString(String line) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid session format: " + line);
        }

        LocalDateTime start = LocalDateTime.parse(parts[0], FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1], FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2]);

        return new SleepingSession(start, end, quality);
    }

    @Override
    public String toString() {
        return String.format("Sleep from %s to %s, quality: %s, duration: %d min",
                startTime.format(FORMATTER), endTime.format(FORMATTER),
                quality, getDurationMinutes());
    }
}
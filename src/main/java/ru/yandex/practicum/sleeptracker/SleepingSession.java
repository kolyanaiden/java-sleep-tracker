package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SleepingSession {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final SleepQuality quality;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public SleepingSession(LocalDateTime startTime, LocalDateTime endTime, SleepQuality quality) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
    }

    public static SleepingSession fromString(String line) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid session format: " + line);
        }

        LocalDateTime start = LocalDateTime.parse(parts[0], FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1], FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2]);

        return new SleepingSession(start, end, quality);
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

        // Сессия считается ночной, если она пересекает период с 0 до 6 утра
        return (startHour <= 6 && endHour >= 0) ||  // началась до 6 утра
                (startHour >= 0 && startHour < 6) || // началась ночью
                (startTime.toLocalDate().isBefore(endTime.toLocalDate())); // переходит через полночь
    }

    public boolean coversNightHours() {
        LocalDateTime nightStart = startTime.withHour(0).withMinute(0);
        LocalDateTime nightEnd = startTime.withHour(6).withMinute(0);

        // Проверяем пересечение с интервалом 0:00 - 6:00
        return !(endTime.isBefore(nightStart) || startTime.isAfter(nightEnd));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SleepingSession that = (SleepingSession) o;
        return Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                quality == that.quality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, quality);
    }
}
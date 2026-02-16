package ru.yandex.practicum.sleeptracker;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

enum Chronotype {
    OWL("Сова"),
    LARK("Жаворонок"),
    PIGEON("Голубь");

    private final String description;

    Chronotype(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

public class ChronotypeFunction implements SleepAnalysisFunction<String> {
    @Override
    public SleepAnalysisResult<String> apply(List<SleepingSession> sessions) {
        Map<Chronotype, Long> chronotypeCounts = sessions.stream()
                .filter(SleepingSession::coversNightHours) // Только ночные сессии
                .map(this::determineChronotype)
                .collect(Collectors.groupingBy(
                        chronotype -> chronotype,
                        Collectors.counting()
                ));

        // Если нет ночных сессий
        if (chronotypeCounts.isEmpty()) {
            return new SleepAnalysisResult<>("Хронотип пользователя", "Не определен (нет ночных сессий)");
        }

        Chronotype result = determineOverallChronotype(chronotypeCounts);
        return new SleepAnalysisResult<>("Хронотип пользователя", result.getDescription());
    }

    private Chronotype determineChronotype(SleepingSession session) {
        int startHour = session.getStartTime().getHour();
        int endHour = session.getEndTime().getHour();

        if (startHour >= 23 && endHour >= 9) {
            return Chronotype.OWL;
        } else if (startHour <= 22 && endHour <= 7) {
            return Chronotype.LARK;
        } else {
            return Chronotype.PIGEON;
        }
    }

    private Chronotype determineOverallChronotype(Map<Chronotype, Long> counts) {
        long owlCount = counts.getOrDefault(Chronotype.OWL, 0L);
        long larkCount = counts.getOrDefault(Chronotype.LARK, 0L);
        long pigeonCount = counts.getOrDefault(Chronotype.PIGEON, 0L);

        if (owlCount > larkCount && owlCount > pigeonCount) {
            return Chronotype.OWL;
        } else if (larkCount > owlCount && larkCount > pigeonCount) {
            return Chronotype.LARK;
        } else {
            return Chronotype.PIGEON;
        }
    }
}
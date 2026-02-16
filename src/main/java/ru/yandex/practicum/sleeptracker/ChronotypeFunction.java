package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypeFunction implements SleepAnalysisFunction<String> {

    private enum Chronotype {
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

    @Override
    public SleepAnalysisResult<String> apply(List<SleepingSession> sessions) {
        // Фильтруем только ночные сессии (длительные и в ночное время)
        List<SleepingSession> nightSessions = sessions.stream()
                .filter(s -> s.isNightSession() && s.coversNightHours())
                .collect(Collectors.toList());

        if (nightSessions.isEmpty()) {
            return new SleepAnalysisResult<>("Хронотип пользователя", "Голубь");
        }

        Map<Chronotype, Long> chronotypeCounts = nightSessions.stream()
                .map(this::determineChronotype)
                .collect(Collectors.groupingBy(
                        chronotype -> chronotype,
                        Collectors.counting()
                ));

        Chronotype result = determineOverallChronotype(chronotypeCounts);
        return new SleepAnalysisResult<>("Хронотип пользователя", result.getDescription());
    }

    private Chronotype determineChronotype(SleepingSession session) {
        int startHour = session.getStartTime().getHour();
        int endHour = session.getEndTime().getHour();

        // Сова: ложится после 23:00, встает после 8:00
        if (startHour >= 23 && endHour >= 8) {
            return Chronotype.OWL;
        }
        // Жаворонок: ложится до 22:00, встает до 7:00
        else if (startHour <= 22 && endHour <= 7) {
            return Chronotype.LARK;
        }
        // Все остальные случаи - голуби
        else {
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
package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypeFunction implements SleepAnalysisFunction<String> {
    @Override
    public SleepAnalysisResult<String> apply(List<SleepingSession> sessions) {
        Map<Chronotype, Long> chronotypeCounts = sessions.stream()
                .filter(SleepingSession::isNightSession)
                .map(this::determineChronotype)
                .collect(Collectors.groupingBy(
                        chronotype -> chronotype,
                        Collectors.counting()
                ));

        Chronotype resultChronotype = determineMostCommonChronotype(chronotypeCounts);

        String description = String.format("Хронотип пользователя (%s)",
                getChronotypeDescription(resultChronotype));

        return new SleepAnalysisResult<>(description, resultChronotype.getDisplayName());
    }

    private Chronotype determineChronotype(SleepingSession session) {
        int startHour = session.getStartTime().getHour();
        int startMinute = session.getStartTime().getMinute();
        int endHour = session.getEndTime().getHour();
        int endMinute = session.getEndTime().getMinute();

        // Проверяем, является ли сессия дневной или бессонной ночью не рассматриваем
        if (!session.isNightSession()) {
            return null; // Игнорируем
        }

        // Сова: засыпание после 23:00, пробуждение после 9:00
        if (startHour > 23 || (startHour == 23 && startMinute > 0) || startHour >= 23) {
            if (endHour > 9 || (endHour == 9 && endMinute > 0) || endHour >= 9) {
                return Chronotype.OWL;
            }
        }

        // Жаворонок: засыпание до 22:00, пробуждение до 7:00
        if (startHour < 22 || (startHour == 22 && startMinute == 0)) {
            if (endHour < 7 || (endHour == 7 && endMinute == 0)) {
                return Chronotype.LARK;
            }
        }

        return Chronotype.PIGEON;
    }

    private Chronotype determineMostCommonChronotype(Map<Chronotype, Long> counts) {
        if (counts.isEmpty()) {
            return Chronotype.PIGEON;
        }

        // Удаляем null значения
        counts.remove(null);

        if (counts.isEmpty()) {
            return Chronotype.PIGEON;
        }

        // Находим максимальное значение
        long maxCount = counts.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        // Собираем все типы с максимальным количеством
        List<Chronotype> maxTypes = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Если есть несколько типов с одинаковым количеством или данных недостаточно
        if (maxTypes.size() > 1) {
            return Chronotype.PIGEON;
        }

        return maxTypes.get(0);
    }

    private String getChronotypeDescription(Chronotype chronotype) {
        switch (chronotype) {
            case OWL:
                return "любит поздно ложиться и поздно вставать";
            case LARK:
                return "любит рано ложиться и рано вставать";
            case PIGEON:
                return "промежуточный тип";
            default:
                return "";
        }
    }
}
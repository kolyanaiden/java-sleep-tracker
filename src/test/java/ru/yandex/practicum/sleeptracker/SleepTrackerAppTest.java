package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analysis.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SleepTrackerAppTest {

    private List<SleepingSession> testSessions;
    private SleepTrackerApp app;

    @BeforeEach
    void setUp() {
        app = new SleepTrackerApp();

        // Создаем тестовые данные
        testSessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 15),
                        LocalDateTime.of(2025, 10, 2, 8, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 8, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 30),
                        LocalDateTime.of(2025, 10, 3, 15, 20),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 23, 30),
                        LocalDateTime.of(2025, 10, 4, 6, 20),
                        SleepQuality.BAD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 5, 0, 10),
                        LocalDateTime.of(2025, 10, 5, 6, 20),
                        SleepQuality.GOOD
                )
        );
    }

    @Test
    void testTotalSessionsFunction() {
        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(testSessions);

        assertEquals(5L, result.getValue());
        assertTrue(result.getDescription().contains("Общее количество"));
    }

    @Test
    void testTotalSessionsFunctionEmptyList() {
        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(List.of());

        assertEquals(0L, result.getValue());
    }

    @Test
    void testMinDurationFunction() {
        MinDurationFunction function = new MinDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(testSessions);

        assertEquals(50L, result.getValue()); // 14:30-15:20 = 50 минут
    }

    @Test
    void testMinDurationFunctionEmptyList() {
        MinDurationFunction function = new MinDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(List.of());

        assertEquals(0L, result.getValue());
    }

    @Test
    void testMaxDurationFunction() {
        MaxDurationFunction function = new MaxDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(testSessions);

        assertEquals(585L, result.getValue()); // 22:15-08:00 = 585 минут (9ч 45м)
    }

    @Test
    void testMaxDurationFunctionEmptyList() {
        MaxDurationFunction function = new MaxDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(List.of());

        assertEquals(0L, result.getValue());
    }

    @Test
    void testAvgDurationFunction() {
        AvgDurationFunction function = new AvgDurationFunction();
        SleepAnalysisResult<Double> result = function.apply(testSessions);

        // Ожидаемая средняя: (585 + 540 + 50 + 410 + 370) / 5 = 391
        assertEquals(391.0, result.getValue(), 0.1);
    }

    @Test
    void testAvgDurationFunctionEmptyList() {
        AvgDurationFunction function = new AvgDurationFunction();
        SleepAnalysisResult<Double> result = function.apply(List.of());

        assertEquals(0.0, result.getValue());
    }

    @Test
    void testBadQualitySessionsFunction() {
        BadQualitySessionsFunction function = new BadQualitySessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(testSessions);

        assertEquals(1L, result.getValue()); // Только одна сессия с BAD качеством
    }

    @Test
    void testBadQualitySessionsFunctionNoBad() {
        List<SleepingSession> goodSessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 15),
                        LocalDateTime.of(2025, 10, 2, 8, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 8, 0),
                        SleepQuality.NORMAL
                )
        );

        BadQualitySessionsFunction function = new BadQualitySessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(goodSessions);

        assertEquals(0L, result.getValue());
    }

    @Test
    void testSleeplessNightsFunctionWithDaySessionsOnly() {
        List<SleepingSession> daySessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 30),
                        LocalDateTime.of(2025, 10, 1, 15, 20),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 13, 0),
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        SleepQuality.NORMAL
                )
        );

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(daySessions);

        // Должно быть 2 бессонные ночи (1-2 и 2-3 октября)
        assertEquals(2L, result.getValue());
    }

    @Test
    void testChronotypeFunction() {
        ChronotypeFunction function = new ChronotypeFunction();

        // Создаем сессии разных типов
        List<SleepingSession> mixedSessions = Arrays.asList(
                // Сова: засыпание после 23:00, пробуждение после 9:00
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                ),
                // Жаворонок: засыпание до 22:00, пробуждение до 7:00
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 21, 30),
                        LocalDateTime.of(2025, 10, 3, 6, 30),
                        SleepQuality.GOOD
                ),
                // Голубь: остальные случаи
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 22, 30),
                        LocalDateTime.of(2025, 10, 4, 8, 0),
                        SleepQuality.GOOD
                ),
                // Еще одна сова
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 4, 23, 45),
                        LocalDateTime.of(2025, 10, 5, 10, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult<String> result = function.apply(mixedSessions);

        // Должен определиться как "Сова" (2 против 1 и 1)
        assertEquals("Сова", result.getValue());
    }

    @Test
    void testChronotypeFunctionTie() {
        ChronotypeFunction function = new ChronotypeFunction();

        // Создаем сессии с равным количеством типов
        List<SleepingSession> tieSessions = Arrays.asList(
                // Сова
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                ),
                // Жаворонок
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 21, 30),
                        LocalDateTime.of(2025, 10, 3, 6, 30),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult<String> result = function.apply(tieSessions);

        // При равенстве должен быть "Голубь"
        assertEquals("Голубь", result.getValue());
    }

    @Test
    void testChronotypeFunctionIgnoreDaySessions() {
        ChronotypeFunction function = new ChronotypeFunction();

        // Создаем сессии с дневным сном
        List<SleepingSession> sessionsWithDaySleep = Arrays.asList(
                // Сова
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                ),
                // Дневной сон (должен игнорироваться)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 15, 0),
                        SleepQuality.NORMAL
                ),
                // Жаворонок
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 21, 30),
                        LocalDateTime.of(2025, 10, 3, 6, 30),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult<String> result = function.apply(sessionsWithDaySleep);

        // Должен быть "Голубь" (по 1 каждого типа, дневной сон игнорируется)
        assertEquals("Голубь", result.getValue());
    }

    @Test
    void testSleepingSessionParsing() {
        String line = "01.10.25 22:15;02.10.25 08:00;GOOD";
        SleepingSession session = SleepingSession.parseFromString(line);

        assertEquals(2025, session.getStartTime().getYear());
        assertEquals(10, session.getStartTime().getMonthValue());
        assertEquals(1, session.getStartTime().getDayOfMonth());
        assertEquals(22, session.getStartTime().getHour());
        assertEquals(15, session.getStartTime().getMinute());

        assertEquals(2025, session.getEndTime().getYear());
        assertEquals(10, session.getEndTime().getMonthValue());
        assertEquals(2, session.getEndTime().getDayOfMonth());
        assertEquals(8, session.getEndTime().getHour());
        assertEquals(0, session.getEndTime().getMinute());

        assertEquals(SleepQuality.GOOD, session.getQuality());
    }

    @Test
    void testSleepingSessionDuration() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 0),
                LocalDateTime.of(2025, 10, 2, 7, 30),
                SleepQuality.NORMAL
        );

        assertEquals(510, session.getDurationMinutes()); // 8 часов 30 минут = 510 минут
    }

    @Test
    void testSleepingSessionIsNightSession() {
        // Ночная сессия (пересекается с 0-6)
        SleepingSession nightSession1 = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 0),
                LocalDateTime.of(2025, 10, 2, 8, 0),
                SleepQuality.GOOD
        );
        assertTrue(nightSession1.isNightSession());

        // Ночная сессия (заканчивается в 5 утра)
        SleepingSession nightSession2 = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 2, 0),
                LocalDateTime.of(2025, 10, 1, 5, 0),
                SleepQuality.GOOD
        );
        assertTrue(nightSession2.isNightSession());

        // Дневная сессия
        SleepingSession daySession = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 14, 0),
                LocalDateTime.of(2025, 10, 1, 15, 0),
                SleepQuality.NORMAL
        );
        assertFalse(daySession.isNightSession());
    }
}
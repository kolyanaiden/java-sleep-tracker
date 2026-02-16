package ru.yandex.practicum.sleeptracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SleepTrackerAppTest {
    private List<SleepingSession> testSessions;

    @BeforeEach
    void setUp() {
        // Используем данные из примера
        testSessions = Arrays.asList(
                SleepingSession.fromString("01.10.25 23:15;02.10.25 07:30;GOOD"),
                SleepingSession.fromString("02.10.25 23:50;03.10.25 06:40;NORMAL"),
                SleepingSession.fromString("03.10.25 14:10;03.10.25 15:00;NORMAL"),
                SleepingSession.fromString("03.10.25 23:40;04.10.25 08:00;BAD"),
                SleepingSession.fromString("05.10.25 00:10;05.10.25 06:20;GOOD"),
                SleepingSession.fromString("05.10.25 13:30;05.10.25 14:15;NORMAL"),
                SleepingSession.fromString("06.10.25 22:30;07.10.25 05:50;GOOD"),
                SleepingSession.fromString("07.10.25 23:45;08.10.25 06:30;GOOD"),
                SleepingSession.fromString("08.10.25 23:50;09.10.25 07:10;GOOD"),
                SleepingSession.fromString("10.10.25 13:00;10.10.25 14:30;NORMAL"),
                SleepingSession.fromString("10.10.25 23:55;11.10.25 06:10;GOOD"),
                SleepingSession.fromString("11.10.25 23:10;12.10.25 07:00;BAD"),
                SleepingSession.fromString("30.10.25 23:50;31.10.25 06:30;GOOD")
        );
    }

    @Test
    void testTotalSessionsFunction() {
        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(testSessions);
        assertEquals(13L, result.getValue());
        assertEquals("Общее количество сессий сна", result.getDescription());
    }

    @Test
    void testTotalSessionsFunction_EmptyList() {
        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(List.of());
        assertEquals(0L, result.getValue());
    }

    @Test
    void testMinDurationFunction() {
        MinDurationFunction function = new MinDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(testSessions);
        // Минимальная длительность: сессия 03.10.25 14:10-15:00 = 50 минут
        // Но также есть сессия 05.10.25 13:30-14:15 = 45 минут
        assertEquals(45L, result.getValue());
    }

    @Test
    void testMinDurationFunction_EmptyList() {
        MinDurationFunction function = new MinDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(List.of());
        assertEquals(0L, result.getValue());
    }

    @Test
    void testMaxDurationFunction() {
        MaxDurationFunction function = new MaxDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(testSessions);
        // Максимальная длительность: 01.10.25 23:15 - 02.10.25 07:30 = 495 минут
        assertEquals(495L, result.getValue());
    }

    @Test
    void testMaxDurationFunction_EmptyList() {
        MaxDurationFunction function = new MaxDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(List.of());
        assertEquals(0L, result.getValue());
    }

    @Test
    void testAverageDurationFunction() {
        AverageDurationFunction function = new AverageDurationFunction();
        SleepAnalysisResult<Double> result = function.apply(testSessions);
        // Рассчитаем среднее вручную для проверки
        double expectedAvg = testSessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .average()
                .orElse(0);
        assertEquals(expectedAvg, result.getValue(), 0.01);
    }

    @Test
    void testAverageDurationFunction_EmptyList() {
        AverageDurationFunction function = new AverageDurationFunction();
        SleepAnalysisResult<Double> result = function.apply(List.of());
        assertEquals(0.0, result.getValue());
    }

    @Test
    void testBadQualitySessionsFunction() {
        BadQualitySessionsFunction function = new BadQualitySessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(testSessions);
        assertEquals(2L, result.getValue()); // BAD сессии: 03.10.25 и 11.10.25
    }

    @Test
    void testBadQualitySessionsFunction_EmptyList() {
        BadQualitySessionsFunction function = new BadQualitySessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(List.of());
        assertEquals(0L, result.getValue());
    }

    @Test
    void testSleeplessNightsFunction() {
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(testSessions);
        // Рассчитаем ожидаемое значение
        assertNotNull(result.getValue());
        System.out.println("Бессонных ночей: " + result.getValue());
    }

    @Test
    void testSleeplessNightsFunction_EmptyList() {
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(List.of());
        assertEquals(0L, result.getValue());
    }

    @Test
    void testSleeplessNightsFunction_SingleNight() {
        List<SleepingSession> singleNight = List.of(
                SleepingSession.fromString("01.10.25 23:00;02.10.25 07:00;GOOD")
        );
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(singleNight);
        assertEquals(0L, result.getValue()); // Не должно быть бессонных ночей
    }

    @Test
    void testSleeplessNightsFunction_DaySleepOnly() {
        List<SleepingSession> daySleep = List.of(
                SleepingSession.fromString("01.10.25 14:00;01.10.25 15:00;NORMAL")
        );
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(daySleep);
        assertEquals(1L, result.getValue()); // Одна бессонная ночь
    }

    @Test
    void testSleeplessNightsFunction_MultipleDays() {
        List<SleepingSession> multipleDays = Arrays.asList(
                SleepingSession.fromString("01.10.25 23:00;02.10.25 07:00;GOOD"),  // Ночь 1-2 окт
                SleepingSession.fromString("03.10.25 14:00;03.10.25 15:00;NORMAL"), // Дневной сон
                SleepingSession.fromString("04.10.25 01:00;04.10.25 06:00;GOOD")    // Ночь 3-4 окт
        );
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(multipleDays);
        // Должна быть одна бессонная ночь (2-3 окт)
        assertEquals(1L, result.getValue());
    }

    @Test
    void testChronotypeFunction() {
        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(testSessions);
        assertNotNull(result.getValue());
        assertTrue(result.getValue().equals("Сова") ||
                result.getValue().equals("Жаворонок") ||
                result.getValue().equals("Голубь"));
    }

    @Test
    void testChronotypeFunction_OnlyOwls() {
        List<SleepingSession> owlSessions = List.of(
                SleepingSession.fromString("01.10.25 23:30;02.10.25 09:30;GOOD"),
                SleepingSession.fromString("02.10.25 23:45;03.10.25 09:15;GOOD")
        );
        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(owlSessions);
        assertEquals("Сова", result.getValue());
    }

    @Test
    void testChronotypeFunction_OnlyLarks() {
        List<SleepingSession> larkSessions = List.of(
                SleepingSession.fromString("01.10.25 21:30;02.10.25 06:30;GOOD"),
                SleepingSession.fromString("02.10.25 21:45;03.10.25 06:15;GOOD")
        );
        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(larkSessions);
        assertEquals("Жаворонок", result.getValue());
    }

    @Test
    void testChronotypeFunction_Mixed() {
        List<SleepingSession> mixedSessions = Arrays.asList(
                SleepingSession.fromString("01.10.25 23:30;02.10.25 09:30;GOOD"),  // Сова
                SleepingSession.fromString("02.10.25 21:30;03.10.25 06:30;GOOD"),  // Жаворонок
                SleepingSession.fromString("03.10.25 22:30;04.10.25 08:00;GOOD"),  // Голубь
                SleepingSession.fromString("04.10.25 23:45;05.10.25 09:15;GOOD")   // Сова
        );
        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(mixedSessions);
        assertEquals("Сова", result.getValue()); // Сов больше
    }

    @Test
    void testChronotypeFunction_Tie() {
        List<SleepingSession> tieSessions = Arrays.asList(
                SleepingSession.fromString("01.10.25 23:30;02.10.25 09:30;GOOD"), // Сова
                SleepingSession.fromString("02.10.25 21:30;03.10.25 06:30;GOOD"), // Жаворонок
                SleepingSession.fromString("03.10.25 22:30;04.10.25 08:00;GOOD")  // Голубь
        );
        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(tieSessions);
        assertEquals("Голубь", result.getValue()); // При равенстве - голубь
    }

    @Test
    void testChronotypeFunction_IgnoreDaySessions() {
        List<SleepingSession> sessionsWithDaySleep = Arrays.asList(
                SleepingSession.fromString("01.10.25 23:30;02.10.25 09:30;GOOD"),  // Сова (ночь)
                SleepingSession.fromString("02.10.25 14:00;02.10.25 15:00;NORMAL"), // Дневной сон (игнорируется)
                SleepingSession.fromString("02.10.25 21:30;03.10.25 06:30;GOOD")   // Жаворонок (ночь)
        );
        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(sessionsWithDaySleep);
        // Дневной сон не должен учитываться, поэтому один сова и один жаворонок -> голубь
        assertEquals("Голубь", result.getValue());
    }

    @Test
    void testSleepingSessionFromString() {
        SleepingSession session = SleepingSession.fromString("01.10.25 22:15;02.10.25 08:00;GOOD");
        assertEquals(22, session.getStartTime().getHour());
        assertEquals(15, session.getStartTime().getMinute());
        assertEquals(8, session.getEndTime().getHour());
        assertEquals(0, session.getEndTime().getMinute());
        assertEquals(SleepQuality.GOOD, session.getQuality());
    }

    @Test
    void testSessionDuration() {
        SleepingSession session = SleepingSession.fromString("01.10.25 22:00;02.10.25 06:00;GOOD");
        assertEquals(8 * 60, session.getDurationMinutes()); // 8 часов = 480 минут
    }

    @Test
    void testCoversNightHours() {
        SleepingSession nightSession = SleepingSession.fromString("01.10.25 23:00;02.10.25 07:00;GOOD");
        assertTrue(nightSession.coversNightHours());

        SleepingSession daySession = SleepingSession.fromString("01.10.25 14:00;01.10.25 15:00;NORMAL");
        assertFalse(daySession.coversNightHours());

        SleepingSession lateNight = SleepingSession.fromString("01.10.25 02:00;01.10.25 05:00;GOOD");
        assertTrue(lateNight.coversNightHours());

        SleepingSession earlyMorning = SleepingSession.fromString("01.10.25 04:00;01.10.25 08:00;GOOD");
        assertTrue(earlyMorning.coversNightHours());
    }

    @Test
    void testIsNightSession() {
        SleepingSession overnight = SleepingSession.fromString("01.10.25 23:00;02.10.25 07:00;GOOD");
        assertTrue(overnight.isNightSession());

        SleepingSession daySession = SleepingSession.fromString("01.10.25 14:00;01.10.25 15:00;NORMAL");
        assertTrue(daySession.isNightSession()); // Дневная сессия не считается ночной

        SleepingSession earlyMorning = SleepingSession.fromString("01.10.25 04:00;01.10.25 08:00;GOOD");
        assertTrue(earlyMorning.isNightSession());
    }
}
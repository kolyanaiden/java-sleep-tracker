package ru.yandex.practicum.sleeptracker;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SleepTrackerApp {
    private final List<SleepAnalysisFunction<?>> analysisFunctions = new ArrayList<>();

    public SleepTrackerApp() {
        // Регистрируем все функции анализа
        registerAnalysisFunctions();
    }

    private void registerAnalysisFunctions() {
        analysisFunctions.add(new ru.yandex.practicum.sleeptracker.TotalSessionsFunction());
        analysisFunctions.add(new ru.yandex.practicum.sleeptracker.MinDurationFunction());
        analysisFunctions.add(new ru.yandex.practicum.sleeptracker.MaxDurationFunction());
        analysisFunctions.add(new ru.yandex.practicum.sleeptracker.AverageDurationFunction());
        analysisFunctions.add(new ru.yandex.practicum.sleeptracker.BadQualitySessionsFunction());
        analysisFunctions.add(new ru.yandex.practicum.sleeptracker.SleeplessNightsFunction());
        analysisFunctions.add(new ru.yandex.practicum.sleeptracker.ChronotypeFunction());
    }

    public List<SleepingSession> loadSessionsFromFile(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath))
                .filter(line -> !line.trim().isEmpty())
                .map(SleepingSession::fromString)
                .toList();
    }

    public List<SleepAnalysisResult<?>> analyzeSessions(List<SleepingSession> sessions) {
        List<SleepAnalysisResult<?>> results = new ArrayList<>();
        for (SleepAnalysisFunction<?> function : analysisFunctions) {
            results.add(applyFunction(function, sessions));
        }
        return results;
    }

    private <T> SleepAnalysisResult<T> applyFunction(SleepAnalysisFunction<T> function, List<SleepingSession> sessions) {
        return function.apply(sessions);
    }

    public static void main(String[] args) {
        try {
            SleepTrackerApp app = new SleepTrackerApp();
            List<SleepingSession> sessions;

            if (args.length < 1) {
                // Относительный путь от корня проекта
                String defaultPath = "src/main/resources/sleep_log.txt";
                System.out.println("Используется путь по умолчанию: " + defaultPath);
                sessions = app.loadSessionsFromFile(defaultPath);
            } else {
                sessions = app.loadSessionsFromFile(args[0]);
            }

            System.out.println("Анализ сна пользователя");
            System.out.println("=======================");
            System.out.println("Загружено сессий: " + sessions.size());
            System.out.println();

            List<SleepAnalysisResult<?>> results = app.analyzeSessions(sessions);

            // Вывод результатов в main
            results.stream()
                    .map(SleepAnalysisResult::toString)
                    .forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            System.err.println("Проверьте, существует ли файл по пути: src/main/resources/sleep_log.txt");
            System.err.println("Текущая директория: " + System.getProperty("user.dir"));
        } catch (Exception e) {
            System.err.println("Ошибка при анализе данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
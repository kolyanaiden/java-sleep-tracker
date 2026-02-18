package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analysis.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    private final List<SleepAnalysisFunction<?>> analysisFunctions;

    public SleepTrackerApp() {
        this.analysisFunctions = new ArrayList<>();
        initializeFunctions();
    }

    private void initializeFunctions() {
        // Добавляем все аналитические функции
        analysisFunctions.add(new TotalSessionsFunction());
        analysisFunctions.add(new MinDurationFunction());
        analysisFunctions.add(new MaxDurationFunction());
        analysisFunctions.add(new AvgDurationFunction());
        analysisFunctions.add(new BadQualitySessionsFunction());
        analysisFunctions.add(new SleeplessNightsFunction());
        analysisFunctions.add(new ChronotypeFunction());
    }

    public List<SleepingSession> loadSessionsFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        return Files.lines(path)
                .filter(line -> !line.trim().isEmpty())
                .map(SleepingSession::parseFromString)
                .collect(Collectors.toList());
    }

    public List<SleepAnalysisResult<?>> analyzeSessions(List<SleepingSession> sessions) {
        return analysisFunctions.stream()
                .map(function -> function.apply(sessions))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        try {
            // Определяем путь к файлу
            String filePath;
            if (args.length > 0) {
                filePath = args[0];
            } else {
                // Путь по умолчанию для файла в resources
                filePath = "src/main/resources/sleep_log.txt";
            }

            SleepTrackerApp app = new SleepTrackerApp();

            System.out.println("Загрузка данных о сне из файла: " + filePath);
            List<SleepingSession> sessions = app.loadSessionsFromFile(filePath);

            System.out.println("Загружено сессий сна: " + sessions.size() + "\n");

            System.out.println("Детальная информация по сессиям:");
            sessions.forEach(session -> System.out.println("  " + session));
            System.out.println();

            System.out.println("РЕЗУЛЬТАТЫ АНАЛИЗА СНА:");
            System.out.println("========================");

            List<SleepAnalysisResult<?>> results = app.analyzeSessions(sessions);
            results.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ошибка при анализе данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
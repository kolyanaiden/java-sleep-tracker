package ru.yandex.practicum.sleeptracker.analysis;

public enum Chronotype {
    OWL("Сова"),
    LARK("Жаворонок"),
    PIGEON("Голубь");

    private final String displayName;

    Chronotype(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

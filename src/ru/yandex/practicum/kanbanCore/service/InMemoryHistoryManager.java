package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

    @Override
    public void addToHistory(Task task) {
        history.add(task);
        if (history.size() > 10) {
            history.remove(0);
        }
    }
}
package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int SIZE_OF_HISTORY = 10;
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public LinkedList<Task> getHistory() {
        return history;
    }

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            if (history.size() == SIZE_OF_HISTORY) {
                history.remove(0);
            }
            history.add(task);
        } else {
            System.out.println("Задача не найдена.");
        }
    }
}
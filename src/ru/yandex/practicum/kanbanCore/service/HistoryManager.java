package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addToHistory(Task task);

    void remove (int id);
}
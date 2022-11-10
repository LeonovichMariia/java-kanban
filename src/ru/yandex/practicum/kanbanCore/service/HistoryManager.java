package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.LinkedList;

public interface HistoryManager {
    LinkedList<Task> getHistory();

    void addToHistory(Task task);
}
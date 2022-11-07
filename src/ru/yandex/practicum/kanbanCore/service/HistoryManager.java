package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<Task> getHistory();

    void addToHistory(Task task);
}
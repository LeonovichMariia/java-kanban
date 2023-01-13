package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void updateTask(Task updatedTask);

    void updateEpic(Epic updatedEpic);

    void updateSubtask(Subtask updatedSubtask);

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

    List<Task> getPrioritizedTasks();

    Task findTaskById(int id);

    Subtask findSubtaskById(int id);

    Epic findEpicById(int id);

    List<Task> getHistory();

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    void clearPrioritizedTask();

    void clearAllTasks();

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    int generateId();
}
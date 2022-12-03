package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    int generateId();

    Task findTaskById(int id);

    Subtask findSubtaskById(int id);

    Epic findEpicById(int id);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    void addTask(Task task);

    ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void updateTask(Task updatedTask);

    void updateEpic(Epic updatedEpic);

    void updateSubtask(Subtask updatedSubtask);
}
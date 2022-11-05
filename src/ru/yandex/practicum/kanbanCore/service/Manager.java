package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int idGenerator = 0;

    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Map.Entry<Integer, Task> a : tasks.entrySet()) {
            result.add(a.getValue());
        }
        return result;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> a : subTasks.entrySet()) {
            result.add(a.getValue());
        }
        return result;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        for (Map.Entry<Integer, Epic> a : epics.entrySet()) {
            result.add(a.getValue());
        }
        return result;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubtasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    public void clearEpics() {
        subTasks.clear();
        epics.clear();
    }

    public int generateId() {
        return idGenerator++;
    }

    public Task findTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask findSubtaskById(int id) {
        return subTasks.get(id);
    }

    public Epic findEpicById(int id) {
        return epics.get(id);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        subTasks.remove(id);
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    public void removeEpicById(int id) {
        Epic epic = findEpicById(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subTasks.remove(subtask.getId());
            }
            epics.remove(id);
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        if (epic != null) {
            return epic.getSubtasks();
        } else {
            return null;
        }
    }

    public void addSubtask(Subtask subtask) {
        subTasks.put(subtask.getId(), subtask);
        for (Epic epic : epics.values()) {
            epic.addSubtask(subtask);
        }
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateTask(Task updatedTask) {
        if (updatedTask != null) {
            Task originalTask = findTaskById(updatedTask.getId());
            originalTask.setDescription(updatedTask.getDescription());
            originalTask.setName(updatedTask.getName());
            originalTask.setStatus(updatedTask.getStatus());
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    public void updateEpic(Epic updatedEpic) {
        if (updatedEpic != null) {
            Epic originalEpic = findEpicById(updatedEpic.getId());
            originalEpic.setDescription(updatedEpic.getDescription());
            originalEpic.setName(updatedEpic.getName());
            originalEpic.setSubtasks(updatedEpic.getSubtasks());
            updateEpicStatus(updatedEpic);
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    public void updateSubtask(Subtask updatedSubtask) {
        Subtask originalSubtask = findSubtaskById(updatedSubtask.getId());
        originalSubtask.setDescription(updatedSubtask.getDescription());
        originalSubtask.setName(updatedSubtask.getName());
        if (updatedSubtask.getEpicId() != originalSubtask.getEpicId()) {
            Epic updatedEpic = findEpicById(updatedSubtask.getEpicId());
            Epic originalEpic = findEpicById(originalSubtask.getEpicId());
            originalEpic.getSubtasks().removeIf(i -> i.getId() == originalSubtask.getId());
            originalSubtask.setEpicId(updatedSubtask.getEpicId());
            updateEpicStatus(findEpicById(originalEpic.getId()));
            updateEpicStatus(findEpicById(updatedEpic.getId()));
        }
        originalSubtask.setEpicId(updatedSubtask.getEpicId());
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = epic.getSubtasks();
        int inProgressCounter = 0;
        int doneCounter = 0;
        for (int i = 0; i < subtasks.size(); i++) {
            Subtask subtask = subtasks.get(i);
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                inProgressCounter++;
            } else if (subtask.getStatus() == Status.DONE) {
                doneCounter++;
            }
        }
        if (inProgressCounter == 0 && doneCounter == 0) {
            epic.setStatus(Status.NEW);
        } else if (doneCounter == subtasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
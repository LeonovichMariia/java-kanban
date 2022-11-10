package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idGenerator = 0;

    @Override
    public LinkedList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Map.Entry<Integer, Task> a : tasks.entrySet()) {
            result.add(a.getValue());
        }
        return result;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> a : subtasks.entrySet()) {
            result.add(a.getValue());
        }
        return result;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        for (Map.Entry<Integer, Epic> a : epics.entrySet()) {
            result.add(a.getValue());
        }
        return result;
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void clearEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public int generateId() {
        return idGenerator++;
    }

    @Override
    public Task findTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public Subtask findSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addToHistory(subtask);
        return subtask;
    }

    @Override
    public Epic findEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.addToHistory(epic);
        return epic;
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        subtasks.remove(id);
        for (Epic epic : epics.values()) {
            epic.getSubtasks().removeIf(subtask -> subtask.getId() == id);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        if (epic != null) {
            return epic.getSubtasks();
        } else {
            return null;
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtask(subtask);
            } else {
                System.out.println("Эпик с таким id не найден, подзадача не добавлена!");
            }
        } else {
            System.out.println("Подзадача не найдена.");
        }
        assert subtask != null;
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (updatedTask != null) {
            Task originalTask = tasks.get(updatedTask.getId());
            if (originalTask != null) {
                originalTask.setDescription(updatedTask.getDescription());
                originalTask.setName(updatedTask.getName());
                originalTask.setStatus(updatedTask.getStatus());
            } else {
                System.out.println("Задача не найдена.");
            }
        } else {
            System.out.println("Обновленная задача не найдена.");
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (updatedEpic != null) {
            Epic originalEpic = epics.get(updatedEpic.getId());
            if (originalEpic != null) {
                originalEpic.setDescription(updatedEpic.getDescription());
                originalEpic.setName(updatedEpic.getName());
                originalEpic.setSubtasks(updatedEpic.getSubtasks());
                updateEpicStatus(updatedEpic);
            } else {
                System.out.println("Эпик не найден.");
            }
        } else {
            System.out.println("Обновленный эпик не найден.");
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask != null) {
            Subtask originalSubtask = subtasks.get(updatedSubtask.getId());
            if (originalSubtask != null) {
                originalSubtask.setDescription(updatedSubtask.getDescription());
                originalSubtask.setName(updatedSubtask.getName());
                if (updatedSubtask.getEpicId() != originalSubtask.getEpicId()) {
                    Epic updatedEpic = epics.get(updatedSubtask.getEpicId());
                    if (updatedEpic != null) {
                        Epic originalEpic = epics.get(originalSubtask.getEpicId());
                        if (originalEpic != null) {
                            originalEpic.getSubtasks().removeIf(i -> i.getId() == originalSubtask.getId());
                            originalSubtask.setEpicId(updatedSubtask.getEpicId());
                            updateEpicStatus(epics.get(originalEpic.getId()));
                            updateEpicStatus(epics.get(updatedEpic.getId()));
                        } else {
                            System.out.println("Эпик не найден.");
                        }
                    } else {
                        System.out.println("Обновленный эпик не найден.");
                    }
                }
                originalSubtask.setEpicId(updatedSubtask.getEpicId());
            } else {
                System.out.println("Подзадача не найдена.");
            }
        } else {
            System.out.println("Обновленная подзадача не найдена.");
        }
    }

    private void updateEpicStatus(Epic epic) {
        if (epic != null) {
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
}
package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idGenerator = 0;
    private final TreeSet<Task> prioritizedTaskSet = new TreeSet<>(Comparator.nullsLast(Comparator.comparing(Task::getStartTime)).
            thenComparing(Task::getId));

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTaskSet);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
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

    public void clearPrioritizedTask() {
        if (!prioritizedTaskSet.isEmpty()) {
            prioritizedTaskSet.clear();
        } else {
            throw new IllegalArgumentException("Список задач в порядке приоритета пуст: ничего не удалено!");
        }
    }

    public void clearAllTasks() {
        if ((!tasks.isEmpty()) && (!subtasks.isEmpty()) && (!epics.isEmpty()) && (!prioritizedTaskSet.isEmpty())) {
            tasks.clear();
            subtasks.clear();
            epics.clear();
            prioritizedTaskSet.clear();
        } else {
            throw new IllegalArgumentException("Списки задач пусты: ничего не удалено!");
        }
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTaskSet.remove(task);
        }
        if (!tasks.isEmpty()) {
            tasks.clear();
        } else {
            throw new IllegalArgumentException("Список задач пуст: ничего не удалено!");
        }
    }

    @Override
    public void clearSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTaskSet.remove(subtask);
        }
        if (!subtasks.isEmpty()) {
            subtasks.clear();
            for (Epic epic : epics.values()) {
                epic.getSubtasks().clear();
                historyManager.remove(epic.getId());
                updateEpicStatus(epic);
            }
        } else {
            throw new IllegalArgumentException("Список подзадач пуст: ничего не удалено!");
        }
    }

    @Override
    public void clearEpics() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTaskSet.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        if (!epics.isEmpty()) {
            epics.clear();
        } else {
            throw new IllegalArgumentException("Список эпиков пуст: ничего не удалено!");
        }
    }

    @Override
    public int generateId() {
        return idGenerator++;
    }

    @Override
    public Task findTaskById(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Некорректный ввод id");
        } else if (!tasks.isEmpty()) {
            Task task = tasks.get(id);
            historyManager.addToHistory(task);
            return task;
        } else {
            throw new IllegalArgumentException("Список задач пуст");
        }
    }

    @Override
    public Subtask findSubtaskById(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Некорректный ввод id");
        } else if (!subtasks.isEmpty()) {
            Subtask subtask = subtasks.get(id);
            historyManager.addToHistory(subtask);
            return subtask;
        } else {
            throw new IllegalArgumentException("Список подзадач пуст");
        }
    }

    @Override
    public Epic findEpicById(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Некорректный ввод id");
        } else if (!epics.isEmpty()) {
            Epic epic = epics.get(id);
            historyManager.addToHistory(epic);
            return epic;
        } else {
            throw new IllegalArgumentException("Список эпиков пуст");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (!tasks.isEmpty()) {
            if (id < 0) {
                throw new IllegalArgumentException("Некорректный ввод id");
            }
            if (tasks.get(id) != null) {
                prioritizedTaskSet.remove(tasks.get(id));
                tasks.remove(id);
                historyManager.remove(id);
            } else {
                System.out.println("Задача с таким id не найдена");
            }
            System.out.println("Задача удалена");
        } else {
            throw new IllegalArgumentException("Удаление по id невозможно, список задач пуст");
        }
    }


    @Override
    public void removeSubtaskById(int id) {
        if (!subtasks.isEmpty()) {
            if (id < 0) {
                throw new IllegalArgumentException("Некорректный ввод id");
            }
            if (subtasks.get(id) != null) {
                prioritizedTaskSet.remove(subtasks.get(id));
                subtasks.remove(id);
                historyManager.remove(id);
                for (Epic epic : epics.values()) {
                    epic.getSubtasks().removeIf(subtask -> subtask.getId() == id);
                    historyManager.remove(id);
                    updateEpicStatus(epic);
                }
            } else {
                System.out.println("Подзадача с таким id не найдена");
            }
            System.out.println("Подзадача удалена");
        } else {
            throw new IllegalArgumentException("Удаление по id невозможно, список подзадач пуст");
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (!epics.isEmpty()) {
            if (id < 0) {
                throw new IllegalArgumentException("Некорректный ввод id");
            }
            Epic epic = epics.get(id);
            if (epic != null) {
                for (Subtask subtask : epic.getSubtasks()) {
                    subtasks.remove(subtask.getId());
                    historyManager.remove(id);
                }
                epics.remove(id);
                historyManager.remove(id);
            } else {
                System.out.println("Эпик не найден.");
            }
        } else {
            throw new IllegalArgumentException("Удаление по id невозможно, список эпиков пуст");
        }
    }


    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (task.getId() < 0) {
                throw new IllegalArgumentException("Некорректный ввод id");
            } else if (isCrossing(task)) {
                System.out.println("Пересечение задач! " + task);
                return;
            }
            tasks.put(task.getId(), task);
            prioritizedTaskSet.add(task);
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    private boolean isCrossing(Task task) {
        for (Task t : prioritizedTaskSet) {
            if (task.getStartTime().isAfter(t.getStartTime()) && task.getStartTime().isBefore(t.getEndTime())) {
                return true;
            } else if (task.getEndTime().isAfter(t.getStartTime()) && task.getEndTime().isBefore(t.getEndTime())) {
                return true;
            } else if (task.getStartTime().equals(t.getStartTime()) || task.getEndTime().equals(t.getEndTime())) {
                return true;
            }
        }
        return false;
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
            if (subtask.getId() < 0) {
                throw new IllegalArgumentException("Некорректный ввод id");
            } else if (isCrossing(subtask)) {
                System.out.println("Пересечение задач! " + subtask);
                return;
            }
            subtasks.put(subtask.getId(), subtask);
            prioritizedTaskSet.add(subtask);
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
            if (epic.getId() < 0) {
                throw new IllegalArgumentException("Некорректный ввод id");
            }
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (!tasks.isEmpty()) {
            if (updatedTask != null) {
                if (updatedTask.getId() < 0) {
                    throw new IllegalArgumentException("Некорректный ввод id");
                }
                if (isCrossing(updatedTask)) {
                    System.out.println("Пересечение задач! " + updatedTask);
                    return;
                }
                Task originalTask = tasks.get(updatedTask.getId());
                if (originalTask != null) {
                    originalTask.setDescription(updatedTask.getDescription());
                    originalTask.setName(updatedTask.getName());
                    originalTask.setStatus(updatedTask.getStatus());
                    originalTask.setStartTime(updatedTask.getStartTime());
                    originalTask.setDuration(updatedTask.getDuration());
                } else {
                    System.out.println("Задача не найдена.");
                }
            } else {
                System.out.println("Обновленная задача не найдена.");
            }
        } else {
            throw new IllegalArgumentException("Обновление невозможно: список задач пуст");
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (!epics.isEmpty()) {
            if (updatedEpic != null) {
                if (updatedEpic.getId() < 0) {
                    throw new IllegalArgumentException("Некорректный ввод id");
                }
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
        } else {
            throw new IllegalArgumentException("Обновление невозможно: список эпиков пуст");
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (!subtasks.isEmpty()) {
            if (updatedSubtask != null) {
                if (updatedSubtask.getId() < 0) {
                    throw new IllegalArgumentException("Некорректный ввод id");
                }
                if (isCrossing(updatedSubtask)) {
                    System.out.println("Пересечение задач! " + updatedSubtask);
                    return;
                }
                Subtask originalSubtask = subtasks.get(updatedSubtask.getId());
                if (originalSubtask != null) {
                    originalSubtask.setDescription(updatedSubtask.getDescription());
                    originalSubtask.setName(updatedSubtask.getName());
                    originalSubtask.setStatus(updatedSubtask.getStatus());
                    originalSubtask.setStartTime(updatedSubtask.getStartTime());
                    originalSubtask.setDuration(updatedSubtask.getDuration());
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
        } else {
            throw new IllegalArgumentException("Обновление невозможно: список подзадач пуст");
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
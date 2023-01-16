package ru.yandex.practicum.kanbanCore.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.server.KVTaskClient;
import ru.yandex.practicum.kanbanCore.service.FileBackedTasksManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
    public KVTaskClient kvTaskClient;
    public final String TASKS_KEY = "TASKS";
    public final String HISTORY_KEY = "HISTORY";
    public final String SUBTASKS_KEY = "SUBTASKS";
    public final String EPICS_KEY = "EPICS";
    private final List<Task> tasks = new ArrayList<>();
    private final List<Subtask> subtasks = new ArrayList<>();
    private final List<Epic> epics = new ArrayList<>();
    private final List<Integer> history = new ArrayList<>();
    private final TreeSet<Task> prioritizedTaskSet = new TreeSet<>(Comparator.nullsLast(Comparator.comparing(Task::getStartTime)).
            thenComparing(Task::getId));
    Gson gson = new Gson();

    public HttpTaskManager() throws IOException, InterruptedException {
        super(new File("resources/tasks"));
        kvTaskClient = new KVTaskClient();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        tasks.add(task);
        kvTaskClient.put(TASKS_KEY, gson.toJson(tasks));
        history.add(task.getId());
        kvTaskClient.put(HISTORY_KEY, gson.toJson(history));
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        try {
            subtasks.add(subtask);
            kvTaskClient.put(SUBTASKS_KEY, gson.toJson(subtasks));
            history.add(subtask.getId());
            kvTaskClient.put(HISTORY_KEY, gson.toJson(history));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        epics.add(epic);
        kvTaskClient.put(EPICS_KEY, gson.toJson(epics));
        history.add(epic.getId());
        kvTaskClient.put(HISTORY_KEY, gson.toJson(history));
    }

    public ArrayList<Task> getTasks() {
        try {
            return gson.fromJson(kvTaskClient.load(TASKS_KEY), new TypeToken<ArrayList<Task>>() {}.getType());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        try {
            return gson.fromJson(kvTaskClient.load(SUBTASKS_KEY), new TypeToken<ArrayList<Task>>() {}.getType());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<Epic> getEpics() {
        try {
            return gson.fromJson(kvTaskClient.load(EPICS_KEY), new TypeToken<ArrayList<Epic>>() {}.getType());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Task> getPrioritizedTasks() {
        try {
            List<Task> tasksPrioritized = gson.fromJson(kvTaskClient.load(TASKS_KEY), new TypeToken<ArrayList<Task>>() {}.getType());
            prioritizedTaskSet.addAll(tasksPrioritized);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            List<Task> subtasksPrioritized = gson.fromJson(kvTaskClient.load(SUBTASKS_KEY), new TypeToken<ArrayList<Task>>() {}.getType());
            prioritizedTaskSet.addAll(subtasksPrioritized);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(prioritizedTaskSet);
    }

    @Override
    public Task findTaskById(int id) {
        try {
            super.findTaskById(id);
            if (kvTaskClient.load(TASKS_KEY) != null) {
                List<Task> findTask = gson.fromJson(kvTaskClient.load(TASKS_KEY), new TypeToken<ArrayList<Task>>() {
                }.getType());
                for (Task task : findTask) {
                    if (task.getId() == id) {
                        history.add(id);
                        kvTaskClient.put(HISTORY_KEY, gson.toJson(history));
                        return task;
                    } else {
                        System.out.println("Задача с id" + id + " не найдена");
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Subtask findSubtaskById(int id) {
        try {
            super.findSubtaskById(id);
            List<Task> findSub = gson.fromJson(kvTaskClient.load(SUBTASKS_KEY), new TypeToken<ArrayList<Task>>() {}.getType());
            for (Task task : findSub) {
                if (task.getId() == id) {
                    history.add(id);
                    kvTaskClient.put(HISTORY_KEY, gson.toJson(history));
                    return (Subtask) task;
                } else {
                    System.out.println("Подзадача с id" + id + " не найдена");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Epic findEpicById(int id) {
        try {
            super.findEpicById(id);
            List<Epic> findEpic = gson.fromJson(kvTaskClient.load(EPICS_KEY), new TypeToken<ArrayList<Epic>>() {}.getType());
            for (Epic epic : findEpic) {
                if (epic.getId() == id) {
                    history.add(id);
                    kvTaskClient.put(HISTORY_KEY, gson.toJson(history));
                    return epic;
                } else {
                    System.out.println("Эпик с id" + id + " не найден");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        try {
            List<Task> taskRemoved = gson.fromJson(kvTaskClient.load(TASKS_KEY), new TypeToken<ArrayList<Task>>() {}.getType());
            taskRemoved.removeIf(task -> (Objects.equals(task.getId(), id)));
            kvTaskClient.put(TASKS_KEY, gson.toJson(taskRemoved));
            removeFromHistory(id);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        try {
            List<Subtask> subtaskRemoved = gson.fromJson(kvTaskClient.load(SUBTASKS_KEY), new TypeToken<ArrayList<Subtask>>() {}.getType());
            subtaskRemoved.removeIf(subtask -> (Objects.equals(subtask.getId(), id)));
            kvTaskClient.put(SUBTASKS_KEY, gson.toJson(subtaskRemoved));
            removeFromHistory(id);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        try {
            List<Epic> epicRemoved = gson.fromJson(kvTaskClient.load(EPICS_KEY), new TypeToken<ArrayList<Epic>>() {}.getType());
            epicRemoved.removeIf(epic -> (Objects.equals(epic.getId(), id)));
            kvTaskClient.put(SUBTASKS_KEY, gson.toJson(epicRemoved));
            removeFromHistory(id);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void removeFromHistory(int id) {
        try {
            List<Integer> history = gson.fromJson(kvTaskClient.load(HISTORY_KEY), new TypeToken<ArrayList<Integer>>() {}.getType());
            history.removeIf(history::contains);
            kvTaskClient.put(HISTORY_KEY, gson.toJson(history));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        List<Task> emptyTasks = new ArrayList<>();
        kvTaskClient.put(TASKS_KEY, gson.toJson(emptyTasks));
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        List<Task> emptySubtasks = new ArrayList<>();
        kvTaskClient.put(SUBTASKS_KEY, gson.toJson(emptySubtasks));
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        List<Epic> emptyEpics = new ArrayList<>();
        kvTaskClient.put(EPICS_KEY, gson.toJson(emptyEpics));
    }

    @Override
    public List<Task> getHistory() {
        try {
            return gson.fromJson(kvTaskClient.load(HISTORY_KEY), new TypeToken<ArrayList<Integer>>() {}.getType());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}

package ru.yandex.practicum.kanbanCore.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.entity.TaskType;
import ru.yandex.practicum.kanbanCore.server.KVTaskClient;
import ru.yandex.practicum.kanbanCore.server.adapters.JsonAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    public KVTaskClient kvTaskClient;
    public final String TASKS_KEY = "tasks";
    public final String SUBTASKS_KEY = "subtasks";
    public final String EPICS_KEY = "epics";
    public final String HISTORY_KEY = "history";

    private Gson gson = JsonAdapter.getDefaultGson();

    public HttpTaskManager() {
        super(new File("resources/tasks"));
        kvTaskClient = new KVTaskClient("http://localhost:8080");
    }

    private void saveToServer() {
        kvTaskClient.put(TASKS_KEY, gson.toJson(tasks));
        kvTaskClient.put(SUBTASKS_KEY, gson.toJson(subtasks));
        kvTaskClient.put(EPICS_KEY, gson.toJson(epics));
        kvTaskClient.put(HISTORY_KEY, gson.toJson(getHistory()));
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        saveToServer();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        saveToServer();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        saveToServer();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        saveToServer();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        saveToServer();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        saveToServer();
    }

    @Override
    public Task findTaskById(int id) {
        Task task = super.findTaskById(id);
        saveToServer();
        return task;
    }

    @Override
    public Epic findEpicById(int id) {
        Epic epic = super.findEpicById(id);
        saveToServer();
        return epic;
    }

    @Override
    public Subtask findSubtaskById(int id) {
        Subtask subtask = super.findSubtaskById(id);
        saveToServer();
        return subtask;
    }

    public void load() {
        try {
            tasks = gson.fromJson(kvTaskClient.load(TASKS_KEY), new TypeToken<Map<Integer, Task>>() {}.getType());
            for (Task task : getTasks()) {
                super.addTask(task);
            }
        } catch (NullPointerException e) {
            System.out.println("Список задач пуст.");
        }
        try {
            epics = gson.fromJson(kvTaskClient.load(EPICS_KEY), new TypeToken<Map<Integer, Epic>>() {}.getType());
            for (Epic epic : getEpics()) {
                super.addEpic(epic);
            }
        } catch (NullPointerException e) {
            System.out.println("Список эпиков пуст.");
        }
        try {
            subtasks = gson.fromJson(kvTaskClient.load(SUBTASKS_KEY), new TypeToken<Map<Integer, Subtask>>() {}.getType());
            for (Subtask subtask : getSubtasks()) {
                super.addSubtask(subtask);
            }
        } catch (NullPointerException e) {
            System.out.println("Список подзадач пуст.");
        }
        try {
            ArrayList<Task> history = gson.fromJson(kvTaskClient.load(HISTORY_KEY), new TypeToken<ArrayList<Task>>() {}.getType());
            for (Task task : history) {
                if (task.getTaskType() == TaskType.TASK) {
                    findTaskById(task.getId());
                } else if (task.getTaskType() == TaskType.EPIC) {
                    findSubtaskById(task.getId());
                } else findSubtaskById(task.getId());
            }
        } catch (NullPointerException e) {
            System.out.println("История задач пуста.");
        }
        this.prioritizedTaskSet.addAll(tasks.values());
        this.prioritizedTaskSet.addAll(subtasks.values());
    }
}
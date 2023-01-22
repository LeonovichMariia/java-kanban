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
import java.util.List;
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

    public void save() {
        kvTaskClient.put(TASKS_KEY, gson.toJson(tasks));
        kvTaskClient.put(SUBTASKS_KEY, gson.toJson(subtasks));
        kvTaskClient.put(EPICS_KEY, gson.toJson(epics));
        List<Task> tasks = historyManager.getHistory();
        List<Integer> ids = new ArrayList<>();
        for (Task task: tasks) {
            int taskId = task.getId();
            ids.add(taskId);
        }
        kvTaskClient.put(HISTORY_KEY, gson.toJson(ids));
    }

    public void load() {
        try {
            tasks = gson.fromJson(kvTaskClient.load(TASKS_KEY), new TypeToken<Map<Integer, Task>>() {}.getType());
            for (Task task : getTasks()) {
                this.addTask(task);
            }
        } catch (NullPointerException e) {
            System.out.println("Список задач пуст.");
        }
        try {
            epics = gson.fromJson(kvTaskClient.load(EPICS_KEY), new TypeToken<Map<Integer, Epic>>() {}.getType());
            for (Epic epic : getEpics()) {
                this.addEpic(epic);
            }
        } catch (NullPointerException e) {
            System.out.println("Список эпиков пуст.");
        }
        try {
            subtasks = gson.fromJson(kvTaskClient.load(SUBTASKS_KEY), new TypeToken<Map<Integer, Subtask>>() {}.getType());
            for (Subtask subtask : getSubtasks()) {
                this.addSubtask(subtask);
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
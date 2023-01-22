package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.server.KVServer;
import ru.yandex.practicum.kanbanCore.server.KVTaskClient;
import ru.yandex.practicum.kanbanCore.server.adapters.JsonAdapter;
import ru.yandex.practicum.kanbanCore.service.HttpTaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer kvServer;
    private static KVTaskClient kvTaskClient;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            throw new RuntimeException("KVServer не запущен", e.getCause());
        }
        kvTaskClient = new KVTaskClient("http://localhost:8080");
        taskManager = new HttpTaskManager();
        gson = JsonAdapter.getDefaultGson();
    }

    @AfterEach
    void stopServer() {
        kvServer.stop();
    }

    @Test
    public void loadTest() {
        Epic epic = new Epic(taskManager.generateId(), Status.NEW, "Epic description", "Epic name");
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task1 description", "Task1 name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic.getId(), LocalDateTime.of(2022, 12, 31, 15, 20), 20);

        taskManager.addSubtask(subtask);
        taskManager.addTask(task);
        taskManager.addEpic(epic);

        List<Epic> expectedEpics = taskManager.getEpics();
        List<Task> expectedTask = taskManager.getTasks();
        List<Subtask> expectedSubtask = taskManager.getSubtasks();


        taskManager = new HttpTaskManager();
        taskManager.load();

        assertEquals(expectedTask, taskManager.getTasks());
        assertEquals(expectedSubtask, taskManager.getSubtasks());
        assertEquals(expectedEpics, taskManager.getEpics());
    }

    @Test
    public void shouldSaveTasks() {
        Task task1 = new Task(taskManager.generateId(), Status.NEW, "Task1 description", "Task1 name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 30), 20);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        String responseFromKVServer = kvTaskClient.load("tasks");
        JsonObject jsonObject = JsonParser.parseString(responseFromKVServer).getAsJsonObject();
        Task task1FromKVServer = gson.fromJson(jsonObject.get(String.valueOf(0)), Task.class);
        Task task2FromKVServer = gson.fromJson(jsonObject.get(String.valueOf(1)), Task.class);
        assertEquals(task1, task1FromKVServer);
        assertEquals(task2, task2FromKVServer);
    }

    @Test
    public void shouldSaveSubtasks() {
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", 0, LocalDateTime.of(2022, 12, 31, 15, 20), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", 0, LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        String responseFromKVServer = kvTaskClient.load("subtasks");
        JsonObject jsonObject = JsonParser.parseString(responseFromKVServer).getAsJsonObject();
        Subtask subtask11FromKVServer = gson.fromJson(jsonObject.get(String.valueOf(0)), Subtask.class);
        Subtask subtask12FromKVServer = gson.fromJson(jsonObject.get(String.valueOf(1)), Subtask.class);
        assertEquals(subtask11, subtask11FromKVServer);
        assertEquals(subtask12, subtask12FromKVServer);
    }

    @Test
    public void shouldSaveEpics() {
        Epic epic = new Epic(taskManager.generateId(), Status.NEW, "Epic description", "Epic name");
        Epic epic1 = new Epic(taskManager.generateId(), Status.DONE, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic);
        taskManager.addEpic(epic1);
        String responseFromKVServer = kvTaskClient.load("epics");
        JsonObject jsonObject = JsonParser.parseString(responseFromKVServer).getAsJsonObject();
        Epic epicFromKVServer = gson.fromJson(jsonObject.get(String.valueOf(0)), Epic.class);
        Epic epic1FromKVServer = gson.fromJson(jsonObject.get(String.valueOf(1)), Epic.class);
        assertEquals(epic, epicFromKVServer);
        assertEquals(epic1, epic1FromKVServer);
    }

    @Test
    public void shouldUpdateTaskInKVServerWhenUpdatedTaskInTaskManager() {
        Task task1 = new Task(taskManager.generateId(), Status.NEW, "Task1 description", "Task1 name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task1);
        String newName = "New name";
        task1.setName(newName);
        taskManager.updateTask(task1);
        String responseFromKVServer = kvTaskClient.load("tasks");
        JsonObject jsonObject = JsonParser.parseString(responseFromKVServer).getAsJsonObject();
        Task fromKVServer = gson.fromJson(jsonObject.get(String.valueOf(0)), Task.class);
        assertEquals(newName, fromKVServer.getName());
    }

    @Test
    public void shouldUpdateSubtaskInKVServerWhenUpdatedSubtaskInTaskManager() {
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", 0, LocalDateTime.of(2022, 12, 31, 15, 20), 20);
        taskManager.addSubtask(subtask11);
        String newName = "New name";
        subtask11.setName(newName);
        taskManager.updateSubtask(subtask11);
        String responseFromKVServer = kvTaskClient.load("subtasks");
        JsonObject jsonObject = JsonParser.parseString(responseFromKVServer).getAsJsonObject();
        Subtask fromKVServer = gson.fromJson(jsonObject.get(String.valueOf(0)), Subtask.class);
        assertEquals(newName, fromKVServer.getName());
    }

    @Test
    public void shouldUpdateEpicInKVServerWhenUpdatedEpicInTaskManager() {
        Epic epic = new Epic(taskManager.generateId(), Status.NEW, "Epic description", "Epic name");
        taskManager.addEpic(epic);
        String newName = "New name";
        epic.setName(newName);
        taskManager.updateEpic(epic);
        String responseFromKVServer = kvTaskClient.load("epics");
        JsonObject jsonObject = JsonParser.parseString(responseFromKVServer).getAsJsonObject();
        Epic fromKVServer = gson.fromJson(jsonObject.get(String.valueOf(0)), Epic.class);
        assertEquals(fromKVServer.getName(), newName);
    }

    @Test
    public void shouldHaveHistoryOnKVServer() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 20), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task1 description", "Task1 name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);


        taskManager.findTaskById(3);
        taskManager.findSubtaskById(1);
        taskManager.findSubtaskById(2);
        taskManager.findEpicById(0);
        String responseFromKVServer = kvTaskClient.load("history");
        JsonArray jsonArray = JsonParser.parseString(responseFromKVServer).getAsJsonArray();
        Subtask subtask11FromJson = gson.fromJson(jsonArray.get(1), Subtask.class);
        Task TaskFromJson = gson.fromJson(jsonArray.get(0), Task.class);
        Epic epicFromJson = gson.fromJson(jsonArray.get(3), Epic.class);
        assertEquals(subtask11, subtask11FromJson);
        assertEquals(task, TaskFromJson);
        assertEquals(epic1, epicFromJson);
    }
}
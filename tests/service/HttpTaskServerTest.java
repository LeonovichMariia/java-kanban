package service;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.server.HttpTaskServer;
import ru.yandex.practicum.kanbanCore.server.KVServer;
import ru.yandex.practicum.kanbanCore.server.adapters.JsonAdapter;
import ru.yandex.practicum.kanbanCore.service.InMemoryTaskManager;
import ru.yandex.practicum.kanbanCore.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static java.net.http.HttpRequest.newBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = JsonAdapter.getDefaultGson();
    private TaskManager taskManager = new InMemoryTaskManager();
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void beforeEach() throws IOException {
        httpTaskServer = new HttpTaskServer(taskManager);
        task = new Task(1, Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        epic = new Epic(5, Status.NEW, "Epic1 description", "Epic1 name");
        subtask = new Subtask(8, Status.NEW, "Subtask11 description",
                "Subtask11 name", 5, LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addTask(task);
        taskManager.addSubtask(subtask);
        httpTaskServer.start();
    }

    @AfterEach
    public void cleanup() {
        httpTaskServer.stop();
    }

    @Test
    public void getPrioritizedTasksTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());
        ArrayList<Task> priority= new ArrayList<>();
        priority.add(task);
        priority.add(subtask);
        String actualString = gson.toJson(priority);
        assertEquals(200, emptyResponse.statusCode());
        assertEquals(actualString, emptyResponse.body());
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/history");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("[]", emptyResponse.body());
    }

    @Test
    public void getTasksTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/task");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());
        String taskExp = "[\n  {\n    \"name\": \"Task name\",\n    \"description\": \"Task description\",\n    \"id\": 1,\n    \"status\": \"NEW\",\n    \"taskType\": \"TASK\",\n    \"startTime\": \"31.12.2022, 15:00\",\n    \"duration\": 20\n  }\n]";
        String taskExpToJson = gson.toJson(taskExp);
        assertEquals(200, emptyResponse.statusCode());
        assertEquals(taskExpToJson, gson.toJson(emptyResponse.body()));
    }

    @Test
    public void getTasksByIdTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8085/tasks/task/?id=1");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("", response.body());

        Task testTask = new Task(1, Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        String taskToJson =gson.toJson(testTask);
        URI uri = URI.create("http://localhost:8085/tasks/task/?id=1");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> getResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertEquals(taskToJson, getResponse.body());
    }

    @Test
    public void postTasksTest() throws IOException, InterruptedException {
        Task task = new Task(0, Status.NEW, "Task description", "Task name",
                LocalDateTime.now(), 20);
        URI url = URI.create("http://localhost:8085/tasks/task/");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
    }

    @Test
    public void postTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task(1, Status.NEW, "Task description", "Task name",
                LocalDateTime.now(), 20);
        URI url = URI.create("http://localhost:8085/tasks/task/?id=1");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
    }

    @Test
    public void deleteTasksTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/task");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }

    @Test
    public void deleteTasksByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/task/?id=1");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }

    @Test
    public void getEpicTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/epic");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/epic/?id=1");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }

    @Test
    public void postEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic(1, Status.NEW, "Epic description", "Epic name");
        URI url = URI.create("http://localhost:8085/tasks/epic");
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
    }

    @Test
    public void postEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic(1, Status.NEW, "Epic description", "Epic name");
        URI url = URI.create("http://localhost:8085/tasks/epic?id=1");
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
    }

    @Test
    public void deleteEpicTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/epic/");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }

    @Test
    public void deleteEpicByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/epic?id=1");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }

    @Test
    public void getSubtaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/subtask");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }

    @Test
    public void getSubtaskByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/subtask/?id=1");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }

    @Test
    public void postSubtaskTest() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(1, Status.NEW, "Subtask description",
                "Subtask name", 0, LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        URI url = URI.create("http://localhost:8085/tasks/subtask");
        String json = gson.toJson(subtask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
    }

    @Test
    public void postSubtaskByIdTest() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(1, Status.NEW, "Subtask description",
                "Subtask name", 0, LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        URI url = URI.create("http://localhost:8085/tasks/subtask?id=1");
        String json = gson.toJson(subtask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
    }

    @Test
    public void deleteSubtaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/subtask");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }

    @Test
    public void deleteSubtaskByIdTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8085/tasks/subtask/?id=1");
        HttpRequest getEmptyRequest = newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> emptyResponse = httpClient.send(getEmptyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, emptyResponse.statusCode());
        assertEquals("", emptyResponse.body());
    }
}
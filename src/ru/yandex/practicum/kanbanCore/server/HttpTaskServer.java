package ru.yandex.practicum.kanbanCore.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.server.adapters.JsonAdapter;
import ru.yandex.practicum.kanbanCore.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8085;
    private static final Charset DEFAULT_CHARSET = UTF_8;
    private TaskManager taskManager;
    private final HttpServer httpServer;
    private Gson gson = JsonAdapter.getDefaultGson();

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", this::prioritizedTaskHandler);
        httpServer.createContext("/tasks/task", this::tasksHandler);
        httpServer.createContext("/tasks/subtask", this::subtasksHandler);
        httpServer.createContext("/tasks/epic", this::epicsHandler);
        httpServer.createContext("/tasks/history", this::historyHandler);
    }

    public void prioritizedTaskHandler(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n/tasks/");
            if (httpExchange.getRequestMethod().equals("GET")) {
                if (taskManager.getPrioritizedTasks().isEmpty()) {
                    System.out.println("Список задач в порядке приоритета пуст!");
                }
                writeResponse(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()));
            } else {
                System.out.println("/tasks/ ждет запрос GET, а получил " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    public void tasksHandler(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String query = httpExchange.getRequestURI().toString();
        try {
            System.out.println("\n/tasks/task");
            switch (method) {
                case "GET":
                    if (!query.contains("id")) {
                        writeResponse(httpExchange, gson.toJson(taskManager.getTasks()));
                        System.out.println("Выведен список всех задач");
                        return;
                    } else {
                        String taskIdString = query.split("=")[1];
                        int taskIdFin = Integer.parseInt(taskIdString);
                        Task task = taskManager.findTaskById(taskIdFin);
                        writeResponse(httpExchange, gson.toJson(task));
                        System.out.println("Задача по ключу " + taskIdFin);
                    }
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    JsonElement jsonElement = JsonParser.parseString(jsonString);
                    if (!jsonElement.isJsonObject()) {
                        throw new RuntimeException("Это не jsonObject");
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Task task = gson.fromJson(jsonObject, Task.class);
                    List<Task> taskMap = taskManager.getTasks();
                    if (httpExchange.getRequestURI().toString().equals("/tasks/task/")) {
                        if (taskMap.contains(task)) {
                            taskManager.updateTask(task);
                        } else {
                            taskManager.addTask(task);
                        }
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                    httpExchange.close();
                    break;
                case "DELETE":
                    if (!query.contains("id")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        List<Task> removedTasks = taskManager.getTasks();
                        removedTasks.clear();
                        taskManager.clearTasks();
                        writeResponse(httpExchange, gson.toJson(removedTasks));
                        System.out.println("Все задачи удалены");
                        return;
                    } else {
                        int taskId = Integer.parseInt(query.split("=")[1]);
                        httpExchange.sendResponseHeaders(200, 0);
                        task = taskManager.findTaskById(taskId);
                        taskManager.removeSubtaskById(task.getId());
                        writeResponse(httpExchange, gson.toJson(task));
                        System.out.println("Задача по ключу " + taskId + " удалена");
                    }
                    break;
                default:
                    System.out.println("Некорректный запрос: " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    public void subtasksHandler(HttpExchange httpExchange) throws IOException {
        int taskId = 0;
        String method = httpExchange.getRequestMethod();
        String query = httpExchange.getRequestURI().toString();
        try {
            System.out.println("\n/tasks/subtask");
            switch (method) {
                case "GET":
                    if (!query.contains("id")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        List<Subtask> subtasks = taskManager.getSubtasks();
                        writeResponse(httpExchange, gson.toJson(subtasks));
                        System.out.println("Выведен список всех подзадач");
                        return;
                    } else {
                        taskId = Integer.parseInt(query.split("=")[1]);
                        httpExchange.sendResponseHeaders(200, 0);
                        Subtask subtask = taskManager.findSubtaskById(taskId);
                        writeResponse(httpExchange, gson.toJson(subtask));
                        System.out.println("Подадача по ключу " + taskId);
                    }
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    JsonElement jsonElement = JsonParser.parseString(jsonString);
                    if (!jsonElement.isJsonObject()) {
                        throw new RuntimeException("Это не jsonObject");
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Subtask subtask = gson.fromJson(jsonObject, Subtask.class);
                    ArrayList<Subtask> subtaskMap = taskManager.getSubtasks();
                    if (httpExchange.getRequestURI().toString().equals("/tasks/subtask/")) {
                        if (subtaskMap.contains(subtask)) {
                            taskManager.updateSubtask(subtask);
                        } else {
                            taskManager.addSubtask(subtask);
                        }
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                    httpExchange.close();
                    break;
                case "DELETE":
                    if (!query.contains("id")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        List<Subtask> removedSubtasks = taskManager.getSubtasks();
                        removedSubtasks.clear();
                        taskManager.clearSubtasks();
                        writeResponse(httpExchange, gson.toJson(removedSubtasks));
                        System.out.println("Все подзадачи удалены");
                        return;
                    } else {
                        taskId = Integer.parseInt(query.split("=")[1]);
                        httpExchange.sendResponseHeaders(200, 0);
                        subtask = taskManager.findSubtaskById(taskId);
                        taskManager.removeSubtaskById(subtask.getId());
                        writeResponse(httpExchange, gson.toJson(subtask));
                        System.out.println("Подзадача по ключу " + taskId + " удалена");
                    }
                    break;
                default:
                    System.out.println("Некорректный запрос: " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    public void epicsHandler(HttpExchange httpExchange) throws IOException {
        int taskId = 0;
        String method = httpExchange.getRequestMethod();
        String query = httpExchange.getRequestURI().toString();
        try {
            System.out.println("\n/tasks/epic");
            switch (method) {
                case "GET":
                    if (!query.contains("id")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        List<Epic> epics = taskManager.getEpics();
                        writeResponse(httpExchange, gson.toJson(epics));
                        System.out.println("Выведен список всех эпиков");
                        return;
                    } else {
                        taskId = Integer.parseInt(query.split("=")[1]);
                        httpExchange.sendResponseHeaders(200, 0);
                        Epic epic = taskManager.findEpicById(taskId);
                        writeResponse(httpExchange, gson.toJson(epic));
                        System.out.println("Эпик по ключу " + taskId);
                    }
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    JsonElement jsonElement = JsonParser.parseString(jsonString);
                    if (!jsonElement.isJsonObject()) {
                        throw new RuntimeException("Это не jsonObject");
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Epic epic = gson.fromJson(jsonObject, Epic.class);
                    ArrayList<Epic> epicMap = taskManager.getEpics();
                    if (httpExchange.getRequestURI().toString().equals("/tasks/epic/")) {
                        if (epicMap.contains(epic)) {
                            taskManager.updateEpic(epic);
                        } else {
                            taskManager.addEpic(epic);
                        }
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                    httpExchange.close();
                    break;
                case "DELETE":
                    if (!query.contains("id")) {
                        httpExchange.sendResponseHeaders(200, 0);
                        List<Epic> removedEpics = taskManager.getEpics();
                        removedEpics.clear();
                        taskManager.clearEpics();
                        writeResponse(httpExchange, gson.toJson(removedEpics));
                        System.out.println("Все эпики удалены");
                        return;
                    } else {
                        taskId = Integer.parseInt(query.split("=")[1]);
                        httpExchange.sendResponseHeaders(200, 0);
                        epic = taskManager.findEpicById(taskId);
                        taskManager.removeEpicById(epic.getId());
                        writeResponse(httpExchange, gson.toJson(epic));
                        System.out.println("Эпик по ключу " + taskId + " удален");
                    }
                    break;
                default:
                    System.out.println("Некорректный запрос: " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    public void historyHandler(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n/tasks/history");
            if (httpExchange.getRequestMethod().equals("GET")) {
                if (taskManager.getHistory().isEmpty()) {
                    System.out.println("История пуста!");
                }
                writeResponse(httpExchange, gson.toJson(taskManager.getHistory()));
            } else {
                System.out.println("/tasks/ ждет запрос GET. Ваш запрос: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    private void writeResponse(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, response.length);
        h.getResponseBody().write(response);
    }

    public void start() {
        System.out.println("Запускаем HTTP-сервер на порту " + PORT);
        httpServer.start();
        System.out.println("Сервер успешно запущен.");
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
    }

    public void stop() {
        System.out.println("Останавливаем HTTP-сервер на порту  " + PORT + " порту!");
        System.out.println("Сервер успешно остановлен.");
        httpServer.stop(1);
    }
}
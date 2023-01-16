package ru.yandex.practicum.kanbanCore.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.Managers;
import ru.yandex.practicum.kanbanCore.service.TaskManager;

import java.beans.Introspector;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8083;
    private static final Charset DEFAULT_CHARSET = UTF_8;
    private TaskManager taskManager;
    private final HttpServer httpServer;
    private Gson gson;

    public HttpTaskServer() throws IOException, InterruptedException {
        taskManager = Managers.getDefaultHttp();
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
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
            if (endpoint == Endpoint.GET) {
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
        try {
            System.out.println("\n/tasks/task");
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
            String query = httpExchange.getRequestURI().getQuery();
            int taskId = Integer.parseInt(query.split("=")[1]);
            switch (endpoint) {
                case GET:
                    if (query.isEmpty()) {
                        httpExchange.sendResponseHeaders(200, 0);
                        List<Task> tasks = taskManager.getTasks();
                        writeResponse(httpExchange, gson.toJson(tasks));
                        System.out.println("Выведен список всех задач");
                        return;
                    } else {
                        httpExchange.sendResponseHeaders(200, 0);
                        Task task = taskManager.findTaskById(taskId);
                        writeResponse(httpExchange, gson.toJson(task));
                        System.out.println("Задача по ключу " + taskId);
                    }
                    break;
                case POST:
                    if (query.isEmpty()) {
                        System.out.println("Key для сохранения пустой. key указывается в пути: /tasks/task/?id={key}");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    String value = readText(httpExchange);
                    if (value.isEmpty()) {
                        System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    taskManager.addTask(gson.fromJson(value, Task.class));
                    System.out.println("Задача по ключу " + taskId + " обновлена");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                case DELETE:
                    if (query.isEmpty()) {
                        taskManager.clearTasks();
                        System.out.println("Все задачи успешно удалены.");
                        httpExchange.sendResponseHeaders(200, 0);
                        return;
                    }
                    taskManager.removeTaskById(taskId);
                    System.out.println("Задача по ключу " + taskId + " удалена");
                    httpExchange.sendResponseHeaders(200, 0);
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
        try {
            System.out.println("\n/tasks/subtask");
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
            String query = httpExchange.getRequestURI().getQuery();
            int taskId = Integer.parseInt(query.split("=")[1]);
            switch (endpoint) {
                case GET:
                    if (query.isEmpty()) {
                        httpExchange.sendResponseHeaders(200, 0);
                        List<Subtask> subtasks = taskManager.getSubtasks();
                        writeResponse(httpExchange, gson.toJson(subtasks));
                        System.out.println("Выведен список всех подзадач");
                        return;
                    } else {
                        httpExchange.sendResponseHeaders(200, 0);
                        Subtask subtask = taskManager.findSubtaskById(taskId);
                        writeResponse(httpExchange, gson.toJson(subtask));
                        System.out.println("Подадача по ключу " + taskId);
                    }
                    break;
                case POST:
                    if (query.isEmpty()) {
                        System.out.println("Key для сохранения пустой. key указывается в пути: /tasks/subtask/?id={key}");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    String value = readText(httpExchange);
                    if (value.isEmpty()) {
                        System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    taskManager.addSubtask(gson.fromJson(value, Subtask.class));
                    System.out.println("Подзадача по ключу " + taskId + " обновлена");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                case DELETE:
                    if (query.isEmpty()) {
                        taskManager.clearSubtasks();
                        System.out.println("Все подзадачи успешно удалены.");
                        httpExchange.sendResponseHeaders(200, 0);
                        return;
                    }
                    taskManager.removeSubtaskById(taskId);
                    System.out.println("Подзадача по ключу " + taskId + " удалена");
                    httpExchange.sendResponseHeaders(200, 0);
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
        try {
            System.out.println("\n/tasks/epic");
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
            String query = httpExchange.getRequestURI().getQuery();
            int taskId = Integer.parseInt(query.split("=")[1]);
            switch (endpoint) {
                case GET:
                    if (query.isEmpty()) {
                        httpExchange.sendResponseHeaders(200, 0);
                        List<Epic> epics = taskManager.getEpics();
                        writeResponse(httpExchange, gson.toJson(epics));
                        System.out.println("Выведен список всех эпиков");
                        return;
                    } else {
                        httpExchange.sendResponseHeaders(200, 0);
                        Epic epic = taskManager.findEpicById(taskId);
                        writeResponse(httpExchange, gson.toJson(epic));
                        System.out.println("Эпик по ключу " + taskId);
                    }
                    break;
                case POST:
                    if (query.isEmpty()) {
                        System.out.println("Key для сохранения пустой. key указывается в пути: /tasks/epic/?id={key}");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    String value = readText(httpExchange);
                    if (value.isEmpty()) {
                        System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    taskManager.addEpic(gson.fromJson(value, Epic.class));
                    System.out.println("Эпик по ключу " + taskId + " обновлена");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                case DELETE:
                    if (query.isEmpty()) {
                        taskManager.clearEpics();
                        System.out.println("Все эпики успешно удалены.");
                        httpExchange.sendResponseHeaders(200, 0);
                        return;
                    }
                    taskManager.removeEpicById(taskId);
                    System.out.println("Эпик по ключу " + taskId + " удален");
                    httpExchange.sendResponseHeaders(200, 0);
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
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
            if (endpoint == Endpoint.GET) {
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

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            switch (requestMethod) {
                case "GET":
                    return Endpoint.GET;
                case "POST":
                case "DELETE":
                    System.out.println("Такого эндпоинта не существует");
                    break;
            }
        }
        if (pathParts.length == 4 && pathParts[1].equals("tasks") && pathParts[3].equals("task")) {
            switch (requestMethod) {
                case "GET":
                    return Endpoint.GET;
                case "POST":
                    return Endpoint.POST;
                case "DELETE":
                    return Endpoint.DELETE;
            }
        }
        if (pathParts.length == 4 && pathParts[1].equals("tasks") && pathParts[3].equals("subtask")) {
            switch (requestMethod) {
                case "GET":
                    return Endpoint.GET;
                case "POST":
                    return Endpoint.POST;
                case "DELETE":
                    return Endpoint.DELETE;
            }
        }
        if (pathParts.length == 4 && pathParts[1].equals("tasks") && pathParts[3].equals("epic")) {
            switch (requestMethod) {
                case "GET":
                    return Endpoint.GET;
                case "POST":
                    return Endpoint.POST;
                case "DELETE":
                    return Endpoint.DELETE;
            }
        }
        if (pathParts.length == 4 && pathParts[1].equals("tasks") && pathParts[3].equals("history")) {
            switch (requestMethod) {
                case "GET":
                    return Endpoint.GET;
                case "POST":
                case "DELETE":
                    System.out.println("Такого эндпоинта не существует");
                    break;
            }
        }
        return Endpoint.UNKNOWN;
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
        System.out.println("Сервер успешно запущен.");
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }

    public void stop() {
        System.out.println("Останавливаем HTTP-сервер на порту  " + PORT + " порту!");
        System.out.println("Сервер успешно остановлен.");
        httpServer.stop(1);
    }
}
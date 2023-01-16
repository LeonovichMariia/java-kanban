package service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.server.KVServer;
import ru.yandex.practicum.kanbanCore.service.HttpTaskManager;
import ru.yandex.practicum.kanbanCore.service.Managers;
import ru.yandex.practicum.kanbanCore.service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    static HttpTaskManager manager;

    static {
        try {
            manager = new HttpTaskManager();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static TaskManager taskManager;
    static KVServer server;
    private Task task, task2, task3;
    private Epic epic1, epic2;
    private Subtask subtask11, subtask12, subtask21, subtask22;

    @BeforeAll
    static void startServer() throws IOException {
        taskManager = Managers.getDefault();
        server = new KVServer();
        server.start();
    }

    @BeforeEach
    public void setUp() {
        task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 14, 0), 20);
        task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 14, 40), 20);
        task3 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 10), 20);

        epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        epic2 = new Epic(taskManager.generateId(), Status.NEW, "Epic2 description", "Epic2 name");

        subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        subtask21 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 17, 0), 20);
        subtask22 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 17, 30), 20);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void shouldAddTask() {
        manager.addTask(task);
        Task savedTask = manager.findTaskById(0);
        assertEquals(savedTask, task);
        assertEquals(2, manager.getHistory().size(), "История пуста");
        manager.removeTaskById(task.getId());
        assertNull(manager.findTaskById(task.getId()));
        assertEquals(0, manager.getHistory().size());
    }
}
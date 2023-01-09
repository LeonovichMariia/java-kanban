package ru.yandex.practicum.kanbanCore.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    public InMemoryTaskManagerTest() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldClearAllTasks() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 20), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 50), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addEpic(epic1);
        assertTrue((taskManager.getPrioritizedTasks().size() == 4), "Количество задач = 4");
        assertTrue((taskManager.getTasks().size() == 2), "Количество задач = 2");
        assertTrue((taskManager.getEpics().size() == 1), "Количество задач = 1");
        assertTrue((taskManager.getSubtasks().size() == 2), "Количество задач = 2");
        taskManager.clearAllTasks();
        assertTrue((taskManager.getPrioritizedTasks().size() == 0), "Количество задач = 0");
        assertTrue((taskManager.getTasks().size() == 0), "Количество задач = 0");
        assertTrue((taskManager.getEpics().size() == 0), "Количество задач = 0");
        assertTrue((taskManager.getSubtasks().size() == 0), "Количество задач = 0");
    }

    @Test
    void shouldClearPrioritizedTasks() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 20), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 50), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        assertTrue((taskManager.getPrioritizedTasks().size() == 4), "Количество задач = 4");
        taskManager.clearPrioritizedTask();
        assertTrue((taskManager.getPrioritizedTasks().size() == 0), "Количество задач = 0");
    }
}
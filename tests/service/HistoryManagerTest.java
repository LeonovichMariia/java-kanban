package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.HistoryManager;
import ru.yandex.practicum.kanbanCore.service.Managers;
import ru.yandex.practicum.kanbanCore.service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    public HistoryManager historyManager;
    public TaskManager taskManager;
    private Task task;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        task3 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 16, 10), 20);
    }

    @Test
    public void shouldAddOneTaskInHistory() {
        historyManager.addToHistory(task);
        List<Task> history = new ArrayList<>();
        history.add(task);
        assertNotNull(history, "История не пустая.");
        assertEquals(history, historyManager.getHistory(), "История не пустая.");
    }

    @Test //Пустая история задач
    public void shouldBeEmptyHistoryList() {
        List<Task> taskHistory = historyManager.getHistory();
        assertTrue(taskHistory.isEmpty(), "Список истории задач пуст.");
    }

    @Test //Дублирование
    public void shouldNotDoubleTaskHistory() {
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        List<Task> taskHistory = new ArrayList<>();
        taskHistory.add(task);
        assertEquals(taskHistory, historyManager.getHistory(), "Повторные записи отсуствуют");
    }

    @Test //Проверка на удаление из начала списка истории задач
    public void shouldRemoveFromHistoryListFirst() {
        historyManager.addToHistory(task);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.remove(task.getId());
        List<Task> taskHistory = new ArrayList<>();
        taskHistory.add(task);
        taskHistory.add(task2);
        taskHistory.add(task3);
        taskHistory.remove(task);

        assertEquals(taskHistory, historyManager.getHistory(), "Удалена первая задача");
    }

    @Test //Проверка на удаление из середины списка истории задач
    public void shouldRemoveFromHistoryListMiddle() {
        historyManager.addToHistory(task);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.remove(task2.getId());
        List<Task> taskHistory = new ArrayList<>();
        taskHistory.add(task);
        taskHistory.add(task2);
        taskHistory.add(task3);
        taskHistory.remove(task2);

        assertEquals(taskHistory, historyManager.getHistory(), "Удалена вторая задача");
    }

    @Test // Проверка на удаление из конца списка истории задач
    public void shouldRemoveFromHistoryListLast() {
        historyManager.addToHistory(task);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.remove(task3.getId());
        List<Task> taskHistory = new ArrayList<>();
        taskHistory.add(task);
        taskHistory.add(task2);
        taskHistory.add(task3);
        taskHistory.remove(task3);

        assertEquals(taskHistory, historyManager.getHistory(), "Удалена последняя задача");
    }

    @Test
    void shouldReturnHistory() {
        assertEquals(0, historyManager.getHistory().size());
        historyManager.addToHistory(task);
        historyManager.addToHistory(task2);
        List<Task> history = new ArrayList<>();
        history.add(task);
        history.add(task2);
        assertNotNull(history, "История не пустая.");
        assertEquals(history, historyManager.getHistory(), "Количество записей в истории: 2");
    }
}
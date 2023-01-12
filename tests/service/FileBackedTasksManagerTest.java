package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.exceptions.ManagerLoadException;
import ru.yandex.practicum.kanbanCore.service.FileBackedTasksManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(new File("resources/emptyFile"));
    }

    @Test // Пустой список задач.
    public void shouldThrowExceptionWhenLoadFromEmptyFile() {
        taskManager = new FileBackedTasksManager(new File("resources/emptyFile"));
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        taskManager.clearTasks();
        ManagerLoadException ex = Assertions.assertThrows(
                ManagerLoadException.class,
                () -> {
                    File file = new File("resources/emptyFile");
                    FileBackedTasksManager tasksFromFile = FileBackedTasksManager.loadFromFile(file);
                });
        assertEquals("Файл пуст: загрузка данных невозможна!", ex.getMessage());
    }

    @Test
    public void shouldSaveEmptyList() { // Пустой список задач.
        taskManager = new FileBackedTasksManager(new File("resources/emptyFile"));
        List<Task> tasks = new ArrayList<>();
        assertEquals(taskManager.getTasks(), tasks);
        taskManager.save();
        File file = new File("resources/emptyFile");
        assertNotEquals(0, file.length(), "Пустой список задач");
    }

    @Test
    void shouldBeEpicWithoutSubtasks() { // Эпик без подзадач.
        taskManager = new FileBackedTasksManager(new File("resources/emptyFile"));
        Epic epic = new Epic(taskManager.generateId(), Status.NEW, "Epic description", "Epic name");
        taskManager.addEpic(epic);
        FileBackedTasksManager.loadFromFile(new File("resources/emptyFile"));
        List<Epic> epics = new ArrayList<>();
        epics.add(epic);
        List<Subtask> subtasks = new ArrayList<>();
        assertEquals(taskManager.getEpics(), epics);
        assertEquals(taskManager.getSubtasksOfEpic(epic), subtasks);
    }

    @Test
    void shouldLoadWithEmptyHistoryList() { // Пустой список истории.
        FileBackedTasksManager.loadFromFile(new File("resources/emptyHistory"));
        assertTrue(taskManager.getHistory().isEmpty());
    }
}
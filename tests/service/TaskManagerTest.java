package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void subtaskShouldHaveEpic() { // наличие эпика у подзадачи
        Epic epic = new Epic(taskManager.generateId(), Status.NEW, "Epic description", "Epic name");
        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask1 description",
                "Subtask1 name", epic.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        assertFalse(subtask1.getEpicId() != 0);
    }

    @Test
    void shouldReturnEpicStatus() {
        Epic epic0 = new Epic(taskManager.generateId(), Status.NEW, "Epic0 description", "Epic0 name");
        Epic epic = new Epic(taskManager.generateId(), epic0.getStatus(), "Epic description", "Epic name");
        Epic epic1 = new Epic(taskManager.generateId(), epic0.getStatus(), "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        Epic epic2 = new Epic(taskManager.generateId(), epic0.getStatus(), "Epic2 description", "Epic2 name");
        Subtask subtask21 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask21 description",
                "Subtask21 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 16, 20), 20);
        Subtask subtask22 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask22 description",
                "Subtask22 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 16, 50), 20);
        Epic epic3 = new Epic(taskManager.generateId(), epic0.getStatus(), "Epic3 description", "Epic3 name");
        Subtask subtask31 = new Subtask(taskManager.generateId(), Status.DONE, "Subtask31 description",
                "Subtask31 name", epic3.getId(), LocalDateTime.of(2022, 12, 31, 17, 20), 20);
        Subtask subtask32 = new Subtask(taskManager.generateId(), Status.DONE, "Subtask32 description",
                "Subtask32 name", epic3.getId(), LocalDateTime.of(2022, 12, 31, 17, 50), 20);
        taskManager.addEpic(epic);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addSubtask(subtask21);
        taskManager.addSubtask(subtask22);
        taskManager.addSubtask(subtask31);
        taskManager.addSubtask(subtask32);
        Status epicStatus = epic.getStatus();
        Status epic1Status = epic1.getStatus();
        Status epic2Status = epic2.getStatus();
        Status epic3Status = epic3.getStatus();
        assertEquals(epicStatus, Status.NEW, "Статус пустого эпика - NEW");
        assertEquals(epic1Status, Status.NEW, "Статус нового эпика - NEW");
        assertEquals(epic2Status, Status.IN_PROGRESS, "Статус незавершенного эпика - IN_PROGRESS");
        assertEquals(epic3Status, Status.DONE, "Статус завершенного эпика корректен - DONE");
    }

    //Стандартное поведение
    @Test
    void shouldAddTasks() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        assertTrue(taskManager.getTasks().contains(task));
        assertTrue(taskManager.getPrioritizedTasks().contains(task));
    }

    @Test
    void shouldAddEpics() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        assertTrue(taskManager.getEpics().contains(epic1));
    }

    @Test
    void shouldAddSubtasks() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addSubtask(subtask11);
        assertTrue(taskManager.getSubtasks().contains(subtask11));
        assertTrue(taskManager.getPrioritizedTasks().contains(subtask11));
    }

    @Test
    void shouldGetTasks() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 30), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        Epic epic2 = new Epic(taskManager.generateId(), Status.IN_PROGRESS, "Epic2 description", "Epic2 name");
        Subtask subtask21 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask21 description",
                "Subtask21 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 16, 50), 20);
        Subtask subtask22 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask22 description",
                "Subtask22 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 17, 30), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addSubtask(subtask21);
        taskManager.addSubtask(subtask22);
        assertTrue((taskManager.getTasks().size() == 2), "Количество задач = 2");
        assertTrue((taskManager.getEpics().size() == 2), "Количество эпиков = 2");
        assertTrue((taskManager.getSubtasks().size() == 4), "Количество подзадач = 4");
        assertTrue((taskManager.getPrioritizedTasks().size() == 6), "Количество подзадач = 6");
    }

    @Test
    void shouldRemoveAllTasks() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 30), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        Epic epic2 = new Epic(taskManager.generateId(), Status.IN_PROGRESS, "Epic2 description", "Epic2 name");
        Subtask subtask21 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask21 description",
                "Subtask21 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 16, 50), 20);
        Subtask subtask22 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask22 description",
                "Subtask22 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 17, 30), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addSubtask(subtask21);
        taskManager.addSubtask(subtask22);
        taskManager.clearAllTasks();
        assertTrue(taskManager.getTasks().isEmpty(), "Количество задач = 0");
        assertTrue(taskManager.getEpics().isEmpty(), "Количество эпиков = 0");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Количество подзадач = 0");
        assertTrue((taskManager.getPrioritizedTasks().isEmpty()), "Количество подзадач = 0");
    }

    @Test
    void shouldFindAnyTaskById() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addTask(task);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask11);
        assertEquals(taskManager.findTaskById(task.getId()), task);
        assertEquals(taskManager.findEpicById(epic1.getId()), epic1);
        assertEquals(taskManager.findSubtaskById(subtask11.getId()), subtask11);
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        Task updatedTask = new Task(task.getId(), Status.DONE, task.getDescription(), task.getName(),
                task.getStartTime(), task.getDuration());
        taskManager.updateTask(updatedTask);
        assertEquals(updatedTask, taskManager.getTasks().get(0));
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Epic updatedEpic1 = new Epic(epic1.getId(), Status.DONE, epic1.getDescription(), epic1.getName());
        taskManager.updateEpic(updatedEpic1);
        assertEquals(updatedEpic1, taskManager.getEpics().get(0));
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Epic updatedEpic1 = new Epic(taskManager.generateId(), Status.DONE, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        taskManager.updateEpic(updatedEpic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addSubtask(subtask11);
        epic1.addSubtask(subtask11);
        Subtask updatedSubtask11 = new Subtask(subtask11.getId(), Status.DONE, subtask11.getDescription(),
                subtask11.getName(), epic1.getId(), subtask11.getStartTime(), subtask11.getDuration());
        taskManager.updateSubtask(updatedSubtask11);
        assertEquals(updatedSubtask11, taskManager.getSubtasks().get(0));
    }

    @Test
    void shouldDeleteTasksById() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addTask(task);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask11);
        taskManager.removeTaskById(task.getId());
        taskManager.removeEpicById(epic1.getId());
        taskManager.removeSubtaskById(subtask11.getId());

        assertFalse(taskManager.getTasks().contains(task));
        assertFalse(taskManager.getEpics().contains(epic1));
        assertFalse(taskManager.getSubtasks().contains(subtask11));
    }

    @Test
    void shouldGetHistory() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.findTaskById(task.getId());
        taskManager.findTaskById(task2.getId());

        assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    void shouldGetId() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask11);

        assertEquals(0, task.getId());
        assertEquals(1, epic1.getId());
        assertEquals(2, subtask11.getId());
    }

    @Test
    void shouldGetSubtasksOfEpic() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        epic1.addSubtask(subtask11);
        epic1.addSubtask(subtask12);
        ArrayList<Subtask> subtasksOfEpic = taskManager.getSubtasksOfEpic(epic1);
        assertEquals(2, subtasksOfEpic.size());
    }

    //Пустой список задач.
    @Test
    void shouldHaveNoTasksWhenEmptyList() {
        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldRemoveTaskWhenEmptyList() {
        taskManager.removeTaskById(1);
        taskManager.removeSubtaskById(1);
        taskManager.removeEpicById(1);
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void shouldNotGetHistory() {
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void shouldNotUpdateAnyTask() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task updatedTask = new Task(task.getId(), Status.DONE, task.getDescription(), task.getName(),
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.updateTask(updatedTask);
        assertNotEquals(updatedTask, task);

        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Epic updatedEpic1 = new Epic(epic1.getId(), Status.DONE, epic1.getDescription(), epic1.getName());
        taskManager.updateEpic(updatedEpic1);
        assertNotEquals(updatedEpic1, epic1);

        Epic epic2 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic2);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        Subtask updatedSubtask11 = new Subtask(subtask11.getId(), Status.DONE, subtask11.getDescription(),
                subtask11.getName(), epic2.getId(), LocalDateTime.of(2022, 12, 31, 17, 0), 20);
        taskManager.updateSubtask(updatedSubtask11);
        assertNotEquals(updatedSubtask11, subtask11);
    }

    @Test
    void shouldRemoveTasksWhenEmptyList() {
        taskManager.clearTasks();
        taskManager.clearEpics();
        taskManager.getSubtasks();

        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    //Неверный идентификатор задачи.
    @Test
    void shouldNotUpdateTasks() { //??
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        Task updatedTask = new Task(5, task.getStatus(), task.getDescription(), task.getName(), task.getStartTime(),
                30);
        taskManager.updateTask(updatedTask);
        assertNotEquals(task, updatedTask);

        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Epic updatedEpic1 = new Epic(5, Status.DONE, epic1.getDescription(), epic1.getName());
        taskManager.updateEpic(updatedEpic1);
        assertNotEquals(epic1, updatedEpic1);

        Epic epic2 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask11);
        Subtask updatedSubtask11 = new Subtask(5, Status.DONE, subtask11.getDescription(),
                subtask11.getName(), epic2.getId(), subtask11.getStartTime(), 20);
        taskManager.updateSubtask(updatedSubtask11);
        assertNotEquals(subtask11, updatedSubtask11);
    }

    @Test
    void shouldNotDeleteTasksById() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addTask(task);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask11);
        taskManager.removeTaskById(5);
        taskManager.removeEpicById(6);
        taskManager.removeSubtaskById(7);
        assertTrue(taskManager.getTasks().contains(task));
        assertTrue(taskManager.getEpics().contains(epic1));
        assertTrue(taskManager.getSubtasks().contains(subtask11));
    }

    @Test
    void shouldNotFindTasksById() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addTask(task);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask11);
        assertNull(taskManager.findTaskById(8));
        assertNull(taskManager.findEpicById(8));
        assertNull(taskManager.findSubtaskById(8));
    }

    @Test
    void shouldBeZeroTimeOfEpicWithoutSubtasks() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        assertNull(epic1.getStartTime());
        assertEquals(0, epic1.getDuration());
        assertNull(epic1.getEndTime());
    }

    @Test
    void shouldHaveTimeOfEpicWithSubtasks() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 20), 20);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        assertEquals(epic1.getStartTime(), LocalDateTime.of(2022, 12, 31, 15, 0));
        assertEquals(40, epic1.getDuration());
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2022, 12, 31, 15, 40));
    }

    @Test
    void shouldHaveTimeOfEpicWhenOneSubtaskDeleted() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 20), 20);
        epic1.addSubtask(subtask11);
        epic1.addSubtask(subtask12);
        taskManager.removeSubtaskById(subtask12.getId());

        assertEquals(epic1.getStartTime(), LocalDateTime.of(2022, 12, 31, 15, 0));
        assertEquals(20, epic1.getDuration());
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2022, 12, 31, 15, 20));
    }

    @Test
    void shouldHaveTimeOfEpicWhenAllSubtasksDeleted() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.removeSubtaskById(subtask12.getId());
        taskManager.removeSubtaskById(subtask11.getId());

        assertNull(epic1.getStartTime());
        assertEquals(0, epic1.getDuration());
        assertNull(epic1.getEndTime());
    }

    @Test
    void shouldHaveTimeOfEpicWhenSubtasksUpdated() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 20, 30), 20);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        Subtask updatedSubtask11 = new Subtask(subtask11.getId(), subtask11.getStatus(), subtask11.getDescription(),
                subtask11.getName(), subtask11.getEpicId(), subtask11.getStartTime(), subtask11.getDuration());
        Subtask updatedSubtask12 = new Subtask(subtask12.getId(), subtask12.getStatus(), subtask12.getDescription(),
                subtask12.getName(), subtask12.getEpicId(), LocalDateTime.of(2022, 12, 31, 16, 30), 40);
        taskManager.updateSubtask(updatedSubtask11);
        taskManager.updateSubtask(updatedSubtask12);

        assertEquals(epic1.getStartTime(), LocalDateTime.of(2022, 12, 31, 15, 0));
        assertEquals(130, epic1.getDuration());
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2022, 12, 31, 17, 10));
    }

    @Test
    void shouldHaveTasksPrioritized() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        Task task3 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 16, 10), 20);
        taskManager.addTask(task3);
        taskManager.addTask(task);
        taskManager.addTask(task2);

        assertEquals(taskManager.getPrioritizedTasks().get(0), task);
        assertEquals(taskManager.getPrioritizedTasks().get(2), task3);
    }

    @Test
    void shouldHaveCrossingTasks() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 10), 20);
        Task task3 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addTask(task3);
        taskManager.addTask(task);
        taskManager.addTask(task2);

        assertTrue(taskManager.isCrossing(task2));
        assertTrue(taskManager.getPrioritizedTasks().contains(task3));
    }
}
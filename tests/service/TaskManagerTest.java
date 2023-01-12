package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task2);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask11);
        subtasks.add(subtask12);
        subtasks.add(subtask21);
        subtasks.add(subtask22);
        List<Epic> epics = new ArrayList<>();
        epics.add(epic1);
        epics.add(epic2);
        List<Task> prioritizedTasks = new ArrayList<>();
        prioritizedTasks.add(task);
        prioritizedTasks.add(task2);
        prioritizedTasks.add(subtask11);
        prioritizedTasks.add(subtask12);
        prioritizedTasks.add(subtask21);
        prioritizedTasks.add(subtask22);
        assertTrue((taskManager.getTasks().size() == 2), "Количество задач = 2");
        assertEquals(taskManager.getTasks(), tasks, "Количество задач = 2");
        assertTrue((taskManager.getEpics().size() == 2), "Количество эпиков = 2");
        assertEquals(taskManager.getEpics(), epics, "Количество эпиков = 2");
        assertTrue((taskManager.getSubtasks().size() == 4), "Количество подзадач = 4");
        assertEquals(taskManager.getSubtasks(), subtasks, "Количество подзадач = 4");
        assertTrue((taskManager.getPrioritizedTasks().size() == 6), "Количество подзадач = 6");
        assertEquals(taskManager.getPrioritizedTasks(), prioritizedTasks, "Количество подзадач = 6");
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
        Task updatedTask = new Task(task.getId(), Status.DONE, "New task description", task.getName(),
                task.getStartTime(), task.getDuration());
        taskManager.updateTask(updatedTask);
        assertEquals(updatedTask, taskManager.getTasks().get(0));
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Epic updatedEpic1 = new Epic(epic1.getId(), Status.DONE, "New epic description", epic1.getName());
        taskManager.updateEpic(updatedEpic1);
        assertEquals(updatedEpic1, taskManager.getEpics().get(0));
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addSubtask(subtask11);
        epic1.addSubtask(subtask11);
        Subtask updatedSubtask11 = new Subtask(subtask11.getId(), Status.DONE, "New subtask11 description",
                subtask11.getName(), epic1.getId(), subtask11.getStartTime(), subtask11.getDuration());
        taskManager.updateSubtask(updatedSubtask11);
        assertEquals(updatedSubtask11, taskManager.getSubtasks().get(0));
    }

    @Test
    void shouldRemoveTaskById() {
        Task task = new Task(taskManager.generateId(), Status.DONE, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.removeTaskById(task.getId());

        assertFalse(taskManager.getTasks().contains(task));
        assertTrue(taskManager.getTasks().contains(task2));
    }

    @Test
    void shouldRemoveEpicById() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Epic epic2 = new Epic(taskManager.generateId(), Status.DONE, "Epic2 description", "Epic2 name");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.removeEpicById(epic1.getId());

        assertFalse(taskManager.getEpics().contains(epic1));
        assertTrue(taskManager.getEpics().contains(epic2));
    }

    @Test
    void shouldRemoveSubtaskById() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.DONE, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.removeSubtaskById(subtask11.getId());

        assertFalse(taskManager.getSubtasks().contains(subtask11));
        assertTrue(taskManager.getSubtasks().contains(subtask12));
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
        List<Task> tasksHistoryList = new ArrayList<>();
        tasksHistoryList.add(task);
        tasksHistoryList.add(task2);

        assertEquals(tasksHistoryList, taskManager.getHistory());
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
        assertEquals(taskManager.getSubtasksOfEpic(epic1), subtasksOfEpic);
    }

    @Test
    void shouldSetStartTime() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addSubtask(subtask11);
        task.setStartTime(LocalDateTime.of(2023, 1, 1, 17, 30));
        subtask11.setStartTime(LocalDateTime.of(2023, 1, 1, 18, 30));

        assertEquals(task.getStartTime(), LocalDateTime.of(2023, 1, 1, 17, 30));
        assertEquals(subtask11.getStartTime(), LocalDateTime.of(2023, 1, 1, 18, 30));
    }

    @Test
    void shouldSetDuration() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addSubtask(subtask11);
        task.setDuration(5);
        subtask11.setDuration(10);

        assertEquals(task.getDuration(), 5);
        assertEquals(subtask11.getDuration(), 10);
    }

    @Test
    void shouldReturnEndTime() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 20), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);

        assertEquals(task.getEndTime(), LocalDateTime.of(2022, 12, 31, 15, 20));
        assertEquals(subtask11.getEndTime(), LocalDateTime.of(2022, 12, 31, 15, 40));
        assertEquals(subtask12.getEndTime(), LocalDateTime.of(2022, 12, 31, 16, 0));
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2022, 12, 31, 16, 0));
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
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task2);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask11);
        subtasks.add(subtask12);
        List<Epic> epics = new ArrayList<>();
        epics.add(epic1);
        List<Task> prioritizedTasks = new ArrayList<>();
        prioritizedTasks.add(task);
        prioritizedTasks.add(task2);
        prioritizedTasks.add(subtask11);
        prioritizedTasks.add(subtask12);
        assertTrue((taskManager.getTasks().size() == 2), "Количество задач = 2");
        assertEquals(taskManager.getTasks(), tasks, "Количество задач = 2");
        assertTrue((taskManager.getEpics().size() == 1), "Количество эпиков = 1");
        assertEquals(taskManager.getEpics(), epics, "Количество эпиков = 1");
        assertTrue((taskManager.getSubtasks().size() == 2), "Количество подзадач = 2");
        assertEquals(taskManager.getSubtasks(), subtasks, "Количество подзадач = 4");
        assertTrue((taskManager.getPrioritizedTasks().size() == 4), "Количество подзадач = 4");
        assertEquals(taskManager.getPrioritizedTasks(), prioritizedTasks, "Количество подзадач = 4");
        taskManager.clearAllTasks();
        tasks.clear();
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
        assertTrue((taskManager.getPrioritizedTasks().size() == 0), "Количество задач = 0");
        assertEquals(prioritizedTasks, taskManager.getPrioritizedTasks());
        assertTrue((taskManager.getTasks().size() == 0), "Количество задач = 0");
        assertEquals(tasks, taskManager.getTasks());
        assertTrue((taskManager.getEpics().size() == 0), "Количество задач = 0");
        assertEquals(epics, taskManager.getEpics());
        assertTrue((taskManager.getSubtasks().size() == 0), "Количество задач = 0");
        assertEquals(subtasks, taskManager.getSubtasks());
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
        List<Task> prioritizedTasks = new ArrayList<>();
        prioritizedTasks.add(task);
        prioritizedTasks.add(task2);
        prioritizedTasks.add(subtask11);
        prioritizedTasks.add(subtask12);
        assertTrue((taskManager.getPrioritizedTasks().size() == 4), "Количество задач = 4");
        assertEquals(taskManager.getPrioritizedTasks(), prioritizedTasks);
        taskManager.clearPrioritizedTask();
        prioritizedTasks.clear();
        assertTrue((taskManager.getPrioritizedTasks().size() == 0), "Количество задач = 0");
        assertEquals(taskManager.getPrioritizedTasks(), prioritizedTasks);
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
        assertEquals(40, epic1.getDuration());
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2022, 12, 31, 15, 40));
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
                subtask12.getName(), subtask12.getEpicId(), LocalDateTime.of(2022, 12, 31, 16, 30), 30);
        taskManager.updateSubtask(updatedSubtask11);
        taskManager.updateSubtask(updatedSubtask12);

        assertEquals(epic1.getStartTime(), LocalDateTime.of(2022, 12, 31, 15, 0));
        assertEquals(50, epic1.getDuration());
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2022, 12, 31, 17, 0));
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

    //Пустой список задач.
    @Test
    void shouldNotGetTasksWhenEmptyList() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        assertTrue(taskManager.getSubtasksOfEpic(epic1).isEmpty());
    }

    @Test
    void shouldRemoveAllTasksByIdWhenEmptyList() {
        taskManager.removeTaskById(1);
        taskManager.removeSubtaskById(1);
        taskManager.removeEpicById(1);
        List<Task> tasks = new ArrayList<>();
        List<Task> epics = new ArrayList<>();
        List<Task> subtasks = new ArrayList<>();
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(tasks, taskManager.getTasks());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(epics, taskManager.getEpics());
        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(subtasks, taskManager.getSubtasks());
    }

    @Test
    void shouldClearAllTasksWhenEmptyList() {
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        taskManager.clearPrioritizedTask();
        List<Task> tasks = new ArrayList<>();
        List<Task> epics = new ArrayList<>();
        List<Task> subtasks = new ArrayList<>();
        List<Task> prioritizedTasks = new ArrayList<>();
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(tasks, taskManager.getTasks());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(epics, taskManager.getEpics());
        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(subtasks, taskManager.getSubtasks());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(prioritizedTasks, taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldNotGetHistory() {
        List<Task> history = new ArrayList<>();
        assertEquals(0, taskManager.getHistory().size());
        assertEquals(history, taskManager.getHistory());
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

    @Test
    void shouldNotFindAnyTaskByIdWhenEmptyList() {
        assertNull(taskManager.findTaskById(0));
        assertNull(taskManager.findTaskById(1));
        assertNull(taskManager.findTaskById(2));
        assertNull(taskManager.findTaskById(3));
    }

    @Test
    void shouldAddTasksWhenEmptyList() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.removeTaskById(task.getId());
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task2);
        tasks.remove(task);
        assertEquals(tasks, taskManager.getTasks());
    }

    @Test
    void shouldNotHaveCrossingsWhenEmptyList() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addTask(task2);
        taskManager.addTask(task);
        taskManager.clearTasks();
        assertFalse(taskManager.isCrossing(task2));
    }

    @Test
    void shouldBeIdZeroWhenEmptyList() {
        assertEquals(0, taskManager.generateId());
    }

    //Неверный идентификатор задачи.
    @Test
    void shouldNotUpdateTasksWhenIncorrectId() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        Task updatedTask = new Task(-1, task.getStatus(), task.getDescription(), task.getName(), task.getStartTime(),
                30);
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.updateTask(updatedTask);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotUpdateEpicsWhenIncorrectId() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Epic updatedEpic1 = new Epic(-1, Status.DONE, epic1.getDescription(), epic1.getName());
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.updateEpic(updatedEpic1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotUpdateSubtasksWhenIncorrectId() {
        Epic epic2 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic2);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask11);
        Subtask updatedSubtask11 = new Subtask(-1, Status.DONE, subtask11.getDescription(),
                subtask11.getName(), epic2.getId(), subtask11.getStartTime(), 20);
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.updateSubtask(updatedSubtask11);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotRemoveTasksByIdWhenIncorrectId() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.removeTaskById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotRemoveSubtasksByIdWhenIncorrectId() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.removeSubtaskById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotRemoveEpicsByIdWhenIncorrectId() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.removeEpicById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotFindTasksByIdWhenIncorrectId() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findTaskById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotFindEpicsByIdWhenIncorrectId() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findEpicById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotFindSubtasksByIdWhenIncorrectId() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findSubtaskById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotAddTasksWhenIncorrectId() {
        Task task = new Task(-1, Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addTask(task);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotAddSubtasksWhenIncorrectId() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(-1, Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addSubtask(subtask11);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }

    @Test
    void shouldNotAddEpicsWhenIncorrectId() {
        Epic epic1 = new Epic(-1, Status.NEW, "Epic1 description", "Epic1 name");
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addEpic(epic1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
    }
}
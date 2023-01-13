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

    @Test
    void shouldAddTasksNorm() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        assertTrue(taskManager.getTasks().contains(task));
        assertTrue(taskManager.getPrioritizedTasks().contains(task));
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
    void shouldAddEpicsNorm() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        assertTrue(taskManager.getEpics().contains(epic1));
    }

    @Test
    void shouldAddEpicsWhenEmptyList() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Epic epic2 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.removeEpicById(epic2.getId());
        List<Epic> epics = new ArrayList<>();
        epics.add(epic1);
        epics.add(epic2);
        epics.remove(epic2);
        assertEquals(epics, taskManager.getEpics());
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

    @Test
    void shouldAddSubtasksNorm() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addSubtask(subtask11);
        assertTrue(taskManager.getSubtasks().contains(subtask11));
        assertTrue(taskManager.getPrioritizedTasks().contains(subtask11));
    }

    @Test
    void shouldAddSubtasksWhenEmptyList() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 50);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.removeSubtaskById(subtask11.getId());
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask11);
        subtasks.add(subtask12);
        subtasks.remove(subtask11);
        assertEquals(subtasks, taskManager.getSubtasks());
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
    void shouldGetTasksNorm() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 30), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task2);
        assertTrue((taskManager.getTasks().size() == 2), "Количество задач = 2");
        assertEquals(taskManager.getTasks(), tasks, "Количество задач = 2");
    }

    @Test
    void shouldNotGetAnyTaskTypeWhenEmptyList() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        assertTrue(taskManager.getSubtasksOfEpic(epic1).isEmpty());
    }

    @Test
    void shouldNotGetTasksWhenIncorrectId() {
        Task task = new Task(-5, Status.NEW, "Task description", "Task name",
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
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.clear();
        assertEquals(tasks, taskManager.getTasks());
    }

    @Test
    void shouldGetEpicsNorm() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Epic epic2 = new Epic(taskManager.generateId(), Status.IN_PROGRESS, "Epic2 description", "Epic2 name");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        List<Epic> epics = new ArrayList<>();
        epics.add(epic1);
        epics.add(epic2);
        assertTrue((taskManager.getEpics().size() == 2), "Количество эпиков = 2");
        assertEquals(taskManager.getEpics(), epics, "Количество эпиков = 2");
    }

    @Test
    void shouldNotGetEpicsWhenIncorrectId() {
        Epic epic1 = new Epic(-5, Status.NEW, "Epic1 description", "Epic1 name");
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addEpic(epic1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
        List<Epic> epics = new ArrayList<>();
        epics.add(epic1);
        epics.clear();
        assertEquals(epics, taskManager.getEpics());
    }

    @Test
    void shouldGetSubtasksNorm() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask11);
        subtasks.add(subtask12);
        assertTrue((taskManager.getSubtasks().size() == 2), "Количество подзадач = 2");
        assertEquals(taskManager.getSubtasks(), subtasks, "Количество подзадач = 2");
    }

    @Test
    void shouldGetPrioritizedTasksNorm() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 30), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        List<Task> prioritizedTasks = new ArrayList<>();
        prioritizedTasks.add(task);
        prioritizedTasks.add(task2);
        prioritizedTasks.add(subtask11);
        prioritizedTasks.add(subtask12);
        assertTrue((taskManager.getPrioritizedTasks().size() == 4), "Количество подзадач = 4");
        assertEquals(taskManager.getPrioritizedTasks(), prioritizedTasks, "Количество подзадач = 4");
    }

    @Test
    void shouldNotGetPrioritizedTasksWhenIncorrectId() {
        Task task = new Task(-5, Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(-5, Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        taskManager.addEpic(epic1);
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addTask(task);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionT.getMessage());
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addSubtask(subtask11);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionS.getMessage());
        List<Task> prioritizedTasks = new ArrayList<>();
        prioritizedTasks.add(task);
        prioritizedTasks.add(subtask11);
        prioritizedTasks.clear();
        assertEquals(taskManager.getPrioritizedTasks(), prioritizedTasks);
    }

    @Test
    void shouldGetSubtasksOfEpicNorm() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        subtasksOfEpic.add(subtask11);
        subtasksOfEpic.add(subtask12);
        assertEquals(subtasksOfEpic, epic1.getSubtasks());
        taskManager.clearSubtasks();
        subtasksOfEpic.clear();
        assertEquals(subtasksOfEpic, epic1.getSubtasks());
        assertEquals(subtasksOfEpic, taskManager.getSubtasksOfEpic(epic1));
    }

    @Test
    void shouldNotGetSubtasksOfEpicWhenEmptyList() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        final IllegalArgumentException exceptionE = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearEpics();
                    }
                });
        assertEquals("Список эпиков пуст: ничего не удалено!", exceptionE.getMessage());
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearSubtasks();
                    }
                });
        assertEquals("Список подзадач пуст: ничего не удалено!", exceptionS.getMessage());
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        subtasksOfEpic.add(subtask11);
        subtasksOfEpic.add(subtask12);
        subtasksOfEpic.clear();
        assertEquals(subtasksOfEpic, epic1.getSubtasks());
    }

    @Test
    void shouldNotGetSubtasksOfEpicWhenIncorrectId() {
        Epic epic1 = new Epic(-5, Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addEpic(epic1);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        subtasksOfEpic.add(subtask11);
        subtasksOfEpic.add(subtask12);
        subtasksOfEpic.clear();
        assertEquals(subtasksOfEpic, epic1.getSubtasks());
    }

    @Test
    void shouldFindAnyTaskByIdNorm() {
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
    void shouldNotFindAnyTaskByIdWhenEmptyList() {
        Task task = new Task(0, Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Epic epic1 = new Epic(1, Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(2, Status.NEW, "Subtask11 description",
                "Subtask11 name", 1, LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findTaskById(0);
                    }
                });
        assertEquals("Список задач пуст", exceptionT.getMessage());
        final IllegalArgumentException exceptionE = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findEpicById(1);
                    }
                });
        assertEquals("Список эпиков пуст", exceptionE.getMessage());
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findSubtaskById(2);
                    }
                });
        assertEquals("Список подзадач пуст", exceptionS.getMessage());
    }

    @Test
    void shouldNotFindAnyTasksByIdWhenIncorrectId() {
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findTaskById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionT.getMessage());
        final IllegalArgumentException exceptionE = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findEpicById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionE.getMessage());
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findSubtaskById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionS.getMessage());
    }

    @Test
    void shouldUpdateTaskNorm() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addTask(task);
        Task updatedTask = new Task(task.getId(), task.getStatus(), task.getDescription(), task.getName(),
                task.getStartTime(), task.getDuration());
        updatedTask.setStatus(Status.DONE);
        updatedTask.setDuration(50);
        updatedTask.setDescription("New description");
        updatedTask.setName("New name");
        updatedTask.setStartTime(LocalDateTime.of(2022, 12, 31, 15, 0).plusDays(5));
        taskManager.updateTask(updatedTask);
        Task expectedTask = taskManager.findTaskById(task.getId());
        assertEquals(task, expectedTask, "Задачи не совпадают");
    }

    @Test
    void shouldNotUpdateTaskWhenEmptyList() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task updatedTask = new Task(task.getId(), Status.DONE, task.getDescription(), task.getName(),
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.updateTask(updatedTask);
                    }
                });
        assertEquals("Обновление невозможно: список задач пуст", exceptionT.getMessage());
    }

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
    void shouldUpdateEpicNorm() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Epic updatedEpic1 = new Epic(epic1.getId(), epic1.getStatus(), epic1.getDescription(), epic1.getName());
        updatedEpic1.setName("New name");
        updatedEpic1.setStatus(Status.DONE);
        updatedEpic1.setDescription("New description");
        taskManager.updateEpic(updatedEpic1);
        Epic expectedEpic = taskManager.findEpicById(epic1.getId());
        assertEquals(epic1, expectedEpic, "Эпики не совпадают");
    }

    @Test
    void shouldNotUpdateEpicsWhenEmptyList() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Epic updatedEpic1 = new Epic(epic1.getId(), Status.DONE, epic1.getDescription(), epic1.getName());
        final IllegalArgumentException exceptionE = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.updateEpic(updatedEpic1);
                    }
                });
        assertEquals("Обновление невозможно: список эпиков пуст", exceptionE.getMessage());
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
    void shouldUpdateSubtaskNorm() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addSubtask(subtask11);
        epic1.addSubtask(subtask11);
        Subtask updatedSubtask11 = new Subtask(subtask11.getId(), subtask11.getStatus(), subtask11.getDescription(),
                subtask11.getName(), epic1.getId(), subtask11.getStartTime(), subtask11.getDuration());
        updatedSubtask11.setStatus(Status.DONE);
        updatedSubtask11.setDuration(50);
        updatedSubtask11.setDescription("New description");
        updatedSubtask11.setName("New name");
        updatedSubtask11.setStartTime(LocalDateTime.of(2022, 12, 31, 15, 0).plusDays(5));
        taskManager.updateSubtask(updatedSubtask11);
        Subtask expectedSubtask = taskManager.findSubtaskById(subtask11.getId());
        assertEquals(subtask11, expectedSubtask, "Подзадачи не совпадают");
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
    void shouldNotUpdateSubtasksWhenEmptyList() {
        Epic epic2 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        Subtask updatedSubtask11 = new Subtask(subtask11.getId(), Status.DONE, subtask11.getDescription(),
                subtask11.getName(), epic2.getId(), LocalDateTime.of(2022, 12, 31, 17, 0), 20);
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.updateSubtask(updatedSubtask11);
                    }
                });
        assertEquals("Обновление невозможно: список подзадач пуст", exceptionS.getMessage());
    }

    @Test
    void shouldRemoveTaskByIdNorm() {
        Task task = new Task(taskManager.generateId(), Status.DONE, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.removeTaskById(task.getId());
        taskManager.removeTaskById(5);
        assertFalse(taskManager.getTasks().contains(task));
        assertTrue(taskManager.getTasks().contains(task2));
    }

    @Test
    void shouldNotRemoveTaskByIdWhenEmptyList() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.removeTaskById(task.getId());
                    }
                });
        assertEquals("Удаление по id невозможно, список задач пуст", exceptionS.getMessage());
    }

    @Test
    void shouldNotRemoveTaskByIdWhenIncorrectId() {
        Task task = new Task(-5, Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addTask(task);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionT.getMessage());
        final IllegalArgumentException exceptionTR = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.removeTaskById(task.getId());
                    }
                });
        assertEquals("Удаление по id невозможно, список задач пуст", exceptionTR.getMessage());
    }

    @Test
    void shouldRemoveEpicByIdNorm() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Epic epic2 = new Epic(taskManager.generateId(), Status.DONE, "Epic2 description", "Epic2 name");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.removeEpicById(epic1.getId());
        assertFalse(taskManager.getEpics().contains(epic1));
        assertTrue(taskManager.getEpics().contains(epic2));
    }

    @Test
    void shouldNotRemoveEpicsByIdWhenEmptyList() {
        Epic epic2 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.removeEpicById(epic2.getId());
                    }
                });
        assertEquals("Удаление по id невозможно, список эпиков пуст", exceptionS.getMessage());
    }

    @Test
    void shouldNotRemoveEpicByIdWhenIncorrectId() {
        Epic epic2 = new Epic(-5, Status.NEW, "Epic1 description", "Epic1 name");
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addEpic(epic2);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionT.getMessage());
        final IllegalArgumentException exceptionER = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.removeEpicById(epic2.getId());
                    }
                });
        assertEquals("Удаление по id невозможно, список эпиков пуст", exceptionER.getMessage());
    }

    @Test
    void shouldRemoveSubtaskByIdNorm() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.DONE, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        Subtask subtask12 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask12 description",
                "Subtask12 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 0), 20);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.removeSubtaskById(subtask11.getId());
        assertFalse(taskManager.getSubtasks().contains(subtask11));
        assertTrue(taskManager.getSubtasks().contains(subtask12));
    }

    @Test
    void shouldNotRemoveSubtasksByIdWhenEmptyList() {
        Epic epic2 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask11 description",
                "Subtask11 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 16, 30), 20);
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.removeSubtaskById(subtask11.getId());
                    }
                });
        assertEquals("Удаление по id невозможно, список подзадач пуст", exceptionS.getMessage());
    }

    @Test
    void shouldNotRemoveSubtaskByIdWhenIncorrectId() {
        Epic epic1 = new Epic(taskManager.generateId(), Status.NEW, "Epic1 description", "Epic1 name");
        Subtask subtask11 = new Subtask(-5, Status.DONE, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addSubtask(subtask11);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionT.getMessage());
        final IllegalArgumentException exceptionER = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.removeSubtaskById(subtask11.getId());
                    }
                });
        assertEquals("Удаление по id невозможно, список подзадач пуст", exceptionER.getMessage());
    }

    @Test
    void shouldGetHistoryNorm() {
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
    void shouldNotGetHistoryWhenEmptyList() {
        List<Task> history = new ArrayList<>();
        assertEquals(0, taskManager.getHistory().size());
        assertEquals(history, taskManager.getHistory());
    }

    @Test
    void shouldNotGetHistoryWhenIncorrectId() {
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findTaskById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionT.getMessage());
        final IllegalArgumentException exceptionE = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findEpicById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionE.getMessage());
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.findSubtaskById(-1);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionS.getMessage());
        List<Task> history = new ArrayList<>();
        assertEquals(0, taskManager.getHistory().size());
        assertEquals(history, taskManager.getHistory());
    }

    @Test
    void shouldClearAllTaskNorm() {
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
    void shouldNotClearAllTasksWhenEmptyList() {
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearAllTasks();
                    }
                });
        assertEquals("Списки задач пусты: ничего не удалено!", exceptionT.getMessage());
    }

    @Test
    void shouldNotClearAllTasksWhenIncorrectId() {
        Task task = new Task(-1, Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addTask(task);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionT.getMessage());
        Epic epic1 = new Epic(-1, Status.NEW, "Epic1 description", "Epic1 name");
        final IllegalArgumentException exceptionE = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addEpic(epic1);
                    }
                });
        assertEquals("Некорректный ввод id", exceptionE.getMessage());
        Epic epic2 = new Epic(taskManager.generateId(), Status.NEW, "Epic2 description", "Epic2 name");
        Subtask subtask11 = new Subtask(-1, Status.NEW, "Subtask11 description",
                "Subtask11 name", epic2.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addSubtask(subtask11);
                    }
                });
        assertEquals("Некорректный ввод id", exception.getMessage());
        final IllegalArgumentException exceptionTC = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearAllTasks();
                    }
                });
        assertEquals("Списки задач пусты: ничего не удалено!", exceptionTC.getMessage());
    }

    @Test
    void shouldClearAnyTasksTypesNorm() {
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
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task2);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask11);
        subtasks.add(subtask12);
        List<Epic> epics = new ArrayList<>();
        epics.add(epic1);
        assertTrue((taskManager.getTasks().size() == 2), "Количество задач = 2");
        assertEquals(taskManager.getTasks(), tasks, "Количество задач = 2");
        assertTrue((taskManager.getEpics().size() == 1), "Количество эпиков = 1");
        assertEquals(taskManager.getEpics(), epics, "Количество эпиков = 1");
        assertTrue((taskManager.getSubtasks().size() == 2), "Количество подзадач = 2");
        assertEquals(taskManager.getSubtasks(), subtasks, "Количество подзадач = 4");
        tasks.clear();
        epics.clear();
        subtasks.clear();
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        assertTrue((taskManager.getTasks().size() == 0), "Количество задач = 0");
        assertEquals(tasks, taskManager.getTasks());
        assertTrue((taskManager.getEpics().size() == 0), "Количество задач = 0");
        assertEquals(epics, taskManager.getEpics());
        assertTrue((taskManager.getSubtasks().size() == 0), "Количество задач = 0");
        assertEquals(subtasks, taskManager.getSubtasks());
    }

    @Test
    void shouldNotClearAnyTasksTypesWhenIncorrectId() {
        Task task = new Task(-5, Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addTask(task);
                        ;
                    }
                });
        assertEquals("Некорректный ввод id", exceptionT.getMessage());
        final IllegalArgumentException exceptionTC = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearTasks();
                    }
                });
        assertEquals("Список задач пуст: ничего не удалено!", exceptionTC.getMessage());
        Epic epic1 = new Epic(-1, Status.NEW, "Epic1 description", "Epic1 name");
        final IllegalArgumentException exceptionE = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addEpic(epic1);
                        ;
                    }
                });
        assertEquals("Некорректный ввод id", exceptionE.getMessage());
        final IllegalArgumentException exceptionEC = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearEpics();
                    }
                });
        assertEquals("Список эпиков пуст: ничего не удалено!", exceptionEC.getMessage());
        Subtask subtask11 = new Subtask(-6, Status.NEW, "Subtask11 description",
                "Subtask11 name", epic1.getId(), LocalDateTime.of(2022, 12, 31, 16, 20), 20);
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addSubtask(subtask11);
                        ;
                    }
                });
        assertEquals("Некорректный ввод id", exceptionS.getMessage());
        final IllegalArgumentException exceptionSC = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearSubtasks();
                    }
                });
        assertEquals("Список подзадач пуст: ничего не удалено!", exceptionSC.getMessage());
    }

    @Test
    void shouldNotClearAnyTasksTypesWhenEmptyList() {
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearTasks();
                    }
                });
        assertEquals("Список задач пуст: ничего не удалено!", exceptionT.getMessage());
        final IllegalArgumentException exceptionE = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearEpics();
                    }
                });
        assertEquals("Список эпиков пуст: ничего не удалено!", exceptionE.getMessage());
        final IllegalArgumentException exceptionS = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearSubtasks();
                    }
                });
        assertEquals("Список подзадач пуст: ничего не удалено!", exceptionS.getMessage());
    }

    @Test
    void shouldClearPrioritizedTasksNorm() {
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
    void shouldNotClearPrioritizedTasksTypesWhenEmptyList() {
        final IllegalArgumentException exceptionT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearPrioritizedTask();
                    }
                });
        assertEquals("Список задач в порядке приоритета пуст: ничего не удалено!", exceptionT.getMessage());
    }

    @Test
    void shouldNotClearPrioritizedTasksTypesWhenIncorrectId() {
        Task task = new Task(-5, Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        final IllegalArgumentException exceptionPT = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.addTask(task);
                        ;
                    }
                });
        assertEquals("Некорректный ввод id", exceptionPT.getMessage());
        final IllegalArgumentException exceptionPTC = assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.clearPrioritizedTask();
                        ;
                    }
                });
        assertEquals("Список задач в порядке приоритета пуст: ничего не удалено!", exceptionPTC.getMessage());
    }

    @Test
    void shouldGetTasksPrioritizedNorm() {
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
    void shouldGenerateIdNorm() {
        Task task = new Task(taskManager.generateId(), Status.NEW, "Task description", "Task name",
                LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Task task2 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        Task task3 = new Task(taskManager.generateId(), Status.NEW, "Task2 description", "Task2 name",
                LocalDateTime.of(2022, 12, 31, 16, 10), 20);
        assertEquals(0, task.getId());
        assertEquals(1, task2.getId());
        assertEquals(2, task3.getId());
    }

    @Test
    void shouldBeIdZeroWhenEmptyList() {
        assertEquals(0, taskManager.generateId());
    }

    // Тесты времени
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
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.removeSubtaskById(subtask12.getId());
        assertEquals(epic1.getStartTime(), subtask11.getStartTime());
        assertEquals(20, epic1.getDuration());
        assertEquals(epic1.getEndTime(), subtask11.getEndTime());
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
}
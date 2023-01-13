package Epic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.service.Managers;
import ru.yandex.practicum.kanbanCore.service.TaskManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    public TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
        epic = new Epic(taskManager.generateId(), Status.NEW, "Epic description", "Epic name");
        taskManager.addEpic(epic);
    }

    @Test  //Пустой список подзадач
    public void shouldBeZeroSubtaskWhenNewEpic() {
        assertTrue(epic.getSubtasks().isEmpty());
        assertEquals(0, epic.getSubtasks().size(), "Список подзадач пуст");
        assertEquals(Status.NEW, taskManager.getEpics().get(0).getStatus());
    }

    @Test  //Все подзадачи со статусом NEW
    public void shouldBeNewWhenAllSubtasksNew() {
        final int id = epic.getId();
        final Epic newEpic = taskManager.findEpicById(id);
        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask1 description",
                "Subtask1 name", id, LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask2 description",
                "Subtask2 name", id, LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Status epicStatus = newEpic.getStatus();
        assertEquals(Status.NEW, epicStatus, "Верный статус эпика - NEW");
    }

    @Test  //Все подзадачи со статусом Done
    public void shouldBeDoneWhenAllSubtasksDone() {
        final int id = epic.getId();
        final Epic newEpic = taskManager.findEpicById(id);
        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.DONE, "Subtask1 description",
                "Subtask1 name", id, LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.DONE, "Subtask2 description",
                "Subtask2 name", id, LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Status epicStatus = newEpic.getStatus();
        assertEquals(Status.DONE, epicStatus, "Верный статус эпика - DONE");
    }

    @Test  //Подзадачи со статусами NEW и DONE
    public void shouldBeInProgressWhenSubtasksAreNewAndDone() {
        final int id = epic.getId();
        final Epic newEpic = taskManager.findEpicById(id);
        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.NEW, "Subtask1 description",
                "Subtask1 name", id, LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.DONE, "Subtask2 description",
                "Subtask2 name", id, LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Status epicStatus = newEpic.getStatus();
        assertEquals(Status.IN_PROGRESS, epicStatus, "Верный статус эпика - IN_PROGRESS");
    }

    @Test  //Подзадачи со статусом IN_PROGRESS
    public void shouldBeInProgressWhenSubtasksInProgress() {
        final int id = epic.getId();
        final Epic newEpic = taskManager.findEpicById(id);
        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask1 description",
                "Subtask1 name", id, LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask2 description",
                "Subtask2 name", id, LocalDateTime.of(2022, 12, 31, 15, 40), 20);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Status epicStatus = newEpic.getStatus();
        assertEquals(Status.IN_PROGRESS, epicStatus, "Верный статус эпика - DONE");
    }

    @Test  //Подзадачи со статусом IN_PROGRESS
    public void shouldReturnZeroEpicTimeDataWhenNoSubtasks() {
        assertNull(epic.getStartTime());
        assertEquals(epic.getDuration(), 0);
        assertNull(epic.getEndTime());
    }

    @Test
    public void shouldCountEpicTimeDataWhenHaveOneSubtask() {
        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask1 description",
                "Subtask1 name", epic.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addSubtask(subtask1);
        assertEquals(epic.getStartTime(), subtask1.getStartTime());
        assertEquals(epic.getDuration(), subtask1.getDuration());
        assertEquals(epic.getEndTime(), subtask1.getEndTime());
    }

    @Test
    public void shouldCountEpicTimeDataWhenHaveTwoSubtasks() {
        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask1 description",
                "Subtask1 name", epic.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask2 description",
                "Subtask2 name", epic.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 30);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(epic.getStartTime(), subtask1.getStartTime());
        assertEquals(epic.getDuration(), (subtask1.getDuration() + subtask2.getDuration()));
        assertEquals(epic.getEndTime(), subtask2.getEndTime());
    }

    @Test
    public void shouldCountEpicTimeDataWhenSubtaskUpdated() {
        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask1 description",
                "Subtask1 name", epic.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask2 description",
                "Subtask2 name", epic.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 30);
        Subtask updatedSubtask2 = new Subtask(subtask2.getId(), subtask2.getStatus(), subtask2.getDescription(),
                subtask2.getName(), epic.getId(), LocalDateTime.of(2023, 1, 1, 10, 0), 50);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.updateSubtask(updatedSubtask2);
        assertEquals(epic.getStartTime(), subtask1.getStartTime());
        assertEquals(epic.getDuration(), (subtask1.getDuration() + subtask2.getDuration()));
        assertEquals(epic.getEndTime(), subtask2.getEndTime());
    }

    @Test
    public void shouldCountEpicEndTimeDataInDifferentSituations() {
        // Эпик без подзадач
        assertNull(epic.getEndTime());
        // Эпик с одной подзадачей
        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask1 description",
                "Subtask1 name", epic.getId(), LocalDateTime.of(2022, 12, 31, 15, 0), 20);
        taskManager.addSubtask(subtask1);
        assertEquals(epic.getEndTime(), subtask1.getEndTime());
        //Эпик с 2-мя подзадачами
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Subtask2 description",
                "Subtask2 name", epic.getId(), LocalDateTime.of(2022, 12, 31, 15, 40), 30);
        taskManager.addSubtask(subtask2);
        assertEquals(epic.getEndTime(), subtask2.getEndTime());
        // Пересчет времени эпика при удалении одной подзадачи
        taskManager.removeSubtaskById(subtask1.getId());
        assertEquals(epic.getEndTime(), subtask2.getEndTime());
        // Пересчет времени эпика при обновлении времени у подзадачи
        subtask2.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        subtask2.setDuration(50);
        assertEquals(epic.getEndTime(), subtask2.getEndTime());
        // Пересчет времени эпика при добавлении второй подзадачи
        taskManager.addSubtask(subtask1);
        assertEquals(epic.getEndTime(), subtask2.getEndTime());
    }
}
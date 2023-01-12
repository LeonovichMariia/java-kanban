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
}
import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.HistoryManager;
import ru.yandex.practicum.kanbanCore.service.Managers;
import ru.yandex.practicum.kanbanCore.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task(taskManager.generateId(), Status.NEW, "TaskDescription", "Doll");
        Task task2 = new Task(taskManager.generateId(), Status.IN_PROGRESS, "TaskDescription2", "Doll2");
        Task task3 = new Task(taskManager.generateId(), Status.DONE, "TaskDescription3", "Doll3");
        Epic epic = new Epic(taskManager.generateId(), Status.NEW, "EpicDescription", "Book");
        Subtask subtask = new Subtask(taskManager.generateId(), Status.NEW, "SubtaskDescription", "Page",
                epic.getId());
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.NEW, "SubtaskDescription2", "Page2",
                epic.getId());
        Epic epic2 = new Epic(taskManager.generateId(), Status.IN_PROGRESS, "EpicDescription2", "Book");
        Subtask subtask3 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "SubtaskDescription3", "Page3",
                epic.getId());
        Subtask subtask4 = new Subtask(taskManager.generateId(), Status.DONE, "SubtaskDescription4", "Page4",
                epic.getId());
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask2);
        epic.addSubtask(subtask);
        epic.addSubtask(subtask2);
        epic2.addSubtask(subtask3);
        epic2.addSubtask(subtask4);
        taskManager.findEpicById(epic.getId());
        taskManager.findTaskById(task.getId());
        taskManager.findTaskById(task2.getId());
        taskManager.findEpicById(epic2.getId());
        taskManager.findTaskById(task2.getId());
        taskManager.findSubtaskById(subtask2.getId());
        taskManager.findTaskById(task2.getId());
        taskManager.findTaskById(task2.getId());
        taskManager.findTaskById(task2.getId());
        taskManager.findTaskById(task2.getId());
        taskManager.findTaskById(task2.getId());
        System.out.println(taskManager.getHistory());
        Task task1 = new Task(task.getId(), Status.IN_PROGRESS, task.getDescription(), task.getName());
        taskManager.updateTask(task1);
        System.out.println(taskManager.getTasks());
    }
}
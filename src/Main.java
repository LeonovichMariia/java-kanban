import kanbanCore.entity.Epic;
import kanbanCore.entity.Status;
import kanbanCore.entity.Subtask;
import kanbanCore.entity.Task;
import kanbanCore.service.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task = new Task(manager.generateId(), Status.NEW, "myDescription0", "Book0");
        manager.addTask(task);
        Epic epic = new Epic(manager.generateId(), Status.NEW, "myNewDescription1", "Book1");
        Subtask subtask = new Subtask(manager.generateId(), Status.NEW, "myNewDescription2",
                "Book2", epic);
        epic.addSubtask(subtask);
        manager.addTask(epic);
        Subtask subtask1 = new Subtask(subtask.getId(), Status.DONE, subtask.getDescription(), subtask.getName(),
                subtask.getEpic());
        manager.updateTask(subtask1);
        System.out.println(manager.getAllTasks());
        System.out.println(manager.findTaskById(subtask1.getId()));
        manager.removeById(subtask.getId());
        System.out.println(manager.getAllTasks());
    }
}

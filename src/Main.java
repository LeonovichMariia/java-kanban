import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.Manager;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task = new Task(manager.generateId(), Status.NEW, "Купить хлеб", "Дарницкий");
        Epic epic = new Epic(manager.generateId(), Status.IN_PROGRESS, "Сделать ремонт в ванной комнате",
                "Ремонт");
        Subtask subtask = new Subtask(manager.generateId(), Status.NEW, "Убрать старую плитку",
                "Избавление от старого", epic.getId());
        Subtask subtask1 = new Subtask(manager.generateId(), Status.DONE, "Положить новую плитку",
                "Обновление",
                epic.getId());
        Subtask subtask2 = new Subtask(manager.generateId(), Status.IN_PROGRESS, "Убрать всю грязь и пыль",
                "Уборка", epic.getId());
        epic.addSubtask(subtask);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        manager.addTask(task);
        manager.addEpic(epic);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        manager.clearTasks();
        System.out.println(manager.getTasks());
        manager.clearSubtasks();
        System.out.println(manager.getSubtasks());
        manager.clearEpics();
        System.out.println(manager.getEpics());
        Manager manager2 = new Manager();
        Task task2 = new Task(manager2.generateId(), Status.NEW, "Купить хлеб", "Дарницкий");
        Epic epic2 = new Epic(manager2.generateId(), Status.NEW, "Сделать ремонт в ванной комнате",
                "Ремонт");
        Subtask subtask3 = new Subtask(manager2.generateId(), Status.NEW, "Убрать старую плитку",
                "Избавление от старого", epic.getId());
        Subtask subtask4 = new Subtask(manager2.generateId(), Status.DONE, "Положить новую плитку",
                "Обновление",
                epic.getId());
        Subtask subtask5 = new Subtask(manager2.generateId(), Status.IN_PROGRESS, "Убрать всю грязь и пыль",
                "Уборка", epic.getId());
        epic2.addSubtask(subtask3);
        epic2.addSubtask(subtask4);
        epic2.addSubtask(subtask5);
        manager2.addTask(task2);
        manager2.addEpic(epic2);
        System.out.println(manager2.getTasks());
        System.out.println(manager2.getEpics());
        System.out.println(manager2.getSubtasks());
        System.out.println(manager2.getSubtasksOfEpic(epic2));
    }
}
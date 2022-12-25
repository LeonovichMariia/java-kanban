import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.exceptions.ManagerLoadException;
import ru.yandex.practicum.kanbanCore.service.FileBackedTasksManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
//        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(new File("resources/tasks"));
//        Task task0 = new Task(fileBackedTasksManager.generateId(), Status.NEW, "Read the book", "Book");
//        Task task1 = new Task(fileBackedTasksManager.generateId(), Status.NEW, "Java learning", "Java");
//        Epic epic = new Epic(fileBackedTasksManager.generateId(), Status.IN_PROGRESS, "Make a renovation at home",
//                "Renovation");
//        Subtask subtask = new Subtask(fileBackedTasksManager.generateId(), Status.DONE, "Change flooring",
//                "Bedroom", epic.getId());
//        Subtask subtask2 = new Subtask(fileBackedTasksManager.generateId(), Status.NEW, "Change wallpaper",
//                "Kitchen", epic.getId());
//        Subtask subtask3 = new Subtask(fileBackedTasksManager.generateId(), Status.IN_PROGRESS, "Hang a shelf",
//                "Bathroom", epic.getId());
//        fileBackedTasksManager.addTask(task0);
//        fileBackedTasksManager.addTask(task1);
//        fileBackedTasksManager.addEpic(epic);
//        fileBackedTasksManager.addSubtask(subtask);
//        fileBackedTasksManager.addSubtask(subtask2);
//        fileBackedTasksManager.addSubtask(subtask3);
//        fileBackedTasksManager.findEpicById(epic.getId());
//        fileBackedTasksManager.findTaskById(task0.getId());
//        fileBackedTasksManager.findTaskById(task1.getId());
//        fileBackedTasksManager.findSubtaskById(subtask3.getId());
//        fileBackedTasksManager.findSubtaskById(subtask2.getId());
//        fileBackedTasksManager.findSubtaskById(subtask.getId());
//        System.out.println(fileBackedTasksManager.getHistory());
        try {
            FileBackedTasksManager fb2 = FileBackedTasksManager.loadFromFile(new File("resources/tasks"));
        } catch (ManagerLoadException e) {
            System.out.println(e.getMessage());
        }
    }
}
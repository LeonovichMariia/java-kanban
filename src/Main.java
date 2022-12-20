import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.FileBackedTasksManager;
import ru.yandex.practicum.kanbanCore.service.Managers;
import ru.yandex.practicum.kanbanCore.service.TaskManager;
import ru.yandex.practicum.kanbanCore.service.customLinkedList.CustomLinkedList;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        CustomLinkedList customLinkedList = new CustomLinkedList();
        TaskManager taskManager = Managers.getDefault();
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("tasks"));
        System.out.println();
//        Task task0 = new Task(taskManager.generateId(), Status.NEW, "ReadTheBook", "Book");
//        Epic epic = new Epic(taskManager.generateId(), Status.DONE,"ReadTheBook", "Book");
//        Subtask subtask = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Read", "Book0", epic.getId());
//        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Read2", "Book2", epic.getId());
//        Subtask subtask3 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "Read3", "Java", epic.getId());
//        fileBackedTasksManager.addTask(task0);
//        fileBackedTasksManager.addEpic(epic);
//        fileBackedTasksManager.addSubtask(subtask);
//        fileBackedTasksManager.addSubtask(subtask2);
//        fileBackedTasksManager.addSubtask(subtask3);
//        fileBackedTasksManager.findTaskById(task0.getId());
//        fileBackedTasksManager.findEpicById(epic.getId());
//        fileBackedTasksManager.findSubtaskById(subtask.getId());
//        fileBackedTasksManager.findSubtaskById(subtask2.getId());
//        fileBackedTasksManager.findSubtaskById(subtask3.getId());
//        fileBackedTasksManager.findSubtaskById(subtask3.getId());
//        fileBackedTasksManager.findSubtaskById(subtask3.getId());
//        fileBackedTasksManager.findSubtaskById(subtask3.getId());
//        fileBackedTasksManager.findSubtaskById(subtask3.getId());
//        fileBackedTasksManager.findSubtaskById(subtask3.getId());
//        System.out.println(fileBackedTasksManager.getHistory());
    }
}
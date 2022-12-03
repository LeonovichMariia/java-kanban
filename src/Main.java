import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.Managers;
import ru.yandex.practicum.kanbanCore.service.TaskManager;
import ru.yandex.practicum.kanbanCore.service.customLinkedList.CustomLinkedList;

public class Main {
    public static void main(String[] args) {
        CustomLinkedList customLinkedList = new CustomLinkedList();
        TaskManager taskManager = Managers.getDefault();
        Task task0 = new Task(taskManager.generateId(), Status.NEW, "ReadTheBook", "Book");
        customLinkedList.linkLast(task0);
        System.out.println(customLinkedList.getTasks());
        customLinkedList.removeNode(task0);
        System.out.println(customLinkedList.getTasks());

        Task task1 = new Task(taskManager.generateId(), Status.NEW, "Java learning", "Java");
        Task task2 = new Task(taskManager.generateId(), Status.IN_PROGRESS, "Chemistry learning", "Education");
        Task task3 = new Task(taskManager.generateId(), Status.IN_PROGRESS, "CookSomeFood", "Food");
        customLinkedList.linkLast(task1);
        customLinkedList.linkLast(task2);
        customLinkedList.linkLast(task3);
        System.out.println(customLinkedList.getTasks());
        customLinkedList.removeNode(task2);
        System.out.println(customLinkedList.getTasks());
        customLinkedList.removeNode(task1);
        customLinkedList.removeNode(task3);

        Task task4 = new Task(taskManager.generateId(), Status.NEW, "CleanTheHouse", "Cleaning");
        Task task5 = new Task(taskManager.generateId(), Status.IN_PROGRESS, "BuySomeClothes", "Shopping");
        Task task6 = new Task(taskManager.generateId(), Status.IN_PROGRESS, "MeetWithFriends", "Meetings");
        customLinkedList.linkLast(task4);
        customLinkedList.linkLast(task5);
        customLinkedList.linkLast(task6);
        System.out.println(customLinkedList.getTasks());
        customLinkedList.removeNode(task6);
        System.out.println(customLinkedList.getTasks());
    }
}
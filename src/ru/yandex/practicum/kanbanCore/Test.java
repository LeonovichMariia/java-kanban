package ru.yandex.practicum.kanbanCore;

import ru.yandex.practicum.kanbanCore.entity.Epic;
import ru.yandex.practicum.kanbanCore.entity.Status;
import ru.yandex.practicum.kanbanCore.entity.Subtask;
import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.Managers;
import ru.yandex.practicum.kanbanCore.service.TaskManager;
import ru.yandex.practicum.kanbanCore.service.customLinkedList.CustomLinkedList;

public class Test {
    public static void main(String[] args) {
        CustomLinkedList customLinkedList = new CustomLinkedList();
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task(taskManager.generateId(), Status.NEW, "TaskDescription", "Doll");
        Task task2 = new Task(taskManager.generateId(), Status.IN_PROGRESS, "TaskDescription2", "Doll2");
        Epic epic = new Epic(taskManager.generateId(), Status.NEW, "EpicDescription", "Book");
        Subtask subtask = new Subtask(taskManager.generateId(), Status.NEW, "SubtaskDescription", "Page",
                epic.getId());
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.NEW, "SubtaskDescription2", "Page2",
                epic.getId());
        Subtask subtask3 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS, "SubtaskDescription3", "Page3",
                epic.getId());
        Epic epic2 = new Epic(taskManager.generateId(), Status.IN_PROGRESS, "EpicDescription2", "Book");
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        epic.addSubtask(subtask);
        epic.addSubtask(subtask2);
        epic2.addSubtask(subtask3);
        taskManager.findTaskById(task.getId());
        System.out.println(taskManager.getHistory());
        taskManager.findTaskById(task.getId());
        System.out.println(taskManager.getHistory());
        taskManager.findTaskById(task2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.findEpicById(epic.getId());
        taskManager.findEpicById(epic.getId());
        System.out.println(taskManager.getHistory());
        taskManager.findTaskById(task.getId());
        taskManager.findEpicById(epic.getId());
        taskManager.findEpicById(epic2.getId());
        System.out.println(taskManager.getHistory());
        customLinkedList.linkLast(task);
        customLinkedList.linkLast(task2);
        customLinkedList.removeNode(task2);
        System.out.println(customLinkedList.getTasks());
    }
}
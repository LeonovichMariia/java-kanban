package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.customLinkedList.CustomLinkedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history = new CustomLinkedList();
    private final Map<Integer, Task> hashMap = new HashMap<>();

    @Override
    public void remove(int id) {
        ArrayList<Task> tasks = history.getTasks();
        for (Task task : tasks) {
            if (task.getId() == id) {
                history.removeNode(task);
                hashMap.remove(task.getId());
                break;
            }
        }
    }

    @Override
    public LinkedList<Task> getHistory() {
        return new LinkedList<>(history.getTasks());
    }

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            if (history.getTasks().contains(task)) {
                history.removeNode(task);
            }
            history.linkLast(task);
            hashMap.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена.");
        }
    }
}
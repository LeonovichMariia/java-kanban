package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.Task;
import ru.yandex.practicum.kanbanCore.service.customLinkedList.CustomLinkedList;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history = new CustomLinkedList();
    private final Map<Integer, Task> hashMap = new HashMap<>();

    @Override
    public void remove(int id) {
        final Task task = hashMap.get(id);
        if (task != null) {
            history.removeNode(task);
            hashMap.remove(task.getId());
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            if (hashMap.containsKey(task.getId())) {
                history.removeNode(task);
            }
            history.linkLast(task);
            hashMap.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена.");
        }
    }
}
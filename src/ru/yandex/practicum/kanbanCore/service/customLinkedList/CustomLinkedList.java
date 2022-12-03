package ru.yandex.practicum.kanbanCore.service.customLinkedList;

import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> hashMap = new HashMap<>();
    private int size = 0;

    public void linkLast(Task task) {
        Node node = new Node(task);
        node.setPrev(tail);
        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
        }
        tail = node;
        size++;
        hashMap.put(task.getId(), node);
    }

    public List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    public void removeNode(Task task) {
        if (task != null) {
            Node node = hashMap.get(task.getId());
            if (node == null) {
                return;
            }
            Node nextNode = node.getNext();
            Node prevNode = node.getPrev();
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                head = nextNode;
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            } else {
                tail = prevNode;
            }
            hashMap.remove(task.getId());
        }
    }
}
package ru.yandex.practicum.kanbanCore.service.customLinkedList;

import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomLinkedList {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> hashMap = new HashMap<>();
    private int size = 0;

    public void linkLast(Task task) {
        Node node = new Node(task);
        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
            tail = node;
        }
        node.setPrev(tail);
        tail = head;
        size++;
        hashMap.put(task.getId(), node);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    public void removeNode(Task task) {
        Node node = hashMap.get(task.getId());
        if (node == null) {
            return;
        }
        Node nextNode = node.getNext();
        Node prevNode = node.getPrev();
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
        } else if (prevNode == null) {
            nextNode.setPrev(null);
            head = nextNode;
        } else if (nextNode == null) {
            prevNode.setNext(null);
            tail = prevNode;
        } else {
            prevNode.setNext(nextNode);
            nextNode.setPrev(prevNode);
        }
        hashMap.remove(task.getId());
    }
}
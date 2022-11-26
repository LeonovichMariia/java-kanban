package ru.yandex.practicum.kanbanCore.service.customLinkedList;

import ru.yandex.practicum.kanbanCore.entity.Task;

import java.util.ArrayList;

public class CustomLinkedList {
    private Node head;
    private Node tail;
    private int size = 0;

    public void linkLast(Task task) {
        if (head == null) {
            head = new Node(task);
            tail = head;
        } else {
            Node node = new Node(task);
            tail.setNext(node);
            node.setPrev(tail);
            tail = node;
        }
        size++;
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
        Node currentNode = head;
        while (currentNode != null) {
            if (currentNode.getTask() == task) {
                if (size == 1) {
                    head = null;
                    tail = null;
                } else {
                    Node nextNode = currentNode.getNext();
                    Node prevNode = currentNode.getPrev();
                    if (prevNode == null) {
                        nextNode.setPrev(null);
                        head = nextNode;
                    } else if (nextNode == null) {
                        prevNode.setNext(null);
                        tail = prevNode;
                    } else {
                        prevNode.setNext(nextNode);
                        nextNode.setPrev(prevNode);
                    }
                }
                size--;
                break;
            } else {
                currentNode = currentNode.getNext();
            }
        }
    }
}
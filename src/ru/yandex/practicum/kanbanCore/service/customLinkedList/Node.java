package ru.yandex.practicum.kanbanCore.service.customLinkedList;

import ru.yandex.practicum.kanbanCore.entity.Task;

public class Node {
    private final Task task;
    private Node prev;
    private Node next;

    public Node (Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
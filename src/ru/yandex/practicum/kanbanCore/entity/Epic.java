package ru.yandex.practicum.kanbanCore.entity;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(int id, Status status, String description, String name) {
        super(id, status, description, name);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    @Override
    public String toString() {
        return getId() + "," + getTaskType() + "," + getName() + "," + getStatus() + "," + getDescription();
    }
}
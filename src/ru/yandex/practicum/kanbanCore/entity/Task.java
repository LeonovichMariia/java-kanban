package ru.yandex.practicum.kanbanCore.entity;

public class Task {
    private String name;
    private String description;
    private final int id;
    private Status status;
    protected TaskType taskType;

    public Task(int id, Status status, String description, String name) {
        this.id = id;
        this.status = status;
        this.description = description;
        this.name = name;
        this.taskType = TaskType.TASK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + "," + taskType + "," + name+ "," + status + "," + description;
    }
}
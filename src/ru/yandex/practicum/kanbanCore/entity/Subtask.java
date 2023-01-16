package ru.yandex.practicum.kanbanCore.entity;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, Status status, String description, String name, int epicId, LocalDateTime startTime, int duration) {
        super(id, status, description, name, startTime, duration);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id = " + getId() +
                ", taskType = " + taskType +
                ", name = " + getName() +
                ", status = " + getStatus() +
                ", description = " + getDescription() +
                ", startTime = " + getStartTime() +
                ", duration = " + getDuration() +
                ", epicId = " + epicId +
                '}';
    }
}
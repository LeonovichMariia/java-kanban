package ru.yandex.practicum.kanbanCore.entity;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, Status status, String description, String name, int epicId) {
        super(id, status, description, name);
        this.epicId = epicId;
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
                "epicId=" + epicId +
                "} " + super.toString();
    }
}
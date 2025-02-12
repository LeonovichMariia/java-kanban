package ru.yandex.practicum.kanbanCore.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(int id, Status status, String description, String name) {
        super(id, status, description, name, null, 0);
        this.taskType = TaskType.EPIC;
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
    public LocalDateTime getStartTime() {
        Subtask subtask = subtasks.stream().min(Comparator.comparing(Task::getStartTime)).orElse(null);
        if (subtask != null) {
            return subtask.getStartTime();
        }
        return null;
    }

    @Override
    public LocalDateTime getEndTime() {
        Subtask subtask = subtasks.stream().max(Comparator.comparing(Task::getEndTime)).orElse(null);
        if (subtask != null) {
            return subtask.getEndTime();
        }
        return null;
    }

    @Override
    public int getDuration() {
        boolean haveNoSubtask = getSubtasks().isEmpty();
        int duration = 0;
        if (haveNoSubtask) {
            setDuration(0);
        } else {
            ArrayList<Subtask> subtasks = getSubtasks();
            for (Subtask subtask : subtasks) {
                int subtaskDuration = subtask.getDuration();
                duration += subtaskDuration;
            }
        }
        return duration;
    }
}
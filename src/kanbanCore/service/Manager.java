package kanbanCore.service;

import kanbanCore.entity.Epic;
import kanbanCore.entity.Status;
import kanbanCore.entity.Subtask;
import kanbanCore.entity.Task;

import java.util.ArrayList;

public class Manager {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private int idGenerator = 0;

    public ArrayList<Task> getAllTasks() {
        return tasks;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (int i = 0; i < this.tasks.size(); i++) {
            Task task = this.tasks.get(i);
            if (task instanceof Epic) {
                tasks.add(task);
            }
        }
        return epics;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int i = 0; i < this.tasks.size(); i++) {
            Task task = this.tasks.get(i);
            if (task instanceof Epic) {
                tasks.addAll(((Epic) task).getSubtasks());
            }
        }
        return subtasks;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public int generateId() {
        return idGenerator++;
    }

    public Task findTaskById(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (id == task.getId()) {
                return task;
            } else {
                if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    ArrayList<Subtask> subtasks = epic.getSubtasks();
                    for (int j = 0; j < subtasks.size(); j++) {
                        Subtask subtask = subtasks.get(j);
                        if (subtask.getId() == id) {
                            return subtask;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Task removeById(int id) {
        Task task = findTaskById(id);
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            subtask.getEpic().getSubtasks().remove(subtask);
            updateEpicStatus(subtask.getEpic());
        } else {
            tasks.remove(task);
        }
        return task;
    }

    public void addTask(Task task) {
        if (task instanceof Epic) {
            updateEpicStatus((Epic) task);
        }
        tasks.add(task);
    }

    public void updateTask(Task updatedTask) {
        Task task = findTaskById(updatedTask.getId());
        if (task != null) {
            updateTask(task, updatedTask);
        }
    }

    private void updateTask(Task originalTask, Task updatedTask) {
        originalTask.setDescription(updatedTask.getDescription());
        originalTask.setName(updatedTask.getName());
        if (!(originalTask instanceof Epic)) {
            originalTask.setStatus(updatedTask.getStatus());
        }
        if (originalTask instanceof Epic && updatedTask instanceof Epic) {
            Epic originalEpic = (Epic) originalTask;
            Epic updatedEpic = (Epic) updatedTask;
            originalEpic.setSubtasks(updatedEpic.getSubtasks());
            updateEpicStatus(updatedEpic);
        } else if (originalTask instanceof Subtask && updatedTask instanceof Subtask) {
            Subtask originalSubTask = (Subtask) originalTask;
            Subtask updatedSubTask = (Subtask) updatedTask;
            originalSubTask.setEpic(updatedSubTask.getEpic());
            updateEpicStatus(updatedSubTask.getEpic());
        }
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = epic.getSubtasks();
        int newCounter = 0;
        int inProgressCounter = 0;
        int doneCounter = 0;
        for (int i = 0; i < subtasks.size(); i++) {
            Subtask subtask = subtasks.get(i);
            if (subtask.getStatus() == Status.NEW) {
                newCounter++;

            } else if (subtask.getStatus() == Status.IN_PROGRESS) {
                inProgressCounter++;

            } else if (subtask.getStatus() == Status.DONE) {
                doneCounter++;
            }
        }
        if (inProgressCounter == 0 && doneCounter == 0) {
            epic.setStatus(Status.NEW);
        } else if (doneCounter == subtasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}

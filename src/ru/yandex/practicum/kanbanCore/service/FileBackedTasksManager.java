package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.*;
import ru.yandex.practicum.kanbanCore.exceptions.ManagerLoadException;
import ru.yandex.practicum.kanbanCore.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;
    private static final String TITLE = "id,type,name,status,description,epic";

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(TITLE);
            writer.newLine();
            for (Task task : getTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(epic.toString());
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toString());
                writer.newLine();
            }
            List<String> historySave = new ArrayList<>();
            for (Task task : getHistory()) {
                historySave.add(String.valueOf(task.getId()));
            }
            writer.newLine();
            writer.write(String.join(",", historySave));
        } catch (IOException e) {
            throw new ManagerSaveException("Сохранение невозможно" + file.getName());
        }
    }

    public Task taskFromString(String value) {
        String[] taskSplit = value.split(",");
        Task task = null;
        int id = Integer.parseInt(taskSplit[0]);
        Status status = Status.valueOf(taskSplit[3]);
        String title = taskSplit[2];
        String description = taskSplit[4];
        switch (TaskType.valueOf(taskSplit[1])) {
            case TASK:
                task = new Task(id, status, description, title);
                break;
            case EPIC:
                task = new Epic(id, status, description, title);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(taskSplit[5]);
                task = new Subtask(id, status, description, title, epicId);
                break;
        }
        return task;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> tasksId = new ArrayList<>();
        String[] ids = value.split(",");
        for (String id : ids) {
            tasksId.add(Integer.parseInt(id));
        }
        return tasksId;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            String line = bufferedReader.readLine();
            if (StringUtils.isBlank(line)) {
                throw new ManagerLoadException("Файл пуст: загрузка данных невозможна!");
            } else {
                while (!StringUtils.isBlank(line)) {
                    Task task = fileBackedTasksManager.taskFromString(line);
                    if (task.getTaskType() == TaskType.TASK) {
                        fileBackedTasksManager.addTask(task);
                    } else if (task.getTaskType() == TaskType.EPIC) {
                        fileBackedTasksManager.addEpic((Epic) task);
                    } else if (task.getTaskType() == TaskType.SUBTASK) {
                        fileBackedTasksManager.addSubtask((Subtask) task);
                    }
                    System.out.println(line);
                    line = bufferedReader.readLine();
                }
                String historyOfTasksId = bufferedReader.readLine();
                if (historyOfTasksId != null) {
                    List<Integer> ids = historyFromString(historyOfTasksId);
                    System.out.println();
                    for (Integer i : ids) {
                        fileBackedTasksManager.findEpicById(i);
                        fileBackedTasksManager.findTaskById(i);
                        fileBackedTasksManager.findSubtaskById(i);
                    }
                    System.out.println(ids);
                } else {
                    System.out.println(System.lineSeparator() + "История пуста!");
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Невозможно загрузить файл" + file.getName());
        }
        return fileBackedTasksManager;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public int generateId() {
        return super.generateId();
    }

    @Override
    public Task findTaskById(int id) {
        Task task = super.findTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask findSubtaskById(int id) {
        Subtask subtask = super.findSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic findEpicById(int id) {
        Epic epic = super.findEpicById(id);
        save();
        return epic;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        return super.getSubtasksOfEpic(epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }
}
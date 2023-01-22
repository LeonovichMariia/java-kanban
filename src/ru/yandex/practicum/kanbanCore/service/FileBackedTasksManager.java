package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.*;
import ru.yandex.practicum.kanbanCore.exceptions.ManagerLoadException;
import ru.yandex.practicum.kanbanCore.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;
    private static final String TITLE = "id,type,name,status,description,startTime,duration,epic";

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
            throw new ManagerSaveException("Сохранение невозможно: " + file.getName());
        }
    }

    public Task taskFromString(String value) {
        String[] taskSplit = value.split(",");
        Task task = null;
        String[] taskSplitId = taskSplit[0].split(" = ");
        int id = Integer.parseInt(taskSplitId[1]);
        String[] taskSplitStatus = taskSplit[3].split(" = ");
        Status status = Status.valueOf(taskSplitStatus[1]);
        String[] taskSplitTitle = taskSplit[2].split(" = ");
        String title = taskSplitTitle[1];
        String[] taskSplitDescription = taskSplit[4].split(" = ");
        String description = taskSplitDescription[1];
        String[] taskSplitTaskType = taskSplit[1].split(" = ");
        switch (TaskType.valueOf(taskSplitTaskType[1])) {
            case TASK:
                String[] taskSplitStartTime = taskSplit[5].split(" = ");
                LocalDateTime taskStartTime = LocalDateTime.parse(taskSplitStartTime[1]);
                String[] taskSplitDuration = taskSplit[6].split(" = ");
                String[] taskSplitDurationFin = taskSplitDuration[1].split("}");
                int taskDuration = Integer.parseInt(taskSplitDurationFin[0]);
                task = new Task(id, status, description, title, taskStartTime, taskDuration);
                break;
            case EPIC:
                task = new Epic(id, status, description, title);
                break;
            case SUBTASK:
                String[] subtaskSplitEpicId = taskSplit[7].split(" = ");
                String[] subtaskSplitEpicIdFin = subtaskSplitEpicId[1].split("}");
                int epicId = Integer.parseInt(subtaskSplitEpicIdFin[0]);
                String[] subtaskSplitStartTime = taskSplit[5].split(" = ");
                LocalDateTime startTime = LocalDateTime.parse(subtaskSplitStartTime[1]);
                String[] subtaskSplitDuration = taskSplit[6].split(" = ");
                String[] subtaskSplitDurationFin = subtaskSplitDuration[1].split("}");
                int duration = Integer.parseInt(subtaskSplitDurationFin[0]);
                task = new Subtask(id, status, description, title, epicId, startTime, duration);
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
            if (StringUtils.fileIsEmpty(line)) {
                throw new ManagerLoadException("Файл пуст: загрузка данных невозможна!");
            } else {
                while (!StringUtils.fileIsEmpty(line)) {
                    Task task = fileBackedTasksManager.taskFromString(line);
                    TaskType taskType = task.getTaskType();
                    switch (taskType) {
                        case TASK:
                            fileBackedTasksManager.addTask(task);
                            break;
                        case EPIC:
                            fileBackedTasksManager.addEpic((Epic) task);
                            break;
                        case SUBTASK:
                            fileBackedTasksManager.addSubtask((Subtask) task);
                            break;
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
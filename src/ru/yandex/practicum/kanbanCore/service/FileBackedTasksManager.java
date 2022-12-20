package ru.yandex.practicum.kanbanCore.service;

import ru.yandex.practicum.kanbanCore.entity.*;
import ru.yandex.practicum.kanbanCore.exceptions.ManagerLoadException;
import ru.yandex.practicum.kanbanCore.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic" + "\n");
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
            writer.write("\n" + String.join(",", historySave));
        } catch (IOException e) {
            throw new ManagerSaveException("Сохранение невозможно" + file.getName(), e);
        }
    }

    public Task taskFromString(String value) {
        String[] taskSplit = value.split(",");
        Task task = null;
        switch (taskSplit[1]) {
            case "TASK":
                task = new Task(Integer.parseInt(taskSplit[0]), Status.valueOf(taskSplit[3]), taskSplit[4],
                        taskSplit[2]);
                break;
            case "EPIC":
                task = new Epic(Integer.parseInt(taskSplit[0]), Status.valueOf(taskSplit[3]), taskSplit[4],
                        taskSplit[2]);
                break;
            case "SUBTASK":
                task = new Subtask(Integer.parseInt(taskSplit[0]), Status.valueOf(taskSplit[3]), taskSplit[4],
                        taskSplit[2], Integer.parseInt(taskSplit[5]));
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
            String s;
            bufferedReader.readLine();
            while (!(s = bufferedReader.readLine()).equals("")) {
                Task task = fileBackedTasksManager.taskFromString(s);
                if (task.getTaskType() == TaskType.TASK) {
                    fileBackedTasksManager.addTask(task);
                } else if (task.getTaskType() == TaskType.EPIC) {
                    fileBackedTasksManager.addEpic((Epic) task);
                } else if (task.getTaskType() == TaskType.SUBTASK) {
                    fileBackedTasksManager.addSubtask((Subtask) task);
                }
                System.out.println(s);
            }
            String br = bufferedReader.readLine();
            List<Integer> ids = historyFromString(br);
            System.out.println();
            for (Integer i : ids) {
                fileBackedTasksManager.findEpicById(i);
                fileBackedTasksManager.findTaskById(i);
                fileBackedTasksManager.findSubtaskById(i);
            }
            System.out.println();
        } catch (IOException e) {
            throw new ManagerLoadException("Невозможно загрузить файл" + file.getName(), e);
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

//    public static void main(String[] args) {
//        FileBackedTasksManager fb = loadFromFile(new File("tasks"));
//        Task task0 = new Task(fb.generateId(), Status.NEW, "Read the book", "Book");
//        Task task1 = new Task(fb.generateId(), Status.NEW, "Java learning", "Java");
//        Epic epic = new Epic(fb.generateId(), Status.IN_PROGRESS,"Make a renovation at home",
//                "Renovation");
//        Subtask subtask = new Subtask(fb.generateId(), Status.DONE, "Change flooring",
//                "Bedroom", epic.getId());
//        Subtask subtask2 = new Subtask(fb.generateId(), Status.NEW, "Change wallpaper",
//                "Kitchen", epic.getId());
//        Subtask subtask3 = new Subtask(fb.generateId(), Status.IN_PROGRESS, "Hang a shelf",
//                "Bathroom", epic.getId());
//        fb.findTaskById(task0.getId());
//        fb.findTaskById(task1.getId());
//        fb.findTaskById(epic.getId());
//        fb.findTaskById(subtask.getId());
//        fb.findTaskById(subtask2.getId());
//        fb.findTaskById(subtask3.getId());
//        fb.findTaskById(subtask.getId());
//        fb.findTaskById(subtask2.getId());
//        fb.findTaskById(subtask3.getId());
//        System.out.println(fb.getTasks());
//        FileBackedTasksManager fb2 = loadFromFile(new File("tasks"));
//        System.out.println();
//    }
}
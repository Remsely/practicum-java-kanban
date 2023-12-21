package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import services.managers.exceptions.BackupFileReceivingException;
import services.managers.exceptions.ManagerSaveException;
import services.managers.util.CSVFiles;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(String path) {
        super();
        this.path = Path.of(path);
        backupAll();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = super.getTaskByID(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = super.getEpicByID(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = super.getSubtaskByID(id);
        save();
        return subtask;
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createTask(Subtask subtask) {
        int id = super.createTask(subtask);
        save();
        return id;
    }

    @Override
    public int createTask(Epic epic) {
        int id = super.createTask(epic);
        save();
        return id;
    }

    @Override
    public boolean updateTask(int id, Task task) {
        boolean isUpdated = super.updateTask(id, task);
        save();
        return isUpdated;
    }

    @Override
    public boolean updateTask(int id, Subtask subtask) {
        boolean isUpdated = super.updateTask(id, subtask);
        save();
        return isUpdated;
    }

    @Override
    public boolean removeTask(int id) {
        boolean isDeleted = super.removeTask(id);
        save();
        return isDeleted;
    }

    private void backupAll() {
        try {
            String[] fileLines = readFile().split("\n");

            if (fileLines.length > 2) {
                String[] tasks = Arrays.copyOfRange(fileLines, 1, fileLines.length - 2);
                Arrays.sort(tasks, Comparator.comparingInt(s -> Integer.parseInt(s.split(",")[0])));

                for (String line : tasks)
                    backupTask(CSVFiles.taskFromCSV(line));

                if (tasks.length > 0)
                    currentTaskID = Integer.parseInt(tasks[tasks.length - 1].split(",")[0]) + 1;

                createBackedHistory(fileLines[fileLines.length - 1]);

                save();
            } else if (fileLines.length == 2) {
                backupTask(CSVFiles.taskFromCSV(fileLines[1]));
            }
        } catch (BackupFileReceivingException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("Файл по пути " + path.toString() + " не найден!");
            System.out.println("Программой был создан пустой файл.");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
        }
    }

    private String readFile() throws BackupFileReceivingException {
        String content;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            save();
            throw new BackupFileReceivingException(e.getMessage());
        }
        return content;
    }

    private void backupTask(Task task) {
        if (task != null) {
            if (task instanceof Epic)
                createBackedTask((Epic) task);
            else if (task instanceof Subtask)
                createBackedTask((Subtask) task);
            else
                createBackedTask(task);
        }
    }

    private void createBackedTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private void createBackedTask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        int epicID = subtask.getEpicID();
        Epic epic = epics.get(epicID);

        epic.addSubtaskID(subtask.getId());
        epic.setStatus(calculateEpicStatus(epicID));
    }

    private void createBackedTask(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private void createBackedHistory(String csv) {
        List<Integer> historyItemsIDs = CSVFiles.historyFromCSV(csv);

        for (Integer id : historyItemsIDs) {
            if (tasks.containsKey(id))
                history.add(tasks.get(id));
            else if (epics.containsKey(id))
                history.add(epics.get(id));
            else if (subtasks.containsKey(id))
                history.add(subtasks.get(id));
        }
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(path.toFile())) {
            writeLineToFile(CSVFiles.getCSVAttrs(), fileWriter);

            for (Task task : tasks.values())
                writeLineToFile(CSVFiles.taskToCSV(task), fileWriter);

            for (Epic epic : epics.values())
                writeLineToFile(CSVFiles.taskToCSV(epic), fileWriter);

            for (Subtask subtask : subtasks.values())
                writeLineToFile(CSVFiles.taskToCSV(subtask), fileWriter);

            fileWriter.write("\n");
            fileWriter.write(CSVFiles.historyToCSV(history));

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void writeLineToFile(String line, Writer fileWriter) throws IOException {
        fileWriter.write(line + "\n");
    }
}
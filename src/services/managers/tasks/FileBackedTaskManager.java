package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import models.enums.TaskTypes;
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

    public static void main(String[] args) {
        TaskManager manager1 = new FileBackedTaskManager("src/backup/text_files/test_manager.txt");

        manager1.createTask(new Task(0, "Task 0", "Description 0", TaskStatus.NEW));
        manager1.createTask(new Task(1, "Task 1", "Description 1", TaskStatus.IN_PROGRESS));
        manager1.createTask(new Task(2, "Task 2", "Description 2", TaskStatus.NEW));

        manager1.createTask(new Epic(3, "Epic 3", "Description 3"));
        manager1.createTask(new Epic(4, "Epic 4", "Description 4"));

        manager1.createTask(new Subtask(3, 5, "Subtask 5", "Description 5", TaskStatus.NEW));
        manager1.createTask(new Subtask(3, 6, "Subtask 6", "Description 6", TaskStatus.IN_PROGRESS));
        manager1.createTask(new Subtask(4, 7, "Subtask 7", "Description 7", TaskStatus.NEW));

        manager1.getSubtaskByID(5);
        manager1.getSubtaskByID(6);
        manager1.getSubtaskByID(7);
        manager1.getSubtaskByID(5);
        manager1.getTaskByID(0);
        manager1.getTaskByID(1);

        System.out.println(manager1);

        TaskManager manager2 = new FileBackedTaskManager("src/backup/text_files/test_manager.txt");

        System.out.println(manager2);

        manager2.getTaskByID(0);
        manager2.getEpicByID(3);

        manager2.removeTaskByID(0);
        manager2.removeTaskByID(1);

        System.out.println(manager2);

        TaskManager manager3 = new FileBackedTaskManager("src/backup/text_files/test_manager.txt");

        System.out.println(manager3);

        TaskManager manager4 = new FileBackedTaskManager("src/backup/text_files/test_manager.txt");

        System.out.println(manager4);
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
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createTask(Subtask subtask) {
        super.createTask(subtask);
        save();
    }

    @Override
    public void createTask(Epic epic) {
        super.createTask(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateTask(Subtask subtask) {
        super.updateTask(subtask);
        save();
    }

    @Override
    public void removeTaskByID(int id) {
        super.removeTaskByID(id);
        save();
    }

    private void backupAll() {
        try {
            String[] fileLines = readFile().split("\n");

            if (fileLines.length > 2) {
                String[] tasks = Arrays.copyOfRange(fileLines, 1, fileLines.length - 2);
                Arrays.sort(tasks, Comparator.comparingInt(s -> Integer.parseInt(s.split(",")[0])));

                for (String line : tasks)
                    createTaskFromCSV(line);

                currentTaskID = Integer.parseInt(tasks[tasks.length - 1].split(",")[0]) + 1;
                createHistoryFromCSV(fileLines[fileLines.length - 1]);
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

    private void createTaskFromCSV(String csv) {
        if (!csv.isBlank()) {
            String[] csvItems = csv.split(",");

            int id;
            try {
                id = Integer.parseInt(csvItems[0]);
            } catch (NumberFormatException e) {
                return;
            }

            TaskTypes type = TaskTypes.valueOf(csvItems[1]);
            String name = csvItems[2];
            TaskStatus status = TaskStatus.valueOf(csvItems[3]);
            String description = csvItems[4];
            int epicId = csvItems.length > 5 ? Integer.parseInt(csvItems[5]) : -1;

            switch (type) {
                case EPIC:
                    backupTask(new Epic(id, name, description));
                    break;
                case TASK:
                    backupTask(new Task(id, name, description, status));
                    break;
                case SUBTASK:
                    backupTask(new Subtask(epicId, id, name, description, status));
                    break;
            }
        }
    }

    private void backupTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private void backupTask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        int epicID = subtask.getEpicID();
        Epic epic = epics.get(epicID);

        epic.addSubtaskID(subtask.getId());
        epic.setStatus(calculateEpicStatus(epicID));
    }

    private void backupTask(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private void createHistoryFromCSV(String csv) {
        List<Integer> historyItemsIDs = CSVFiles.historyFromCSV(csv);

        for (Integer id : historyItemsIDs) {
            if (tasks.containsKey(id))
                history.add(tasks.get(id));
            else if (epics.containsKey(id))
                history.add(epics.get(id));
            else if (subtasks.containsKey(id))
                history.add(subtasks.get(id));
        }
        save();
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(path.toFile())) {
            writeLineToFile(CSVFiles.getAttrs(), fileWriter);

            for (Task task : tasks.values())
                writeLineToFile(CSVFiles.convertToCSV(task), fileWriter);

            for (Epic epic : epics.values())
                writeLineToFile(CSVFiles.convertToCSV(epic), fileWriter);

            for (Subtask subtask : subtasks.values())
                writeLineToFile(CSVFiles.convertToCSV(subtask), fileWriter);

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
package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import models.enums.TaskTypes;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(String path) {
        super();
        this.path = Path.of(path);
        backupAll();
    }

    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager("src/backup/text_files/test_manager.txt");

        manager.createTask(new Task(0, "Задача 0", "Описание задачи0", TaskStatus.NEW));
        manager.createTask(new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.NEW));

        manager.createTask(new Epic(2, "Эпик 2", "Описание эпика 2"));
        manager.createTask(new Subtask(2, 3, "Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW));
        manager.createTask(new Subtask(2, 4, "Подзадача 4", "Описание подзадачи 4", TaskStatus.NEW));
        manager.createTask(new Subtask(2, 5, "Подзадача 5", "Описание подзадачи 5", TaskStatus.NEW));

        System.out.println(manager);
    }

    private void backupAll() {
        try {
            String[] fileLines = readFile().split("\n");

            for (String line : fileLines) {
                createTaskFromCSV(line);
            }


        } catch (IOException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("ОШИБКА ПРИ РАБОТЕ С ФАЙЛОМ!");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
        } catch (NullPointerException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("Файла по пути " + path.toString() + "не существует!");
            System.out.println("Программой был создан пустой файл!");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    private String readFile() throws IOException {
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            Files.createFile(path);
        }
        return content;
    }

    private String convertToCSV(Task task) {
        return String.format("%d,%s,%s,%s,%s,",
                task.getId(),
                TaskTypes.TASK,
                task.getName(),
                task.getStatus(),
                task.getDescription()
        );
    }

    private String convertToCSV(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d",
                subtask.getId(),
                TaskTypes.SUBTASK,
                subtask.getName(),
                subtask.getStatus(),
                subtask.getDescription(),
                subtask.getEpicID()
        );
    }

    private String convertToCSV(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,",
                epic.getId(),
                TaskTypes.EPIC,
                epic.getName(),
                epic.getStatus(),
                epic.getDescription()
        );
    }

    private String getAttrs() {
        return "id,type,name,status,description,epic";
    }

    private void writeLineToFile(String line, Writer fileWriter) throws IOException {
        fileWriter.write(line + "\n");
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(path.toFile())) {
            writeLineToFile(getAttrs(), fileWriter);

            for (Task task : tasks.values())
                writeLineToFile(convertToCSV(task), fileWriter);

            for (Epic epic : epics.values())
                writeLineToFile(convertToCSV(epic), fileWriter);

            for (Subtask subtask : subtasks.values())
                writeLineToFile(convertToCSV(subtask), fileWriter);

            fileWriter.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                    createTask(new Epic(id, name, description));
                    break;
                case TASK:
                    createTask(new Task(id, name, description, status));
                    break;
                case SUBTASK:
                    createTask(new Subtask(epicId, id, name, description, status));
                    break;
            }
        }
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
}

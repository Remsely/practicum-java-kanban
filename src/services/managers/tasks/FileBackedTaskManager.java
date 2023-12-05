package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import models.enums.TaskTypes;
import services.managers.histories.HistoryManager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(String path) {
        super();
        this.path = Path.of(path);
        backupAll();
    }

    public static void main(String[] args) {
        FileBackedTaskManager manager1 = new FileBackedTaskManager("src/backup/text_files/test_manager.txt");

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

        System.out.println(manager2);

        TaskManager manager3 = new FileBackedTaskManager("src/backup/text_files/test_manager.txt");

        System.out.println(manager3);

        TaskManager manager4 = new FileBackedTaskManager("src/backup/text_files/test_manager.txt");

        System.out.println(manager4);
    }

    private void backupAll() {
        try {
            String[] fileLines = readFile().split("\n");

            /*
            Задачи нужно сортировать, т. к. если хранить их по типам, то новые задачи будут добавляться в конец
            определенной категории задач, => они будут созданы в неправильном порядке при следующем создании менеджера,
            и у Subtask и Epic слетят связи по id.
            Я решил, что лучше сортировать их здесь, а хранить их по категориям, т. к. этот метод вызывается 1 раз
            при создании объекта, а метод save отрабатывает при каждом изменении в менеджере.
            Еще можно придумать, как создавать задачи с определенным id, но это слишком долго, и тогда нет смысла
            наследовать это класс от InMemoryTaskManager. Хотя, может, я и не прав...
            */

            if (fileLines.length > 2) {
                String[] tasks = Arrays.copyOfRange(fileLines, 1, fileLines.length - 2);
                Arrays.sort(tasks, Comparator.comparingInt(s -> Integer.parseInt(s.split(",")[0])));

                for (String line : tasks) {
                    createTaskFromCSV(line);
                }
                createHistoryFromCSV(fileLines[fileLines.length - 1]);
            }

        } catch (IOException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("ОШИБКА ПРИ РАБОТЕ С ФАЙЛОМ!");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
        } catch (NullPointerException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("Файл по пути " + path.toString() + " не найден!");
            System.out.println("Программой был создан пустой файл.");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    private String readFile() throws IOException {
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            save();
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

            fileWriter.write("\n");
            fileWriter.write(HistoryManager.historyToCSV(history));

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

    private void createHistoryFromCSV(String csv) {
        List<Integer> historyItemsIDs = HistoryManager.historyFromCSV(csv);

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

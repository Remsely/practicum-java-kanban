package services.managers.util;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import models.enums.TaskTypes;
import services.managers.histories.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class CSVFiles {
    public static List<Integer> historyFromCSV(String csv) {
        List<Integer> history = new ArrayList<>();
        if (!csv.isBlank()) {
            String[] items = csv.split(",");

            for (String item : items)
                history.add(Integer.parseInt(item));
        }
        return history;
    }

    public static Task taskFromCSV(String csv) {
        if (!csv.isBlank()) {
            String[] csvItems = csv.split(",");

            int id = Integer.parseInt(csvItems[0]);
            TaskTypes type = TaskTypes.valueOf(csvItems[1]);
            String name = csvItems[2];
            TaskStatus status = TaskStatus.valueOf(csvItems[3]);
            String description = csvItems[4];
            int epicId = csvItems.length > 5 ? Integer.parseInt(csvItems[5]) : -1;

            switch (type) {
                case EPIC:
                    return new Epic(id, name, description);
                case SUBTASK:
                    return new Subtask(epicId, id, name, description, status);
                case TASK:
                    return new Task(id, name, description, status);
            }
        }
        return null;
    }

    public static String historyToCSV(HistoryManager manager) {
        StringBuilder historyCSV = new StringBuilder();

        for (Task task : manager.getHistory()) {
            historyCSV.append(task.getId()).append(",");
        }

        if (!(historyCSV.length() == 0)) {
            historyCSV.deleteCharAt(historyCSV.length() - 1);
        }

        return historyCSV.toString();
    }

    public static String taskToCSV(Task task) {
        return String.format("%d,%s,%s,%s,%s,",
                task.getId(),
                TaskTypes.TASK,
                task.getName(),
                task.getStatus(),
                task.getDescription()
        );
    }

    public static String taskToCSV(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d",
                subtask.getId(),
                TaskTypes.SUBTASK,
                subtask.getName(),
                subtask.getStatus(),
                subtask.getDescription(),
                subtask.getEpicID()
        );
    }

    public static String taskToCSV(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,",
                epic.getId(),
                TaskTypes.EPIC,
                epic.getName(),
                epic.getStatus(),
                epic.getDescription()
        );
    }

    public static String getCSVAttrs() {
        return "id,type,name,status,description,epic";
    }
}
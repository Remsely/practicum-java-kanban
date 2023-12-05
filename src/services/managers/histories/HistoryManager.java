package services.managers.histories;

import models.business.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    static List<Integer> historyFromCSV(String csv) {
        List<Integer> history = new ArrayList<>();
        if (!csv.isBlank()) {
            String[] items = csv.split(",");

            for (String item : items)
                history.add(Integer.parseInt(item));
        }
        return history;
    }

    static String historyToCSV(HistoryManager manager) {
        StringBuilder historyCSV = new StringBuilder();

        for (Task task : manager.getHistory()) {
            historyCSV.append(task.getId()).append(",");
        }

        if (!(historyCSV.length() == 0)) {
            historyCSV.deleteCharAt(historyCSV.length() - 1);
        }

        return historyCSV.toString();
    }

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}

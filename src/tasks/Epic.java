package tasks;
/*Я добавил этот пакет, чтобы protected поля не были доступны в Manager.
Это правильно в контексте инкапсуляции?*/

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIDs;

    public Epic(int id, String name, String description) {
        super(id, name, description, "NEW");
        subtasksIDs = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksIDs() {
        return subtasksIDs;
    }

    public void addSubtaskID(int id) {
        subtasksIDs.add(id);
    }

    public void removeSubtask(Integer id) {
        subtasksIDs.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return id == epic.id && Objects.equals(name, epic.name)
                && Objects.equals(description, epic.description) && Objects.equals(status, epic.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtasksIDs);
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subtasksIDs=" + subtasksIDs +
                ", status='" + status + '\'' +
                '}';
    }
}

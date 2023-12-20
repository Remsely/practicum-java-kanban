package models.business;

import models.enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksIDs;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasksIDs = new ArrayList<>();
    }

    public List<Integer> getSubtasksIDs() {
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
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subtasksIDs=" + subtasksIDs +
                ", status='" + status + '\'' +
                '}';
    }
}

package models.business;

import models.enums.TaskStatus;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicID;

    public Subtask(int epicID, String name, String description, TaskStatus status) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return id == subtask.id && Objects.equals(name, subtask.name)
                && Objects.equals(description, subtask.description) && Objects.equals(status, subtask.status)
                && Objects.equals(epicID, subtask.epicID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicID);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + epicID +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

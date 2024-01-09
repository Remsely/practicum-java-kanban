package models.business;

import models.enums.TaskStatus;

import java.util.Objects;

public class Subtask extends Task {
    public final int epicID;

    public Subtask(int epicID, String name, String description, TaskStatus status) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public Subtask(Subtask subtask) {
        super(subtask.name, subtask.description, subtask.status);
        this.id = subtask.id;
        this.startTime = subtask.startTime;
        this.duration = subtask.duration;
        this.epicID = subtask.epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicID == subtask.epicID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicID);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + epicID +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}

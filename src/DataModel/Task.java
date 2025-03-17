package DataModel;

import java.io.Serial;
import java.io.Serializable;

public abstract sealed class Task implements Serializable permits SimpleTask, ComplexTask {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final int idTask;
    protected  String statusTask;
    protected  boolean isAssigned;
    protected final String nameTask;

    public Task(int idTask, String nameTask) {
        this.idTask = idTask;
        this.statusTask = "UNCOMPLETED";
        this.nameTask = nameTask;
        this.isAssigned = false;
    }

    public abstract int estimateDuration();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idTask == task.idTask;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idTask);
    }

    public int getIdTask() {
        return idTask;
    }

    public String getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(String statusTask) {
        if(statusTask.equals("UNCOMPLETED") || statusTask.equals("COMPLETED")) {
            this.statusTask = statusTask;
        }
    }

    public String getNameTask() {
        return nameTask;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(boolean isAssigned) {
        this.isAssigned = isAssigned;
    }
}
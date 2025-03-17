package DataModel;

import java.util.ArrayList;
import java.util.List;

public non-sealed class ComplexTask extends Task {

    final private List<Task> componentTasks;

    public ComplexTask(int idTask,String nameTask) {
        super(idTask,nameTask);
        componentTasks = new ArrayList<>();
    }

   public void addTask(Task task) {
        componentTasks.add(task);
    }

   public void deleteTask(Task task) {
        componentTasks.remove(task);
    }

    @Override
    public int estimateDuration() {
            int totalDuration = 0;
            for (Task task : componentTasks) {
                if(task.getStatusTask().equals("UNCOMPLETED")) {
                    totalDuration += task.estimateDuration();
                }
            }
            if(totalDuration==0){
                this.setStatusTask("COMPLETED");
            }
            return totalDuration;
    }

    public List<Task> getComponentTasks() {
        return componentTasks;
    }
}

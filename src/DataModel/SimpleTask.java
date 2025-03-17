package DataModel;

import java.io.Serializable;

public non-sealed class SimpleTask extends Task implements Serializable {
    private final int startHour;
    private final int endHour;

    public SimpleTask(int idTask,String nameTask,int startHour, int endHour) {
        super(idTask,nameTask);

        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Override
    public int estimateDuration() {
        return endHour - startHour;
    }


}

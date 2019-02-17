public class TimerKeeper {

    // keep track of time displayed
    private long startTime;

    public void setStart(){
        startTime = System.nanoTime();
    }

    public long getStart(){
        return startTime;
    }

    public long getEllapsedTime(){
        return System.nanoTime() - startTime;
    }
}

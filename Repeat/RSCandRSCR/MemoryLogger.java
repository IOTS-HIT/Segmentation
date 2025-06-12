


public class MemoryLogger {


    private static MemoryLogger instance = new MemoryLogger();


    private double maxMemory = 0;

    /**
     * Method to obtain the only instance of this class
     * @return instance of MemoryLogger
     */
    public static MemoryLogger getInstance(){
        return instance;
    }

    public double getMaxMemory() {
        return maxMemory;
    }


    public void reset(){
        maxMemory = 0;
    }


    public void checkMemory() {
        double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
                / 1024d / 1024d;
        if (currentMemory > maxMemory) {
            maxMemory = currentMemory;
        }
    }
}
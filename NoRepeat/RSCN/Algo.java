import java.io.IOException;
public class Algo {
    public static void main(String [] args) throws IOException {

        if (args.length < 4) {
            System.out.println("Usage: java Algo <input> <output> <minutil>");
            return;
        }

        String input = args[0];
        String output = args[1];
        double minutil = Double.parseDouble(args[2]);

        double minconf = Double.parseDouble(args[3]);
        System.out.println("=========================== processing " + input + "===========================");

        TotalSRWithAnomalyDetection3 algo = new TotalSRWithAnomalyDetection3();

        algo.runAlgorithm(input, output, minconf, minutil, Integer.MAX_VALUE);
        algo.printStats();
    }
}

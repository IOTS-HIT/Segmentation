/**
 * @Copyright (C), HITSZ
 * @Author Maohua Lv
 * @Date Created in 9:44 2023/6/9.
 * @Version 1.0
 * @Description
 */

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

        TotalSRWithAnomalyDetection2 algo = new TotalSRWithAnomalyDetection2();

        algo.runAlgorithm(input, output, minconf, minutil);
        algo.printStats();
       
    }
}

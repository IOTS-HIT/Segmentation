/**
 * @Copyright (C), HITSZ
 * @Author Maohua Lyu, Wensheng, gan
 * @Date Created in 9:41 2022/10/9.
 * @Version 1.5
 * @Description
 */


import java.io.IOException;


public class Algo {
    public static void main(String [] args) throws IOException {


        if (args.length < 5) {
            System.out.println("Usage: java Algo <input> <output> <minutil>");
            return;
        }

        String input = args[0];
        String output = args[1];
        double minutil = Double.parseDouble(args[2]);
        double minconf = Double.parseDouble(args[3]);
        int type = Integer.parseInt(args[4]);

        System.out.println("=========================== processing " + input + "===========================");
        
        if(type==1){
            Comparators algo = new Comparators();
            algo.runAlgorithm(input, output, minconf, minutil, Integer.MAX_VALUE,Integer.MAX_VALUE, Integer.MAX_VALUE);
            algo.printStats();
        }
        if(type==2){
            AlgoHUSRM algo = new AlgoHUSRM();
            algo.runAlgorithm(input, output, minconf, minutil, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
            algo.printStats();
        }

        

//        String filename = "10K.txt";
//        int index = 6;
//        String input = "./input/" + filename;
//
//        System.out.println("=========================== processing " + filename + "===========================");
//        double minconf = 0.6;
//
//
//
//
//        double[] minutils = {17500, 19000, 20500, 22000, 23500, 25000, 26500};  //kosarak10k
//
//        double minutil = minutils[index];
//
//
//        int maxAntecedentSize = Integer.MAX_VALUE;
//        int maxConsequentSize = Integer.MAX_VALUE;
//        int maximumSequenceCount = Integer.MAX_VALUE;
//        String output = "./output/" + filename.substring(0, filename.length() - 4) + minutils[index] + ".txt";
//
        
        


    }
}

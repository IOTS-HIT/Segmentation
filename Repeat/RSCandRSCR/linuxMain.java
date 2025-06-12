import java.io.IOException;

public class linuxMain {
    public static void main(String[] args) throws IOException {


        if (args.length < 3) {
            System.out.println("Usage: java Algo <input> <output> <minutil>");
            return;
        }

        String input = args[0];
        String output = args[1];
        double minutil = Double.parseDouble(args[2]);

        int type = Integer.parseInt(args[3]);

        if (type==0){
            RuleMining ruleMining = new RuleMining();
            ruleMining.ruleBegin(input,output,minutil,0.6);
        }
        else if(type==1){
            RuleMiningAllRU ruleMining = new RuleMiningAllRU();
            System.out.println("allru is now");
            ruleMining.ruleBegin(input,output,minutil,0.6);
        }
        else if (type==2){
            RuleMiningNoRCalculate ruleMining = new RuleMiningNoRCalculate();
            ruleMining.ruleBegin(input,output,minutil,0.6);

        }

//        RuleMiningNoRCalculate ruleMining = new RuleMiningNoRCalculate();

//        RuleMining ruleMining = new RuleMining();

//          RuleMiningAllRU ruleMining = new RuleMiningAllRU();

//        RuleMiningNoRCalculate ruleMining = new RuleMiningNoRCalculate();

//
//        RuleMiningNoCalculateNoFirstP ruleMining = new RuleMiningNoCalculateNoFirstP();



        //BIBLE


        // output1 : all  output2:no SEU output3: normal ru
    }
}


import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

//        RuleMiningNoRCalculate ruleMining = new RuleMiningNoRCalculate();

//        RuleMining ruleMining = new RuleMining();

//          RuleMiningAllRU ruleMining = new RuleMiningAllRU();

//        RuleMiningNoRCalculate ruleMining = new RuleMiningNoRCalculate();

//
//        RuleMiningNoCalculateNoFirstP ruleMining = new RuleMiningNoCalculateNoFirstP();

        RuleMiningNoRCalculateNoQuickJudge ruleMining = new RuleMiningNoRCalculateNoQuickJudge();

        //BIBLE

        ruleMining.ruleBegin("dataset/BIBLE.txt","output1/NoRCalculateNoQuickJudge_BIBLE_12817.639.txt",12817.639,0.6);
        // output1 : all  output2:no SEU output3: normal ru
    }

}
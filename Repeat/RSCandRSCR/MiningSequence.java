import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MiningSequence {

    private ArrayList<MiningItem> mingSequence;

    public MiningSequence() {
        mingSequence = new ArrayList<>();
    }

    public void addMiningItem(MiningItem miningItem) {
        mingSequence.add(miningItem);
    }

    public MiningItem getMiningItem(int index) {
        return mingSequence.get(index);
    }

    public int getSize() {
        return mingSequence.size();
    }

    public MiningItem getLast(){
        return mingSequence.get(getSize()-1);
    }

    public void removeLast(){
        mingSequence.remove(mingSequence.size()-1);
    }

    public Set<Integer> getUntilSet(){
        Set<Integer> rev = new HashSet<>();
        for (MiningItem miningItem : mingSequence) {
            rev.add(miningItem.getName());
        }
        return rev;
    }

    public ArrayList<Integer> getNameList(){
        ArrayList<Integer> rev = new ArrayList<>();
        for (MiningItem miningItem : mingSequence) {
            rev.add(miningItem.getName());
        }
        return rev;
    }

    public int getSupportByIndex(int index){
        return mingSequence.get(index).getSupport();
    }
    public boolean judgeProduce(double conf){
        return (double)mingSequence.get(getSize()-1).getSupport() >= conf * mingSequence.get(getSize()-2).getSupport() ;
    }

    public int produceRule(boolean whetherOutput,double conf){

        int ruleCount=0;

        int size = mingSequence.size();

        double leastSupport = (double) mingSequence.get(getSize()-1).getSupport() / conf;
        int left = 0;
        int right = size - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (mingSequence.get(mid).getSupport() <= leastSupport) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        if (size - result > 1){
            ruleCount += (size-result-1);
        }

        if (whetherOutput){
            System.out.println("the result: "+result+"  size: "+size);
            System.out.println("HIGH UTILITY SEQ. RULE  utility: "+mingSequence.get(size-1).getUntilMaxUtility()+"  :");
            System.out.println("the support list: ");
            for (int i = 0; i < size; i++) {
                System.out.print(mingSequence.get(i).getSupport()+" ");
            }
            System.out.println();
            StringBuilder output = new StringBuilder();
            int i;
            for ( i = 0; i < size; i++) {
                output.append(mingSequence.get(i).getName()).append(" ");
                if (i == result) {
                    output.append(" --> ");
                }
            }
            output.append("confidence: "+(double)mingSequence.get(size - 1).getSupport() /mingSequence.get(result).getSupport());

            // 右移符号
            int j;
            for (i = result + 1; i < size; i++) {
                output = new StringBuilder();
                for (j = 0; j < size; j++) {
                    if (j == i) {
                        output.append(" --> ");
                    }
                    output.append(mingSequence.get(j).getName()).append(" ");
                }
                output.append("confidence: "+(double)mingSequence.get(size - 1).getSupport()/mingSequence.get(i-1).getSupport());
                System.out.println(output.toString());
            }
            System.out.println("============================");

        }

        return ruleCount;


    }
}

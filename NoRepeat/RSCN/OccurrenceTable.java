import java.util.List;
import java.util.ArrayList;
public class OccurrenceTable {
    double totalUtility = 0;
    double totalRightRemainUtility = 0;

    List<PointerTuple> nextTuples = new ArrayList<>();
    List<Double> profits = new ArrayList<>();
    List<Double> rightRemainUtility = new ArrayList<>();

    public OccurrenceTable(double profit, double rightRemainUtility){
        this.totalUtility += profit;
        this.totalRightRemainUtility += totalRightRemainUtility;
        this.profits.add(profit);
        this.rightRemainUtility.add(rightRemainUtility);
    }

    public int addNewElement(double profit, double rightRemainUtility){
        this.totalUtility += profit;
        this.totalRightRemainUtility += rightRemainUtility;
        this.profits.add(profit);
        this.rightRemainUtility.add(rightRemainUtility);
        return this.profits.size()-1;
    }
    public void addNextTuple(PointerTuple next){
        nextTuples.add(next);
    }

    public List<PointerTuple> getNextTuples(){
        return nextTuples;
    }


    public double getTotalUtility() {
        return totalUtility;
    }


    public double getTotalRightRemainUtility() {
        return totalRightRemainUtility;
    }

    public double getRightRemainUtilityOne(int index){
        return rightRemainUtility.get(index);
    }

    public int getOccurrenceTimes(){
        return profits.size();
    }

    public double getProfitOne(int index){
        return profits.get(index);
    }

    public List<Double> getProfit(){
        return profits;
    }


    public PointerTuple getPointerTuple(int index){
        return nextTuples.get(index);
    }

}

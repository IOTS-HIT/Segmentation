import java.util.ArrayList;
import java.util.List;

public class MiningUtilityTable {

    Integer item;

    int support = 0;

    double currentTotalUtility = 0;

    double rightRemainUtility = 0;

    //升序，ascending order
    List<Integer> pointerPositions = new ArrayList<>();
    List<Double> profits = new ArrayList<>();

    public MiningUtilityTable(Integer item, int support,List<Integer> Position, double rightRemainUtility,double currentTotalUtility, List<Double> profits){
        this.item = item;
        this.support = support;
        this.pointerPositions = Position;
        this.rightRemainUtility = rightRemainUtility;
        this.currentTotalUtility = currentTotalUtility;
        this.profits = profits;


    }



    public Integer getItem(){
        return item;
    }

    public double getRightRemainUtility(){
        return rightRemainUtility;
    }

    public double getCurrentTotalUtility() {
        return currentTotalUtility;
    }

    public double getProfitOne(int index){
        return profits.get(index);
    }

    public List<Integer> getPointerPosition() {
        return pointerPositions;
    }

    public void expendItem(Integer Position, double rightRemainUtility,double currentUtility){
        this.support++;
        this.rightRemainUtility += rightRemainUtility;
        this.currentTotalUtility += currentUtility;
        this.pointerPositions.add(Position);
        this.profits.add(currentUtility);
    }

    public int getSupport(){
        return support;
    }


}

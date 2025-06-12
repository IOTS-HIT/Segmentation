import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OccurrenceDatabase {

    Map<Integer,OccurrenceTable> occurrenceDatabase = new HashMap<>();

    //return the id of the item or itemset
    public PointerTuple addElement(Integer element, double profit,
                                    double rightRemainUtility, PointerTuple next){
        OccurrenceTable table = occurrenceDatabase.get(element);

        if(table!=null){
            return new PointerTuple(element,table.addNewElement(profit, rightRemainUtility));
        }else{
            occurrenceDatabase.put(element,new OccurrenceTable(profit, rightRemainUtility));
            return new PointerTuple(element,0);
        }
    }
    public void addNextTuple(Integer element, PointerTuple next){
        occurrenceDatabase.get(element).addNextTuple(next);
    }

    public OccurrenceTable getOccurrenceTable(Integer item){
        return occurrenceDatabase.get(item);
    }



    public void print(){
        for (Map.Entry<Integer, OccurrenceTable> entry : occurrenceDatabase.entrySet()) {
            List<PointerTuple> a = entry.getValue().nextTuples;
            if (entry.getKey()==888){
                for (PointerTuple b:a){
                    System.out.println(b.getId()+" "+b.getInternalPosition());
                }
            }
        }
    }

    //剪枝1：初始项剪枝
    public List<Integer> getCandidateFirstItem(double minutil){
        List<Integer> candidateFirstItem = new ArrayList<>();
        for(Map.Entry<Integer, OccurrenceTable> entry : occurrenceDatabase.entrySet()){
            OccurrenceTable table = entry.getValue();
            if(table.totalUtility + table.totalRightRemainUtility >= minutil){
                candidateFirstItem.add(entry.getKey());
            }
        }
        return candidateFirstItem;
    }




}

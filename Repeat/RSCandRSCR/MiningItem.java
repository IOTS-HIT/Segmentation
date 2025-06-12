import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MiningItem {
    private Integer name;

    private double untilMaxRemainUtility;

    private double untilMaxUtility;

    // line -> arrayIndex
    private HashMap<Integer, ArrayList<Integer>> occurArrayIndex;

    private HashMap<Integer,ArrayList<Double>> untilUtilityMap;

    // choose the max utility position until now
    // line -> arrayIndex
    private HashMap<Integer, Integer> chooseIndex;

    public MiningItem(Integer name){
        this.name = name;
        this.occurArrayIndex = new HashMap<>();
        this.chooseIndex = new HashMap<>();
        this.untilUtilityMap = new HashMap<>();
    }



    public void setUntilMaxRemainUtility(double untilMaxRemainUtility) {
        this.untilMaxRemainUtility = untilMaxRemainUtility;
    }

    public void setOccurArrayIndex(HashMap<Integer, ArrayList<Integer>> arrayIndex){
        this.occurArrayIndex = arrayIndex;
    }

    public void setChooseIndex(HashMap<Integer, Integer> chooseIndex){
        this.chooseIndex = chooseIndex;
    }

    public void setUntilMaxUtility(double untilMaxUtility){
        this.untilMaxUtility = untilMaxUtility;
    }

    public void setUntilUtilityMap(HashMap<Integer,ArrayList<Double>> untilUtilityMap){
        this.untilUtilityMap = untilUtilityMap;
    }

    public void addOccurArrayIndex(int line, int arrayIndex){

        if(occurArrayIndex.containsKey(line)){
            occurArrayIndex.get(line).add(arrayIndex);
        }else{
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(arrayIndex);
            occurArrayIndex.put(line, temp);
        }
    }

    public void addChooseIndex(int line,int arrayIndex){
        chooseIndex.put(line, arrayIndex);
    }

    public void addUtilityList(int line, ArrayList<Double> utilityList){
        this.untilUtilityMap.put(line,utilityList);
    }

    public HashMap<Integer, ArrayList<Integer>> getOccurArrayIndex(){
        return  this.occurArrayIndex;
    }

    public ArrayList<Integer> getOccurArrayByLine(int line){
        return occurArrayIndex.get(line);
    }

    public Integer getName(){
        return this.name;
    }

    public double getUntilUtilityByArrayIndex(int line,int index){
        return untilUtilityMap.get(line).get(index);
    }

    public double getUntilMaxRemainUtility(){
        return this.untilMaxRemainUtility;
    }

    public double getUntilMaxUtility(){
        return this.untilMaxUtility;
    }
    public Set<Integer> getChooseLineSet(){
        return chooseIndex.keySet();
    }

    public int getSupport(){
        return chooseIndex.size();
    }

    //PrintTest
    public void printTest(){
        System.out.println("name: "+name);
        System.out.println("untilMaxRemainUtility: "+untilMaxRemainUtility);
        System.out.println("untilMaxUtility: "+untilMaxUtility);
        System.out.println("occurArrayIndex: "+occurArrayIndex);
        System.out.println("chooseIndex: "+chooseIndex);
        System.out.println("untilUtilityMap: "+untilUtilityMap);
    }
}

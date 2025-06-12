import java.util.ArrayList;


public class LineInnerTable {


    //注意：行中的序列号
    private ArrayList<Integer> indexList;

    //效用
    private ArrayList<Double> utilityList;

    //所有剩余效用都包括自身来保证不会重复计算
    private ArrayList<Double> remainUtilityList;

    //指向行中下一个项的指针,注意指针中的index是指的Arraylist中的index，而不是行中的index
    private ArrayList<NextPointer> nextPointerList;


    public LineInnerTable() {
        indexList = new ArrayList<>();
        utilityList = new ArrayList<>();
        remainUtilityList = new ArrayList<>();
        nextPointerList = new ArrayList<>();
    }


    public int addNewItem(Integer index, Double utility) {
        indexList.add(index);
        utilityList.add(utility);
        remainUtilityList.add(0.0);
        return indexList.size()-1;
    }

    public int addNewItem(Integer index, Double utility,Double remainUtility) {
        indexList.add(index);
        utilityList.add(utility);
        remainUtilityList.add(remainUtility);
        return indexList.size()-1;
    }

    public void addNextPointer(NextPointer nextPointer) {
        nextPointerList.add(nextPointer);
    }

    public void setNextPointerListIndex(int index, NextPointer nextPointer){
        nextPointerList.set(index, nextPointer);
    }

    public void setRemainUtilityListIndex(int index, Double remainUtility){
        remainUtilityList.set(index, remainUtility);
    }

    public ArrayList<NextPointer> getNextPointerList(){
        return nextPointerList;
    }

    public ArrayList<Integer> getIndexList(){
        return indexList;
    }

    public ArrayList<Double> getUtilityList(){
        return utilityList;
    }
    public ArrayList<Double> getRemainUtilityList(){
        return remainUtilityList;
    }
    public int getMaxUtilityIndex(){
        double max = 0;
        int index = 0;
        for(int i=0;i<indexList.size();i++){
            if(utilityList.get(i)>max){
                max = utilityList.get(i);
                index = i;
            }
        }
        return index;
    }

    public double getUtility(int index){
        return utilityList.get(index);
    }

    public int getLineIndexByArrayIndex(int arrayIndex){
        return indexList.get(arrayIndex);
    }
    public double getUtilityByArrayIndex(int index){
        return utilityList.get(index);
    }

    public double getRemainUtilityByArrayIndex(int index){
        return remainUtilityList.get(index);
    }


}

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LinkTable {

    // item name -> line number -> line inner table
    private HashMap<Integer,HashMap<Integer,LineInnerTable>> linkTable;


    // the maximum remaining utility of each item
    private HashMap<Integer,Double> nameMaxRemainUtility = new HashMap<>();

    HashMap<Integer,Double> SEUMap = new HashMap<>();


    public LinkTable() {
        linkTable = new HashMap<>();
    }

    public void setSEUMap(HashMap<Integer, Double> SEUMap) {
        this.SEUMap = SEUMap;
    }

    public void setRemainUtility(Integer name, int line ,int index ,double remainUtility){
        linkTable.get(name).get(line).setRemainUtilityListIndex(index, remainUtility);
    }

    public void setNameMaxRemainUtility(HashMap<Integer, Double> nameMaxRemainUtility) {
        this.nameMaxRemainUtility = nameMaxRemainUtility;
    }

    public Set<Integer> getCandidateNames(double threshold) {

        //Now we need to delete some impossible first item
        Set<Integer> nameSet = new HashSet<>();
        for (Map.Entry<Integer, Double> entry : nameMaxRemainUtility.entrySet()) {
            if (entry.getValue() >= threshold) {

                nameSet.add(entry.getKey());
            }
        }

        return nameSet;
    }

    public Set<Integer> getAllNames(){
        return linkTable.keySet();
    }

    public double getNameMaxRemainUtilityByName(Integer name) {
        return nameMaxRemainUtility.get(name);
    }

    public int getLineIndexByNameLineArrayIndex(Integer name, int line, int arrayIndex) {
        return linkTable.get(name).get(line).getLineIndexByArrayIndex(arrayIndex);
    }



    public NextPointer getNextNextPointer(NextPointer nextPointer) {
        return linkTable.
                get(nextPointer.getName()).
                get(nextPointer.getLine()).
                getNextPointerList().
                get(nextPointer.getIndex());
    }

    public NextPointer getNextPointer(Integer name,int line,int arrayIndex){
        return linkTable.get(name).get(line).getNextPointerList().get(arrayIndex);
    }

    public double getUtility(Integer name, int line, int index) {
        return linkTable.get(name).get(line).getUtilityByArrayIndex(index);
    }

    public double getRemainUtility(Integer name, int line, int index) {
        return linkTable.get(name).get(line).getRemainUtilityByArrayIndex(index);
    }

    public HashMap<Integer, Double> getSEUMap() {
        return this.SEUMap;
    }

    public MiningItem getNameMiningItem(Integer name){
//TODO: find the way to store the utility
        MiningItem miningItem = new MiningItem(name);



        HashMap<Integer, ArrayList<Integer>> arrayIndex = new HashMap<>();
        HashMap<Integer,ArrayList<Double>> utilityMap = new HashMap<>();
        HashMap<Integer, Integer> chooseIndex = new HashMap<>();
        double []untilMaxUtility = new double[1];
        untilMaxUtility[0] = 0;

        linkTable.get(name).forEach((line,lineTable)->{

            utilityMap.put(line,new ArrayList<>(lineTable.getUtilityList()));
            chooseIndex.put(line,lineTable.getMaxUtilityIndex());
            untilMaxUtility[0] += lineTable.getUtility(lineTable.getMaxUtilityIndex());
            arrayIndex.put(line,    // quickly produce [0, 1, 2, ..., k] 的 ArrayList<Integer>
            IntStream.rangeClosed(0, lineTable.getIndexList().size()-1)
                        .boxed()
                        .collect(Collectors.toCollection(ArrayList::new)
            ));
        });

        miningItem.setUntilMaxRemainUtility(this.nameMaxRemainUtility.get(name));
        miningItem.setChooseIndex(chooseIndex);
        miningItem.setOccurArrayIndex(arrayIndex);
        miningItem.setUntilMaxUtility(untilMaxUtility[0]);
        miningItem.setUntilUtilityMap(utilityMap);


        return miningItem;
    }





    // return the index of the new item
    public int addNewItem(Integer name, Integer line, Integer index, Double utility) {

        int arrayIndex;
        //name is not in the linkTable
        if (!linkTable.containsKey(name)) {
            HashMap<Integer,LineInnerTable> temp = new HashMap<>();
            LineInnerTable lineInnerTable = new LineInnerTable();
            arrayIndex = lineInnerTable.addNewItem(index, utility);
            temp.put(line, lineInnerTable);
            linkTable.put(name, temp);

        }
        //name is in the linkTable but line is not in the linkTable
        else if (!linkTable.get(name).containsKey(line)) {

            LineInnerTable lineInnerTable = new LineInnerTable();
            arrayIndex = lineInnerTable.addNewItem(index, utility);
            linkTable.get(name).put(line, lineInnerTable);


        }
        //name and line are in the linkTable
        else{
            arrayIndex = linkTable.get(name).get(line).addNewItem(index, utility);
        }
        return arrayIndex;
    }

    public int addNewItem(Integer name, Integer line, Integer index, Double utility,Double remainUtility) {

        int arrayIndex;
        //name is not in the linkTable
        if (!linkTable.containsKey(name)) {
            HashMap<Integer,LineInnerTable> temp = new HashMap<>();
            LineInnerTable lineInnerTable = new LineInnerTable();
            arrayIndex = lineInnerTable.addNewItem(index, utility,remainUtility);
            temp.put(line, lineInnerTable);
            linkTable.put(name, temp);

        }
        //name is in the linkTable but line is not in the linkTable
        else if (!linkTable.get(name).containsKey(line)) {

            LineInnerTable lineInnerTable = new LineInnerTable();
            arrayIndex = lineInnerTable.addNewItem(index, utility,remainUtility);
            linkTable.get(name).put(line, lineInnerTable);


        }
        //name and line are in the linkTable
        else{
            arrayIndex = linkTable.get(name).get(line).addNewItem(index, utility,remainUtility);
        }
        return arrayIndex;
    }

    public void addNextPointer(Integer name, Integer line, NextPointer nextPointer) {
        linkTable.get(name).get(line).addNextPointer(nextPointer);
    }

    public void addNameMaxRemainUtility(Integer name, Double utility) {


        if (nameMaxRemainUtility.containsKey(name)) {
            nameMaxRemainUtility.put(name, nameMaxRemainUtility.get(name) + utility);
        } else {
            nameMaxRemainUtility.put(name, utility);
        }

    }



    //SEU Pruning

    public void calculateRemainUtility(){
        this.SEUMap = null;
        for (Map.Entry<Integer, HashMap<Integer, LineInnerTable>> outerEntry : linkTable.entrySet()) {

            HashMap<Integer, LineInnerTable> innerMap = outerEntry.getValue();

            for (Map.Entry<Integer, LineInnerTable> innerEntry : innerMap.entrySet()) {

                //find the beginning position of the line
                if(innerEntry.getValue().getIndexList().get(0)==0){

                    NextPointer nextPointer = innerEntry.getValue().getNextPointerList().get(0);
                    ArrayList<Integer> A = new ArrayList<>(10);
                    ArrayList<Double> B = new ArrayList<>(10);
                    A.add(outerEntry.getKey());
                    B.add(innerEntry.getValue().getUtilityList().get(0));

                    while(nextPointer!=null){
                        A.add(nextPointer.getName());
                        B.add(getUtility(nextPointer.getName(), nextPointer.getLine(), nextPointer.getIndex()));
                        nextPointer = getNextNextPointer(nextPointer);
                    }

                    double C[] = maxRemainingUtility(A, B);



                    HashMap<Integer,Double> nameMap = new HashMap<>();

                    nameMap.put(outerEntry.getKey(),C[0]);

                    addNameMaxRemainUtility(outerEntry.getKey(), C[0]);


                    // the first element
                    setRemainUtility(outerEntry.getKey(), innerEntry.getKey(), 0, C[0]);

                    nextPointer = innerEntry.getValue().getNextPointerList().get(0);

                    for(int i=1;i<C.length;i++){

                        if (nameMap.containsKey(A.get(i))) {

                            if(C[i]>nameMap.get(A.get(i))){

                                addNameMaxRemainUtility(A.get(i),
                                                -nameMap.get(A.get(i))
                                                +C[i]);
                                nameMap.put(A.get(i), C[i]);

                            }

                        }else{
                            nameMap.put(A.get(i), C[i]);
                            addNameMaxRemainUtility(A.get(i), C[i]);
                        }

                        setRemainUtility(nextPointer.getName(), nextPointer.getLine(), nextPointer.getIndex(), C[i]);
                        nextPointer = getNextNextPointer(nextPointer);
                    }


                }

            }

        }

        //print nameMaxRemainUtility
//        for(Map.Entry<String,Double> entry : nameMaxRemainUtility.entrySet()){
//            System.out.println("the name is : "+entry.getKey()+" the max remain utility is : "+entry.getValue());
//        }



    }

    public void deleteKey(Integer key){


        for (Map.Entry<Integer, HashMap<Integer, LineInnerTable>> outerEntry : linkTable.entrySet()) {

            if(!outerEntry.getKey().equals(key)){

                HashMap<Integer, LineInnerTable> innerMap = outerEntry.getValue();

                for (Map.Entry<Integer, LineInnerTable> innerEntry : innerMap.entrySet()) {

                    // NextPointerList
                    ArrayList<NextPointer> nextPointerList = innerEntry.getValue().getNextPointerList();

                    for(int i=0;i<nextPointerList.size();i++){

                        NextPointer nextPointer = nextPointerList.get(i);

                        //需要删除的项
                        if(nextPointer!=null && nextPointer.getName().equals(key)){

//                            System.out.println("delete : the pointer is : "+ outerEntry.getKey()+ "->" + nextPointer.getName()+" is deleting");


                            NextPointer nextP = getNextNextPointer(nextPointer);



                            // if null, then stop; and if equal, then continue
                            while(nextP != null && nextP.getName().equals(key)){



                                nextP = getNextNextPointer(nextP);

                            }

                            innerEntry.getValue().setNextPointerListIndex(i, nextP);

                        }


                    }



                }
            }



        }

        // update the SEUMap, for every delete item, for every line, if the line contains the item, then the SEUMap should be updated
        linkTable.get(key).forEach((k,v)->{
            double max = 0.0;
            for(double i : v.getUtilityList()){
                max = Math.max(max, i);
            }

            for(Map.Entry<Integer,HashMap<Integer,LineInnerTable>> entry : linkTable.entrySet()){
                if(entry.getValue().containsKey(k)){
                    this.SEUMap.put(entry.getKey(), this.SEUMap.get(entry.getKey())-max);
                }
            }


        });


//       System.out.println("delete : the key is : "+ key);

        linkTable.remove(key);
//        SEUMap.remove(key);
    }


    private double[] maxRemainingUtility(ArrayList<Integer> A, ArrayList<Double> B) {

        int n = A.size();
        double[] remainUtilities = new double[n];

        Map<Integer, Double> utilityMap = new HashMap<>();

        Integer item;
        double utility;
        double totalUtility=0.0;
        // 从后向前遍历数组
        for (int i = n - 1; i >= 0; i--) {
            // 当前元素
            item = A.get(i);
            utility = B.get(i);

            if(!utilityMap.containsKey(item)){
                utilityMap.put(item, utility);
                totalUtility += utility;
            }
            else if (utilityMap.get(item) < utility) {
                totalUtility += (utility-utilityMap.get(item));
                utilityMap.put(item, utility);
            }
            remainUtilities[i] = totalUtility;

            if(utilityMap.containsKey(item)){
                remainUtilities[i] -= utilityMap.get(item);
                remainUtilities[i] += utility;
            }
        }

        return remainUtilities;
    }

    // print the linkTable
    public void printTest(){
        linkTable.forEach((k,v)->{
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("the name is : " + k);

            v.forEach((k1,v1)->{
                System.out.println("______________________________");
                System.out.println("the line is : " + k1);
                System.out.println("the indexList is : " + v1.getIndexList());
                System.out.println("the utilityList is : " + v1.getUtilityList());
                System.out.println("the remainUtilityList is : " + v1.getRemainUtilityList());

                v1.getNextPointerList().forEach((v2)->{

                    if (v2 != null){
                        System.out.println("the nextPointerList is : " );
                        System.out.println("the name is : " +
                                v2.getName()+" "+"the line is : " +
                                v2.getLine()+" "+"the ArrayIndex is : " +
                                v2.getIndex());
                    }
                    else {
                        System.out.println("the nextPointerList is : null");
                    }

                });
                System.out.println("______________________________");

            });
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println();

        });
    }

}

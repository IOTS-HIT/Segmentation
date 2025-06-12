import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RuleMining {

    /**
     * start time of latest execution
     */
    long timeStart = 0;
    /**
     * end time of latest execution
     */
    long timeEnd = 0;

    long itemTableCount = 0;

    BufferedWriter writer = null;

    private LinkTable linkTable = new LinkTable();

    private MiningSequence miningSequence = new MiningSequence();

    private double minUtility = 0.0;

    private double minConfidence = 0.0;

    private int ruleCount = 0;



    public RuleMining(){
    }

    public void ruleBegin(String filePath, String output, double minUtilityThreshold,double conf) throws IOException {

        SequenceDatabaseWithUtility database = new SequenceDatabaseWithUtility();

        if (minUtilityThreshold <= 0) {
            this.minUtility = 0.0001;
        }
        else{
            this.minUtility = minUtilityThreshold;
        }
        if (conf<=0){
            this.minConfidence = 0.6;
        }
        else{
            this.minConfidence = conf;
        }

        // save the start time
        this.timeStart = System.currentTimeMillis();


        Files.createDirectories(Paths.get(output).getParent());
        writer = new BufferedWriter(new FileWriter(output));
        MemoryLogger.getInstance().reset();

        if (this.minUtility <1){
            this.minUtility = this.minUtility * database.loadFile(filePath);
        }
        else{
            database.loadFile(filePath);
        }
        System.out.println("this.minUtility: " + minUtility);
        database.SEUPrune(minUtility);
        linkTable.setNameMaxRemainUtility(database.calculateRemainUtility());
        scanIntoTable(database);
        database = null;


        ruleMining();
//        System.out.println("total rule count: " + ruleCount);
        // save the end time
        timeEnd = System.currentTimeMillis();
        printStats();
        writer.close();
    }

    private void scanIntoTable(SequenceDatabaseWithUtility database){
        List<SequenceWithUtility> sequences = database.getSequences();
        Iterator<SequenceWithUtility> iteratorSequence = sequences.iterator();
        while (iteratorSequence.hasNext()) {

            SequenceWithUtility sequence = iteratorSequence.next();
            int lineNumber = sequence.getId();
            Iterator<Integer> iteratorItems = sequence.getItemsets().iterator();
            Iterator<Double> iteratorUtilities = sequence.getUtilities().iterator();
            Iterator<Double> iteratorRemainUtilities = sequence.getRemainUtility().iterator();
            int i =0;
            int previousItem = 0;
            int arrayIndex;
            while (iteratorItems.hasNext()) {
                Integer item = iteratorItems.next();
                Double utility = iteratorUtilities.next();
                Double remainUtility = iteratorRemainUtilities.next();
                arrayIndex = linkTable.addNewItem(item, lineNumber, i, utility,remainUtility);
                if(i!=0){
                    linkTable.addNextPointer(previousItem, lineNumber, new NextPointer(item, lineNumber, arrayIndex));
                }
                previousItem = item;
                i++;
            }
            linkTable.addNextPointer(previousItem, lineNumber, null);
            MemoryLogger.getInstance().checkMemory();

        }

    }

    private double scanIntoTable(String filePath) throws IOException {

        double sumUtility = 0.0;


        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int lineNumber = 0;
            String line;

            //用于SEU剪枝
            HashMap<Integer,Double> SEUMap = new HashMap<>();

            while ((line = br.readLine()) != null && !line.equals("")) {

                // 外层循环：读取每一行的数据
                //System.out.println("Line Number: " + lineNumber);
                String[] items = line.split(" -1 ");
                double totalUtility = 0.0;

                // 提取总效用
                String lastItem = items[items.length - 1];
                if (lastItem.contains("SUtility")) {
                    // 提取序列所有效用之和
                    totalUtility = Double.parseDouble(lastItem.split(":")[1]);
                    sumUtility += totalUtility;
                    //System.out.println("Total Utility: " + totalUtility);
                }

                // 内层循环：解析每个项及其效用
                // 一些变量的定义
                String[] parts;
                Integer name;
                Integer previousName=0;
                double utility;
                int arrayIndex;

                //用于计算剩余效用



                //用于SEU计算
                //Integer : index of the utilities
                HashMap<Integer,Double> SEULine = new HashMap<>();

                // for the line
                for (int i =0; i < items.length-1; i++) {

                    parts = items[i].split("\\[|\\]");
                    name = Integer.parseInt(parts[0]);
                    utility = Double.parseDouble(parts[1]);


                    //重复项检测，用于计算SEU
                    if(SEULine.containsKey(name)){

                        // delete the min repeat value
                        if(SEULine.get(name)>utility){
                            totalUtility -= utility;

                        }
                        else{
                            totalUtility -= SEULine.get(name);
                            SEULine.put(name,utility);

                        }

                    }
                    else{
                        SEULine.put(name,utility);
                    }

                    // 将当前项加入LinkTable, 并返回当前项在ArrayList中的index
                    arrayIndex = linkTable.addNewItem(name, lineNumber, i, utility);

                    // not the first item, add next pointer to the previous item
                    if(i!=0){
                        linkTable.addNextPointer(previousName, lineNumber, new NextPointer(name, lineNumber, arrayIndex));
                    }

                    // store the previous name
                    previousName = name;

                    //System.out.println("Name: " + names[i] + ", Utility: " + utilities[i]);

                }

                // now we need to store the last item's pointer, which is NULL
                linkTable.addNextPointer(previousName, lineNumber, null);


                // end the line calculation,calculate the SEU
                //System.out.println("line: " + lineNumber +" :"+ totalUtility);
//                linkTable.addLineSEU(lineNumber, totalUtility);

                for(Integer key : SEULine.keySet()) {
                    if(SEUMap.containsKey(key)) {
                        SEUMap.put(key,SEUMap.get(key)+totalUtility);
                    }
                    else{
                        SEUMap.put(key,totalUtility);
                    }
                }
                // next line
                lineNumber++;
            }

            // print the linkTable
            //linkTable.printTest();

            // SEU Pruning

            MemoryLogger.getInstance().checkMemory();

            //System.out.println("minUtility: " + minUtility);

            linkTable.setSEUMap(SEUMap);

//            SEUPruningOnce(SEUMap, minUtility);
            SEUMap = null;
            SEUPruningPersist(minUtility);

            //TODO: CALCULATE THE REMAINING UTILITY

            // print the linkTable
//            linkTable.printTest();
            linkTable.calculateRemainUtility();
//            linkTable.printTest();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sumUtility;
    }

    private void ruleMining(){
        //TODO

        for ( Integer key : linkTable.getCandidateNames(this.minUtility)) {

            this.itemTableCount++;

            // push the first item
            miningSequence.addMiningItem(linkTable.getNameMiningItem(key));

            ruleMiningContinue();

            miningSequence.removeLast();
        }
    }

    private void ruleMiningContinue(){
        MiningItem nowItem = miningSequence.getLast();



        // Pruning
        if (nowItem.getUntilMaxRemainUtility()<minUtility){
            return;
        }

        Set<Integer> untilSet = miningSequence.getUntilSet();

        HashMap<Integer,MiningItem> candidateItems = findCandidateItems(nowItem,untilSet);
        if (candidateItems==null){
            return;
        }
        double lineUMax = 0;
        int chooseIndex = -1;
        // for every possible item
        for (Map.Entry<Integer,MiningItem> entry : candidateItems.entrySet()){

            this.itemTableCount++;

            MiningItem nextItem = entry.getValue();

            HashMap<Integer, ArrayList<Integer>> arrayIndexList = new HashMap<>();
            HashMap<Integer,ArrayList<Double>> utilityMap = new HashMap<>();
            HashMap<Integer, Integer> chooseIndexList = new HashMap<>();

            double untilMaxUtility=0;
            double untilMaxRemainUtility = 0;



            // for every line
            for(Map.Entry<Integer, ArrayList<Integer>> nextPlaces: nextItem.getOccurArrayIndex().entrySet()){
                // for every place of line
                double maxU = 0;
                int line = nextPlaces.getKey();
                for (int nextIndex : nextPlaces.getValue()){

                    double positionMaxU = 0;

                    // important
                    int nowPosition =0;

                    for(int nowIndex : nowItem.getOccurArrayByLine(line)){

                        if(linkTable.getLineIndexByNameLineArrayIndex(entry.getKey(),line,nextIndex)
                                > linkTable.getLineIndexByNameLineArrayIndex(nowItem.getName(),line,nowIndex)){

                            // now we find b is after a
                            //TODO:
                            double nowU = linkTable.getUtility(entry.getKey(),line,nextIndex)+nowItem.getUntilUtilityByArrayIndex(line,nowPosition);





                            if (!chooseIndexList.containsKey(line)){
                                ArrayList<Integer> temp1 = new ArrayList<>();
                                temp1.add(nextIndex);
                                arrayIndexList.put(line,temp1);

                                ArrayList<Double> temp2 = new ArrayList<>();
                                temp2.add(nowU);
                                utilityMap.put(line,temp2);


                                maxU = nowU;
                                positionMaxU = nowU;
                                untilMaxUtility += nowU;
                                chooseIndexList.put(line,nextIndex);
                            }
                            // this means more than one times occur
                            else {

                                // notice that index may occur only once, but may be found more than once
                                if (!arrayIndexList.get(line).contains(nextIndex)){
                                    arrayIndexList.get(line).add(nextIndex);
                                    utilityMap.get(line).add(nowU);
                                    positionMaxU = nowU;
                                    if (nowU>maxU){
                                        chooseIndexList.put(line,nextIndex);
                                        untilMaxUtility += (nowU-maxU);
                                        maxU = nowU;
                                    }
                                }
                                // when AAAB was found
                                else {
                                    if (nowU>positionMaxU){
                                        utilityMap.get(line).set(utilityMap.get(line).size()-1,nowU);
                                        positionMaxU = nowU;

                                        if (nowU>maxU){
                                            // chooseIndexList.put(line,nextIndex);
                                            untilMaxUtility += (nowU-maxU);
                                            maxU = nowU;
                                        }
                                    }


                                }



                            }





                        }
                        nowPosition++;

                    }
                }
                // now we  choose the first position of the nextItem

                NextPointer nexPointer = linkTable.getNextPointer(nextItem.getName(),line,arrayIndexList.get(line).get(0));


//                double remainLineU = linkTable.getRemainUtility(nextItem.getName(),line,arrayIndexList.get(line).get(0));

                HashMap<Integer,Double> remainMap = new HashMap<>();

                ArrayList<Integer> A = new ArrayList<>();
                ArrayList<Double> B = new ArrayList<>();

                A.add(nextItem.getName());
                B.add(linkTable.getUtility(nextItem.getName(),line,arrayIndexList.get(line).get(0)));

                while (nexPointer!=null){

                    if (!untilSet.contains(nexPointer.getName())){
                        A.add(nexPointer.getName());
                        B.add(linkTable.getUtility(nexPointer.getName(),nexPointer.getLine(),nexPointer.getIndex()));
                    }

                    nexPointer = linkTable.getNextNextPointer(nexPointer);
                }

                double[] remainU = maxRemainingUtility(A,B);
                double remainMax = 0;
                for(int i=0,j=0;i<A.size();i++){
                    if (A.get(i).equals(nextItem.getName())){
                        double nowRU = remainU[i] + utilityMap.get(line).get(j)-B.get(i);
                        j++;
                        if (nowRU>remainMax){
                            remainMax = nowRU;
                        }
                    }
                }

                untilMaxRemainUtility += remainMax;

            }

            MemoryLogger.getInstance().checkMemory();

            // TODO: 目前存在大问题，就是生成候选项没有排除已有的项
            nextItem.setUntilUtilityMap(utilityMap);
            nextItem.setOccurArrayIndex(arrayIndexList);
            nextItem.setChooseIndex(chooseIndexList);
            nextItem.setUntilMaxUtility(untilMaxUtility);
            nextItem.setUntilMaxRemainUtility(untilMaxRemainUtility);

            /*//PrintTest
            System.out.println("!!!!!!!!!!!!!!!!!!PrintTest!!!!!!!!!!!!!!!!!!");
            System.out.print("~~~~~~~~NowItems: ");
//            miningSequence.getLast().printTest();
            for (String t: miningSequence.getNameList()){
                System.out.print( t+" ");
            }
            System.out.println(" ~~~~~~~~");

            System.out.println("NextItem: " + nextItem.getName());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            nextItem.printTest();*/

            // next item is ready, PUSH
            miningSequence.addMiningItem(nextItem);
            // find the rule
            if(miningSequence.getLast().getUntilMaxUtility()>=minUtility && miningSequence.judgeProduce(minConfidence)){

                //boolean meas whether output the specific rule
                this.ruleCount += miningSequence.produceRule(false,minConfidence);
            }
            // recursion
            ruleMiningContinue();
            // remove the last, POP
            miningSequence.removeLast();







        }
        MemoryLogger.getInstance().checkMemory();


    }


    private HashMap<Integer,MiningItem> findCandidateItems(MiningItem first,Set<Integer> untilSet){
        Integer name = first.getName();
        if (first.getUntilMaxRemainUtility()<this.minUtility){
            return null;
        }

        HashMap<Integer,MiningItem> NextMap = new HashMap<>();


        // choose line
        for(int line : first.getChooseLineSet()){
            // important: if the line has more than one A, we find begin the first A
            NextPointer nextPointer = linkTable.getNextPointer(name,line,first.getOccurArrayIndex().get(line).get(0));
            while(nextPointer!=null){

                if (!untilSet.contains(nextPointer.getName())){

                    // prune strategy
                    if (first.getUntilMaxUtility()+linkTable.getNameMaxRemainUtilityByName(nextPointer.getName())>=minUtility){

                        addNextMap(nextPointer,NextMap);
                    }

                }

                nextPointer = linkTable.getNextNextPointer(nextPointer);
            }

        }

        // Candidate print test
        /*System.out.println("CandidateItems: " + NextMap.keySet());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        for (Map.Entry<String,MiningItem> entry : NextMap.entrySet()){
            System.out.println("next item: " + entry.getKey());
            entry.getValue().printTest();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }*/

        MemoryLogger.getInstance().checkMemory();
        return NextMap;
    }

    private void addNextMap(NextPointer nextPointer,HashMap<Integer,MiningItem> NextMap){
        Integer name = nextPointer.getName();
        int line = nextPointer.getLine(),arrayIndex = nextPointer.getIndex() ;
        if (NextMap.containsKey(name)){
            NextMap.get(name).addOccurArrayIndex(line,arrayIndex);
        }
        else{
            MiningItem t = new MiningItem(name);
            t.addOccurArrayIndex(line,arrayIndex);
            NextMap.put(name,t);
        }
    }





    /*
    String[] A = {"1", "2", "1", "3", "2", "1", "3", "2", "1"};
    double[] B = {3, 6, 1, 4, 6, 7, 3, 1, 3};
    double[] result = maxRemainingUtility(A, B);
    result = [13.0 17.0 11.0 17.0 16.0 11.0 7.0 4.0 3.0 ]
    */
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



    private void SEUPruningPersist(Double threshold){



        boolean flag;

        do{
            System.out.println("SEU persist pruning");
            flag = false;
//            HashMap<String,Double> SEUMap = new HashMap<>(linkTable.getSEUMap());

            // notice that the SEUMap is a reference to the linkTable's SEUMap
            HashMap<Integer,Double> SEUMap = linkTable.getSEUMap();

            Iterator<Map.Entry<Integer, Double>> iterator = SEUMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Double> entry = iterator.next();

//                System.out.println(entry.getKey() + " : " + entry.getValue());

                if (entry.getValue() < threshold) {
                    flag = true;
                    linkTable.deleteKey(entry.getKey());
                    iterator.remove();
                }
            }

        }while (flag);
    }


    public void printStats() throws IOException{
        double runTime =(double) (timeEnd - timeStart) / 1000;
        double maxMemory = MemoryLogger.getInstance().getMaxMemory();
        System.out.println("=============================================================================");
        System.out.println("--- Normal HUSRM algorithm for high utility sequential rule mining ---");
        System.out.println("=============================================================================");
        System.out.println("\tminutil: " + this.minUtility);
        System.out.println("\tSequential rules count: " + ruleCount);
        System.out.println("\tTotal time : " + runTime + " s");
        System.out.println("\tMax memory (mb) : "
                + maxMemory);
        System.out.println("\tNumber of utility table: " + itemTableCount);
        System.out.println("=============================================================================");
        StringBuilder buffer = new StringBuilder();

        buffer.append("\tTotalSR");
        // write the left side of the rule (the antecedent)
        buffer.append("\n\tminutil: ").append(this.minUtility);
        buffer.append("\n\tSequential rules count: ").append(ruleCount);
        buffer.append("\n\tTotal time : ").append(runTime).append(" s");
        buffer.append("\n\tMax memory (mb) : ").append(maxMemory);
        buffer.append("\n\tutility table count: ").append(itemTableCount);

        writer.write(buffer.toString());
        writer.newLine();
        writer.close();
    }


}

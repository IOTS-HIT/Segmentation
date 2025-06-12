import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TotalSRWithAnomalyDetection3 {

    long timeStart = 0;

    long timeEnd = 0;

    int ruleCount;

    private long candidateCount = 0;

    long itemTableCount = 0;


    double minConfidence;


    double minutil;

    int maxItem = -1;

    SequenceDatabaseWithUtility database;


    BufferedWriter writer = null;


    private int maximumRemoveTimes = Integer.MAX_VALUE;



    private OccurrenceDatabase occurrenceDatabase = new OccurrenceDatabase();

    private List<MiningUtilityTable> miningUtilityTables = new ArrayList<>();


    final boolean DEBUG = false;

    private int maxSup;
    private boolean deactivateStrategy1 = true;




    public TotalSRWithAnomalyDetection3() {
    }

    public void runAlgorithm(String input, String output,
                             double minConfidence, double minutilBegin,
                             int maximumNumberOfSequences) throws IOException {

        this.minConfidence = minConfidence;



        // reinitialize the number of rules found
        ruleCount = 0;
        this.minutil = minutilBegin;

        // We reset the tool for calculating the maximum memory usage
        MemoryLogger.getInstance().reset();

        // if the database was not loaded, then load it.
        if (database == null) {
            try {
                database = new SequenceDatabaseWithUtility();
                database.loadFile(input, maximumNumberOfSequences);
                if (minutil < 1) this.minutil = minutil * database.sumUtility;
                maxItem = database.getMaxItem();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        maxSup = database.getSequences().size();
        MemoryLogger.getInstance().checkMemory();

        System.out.println(this.minutil);

        // if in debug mode, we print the database to the console
        if (DEBUG) {
//            System.out.println("Without SEU");
//            database.print();
        }


        // we prepare the object for writing the output file
        Files.createDirectories(Paths.get(output).getParent());
        writer = new BufferedWriter(new FileWriter(output));

        // if minutil is 0, set it to 1 to avoid generating
        // all rules
        if (this.minutil == 0) {
            this.minutil = 0.001;
        }

        // save the start time
        timeStart = System.currentTimeMillis(); // for stats

        int removeCount = 0;
        int removeTimes = 0;

        // FIRST STEP: We will calculate the estimated profit of each single item

        // if this strategy has not been deactivated
        if (deactivateStrategy1 == false) {

            maxItem = -1;
            // This map will store pairs of (key: item   value: estimated profit of the item)
            Map<Integer, Double> mapItemEstimatedUtility = new HashMap<Integer, Double>();

            // We read the database.
            // For each sequence
            // 获取数据库中的每一条序列
            for (SequenceWithUtility sequence : database.getSequences()) {

                // for each itemset in that sequence
                for (List<Integer> itemset : sequence.getItemsets()) {

                    // for each item
                    for (Integer item : itemset) {
                        // get the current sequence estimated utility of that item
                        Double estimatedUtility = mapItemEstimatedUtility.get(item);
                        maxItem = Math.max(maxItem, item);
                        // if we did not see that item yet
                        if (estimatedUtility == null) {
                            // then its estimated utility of that item until now is the
                            // utility of that sequence
                            // 还没存呢，先存进去
                            estimatedUtility = sequence.exactUtility;

                        } else {
                            // otherwise, it is not the first time that we saw that item
                            // so we add the utility of that sequence to the sequence
                            // estimated utility f that item
                            estimatedUtility = estimatedUtility + sequence.exactUtility;
                        }

                        // update the estimated utility of that item in the map
                        mapItemEstimatedUtility.put(item, estimatedUtility);


                    }
                }
            }


            // we create an iterator to loop over all items
            Iterator<Map.Entry<Integer, Double>> iterator = mapItemEstimatedUtility.entrySet().iterator();
            // for each item
            while (iterator.hasNext()) {

                // we obtain the entry in the map
                Map.Entry<Integer, Double> entryMapItemEstimatedUtility
                        = (Map.Entry<Integer, Double>) iterator.next();
                Double estimatedUtility = entryMapItemEstimatedUtility.getValue();
//TWU<minutil, delete it
                // if the estimated utility of the current item is less than minutil
                if (estimatedUtility < minutil) {
                    removeCount++;
                    // we remove the item from the map
                    iterator.remove();
                }
            }

            double removeUtility = 0;
            // TWU more than one times
            while (removeTimes < maximumRemoveTimes) {
                // If not item can be removed
                if (removeCount == 0) {
                    break;
                }
                // update removeTimes
                removeTimes++;
                removeCount = 0;
                // scan the database again.
                // For each sequence
                Iterator<SequenceWithUtility> iteratorSequence = database.getSequences().iterator();
                while (iteratorSequence.hasNext()) {

                    SequenceWithUtility sequence = iteratorSequence.next();
                    // Initialize to 0
                    removeUtility = 0;

                    // For each itemset
                    Iterator<List<Integer>> iteratorItemset = sequence.getItemsets().iterator();
                    Iterator<List<Double>> iteratorItemsetUtilities = sequence.getUtilities().iterator();
                    while (iteratorItemset.hasNext()) {
                        // the items in that itemset
                        List<Integer> itemset = iteratorItemset.next();
                        // the utility values in that itemset
                        List<Double> itemsetUtilities = iteratorItemsetUtilities.next();

                        // Create an iterator over each item in that itemset
                        Iterator<Integer> iteratorItem = itemset.iterator();
                        // Create an iterator over utility values in that itemset
                        Iterator<Double> iteratorItemUtility = itemsetUtilities.iterator();

                        // For each item
                        while (iteratorItem.hasNext()) {
                            // get the item
                            Integer item = iteratorItem.next();
                            // get its utility value
                            Double utility = iteratorItemUtility.next();

                            // if the item is unpromising
                            if (mapItemEstimatedUtility.get(item) == null) {

                                // remove the item
                                iteratorItem.remove();
                                // remove its utility value
                                iteratorItemUtility.remove();
                                // subtract the item utility value from the sequence utility.
                                sequence.exactUtility -= utility;
                                // update removeUtility
                                removeUtility += utility;
                            }
                        }
                        //item set 清理在这一步进行
                        // If the itemset has become empty, we remove it from the sequence
                        if (itemset.isEmpty()) {
                            iteratorItemset.remove();
                            iteratorItemsetUtilities.remove();
                        }
                    }

                    // If the sequence has become empty, we remove the sequences from the database
                    // size = 1 means this sequence only has one itemset and could not produce any rules
                    if (sequence.size() == 0 || sequence.size() == 1) {
                        iteratorSequence.remove();
                    } else {
                        // update the SEU of all items
                        iteratorItemset = sequence.getItemsets().iterator();
                        while (iteratorItemset.hasNext()) {
                            // the items in that itemset
                            List<Integer> itemset = iteratorItemset.next();

                            // Create an iterator over each item in that itemset
                            Iterator<Integer> iteratorItem = itemset.iterator();

                            // For each item
                            while (iteratorItem.hasNext()) {
                                // get the item
                                Integer item = iteratorItem.next();
                                // Update the SEU of this item and determine if it should be removed
                                if (mapItemEstimatedUtility.get(item) - removeUtility < minutil) {
                                    removeCount++;
                                    mapItemEstimatedUtility.remove(item);
                                } else {
                                    mapItemEstimatedUtility.put(item, mapItemEstimatedUtility.get(item) - removeUtility);
                                }
                            }
                        }
                    }
                }
            }
//            maxSup = database.getSequences().size();
            mapItemEstimatedUtility = null;
        }

        // We create a map to store for each item, the list of sequences containing the item
        // Key: an item   Value:  the list of sequences containing the item
//        mapItemSequences = new HashMap<Integer, ListSequenceIDs>();
//
//        UPSL = new double[database.getSequences().size()][database.getLargestLength() + 1];


        // For each sequence
        for (int i = 0; i < database.getSequences().size(); i++) {
            // 获取第i个sequence
            SequenceWithUtility sequence = database.getSequences().get(i);
            // List<Integer> itemset : sequence.getItemsets()
            double prefixSum = 0;
            PointerTuple nextPosition = null;
            // only give a number
            Integer previous= null;
            int j;
            Integer item;
            for (j = 0; j < sequence.getItemsets().size(); j++) {
                //获取第j个 item
                if ( sequence.getItemsets().get(j).size() == 0) {
                    continue;
                }
                item = sequence.getItemsets().get(j).get(0);
                double profit =  sequence.getUtilities().get(j).get(0);
                prefixSum += profit;
                nextPosition = occurrenceDatabase.addElement(item,sequence.getUtilities().get(j).get(0),
                        sequence.exactUtility-prefixSum,nextPosition);
                if (previous != null){
                    occurrenceDatabase.addNextTuple(previous,nextPosition);
                }
                previous = item;
            }
            //the last nextTuples[j] = null 为了方便计算，最后一个位置的nextTuple设为null
            occurrenceDatabase.addNextTuple(previous,null);
        }
        MemoryLogger.getInstance().checkMemory();
        database=null;

        if (DEBUG){
//            occurrenceDatabase.print();
        }

        // Pruning strategy 1：初始项剪枝
        List<Integer> candidateFirstItem = occurrenceDatabase.getCandidateFirstItem(minutil);

        for (Integer firstItem : candidateFirstItem){
            OccurrenceTable firstItemTable =occurrenceDatabase.getOccurrenceTable(firstItem);

            miningUtilityTables.add(new MiningUtilityTable(
                    firstItem,
                    firstItemTable.getOccurrenceTimes(),
                    IntStream.rangeClosed(0, firstItemTable.getOccurrenceTimes() - 1).boxed().collect(Collectors.toList()),
                    firstItemTable.getTotalRightRemainUtility(),
                    firstItemTable.getTotalUtility(),
                    firstItemTable.getProfit()));
            itemTableCount++;
            MingRule();

            miningUtilityTables.remove(miningUtilityTables.size()-1);
        }
        // save the end time
        timeEnd = System.currentTimeMillis(); // for stats

    }

    private void MingRule(){

        MiningUtilityTable currentMiningUtilityTable = miningUtilityTables.get(miningUtilityTables.size()-1);

        if (miningUtilityTables.size()!=1){
            // Pruning strategy 2：可拓展剪枝
            if (currentMiningUtilityTable.getCurrentTotalUtility() + currentMiningUtilityTable.getRightRemainUtility() < minutil){
                return;
            }
            //此时要进行挖掘
            //TODO:挖掘
            //首先判断能不能产生规则
            int size = miningUtilityTables.size();

            if ( miningUtilityTables.get(size-1).currentTotalUtility>=minutil){

                double lastSupport = miningUtilityTables.get(size-1).getSupport();
                if (lastSupport >= miningUtilityTables.get(size-2).getSupport()*minConfidence){
                    lastSupport = (int)(lastSupport / minConfidence);
                    int left = 0;
                    int right = size - 1;
                    int result = -1;

                    while (left <= right) {
                        int mid = left + (right - left) / 2;

                        if (miningUtilityTables.get(mid).getSupport() <= lastSupport) {
                            result = mid;
                            right = mid - 1;
                        } else {
                            left = mid + 1;
                        }
                    }
                    ruleCount += (size-result-1);

                    if (DEBUG){
                        System.out.println("the result: "+result+"  size: "+size);
                        System.out.println("HIGH UTILITY SEQ. RULE  utility: "+miningUtilityTables.get(size-1).getCurrentTotalUtility()+"  :");
                        System.out.println("the support list: ");
                        for (int i = 0; i < size; i++) {
                            System.out.print(miningUtilityTables.get(i).getSupport()+" ");
                        }
                        System.out.println();
                        StringBuilder output = new StringBuilder();
                        int i;
                        for ( i = 0; i < size; i++) {
                            output.append(miningUtilityTables.get(i).getItem()).append(" ");
                            if (i == result) {
                                output.append(" --> ");
                            }
                        }
                        output.append("confidence: "+(double)miningUtilityTables.get(size - 1).getSupport() /miningUtilityTables.get(result).getSupport());

                        // 右移符号
                        int j;
                        for (i = result + 1; i < size; i++) {
                            output = new StringBuilder();
                            for (j = 0; j < size; j++) {
                                if (j == i) {
                                    output.append(" --> ");
                                }
                                output.append(miningUtilityTables.get(j).getItem()).append(" ");
                            }
                            output.append("confidence: "+(double)miningUtilityTables.get(size - 1).getSupport()/miningUtilityTables.get(i-1).getSupport());
                            System.out.println(output.toString());
                        }
                        System.out.println("============================");

                    }

                }
            }


        }

        Collection<MiningUtilityTable> candidateExpend = findItemExpendMiningUtilityTables(currentMiningUtilityTable);
        for (MiningUtilityTable expendItem : candidateExpend){
            miningUtilityTables.add(expendItem);
            MemoryLogger.getInstance().checkMemory();
            itemTableCount++;
            MingRule();
            miningUtilityTables.remove(miningUtilityTables.size()-1);
        }

    }

    private Collection<MiningUtilityTable> findItemExpendMiningUtilityTables(MiningUtilityTable miningUtilityTable) {

        Map<Integer,MiningUtilityTable> candidateExpend = new HashMap<>();
        List<Integer> XPointers = miningUtilityTable.getPointerPosition();//从小到大，有序

        OccurrenceTable XTable = occurrenceDatabase.getOccurrenceTable(miningUtilityTable.getItem());
        List<PointerTuple> nextTables = XTable.getNextTuples();

        for(int i=0;i<XPointers.size();i++){

            //XPointers.get(i) 得到的是X表中的位置
            PointerTuple nextplace = nextTables.get(XPointers.get(i));
            //不断向后找直到遍历完序列
            while(nextplace!=null){

                int Y = nextplace.getId();
                int place = nextplace.getInternalPosition();
                OccurrenceTable YTable = occurrenceDatabase.getOccurrenceTable(Y);

                if (candidateExpend.containsKey(Y)){
                    candidateExpend.get(Y).expendItem(
                            place,
                            YTable.getRightRemainUtilityOne(place),
                            YTable.getProfitOne(place)+miningUtilityTable.getProfitOne(i));



                } else {
                    List<Integer> Position = new ArrayList<>();
                    List<Double> Profits = new ArrayList<>();
                    Position.add(place);
                    Profits.add(YTable.getProfitOne(place)+miningUtilityTable.getProfitOne(i));
                    candidateExpend.put(Y,new MiningUtilityTable(
                            Y,
                            1,
                            Position,
                            YTable.getRightRemainUtilityOne(place),
                            Profits.get(0),
                            Profits));
                }

                nextplace = YTable.getPointerTuple(place);

            }

        }
        MemoryLogger.getInstance().checkMemory();
        Iterator<Map.Entry<Integer,MiningUtilityTable>> iterator = candidateExpend.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer,MiningUtilityTable> entry = iterator.next();

            // pruning strategy 3: 对于候选扩展项x，要保证x扩展后有希望成为高效用模式
            if (entry.getValue().getCurrentTotalUtility()+entry.getValue().getRightRemainUtility() < minutil ) {
                iterator.remove(); // 使用iterator的remove方法删除元素
            }
        }

        return  candidateExpend.values();
    }

    //不好的方法，别用了
//    private MiningUtilityTable findOccurrenceXandY(MiningUtilityTable X,Integer Y){
//
//        OccurrenceTable YTable = occurrenceDatabase.getOccurrenceTable(Y);
//        List<Integer> XPointers = X.getPointerPosition();//从小到大，有序
//        OccurrenceTable XTable = occurrenceDatabase.getOccurrenceTable(X.getItem());
//
//        MiningUtilityTable candidateExpendItem = new MiningUtilityTable(Y);
//        int temp,left,right,mid,xSequenceId,ySequenceId;
//        if(XPointers.size()<= YTable.getOccurrenceTimes()){     //X表小，Y表大
//            temp=0;
//            for(int i=0;i<XPointers.size();i++){
//
//                left = temp; right=YTable.getOccurrenceTimes() - 1;
//                while (left <= right) {
//
//                    mid = (right + left) / 2;
//                    ySequenceId = YTable.getSequencePositionOne(mid);
//                    xSequenceId = XTable.getSequencePositionOne(XPointers.get(i));
//                    if (ySequenceId == xSequenceId) {
//                        //find it
//                        temp = mid;
//                        if (YTable.getInternalPositionOne(mid) > XTable.getInternalPositionOne(XPointers.get(i))){
//                            //MiningUtilityTable拓展项
//                            candidateExpendItem.expendItem(YTable.getInternalPositionOne(mid),
//                                    YTable.getRightRemainUtilityOne(mid),
//                                    X.getProfitOne(i)+YTable.getProfitOne(mid));
//                        }
//                        break; //while 循环退出
//
//                    } else if (ySequenceId < xSequenceId) {
//                        left = mid + 1;
//                    } else {
//                        right = mid - 1;
//                    }
//                }
//            }
//
//        } else {          //X表大，Y表小，此时对Y做遍历，X做二分查找
//            temp=0;
//            for(int i=0;i<YTable.getOccurrenceTimes();i++){
//
//                left = temp; right=XPointers.size() - 1;
//                while (left <= right) {
//
//                    mid = (right + left) / 2;
//                    ySequenceId = YTable.getSequencePositionOne(i);
//                    xSequenceId = XTable.getSequencePositionOne(XPointers.get(mid));
//                    if (ySequenceId == xSequenceId) {
//                        //find it
//                        temp = mid;
//                        if (YTable.getInternalPositionOne(i) > XTable.getInternalPositionOne(XPointers.get(mid))){
//                            //MiningUtilityTable拓展项
//                            candidateExpendItem.expendItem(YTable.getInternalPositionOne(i),
//                                    YTable.getRightRemainUtilityOne(i),
//                                    X.getProfitOne(mid)+YTable.getProfitOne(i));
//                        }
//                        break; //while 循环退出
//
//                    } else if (ySequenceId < xSequenceId) {
//                        right = mid - 1;
//                    } else {
//                        left = mid + 1;
//                    }
//                }
//            }
//        }
//
//
//        return candidateExpendItem;
//    }



    /**
     * Print statistics about the last algorithm execution to System.out.
     */
    public void printStats() throws IOException{
        double runTime =(double) (timeEnd - timeStart) / 1000;
        double maxMemory = MemoryLogger.getInstance().getMaxMemory();
        System.out.println("=============================================================================");
        System.out.println("--- 递归算法 algorithm for high utility sequential rule mining ---");
        System.out.println("=============================================================================");
        System.out.println("\tminutil: " + minutil);
        System.out.println("\tSequential rules count: " + ruleCount);
        System.out.println("\tTotal time : " + runTime + " s");
        System.out.println("\tMax memory (mb) : "
                + maxMemory);
        System.out.println("\texpand time count: " + candidateCount);
        System.out.println("\tNumber of utility table: " + itemTableCount);
        System.out.println("=============================================================================");
        StringBuilder buffer = new StringBuilder();

        buffer.append("\tTotalSR");
        // write the left side of the rule (the antecedent)
        buffer.append("\n\tminutil: ").append(minutil);
        buffer.append("\n\tSequential rules count: ").append(ruleCount);
        buffer.append("\n\tTotal time : ").append(runTime).append(" s");
        buffer.append("\n\tMax memory (mb) : ").append(maxMemory);
        buffer.append("\n\texpand times count: ").append(candidateCount);
        buffer.append("\n\tutility table count: ").append(itemTableCount);

        writer.write(buffer.toString());
        writer.newLine();
        writer.close();
    }
}


import java.io.*;
import java.util.*;


public class SequenceDatabaseWithUtility {

    private List<SequenceWithUtility> sequences = new ArrayList<SequenceWithUtility>();

    Map<Integer, Double> mapItemEstimatedUtility = new HashMap<Integer, Double>();


    public double sumUtility = 0;

    public double loadFile(String path) throws IOException {


        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null && !line.equals("")) {

                SequenceWithUtility sequence = new SequenceWithUtility(lineNumber);

                // 外层循环：读取每一行的数据
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
                double utility;

                //用于计算剩余效用
                //用于SEU计算
                //Integer : index of the utilities
                HashMap<Integer,Double> SEULine = new HashMap<>();

                // for the line
                for (int i =0; i < items.length-1; i++) {


                    parts = items[i].split("\\[|\\]");
                    name = Integer.parseInt(parts[0]);
                    utility = Double.parseDouble(parts[1]);



                    sequence.addItem(name);
                    sequence.addItemsetProfit(utility);



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
                }

                for(Integer key : SEULine.keySet()) {
                    if(mapItemEstimatedUtility.containsKey(key)) {
                        mapItemEstimatedUtility.put(key,mapItemEstimatedUtility.get(key)+totalUtility);
                    }
                    else{
                        mapItemEstimatedUtility.put(key,totalUtility);
                    }
                }

                lineNumber++;
                sequence.exactUtility = totalUtility;
                sequences.add(sequence);

            }

            // print the linkTable
            //linkTable.printTest();

            // SEU Pruning

            MemoryLogger.getInstance().checkMemory();



        } catch (IOException e) {
            e.printStackTrace();
        }
        return sumUtility;
    }

    public void SEUPrune(double minutil){

        Iterator<Map.Entry<Integer, Double>> iterator = mapItemEstimatedUtility.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Double> entryMapItemEstimatedUtility =  iterator.next();
            Double estimatedUtility = entryMapItemEstimatedUtility.getValue();
            if (estimatedUtility < minutil) {
                iterator.remove();
            }
        }

        boolean removeJudge = true;


        while (removeJudge) {

            Iterator<SequenceWithUtility> iteratorSequence = sequences.iterator();

            removeJudge = false;

            while (iteratorSequence.hasNext()) {

                SequenceWithUtility sequence = iteratorSequence.next();

                HashMap<Integer,Double> SEULine = new HashMap<>();

                // For each item
                Iterator<Integer> iteratorItems = sequence.getItemsets().iterator();
                Iterator<Double> iteratorUtilities = sequence.getUtilities().iterator();

                while (iteratorItems.hasNext()) {

                    Integer item = iteratorItems.next();

                    Double utility = iteratorUtilities.next();

                    if (mapItemEstimatedUtility.get(item) == null) {

                        // remove the item
                        iteratorItems.remove();
                        // remove its utility value
                        iteratorUtilities.remove();

                    }
                    else{

                        if(SEULine.containsKey(item)){

                            //TODO:是不是这里？
                            if(SEULine.get(item)<utility){
                                SEULine.put(item,utility);
                            }

                        }
                        else{
                            SEULine.put(item,utility);
                        }

                    }

                }


                if (sequence.size() == 0 || sequence.size() == 1) {

                    for (Integer key : SEULine.keySet()) {
                        if (mapItemEstimatedUtility.get(key) - sequence.exactUtility < minutil) {
                            removeJudge = true;
                            mapItemEstimatedUtility.remove(key);
                        } else {
                            mapItemEstimatedUtility.put(key, mapItemEstimatedUtility.get(key) - sequence.exactUtility);
                        }

                    }
                    iteratorSequence.remove();
                }
                else{

                    double removeU = sequence.exactUtility - SEULine.values().stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();

                    for (Integer key : SEULine.keySet()) {

                        if (mapItemEstimatedUtility.get(key) - removeU < minutil) {
                            removeJudge = true;
                            mapItemEstimatedUtility.remove(key);
                        } else {
                            mapItemEstimatedUtility.put(key, mapItemEstimatedUtility.get(key) - removeU);
                        }

                    }
                    sequence.exactUtility -= removeU;

                }

            }
        }

    }

    public HashMap<Integer,Double> calculateRemainUtility(){
        HashMap<Integer,Double> nameMaxRemainUtility = new HashMap<>();
        for (SequenceWithUtility t : sequences){

            HashMap<Integer,Double> RLine = new HashMap<>();

            List<Integer> A = t.getItemsets();
            List<Double> B = t.getUtilities();
            int n = A.size();
            double[] result = new double[n];
            Map<Integer, Double> utilityMap = new HashMap<>();
            int item;
            double utility=0.0;
            double totalUtility=0.0;
            // 从后向前遍历数组
            for (int i = n - 1; i >= 0; i--) {
                // 当前元素
                item = A.get(i);
                utility = B.get(i);

                if (!utilityMap.containsKey(item)) {
                    utilityMap.put(item, utility);
                    totalUtility += utility;
                } else if (utilityMap.get(item) < utility) {
                    totalUtility += (utility - utilityMap.get(item));
                    utilityMap.put(item, utility);
                }
                result[i] = totalUtility;

                if (utilityMap.containsKey(item)) {
                    result[i] -= utilityMap.get(item);
                    result[i] += utility;
                }

                if (RLine.containsKey(item)) {
                    if (RLine.get(item) < result[i]) {
                        RLine.put(item, result[i]);
                    }
                } else {
                    RLine.put(item, result[i]);
                }

            }

            for (Integer key : RLine.keySet()) {

                if (nameMaxRemainUtility.containsKey(key)) {
                    nameMaxRemainUtility.put(key, nameMaxRemainUtility.get(key)+RLine.get(key));
                } else {
                    nameMaxRemainUtility.put(key, RLine.get(key));
                }
            }



            t.setRemainUtility(result);
        }
        return nameMaxRemainUtility;
    }





    public int size() {
        return sequences.size();
    }

    /**
     * Get the sequences from this sequence database.
     * @return A list of sequences (Sequence).
     */
    public List<SequenceWithUtility> getSequences() {
        return sequences;
    }


    public void print() {
        for (SequenceWithUtility sequence : sequences) {
            System.out.print(sequence.getId() + ":  ");
            sequence.print();
            System.out.println("");
        }
    }
}
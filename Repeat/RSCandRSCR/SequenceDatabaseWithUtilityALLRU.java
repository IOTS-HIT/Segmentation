import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class SequenceDatabaseWithUtilityALLRU {

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
                HashSet<Integer> SEULine = new HashSet<>();

                // for the line
                for (int i =0; i < items.length-1; i++) {


                    parts = items[i].split("\\[|\\]");
                    name = Integer.parseInt(parts[0]);
                    utility = Double.parseDouble(parts[1]);



                    sequence.addItem(name);
                    sequence.addItemsetProfit(utility);



                    //用于计算SEU
                    SEULine.add(name);

                }

                for(Integer key : SEULine) {
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



                // For each item
                Iterator<Integer> iteratorItems = sequence.getItemsets().iterator();
                Iterator<Double> iteratorUtilities = sequence.getUtilities().iterator();
                double removeU = 0;
                HashSet<Integer> SEULine = new HashSet<>();

                while (iteratorItems.hasNext()) {

                    Integer item = iteratorItems.next();

                    Double utility = iteratorUtilities.next();



                    if (mapItemEstimatedUtility.get(item) == null) {

                        removeU += utility;

                        // remove the item
                        iteratorItems.remove();
                        // remove its utility value
                        iteratorUtilities.remove();

                    }
                    else {
                        SEULine.add(item);
                    }


                }


                if (sequence.size() == 0 || sequence.size() == 1 ) {

                    for (Integer key : SEULine) {
                        if (mapItemEstimatedUtility.get(key) - sequence.exactUtility < minutil) {
                            removeJudge = true;
                            mapItemEstimatedUtility.remove(key);
                        } else {
                            mapItemEstimatedUtility.put(key, mapItemEstimatedUtility.get(key) - sequence.exactUtility);
                        }

                    }
                    iteratorSequence.remove();
                }
                else {


                    for (Integer key : SEULine) {

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

            int item;
            double utility=0.0;
            double totalUtility=t.exactUtility;
            double untilU = 0;
            // 从后向前遍历数组
            for (int i = 0; i <n; i++) {
                // 当前元素
                item = A.get(i);
                utility = B.get(i);
                untilU += utility;

                result[i] = totalUtility - untilU;

                if (!RLine.containsKey(item)){
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
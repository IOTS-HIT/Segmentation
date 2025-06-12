import java.util.HashMap;
import java.util.Map;

public class findRemain {
    public static double[] maxRemainingUtility(int[] A, double[] B) {
        int n = A.length;
        double[] result = new double[n];
        Map<Integer, Double> utilityMap = new HashMap<>();

        int item;
        double utility=0.0;
        double totalUtility=0.0;
        // 从后向前遍历数组
        for (int i = n - 1; i >= 0; i--) {
            // 当前元素
            item = A[i];
            utility = B[i];

            if(!utilityMap.containsKey(item)){
                utilityMap.put(item, utility);
                totalUtility += utility;
            }
            else if (utilityMap.get(item) < utility) {
                totalUtility += (utility-utilityMap.get(item));
                utilityMap.put(item, utility);
            }
            result[i] = totalUtility;

            if(utilityMap.containsKey(item)){
                result[i] -= utilityMap.get(item);
                result[i] += utility;
            }



        }

        return result;
    }

    public static void main(String[] args) {
        int[] A = {1, 2, 1, 3, 2, 1, 3, 2, 1};
        double[] B = {3, 6, 1, 4, 6, 7, 3, 1, 3};
        double[] result = maxRemainingUtility(A, B);
        // result = [13.0 17.0 11.0 17.0 16.0 11.0 7.0 4.0 3.0 ]
        // 打印结果
        for (double utility : result) {
            System.out.print(utility + " ");
        }
    }
}

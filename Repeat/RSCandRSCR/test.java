import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class test {
    public static double[] maxRemainingUtility(String[] A, double[] B) {
        int n = A.length;
        double[] result = new double[n];
        Map<String, Double> utilityMap = new HashMap<>();

        String item;
        double utility;
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
    public static boolean sdw(int a , int b){
        System.out.println((double)a/ b);
        return (double)a> b ;
    }

    public static void main(String[] args) {



       ArrayList<Integer> ss =  IntStream.rangeClosed(0, 0).boxed().collect(Collectors.toCollection(ArrayList::new));
       System.out.println(ss);

       A a = new A(1);
       int b = a.getA();
       b++;
       System.out.println(a.getA());
       if (sdw(1,3)){
           System.out.println("true");
       }
       else{
           System.out.println("false");
       }

    }


}

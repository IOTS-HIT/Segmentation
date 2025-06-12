
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SequenceWithUtility {
    // this is the list of itemsets contained in this sequence
    // (each itemset is a list of Integers)
    private final List<Integer> itemsets = new ArrayList<Integer>();

    // this is the list of utility values corresponding to each item in the sequence
    private final List<Double> profits = new ArrayList<Double>();

    private final List<Double> remainUtility = new ArrayList<Double>();

    // this is a unique sequence id
    private int id;

    // this is the sequence utility (the sum of the utility of each item in that sequence)
    public double exactUtility;


    public void setRemainUtility(double[] remainList){
        for (double d : remainList){
            remainUtility.add(d);
        }
    }








    /**
     * This method returns the list of utility values for all items in that sequence.
     * @return A list of list of doubles.  The first list represents the itemsets. Each itemset is a list
     * of Double where double values indicate the utility of each item.
     */
    public List<Double> getUtilities() {
        return profits;
    }

    /**
     * Constructor. This method creates a sequence with a given id.
     * @param id the id of this sequence.
     */
    public SequenceWithUtility(int id) {
        this.id = id;
    }

    /**
     * Add an itemset to this sequence.
     * @param itemset An itemset (list of integers, where integers represent the items)
     */
    public void addItem(Integer itemset) {
        itemsets.add(itemset);
    }


    public void addItemsetProfit(Double utilityValue) {
        profits.add(utilityValue);
    }



    /**
     * Get the sequence ID of this sequence.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the list of itemsets in this sequence.
     * @return the list of itemsets. Each itemset is a list of Integers.
     */
    public List<Integer> getItemsets() {
        return itemsets;
    }

    /**
     * Get the i-th itemset in this sequence.
     * @param index a positive integer i
     * @return the i-th itemset as a list of integers.
     */
    public Integer get(int index) {
        return itemsets.get(index);
    }


    public List<Double> getRemainUtility() {
        return remainUtility;
    }


    /**
     * Get the size of this sequence (number of itemsets).
     * @return the size (an integer).
     */
    public int size() {
        return itemsets.size();
    }

    public void print(){
        for (int i = 0; i < itemsets.size(); i++){
            System.out.print("item: "+itemsets.get(i) + " ");
            System.out.println("utility: "+profits.get(i) + " ");

        }
    }

}


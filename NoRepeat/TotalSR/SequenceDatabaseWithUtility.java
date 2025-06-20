
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class SequenceDatabaseWithUtility {
    // variable that contains the sequences of this database
    private List<SequenceWithUtility> sequences = new ArrayList<SequenceWithUtility>();

    private int maxItem = 0;
    private int largestLength = 0;

    public double sumUtility = 0;

    /**
     * Method to load a sequence database from a text file in SPMF format.
     * @param path  the input file path.
     * @param maximumNumberOfSequences the maximum number of sequences to be read
     * @throws IOException exception if error while reading the file.
     */
    public void loadFile(String path, int maximumNumberOfSequences) throws IOException {
        String thisLine; // variable to read each line.
        BufferedReader myInput = null;
        try {
            FileInputStream fin = new FileInputStream(new File(path));
            myInput = new BufferedReader(new InputStreamReader(fin));
            // for each line until the end of the file
            int i = 0;
            while ((thisLine = myInput.readLine()) != null) {
                // if the line is not a comment, is not empty or is not other
                // kind of metadata
                if (thisLine.isEmpty() == false &&
                        thisLine.charAt(0) != '#' && thisLine.charAt(0) != '%'
                        && thisLine.charAt(0) != '@') {
                    // split this line according to spaces and process the line
                    String [] split = thisLine.split(" ");
                    largestLength = Math.max(largestLength, addSequence(split, 0));
                    i++;    //记录序列的数目
                    // if we reached the maximum number of lines, we stop reading
                    if(i == maximumNumberOfSequences){
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                // close the file
                myInput.close();
            }
        }
    }

    /**
     * Method to process a line from the input file
     * @param tokens A list of tokens from the line (which were separated by spaces in the original file).
     */
    int addSequence(String[] tokens, int length) {

        // This set is used to remember items that we have seen already
        Set<Integer> alreadySeenItems = new HashSet<Integer>();

        // This variable is to count the utility of items that appear twice in the sequence.
        // The reason why we count this, is that HUSRM does not allow items to appear twice or more in the same
        // sequence. Thus, we have to count the extra utility of items appearing more than once and
        // subtract this utility from the utility of the sequence and remove these extra occurrences
        // of items.
        int profitExtraItemOccurrences = 0;

        // create a new Sequence to store the sequence
        SequenceWithUtility sequence = new SequenceWithUtility(sequences.size());   //sequences.size()==序列的sid

        // create a list of integers to store the items of the current  itemset.
        List<Integer> itemset = new ArrayList<Integer>();
        // create a list of double values to store the utility of each item in the current itemset
        List<Double> itemsetProfit = new ArrayList<Double>();

        // for each token in this line ======> tokens 为这一行的序列
        for (String token : tokens) {

            // if this token is not empty
            if(token.isEmpty()){
                continue;
            }
            // if the token is -1, it means that we reached the end of an itemset.
            if( token.charAt(0) == 'S') {
                String[] strings = token.split(":");  //
                String exactUtility = strings[1];
                sequence.exactUtility = Double.parseDouble(exactUtility) - profitExtraItemOccurrences;
            }
            // if it is the end of an itemset
            else if (token.equals("-1")) {
                // add the current itemset to the sequence
                sequence.addItemset(itemset);
                // add the utility of this itemset to the sequence
                sequence.addItemsetProfit(itemsetProfit);

                // create a new itemset
                itemset = new ArrayList<Integer>();
                itemsetProfit = new ArrayList<Double>();
            }
            // if the token is -2, it means that we reached the end of
            // the sequence.
            else if (token.equals("-2")) {
                // we add it to the list of sequences
                sequence.setItems(alreadySeenItems);
                sequences.add(sequence);
            } else {
                // otherwise it is an item.
                // we parse it as an integer and add it to
                // the current itemset.
                String[] strings = token.split("\\[");  //
                String item = strings[0];
                int itemInt = Integer.parseInt(item);

                //***************************************************************
                //***************************************************************
                //***************************************************************
                //***************************************************************
                // If an itemset was already seen in this sequence, we don't keep
                // the current occurrence. The reason is that HUSRM does not handle
                // items that appear more than once in a sequence.
                //***************************************************************
                //***************************************************************
                //***************************************************************
                if(alreadySeenItems.contains(itemInt) == false){

                    // if this is the first time that we see that item
                    String profit = strings[1];
                    String profitWithoutBrackets = profit.substring(0, profit.length()-1);

                    // we add it to the current itemset
                    itemset.add(itemInt);

                    maxItem = Math.max(maxItem, itemInt);
                    length++;

                    // we add its utility to the current itemset
                    itemsetProfit.add(Double.parseDouble(profitWithoutBrackets));

                    // we remember that we have seen this item once in that sequence
                    alreadySeenItems.add(itemInt);
                }else{
                    // if it is not the first time that we see that item
                    // we will not add it to the current itemset
                    String profit = strings[1];
                    String profitWithoutBrackets = profit.substring(0, profit.length()-1);
                    // but we will remember its utility to subtract it from the sequence utility
                    profitExtraItemOccurrences += Double.parseDouble(profitWithoutBrackets);
                }
            }
        }
        sumUtility += sequence.exactUtility;
        return length;
    }

    /**
     * Method to add a sequence to this sequence database
     * @param sequence A sequence of type "Sequence".
     */
    public void addSequence(SequenceWithUtility sequence) {
        sequences.add(sequence);
    }

    /**
     * Print this sequence database to System.out.
     */
    public void print() {
        System.out.println("============  SEQUENCE DATABASE ==========");
        // for each sequence
        for (SequenceWithUtility sequence : sequences) {

            // print the sequence id
            System.out.print(sequence.getId() + ":  ");

            // then print the sequence
            sequence.print();
            System.out.println("");

        }
    }

    /**
     * Print statistics about this database.
     */
    public void printDatabaseStats() {
        System.out.println("============  STATS ==========");
        System.out.println("Number of sequences : " + sequences.size());

        // Calculate the average size of sequences in this database
        long size = 0;
        for(SequenceWithUtility sequence : sequences){
            size += sequence.size();
        }
        double meansize = ((float)size) / ((float)sequences.size());
        System.out.println("mean size" + meansize);
    }

    /**
     * Return a string representation of this sequence database.
     */
    public String toString() {
        StringBuilder r = new StringBuilder();
        // for each sequence
        for (SequenceWithUtility sequence : sequences) {
            r.append(sequence.getId());
            r.append(":  ");
            r.append(sequence.toString());
            r.append('\n');
        }
        return r.toString();
    }

    /**
     * Get the sequence count in this database.
     * @return the sequence count.
     */
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

    public int getMaxItem() {
        return maxItem;
    }

    public int getLargestLength(){
        return largestLength;
    }
    /**
     * Get the list of sequence IDs for this database.
     * @return A set containing the sequence IDs of sequence in this
     * database.
     */
    public Set<Integer> getSequenceIDs() {
        // create a set
        Set<Integer> set = new HashSet<Integer>();
        // for each sequence
        for (SequenceWithUtility sequence : getSequences()) {
            set.add(sequence.getId()); // add the id to the set.
        }
        return set; // return the set.
    }
}
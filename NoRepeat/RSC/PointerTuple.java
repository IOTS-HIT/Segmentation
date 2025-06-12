public class PointerTuple {
    private int id;
    private int internalPosition;

    public int getId() {
        return id;
    }
    public int getInternalPosition(){
        return internalPosition;
    }
    public PointerTuple(int id, int internalPosition){
        this.id = id;
        this.internalPosition = internalPosition;
    }
}

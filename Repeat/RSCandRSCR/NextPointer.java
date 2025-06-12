public class NextPointer {
    private Integer name;

    private Integer line;

    //这里特指ArrayList中的第几个，而不是行中的第几个
    private Integer listIndex;

    public NextPointer(Integer name, Integer line, Integer index) {
        this.name = name;
        this.line = line;
        this.listIndex = index;
    }

    public Integer getName() {
        return this.name;
    }

    public Integer getLine() {
        return this.line;
    }

    public Integer getIndex() {
        return this.listIndex;
    }

}

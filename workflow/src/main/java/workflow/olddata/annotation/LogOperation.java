package workflow.olddata.annotation;

public enum  LogOperation{

    Add("add",1),Delete("delete",2),Query("query",3),Modify("modify",4),BatchDelete("batchDelete",5),Other("other",5 );

    private String name;
    private int index;

    private LogOperation(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

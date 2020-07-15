package generator;
import multiblock.Multiblock;
public abstract class Priority<T extends Multiblock>{
    public final String name;
    public Priority(String name){
        this.name = name;
    }
    @Override
    public String toString(){
        return name;
    }
    public final double compare(T main, T other){
        if(main==null&&other==null)return 0;
        if(main==null&&other!=null)return -1;
        if(main!=null&&other==null)return 1;
        return doCompare(main, other);
    }
    protected abstract double doCompare(T main, T other);
}
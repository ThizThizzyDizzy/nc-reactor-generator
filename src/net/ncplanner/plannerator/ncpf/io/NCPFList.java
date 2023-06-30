package net.ncplanner.plannerator.ncpf.io;
import java.util.ArrayList;
public class NCPFList<T> extends ArrayList<T>{
    public NCPFObject getNCPFObject(int i){
        Object o = get(i);
        if(o instanceof NCPFObject)return (NCPFObject)o;
        return null;
    }
    public NCPFList getNCPFList(int i){
        Object o = get(i);
        if(o instanceof NCPFList)return (NCPFList)o;
        return null;
    }
    public String getString(int i){
        Object o = get(i);
        if(o instanceof String)return (String)o;
        return null;
    }
    public Boolean getBoolean(int i){
        Object o = get(i);
        if(o instanceof Boolean)return (Boolean)o;
        return null;
    }
    public Double getDouble(int i){
        Object o = get(i);
        if(o instanceof Number)return ((Number)o).doubleValue();
        return null;
    }
    public Float getFloat(int i){
        Object o = get(i);
        if(o instanceof Number)return ((Number)o).floatValue();
        return null;
    }
    public Integer getInteger(int i){
        Object o = get(i);
        if(o instanceof Number)return ((Number)o).intValue();
        return null;
    }
    public Long getLong(int i){
        Object o = get(i);
        if(o instanceof Number)return ((Number)o).longValue();
        return null;
    }
    @Override
    public boolean add(T e){
        if(e==null)return true;
        return super.add(e);
    }
}
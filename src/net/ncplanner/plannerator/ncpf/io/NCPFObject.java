package net.ncplanner.plannerator.ncpf.io;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
public class NCPFObject extends HashMap<String, Object>{
    public <T extends DefinedNCPFObject> T getDefinedNCPFObject(String key, Supplier<T> supplier){
        T object = supplier.get();
        NCPFObject ncpf = getNCPFObject(key);
        object.convertFromObject(ncpf);
        return object;
    }
    public <T extends DefinedNCPFObject, V extends List<T>> V getDefinedNCPFList(String key, Supplier<T> objectSupplier){
        return (V)getDefinedNCPFList(key, new ArrayList<>(), objectSupplier);
    }
    public <T extends DefinedNCPFObject, V extends List<T>> V getDefinedNCPFList(String key, V list, Supplier<T> objectSupplier){
        NCPFList ncpf = getNCPFList(key);
        for(int i = 0; i<ncpf.size(); i++){
            NCPFObject obj = ncpf.getNCPFObject(i);
            T object = objectSupplier.get();
            object.convertFromObject(obj);
            list.add(object);
        }
        return list;
    }
    
    public <T extends DefinedNCPFObject> void getDefined3DArray(String name, T[][][] array, List<T> indicies){
        NCPFList list = getNCPFList(name);
        int i = 0;
        for(int x = 0; x<=array.length; x++){
            for(int y = 0; y<=array[x].length; y++){
                for(int z = 0; z<=array[z].length; z++){
                    array[x][y][z] = indicies.get(list.getInteger(i++));
                }
            }
        }
    }
    public <T extends DefinedNCPFObject> void setDefined3DArray(String name, T[][][] array, List<T> indicies){
        NCPFList<Integer> list = new NCPFList<>();
        for(int x = 0; x<=array.length; x++){
            for(int y = 0; y<=array[x].length; y++){
                for(int z = 0; z<=array[z].length; z++){
                    list.add(indicies.indexOf(array[x][y][z]));
                }
            }
        }
        setNCPFList(name, list);
    }
    
    public <T extends DefinedNCPFModularObject> void getRecipe3DArray(String name, NCPFElement[][][] array, T[][][] design){
        NCPFList list = getNCPFList(name);
        int r = -1;
        for(int x = 0; x<=design.length; x++){
            for(int y = 0; y<=design[x].length; y++){
                for(int z = 0; z<=design[z].length; z++){
                    if(design[x][y][z].hasModule(NCPFBlockRecipesModule::new)){
                        array[x][y][z] = design[x][y][z].getModule(NCPFBlockRecipesModule::new).recipes.get(list.getInteger(++r));
                    }
                }
            }
        }
    }
    public <T extends DefinedNCPFModularObject> void setRecipe3DArray(String name, NCPFElement[][][] array, T[][][] design){
        NCPFList list = getNCPFList(name);
        for(int x = 0; x<=design.length; x++){
            for(int y = 0; y<=design[x].length; y++){
                for(int z = 0; z<=design[z].length; z++){
                    if(design[x][y][z].hasModule(NCPFBlockRecipesModule::new)){
                        list.add(design[x][y][z].getModule(NCPFBlockRecipesModule::new).recipes.indexOf(array[x][y][z]));
                    }
                }
            }
        }
        setNCPFList(name, list);
    }
    
    public void setDefinedNCPFObject(String key, DefinedNCPFObject object){
        NCPFObject ncpf = new NCPFObject();
        object.convertToObject(ncpf);
        setNCPFObject(key, ncpf);
    }
    public <T extends DefinedNCPFObject> void setDefinedNCPFList(String key, List<T> list){
        NCPFList<NCPFObject> ncpf = new NCPFList<>();
        for(DefinedNCPFObject obj : list){
            NCPFObject object = new NCPFObject();
            obj.convertToObject(object);
            ncpf.add(object);
        }
        setNCPFList(key, ncpf);
    }
    
    public NCPFObject getNCPFObject(String key){
        Object o = get(key);
        if(o instanceof NCPFObject)return (NCPFObject)o;
        return null;
    }
    public NCPFList getNCPFList(String key){
        Object o = get(key);
        if(o instanceof NCPFList)return (NCPFList)o;
        return null;
    }
    public String getString(String key){
        Object o = get(key);
        if(o instanceof String)return (String)o;
        return null;
    }
    public Boolean getBoolean(String key){
        Object o = get(key);
        if(o instanceof Boolean)return (Boolean)o;
        return null;
    }
    public Double getDouble(String key){
        Object o = get(key);
        if(o instanceof Number)return ((Number)o).doubleValue();
        return null;
    }
    public Float getFloat(String key){
        Object o = get(key);
        if(o instanceof Number)return ((Number)o).floatValue();
        return null;
    }
    public Integer getInteger(String key){
        Object o = get(key);
        if(o instanceof Number)return ((Number)o).intValue();
        return null;
    }
    public Long getLong(String key){
        Object o = get(key);
        if(o instanceof Number)return ((Number)o).longValue();
        return null;
    }
    //because I like set instead of put
    public Object set(String key, Object value){
        if(value==null)return remove(key);//never hold nulls!
        return put(key, value);
    }
    
    //just so I don't change types accidentally
    public void setNCPFObject(String key, NCPFObject value){
        set(key, value);
    }
    public void setNCPFList(String key, NCPFList value){
        set(key, value);
    }
    public void setString(String key, String value){
        set(key, value);
    }
    public void setBoolean(String key, Boolean value){
        set(key, value);
    }
    public void setDouble(String key, Double value){
        set(key, value);
    }
    public void setFloat(String key, Float value){
        set(key, value);
    }
    public void setInteger(String key, Integer value){
        set(key, value);
    }
    public void setLong(String key, Long value){
        set(key, value);
    }
}
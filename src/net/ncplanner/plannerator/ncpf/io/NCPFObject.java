package net.ncplanner.plannerator.ncpf.io;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.ncpf.NCPFModuleReference;
import net.ncplanner.plannerator.ncpf.RegisteredNCPFObject;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
public class NCPFObject extends HashMap<String, Object>{
    public <T extends DefinedNCPFObject> T getDefinedNCPFObject(String key, Supplier<T> supplier){
        T object = supplier.get();
        NCPFObject ncpf = getNCPFObject(key);
        object.convertFromObject(ncpf);
        return object;
    }
    public void setDefinedNCPFObject(String key, DefinedNCPFObject object){
        NCPFObject ncpf = new NCPFObject();
        object.convertToObject(ncpf);
        setNCPFObject(key, ncpf);
    }
    
    public <T extends DefinedNCPFObject, V extends List<T>> V getDefinedNCPFList(String key, Supplier<T> objectSupplier){
        return (V)getDefinedNCPFList(key, new ArrayList<>(), objectSupplier);
    }
    public <T extends DefinedNCPFObject, V extends List<T>> V getDefinedNCPFList(String key, V list, Supplier<T> objectSupplier){
        NCPFList ncpf = getNCPFList(key);
        for(int i = 0; i<ncpf.size(); i++){
            NCPFObject obj = ncpf.getNCPFObject(i);
            T object = objectSupplier.get();
            list.add(object);
            object.convertFromObject(obj);
        }
        return list;
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
    
    public <T extends DefinedNCPFObject> void getDefined3DArray(String name, T[][][] array, List<T> indicies){
        NCPFList list3 = getNCPFList(name);
        for(int x = 0; x<array.length; x++){
            NCPFList list2 = list3.getNCPFList(x);
            for(int y = 0; y<array[x].length; y++){
                NCPFList list = list2.getNCPFList(y);
                for(int z = 0; z<array[x][y].length; z++){
                    int idx = list.getInteger(z);
                    if(idx>=0)array[x][y][z] = indicies.get(idx);
                }
            }
        }
    }
    public <T extends NCPFElement> void setDefined3DArray(String name, T[][][] array, List<T> indicies){
        NCPFList<NCPFList<NCPFList<Integer>>> list3 = new NCPFList<>();
        for(int x = 0; x<array.length; x++){
            NCPFList<NCPFList<Integer>> list2 = new NCPFList<>();
            for(int y = 0; y<array[x].length; y++){
                NCPFList<Integer> list = new NCPFList<>();
                for(int z = 0; z<array[x][y].length; z++){
                    list.add(indexof(array[x][y][z], indicies));
                }
                list2.add(list);
            }
            list3.add(list2);
        }
        setNCPFList(name, list3);
    }
    
    public <T extends RegisteredNCPFObject> T getRegisteredNCPFObject(String key, HashMap<String, Supplier<T>> registry){
        String type = getNCPFObject(key).getString("type");
        Supplier<T> supplier = registry.get(type);
        if(supplier==null)throw new IllegalArgumentException("Cannot load unregistered object: "+type+"!");
        return getDefinedNCPFObject(key, supplier);
    }
    public void setRegisteredNCPFObject(String key, RegisteredNCPFObject object){
        setDefinedNCPFObject(key, object);
        getNCPFObject(key).setString("type", object.type);
    }
    
    public <T extends RegisteredNCPFObject, V extends List<T>> V getRegisteredNCPFList(String key, HashMap<String, Supplier<T>> registry){
        return (V)getRegisteredNCPFList(key, new ArrayList<>(), registry);
    }
    public <T extends RegisteredNCPFObject, V extends List<T>> V getRegisteredNCPFList(String key, V list, HashMap<String, Supplier<T>> registry){
        NCPFList ncpf = getNCPFList(key);
        for(int i = 0; i<ncpf.size(); i++){
            NCPFObject obj = ncpf.getNCPFObject(i);
            String type = obj.getString("type");
            Supplier<T> supplier = registry.get(type);
            if(supplier==null)throw new IllegalArgumentException("Cannot load unregistered object: "+type+"!");
            T object = supplier.get();
            list.add(object);
            object.convertFromObject(obj);
        }
        return list;
    }
    public <T extends RegisteredNCPFObject> void setRegisteredNCPFList(String key, List<T> list){
        NCPFList<NCPFObject> ncpf = new NCPFList<>();
        for(RegisteredNCPFObject obj : list){
            NCPFObject object = new NCPFObject();
            obj.convertToObject(object);
            object.setString("type", obj.type);
            ncpf.add(object);
        }
        setNCPFList(key, ncpf);
    }
    
    public <T extends DefinedNCPFModularObject> void getRecipe3DArray(String name, NCPFElement[][][] array, T[][][] design){
        NCPFList list3 = getNCPFList(name);
        int X = -1;
        int lastX = -1;
        for(int x = 0; x<design.length; x++){
            int Y = -1;
            int lastY = -1;
            for(int y = 0; y<design[x].length; y++){
                int Z = -1;
                int lastZ = -1;
                for(int z = 0; z<design[x][y].length; z++){
                    if(design[x][y][z]!=null&&design[x][y][z].hasModule(NCPFBlockRecipesModule::new)){
                        if(x!=lastX){
                            lastX = x;
                            X++;
                        }
                        if(y!=lastY){
                            lastY = y;
                            Y++;
                        }
                        if(z!=lastZ){
                            lastZ = z;
                            Z++;
                        }
                        int idx = list3.getNCPFList(X).getNCPFList(Y).getInteger(Z);
                        if(idx>-1)array[x][y][z] = design[x][y][z].getModule(NCPFBlockRecipesModule::new).recipes.get(idx);
                    }
                }
            }
        }
    }
    public <T extends DefinedNCPFModularObject> void setRecipe3DArray(String name, NCPFElement[][][] array, T[][][] design){
        NCPFList<NCPFList<NCPFList<Integer>>> list3 = new NCPFList();
        for(int x = 0; x<design.length; x++){
            NCPFList<NCPFList<Integer>> list2 = new NCPFList<>();
            for(int y = 0; y<design[x].length; y++){
                NCPFList<Integer> list = new NCPFList<>();
                for(int z = 0; z<design[x][y].length; z++){
                    if(design[x][y][z]!=null&&design[x][y][z].hasModule(NCPFBlockRecipesModule::new)){
                        int idx = indexof(array[x][y][z], design[x][y][z].getModule(NCPFBlockRecipesModule::new).recipes);
                        if(idx==-1&&array[x][y][z]!=null)throw new IllegalArgumentException("Unable to save recipe array: Element "+array[x][y][z].toString()+" is not a valid recipe of "+design[x][y][z].toString()+"!");
                        list.add(idx);
                    }
                }
                if(!list.isEmpty())list2.add(list);
            }
            if(!list2.isEmpty())list3.add(list2);
        }
        setNCPFList(name, list3);
    }
    
    public void getVariable(String key, SettingVariable setting){
        setting.set(setting.convertFromObject(getNCPFObject(key)));
    }
    public void setVariable(String key, SettingVariable setting){
        setNCPFObject(key, setting.convertToObject());
    }
    
    public int[] getIntArray(String key){
        NCPFList list = getNCPFList(key);
        int[] arr = new int[list.size()];
        for(int i = 0; i<arr.length; i++)arr[i] = list.getInteger(i);
        return arr;
    }
    
    public void setIntArray(String key, int[] value){
        NCPFList<Integer> list = new NCPFList<>();
        for(int i : value)list.add(i);
        setNCPFList(key, list);
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
    private <T extends NCPFElement> int indexof(T element, List<T> list){
        if(element==null)return -1;
        for(int i = 0; i<list.size(); i++){
            if(list.get(i).definition.matches(element.definition))return i;
        }
        return -1;
    }
    public <T extends NCPFElement> T getIndex(String name, List<T> indicies){
        int index = getInteger(name);
        if(index==-1)return null;
        return indicies.get(index);
    }
    public <T extends NCPFElement> void setIndex(String name, T element, List<T> indicies){
        setInteger(name, indexof(element, indicies));
    }
    public NCPFElementReference getDefinedModuleOrElementReference(String block){
        NCPFElementReference reference = getDefinedNCPFObject(block, NCPFElementReference::new);
        if(reference.definition.typeMatches(NCPFModuleElement::new))return reference.copyTo(NCPFModuleReference::new);
        return reference;
    }
}
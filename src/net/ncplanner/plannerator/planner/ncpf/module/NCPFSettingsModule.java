package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public abstract class NCPFSettingsModule extends NCPFModule{
    public final ArrayList<String> settings = new ArrayList<>();
    public final HashMap<String, Type> types = new HashMap<>();
    public final HashMap<String, Supplier> gets = new HashMap<>();
    public final HashMap<String, Consumer> sets = new HashMap<>();
    public final HashMap<String, String> titles = new HashMap<>();
    public final HashMap<String, String> tooltips = new HashMap<>();
    public NCPFSettingsModule(String name){
        super(name);
    }
    public void addInteger(String name, Supplier<Integer> get, Consumer<Integer> set, String title){
        addInteger(name, get, set, title, null);
    }
    public void addInteger(String name, Supplier<Integer> get, Consumer<Integer> set, String title, String tooltip){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.INTEGER);
        titles.put(name, title);
        tooltips.put(name, tooltip);
    }
    public void addFloat(String name, Supplier<Float> get, Consumer<Float> set, String title){
        addFloat(name, get, set, title, null);
    }
    public void addFloat(String name, Supplier<Float> get, Consumer<Float> set, String title, String tooltip){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.FLOAT);
        titles.put(name, title);
        tooltips.put(name, tooltip);
    }
    public void addDouble(String name, Supplier<Double> get, Consumer<Double> set, String title){
        addDouble(name, get, set, title, null);
    }
    public void addDouble(String name, Supplier<Double> get, Consumer<Double> set, String title, String tooltip){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.DOUBLE);
        titles.put(name, title);
        tooltips.put(name, tooltip);
    }
    public void addBoolean(String name, Supplier<Boolean> get, Consumer<Boolean> set, String title){
        addBoolean(name, get, set, title, null);
    }
    public void addBoolean(String name, Supplier<Boolean> get, Consumer<Boolean> set, String title, String tooltip){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.BOOLEAN);
        titles.put(name, title);
        tooltips.put(name, tooltip);
    }
    public void addStringList(String name, Supplier<List<String>> get, Consumer<List<String>> set, String title){
        addStringList(name, get, set, title, null);
    }
    public void addStringList(String name, Supplier<List<String>> get, Consumer<List<String>> set, String title, String tooltip){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.STRING_LIST);
        titles.put(name, title);
        tooltips.put(name, tooltip);
    }
    public void addReference(String name, Supplier<? extends NCPFElementReference> get, Consumer<NCPFElement> set, String title){
        addReference(name, get, set, title, null);
    }
    public void addReference(String name, Supplier<? extends NCPFElementReference> get, Consumer<NCPFElement> set, String title, String tooltip){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.REFERENCE);
        titles.put(name, title);
        tooltips.put(name, tooltip);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        for(String setting : settings){
            Consumer set = sets.get(setting);
            switch(types.get(setting)){
                case FLOAT:
                    ((Consumer<Float>)set).accept(ncpf.getFloat(setting));
                    break;
                case DOUBLE:
                    ((Consumer<Double>)set).accept(ncpf.getDouble(setting));
                    break;
                case INTEGER:
                    ((Consumer<Integer>)set).accept(ncpf.getInteger(setting));
                    break;
                case BOOLEAN:
                    ((Consumer<Boolean>)set).accept(ncpf.getBoolean(setting));
                    break;
                case STRING_LIST:
                    List<String> lst = new ArrayList<>();
                    NCPFList list = ncpf.getNCPFList(setting);
                    for(int i = 0; i<list.size(); i++)lst.add(list.getString(i));
                    ((Consumer<List<String>>)set).accept(lst);
                    break;
            }
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        for(String setting : settings){
            Supplier get = gets.get(setting);
            switch(types.get(setting)){
                case FLOAT:
                    ncpf.setFloat(setting, ((Supplier<Float>)get).get());
                    break;
                case DOUBLE:
                    ncpf.setDouble(setting, ((Supplier<Double>)get).get());
                    break;
                case INTEGER:
                    ncpf.setInteger(setting, ((Supplier<Integer>)get).get());
                    break;
                case BOOLEAN:
                    ncpf.setBoolean(setting, ((Supplier<Boolean>)get).get());
                    break;
                case STRING_LIST:
                    NCPFList<String> list = new NCPFList<>();
                    list.addAll(((Supplier<List<String>>)get).get());
                    ncpf.setNCPFList(setting, list);
                    break;
                case REFERENCE:
                    NCPFElementReference ref = ((Supplier<NCPFElementReference>)get).get();
                    if(ref==null)throw new IllegalArgumentException("Cannot set null setting in class: "+getClass().getName());
                    ncpf.setDefinedNCPFObject(setting, ref);
                    break;
            }
        }
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        for(String setting : settings){
            Supplier get = gets.get(setting);
            switch(types.get(setting)){
                case REFERENCE:
                    ((Supplier<NCPFElementReference>)get).get().setReferences(lst);
                    break;
            }
        }
    }
    @Override
    public void conglomerate(NCPFModule addon){
        NCPFSettingsModule other = (NCPFSettingsModule)addon;
        for(String setting : settings){
            sets.get(setting).accept(other.gets.get(setting).get());
        }
    }
    public String getTooltip(){
        String ttp = "";
        for(String setting : settings){
            switch(types.get(setting)){
                case BOOLEAN:
                    if(((Supplier<Boolean>)gets.get(setting)).get())ttp+=titles.get(setting)+"\n";
                    break;
                case FLOAT:
                    ttp+=titles.get(setting)+": "+((Supplier<Float>)gets.get(setting)).get()+"\n";
                    break;
                case DOUBLE:
                    ttp+=titles.get(setting)+": "+((Supplier<Double>)gets.get(setting)).get()+"\n";
                    break;
                case INTEGER:
                    ttp+=titles.get(setting)+": "+((Supplier<Integer>)gets.get(setting)).get()+"\n";
                    break;
            }
        }
        return ttp.trim();
    }
    public static enum Type{
        INTEGER,FLOAT,DOUBLE,BOOLEAN,STRING_LIST,REFERENCE;
    }
}
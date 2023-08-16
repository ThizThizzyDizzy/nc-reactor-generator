package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public abstract class NCPFSettingsModule extends NCPFModule{
    public final ArrayList<String> settings = new ArrayList<>();
    public final HashMap<String, Type> types = new HashMap<>();
    public final HashMap<String, Supplier<? extends Number>> gets = new HashMap<>();
    public final HashMap<String, Consumer<? extends Number>> sets = new HashMap<>();
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
    @Override
    public void convertFromObject(NCPFObject ncpf){
        for(String setting : settings){
            Consumer<? extends Number> set = sets.get(setting);
            switch(types.get(setting)){
                case FLOAT:
                    Consumer<Float> setf = (Consumer<Float>)sets.get(setting);
                    setf.accept(ncpf.getFloat(setting));
                    break;
                case INTEGER:
                    Consumer<Integer> seti = (Consumer<Integer>)sets.get(setting);
                    seti.accept(ncpf.getInteger(setting));
                    break;
            }
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        for(String setting : settings){
            Supplier<? extends Number> get = gets.get(setting);
            switch(types.get(setting)){
                case FLOAT:
                    ncpf.setFloat(setting, get.get().floatValue());
                    break;
                case INTEGER:
                    ncpf.setInteger(setting, get.get().intValue());
                    break;
            }
        }
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Settings may not be overwritten!");
    }
    public static enum Type{
        INTEGER,FLOAT;
    }
}
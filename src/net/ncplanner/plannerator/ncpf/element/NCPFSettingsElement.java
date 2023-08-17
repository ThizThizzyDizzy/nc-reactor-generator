package net.ncplanner.plannerator.ncpf.element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class NCPFSettingsElement extends NCPFElementDefinition{
    public final ArrayList<String> settings = new ArrayList<>();
    public final HashMap<String, Type> types = new HashMap<>();
    public final HashMap<String, Supplier> gets = new HashMap<>();
    public final HashMap<String, Consumer> sets = new HashMap<>();
    public final HashMap<String, String> titles = new HashMap<>();
    public NCPFSettingsElement(String type){
        super(type);
    }
    public void addBlockstate(Supplier<HashMap<String, Object>> get, Consumer<HashMap<String, Object>> set){
        addBlockstate("blockstate", get, set, "Blockstate");
    }
    public void addBlockstate(String name, Supplier<HashMap<String, Object>> get, Consumer<HashMap<String, Object>> set, String title){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.BLOCKSTATE);
        titles.put(name, title);
    }
    public void addMetadata(Supplier<Integer> get, Consumer<Integer> set){
        addMetadata("metadata", get, set, "Metadata");
    }
    public void addMetadata(String name, Supplier<Integer> get, Consumer<Integer> set, String title){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.METADATA);
        titles.put(name, title);
    }
    public void addString(String name, Supplier<String> get, Consumer<String> set, String title, Type type){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, type);
        titles.put(name, title);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        for(String setting : settings){
            Consumer set = sets.get(setting);
            switch(types.get(setting)){
                case METADATA:
                    ((Consumer<Integer>)set).accept(ncpf.getInteger(setting));
                    break;
                case BLOCKSTATE:
                    NCPFObject state = ncpf.getNCPFObject(setting);
                    if(state!=null){
                        HashMap<String, Object> map = new HashMap<>();
                        map.putAll(state);
                        ((Consumer<HashMap<String, Object>>)set).accept(map);
                    }
                    break;
                case NAME:
                case NAMESPACED_NAME:
                case NBT:
                case OREDICT:
                case TAG:
                    ((Consumer<String>)set).accept(ncpf.getString(setting));
                    break;
            }
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        for(String setting : settings){
            Supplier get = gets.get(setting);
            switch(types.get(setting)){
                case METADATA:
                    ncpf.setInteger(setting, ((Supplier<Integer>)get).get());
                    break;
                case BLOCKSTATE:
                    HashMap<String, Object> blockstate = ((Supplier<HashMap<String, Object>>)get).get();
                    if(!blockstate.isEmpty()){
                        NCPFObject state = new NCPFObject();
                        state.putAll(blockstate);
                        ncpf.setNCPFObject(setting, state);
                    }
                    break;
                case NAME:
                case NAMESPACED_NAME:
                case NBT:
                case OREDICT:
                case TAG:
                    ncpf.setString(setting, ((Supplier<String>)get).get());
                    break;
            }
        }
    }
    @Override
    public boolean matches(NCPFElementDefinition definition){
        if(definition instanceof NCPFSettingsElement){
            NCPFSettingsElement other = (NCPFSettingsElement)definition;
            if(!other.type.equals(type))return false;
            if(settings.size()!=other.settings.size())return false;
            for(String key : settings){
                if(!Objects.equals(gets.get(key).get(), other.gets.get(key).get()))return false;
            }
            return true;
        }
        return false;
    }
    public static enum Type{
        NAMESPACED_NAME,NAME,NBT,BLOCKSTATE,METADATA,TAG,OREDICT;
    }
}
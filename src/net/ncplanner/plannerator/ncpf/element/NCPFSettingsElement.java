package net.ncplanner.plannerator.ncpf.element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementStack;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
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
    public void addElementsList(String name, Supplier<ArrayList<NCPFElementDefinition>> get, Consumer<ArrayList<NCPFElementDefinition>> set, String title){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.ELEMENT_LIST);
        titles.put(name, title);
    }
    public void addElementStacksList(String name, Supplier<ArrayList<NCPFElementStack>> get, Consumer<ArrayList<NCPFElementStack>> set, String title){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.ELEMENT_STACK_LIST);
        titles.put(name, title);
    }
    public void addElementStacks(String name, Supplier<HashSet<NCPFElementStack>> get, Consumer<HashSet<NCPFElementStack>> set, String title){
        settings.add(name);
        gets.put(name, get);
        sets.put(name, set);
        types.put(name, Type.ELEMENT_STACK_SET);
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
                case ELEMENT_LIST:
                    NCPFList list = ncpf.getNCPFList(setting);
                    ArrayList<NCPFElementDefinition> definitions = new ArrayList<>();
                    for(int i = 0; i<list.size(); i++){
                        NCPFObject obj = list.getNCPFObject(i);
                        NCPFElementDefinition definition = NCPFElement.recognizedElements.getOrDefault(obj.getString("type"), UnknownNCPFElement::new).get();
                        definition.convertFromObject(obj);
                        definitions.add(definition);
                    }
                    ((Consumer<ArrayList<NCPFElementDefinition>>)set).accept(definitions);
                    break;
                case ELEMENT_STACK_SET:
                    ((Consumer<HashSet<NCPFElementStack>>)set).accept(ncpf.getDefinedNCPFSet(setting, NCPFElementStack::new));
                    break;
                case ELEMENT_STACK_LIST:
                    ((Consumer<ArrayList<NCPFElementStack>>)set).accept(ncpf.getDefinedNCPFList(setting, NCPFElementStack::new));
                    break;
                default:
                    throw new AssertionError("You forgot to add save/load to that ("+types.get(setting).name()+")");
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
                case ELEMENT_LIST:
                    NCPFList<NCPFObject> list = new NCPFList();
                    for(NCPFElementDefinition def : ((Supplier<ArrayList<NCPFElementDefinition>>)get).get()){
                        NCPFObject obj = new NCPFObject();
                        obj.set("type", def.type);
                        def.convertToObject(obj);
                        list.add(obj);
                    }
                    ncpf.set(setting, list);
                    break;
                case ELEMENT_STACK_SET:
                    ncpf.setDefinedNCPFSet(setting, ((Supplier<HashSet<NCPFElementStack>>)get).get());
                    break;
                case ELEMENT_STACK_LIST:
                    ncpf.setDefinedNCPFList(setting, ((Supplier<ArrayList<NCPFElementStack>>)get).get());
                    break;
                default:
                    throw new AssertionError("You forgot to add save/load to that ("+types.get(setting).name()+")");
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
                Object val1 = gets.get(key).get();
                Object val2 = other.gets.get(key).get();
                boolean equal = Objects.equals(val1, val2);
                if(val1 instanceof List&&val2 instanceof List){
                    List l1 = (List)val1;
                    List l2 = (List)val2;
                    equal = l1.size()==l2.size();
                    if(equal){
                        for(int i = 0; i<l1.size(); i++){
                            Object elem1 = l1.get(i);
                            Object elem2 = l2.get(i);
                            if(elem1 instanceof NCPFElementDefinition&&elem2 instanceof NCPFElementDefinition){
                                equal &= ((NCPFElementDefinition)elem1).matches((NCPFElementDefinition)elem2);
                            }else
                                equal = false;
                        }
                    }
                }
                if(val1 instanceof Set&&val2 instanceof Set){
                    Set s1 = (Set)val1;
                    Set s2 = (Set)val2;
                    equal = s1.size()==s2.size();
                    if(equal){
                        for(Object elem1 : s1){
                            int count1 = 0;
                            for(Object elem1Again : s1){
                                if(elem1 instanceof NCPFElementDefinition&&elem1Again instanceof NCPFElementDefinition){
                                    if(((NCPFElementDefinition)elem1).matches((NCPFElementDefinition)elem1Again))count1++;
                                }else
                                    equal = false;
                            }

                            int count2 = 0;
                            for(Object elem2 : s2){
                                if(elem1 instanceof NCPFElementDefinition&&elem2 instanceof NCPFElementDefinition){
                                    if(((NCPFElementDefinition)elem1).matches((NCPFElementDefinition)elem2))count2++;
                                }else
                                    equal = false;
                            }
                            equal &= count1==count2;
                        }
                    }
                }
                if(!equal)return false;
            }
            return true;
        }
        return false;
    }
    public String stringifyBlockstate(HashMap<String, Object> state){
        if(state.isEmpty())return "";
        String s = "";
        ArrayList<String> keys = new ArrayList<>(state.keySet());
        Collections.sort(keys);
        for(String key : keys)s += ","+key+"="+state.get(key);
        return "["+s.substring(1)+"]";
    }
    public static enum Type{
        NAMESPACED_NAME, NAME, NBT, BLOCKSTATE(true), METADATA(true), TAG, OREDICT, ELEMENT_LIST(true), ELEMENT_STACK_SET(true), ELEMENT_STACK_LIST(true);
        public final boolean special;
        private Type(){
            this(false);
        }
        private Type(boolean special){
            this.special = special;
        }
    }
}

package net.ncplanner.plannerator.ncpf.element;
import java.util.HashMap;
import java.util.Objects;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFBlockTagElement extends NCPFElementDefinition{
    public String name;
    public HashMap<String, Object> blockstate = new HashMap<>();
    public String nbt;
    public NCPFBlockTagElement(){
        super("block_tag");
    }
    public NCPFBlockTagElement(String name){
        this();
        this.name = name;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        name = ncpf.getString("name");
        NCPFObject state = ncpf.getNCPFObject("blockstate");
        if(state!=null)blockstate.putAll(state);
        nbt = ncpf.getString("nbt");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("name", name);
        if(!blockstate.isEmpty()){
            NCPFObject state = new NCPFObject();
            state.putAll(blockstate);
            ncpf.setNCPFObject("blockstate", state);
        }
        ncpf.setString("nbt", nbt);
    }
    @Override
    public boolean matches(NCPFElementDefinition definition){
        NCPFBlockTagElement other = (NCPFBlockTagElement) definition;
        return name.equals(other.name)&&Objects.equals(blockstate, other.blockstate)&&Objects.equals(nbt, other.nbt);
    }
    @Override
    public String getName(){
        return name;
    }
}
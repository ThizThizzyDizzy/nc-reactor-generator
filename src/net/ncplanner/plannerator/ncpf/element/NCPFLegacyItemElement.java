package net.ncplanner.plannerator.ncpf.element;
import java.util.Objects;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFLegacyItemElement extends NCPFElementDefinition{
    public String name;
    public Integer metadata;
    public String nbt;
    public NCPFLegacyItemElement(){
        super("legacy_item");
    }
    public NCPFLegacyItemElement(String name){
        this();
        if(name.matches(".*:\\d+")){
            String[] nameParts = name.split(":");
            metadata = Integer.valueOf(nameParts[nameParts.length-1]);
            name = name.substring(0, name.length()-(metadata+"").length()-1);
        }
        this.name = name;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        name = ncpf.getString("name");
        metadata = ncpf.getInteger("metadata");
        nbt = ncpf.getString("nbt");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("name", name);
        ncpf.setInteger("metadata", metadata);
        ncpf.setString("nbt", nbt);
    }
    @Override
    public boolean matches(NCPFElementDefinition definition){
        NCPFLegacyItemElement other = (NCPFLegacyItemElement) definition;
        return name.equals(other.name)&&Objects.equals(metadata, other.metadata)&&Objects.equals(nbt, other.nbt);
    }
    @Override
    public String getName(){
        return name;
    }
}
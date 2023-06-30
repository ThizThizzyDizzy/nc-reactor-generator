package net.ncplanner.plannerator.ncpf.element;
import java.util.Objects;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFItemTagElement extends NCPFElementDefinition{
    public String name;
    public String nbt;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        name = ncpf.getString("name");
        nbt = ncpf.getString("nbt");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("name", name);
        ncpf.setString("nbt", nbt);
    }
    @Override
    public boolean matches(NCPFElementDefinition definition){
        NCPFItemTagElement other = (NCPFItemTagElement) definition;
        return name.equals(other.name)&&Objects.equals(nbt, other.nbt);
    }
}
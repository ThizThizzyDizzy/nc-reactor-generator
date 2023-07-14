package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFModuleElement extends NCPFElementDefinition{
    public String name;
    public NCPFModuleElement(){
        super("module");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        name = ncpf.getString("name");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("name", name);
    }
    @Override
    public boolean matches(NCPFElementDefinition definition){
        NCPFModuleElement other = (NCPFModuleElement) definition;
        return name.equals(other.name);
    }
}
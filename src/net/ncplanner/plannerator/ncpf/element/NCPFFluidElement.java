package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFFluidElement extends NCPFElementDefinition{
    public String name;
    public NCPFFluidElement(){
        super("fluid");
    }
    public NCPFFluidElement(String name){
        this();
        if(!name.contains(":"))throw new IllegalArgumentException("NCPFFluidElement must be namespaced!");
        this.name = name;
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
        if(definition instanceof NCPFFluidElement){
            NCPFFluidElement other = (NCPFFluidElement) definition;
            return name.equals(other.name);
        }
        return false;
    }
    @Override
    public String getName(){
        return name;
    }
}
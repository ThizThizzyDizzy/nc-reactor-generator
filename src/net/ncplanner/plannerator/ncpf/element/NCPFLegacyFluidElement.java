package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFLegacyFluidElement extends NCPFElementDefinition{
    public String name;
    public NCPFLegacyFluidElement(){
        super("legacy_fluid");
    }
    public NCPFLegacyFluidElement(String name){
        this();
        if(name.contains(":"))throw new IllegalArgumentException("NCPFLegacyFluidElement must not be namespaced!");
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
        if(definition instanceof NCPFLegacyFluidElement){
            return name.equals(((NCPFLegacyFluidElement)definition).name);
        }
        return false;
    }
    @Override
    public String getName(){
        return name;
    }
}
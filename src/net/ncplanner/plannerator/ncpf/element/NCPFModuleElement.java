package net.ncplanner.plannerator.ncpf.element;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class NCPFModuleElement extends NCPFElementDefinition{
    public String name = "";
    public NCPFModuleElement(){
        super("module");
    }
    public NCPFModuleElement(Supplier<NCPFModule> module){
        this(module.get());
    }
    public NCPFModuleElement(NCPFModule module){
        this(module.name);
    }
    private NCPFModuleElement(String name){
        this();
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
        if(definition instanceof NCPFModuleElement){
            NCPFModuleElement other = (NCPFModuleElement) definition;
            return name.equals(other.name);
        }
        return false;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String getTypeName(){
        return "Module";
    }
}
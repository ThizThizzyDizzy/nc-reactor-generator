package net.ncplanner.plannerator.planner.ncpf.configuration;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public abstract class NamedTexturedNCPFElement extends NCPFElement{
    public DisplayNameModule names = new DisplayNameModule();
    public TextureModule texture = new TextureModule();
    public NamedTexturedNCPFElement(){
    }
    public NamedTexturedNCPFElement(NCPFElementDefinition definition){
        super(definition);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNameModule::new);
        texture = getModule(TextureModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture);
        super.convertToObject(ncpf);
    }
    @Override
    public void removeModule(NCPFModule module){
        if(module==names)names = null;
        if(module==texture)texture = null;
        super.removeModule(module);
    }
}

package net.ncplanner.plannerator.ncpf.element;
public class NCPFLegacyFluidElement extends NCPFSettingsElement{
    public String name = "";
    public NCPFLegacyFluidElement(){
        super("legacy_fluid");
        addString("name", ()->name, (v)->name = v, "Name", Type.NAME);
    }
    public NCPFLegacyFluidElement(String name){
        this();
        if(name.contains(":"))throw new IllegalArgumentException("NCPFLegacyFluidElement must not be namespaced!");
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String getTypeName(){
        return "Legacy Fluid";
    }
}
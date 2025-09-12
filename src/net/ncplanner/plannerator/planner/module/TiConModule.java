package net.ncplanner.plannerator.planner.module;
public class TiConModule extends Module{
    public TiConModule(){
        super("tinkers_construct", "TICON");
    }
    @Override
    public String getDisplayName(){
        return "Tinker's Construct";
    }
    @Override
    public String getDescription(){
        return "Tinker's tools, (not multiblocks)";
    }
}

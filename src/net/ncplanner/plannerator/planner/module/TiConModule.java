package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.tinkers.TinkerTool;
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
    @Override
    public void addMultiblockTypes(ArrayList multiblockTypes){
        multiblockTypes.add(new TinkerTool(null, 0));
    }
}
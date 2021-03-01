package planner.module;
import java.util.ArrayList;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
public class FusionTestModule extends Module{
    @Override
    public String getName(){
        return "Fusion Test";
    }
    @Override
    public String getDescription(){
        return "A testbed for future overhaul fusion reactors.";
    }
    @Override
    public void addMultiblockTypes(ArrayList multiblockTypes){
        multiblockTypes.add(new OverhaulFusionReactor());
    }
}
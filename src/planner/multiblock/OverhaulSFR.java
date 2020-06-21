package planner.multiblock;
public class OverhaulSFR implements Multiblock{
    @Override
    public String getDefinitionName(){
        return "Overhaul SFR";
    }
    @Override
    public OverhaulSFR newInstance(){
        return new OverhaulSFR();
    }
}
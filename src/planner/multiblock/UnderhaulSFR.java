package planner.multiblock;
public class UnderhaulSFR implements Multiblock{
    @Override
    public String getDefinitionName(){
        return "Underhaul SFR";
    }
    @Override
    public UnderhaulSFR newInstance(){
        return new UnderhaulSFR();
    }
}
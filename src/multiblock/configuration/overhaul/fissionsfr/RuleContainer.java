package multiblock.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import multiblock.MultiblockBit;
public abstract class RuleContainer extends MultiblockBit{
    public ArrayList<PlacementRule> rules = new ArrayList<>();
    @Override
    public boolean equals(Object obj){
        if(obj==null)return false;
        if(obj instanceof RuleContainer){
            return rules.equals(((RuleContainer)obj).rules)&&stillEquals((RuleContainer)obj);
        }
        return false;
    }
    public abstract boolean stillEquals(RuleContainer rc);
}
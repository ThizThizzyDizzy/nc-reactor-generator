package multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
public abstract class RuleContainer{
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
package net.ncplanner.plannerator.multiblock.configuration;
import java.util.ArrayList;
public abstract class RuleContainer<BlockType extends IBlockType, Template extends IBlockTemplate>{
    public ArrayList<AbstractPlacementRule<BlockType, Template>> rules = new ArrayList<>();
    @Override
    public boolean equals(Object obj){
        if(obj instanceof RuleContainer){
            return rules.equals(((RuleContainer)obj).rules) && stillEquals((RuleContainer)obj);
        }
        return false;
    }
    public abstract boolean stillEquals(RuleContainer<BlockType, Template> rc);
}
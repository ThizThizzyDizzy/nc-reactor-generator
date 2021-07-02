package multiblock.configuration;
import java.util.ArrayList;
import multiblock.MultiblockBit;
public abstract class RuleContainer<BlockType extends IBlockType, Template extends IBlockTemplate>
        extends MultiblockBit {
    public ArrayList<AbstractPlacementRule<BlockType, Template>> rules = new ArrayList<>();

    @Override
    public boolean equals(Object obj){
        if(obj==null)return false;
        if(obj instanceof RuleContainer){
            return rules.equals(((RuleContainer)obj).rules) && stillEquals((RuleContainer)obj);
        }
        return false;
    }

    public abstract boolean stillEquals(RuleContainer<BlockType, Template> rc);
}
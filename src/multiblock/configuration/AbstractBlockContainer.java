package multiblock.configuration;

import java.util.ArrayList;

public abstract class AbstractBlockContainer<Template extends IBlockTemplate> {
    public ArrayList<Template> allBlocks = new ArrayList<>();//because I feel like being complicated, this is filled with parent duplicates for addons with recipes
    /**
     * @deprecated You should probably be using allBlocks
     */
    @Deprecated
    public ArrayList<Template> blocks = new ArrayList<>();

    protected ArrayList<Template> getAllUsedBlocks(RuleContainer<?, Template> container){
        ArrayList<Template> used = new ArrayList<>();
        for(AbstractPlacementRule<?, Template> rule : container.rules){
            used.addAll(getAllUsedBlocks(rule));
            if(rule.block!=null)used.add(rule.block);
        }
        return used;
    }
    protected <BlockType extends IBlockType> ArrayList<AbstractPlacementRule<BlockType, Template>> getAllSubRules(RuleContainer<BlockType, Template> container){
        ArrayList<AbstractPlacementRule<BlockType, Template>> rules = new ArrayList<>();
        for(AbstractPlacementRule<BlockType, Template> rule : container.rules){
            rules.addAll(getAllSubRules(rule));
            rules.add(rule);
        }
        return rules;
    }

}

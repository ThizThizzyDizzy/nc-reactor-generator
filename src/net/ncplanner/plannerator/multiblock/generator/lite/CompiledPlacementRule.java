package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.IBlockTemplate;
import net.ncplanner.plannerator.multiblock.configuration.IBlockType;
public class CompiledPlacementRule<BlockType extends IBlockType, Template extends IBlockTemplate>{
    public final AbstractPlacementRule.RuleType ruleType;
    public final boolean isSpecificBlock;
    public final BlockType blockType;
    public final int block;
    public final byte min;
    public final byte max;
    public CompiledPlacementRule<BlockType, Template>[] rules;
    public CompiledPlacementRule(AbstractPlacementRule.RuleType ruleType, boolean isSpecificBlock, BlockType blockType, int block, byte min, byte max){
        this.ruleType = ruleType;
        this.isSpecificBlock = isSpecificBlock;
        this.blockType = blockType;
        this.block = block;
        this.min = min;
        this.max = max;
    }
    public static <BlockType extends IBlockType, Template extends IBlockTemplate> CompiledPlacementRule<BlockType, Template> compile(AbstractPlacementRule<BlockType, Template> rule, ArrayList<Template> blocks){
        CompiledPlacementRule<BlockType, Template> compiled = new CompiledPlacementRule<>(rule.ruleType, rule.isSpecificBlock, rule.blockType, blocks.indexOf(rule.block), rule.min, rule.max);
        compiled.rules = new CompiledPlacementRule[rule.rules.size()];
        for(int i = 0; i<rule.rules.size(); i++){
            AbstractPlacementRule<BlockType, Template> rul = rule.rules.get(i);
            compiled.rules[i] = compile(rul, blocks);
        }
        return compiled;
    }
    public boolean isAirMatch() {
        return !isSpecificBlock && blockType != null && blockType.isAir();
    }
}
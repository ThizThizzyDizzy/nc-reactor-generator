package multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.Axis;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.Vertex;
import multiblock.configuration.AbstractBlockContainer;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.IBlockType;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.menu.component.Searchable;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class PlacementRule extends AbstractPlacementRule<PlacementRule.BlockType, Block> {
    public static PlacementRule atLeast(int min, BlockType block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.BETWEEN_GROUP;
        rule.blockType = block;
        rule.min = (byte)Math.min(6,Math.max(1,min));
        rule.max = 6;
        return rule;
    }
    public static PlacementRule atLeast(int min, Block block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.BETWEEN;
        rule.block = block;
        rule.min = (byte)Math.min(6,Math.max(1,min));
        rule.max = 6;
        return rule;
    }
    public static PlacementRule exactly(int num, BlockType block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.BETWEEN_GROUP;
        rule.blockType = block;
        rule.min = rule.max = (byte)Math.min(6,Math.max(1,num));
        return rule;
    }
    public static PlacementRule exactly(int num, Block block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.BETWEEN;
        rule.block = block;
        rule.min = rule.max = (byte)Math.min(6,Math.max(1,num));
        return rule;
    }
    public static PlacementRule axis(BlockType block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.AXIAL_GROUP;
        rule.blockType = block;
        rule.min = 1;
        rule.max = 3;
        return rule;
    }
    public static PlacementRule axis(Block block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.AXIAL;
        rule.block = block;
        rule.min = 1;
        rule.max = 3;
        return rule;
    }
    public static PlacementRule or(PlacementRule... rules){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.OR;
        for(PlacementRule r : rules){
            rule.rules.add(r);
        }
        return rule;
    }
    public static PlacementRule and(PlacementRule... rules){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.AND;
        for(PlacementRule r : rules){
            rule.rules.add(r);
        }
        return rule;
    }
    public static PlacementRule vertex(BlockType block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.VERTEX_GROUP;
        rule.blockType = block;
        return rule;
    }

    @Override
    protected AbstractBlockContainer<Block> getContainerFromParent(Configuration parent) {
        return parent.underhaul.fissionSFR;
    }

    @Override
    public AbstractPlacementRule<BlockType, Block> newRule() {
        return new PlacementRule();
    }

    @Override
    protected byte saveBlockType(BlockType type) {
        return (byte) type.ordinal();
    }

    @Override
    public BlockType loadBlockType(byte type) {
        return BlockType.values()[type];
    }

    public enum BlockType implements IBlockType {
        AIR("Air"),
        CASING("Casing"),
        COOLER("Cooler"),
        FUEL_CELL("Fuel Cell"),
        MODERATOR("Moderator");

        public final String name;
        BlockType(String name){
            this.name = name;
        }
        @Override
        public String toString(){
            return name;
        }
        @Override
        public boolean isAir() {
            return this == AIR;
        }
        @Override
        public String getDisplayName() {
            return name;
        }

        @Override
        public boolean blockMatches(Multiblock<?> generator, multiblock.Block rb) {
            multiblock.underhaul.fissionsfr.Block b = (multiblock.underhaul.fissionsfr.Block) rb;
            switch(this){
                case CASING:
                    return b.isCasing();
                case COOLER:
                    return b.isCooler();
                case MODERATOR:
                    return b.isModerator();
                case FUEL_CELL:
                    return b.isFuelCell();
                default:
                    throw new RuntimeException("Invalid enum type?");
            }
        }
    }
}
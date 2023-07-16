package net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.AbstractBlockContainer;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockType;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.CompiledUnderhaulSFRConfiguration;
public class PlacementRule extends AbstractPlacementRule<PlacementRule.BlockType, Block> {

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
        public boolean blockMatches(Multiblock<?> generator, net.ncplanner.plannerator.multiblock.Block rb) {
            net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block) rb;
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
        public boolean blockMatches(int b, CompiledUnderhaulSFRConfiguration config){
            switch(this){
                case CASING:
                    return b==-2;
                case COOLER:
                    return b>=0&&config.blockCooling[b]!=0;
                case MODERATOR:
                    return b>=0&&config.blockModerator[b];
                case FUEL_CELL:
                    return b>=0&&config.blockFuelCell[b];
                default:
                    throw new RuntimeException("Invalid enum type?");
            }
        }
    }
}
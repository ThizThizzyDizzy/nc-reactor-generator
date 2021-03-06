package multiblock.configuration.overhaul.fusion;

import multiblock.Multiblock;
import multiblock.configuration.AbstractBlockContainer;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.IBlockType;
import multiblock.overhaul.fusion.OverhaulFusionReactor;

public class PlacementRule extends AbstractPlacementRule<PlacementRule.BlockType, Block> {
    @Override
    protected AbstractBlockContainer<Block> getContainerFromParent(Configuration parent) {
        return parent.overhaul.fusion;
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
        TOROIDAL_ELECTROMAGNET("Toroidal Electromagnet"),
        POLOIDAL_ELECTROMAGNET("Poloidal Electromagnet"),
        HEATING_BLANKET("Heating Blanket"),
        BREEDING_BLANKET("Breeding Blanket"),
        REFLECTOR("Reflector"),
        HEAT_SINK("Heat Sink"),
        SHIELDING("Shielding"),
        CONDUCTOR("Conductor"),
        CONNECTOR("Connector");

        private final String name;

        BlockType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
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
        public boolean blockMatches(Multiblock<?> r, multiblock.Block rb) {
            OverhaulFusionReactor reactor = (OverhaulFusionReactor) r;
            multiblock.overhaul.fusion.Block b = (multiblock.overhaul.fusion.Block) rb;
            switch (this) {
                case BREEDING_BLANKET:
                    return b.isBreedingBlanket();
                case CONDUCTOR:
                    return b.isConductor();
                case CONNECTOR:
                    return b.isConnector();
                case HEATING_BLANKET:
                    return b.isHeatingBlanket();
                case HEAT_SINK:
                    return b.isHeatsink();
                case POLOIDAL_ELECTROMAGNET:
                    return (b.isElectromagnet() && reactor.isPoloidal(b));
                case REFLECTOR:
                    return b.isReflector();
                case SHIELDING:
                    return b.isShielding();
                case TOROIDAL_ELECTROMAGNET:
                    return (b.isElectromagnet() && reactor.isToroidal(b));
                default:
                    throw new RuntimeException("Invalid enum type?");
            }
        }
    }
}
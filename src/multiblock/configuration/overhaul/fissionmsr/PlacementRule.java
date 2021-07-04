package multiblock.configuration.overhaul.fissionmsr;
import java.util.Locale;
import multiblock.Multiblock;
import multiblock.configuration.AbstractBlockContainer;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.IBlockType;
public class PlacementRule extends AbstractPlacementRule<PlacementRule.BlockType, Block> {
    public static PlacementRule parseNC(FissionMSRConfiguration configuration, String str) {
        PlacementRule rule = new PlacementRule();
        rule.parseNcInto(configuration, str);
        return rule;
    }

    @Override
    protected AbstractBlockContainer<Block> getContainerFromParent(Configuration parent) {
        return parent.overhaul.fissionMSR;
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
        HEATER("Heater"),
        VESSEL("Fuel Vessel"),
        MODERATOR("Moderator"),
        REFLECTOR("Reflector"),
        SHIELD("Neutron Shield"),
        IRRADIATOR("Irradiator"),
        CONDUCTOR("Conductor");

        public final String name;

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
        public boolean blockMatches(Multiblock<?> generator, multiblock.Block rb) {
            multiblock.overhaul.fissionmsr.Block b = (multiblock.overhaul.fissionmsr.Block) rb;
            switch (this) {
                case CASING:
                    return b.isCasing();
                case CONDUCTOR:
                    return b.isConductor();
                case HEATER:
                    return b.isHeater();
                case IRRADIATOR:
                    return b.isIrradiator();
                case MODERATOR:
                    return b.isModerator();
                case REFLECTOR:
                    return b.isReflector();
                case SHIELD:
                    return b.isShield();
                case VESSEL:
                    return b.isFuelVessel();
                default:
                    throw new RuntimeException("Invalid enum type?");
            }
        }
    }

    @Override
    protected BlockType parseBlockType(AbstractBlockContainer<Block> configuration, String str) {
        if (str.startsWith("cell")) return BlockType.VESSEL;
        else if (str.startsWith("vessel")) return BlockType.VESSEL;
        else if (str.startsWith("moderator")) return BlockType.MODERATOR;
        else if (str.startsWith("reflector")) return BlockType.REFLECTOR;
        else if (str.startsWith("casing")) return BlockType.CASING;
        else if (str.startsWith("air")) return BlockType.AIR;
        else if (str.startsWith("conductor")) return BlockType.CONDUCTOR;
        else if (str.startsWith("sink")) return BlockType.HEATER;
        else if (str.startsWith("heater")) return BlockType.HEATER;
        else if (str.startsWith("shield")) return BlockType.SHIELD;
        else if (str.startsWith("irradiator")) return BlockType.IRRADIATOR;
        else return null;
    }

    @Override
    protected Block parseTemplate(AbstractBlockContainer<Block> configuration, String str) {
        Block block = null;
        int shortest = 0;
        if(str.startsWith("water heater")||str.startsWith("water sink"))str = "standard"+str.substring("water".length());
        String[] strs = str.split(" ");
        if (strs.length != 2 || !(strs[1].startsWith("heater")||strs[1].startsWith("sink"))) {
            throw new IllegalArgumentException("Unknown rule bit: " + str);
        }
        for (Block b : configuration.allBlocks) {
            if (b.parent != null) continue;
            for (String s : b.getLegacyNames()) {
                if(str.endsWith(" heater")||str.endsWith(" heaters")){
                    String withoutTheHeater = str.substring(0, str.indexOf(" heater"));
                    if(s.equals("nuclearcraft:salt_fission_heater_"+withoutTheHeater)){
                        return b;
                    }
                }
                if(str.endsWith(" sink")||str.endsWith(" sinks")){
                    String withoutTheSink = str.substring(0, str.indexOf(" sink"));
                    if(s.equals("nuclearcraft:salt_fission_heater_"+withoutTheSink)){
                        return b;
                    }
                }
                if (s.toLowerCase(Locale.ENGLISH).contains("heater")
                        && s.toLowerCase(Locale.ENGLISH).matches("(\\s|^)?" + strs[0].toLowerCase(Locale.ENGLISH).replace("_", "[_ ]") + "(\\s|$)?.*")) {
                    int len = s.length();
                    if (block == null || len < shortest) {
                        block = b;
                        shortest = len;
                    }
                }
            }
        }
        if (block == null) throw new IllegalArgumentException("Could not find block matching rule bit " + str + "!");
        return block;
    }
}
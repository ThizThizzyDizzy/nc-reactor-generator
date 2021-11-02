package net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.AbstractBlockContainer;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockType;
import net.ncplanner.plannerator.planner.StringUtil;
public class PlacementRule extends AbstractPlacementRule<PlacementRule.BlockType, Block> {
    public static PlacementRule parseNC(TurbineConfiguration configuration, String str) {
        PlacementRule rule = new PlacementRule();
        rule.parseNcInto(configuration, str);
        return rule;
    }

    @Override
    protected AbstractBlockContainer<Block> getContainerFromParent(Configuration parent) {
        return parent.overhaul.turbine;
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

    public enum BlockType implements IBlockType  {
        CASING("Casing"),
        COIL("Coil"),
        BEARING("Bearing"),
        CONNECTOR("Connector");

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
            return this == CASING;
        }
        @Override
        public String getDisplayName() {
            return name;
        }

        @Override
        public boolean blockMatches(Multiblock<?> generator, net.ncplanner.plannerator.multiblock.Block rb) {
            net.ncplanner.plannerator.multiblock.overhaul.turbine.Block b = (net.ncplanner.plannerator.multiblock.overhaul.turbine.Block) rb;
            switch (this) {
                case BEARING:
                    return b == null || b.isBearing();
                case COIL:
                    return b == null || b.isCoil();
                case CONNECTOR:
                    return b == null || b.isConnector();
                default:
                    throw new RuntimeException("Invalid enum type?");
            }
        }
    }

    @Override
    protected PlacementRule.BlockType parseBlockType(AbstractBlockContainer<Block> configuration, String str) {
        if(str.startsWith("coil")) return BlockType.COIL;
        else if(str.startsWith("bearing")) return BlockType.BEARING;
        else if(str.startsWith("connector")) return BlockType.CONNECTOR;
        else if(str.startsWith("casing")) return BlockType.CASING;
        else return null;
    }

    @Override
    protected Block parseTemplate(AbstractBlockContainer<Block> configuration, String str) {
        Block block = null;
        int shortest = 0;
        String[] strs = StringUtil.split(str, " ");
        if(strs.length!=2||!strs[1].startsWith("coil")){
            throw new IllegalArgumentException("Unknown rule bit: "+str);
        }
        for(Block b : configuration.allBlocks){
            for(String s : b.getLegacyNames()){
                if(str.endsWith(" coil")||str.endsWith(" coils")){
                    String withoutTheCoil = str.substring(0, str.indexOf(" coil"));
                    if(s.equals("nuclearcraft:turbine_dynamo_coil_"+withoutTheCoil)){
                        return b;
                    }
                }
                if(StringUtil.toLowerCase(s).contains("coil")&&StringUtil.matches(StringUtil.toLowerCase(s), "(\\s|^)?"+StringUtil.replace(StringUtil.toLowerCase(strs[0]), "_", "[_ ]")+"(\\s|$)?.*")){
                    int len = s.length();
                    if(block==null||len<shortest){
                        block = b;
                        shortest = len;
                    }
                }
            }
        }
        if(block==null)throw new IllegalArgumentException("Could not find block matching rule bit "+str+"!");
        return block;
    }
}
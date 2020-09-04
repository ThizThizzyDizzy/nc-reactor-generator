package multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FissionSFRConfiguration{
    public ArrayList<Block> allBlocks = new ArrayList<>();
    public ArrayList<Fuel> allFuels = new ArrayList<>();
    /**
     * @deprecated You should probably be using allBlocks
     */
    @Deprecated
    public ArrayList<Block> blocks = new ArrayList<>();
    /**
     * @deprecated You should probably be using allFuels
     */
    @Deprecated
    public ArrayList<Fuel> fuels = new ArrayList<>();
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public float moderatorExtraPower;
    public float moderatorExtraHeat;
    public int activeCoolerRate;
    public String[] getAllBlocksStringList(){
        String[] strs = new String[allBlocks.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = allBlocks.get(i).name;
        }
        return strs;
    }
    public Config save(Configuration parent, boolean partial){
        Config config = Config.newConfig();
        if(parent==null){
            config.set("minSize", minSize);
            config.set("maxSize", maxSize);
            config.set("neutronReach", neutronReach);
            config.set("moderatorExtraPower", moderatorExtraPower);
            config.set("moderatorExtraHeat", moderatorExtraHeat);
            config.set("activeCoolerRate", activeCoolerRate);
        }
        ConfigList blocks = new ConfigList();
        for(Block b : this.blocks){
            blocks.add(b.save(parent, this, partial));
        }
        config.set("blocks", blocks);
        ConfigList fuels = new ConfigList();
        for(Fuel f : this.fuels){
            fuels.add(f.save());
        }
        config.set("fuels", fuels);
        return config;
    }
    public void applyPartial(FissionSFRConfiguration partial, ArrayList<Multiblock> multiblocks){
        Set<Block> usedBlocks = new HashSet<>();
        Set<Fuel> usedFuels = new HashSet<>();
        for(Multiblock mb : multiblocks){
            if(mb instanceof UnderhaulSFR){
                for(multiblock.underhaul.fissionsfr.Block b : ((UnderhaulSFR)mb).getBlocks()){
                    usedBlocks.add(b.template);
                }
                usedFuels.add(((UnderhaulSFR)mb).fuel);
            }
        }
        partial.blocks.addAll(usedBlocks);
        partial.allBlocks.addAll(usedBlocks);
        partial.fuels.addAll(usedFuels);
        partial.allFuels.addAll(usedFuels);
        partial.minSize = minSize;
        partial.maxSize = maxSize;
        partial.neutronReach = neutronReach;
        partial.moderatorExtraPower = moderatorExtraPower;
        partial.moderatorExtraHeat = moderatorExtraHeat;
        partial.activeCoolerRate = activeCoolerRate;
    }
    public Block convert(Block template){
        for(Block block : blocks){
            if(block.name.trim().equalsIgnoreCase(template.name.trim()))return block;
        }
        throw new IllegalArgumentException("Failed to find match for block "+template.toString()+"!");
    }
    public Fuel convert(Fuel template){
        for(Fuel fuel : fuels){
            if(fuel.name.trim().equalsIgnoreCase(template.name.trim()))return fuel;
        }
        throw new IllegalArgumentException("Failed to find match for fuel "+template.toString()+"!");
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof FissionSFRConfiguration){
            FissionSFRConfiguration fsfrc = (FissionSFRConfiguration)obj;
            return Objects.equals(fsfrc.allBlocks, allBlocks)
                    &&Objects.equals(fsfrc.allFuels, allFuels)
                    &&Objects.equals(fsfrc.blocks, blocks)
                    &&Objects.equals(fsfrc.fuels, fuels)
                    &&minSize==fsfrc.minSize
                    &&maxSize==fsfrc.maxSize
                    &&neutronReach==fsfrc.neutronReach
                    &&moderatorExtraPower==fsfrc.moderatorExtraPower
                    &&moderatorExtraHeat==fsfrc.moderatorExtraHeat
                    &&activeCoolerRate==fsfrc.activeCoolerRate;
        }
        return false;
    }
}
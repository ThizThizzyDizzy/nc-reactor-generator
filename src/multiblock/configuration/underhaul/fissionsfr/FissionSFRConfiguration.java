package multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FissionSFRConfiguration{
    public ArrayList<Block> blocks = new ArrayList<>();
    public ArrayList<Fuel> fuels = new ArrayList<>();
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public float moderatorExtraPower;
    public float moderatorExtraHeat;
    public int activeCoolerRate;
    public String[] getBlockStringList(){
        String[] strs = new String[blocks.size()];
        for(int i = 0; i<strs.length; i++){
            strs[i] = blocks.get(i).name;
        }
        return strs;
    }
    public Config save(boolean partial){
        Config config = Config.newConfig();
        config.set("minSize", minSize);
        config.set("maxSize", maxSize);
        config.set("neutronReach", neutronReach);
        config.set("moderatorExtraPower", moderatorExtraPower);
        config.set("moderatorExtraHeat", moderatorExtraHeat);
        config.set("activeCoolerRate", activeCoolerRate);
        ConfigList blocks = new ConfigList();
        for(Block b : this.blocks){
            blocks.add(b.save(this, partial));
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
        partial.fuels.addAll(usedFuels);
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
}
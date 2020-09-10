package multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
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
        if(parent==null&&!partial){
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
    public void apply(FissionSFRConfiguration partial, ArrayList<Multiblock> multiblocks, PartialConfiguration parent){
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
        parent.underhaul.fissionSFR.allBlocks.addAll(usedBlocks);
        partial.fuels.addAll(usedFuels);
        parent.underhaul.fissionSFR.allFuels.addAll(usedFuels);
    }
    public void apply(AddonConfiguration addon, Configuration parent){
        Set<Block> usedBlocks = new HashSet<>();
        for(Block b : blocks){
            usedBlocks.addAll(getAllUsedBlocks(b));
            usedBlocks.removeAll(blocks);
        }
        //parent blocks
        ArrayList<Block> theBlocks = new ArrayList<>();
        for(Block b : parent.underhaul.fissionSFR.blocks){
            if(usedBlocks.contains(b)){
                theBlocks.add(b);
            }
        }
        addon.underhaul.fissionSFR.allBlocks.addAll(theBlocks);
        addon.underhaul.fissionSFR.blocks.addAll(theBlocks);
        //self blocks
        addon.self.underhaul.fissionSFR.blocks.addAll(blocks);
        addon.underhaul.fissionSFR.allBlocks.addAll(blocks);
        //addon blocks
        for(Configuration addn : parent.addons){
            theBlocks = new ArrayList<>();
            if(addn.underhaul!=null&&addn.underhaul.fissionSFR!=null){
                for(Block b : addn.underhaul.fissionSFR.blocks){
                    if(usedBlocks.contains(b)){
                        theBlocks.add(b);
                    }
                }
            }
            addon.underhaul.fissionSFR.allBlocks.addAll(theBlocks);
            if(!theBlocks.isEmpty()){
                boolean foundMatch = false;
                for(Configuration c : addon.addons){
                    if(c.underhaulNameMatches(addn)){
                        foundMatch = true;
                        c.underhaul.fissionSFR.blocks.addAll(theBlocks);
                    }
                }
                if(!foundMatch){
                    Configuration c = new PartialConfiguration(addn.name, addn.overhaulVersion, addn.underhaulVersion);
                    addon.addons.add(c);
                    c.addon = true;
                    c.underhaul = new UnderhaulConfiguration();
                    c.underhaul.fissionSFR = new FissionSFRConfiguration();
                    c.underhaul.fissionSFR.blocks.addAll(theBlocks);
                }
            }
        }
        addon.self.underhaul.fissionSFR.fuels.addAll(fuels);
        parent.underhaul.fissionSFR.allFuels.addAll(fuels);
    }
    public Block convert(Block template){
        for(Block block : allBlocks){
            if(block.name.trim().equalsIgnoreCase(template.name.trim()))return block;
        }
        throw new IllegalArgumentException("Failed to find match for block "+template.toString()+"!");
    }
    public Fuel convert(Fuel template){
        for(Fuel fuel : allFuels){
            if(fuel.name.trim().equalsIgnoreCase(template.name.trim()))return fuel;
        }
        throw new IllegalArgumentException("Failed to find match for fuel "+template.toString()+"!");
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof FissionSFRConfiguration){
            FissionSFRConfiguration fsfrc = (FissionSFRConfiguration)obj;
            return Objects.equals(fsfrc.blocks, blocks)
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
    private ArrayList<Block> getAllUsedBlocks(RuleContainer container){
        ArrayList<Block> used = new ArrayList<>();
        for(PlacementRule rule : container.rules){
            used.addAll(getAllUsedBlocks(rule));
            if(rule.block!=null)used.add(rule.block);
        }
        return used;
    }
}
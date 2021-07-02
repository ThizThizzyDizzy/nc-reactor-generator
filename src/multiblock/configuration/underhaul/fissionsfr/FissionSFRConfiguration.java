package multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.configuration.AbstractBlockContainer;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.exception.MissingConfigurationEntryException;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FissionSFRConfiguration extends AbstractBlockContainer<Block> {
    public ArrayList<Fuel> allFuels = new ArrayList<>();
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
            fuels.add(f.save(partial));
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
    public Block convert(Block template) throws MissingConfigurationEntryException{
        for(Block block : allBlocks){
            for(String name : block.getLegacyNames()){
                if(name.equals(template.name))return block;
            }
        }
        for(Block block : blocks){
            for(String name : block.getLegacyNames()){
                if(name.equals(template.name))return block;
            }
        }
        throw new MissingConfigurationEntryException("Failed to find match for block "+template.name+"!");
    }
    public Fuel convert(Fuel template) throws MissingConfigurationEntryException{
        for(Fuel fuel : allFuels){
            for(String name : fuel.getLegacyNames()){
                if(name.equals(template.name))return fuel;
            }
        }
        for(Fuel fuel : fuels){
            for(String name : fuel.getLegacyNames()){
                if(name.equals(template.name))return fuel;
            }
        }
        throw new MissingConfigurationEntryException("Failed to find match for fuel "+template.name+"!");
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
    public void convertAddon(AddonConfiguration parent, Configuration convertTo) throws MissingConfigurationEntryException{
        for(Block block : blocks){
            for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : getAllSubRules(block)){
                if(rule.block==null)continue;
                if(parent.underhaul!=null&&parent.underhaul.fissionSFR!=null&&parent.underhaul.fissionSFR.blocks.contains(rule.block)){
                    rule.block = convertTo.underhaul.fissionSFR.convert(rule.block);
                }else if(blocks.contains(rule.block)){
                    //do nothing :)
                }else{
                    //in sub-addon, find and convert
                    boolean found = false;
                    for(Configuration addon : parent.addons){
                        if(addon.underhaul!=null&&addon.underhaul.fissionSFR!=null){
                            if(addon.underhaul.fissionSFR.blocks.contains(rule.block)){
                                rule.block = convertTo.findMatchingAddon(addon).underhaul.fissionSFR.convert(rule.block);
                                found = true;
                            }
                        }
                    }
                    if(!found)throw new IllegalArgumentException("Could not convert block "+block.name+"!");
                }
            }
        }
    }
    public void makeAddon(FissionSFRConfiguration parent, FissionSFRConfiguration addon){
        B:for(Block b : blocks){
            for(Block pb : parent.blocks){
                if(pb.name.equals(b.name)){
                    continue B;
                }
            }
            addon.blocks.add(b);
            parent.allBlocks.add(b);
        }
        f:for(Fuel f : fuels){
            for(Fuel pf : parent.fuels){
                if(pf.name.equals(f.name)){
                    continue f;
                }
            }
            addon.fuels.add(f);
            parent.allFuels.add(f);
        }
    }
}
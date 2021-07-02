package multiblock.configuration.overhaul.fissionmsr;
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
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import planner.exception.MissingConfigurationEntryException;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class FissionMSRConfiguration extends AbstractBlockContainer<Block> {
    public int minSize;
    public int maxSize;
    public int neutronReach;
    public int coolingEfficiencyLeniency;
    public float sparsityPenaltyMult;
    public float sparsityPenaltyThreshold;
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
            config.set("coolingEfficiencyLeniency", coolingEfficiencyLeniency);
            config.set("sparsityPenaltyMult", sparsityPenaltyMult);
            config.set("sparsityPenaltyThreshold", sparsityPenaltyThreshold);
        }
        ConfigList blocks = new ConfigList();
        for(Block block : this.blocks){
            if(block.parent!=null)continue;
            blocks.add(block.save(parent, this, partial));
        }
        config.set("blocks", blocks);
        return config;
    }
    public void apply(FissionMSRConfiguration partial, ArrayList<Multiblock> multiblocks, PartialConfiguration parent){
        ArrayList<Block> usedBlocks = new ArrayList<>();
        for(Multiblock mb : multiblocks){
            if(mb instanceof OverhaulMSR){
                for(multiblock.overhaul.fissionmsr.Block b : ((OverhaulMSR)mb).getBlocks()){
                    if(b.template.parent!=null)if(!usedBlocks.contains(b.template.parent))usedBlocks.add(b.template.parent);
                    if(!usedBlocks.contains(b.template))usedBlocks.add(b.template);
                    if(b.template.port!=null)if(!usedBlocks.contains(b.template.port))usedBlocks.add(b.template.port);
                }
            }
        }
        ArrayList<Block> convertedBlocks = new ArrayList<>();
        for(Block b : usedBlocks){
            for(Block bl : blocks){
                if(bl.name.equals(b.name))convertedBlocks.add(bl);
            }
        }
        partial.blocks.addAll(convertedBlocks);
        parent.overhaul.fissionMSR.allBlocks.addAll(convertedBlocks);
        ArrayList convertedBlocksForRecipes = new ArrayList<>();
        for(Block b : usedBlocks){
            for(Block bl : allBlocks){
                if(bl.name.equals(b.name)&&!convertedBlocks.contains(bl))convertedBlocksForRecipes.add(bl);
            }
        }
        partial.blocks.addAll(convertedBlocksForRecipes);
        partial.allBlocks.addAll(convertedBlocksForRecipes);
    }
    public void apply(AddonConfiguration addon, Configuration parent){
        Set<Block> usedBlocks = new HashSet<>();
        for(Block b : blocks){
            usedBlocks.addAll(getAllUsedBlocks(b));
            usedBlocks.removeAll(blocks);
        }
        for(Block block : allBlocks){
            for(Block b : parent.overhaul.fissionMSR.allBlocks){
                for(String nam : b.getLegacyNames()){
                    if(block.name.equals(nam))usedBlocks.add(b);
                }
            }
        }
        //parent blocks
        ArrayList<Block> theBlocks = new ArrayList<>();
        for(Block b : parent.overhaul.fissionMSR.blocks){
            if(usedBlocks.contains(b)){
                theBlocks.add(b);
            }
        }
        addon.overhaul.fissionMSR.allBlocks.addAll(theBlocks);
        addon.overhaul.fissionMSR.blocks.addAll(theBlocks);
        //self blocks
        addon.self.overhaul.fissionMSR.blocks.addAll(blocks);
        addon.overhaul.fissionMSR.allBlocks.addAll(blocks);
        //recipe blocks
        addon.self.overhaul.fissionMSR.blocks.addAll(allBlocks);
        //addon blocks
        for(Configuration addn : parent.addons){
            theBlocks = new ArrayList<>();
            if(addn.overhaul!=null&&addn.overhaul.fissionMSR!=null){
                for(Block b : addn.overhaul.fissionMSR.blocks){
                    if(usedBlocks.contains(b)){
                        theBlocks.add(b);
                    }
                }
            }
            addon.overhaul.fissionMSR.allBlocks.addAll(theBlocks);
            if(!theBlocks.isEmpty()){
                boolean foundMatch = false;
                for(Configuration c : addon.addons){
                    if(c.overhaulNameMatches(addn)){
                        foundMatch = true;
                        c.overhaul.fissionMSR.blocks.addAll(theBlocks);
                    }
                }
                if(!foundMatch){
                    Configuration c = new PartialConfiguration(addn.name, addn.overhaulVersion, addn.overhaulVersion);
                    addon.addons.add(c);
                    c.addon = true;
                    c.overhaul = new OverhaulConfiguration();
                    c.overhaul.fissionMSR = new FissionMSRConfiguration();
                    c.overhaul.fissionMSR.blocks.addAll(theBlocks);
                }
            }
        }
    }
    public Block convert(Block template) throws MissingConfigurationEntryException{
        if(template==null)return null;
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
    public Block convertToMSR(multiblock.configuration.overhaul.fissionsfr.Block template) throws MissingConfigurationEntryException{
        if(template==null)return null;
        for(Block block : allBlocks){
            if(block.name.equals(template.name.replace("solid", "salt").replace("cell", "vessel").replace("sink", "heater")))return block;
        }
        for(Block block : blocks){
            if(block.name.equals(template.name.replace("solid", "salt").replace("cell", "vessel").replace("sink", "heater")))return block;
        }
        throw new MissingConfigurationEntryException("Failed to find match for block "+template.name+"!");
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof FissionMSRConfiguration){
            FissionMSRConfiguration fsfrc = (FissionMSRConfiguration)obj;
            return Objects.equals(fsfrc.blocks, blocks)
                    &&minSize==fsfrc.minSize
                    &&maxSize==fsfrc.maxSize
                    &&neutronReach==fsfrc.neutronReach
                    &&coolingEfficiencyLeniency==fsfrc.coolingEfficiencyLeniency
                    &&sparsityPenaltyMult==fsfrc.sparsityPenaltyMult
                    &&sparsityPenaltyThreshold==fsfrc.sparsityPenaltyThreshold;
        }
        return false;
    }
    public void convertAddon(AddonConfiguration parent, Configuration convertTo) throws MissingConfigurationEntryException{
        for(Block block : blocks){
            for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : getAllSubRules(block)){
                if(rule.block==null)continue;
                if(parent.overhaul!=null&&parent.overhaul.fissionMSR!=null&&parent.overhaul.fissionMSR.blocks.contains(rule.block)){
                    rule.block = convertTo.overhaul.fissionMSR.convert(rule.block);
                }else if(blocks.contains(rule.block)){
                    //do nothing :)
                }else{
                    //in sub-addon, find and convert
                    boolean found = false;
                    for(Configuration addon : parent.addons){
                        if(addon.overhaul!=null&&addon.overhaul.fissionMSR!=null){
                            if(addon.overhaul.fissionMSR.blocks.contains(rule.block)){
                                rule.block = convertTo.findMatchingAddon(addon).overhaul.fissionMSR.convert(rule.block);
                                found = true;
                            }
                        }
                    }
                    if(!found)throw new IllegalArgumentException("Could not convert block "+block.name+"!");
                }
            }
        }
    }
    public void makeAddon(FissionMSRConfiguration parent, FissionMSRConfiguration addon){
        B:for(Block b : blocks){
            for(Block pb : parent.blocks){
                if(pb.name.equals(b.name)){
                    ArrayList<BlockRecipe> extras = new ArrayList<>();
                    R:for(BlockRecipe r : b.recipes){
                        for(BlockRecipe pr : pb.recipes){
                            if(r.inputName.equals(pr.inputName))continue R;
                        }
                        extras.add(r);
                    }
                    if(!extras.isEmpty()){
                        addon.allBlocks.add(b);
                        b.recipes.clear();
                        b.allRecipes.clear();
                        b.recipes.addAll(extras);
                        pb.allRecipes.addAll(extras);
                    }
                    continue B;
                }
            }
            addon.blocks.add(b);
            parent.allBlocks.add(b);
        }
    }
}
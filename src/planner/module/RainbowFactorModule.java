package planner.module;
import generator.Priority;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.suggestion.Suggestor;
public class RainbowFactorModule extends Module<Float>{
    @Override
    public String getName(){
        return "Rainbow Factor";
    }
    @Override
    public String getDescription(){
        return "Gives multiblocks a Rainbow Factor based on how many different types of coolers/heatsinks/heaters/etc. they have";
    }
    @Override
    public Float calculateMultiblock(Multiblock m){
        if(m instanceof UnderhaulSFR){
            float totalSinks = 0;
            for(multiblock.configuration.underhaul.fissionsfr.Block b : m.getConfiguration().underhaul.fissionSFR.allBlocks){
                if(b.cooling!=0&&b.active==null)totalSinks++;
            }
            Set<multiblock.configuration.underhaul.fissionsfr.Block> unique = new HashSet<>();
            for(multiblock.underhaul.fissionsfr.Block b : ((UnderhaulSFR)m).getBlocks()){
                if(!b.isActive())continue;
                if(b.isCooler())unique.add(b.template);
            }
            return unique.size()/totalSinks;
        }
        if(m instanceof OverhaulSFR){
            float totalSinks = 0;
            for(multiblock.configuration.overhaul.fissionsfr.Block b : m.getConfiguration().overhaul.fissionSFR.allBlocks){
                if(b.cooling!=0)totalSinks++;
            }
            Set<multiblock.configuration.overhaul.fissionsfr.Block> unique = new HashSet<>();
            for(multiblock.overhaul.fissionsfr.Block b : ((OverhaulSFR)m).getBlocks()){
                if(!b.isActive())continue;
                if(b.isHeatsink())unique.add(b.template);
            }
            return unique.size()/totalSinks;
        }
        if(m instanceof OverhaulMSR){
            float totalSinks = 0;
            for(multiblock.configuration.overhaul.fissionmsr.Block b : m.getConfiguration().overhaul.fissionMSR.allBlocks){
                if(b.cooling!=0)totalSinks++;
            }
            Set<multiblock.configuration.overhaul.fissionmsr.Block> unique = new HashSet<>();
            for(multiblock.overhaul.fissionmsr.Block b : ((OverhaulMSR)m).getBlocks()){
                if(!b.isActive())continue;
                if(b.isHeater())unique.add(b.template);
            }
            return unique.size()/totalSinks;
        }
        if(m instanceof OverhaulFusionReactor){
            float totalSinks = 0;
            for(multiblock.configuration.overhaul.fusion.Block b : m.getConfiguration().overhaul.fusion.allBlocks){
                if(b.cooling!=0)totalSinks++;
            }
            Set<multiblock.configuration.overhaul.fusion.Block> unique = new HashSet<>();
            for(multiblock.overhaul.fusion.Block b : ((OverhaulFusionReactor)m).getBlocks()){
                if(!b.isActive())continue;
                if(b.isHeatsink())unique.add(b.template);
            }
            return unique.size()/totalSinks;
        }
        return null;
    }
    @Override
    public String getTooltip(Multiblock m, Float o){
        return "Rainbow Score: "+percent(o, 2);
    }
    @Override
    public void getGenerationPriorities(Multiblock multiblock, ArrayList<Priority> priorities){
        Module that = this;
        if(multiblock instanceof UnderhaulSFR){
            priorities.add(new Priority<UnderhaulSFR>("Rainbow", false, true){
                @Override
                protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                    return (float)main.moduleData.get(that)-(float)other.moduleData.get(that);
                }
            });
        }
        if(multiblock instanceof OverhaulSFR){
            priorities.add(new Priority<OverhaulSFR>("Rainbow", false, true){
                @Override
                protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                    return (float)main.moduleData.get(that)-(float)other.moduleData.get(that);
                }
            });
        }
        if(multiblock instanceof OverhaulMSR){
            priorities.add(new Priority<OverhaulMSR>("Rainbow", false, true){
                @Override
                protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                    return (float)main.moduleData.get(that)-(float)other.moduleData.get(that);
                }
            });
        }
        if(multiblock instanceof OverhaulFusionReactor){
            priorities.add(new Priority<OverhaulFusionReactor>("Rainbow", false, true){
                @Override
                protected double doCompare(OverhaulFusionReactor main, OverhaulFusionReactor other){
                    return (float)main.moduleData.get(that)-(float)other.moduleData.get(that);
                }
            });
        }
    }
    @Override
    public void getSuggestors(Multiblock multiblock, ArrayList<Suggestor> suggestors){}//TODO rainbowification suggestions? (only if reactor is stable!)
}
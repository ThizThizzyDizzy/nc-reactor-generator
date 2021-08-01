package planner.module;
import generator.Priority;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import multiblock.Multiblock;
import multiblock.action.SetblockAction;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.editor.suggestion.Suggestion;
import planner.editor.suggestion.Suggestor;
public class RainbowFactorModule extends Module<Float>{
    public RainbowFactorModule(){
        super("rainbow_factor");
    }
    @Override
    public String getDisplayName(){
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
                if(b.heatsink)totalSinks++;
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
                if(b.heater)totalSinks++;
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
                if(b.heatsink)totalSinks++;
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
    public void getSuggestors(Multiblock multiblock, ArrayList<Suggestor> suggestors){
        Module that = this;
        if(multiblock instanceof UnderhaulSFR){
            suggestors.add(new Suggestor<UnderhaulSFR>("Rainbowificator", -1, -1){
                ArrayList<Priority> priorities = new ArrayList<>();
                {
                    priorities.add(new Priority<UnderhaulSFR>("Rainbow", false, true){
                        @Override
                        protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                            return (float)main.moduleData.get(that)-(float)other.moduleData.get(that);
                        }
                    });
                }
                @Override
                public String getDescription(){
                    return "Suggests adding or replacing passive coolers to increase the reactor's rainbow factor.";
                }
                @Override
                public void generateSuggestions(UnderhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                    ArrayList<multiblock.underhaul.fissionsfr.Block> blocks = new ArrayList<>();
                    multiblock.getAvailableBlocks(blocks);
                    for(Iterator<multiblock.underhaul.fissionsfr.Block> it = blocks.iterator(); it.hasNext();){
                        multiblock.underhaul.fissionsfr.Block b = it.next();
                        if(!b.isCooler()||b.template.active!=null)it.remove();
                    }
                    int[] count = new int[1];
                    multiblock.forEachPosition((x, y, z) -> {
                        multiblock.underhaul.fissionsfr.Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            count[0]++;
                        }
                    });
                    suggestor.setCount(count[0]*blocks.size());
                    multiblock.forEachPosition((x, y, z) -> {
                        for(multiblock.underhaul.fissionsfr.Block newBlock : blocks){
                            multiblock.underhaul.fissionsfr.Block block = multiblock.getBlock(x, y, z);
                            if(block==null||block.canBeQuickReplaced()){
                                if(newBlock.template.cooling>(block==null?0:block.template.cooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock), priorities));
                                else suggestor.task.max--;
                            }
                        }
                    });
                }
            });
        }
        if(multiblock instanceof OverhaulSFR){
            suggestors.add(new Suggestor<OverhaulSFR>("Rainbowificator", -1, -1){
                ArrayList<Priority> priorities = new ArrayList<>();
                {
                    priorities.add(new Priority<OverhaulSFR>("Rainbow", false, true){
                        @Override
                        protected double doCompare(OverhaulSFR main, OverhaulSFR other){
                            return (float)main.moduleData.get(that)-(float)other.moduleData.get(that);
                        }
                    });
                }
                @Override
                public String getDescription(){
                    return "Suggests adding or replacing heat sinks to increase the reactor's rainbow factor";
                }
                @Override
                public void generateSuggestions(OverhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                    ArrayList<multiblock.overhaul.fissionsfr.Block> blocks = new ArrayList<>();
                    multiblock.getAvailableBlocks(blocks);
                    for(Iterator<multiblock.overhaul.fissionsfr.Block> it = blocks.iterator(); it.hasNext();){
                        multiblock.overhaul.fissionsfr.Block b = it.next();
                        if(!b.isHeatsink())it.remove();
                    }
                    int[] count = new int[1];
                    multiblock.forEachPosition((x, y, z) -> {
                        multiblock.overhaul.fissionsfr.Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            count[0]++;
                        }
                    });
                    suggestor.setCount(count[0]*blocks.size());
                    multiblock.forEachPosition((x, y, z) -> {
                        for(multiblock.overhaul.fissionsfr.Block newBlock : blocks){
                            multiblock.overhaul.fissionsfr.Block block = multiblock.getBlock(x, y, z);
                            if(block==null||block.canBeQuickReplaced()){
                                if(newBlock.template.heatsinkCooling>(block==null?0:block.template.heatsinkCooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock), priorities));
                                else suggestor.task.max--;
                            }
                        }
                    });
                }
            });
        }
        if(multiblock instanceof OverhaulMSR){
            suggestors.add(new Suggestor<OverhaulMSR>("Rainbowificator", -1, -1){
                ArrayList<Priority> priorities = new ArrayList<>();
                {
                    priorities.add(new Priority<OverhaulMSR>("Rainbow", false, true){
                        @Override
                        protected double doCompare(OverhaulMSR main, OverhaulMSR other){
                            return (float)main.moduleData.get(that)-(float)other.moduleData.get(that);
                        }
                    });
                }
                @Override
                public String getDescription(){
                    return "Suggests adding or replacing heat sinks to increase the reactor's rainbow factor";
                }
                @Override
                public void generateSuggestions(OverhaulMSR multiblock, Suggestor.SuggestionAcceptor suggestor){
                    ArrayList<multiblock.overhaul.fissionmsr.Block> blocks = new ArrayList<>();
                    multiblock.getAvailableBlocks(blocks);
                    for(Iterator<multiblock.overhaul.fissionmsr.Block> it = blocks.iterator(); it.hasNext();){
                        multiblock.overhaul.fissionmsr.Block b = it.next();
                        if(!b.isHeater())it.remove();
                    }
                    int[] count = new int[1];
                    multiblock.forEachPosition((x, y, z) -> {
                        multiblock.overhaul.fissionmsr.Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            count[0]++;
                        }
                    });
                    suggestor.setCount(count[0]*blocks.size());
                    multiblock.forEachPosition((x, y, z) -> {
                        for(multiblock.overhaul.fissionmsr.Block newBlock : blocks){
                            multiblock.overhaul.fissionmsr.Block block = multiblock.getBlock(x, y, z);
                            if(block==null||block.canBeQuickReplaced()){
                                if(newBlock.template.heaterCooling>(block==null?0:block.template.heaterCooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock), priorities));
                                else suggestor.task.max--;
                            }
                        }
                    });
                }
            });
        }
        if(multiblock instanceof OverhaulFusionReactor){
            suggestors.add(new Suggestor<OverhaulFusionReactor>("Rainbowificator", -1, -1){
                ArrayList<Priority> priorities = new ArrayList<>();
                {
                    priorities.add(new Priority<OverhaulFusionReactor>("Rainbow", false, true){
                        @Override
                        protected double doCompare(OverhaulFusionReactor main, OverhaulFusionReactor other){
                            return (float)main.moduleData.get(that)-(float)other.moduleData.get(that);
                        }
                    });
                }
                @Override
                public String getDescription(){
                    return "Suggests adding or replacing heat sinks to increase the reactor's rainbow factor";
                }
                @Override
                public void generateSuggestions(OverhaulFusionReactor multiblock, Suggestor.SuggestionAcceptor suggestor){
                    ArrayList<multiblock.overhaul.fusion.Block> blocks = new ArrayList<>();
                    multiblock.getAvailableBlocks(blocks);
                    for(Iterator<multiblock.overhaul.fusion.Block> it = blocks.iterator(); it.hasNext();){
                        multiblock.overhaul.fusion.Block b = it.next();
                        if(!b.isHeatsink())it.remove();
                    }
                    int[] count = new int[1];
                    multiblock.forEachPosition((x, y, z) -> {
                        multiblock.overhaul.fusion.Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            count[0]++;
                        }
                    });
                    suggestor.setCount(count[0]*blocks.size());
                    multiblock.forEachPosition((x, y, z) -> {
                        for(multiblock.overhaul.fusion.Block newBlock : blocks){
                            multiblock.overhaul.fusion.Block block = multiblock.getBlock(x, y, z);
                            if(block==null||block.canBeQuickReplaced()){
                                if(newBlock.template.heatsinkCooling>(block==null?0:block.template.heatsinkCooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock), priorities));
                                else suggestor.task.max--;
                            }
                        }
                    });
                }
            });
        }
    }
}
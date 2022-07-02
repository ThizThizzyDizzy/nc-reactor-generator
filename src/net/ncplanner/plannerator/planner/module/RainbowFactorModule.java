package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.action.SetblockAction;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
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
            for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block b : m.getConfiguration().underhaul.fissionSFR.allBlocks){
                if(b.cooling!=0&&b.active==null)totalSinks++;
            }
            Set<net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block> unique = new HashSet<>();
            for(net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block b : ((UnderhaulSFR)m).getBlocks()){
                if(!b.isActive())continue;
                if(b.isCooler())unique.add(b.template);
            }
            return unique.size()/totalSinks;
        }
        if(m instanceof OverhaulSFR){
            float totalSinks = 0;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b : m.getConfiguration().overhaul.fissionSFR.allBlocks){
                if(b.heatsink)totalSinks++;
            }
            Set<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block> unique = new HashSet<>();
            for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b : ((OverhaulSFR)m).getBlocks()){
                if(!b.isActive())continue;
                if(b.isHeatsink())unique.add(b.template);
            }
            return unique.size()/totalSinks;
        }
        if(m instanceof OverhaulMSR){
            float totalSinks = 0;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : m.getConfiguration().overhaul.fissionMSR.allBlocks){
                if(b.heater)totalSinks++;
            }
            Set<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block> unique = new HashSet<>();
            for(net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b : ((OverhaulMSR)m).getBlocks()){
                if(!b.isActive())continue;
                if(b.isHeater())unique.add(b.template);
            }
            return unique.size()/totalSinks;
        }
        if(m instanceof OverhaulTurbine){
            float totalCoils = 0;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b : m.getConfiguration().overhaul.turbine.allBlocks){
                if(b.coil)totalCoils++;
            }
            Set<net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block> unique = new HashSet<>();
            for(net.ncplanner.plannerator.multiblock.overhaul.turbine.Block b : ((OverhaulTurbine)m).getBlocks()){
                if(!b.isActive())continue;
                if(b.isCoil())unique.add(b.template);
            }
            return unique.size()/totalCoils;
        }
        if(m instanceof OverhaulFusionReactor){
            float totalSinks = 0;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b : m.getConfiguration().overhaul.fusion.allBlocks){
                if(b.heatsink)totalSinks++;
            }
            Set<net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block> unique = new HashSet<>();
            for(net.ncplanner.plannerator.multiblock.overhaul.fusion.Block b : ((OverhaulFusionReactor)m).getBlocks()){
                if(!b.isActive())continue;
                if(b.isHeatsink())unique.add(b.template);
            }
            return unique.size()/totalSinks;
        }
        return null;
    }
    @Override
    public String getTooltip(Multiblock m, Float o){
        return "Rainbow Score: "+MathUtil.percent(o, 2);
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
        if(multiblock instanceof OverhaulTurbine){
            priorities.add(new Priority<OverhaulTurbine>("Rainbow", false, true){
                @Override
                protected double doCompare(OverhaulTurbine main, OverhaulTurbine other){
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
                    ArrayList<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block> blocks = new ArrayList<>();
                    multiblock.getAvailableBlocks(blocks);
                    for(Iterator<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block> it = blocks.iterator(); it.hasNext();){
                        net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block b = it.next();
                        if(!b.isCooler()||b.template.active!=null)it.remove();
                    }
                    int[] count = new int[1];
                    multiblock.forEachPosition((x, y, z) -> {
                        net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            count[0]++;
                        }
                    });
                    suggestor.setCount(count[0]*blocks.size());
                    multiblock.forEachPosition((x, y, z) -> {
                        for(net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block newBlock : blocks){
                            net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block block = multiblock.getBlock(x, y, z);
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
                    ArrayList<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> blocks = new ArrayList<>();
                    multiblock.getAvailableBlocks(blocks);
                    for(Iterator<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> it = blocks.iterator(); it.hasNext();){
                        net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = it.next();
                        if(!b.isHeatsink())it.remove();
                    }
                    int[] count = new int[1];
                    multiblock.forEachPosition((x, y, z) -> {
                        net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            count[0]++;
                        }
                    });
                    suggestor.setCount(count[0]*blocks.size());
                    multiblock.forEachPosition((x, y, z) -> {
                        for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block newBlock : blocks){
                            net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block = multiblock.getBlock(x, y, z);
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
                    ArrayList<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block> blocks = new ArrayList<>();
                    multiblock.getAvailableBlocks(blocks);
                    for(Iterator<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block> it = blocks.iterator(); it.hasNext();){
                        net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = it.next();
                        if(!b.isHeater())it.remove();
                    }
                    int[] count = new int[1];
                    multiblock.forEachPosition((x, y, z) -> {
                        net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            count[0]++;
                        }
                    });
                    suggestor.setCount(count[0]*blocks.size());
                    multiblock.forEachPosition((x, y, z) -> {
                        for(net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block newBlock : blocks){
                            net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block block = multiblock.getBlock(x, y, z);
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
                    ArrayList<net.ncplanner.plannerator.multiblock.overhaul.fusion.Block> blocks = new ArrayList<>();
                    multiblock.getAvailableBlocks(blocks);
                    for(Iterator<net.ncplanner.plannerator.multiblock.overhaul.fusion.Block> it = blocks.iterator(); it.hasNext();){
                        net.ncplanner.plannerator.multiblock.overhaul.fusion.Block b = it.next();
                        if(!b.isHeatsink())it.remove();
                    }
                    int[] count = new int[1];
                    multiblock.forEachPosition((x, y, z) -> {
                        net.ncplanner.plannerator.multiblock.overhaul.fusion.Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            count[0]++;
                        }
                    });
                    suggestor.setCount(count[0]*blocks.size());
                    multiblock.forEachPosition((x, y, z) -> {
                        for(net.ncplanner.plannerator.multiblock.overhaul.fusion.Block newBlock : blocks){
                            net.ncplanner.plannerator.multiblock.overhaul.fusion.Block block = multiblock.getBlock(x, y, z);
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
    private final EditorOverlay rainbowOverlay = new EditorOverlay("Rainbow factor", "Highlights blocks that are the only block of their type", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, Block block, Multiblock multiblock){
            boolean isRainbowable = false;
            if(block instanceof net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block){
                net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block)block;
                isRainbowable = b.template.cooling!=0&&b.template.active==null;
            }
            if(block instanceof net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block){
                net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block;
                isRainbowable = b.template.isHeatsink();
            }
            if(block instanceof net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block){
                net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)block;
                isRainbowable = b.template.isHeater();
            }
            if(block instanceof net.ncplanner.plannerator.multiblock.overhaul.turbine.Block){
                net.ncplanner.plannerator.multiblock.overhaul.turbine.Block b = (net.ncplanner.plannerator.multiblock.overhaul.turbine.Block)block;
                isRainbowable = b.template.isCoil();
            }
            if(block instanceof net.ncplanner.plannerator.multiblock.overhaul.fusion.Block){
                net.ncplanner.plannerator.multiblock.overhaul.fusion.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fusion.Block)block;
                isRainbowable = b.template.isHeatsink();
            }
            if(isRainbowable&&multiblock.count(block)==1){
                int count = Core.theme.getRainbowColorCount();
                for(int i = 0; i<count; i++){
                    renderer.setColor(Core.theme.getRainbowColor(i));
                    float b = width/24;
                    float p1 = i/(float)count;
                    float p2 = (i+1)/(float)count;
                    renderer.fillRect(x+b+width*p1, y, x+b+width*p2, y+b);
                    renderer.fillRect(x, y+b+height*p1, x+b, y+b+height*p2);
                    renderer.fillRect(x+b+width*(1-p2), y+width-b, x+b+width*(1-p1), y+width);
                    renderer.fillRect(x+width-b, y+b+height*(1-p2), x+width, y+b+height*(1-p1));
                }
            }
        }
    };
    @Override
    public void getEditorOverlays(Multiblock multiblock, ArrayList<EditorOverlay> overlays){
        overlays.add(rainbowOverlay);
    }
}
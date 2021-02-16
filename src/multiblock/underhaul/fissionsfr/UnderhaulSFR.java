package multiblock.underhaul.fissionsfr;
import generator.Priority;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import multiblock.Action;
import multiblock.Direction;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.action.SetblockAction;
import multiblock.action.SetblocksAction;
import multiblock.ppe.ClearInvalid;
import multiblock.ppe.PostProcessingEffect;
import multiblock.ppe.SmartFillUnderhaulSFR;
import multiblock.symmetry.AxialSymmetry;
import multiblock.symmetry.Symmetry;
import planner.Core;
import planner.FormattedText;
import planner.Task;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentMinimaList;
import planner.editor.module.Module;
import planner.editor.suggestion.Suggestion;
import planner.editor.suggestion.Suggestor;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
public class UnderhaulSFR extends Multiblock<Block>{
    public int netHeat;
    private int power, heat, cooling, cells;
    private float efficiency;
    public Fuel fuel;
    private double heatMult;
    public UnderhaulSFR(){
        this(null);
    }
    public UnderhaulSFR(Configuration configuration){
        this(configuration, 7, 5, 7, null);
    }
    public UnderhaulSFR(Configuration configuration, int x, int y, int z, Fuel fuel){
        super(configuration, x, y, z);
        this.fuel = fuel==null?getConfiguration().underhaul.fissionSFR.allFuels.get(0):fuel;
    }
    @Override
    public String getDefinitionName(){
        return "Underhaul SFR";
    }
    @Override
    public UnderhaulSFR newInstance(Configuration configuration){
        return new UnderhaulSFR(configuration);
    }
    @Override
    public Multiblock<Block> newInstance(Configuration configuration, int x, int y, int z){
        return new UnderhaulSFR(configuration, x, y, z, null);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getConfiguration()==null||getConfiguration().underhaul==null||getConfiguration().underhaul.fissionSFR==null)return;
        for(multiblock.configuration.underhaul.fissionsfr.Block block : getConfiguration().underhaul.fissionSFR.allBlocks){
            blocks.add(new Block(getConfiguration(),-1,-1,-1,block));
        }
    }
    @Override
    public int getMinX(){
        return getConfiguration().underhaul.fissionSFR.minSize;
    }
    @Override
    public int getMinY(){
        return getConfiguration().underhaul.fissionSFR.minSize;
    }
    @Override
    public int getMinZ(){
        return getConfiguration().underhaul.fissionSFR.minSize;
    }
    @Override
    public int getMaxX(){
        return getConfiguration().underhaul.fissionSFR.maxSize;
    }
    @Override
    public int getMaxY(){
        return getConfiguration().underhaul.fissionSFR.maxSize;
    }
    @Override
    public int getMaxZ(){
        return getConfiguration().underhaul.fissionSFR.maxSize;
    }
    @Override
    public void doCalculate(List<Block> blocks){
        Task core = calculateTask.addSubtask(new Task("Calculating Core"));
        Task coolers = calculateTask.addSubtask(new Task("Calculating Coolers"));
        Task stats = calculateTask.addSubtask(new Task("Calculating Stats"));
        for(int i = 0; i<blocks.size(); i++){
            blocks.get(i).calculateCore(this);
            core.progress = i/(double)blocks.size();
        }
        core.finish();
        float totalHeatMult = 0;
        float totalEnergyMult = 0;
        cells = 0;
        boolean somethingChanged;
        int n = 0;
        do{
            somethingChanged = false;
            n++;
            coolers.name = "Calculating Coolers"+(n>1?" ("+n+")":"");
            for(int i = 0; i<blocks.size(); i++){
                if(blocks.get(i).calculateCooler(this))somethingChanged = true;
                coolers.progress = i/(double)blocks.size();
            }
        }while(somethingChanged);
        coolers.finish();
        cooling = 0;
        ArrayList<Block> allBlocks = getBlocks();
        for(int i = 0; i<allBlocks.size(); i++){
            Block block = allBlocks.get(i);
            if(block.isFuelCell()){
                totalHeatMult+=block.heatMult;
                totalEnergyMult+=block.energyMult;
                cells++;
            }
            if(block.isCooler()&&block.isActive())cooling+=block.getCooling();
            stats.progress = i/(double)allBlocks.size();
        }
        this.heatMult = totalHeatMult/cells;
        if(Double.isNaN(heatMult))heatMult = 0;
        heat = (int) (totalHeatMult*fuel.heat);
        netHeat = heat-cooling;
        power = (int) (totalEnergyMult*fuel.power);
        efficiency = totalEnergyMult/cells;
        if(Double.isNaN(efficiency))efficiency = 0;
        stats.finish();
    }
    @Override
    protected Block newCasing(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, null);
    }
    @Override
    protected FormattedText getExtraSaveTooltip(){
        return new FormattedText("Fuel: "+fuel.name);
    }
    @Override
    protected String getExtraBotTooltip(){
        return getTooltip().text;
    }
    @Override
    public FormattedText getTooltip(){
        String tooltip = "Power Generation: "+power+"RF/t\n"
                + "Total Heat: "+heat+"H/t\n"
                + "Total Cooling: "+cooling+"H/t\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Efficiency: "+percent(efficiency, 0)+"\n"
                + "Heat multiplier: "+percent(heatMult, 0)+"\n"
                + "Fuel cells: "+cells;
        tooltip+=getModuleTooltip();
        return new FormattedText(tooltip, netHeat>0?Core.theme.getRed():null);
    }
    @Override
    public int getMultiblockID(){
        return 0;
    }
    @Override
    protected void save(NCPFFile ncpf, Configuration configuration, Config config){
        ConfigNumberList size = new ConfigNumberList();
        size.add(getX());
        size.add(getY());
        size.add(getZ());
        config.set("size", size);
        config.set("fuel", (byte)configuration.underhaul.fissionSFR.allFuels.indexOf(fuel));
        boolean compact = isCompact(configuration);//find perfect compression ratio
        config.set("compact", compact);
        ConfigNumberList blox = new ConfigNumberList();
        if(compact){
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block block = getBlock(x, y, z);
                        if(block==null)blox.add(0);
                        else blox.add(configuration.underhaul.fissionSFR.allBlocks.indexOf(block.template)+1);
                    }
                }
            }
        }else{
            for(Block block : getBlocks()){
                blox.add(block.x);
                blox.add(block.y);
                blox.add(block.z);
                blox.add(configuration.underhaul.fissionSFR.allBlocks.indexOf(block.template)+1);
            }
        }
        config.set("blocks", blox);
    }
    private boolean isCompact(Configuration configuration){
        int blockCount = getBlocks().size();
        int volume = getX()*getY()*getZ();
        int bitsPerDim = logBase(2, Math.max(getX(), Math.max(getY(), getZ())));
        int bitsPerType = logBase(2, configuration.underhaul.fissionSFR.allBlocks.size());
        int compactBits = bitsPerType*volume;
        int spaciousBits = 4*Math.max(bitsPerDim, bitsPerType)*blockCount;
        return compactBits<spaciousBits;
    }
    private static int logBase(int base, int n){
        return (int)(Math.log(n)/Math.log(base));
    }
    @Override
    public void convertTo(Configuration to){
        if(to.underhaul==null||to.underhaul.fissionSFR==null)return;
        for(Block block : getBlocks()){
            block.convertTo(to);
        }
        fuel = to.underhaul.fissionSFR.convert(fuel);
        configuration = to;
    }
    @Override
    public boolean validate(){
        return false;
    }
    @Override
    public boolean exists(){
        return getConfiguration().underhaul!=null&&getConfiguration().underhaul.fissionSFR!=null;
    }
    @Override
    public void addGeneratorSettings(MenuComponentMinimaList multiblockSettings){}
    private boolean isValid(){
        return power>0;
    }
    @Override
    public void getGenerationPriorities(ArrayList<Priority> priorities){
        priorities.add(new Priority<UnderhaulSFR>("Valid (>0 output)", true, true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                return 0;
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Stability", false, true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return Math.max(0, other.netHeat)-Math.max(0, main.netHeat);
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Efficiency", true, true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return main.efficiency-other.efficiency;
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Output", true, true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return main.power-other.power;
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Minimize Heat", false, true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return other.heat-main.heat;
            }
        });
        priorities.add(new Priority<UnderhaulSFR>("Fuel usage", true, true){
            @Override
            protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                return main.cells-other.cells;
            }
        });
        for(Module m : Core.modules){
            if(m.isActive())m.getGenerationPriorities(this, priorities);
        }
    }
    @Override
    public void getGenerationPriorityPresets(ArrayList<Priority> priorities, ArrayList<Priority.Preset> presets){
        presets.add(new Priority.Preset("Efficiency", priorities.get(0), priorities.get(1), priorities.get(2), priorities.get(3), priorities.get(4)).addAlternative("Efficient"));
        presets.add(new Priority.Preset("Output", priorities.get(0), priorities.get(1), priorities.get(3), priorities.get(2), priorities.get(4)));
        presets.add(new Priority.Preset("Fuel Usage (Burner)", priorities.get(0), priorities.get(1), priorities.get(5), priorities.get(4), priorities.get(3), priorities.get(2)).addAlternative("Fuel Usage").addAlternative("Speed").addAlternative("Cell Count").addAlternative("Breeder").addAlternative("Burner").addAlternative("Fast"));
    }
    @Override
    public void getSymmetries(ArrayList<Symmetry> symmetries){
        symmetries.add(AxialSymmetry.X);
        symmetries.add(AxialSymmetry.Y);
        symmetries.add(AxialSymmetry.Z);
    }
    @Override
    public void getPostProcessingEffects(ArrayList<PostProcessingEffect> postProcessingEffects){
        postProcessingEffects.add(new ClearInvalid());
        postProcessingEffects.add(new SmartFillUnderhaulSFR());
    }
    @Override
    public UnderhaulSFR blankCopy(){
        return new UnderhaulSFR(configuration, getX(), getY(), getZ(), fuel);
    }
    @Override
    public UnderhaulSFR doCopy(){
        UnderhaulSFR copy = blankCopy();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block get = getBlock(x, y, z);
                    if(get!=null)copy.setBlockExact(x, y, z, get.copy());
                }
            }
        }
        copy.netHeat = netHeat;
        copy.power = power;
        copy.heat = heat;
        copy.cooling = cooling;
        copy.cells = cells;
        copy.efficiency = efficiency;
        copy.heatMult = heatMult;
        return copy;
    }
    @Override
    protected int doCount(Object o){
        throw new IllegalArgumentException("Cannot count "+o.getClass().getName()+" in "+getDefinitionName()+"!");
    }
    @Override
    public String getGeneralName(){
        return "Reactor";
    }
    @Override
    public boolean isCompatible(Multiblock<Block> other){
        return ((UnderhaulSFR)other).fuel==fuel;
    }
    @Override
    protected void getFluidOutputs(HashMap<String, Double> outputs){}
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){
        parts.add(new PartCount(null, "Casing", getX()*getZ()*2+getX()*getY()*2+getY()*getZ()*2));
    }
    @Override
    public String getDescriptionTooltip(){
        return "Underhaul SFRs are Solid-Fueled Fission reactors in NuclearCraft\nIf you have blocks called \"Heat Sink\" instead of \"Cooler\", you are playing Overhaul";
    }
    @Override
    public void getSuggestors(ArrayList<Suggestor> suggestors){
        suggestors.add(new Suggestor<UnderhaulSFR>("Full Cell Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<UnderhaulSFR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                        return main.efficiency-other.efficiency;
                    }
                });
                priorities.add(new Priority<UnderhaulSFR>("Power", true, true){
                    @Override
                    protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                        return main.power-other.power;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding Fuel cells with moderators to increase efficiency and output";
            }
            @Override
            public void generateSuggestions(UnderhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> cells = new ArrayList<>();
                multiblock.getAvailableBlocks(cells);
                for(Iterator<Block> it = cells.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isFuelCell())it.remove();
                }
                ArrayList<Block> moderators = new ArrayList<>();
                multiblock.getAvailableBlocks(moderators);
                for(Iterator<Block> it = moderators.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isModerator())it.remove();
                }
                int cellCount = 0;
                for(int y = 0; y<multiblock.getY(); y++){
                    for(int z = 0; z<multiblock.getZ(); z++){
                        for(int x = 0; x<multiblock.getX(); x++){
                            Block b = multiblock.getBlock(x, y, z);
                            if(b!=null&&b.isFuelCell())cellCount++;
                        }
                    }
                }
                suggestor.setCount(multiblock.getX()*multiblock.getY()*multiblock.getZ()*cells.size()*moderators.size()-cellCount);
                for(Block cell : cells){
                    for(Block moderator : moderators){
                        for(int y = 0; y<multiblock.getY(); y++){
                            for(int z = 0; z<multiblock.getZ(); z++){
                                for(int x = 0; x<multiblock.getX(); x++){
                                    Block was = multiblock.getBlock(x, y, z);
                                    if(was!=null&&was.isFuelCell())continue;
                                    ArrayList<Action> actions = new ArrayList<>();
                                    actions.add(new SetblockAction(x, y, z, cell.newInstance(x, y, z)));
                                    SetblocksAction multi = new SetblocksAction(moderator);
                                    DIRECTION:for(Direction d : directions){
                                        ArrayList<int[]> toSet = new ArrayList<>();
                                        boolean yep = false;
                                        for(int i = 1; i<=configuration.underhaul.fissionSFR.neutronReach+1; i++){
                                            int X = x+d.x*i;
                                            int Y = y+d.y*i;
                                            int Z = z+d.z*i;
                                            Block b = multiblock.getBlock(X, Y, Z);
                                            if(b!=null){
                                                if(b.isCasing())break;//end of the line
                                                if(b.isModerator())continue;//already a moderator
                                                if(b.isFuelCell()){
                                                    yep = true;
                                                    break;
                                                }
                                            }
                                            if(i<=configuration.underhaul.fissionSFR.neutronReach){
                                                toSet.add(new int[]{X,Y,Z});
                                            }
                                        }
                                        if(!toSet.isEmpty()){
                                            if(yep){
                                                for(int[] b : toSet)multi.add(b[0], b[1], b[2]);
                                            }else{
                                                int[] b = toSet.get(0);
                                                multi.add(b[0], b[1], b[2]);
                                            }
                                        }
                                    }
                                    if(!multi.isEmpty())actions.add(multi);
                                    if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion("Add "+cell.getName()+(multi.isEmpty()?"":" with "+moderator.getName()), actions, priorities));
                                }
                            }
                        }
                    }
                }
            }
        });
        suggestors.add(new Suggestor<UnderhaulSFR>("Lone Cell Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<UnderhaulSFR>("Power", true, true){
                    @Override
                    protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                        return main.power-other.power;
                    }
                });
                priorities.add(new Priority<UnderhaulSFR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                        return main.efficiency-other.efficiency;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding Fuel cells to increase output";
            }
            @Override
            public void generateSuggestions(UnderhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> blocks = new ArrayList<>();
                multiblock.getAvailableBlocks(blocks);
                for(Iterator<Block> it = blocks.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isFuelCell())it.remove();
                }
                int cellCount = 0;
                for(int y = 0; y<multiblock.getY(); y++){
                    for(int z = 0; z<multiblock.getZ(); z++){
                        for(int x = 0; x<multiblock.getX(); x++){
                            Block b = multiblock.getBlock(x, y, z);
                            if(b!=null&&b.isFuelCell())cellCount++;
                        }
                    }
                }
                suggestor.setCount(multiblock.getX()*multiblock.getY()*multiblock.getZ()*blocks.size()-cellCount);
                for(Block b : blocks){
                    for(int x = 0; x<multiblock.getX(); x++){
                        for(int y = 0; y<multiblock.getY(); y++){
                            for(int z = 0; z<multiblock.getZ(); z++){
                                Block was = multiblock.getBlock(x, y, z);
                                if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion(was==null?"Add "+b.getName():"Replace "+was.getName()+" with "+b.getName(), new SetblockAction(x, y, z, b.newInstance(x, y, z)), priorities));
                            }
                        }
                    }
                }
            }
        });
        suggestors.add(new Suggestor<UnderhaulSFR>("Moderator Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<UnderhaulSFR>("Efficiency", true, true){
                    @Override
                    protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                        return main.efficiency-other.efficiency;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding Moderator blocks to increase efficiency";
            }
            @Override
            public void generateSuggestions(UnderhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> blocks = new ArrayList<>();
                multiblock.getAvailableBlocks(blocks);
                for(Iterator<Block> it = blocks.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isModerator())it.remove();
                }
                int modCount = 0;
                for(int y = 0; y<multiblock.getY(); y++){
                    for(int z = 0; z<multiblock.getZ(); z++){
                        for(int x = 0; x<multiblock.getX(); x++){
                            Block b = multiblock.getBlock(x, y, z);
                            if(b!=null&&b.isModerator())modCount++;
                        }
                    }
                }
                suggestor.setCount(multiblock.getX()*multiblock.getY()*multiblock.getZ()*blocks.size()-modCount);
                for(Block b : blocks){
                    for(int x = 0; x<multiblock.getX(); x++){
                        for(int y = 0; y<multiblock.getY(); y++){
                            for(int z = 0; z<multiblock.getZ(); z++){
                                Block was = multiblock.getBlock(x, y, z);
                                if(was!=null&&was.isModerator())continue;
                                if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion(was==null?"Add "+b.getName():"Replace "+was.getName()+" with "+b.getName(), new SetblockAction(x, y, z, b.newInstance(x, y, z)), priorities));
                            }
                        }
                    }
                }
            }
        });
        suggestors.add(new Suggestor<UnderhaulSFR>("Passive Cooler Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<UnderhaulSFR>("Temperature", true, true){
                    @Override
                    protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                        return other.netHeat-main.netHeat;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding or replacing passive coolers to cool the reactor";
            }
            @Override
            public void generateSuggestions(UnderhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> blocks = new ArrayList<>();
                multiblock.getAvailableBlocks(blocks);
                for(Iterator<Block> it = blocks.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isCooler()||b.template.active!=null)it.remove();
                }
                int count = 0;
                for(int x = 0; x<multiblock.getX(); x++){
                    for(int y = 0; y<multiblock.getY(); y++){
                        for(int z = 0; z<multiblock.getZ(); z++){
                            Block block = multiblock.getBlock(x, y, z);
                            if(block==null||block.canBeQuickReplaced()){
                                count++;
                            }
                        }
                    }
                }
                suggestor.setCount(count*blocks.size());
                for(int x = 0; x<multiblock.getX(); x++){
                    for(int y = 0; y<multiblock.getY(); y++){
                        for(int z = 0; z<multiblock.getZ(); z++){
                            for(Block newBlock : blocks){
                                Block block = multiblock.getBlock(x, y, z);
                                if(block==null||block.canBeQuickReplaced()){
                                    if(newBlock.template.cooling>(block==null?0:block.template.cooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock.newInstance(x, y, z)), priorities));
                                    else suggestor.task.max--;
                                }
                            }
                        }
                    }
                }
            }
        });
        suggestors.add(new Suggestor<UnderhaulSFR>("Active Cooler Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<UnderhaulSFR>("Temperature", true, true){
                    @Override
                    protected double doCompare(UnderhaulSFR main, UnderhaulSFR other){
                        return other.netHeat-main.netHeat;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding or replacing active coolers to cool the reactor";
            }
            @Override
            public void generateSuggestions(UnderhaulSFR multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> blocks = new ArrayList<>();
                multiblock.getAvailableBlocks(blocks);
                for(Iterator<Block> it = blocks.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isCooler()||b.template.active==null)it.remove();
                }
                int count = 0;
                for(int x = 0; x<multiblock.getX(); x++){
                    for(int y = 0; y<multiblock.getY(); y++){
                        for(int z = 0; z<multiblock.getZ(); z++){
                            Block block = multiblock.getBlock(x, y, z);
                            if(block==null||block.canBeQuickReplaced()){
                                count++;
                            }
                        }
                    }
                }
                suggestor.setCount(count*blocks.size());
                for(int x = 0; x<multiblock.getX(); x++){
                    for(int y = 0; y<multiblock.getY(); y++){
                        for(int z = 0; z<multiblock.getZ(); z++){
                            for(Block newBlock : blocks){
                                Block block = multiblock.getBlock(x, y, z);
                                if(block==null||block.canBeQuickReplaced()){
                                    if(newBlock.template.cooling>(block==null?0:block.template.cooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock.newInstance(x, y, z)), priorities));
                                    else suggestor.task.max--;
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
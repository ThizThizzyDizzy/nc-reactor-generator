package multiblock.underhaul.fissionsfr;
import generator.Priority;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import multiblock.Action;
import multiblock.Axis;
import multiblock.CuboidalMultiblock;
import multiblock.Direction;
import multiblock.FluidStack;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.action.SetblockAction;
import multiblock.action.SetblocksAction;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import multiblock.decal.AdjacentCellDecal;
import multiblock.decal.AdjacentModeratorDecal;
import multiblock.decal.BlockInvalidDecal;
import multiblock.decal.BlockValidDecal;
import multiblock.decal.MissingCasingDecal;
import multiblock.decal.UnderhaulModeratorLineDecal;
import multiblock.ppe.ClearInvalid;
import multiblock.ppe.PostProcessingEffect;
import multiblock.ppe.SmartFillUnderhaulSFR;
import multiblock.symmetry.AxialSymmetry;
import multiblock.symmetry.Symmetry;
import planner.Core;
import planner.FormattedText;
import planner.Task;
import planner.editor.suggestion.Suggestion;
import planner.editor.suggestion.Suggestor;
import planner.exception.MissingConfigurationEntryException;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentMinimaList;
import planner.module.Module;
import simplelibrary.Queue;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
public class UnderhaulSFR extends CuboidalMultiblock<Block>{
    public int netHeat;
    private int power, heat, cooling, cells;
    private float efficiency;
    public Fuel fuel;
    private double heatMult;
    private int calcStep = 0;
    private int calcSubstep = 0;
    private Task calcCasing;
    private Task calcCore;
    private Task calcCoolers;
    private Task calcStats;
    private int numControllers;
    private int missingCasings;
    public UnderhaulSFR(){
        this(null);
    }
    public UnderhaulSFR(Configuration configuration){
        this(configuration, 7, 5, 7, null);
    }
    public UnderhaulSFR(Configuration configuration, int x, int y, int z, Fuel fuel){
        super(configuration, x, y, z);
        this.fuel = fuel==null?(exists()?getConfiguration().underhaul.fissionSFR.allFuels.get(0):null):fuel;
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
    public void clearData(List<Block> blocks){
        super.clearData(blocks);
        heatMult = efficiency = power = heat = cooling = netHeat = cells = 0;
    }
    @Override
    public void genCalcSubtasks(){
        calcCasing = calculateTask.addSubtask(new Task("Checking Casing"));
        calcCore = calculateTask.addSubtask(new Task("Calculating Core"));
        calcCoolers = calculateTask.addSubtask(new Task("Calculating Coolers"));
        calcStats = calculateTask.addSubtask(new Task("Calculating Stats"));
    }
    @Override
    public boolean doCalculationStep(List<Block> blocks, boolean addDecals){
        switch(calcStep){
            case 0://casing
                numControllers = 0;
                forEachCasingEdgePosition((x, y, z) -> {
                    Block block = getBlock(x, y, z);
                    if(block==null)return;
                    if(block.isController()){
                        block.casingValid = true;
                        numControllers++;
                    }
                });
                missingCasings = 0;
                forEachCasingFacePosition((x, y, z) -> {
                    Block block = getBlock(x, y, z);
                    if(block==null||!block.isCasing()){
                        missingCasings++;
                        if(addDecals)decals.enqueue(new MissingCasingDecal(x,y,z));
                    }
                    if(block!=null&&block.isCasing()){
                        block.casingValid = true;
                        if(addDecals)decals.enqueue(new BlockValidDecal(x,y,z));
                    }
                });
                calcCasing.finish();
                calcStep++;
                return true;
            case 1://core
                for(int i = 0; i<blocks.size(); i++){
                    calculateCore(blocks.get(i), addDecals);
                    calcCore.progress = i/(double)blocks.size();
                }
                calcCore.finish();
                calcStep++;
                return true;
            case 2://coolers
                calcSubstep++;
                boolean somethingChanged = false;
                calcCoolers.name = "Calculating Coolers"+(calcSubstep>1?" ("+calcSubstep+")":"");
                for(int i = 0; i<blocks.size(); i++){
                    if(calculateCooler(blocks.get(i), addDecals))somethingChanged = true;
                    calcCoolers.progress = i/(double)blocks.size();
                }
                if(somethingChanged)return true;
                calcCoolers.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 3://stats
                float totalHeatMult = 0;
                float totalEnergyMult = 0;
                cells = 0;
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
                    calcStats.progress = i/(double)allBlocks.size();
                }
                this.heatMult = totalHeatMult/cells;
                if(Double.isNaN(heatMult))heatMult = 0;
                heat = (int) (totalHeatMult*fuel.heat);
                netHeat = heat-cooling;
                power = (int) (totalEnergyMult*fuel.power);
                efficiency = totalEnergyMult/cells;
                if(Double.isNaN(efficiency))efficiency = 0;
                calcStats.finish();
                calcStep = 0;
                return false;
            default:
                throw new IllegalStateException("Invalid calculation step: "+calcStep+"!");
        }
    }
    public void calculateCore(Block that, boolean addDecals){
        if(!that.template.fuelCell)return;
        if(addDecals)decals.enqueue(new BlockValidDecal(that.x, that.y, that.z));
        for(Direction d : directions){
            Queue<Block> toValidate = new Queue<>();
            for(int i = 1; i<=getConfiguration().underhaul.fissionSFR.neutronReach+1; i++){
                if(!contains(that.x+d.x*i, that.y+d.y*i, that.z+d.z*i))break;
                Block block = getBlock(that.x+d.x*i,that.y+d.y*i,that.z+d.z*i);
                if(block==null)break;
                if(block.isModerator()){
                    if(i==1){
                        block.moderatorActive = block.moderatorValid = true;
                        if(addDecals)decals.enqueue(new AdjacentModeratorDecal(block.x, block.y, block.z, d.getOpposite()));
                        that.adjacentModerators++;
                    }
                    toValidate.enqueue(block);
                    continue;
                }
                if(block.isFuelCell()){
                    if(addDecals)decals.enqueue(new AdjacentCellDecal(that.x, that.y, that.z, d));
                    for(Block b : toValidate){
                        b.moderatorValid = true;
                        if(addDecals&&d.x+d.y+d.z<1)decals.enqueue(new UnderhaulModeratorLineDecal(b.x, b.y, b.z, Axis.fromDirection(d)));//negative directions go last; this stops double-decals
                    }
                    that.adjacentCells++;
                    break;
                }
                break;
            }
        }
        float baseEff = that.energyMult = that.adjacentCells+1;
        that.heatMult = (baseEff*(baseEff+1))/2;
        that.energyMult+=baseEff/6*getConfiguration().underhaul.fissionSFR.moderatorExtraPower*that.adjacentModerators;
        that.heatMult+=baseEff/6*getConfiguration().underhaul.fissionSFR.moderatorExtraHeat*that.adjacentModerators;
    }
    /**
     * Calculates the cooler
     * @param block the block to calculate
     * @param addDecals whether or not to add decals
     * @return <code>true</code> if the cooler state has changed
     */
    public boolean calculateCooler(Block block, boolean addDecals){
        if(block.template.cooling==0)return false;
        boolean wasValid = block.coolerValid;
        for(PlacementRule rule : block.template.rules){
            if(!rule.isValid(block, this)){
                if(block.coolerValid&&addDecals)decals.enqueue(new BlockInvalidDecal(block.x,block.y,block.z));
                block.coolerValid = false;
                return wasValid!=block.coolerValid;
            }
        }
        if(!block.coolerValid&&addDecals)decals.enqueue(new BlockValidDecal(block.x,block.y,block.z));
        block.coolerValid = true;
        return wasValid!=block.coolerValid;
    }
    @Override
    protected FormattedText getExtraSaveTooltip(){
        return new FormattedText("Fuel: "+fuel.getDisplayName());
    }
    @Override
    public FormattedText getTooltip(boolean full){
        String mainTooltip = "Power Generation: "+power+"RF/t\n"
                + "Total Heat: "+heat+"H/t\n"
                + "Total Cooling: "+cooling+"H/t\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Efficiency: "+percent(efficiency, 0)+"\n"
                + "Heat multiplier: "+percent(heatMult, 0)+"\n"
                + "Fuel cells: "+cells;
        mainTooltip+=getModuleTooltip();
        FormattedText finalTooltip = new FormattedText();
        if(numControllers<1)finalTooltip.addText("No controller!", Core.theme.getTooltipInvalidTextColor());
        if(numControllers>1)finalTooltip.addText("Too many controllers!", Core.theme.getTooltipInvalidTextColor());
        if(missingCasings>0)finalTooltip.addText("Casing incomplete! (Missing "+missingCasings+")", Core.theme.getTooltipInvalidTextColor());
        finalTooltip.addText(new FormattedText(mainTooltip, netHeat>0?Core.theme.getTooltipInvalidTextColor():Core.theme.getTooltipTextColor()));
        return finalTooltip;
    }
    @Override
    public int getMultiblockID(){
        return 0;
    }
    @Override
    protected void save(NCPFFile ncpf, Configuration configuration, Config config){
        config.set("fuel", configuration.underhaul.fissionSFR.allFuels.indexOf(fuel));
        boolean compact = isCompact(configuration);//find perfect compression ratio
        config.set("compact", compact);
        ConfigNumberList blox = new ConfigNumberList();
        if(compact){
            forEachPosition((x, y, z) -> {
                Block block = getBlock(x, y, z);
                if(block==null)blox.add(0);
                else blox.add(configuration.underhaul.fissionSFR.allBlocks.indexOf(block.template)+1);
            });
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
        return isCompact(configuration.underhaul.fissionSFR.allBlocks.size());
    }
    @Override
    public void doConvertTo(Configuration to) throws MissingConfigurationEntryException{
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
        return super.exists()&&getConfiguration().underhaul!=null&&getConfiguration().underhaul.fissionSFR!=null;
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
        presets.add(new Priority.Preset("Output", priorities.get(0), priorities.get(1), priorities.get(3), priorities.get(2), priorities.get(4)).addAlternative("Power"));
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
        return new UnderhaulSFR(configuration, getInternalWidth(), getInternalHeight(), getInternalDepth(), fuel);
    }
    @Override
    public UnderhaulSFR doCopy(){
        UnderhaulSFR copy = blankCopy();
        forEachPosition((x, y, z) -> {
            Block get = getBlock(x, y, z);
            if(get!=null)copy.setBlockExact(x, y, z, get.copy());
        });
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
    protected void getFluidOutputs(ArrayList<FluidStack> outputs){}
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){}
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
                int[] cellCount = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block b = multiblock.getBlock(x, y, z);
                    if(b!=null&&b.isFuelCell())cellCount[0]++;
                });
                suggestor.setCount(multiblock.getInternalVolume()*cells.size()*moderators.size()-cellCount[0]);
                for(Block cell : cells){
                    for(Block moderator : moderators){
                        multiblock.forEachInternalPosition((x, y, z) -> {
                            Block was = multiblock.getBlock(x, y, z);
                            if(was!=null&&was.isFuelCell())return;
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
                                    if(X==0||Y==0||Z==0||X==UnderhaulSFR.this.x+1||Y==UnderhaulSFR.this.y+1||Z==UnderhaulSFR.this.z+1)break;//that's the casing
                                    if(!multiblock.contains(X, Y, Z))break;//end of the line;
                                    Block b = multiblock.getBlock(X, Y, Z);
                                    if(b!=null){
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
                                        for(int[] b : toSet){
                                            if(b[0]==0||b[1]==0||b[2]==0||b[0]==UnderhaulSFR.this.x+1||b[1]==UnderhaulSFR.this.y+1||b[2]==UnderhaulSFR.this.z+1)continue;//also casing
                                            multi.add(b[0], b[1], b[2]);
                                        }
                                    }else{
                                        int[] b = toSet.get(0);
                                        if(b[0]==0||b[1]==0||b[2]==0||b[0]==UnderhaulSFR.this.x+1||b[1]==UnderhaulSFR.this.y+1||b[2]==UnderhaulSFR.this.z+1){
                                        }else{
                                            multi.add(b[0], b[1], b[2]);
                                        }
                                    }
                                }
                            }
                            if(!multi.isEmpty())actions.add(multi);
                            if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion("Add "+cell.getName()+(multi.isEmpty()?"":" with "+moderator.getName()), actions, priorities, cell.template.displayTexture, moderator.template.displayTexture));
                        });
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
                int[] cellCount = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block b = multiblock.getBlock(x, y, z);
                    if(b!=null&&b.isFuelCell())cellCount[0]++;
                });
                suggestor.setCount(multiblock.getInternalVolume()*blocks.size()-cellCount[0]);
                for(Block b : blocks){
                    multiblock.forEachInternalPosition((x, y, z) -> {
                        Block was = multiblock.getBlock(x, y, z);
                        if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion(was==null?"Add "+b.getName():"Replace "+was.getName()+" with "+b.getName(), new SetblockAction(x, y, z, b.newInstance(x, y, z)), priorities, b.template.displayTexture));
                    });
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
                int[] modCount = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block b = multiblock.getBlock(x, y, z);
                    if(b!=null&&b.isModerator())modCount[0]++;
                });
                suggestor.setCount(multiblock.getInternalVolume()*blocks.size()-modCount[0]);
                for(Block b : blocks){
                    multiblock.forEachInternalPosition((x, y, z) -> {
                        Block was = multiblock.getBlock(x, y, z);
                        if(was!=null&&was.isModerator())return;
                        if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion(was==null?"Add "+b.getName():"Replace "+was.getName()+" with "+b.getName(), new SetblockAction(x, y, z, b.newInstance(x, y, z)), priorities, b.template.displayTexture));
                    });
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
                int[] count = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block block = multiblock.getBlock(x, y, z);
                    if(block==null||block.canBeQuickReplaced()){
                        count[0]++;
                    }
                });
                suggestor.setCount(count[0]*blocks.size());
                multiblock.forEachInternalPosition((x, y, z) -> {
                    for(Block newBlock : blocks){
                        Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            if(newBlock.template.cooling>(block==null||!block.isActive()?0:block.template.cooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock.newInstance(x, y, z)), priorities, newBlock.template.displayTexture));
                            else suggestor.task.max--;
                        }
                    }
                });
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
                int[] count = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block block = multiblock.getBlock(x, y, z);
                    if(block==null||block.canBeQuickReplaced()){
                        count[0]++;
                    }
                });
                suggestor.setCount(count[0]*blocks.size());
                multiblock.forEachInternalPosition((x, y, z) -> {
                    for(Block newBlock : blocks){
                        Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            if(newBlock.template.cooling>(block==null||!block.isActive()?0:block.template.cooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock.newInstance(x, y, z)), priorities, newBlock.template.displayTexture));
                            else suggestor.task.max--;
                        }
                    }
                });
            }
        });
    }
    @Override
    public boolean canBePlacedInCasingEdge(Block b){
        return b.isController();
    }
    @Override
    public boolean canBePlacedInCasingFace(Block b){
        return b.isCasing();
    }
    @Override
    public boolean canBePlacedWithinCasing(Block b){
        return b.isFuelCell()||b.isModerator()||b.isCooler();
    }
    @Override
    public void buildDefaultCasing(){
        Block casing = null;
        Block controller = null;
        for(multiblock.configuration.underhaul.fissionsfr.Block template : getConfiguration().underhaul.fissionSFR.allBlocks){
            if(template.casing)casing = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.controller)controller = new Block(getConfiguration(), 0, 0, 0, template);
        }
        for(multiblock.configuration.underhaul.fissionsfr.Block template : Core.configuration.underhaul.fissionSFR.allBlocks){
            if(casing==null&&template.casing)casing = new Block(getConfiguration(), 0, 0, 0, template);
            if(controller==null&&template.controller)controller = new Block(getConfiguration(), 0, 0, 0, template);
        }
        final Block theCasing = casing;
        final Block theController = controller;
        boolean[] hasPlacedTheController = new boolean[1];
        for(Block block : getBlocks()){
            if(block.template.controller)hasPlacedTheController[0] = true;
        }
        forEachCasingFacePosition((x, y, z) -> {
            setBlock(x, y, z, theCasing);
        });
        forEachCasingEdgePosition((x, y, z) -> {
            if(hasPlacedTheController[0])return;
            setBlock(x, y, z, theController);
            hasPlacedTheController[0] = true;
        });
    }
}
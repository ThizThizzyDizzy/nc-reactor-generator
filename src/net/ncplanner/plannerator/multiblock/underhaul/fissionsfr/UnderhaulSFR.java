package net.ncplanner.plannerator.multiblock.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.PartCount;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.editor.action.SetblockAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.multiblock.editor.decal.AdjacentCellDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.AdjacentModeratorDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.BlockInvalidDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.BlockValidDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.MissingCasingDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.UnderhaulModeratorLineDecal;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.CompiledUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.LiteUnderhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.design.UnderhaulSFRDesign;
public class UnderhaulSFR extends CuboidalMultiblock<Block> {
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
    public UnderhaulSFR(NCPFConfigurationContainer configuration){
        this(configuration, configuration==null?0:configuration.getConfiguration(UnderhaulSFRConfiguration::new).boundSize(7), configuration==null?0:configuration.getConfiguration(UnderhaulSFRConfiguration::new).boundSize(5), configuration==null?0:configuration.getConfiguration(UnderhaulSFRConfiguration::new).boundSize(7), null);
    }
    public UnderhaulSFR(NCPFConfigurationContainer configuration, int x, int y, int z, Fuel fuel){
        super(configuration, x, y, z);
        this.fuel = fuel==null?(exists()?getSpecificConfiguration().fuels.get(0):null):fuel;
    }
    @Override
    public UnderhaulSFRConfiguration getSpecificConfiguration(){
        NCPFConfigurationContainer conf = getConfiguration();
        if(conf==null)return null;
        return conf.getConfiguration(UnderhaulSFRConfiguration::new);
    }
    @Override
    public String getDefinitionName(){
        return "Underhaul SFR";
    }
    @Override
    public UnderhaulSFR newInstance(NCPFConfigurationContainer configuration){
        return new UnderhaulSFR(configuration);
    }
    @Override
    public Multiblock<Block> newInstance(NCPFConfigurationContainer configuration, int x, int y, int z){
        return new UnderhaulSFR(configuration, x, y, z, null);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getSpecificConfiguration()==null)return;
        for(BlockElement block : getSpecificConfiguration().blocks){
            blocks.add(new Block(getConfiguration(),-1,-1,-1,block));
        }
    }
    @Override
    public int getMinX(){
        return getSpecificConfiguration().settings.minSize;
    }
    @Override
    public int getMinY(){
        return getSpecificConfiguration().settings.minSize;
    }
    @Override
    public int getMinZ(){
        return getSpecificConfiguration().settings.minSize;
    }
    @Override
    public int getMaxX(){
        return getSpecificConfiguration().settings.maxSize;
    }
    @Override
    public int getMaxY(){
        return getSpecificConfiguration().settings.maxSize;
    }
    @Override
    public int getMaxZ(){
        return getSpecificConfiguration().settings.maxSize;
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
                    if((block.isCooler()||block.template.activeCooler!=null)&&block.isActive())cooling+=block.getCooling();
                    calcStats.progress = i/(double)allBlocks.size();
                }
                this.heatMult = totalHeatMult/cells;
                if(Double.isNaN(heatMult))heatMult = 0;
                heat = (int) (totalHeatMult*fuel.stats.heat);
                netHeat = heat-cooling;
                power = (int) (totalEnergyMult*fuel.stats.power);
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
        if(that.template.fuelCell==null)return;
        if(addDecals)decals.enqueue(new BlockValidDecal(that.x, that.y, that.z));
        for(Direction d : Direction.values()){
            Queue<Block> toValidate = new Queue<>();
            for(int i = 1; i<=getSpecificConfiguration().settings.neutronReach+1; i++){
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
        that.energyMult+=baseEff/6*getSpecificConfiguration().settings.moderatorExtraPower*that.adjacentModerators;
        that.heatMult+=baseEff/6*getSpecificConfiguration().settings.moderatorExtraHeat*that.adjacentModerators;
    }
    /**
     * Calculates the cooler
     * @param block the block to calculate
     * @param addDecals whether or not to add decals
     * @return <code>true</code> if the cooler state has changed
     */
    public boolean calculateCooler(Block block, boolean addDecals){
        if(block.template.cooler==null&&block.template.activeCooler==null)return false;
        boolean wasValid = block.coolerValid;
        if(block.template.activeCooler!=null&&block.recipe==null){
            if(block.coolerValid&&addDecals)decals.enqueue(new BlockInvalidDecal(block.x,block.y,block.z));
            block.coolerValid = false;
            return wasValid!=block.coolerValid;
        }
        for(NCPFPlacementRule rule : block.getRules()){
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
                + "Efficiency: "+MathUtil.percent(efficiency, 0)+"\n"
                + "Heat multiplier: "+MathUtil.percent(heatMult, 0)+"\n"
                + (cells>0?"Fuel burn time: "+fuel.stats.time/cells+"\n":"")
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
    public boolean validate(){
        return false;
    }
    private boolean isValid(){
        return power>0;
    }
    @Override
    public UnderhaulSFR blankCopy(){
        return new UnderhaulSFR(configuration, getInternalWidth(), getInternalHeight(), getInternalDepth(), fuel);
    }
    @Override
    public UnderhaulSFR doCopy(){
        UnderhaulSFR copy = blankCopy();
        forEachPosition((x, y, z) -> {
            copy.setBlock(x, y, z, getBlock(x, y, z));
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
    public String getGeneralName(){
        return "Reactor";
    }
    @Override
    public boolean isCompatible(Multiblock<Block> other){
        return ((UnderhaulSFR)other).fuel==fuel;
    }
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
                            actions.add(new SetblockAction(x, y, z, cell));
                            SetblocksAction multi = new SetblocksAction(moderator);
                            DIRECTION:for(Direction d : Direction.values()){
                                ArrayList<int[]> toSet = new ArrayList<>();
                                boolean yep = false;
                                for(int i = 1; i<=getSpecificConfiguration().settings.neutronReach+1; i++){
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
                                    if(i<=getSpecificConfiguration().settings.neutronReach){
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
                            if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion("Add "+cell.getName()+(multi.isEmpty()?"":" with "+moderator.getName()), actions, priorities, cell.getTexture(), moderator.getTexture()));
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
                        if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion(was==null?"Add "+b.getName():"Replace "+was.getName()+" with "+b.getName(), new SetblockAction(x, y, z, b), priorities, b.getTexture()));
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
                        if(suggestor.acceptingSuggestions())suggestor.suggest(new Suggestion(was==null?"Add "+b.getName():"Replace "+was.getName()+" with "+b.getName(), new SetblockAction(x, y, z, b), priorities, b.getTexture()));
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
                    if(b.template.cooler==null)it.remove();
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
                            if(newBlock.template.cooler.cooling>(block==null||!block.isActive()?0:block.template.cooler.cooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock), priorities, newBlock.getTexture()));
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
                    if(b.template.activeCooler==null)it.remove();
                }
                ArrayList<Block> blocksWithRecipes = new ArrayList<>();
                for(Block b : blocks){
                    for(ActiveCoolerRecipe recipe : b.template.activeCoolerRecipes){
                        Block block = (Block)b.newInstance(b.x, b.y, b.z);
                        block.recipe = recipe;
                        blocksWithRecipes.add(block);
                    }
                }
                int[] count = new int[1];
                multiblock.forEachInternalPosition((x, y, z) -> {
                    Block block = multiblock.getBlock(x, y, z);
                    if(block==null||block.canBeQuickReplaced()){
                        count[0]++;
                    }
                });
                suggestor.setCount(count[0]*blocksWithRecipes.size());
                multiblock.forEachInternalPosition((x, y, z) -> {
                    for(Block newBlock : blocksWithRecipes){
                        Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            if(newBlock.recipe.stats.cooling>(block==null||!block.isActive()?0:block.recipe.stats.cooling)&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock), priorities, newBlock.getTexture()));
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
        return b.isFuelCell()||b.isModerator()||b.isCooler()||b.template.activeCooler!=null;
    }
    @Override
    public void buildDefaultCasing(){
        Block casing = null;
        Block controller = null;
        for(BlockElement template : getSpecificConfiguration().blocks){
            if(template.casing!=null)casing = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.controller!=null)controller = new Block(getConfiguration(), 0, 0, 0, template);
        }
        for(BlockElement template : Core.project.getConfiguration(UnderhaulSFRConfiguration::new).blocks){
            if(casing==null&&template.casing!=null)casing = new Block(getConfiguration(), 0, 0, 0, template);
            if(controller==null&&template.controller!=null)controller = new Block(getConfiguration(), 0, 0, 0, template);
        }
        final Block theCasing = casing;
        final Block theController = controller;
        boolean[] hasPlacedTheController = new boolean[1];
        for(Block block : getBlocks()){
            if(block.template.controller!=null)hasPlacedTheController[0] = true;
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
    @Override
    public String getPreviewTexture(){
        return "multiblocks/underhaul_sfr";
    }
    @Override
    public LiteUnderhaulSFR compile(){
        LiteUnderhaulSFR sfr = new LiteUnderhaulSFR(CompiledUnderhaulSFRConfiguration.compile(getSpecificConfiguration()));
        sfr.importAndConvert(this);
        return sfr;
    }
    @Override
    public UnderhaulSFRDesign convertToDesign(){
        UnderhaulSFRDesign design = new UnderhaulSFRDesign(Core.project, x, y, z);
        forEachPosition((x, y, z) -> {
            Block block = getBlock(x, y, z);
            design.design[x][y][z] = block==null?null:block.template;
            design.recipes[x][y][z] = block==null?null:block.recipe;
        });
        design.fuel = fuel;
        return design;
    }
    @Override
    public NCPFElement[] getMultiblockRecipes(){
        return new NCPFElement[]{fuel};
    }
    @Override
    public void setMultiblockRecipe(int recipeType, NCPFElement recipe){
        fuel = (Fuel)recipe;
    }
}
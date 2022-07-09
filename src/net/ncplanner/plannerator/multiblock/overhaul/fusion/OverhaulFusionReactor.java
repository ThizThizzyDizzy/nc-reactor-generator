package net.ncplanner.plannerator.multiblock.overhaul.fusion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigNumberList;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockGrid;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.FluidStack;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.PartCount;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.CoolantRecipe;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.SetblockAction;
import net.ncplanner.plannerator.multiblock.editor.ppe.ClearInvalid;
import net.ncplanner.plannerator.multiblock.editor.ppe.FusionFill;
import net.ncplanner.plannerator.multiblock.editor.ppe.PostProcessingEffect;
import net.ncplanner.plannerator.multiblock.editor.ppe.SmartFillOverhaulFusion;
import net.ncplanner.plannerator.multiblock.editor.symmetry.AxialSymmetry;
import net.ncplanner.plannerator.multiblock.editor.symmetry.Symmetry;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import net.ncplanner.plannerator.planner.gui.menu.MenuResizeFusion;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentEditorGrid;
import net.ncplanner.plannerator.planner.gui.menu.component.generator.MenuComponentFusionToggleBlockRecipe;
import net.ncplanner.plannerator.planner.module.Module;
import net.ncplanner.plannerator.planner.vr.VRGUI;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuResizeFusion;
public class OverhaulFusionReactor extends Multiblock<Block> {
    public CoolantRecipe coolantRecipe;
    public int innerRadius;
    public int coreSize;
    public int toroidWidth;
    public int liningThickness;
    public Recipe recipe;
    public ArrayList<Cluster> clusters = new ArrayList<>();
    public int totalHeatingBlankets;
    public int rawOutput;
    public float totalOutput;
    public int totalCooling;
    public int totalHeat;
    public int netHeat;
    public float totalEfficiency;
    public float totalHeatMult;
    public int functionalBlocks;
    private float sparsityMult;
    private float shieldinessFactor;
    private static int getWidth(int innerRadius, int coreSize, int toroidWidth, int liningThickness){
        return coreSize+2+innerRadius*2+toroidWidth*2+liningThickness*4+4;
    }
    private static int getHeight(int innerRadius, int coreSize, int toroidWidth, int liningThickness){
        return liningThickness*2+coreSize+2;
    }
    private int getPlasmaSurfaceArea(){
        int corner = toroidWidth*toroidWidth*2+toroidWidth*2;
        int edgeLength = liningThickness*2+coreSize+innerRadius*2+4;
        int edgeTop = toroidWidth*edgeLength;
        int edgeEnd = coreSize*edgeLength;
        int edge = edgeTop*2+edgeEnd*2;
        return corner*4+edge*4;
    }
    public OverhaulFusionReactor(){
        this(null);
    }
    public OverhaulFusionReactor(Configuration configuration){
        this(configuration, 1, 1, 1, 1, null, null);
    }
    public OverhaulFusionReactor(Configuration configuration, int innerRadius, int coreSize, int toroidWidth, int liningThickness, Recipe recipe, CoolantRecipe coolantRecipe){
        super(configuration, innerRadius, coreSize, toroidWidth, liningThickness);
        this.recipe = recipe==null?(exists()?getConfiguration().overhaul.fusion.allRecipes.get(0):null):recipe;
        this.coolantRecipe = coolantRecipe==null?(exists()?getConfiguration().overhaul.fusion.allCoolantRecipes.get(0):null):coolantRecipe;
        this.innerRadius = innerRadius;
        this.coreSize = coreSize;
        this.toroidWidth = toroidWidth;
        this.liningThickness = liningThickness;
        fillRequiredSpaces();
    }
    @Override
    protected void createBlockGrids(){
        int width = getWidth(dimensions[0], dimensions[1], dimensions[2], dimensions[3]);
        int height = getHeight(dimensions[0], dimensions[1], dimensions[2], dimensions[3]);
        blockGrids.add(new BlockGrid(0, 0, 0, width-1, height-1, width-1));
    }
    @Override
    public void getEditorSpaces(ArrayList<EditorSpace<Block>> editorSpaces){
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        editorSpaces.add(new EditorSpace<Block>(0, 0, 0, width-1, height-1, width-1){
            @Override
            public boolean isSpaceValid(Block block, int x, int y, int z){
                return isLocationValid(block, x, y, z);
            }
            @Override
            public void createComponents(MenuEdit editor, ArrayList<Component> comps, int cellSize){
                for(int y = 0; y<height; y++){
                    comps.add(new MenuComponentEditorGrid(0, 0, cellSize, editor, OverhaulFusionReactor.this, this, 0, 0, width-1, width-1, Axis.Y, y));
                }
            }
        });
    }
    private void fillRequiredSpaces(){
        Block core = null, connector = null, electromagnet = null, heatingBlanket = null, breedingBlanket = null;
        ArrayList<Block> availableBlocks = new ArrayList<>();
        getAvailableBlocks(availableBlocks);
        for(Block b : availableBlocks){
            if(core==null&&b.template.core)core = b;
            if(connector==null&&b.template.connector)connector = b;
            if(electromagnet==null&&b.template.electromagnet)electromagnet = b;
            if(heatingBlanket==null&&b.template.heatingBlanket)heatingBlanket = b;
            if(breedingBlanket==null&&b.template.breedingBlanket)breedingBlanket = b;
        }
        final Block cor = core;
        final Block con = connector;
        final Block elec = electromagnet;
        forEachPosition((x, y, z) -> {
            switch(getLocationCategory(x, y, z)){
                case CORE:
                    setBlock(x, y, z, cor);
                case CONNECTOR:
                    setBlock(x, y, z, con);
                    break;
                case POLOID:
                case TOROID:
                    setBlock(x, y, z, elec);
                    break;
                case PLASMA:
                case NONE:
                case INTERIOR:
                case EXTERIOR:
                    break;
            }
        });
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul Fusion Reactor";
    }
    @Override
    public OverhaulFusionReactor newInstance(Configuration configuration){
        return new OverhaulFusionReactor(configuration);
    }
    @Override
    public Multiblock<Block> newInstance(Configuration configuration, int... dimensions){
        return new OverhaulFusionReactor(configuration, dimensions[0], dimensions[1], dimensions[2], dimensions[3], null, null);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getConfiguration()==null||getConfiguration().overhaul==null||getConfiguration().overhaul.fusion==null)return;
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block block : getConfiguration().overhaul.fusion.allBlocks){
            blocks.add(new Block(getConfiguration(), -1, -1, -1, block));
        }
    }
    @Override
    public void genCalcSubtasks(){}
    @Override
    public boolean doCalculationStep(List<Block> blocks, boolean addDecals){
        Task calcBreeding = calculateTask.addSubtask(new Task("Calculating Breeding Blankets"));
        Task calcHeating = calculateTask.addSubtask(new Task("Calculating Breeding Blankets"));
        Task calcHeatsinks = calculateTask.addSubtask(new Task("Calculating Heatsinks"));
        Task buildClusters = calculateTask.addSubtask(new Task("Building Clusters"));
        Task calcClusters = calculateTask.addSubtask(new Task("Calculating Clusters"));
        Task calcStats = calculateTask.addSubtask(new Task("Calculating Stats"));
        List<Block> allBlocks = getBlocks();
        for(int i = 0; i<blocks.size(); i++){
            blocks.get(i).calculateBreedingBlanket(this);
            calcBreeding.progress = i/(double)blocks.size();
        }
        calcBreeding.finish();
        for(int i = 0; i<blocks.size(); i++){
            blocks.get(i).calculateHeatingBlanket(this);
            calcHeating.progress = i/(double)blocks.size();
        }
        calcHeating.finish();
        boolean somethingChanged;
        int n = 0;
        do{
            somethingChanged = false;
            n++;
            calcHeatsinks.name = "Calculating Heatsinks"+(n>1?" ("+n+")":"");
            for(int i = 0; i<blocks.size(); i++){
                if(blocks.get(i).calculateHeatsink(this))somethingChanged = true;
                calcHeatsinks.progress = i/(double)blocks.size();
            }
        }while(somethingChanged);
        calcHeatsinks.finish();
        clusters.clear();
        shieldinessFactor = 0;
        for(int i = 0; i<allBlocks.size(); i++){
            Block block = allBlocks.get(i);//detect clusters and shieldniness too
            if(block.isShielding()){
                if(block.template.shieldingHasBaseStats||block.recipe!=null){
                    shieldinessFactor+=block.recipe==null?block.template.shieldingShieldiness:block.recipe.shieldingShieldiness;
                }
            }
            Cluster cluster = getCluster(block);
            if(cluster==null)continue;//that's not a cluster!
            synchronized(clusters){
                if(clusters.contains(cluster))continue;//already know about that one!
                clusters.add(cluster);
            }
            buildClusters.progress = i/(double)allBlocks.size();
        }
        buildClusters.finish();
        shieldinessFactor/=getPlasmaSurfaceArea();
        synchronized(clusters){
            for(int i = 0; i<clusters.size(); i++){
                Cluster cluster = clusters.get(i);
                int heatingBlankets = 0;
                for(int j = 0; j<cluster.blocks.size(); j++){
                    Block b = cluster.blocks.get(j);
                    if(b.isHeatingBlanketActive()){
                        heatingBlankets++;
                        cluster.totalOutput += recipe.heat*b.efficiency;
                        cluster.efficiency+=b.efficiency;
                        cluster.totalHeat+=recipe.heat*b.heatMult;
                        cluster.heatMult+=b.heatMult;
                    }
                    if(b.isHeatsinkActive()){
                        cluster.totalCooling+=b.recipe==null?b.template.heatsinkCooling:b.recipe.heatsinkCooling;
                    }
                    calcClusters.progress = (i+j/(double)cluster.blocks.size())/(double)clusters.size();
                }
                cluster.efficiency/=heatingBlankets;
                cluster.heatMult/=heatingBlankets;
                if(Double.isNaN(cluster.efficiency))cluster.efficiency = 0;
                if(Double.isNaN(cluster.heatMult))cluster.heatMult = 0;
                cluster.netHeat = cluster.totalHeat-cluster.totalCooling;
                if(cluster.totalCooling==0)cluster.coolingPenaltyMult = 1;
                else cluster.coolingPenaltyMult = Math.min(1, (cluster.totalHeat+getConfiguration().overhaul.fusion.coolingEfficiencyLeniency)/(float)cluster.totalCooling);
                cluster.efficiency*=cluster.coolingPenaltyMult;
                cluster.totalOutput*=cluster.coolingPenaltyMult;
                totalHeatingBlankets+=heatingBlankets;
                rawOutput+=cluster.totalOutput;
                totalOutput+=cluster.totalOutput;
                totalCooling+=cluster.totalCooling;
                totalHeat+=cluster.totalHeat;
                netHeat+=cluster.netHeat;
                totalEfficiency+=cluster.efficiency*heatingBlankets;
                totalHeatMult+=cluster.heatMult*heatingBlankets;
                calcClusters.progress = (i+1)/(double)clusters.size();
            }
        }
        calcClusters.finish();
        totalEfficiency/=totalHeatingBlankets;
        totalHeatMult/=totalHeatingBlankets;
        if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
        if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
        functionalBlocks = 0;
        for(Block block : allBlocks){
            LocationCategory cat = getLocationCategory(block.x, block.y, block.z);
            if(cat==LocationCategory.INTERIOR||cat==LocationCategory.EXTERIOR){
                if(block.isFunctional())functionalBlocks++;
            }
        }
        int[] volume = new int[1];
        forEachPosition((x, y, z) -> {
            LocationCategory cat = getLocationCategory(x, y, z);
            if(cat==LocationCategory.INTERIOR||cat==LocationCategory.EXTERIOR)volume[0]++;
        });
        sparsityMult = (float) (functionalBlocks/(float)volume[0]>=getConfiguration().overhaul.fusion.sparsityPenaltyThreshold?1:getConfiguration().overhaul.fusion.sparsityPenaltyMult+(1-getConfiguration().overhaul.fusion.sparsityPenaltyMult)*Math.sin(Math.PI*functionalBlocks/(2*volume[0]*getConfiguration().overhaul.fusion.sparsityPenaltyThreshold)));
        totalOutput*=sparsityMult;
        totalEfficiency*=sparsityMult;
        totalOutput/=coolantRecipe.heat/coolantRecipe.outputRatio;
        calcStats.finish();
        return false;
    }
    public FormattedText getTooltip(boolean full){
        if(this.showDetails!=null)full = this.showDetails;
        synchronized(clusters){
            int validClusters = 0;
            for(Cluster c : clusters){
                if(c.isValid())validClusters++;
            }
            FormattedText text = new FormattedText("Total output: "+totalOutput+" mb/t of "+coolantRecipe.getOutputDisplayName()+"\n"
                    + "Total Heat: "+totalHeat+"H/t\n"
                    + "Total Cooling: "+totalCooling+"H/t\n"
                    + "Net Heat: "+netHeat+"H/t\n"
                    + "Overall Efficiency: "+MathUtil.percent(totalEfficiency, 0)+"\n"
                    + "Overall Heat Multiplier: "+MathUtil.percent(totalHeatMult, 0)+"\n"
                    + "Sparsity Penalty Multiplier: "+Math.round(sparsityMult*10000)/10000d+"\n"
                    + "Shieldiness Factor: "+MathUtil.percent(shieldinessFactor, 1)+"\n"
                    + "Clusters: "+(validClusters==clusters.size()?clusters.size():(validClusters+"/"+clusters.size())));
            text.addText(getModuleTooltip()+"\n");
            if(full){
                HashMap<String, Integer> counts = new HashMap<>();
                HashMap<String, Color> colors = new HashMap<>();
                ArrayList<String> order = new ArrayList<>();
                for(Cluster c : clusters){
                    String str = c.getTooltip();
                    if(counts.containsKey(str)){
                        counts.put(str, counts.get(str)+1);
                    }else{
                        counts.put(str, 1);
                        order.add(str);
                    }
                    if(!c.isCreated()){
                        colors.put(str, Core.theme.getClusterInvalidColor());
                    }else if(!c.isConnectedToWall){
                        colors.put(str, Core.theme.getClusterDisconnectedColor());
                    }else if(c.netHeat>0)colors.put(str, Core.theme.getClusterOverheatingColor());
                    else if(c.coolingPenaltyMult!=1)colors.put(str, Core.theme.getClusterOvercoolingColor());
                }
                for(String str : order){
                    int count = counts.get(str);
                    String s;
                    if(count==1)s="\n\n"+str;
                    else{
                        s="\n\n"+count+" similar clusters:\n\n"+str;
                    }
                    text.addText(s, colors.get(str));
                }
            }
            return text;
        }
    }
    @Override
    public int getMultiblockID(){
        return 4;
    }
    @Override
    protected void save(NCPFFile ncpf, Configuration configuration, Config config){
        ConfigNumberList size = new ConfigNumberList();
        size.add(innerRadius);
        size.add(coreSize);
        size.add(toroidWidth);
        size.add(liningThickness);
        config.set("size", size);
        ConfigNumberList blox = new ConfigNumberList();
        forEachPosition((x, y, z) -> {
            Block block = getBlock(x, y, z);
            if(block==null)blox.add(0);
            else blox.add(configuration.overhaul.fusion.allBlocks.indexOf(block.template)+1);
            System.out.print((block==null?"0":(configuration.overhaul.fusion.allBlocks.indexOf(block.template)+1))+" ");
        });
        System.out.println(blox.size());
        ConfigNumberList blockRecipes = new ConfigNumberList();
        for(Block block : getBlocks()){
            if(block.template.allRecipes.isEmpty())continue;
            blockRecipes.add(block.template.allRecipes.indexOf(block.recipe)+1);
        }
        config.set("blocks", blox);
        config.set("blockRecipes", blockRecipes);
        config.set("recipe", (byte)configuration.overhaul.fusion.allRecipes.indexOf(recipe));
        config.set("coolantRecipe", (byte)configuration.overhaul.fusion.allCoolantRecipes.indexOf(coolantRecipe));
    }
    @Override
    public void convertTo(Configuration to) throws MissingConfigurationEntryException{
        if(to.overhaul==null||to.overhaul.fusion==null)return;
        for(Block block : getBlocks()){
            block.convertTo(to);
        }
        recipe = to.overhaul.fusion.convert(recipe);
        coolantRecipe = to.overhaul.fusion.convert(coolantRecipe);
        configuration = to;
    }
    @Override
    public boolean validate(){
        return false;
    }
    public Cluster getCluster(Block block){
        if(block==null)return null;
        if(!block.canCluster())return null;
        synchronized(clusters){
            for(Cluster cluster : clusters){
                if(cluster.contains(block))return cluster;
            }
        }
        return new Cluster(block);
    }
    private HashMap<BlockRecipe, MenuComponentFusionToggleBlockRecipe> blockRecipeToggles;
    @Override
    public void addGeneratorSettings(SingleColumnList multiblockSettings){
        if(blockRecipeToggles==null)blockRecipeToggles = new HashMap<>();
        blockRecipeToggles.clear();
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block block : getConfiguration().overhaul.fusion.allBlocks){
            for(BlockRecipe recipe : block.allRecipes){
                MenuComponentFusionToggleBlockRecipe toggle = new MenuComponentFusionToggleBlockRecipe(recipe);
                blockRecipeToggles.put(recipe, toggle);
                multiblockSettings.add(toggle);
            }
        }
    }
    private boolean isValid(){
        return totalOutput>0;
    }
    @Override
    public void getGenerationPriorities(ArrayList<Priority> priorities){
        priorities.add(new Priority<OverhaulFusionReactor>("Valid (>0 output)", true, true){
            @Override
            protected double doCompare(OverhaulFusionReactor main, OverhaulFusionReactor other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                return 0;
            }
        });
        priorities.add(new Priority<OverhaulFusionReactor>("Stability", false, true){
            @Override
            protected double doCompare(OverhaulFusionReactor main, OverhaulFusionReactor other){
                return Math.max(0, other.netHeat)-Math.max(0, main.netHeat);
            }
        });
        priorities.add(new Priority<OverhaulFusionReactor>("Efficiency", true, true){
            @Override
            protected double doCompare(OverhaulFusionReactor main, OverhaulFusionReactor other){
                return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
            }
        });
        priorities.add(new Priority<OverhaulFusionReactor>("Output", true, true){
            @Override
            protected double doCompare(OverhaulFusionReactor main, OverhaulFusionReactor other){
                return main.totalOutput-other.totalOutput;
            }
        });
        for(Module m : Core.modules){
            if(m.isActive())m.getGenerationPriorities(this, priorities);
        }
    }
    @Override
    public void getGenerationPriorityPresets(ArrayList<Priority> priorities, ArrayList<Priority.Preset> presets){
        presets.add(new Priority.Preset("Efficiency", priorities.get(0), priorities.get(1), priorities.get(2), priorities.get(3)).addAlternative("Efficient"));
        presets.add(new Priority.Preset("Output", priorities.get(0), priorities.get(1), priorities.get(3), priorities.get(2)).addAlternative("Power"));
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
        postProcessingEffects.add(new SmartFillOverhaulFusion());
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b : getConfiguration().overhaul.fusion.allBlocks){
            if(b.conductor||(b.cluster&&!b.functional))postProcessingEffects.add(new FusionFill(b));
        }
    }
    public boolean isPoloidal(Block b){
        return b!=null&&getLocationCategory(b.x, b.y, b.z)==LocationCategory.POLOID;
    }
    public boolean isToroidal(Block b){
        return b!=null&&getLocationCategory(b.x, b.y, b.z)==LocationCategory.TOROID;
    }
    public void increaseInnerRadius(){
        if(innerRadius>=getConfiguration().overhaul.fusion.maxInnerRadius)return;
        innerRadius++;
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        blockGrids.clear();
        blockGrids.add(new BlockGrid(0, 0, 0, width-1, height-1, width-1));
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
        clearCaches();
    }
    public void increaseCoreSize(){
        if(coreSize>=getConfiguration().overhaul.fusion.maxCoreSize)return;
        coreSize++;
        blockGrids.clear();
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        blockGrids.add(new BlockGrid(0, 0, 0, width-1, height-1, width-1));
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
        clearCaches();
    }
    public void increaseToroidWidth(){
        if(toroidWidth>=getConfiguration().overhaul.fusion.maxToroidWidth)return;
        toroidWidth++;
        blockGrids.clear();
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        blockGrids.add(new BlockGrid(0, 0, 0, width-1, height-1, width-1));
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
        clearCaches();
    }
    public void increaseLiningThickness(){
        if(liningThickness>=getConfiguration().overhaul.fusion.maxLiningThickness)return;
        liningThickness++;
        blockGrids.clear();
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        blockGrids.add(new BlockGrid(0, 0, 0, width-1, height-1, width-1));
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
        clearCaches();
    }
    public void decreaseInnerRadius(){
        if(innerRadius<=getConfiguration().overhaul.fusion.minInnerRadius)return;
        innerRadius--;
        blockGrids.clear();
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        blockGrids.add(new BlockGrid(0, 0, 0, width-1, height-1, width-1));
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
        clearCaches();
    }
    public void decreaseCoreSize(){
        if(coreSize<=getConfiguration().overhaul.fusion.minCoreSize)return;
        coreSize--;
        blockGrids.clear();
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        blockGrids.add(new BlockGrid(0, 0, 0, width-1, height-1, width-1));
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
        clearCaches();
    }
    public void decreaseToroidWidth(){
        if(toroidWidth<=getConfiguration().overhaul.fusion.minToroidWidth)return;
        toroidWidth--;
        blockGrids.clear();
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        blockGrids.add(new BlockGrid(0, 0, 0, width-1, height-1, width-1));
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
        clearCaches();
    }
    public void decreaseLiningThickness(){
        if(liningThickness<=getConfiguration().overhaul.fusion.minLiningThickness)return;
        liningThickness--;
        blockGrids.clear();
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        blockGrids.add(new BlockGrid(0, 0, 0, width-1, height-1, width-1));
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
        clearCaches();
    }
    public class Cluster{
        public ArrayList<Block> blocks = new ArrayList<>();
        public boolean isConnectedToWall = false;
        public float totalOutput = 0;
        public float efficiency;
        public int totalHeat, totalCooling, netHeat;
        public float heatMult, coolingPenaltyMult;
        public Cluster(Block block){
            blocks.addAll(toList(getClusterBlocks(block, false)));
            isConnectedToWall = wallCheck(blocks);
            if(!isConnectedToWall){
                isConnectedToWall = wallCheck(toList(getClusterBlocks(block, true)));
            }
            for(Block b : blocks){
                b.cluster = this;
            }
        }
        private Cluster(){}
        private boolean isValid(){
            return isConnectedToWall&&isCreated();
        }
        public boolean isCreated(){
            for(Block block : blocks){
                if(block.template.createCluster)return true;
            }
            return false;
        }
        public boolean contains(Block block){
            return blocks.contains(block);
        }
        public boolean contains(int x, int y, int z){
            for(Block b : blocks){
                if(b.x==x&&b.y==y&&b.z==z)return true;
            }
            return false;
        }
        private boolean wallCheck(ArrayList<Block> blocks){
            for(Block block : blocks){
                for(Direction d : Direction.values()){
                    Block b = getBlock(block.x+d.x, block.y+d.y, block.z+d.z);
                    if(b!=null&&b.isConnector())return true;
                }
            }
            return false;
        }
        public String getTooltip(){
            if(!isCreated())return "Invalid cluster!";
            if(!isValid())return "Cluster is not connected to a connector!";
            return "Total output: "+Math.round(totalOutput)+"\n"
                + "Efficiency: "+MathUtil.percent(efficiency, 0)+"\n"
                + "Total Heating: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heating: "+netHeat+"H/t\n"
                + "Heat Multiplier: "+MathUtil.percent(heatMult, 0)+"\n"
                + "Cooling penalty mult: "+Math.round(coolingPenaltyMult*10000)/10000d;
        }
        private Cluster copy(OverhaulFusionReactor newReactor){
            Cluster copy = new Cluster();
            for(Block b : blocks){
                copy.blocks.add(newReactor.getBlock(b.x, b.y, b.z));
            }
            copy.isConnectedToWall = isConnectedToWall;
            copy.totalOutput = totalOutput;
            copy.efficiency = efficiency;
            copy.totalHeat = totalHeat;
            copy.totalCooling = totalCooling;
            copy.netHeat = netHeat;
            copy.heatMult = heatMult;
            copy.coolingPenaltyMult = coolingPenaltyMult;
            return copy;
        }
    }
    @Override
    public void clearData(List<Block> blocks){
        super.clearData(blocks);
        synchronized(clusters){
            clusters.clear();
        }
        shieldinessFactor = totalOutput = totalEfficiency = totalHeatMult = sparsityMult = totalHeatingBlankets = rawOutput = totalCooling = totalHeat = netHeat = functionalBlocks = 0;
    }
    /**
     * Block search algorithm from my Tree Feller for Bukkit.
     */
    private HashMap<Integer, ArrayList<Block>> getClusterBlocks(Block start, boolean useConductors){
        //layer zero
        HashMap<Integer, ArrayList<Block>>results = new HashMap<>();
        ArrayList<Block> zero = new ArrayList<>();
        if(start.canCluster()||(useConductors&&start.isConductor())){
            zero.add(start);
        }
        results.put(0, zero);
        //all the other layers
        int maxDistance = getWidth(innerRadius, coreSize, toroidWidth, liningThickness)*getHeight(innerRadius, coreSize, toroidWidth, liningThickness);//the algorithm requires a max search distance. Rather than changing that, I'll just be lazy and give it a big enough number
        for(int i = 0; i<maxDistance; i++){
            ArrayList<Block> layer = new ArrayList<>();
            ArrayList<Block> lastLayer = new ArrayList<>(results.get(i));
            if(i==0&&lastLayer.isEmpty()){
                lastLayer.add(start);
            }
            for(Block block : lastLayer){
                FOR:for(int j = 0; j<6; j++){
                    int dx=0,dy=0,dz=0;
                    switch(j){//This is a primitive version of the Direction class used in other places here, but I'll just leave it as it is
                        case 0:
                            dx = -1;
                            break;
                        case 1:
                            dx = 1;
                            break;
                        case 2:
                            dy = -1;
                            break;
                        case 3:
                            dy = 1;
                            break;
                        case 4:
                            dz = -1;
                            break;
                        case 5:
                            dz = 1;
                            break;
                        default:
                            throw new IllegalArgumentException("How did this happen?");
                    }
                    if(!contains(block.x+dx, block.y+dy, block.z+dz))continue;
                    Block newBlock = getBlock(block.x+dx,block.y+dy,block.z+dz);
                    if(newBlock==null)continue;
                    if(!(newBlock.canCluster()||(useConductors&&newBlock.isConductor()))){//that's not part of this bunch
                        continue;
                    }
                    for(Block oldbl : lastLayer){//if(lastLayer.contains(newBlock))continue;//if the new block is on the same layer, ignore
                        if(oldbl==newBlock){
                            continue FOR;
                        }
                    }
                    if(i>0){
                        for(Block oldbl : results.get(i-1)){//if(i>0&&results.get(i-1).contains(newBlock))continue;//if the new block is on the previous layer, ignore
                            if(oldbl==newBlock){
                                continue FOR;
                            }
                        }
                    }
                    for(Block oldbl : layer){//if(layer.contains(newBlock))continue;//if the new block is on the next layer, but already processed, ignore
                        if(oldbl==newBlock){
                            continue FOR;
                        }
                    }
                    layer.add(newBlock);
                }
            }
            if(layer.isEmpty())break;
            results.put(i+1, layer);
        }
        return results;
    }
    /**
     * Converts the tiered search returned by getBlocks into a list of blocks.<br>
     * Also from my tree feller
     */
    private static ArrayList<Block> toList(HashMap<Integer, ArrayList<Block>> blocks){
        ArrayList<Block> list = new ArrayList<>();
        for(int i : blocks.keySet()){
            list.addAll(blocks.get(i));
        }
        return list;
    }
    @Override
    public boolean exists(){
        return super.exists()&&getConfiguration().overhaul!=null&&getConfiguration().overhaul.fusion!=null;
    }
    @Override
    public OverhaulFusionReactor blankCopy(){
        return new OverhaulFusionReactor(configuration, innerRadius, coreSize, toroidWidth, liningThickness, recipe, coolantRecipe);
    }
    @Override
    public OverhaulFusionReactor doCopy(){
        OverhaulFusionReactor copy = blankCopy();
        forEachPosition((x, y, z) -> {
            copy.setBlock(x, y, z, getBlock(x, y, z));
        });
        synchronized(clusters){
            for(Cluster cluster : clusters){
                copy.clusters.add(cluster.copy(copy));
            }
        }
        copy.totalHeatingBlankets = totalHeatingBlankets;
        copy.rawOutput = rawOutput;
        copy.totalOutput = totalOutput;
        copy.totalCooling = totalCooling;
        copy.totalHeat = totalHeat;
        copy.netHeat = netHeat;
        copy.totalEfficiency = totalEfficiency;
        copy.totalHeatMult = totalHeatMult;
        copy.functionalBlocks = functionalBlocks;
        copy.sparsityMult = sparsityMult;
        return copy;
    }
    @Override
    protected int doCount(Object o){
        int[] count = new int[1];
        if(o instanceof BlockRecipe){
            BlockRecipe r = (BlockRecipe)o;
            forEachPosition((x, y, z) -> {
                Block b = getBlock(x, y, z);
                if(b==null)return;
                if(b.recipe==r)count[0]++;
            });
            return count[0];
        }
        throw new IllegalArgumentException("Cannot count "+o.getClass().getName()+" in "+getDefinitionName()+"!");
    }
    @Override
    public String getGeneralName(){
        return "Fusion Reactor";
    }
    @Override
    public boolean isCompatible(Multiblock<Block> other){
        return ((OverhaulFusionReactor)other).recipe==recipe&&((OverhaulFusionReactor)other).coolantRecipe==coolantRecipe;
    }
    @Override
    protected void getFluidOutputs(ArrayList<FluidStack> outputs){
        outputs.add(new FluidStack(coolantRecipe.outputName, coolantRecipe.outputDisplayName, totalOutput));
    }
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){}
    @Override
    public String getDescriptionTooltip(){
        return "A fusion reactor for Nuclearcraft: Overhauled\nTHESE DO NOT EXIST INGAME!\nThis is just a prototype, and is VERY different from what they will be in-game";
    }
    public LocationCategory getLocationCategory(int x, int y, int z){
        //old; make and use local variables >.>
        int width = getWidth(innerRadius, coreSize, toroidWidth, liningThickness);
        int height = getHeight(innerRadius, coreSize, toroidWidth, liningThickness);
        int coreMinX = (width-coreSize)/2;
        int coreMaxX = (width+coreSize)/2-1;
        int coreMinY = (height-coreSize)/2;
        int coreMaxY = (height+coreSize)/2-1;
        int coreMinZ = (width-coreSize)/2;
        int coreMaxZ = (width+coreSize)/2-1;
        if(x>=coreMinX-1&&y>=coreMinY&&z>=coreMinZ&&x<=coreMaxX+1&&y<=coreMaxY&&z<=coreMaxZ)return LocationCategory.CORE;
        if(x>=coreMinX&&y>=coreMinY-1&&z>=coreMinZ&&x<=coreMaxX&&y<=coreMaxY+1&&z<=coreMaxZ)return LocationCategory.CORE;
        if(x>=coreMinX&&y>=coreMinY&&z>=coreMinZ-1&&x<=coreMaxX&&y<=coreMaxY&&z<=coreMaxZ+1)return LocationCategory.CORE;
        int connectorLength = innerRadius+liningThickness+1;
        if(x>=coreMinX-connectorLength-1&&y>=coreMinY&&z>=coreMinZ&&x<=coreMaxX+connectorLength+1&&y<=coreMaxY&&z<=coreMaxZ)return LocationCategory.CONNECTOR;
        if(x>=coreMinX&&y>=coreMinY&&z>=coreMinZ-connectorLength-1&&x<=coreMaxX&&y<=coreMaxY&&z<=coreMaxZ+connectorLength+1)return LocationCategory.CONNECTOR;
        int plasmaMinX1 = coreMinX-connectorLength-1-toroidWidth;
        int plasmaMaxX1 = coreMinX-connectorLength-2;
        int plasmaMinX2 = coreMaxX+connectorLength+2;
        int plasmaMaxX2 = coreMaxX+connectorLength+1+toroidWidth;
        if(y>=coreMinY&&y<=coreMaxY){
            //in Y range
            if(x>=plasmaMinX1&&z>=plasmaMinX1&&x<=plasmaMaxX2&&z<=plasmaMaxX2//within outer bounds
                    &&(x<=plasmaMaxX1||x>=plasmaMinX2||z<=plasmaMaxX1||z>=plasmaMinX2)){//not within any inner bounds
                return LocationCategory.PLASMA;
            }
            if(x>=plasmaMinX1-1&&z>=plasmaMinX1-1&&x<=plasmaMaxX2+1&&z<=plasmaMaxX2+1//within outer bounds
                    &&(x<=plasmaMaxX1+1||x>=plasmaMinX2-1||z<=plasmaMaxX1+1||z>=plasmaMinX2-1)){//not within any inner bounds
                return LocationCategory.INTERIOR;
            }
        }
        if(y>=coreMinY-1&&y<=coreMaxY+1){
            if(x>=plasmaMinX1&&z>=plasmaMinX1&&x<=plasmaMaxX2&&z<=plasmaMaxX2//within outer bounds
                    &&(x<=plasmaMaxX1||x>=plasmaMinX2||z<=plasmaMaxX1||z>=plasmaMinX2)){//not within any inner bounds
                return LocationCategory.INTERIOR;
            }
            if(x>=plasmaMinX1-1&&z>=plasmaMinX1-1&&x<=plasmaMaxX2+1&&z<=plasmaMaxX2+1//within outer bounds
                    &&(x<=plasmaMaxX1+1||x>=plasmaMinX2-1||z<=plasmaMaxX1+1||z>=plasmaMinX2-1)){//not within any inner bounds
                return LocationCategory.POLOID;
            }
        }
        if(y>=coreMinY-liningThickness&&y<=coreMaxY+liningThickness){
            if(x>=plasmaMinX1-liningThickness&&z>=plasmaMinX1-liningThickness&&x<=plasmaMaxX2+liningThickness&&z<=plasmaMaxX2+liningThickness//within outer bounds
                    &&(x<=plasmaMaxX1+liningThickness||x>=plasmaMinX2-liningThickness||z<=plasmaMaxX1+liningThickness||z>=plasmaMinX2-liningThickness)){//not within any inner bounds
                return LocationCategory.EXTERIOR;
            }
            if(x>=plasmaMinX1-(liningThickness+1)&&z>=plasmaMinX1-(liningThickness+1)&&x<=plasmaMaxX2+(liningThickness+1)&&z<=plasmaMaxX2+(liningThickness+1)//within outer bounds
                    &&(x<=plasmaMaxX1+(liningThickness+1)||x>=plasmaMinX2-(liningThickness+1)||z<=plasmaMaxX1+(liningThickness+1)||z>=plasmaMinX2-(liningThickness+1))){//not within any inner bounds
                return LocationCategory.TOROID;
            }
        }
        if(y>=coreMinY-(liningThickness+1)&&y<=coreMaxY+(liningThickness+1)){
            if(x>=plasmaMinX1-liningThickness&&z>=plasmaMinX1-liningThickness&&x<=plasmaMaxX2+liningThickness&&z<=plasmaMaxX2+liningThickness//within outer bounds
                    &&(x<=plasmaMaxX1+liningThickness||x>=plasmaMinX2-liningThickness||z<=plasmaMaxX1+liningThickness||z>=plasmaMinX2-liningThickness)){//not within any inner bounds
                return LocationCategory.TOROID;
            }
        }
        //now the interior and poloids. Should be easy with those plasma variables ya got there
        return LocationCategory.NONE;
    }
    public enum LocationCategory{
        CORE,CONNECTOR,PLASMA,INTERIOR,POLOID,EXTERIOR,TOROID,NONE;
    }
    @Deprecated
    @Override
    public Menu getResizeMenu(GUI gui, MenuEdit editor){
        return new MenuResizeFusion(gui, editor, this);
    }
    @Deprecated
    @Override
    public void openVRResizeMenu(VRGUI gui, VRMenuEdit editor){
        gui.open(new VRMenuResizeFusion(gui, editor, this));
    }
    public boolean isLocationValid(Block block, int x, int y, int z){
        switch(getLocationCategory(x, y, z)){
            case CONNECTOR:
                return block!=null&&block.isConnector();
            case CORE:
                return block!=null&&block.template.core;
            case EXTERIOR:
                return block==null||block.isHeatsink()||block.isShielding()||block.isConductor()||block.isInert();
            case INTERIOR:
                return block==null||block.isHeatingBlanket()||block.isBreedingBlanket()||block.isReflector()||block.isHeatsink()||block.isShielding()||block.isConductor()||block.isInert();
            case NONE:
            case PLASMA:
                return block==null;
            case POLOID:
            case TOROID:
                return block!=null&&block.isElectromagnet();
            default:
                throw new IllegalArgumentException("Unknown location category: "+getLocationCategory(x, y, z).name()+"!");
        }
    }
    @Override
    public void setBlock(int x, int y, int z, Block block){
        if(isLocationValid((Block)block, x,y,z))super.setBlock(x, y, z, block);
    }
    @Override
    public void setBlockExact(int x, int y, int z, Block exact){
        if(isLocationValid((Block)exact, x,y,z))super.setBlockExact(x, y, z, exact);
    }
    @Override
    public void getSuggestors(ArrayList<Suggestor> suggestors){
        suggestors.add(new Suggestor<OverhaulFusionReactor>("Heatsink Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulFusionReactor>("Temperature", true, true){
                    @Override
                    protected double doCompare(OverhaulFusionReactor main, OverhaulFusionReactor other){
                        return other.netHeat-main.netHeat;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding or replacing heat sinks to cool the reactor";
            }
            @Override
            public void generateSuggestions(OverhaulFusionReactor multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> blocks = new ArrayList<>();
                multiblock.getAvailableBlocks(blocks);
                for(Iterator<Block> it = blocks.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isHeatsink())it.remove();
                }
                int[] count = new int[1];
                forEachPosition((x, y, z) -> {
                    if(getLocationCategory(x, y, z)==LocationCategory.EXTERIOR||getLocationCategory(x, y, z)==LocationCategory.INTERIOR){
                        Block block = multiblock.getBlock(x, y, z);
                        if(block==null||block.canBeQuickReplaced()){
                            count[0]++;
                        }
                    }
                });
                suggestor.setCount(count[0]*blocks.size());
                multiblock.forEachPosition((x, y, z) -> {
                    if(getLocationCategory(x, y, z)==LocationCategory.EXTERIOR||getLocationCategory(x, y, z)==LocationCategory.INTERIOR){
                        for(Block newBlock : blocks){
                            Block block = multiblock.getBlock(x, y, z);
                            if(block==null||block.canBeQuickReplaced()){
                                int oldCooling = 0;
                                if(block.template.heatsinkHasBaseStats||block.recipe!=null)oldCooling = block.recipe==null?block.template.heatsinkCooling:block.recipe.heatsinkCooling;
                                int newCooling = newBlock.recipe==null?newBlock.template.heatsinkCooling:newBlock.recipe.heatsinkCooling;
                                if(newCooling>oldCooling&&multiblock.isValid(newBlock, x, y, z))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(x, y, z, newBlock), priorities));
                                else suggestor.task.max--;
                            }
                        }
                    }
                });
            }
        });
    }
    @Override
    public String getPreviewTexture(){
        return null;
    }
    @Override
    public LiteMultiblock<OverhaulFusionReactor> compile(){
        return null;
    }
    @Override
    public boolean shouldHideWithCasing(int x, int y, int z){
        return getLocationCategory(x, y, z)==LocationCategory.TOROID;
    }
}
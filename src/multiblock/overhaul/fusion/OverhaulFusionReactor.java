package multiblock.overhaul.fusion;
import generator.Priority;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fusion.BreedingBlanketRecipe;
import multiblock.configuration.overhaul.fusion.CoolantRecipe;
import multiblock.configuration.overhaul.fusion.Recipe;
import multiblock.ppe.ClearInvalid;
import multiblock.ppe.FusionFill;
import multiblock.ppe.PostProcessingEffect;
import multiblock.ppe.SmartFillOverhaulFusion;
import multiblock.symmetry.AxialSymmetry;
import multiblock.symmetry.Symmetry;
import planner.file.NCPFFile;
import planner.menu.MenuEdit;
import planner.menu.MenuResizeFusion;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.generator.MenuComponentFusionToggleBreedingBlanketRecipe;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.opengl.gui.GUI;
public class OverhaulFusionReactor extends Multiblock<Block>{
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
    private float sparsityMult;
    private float shieldinessFactor;
    public OverhaulFusionReactor(){
        this(null);
    }
    public OverhaulFusionReactor(Configuration configuration){
        this(configuration, 1, 1, 1, 1, null, null);
    }
    public OverhaulFusionReactor(Configuration configuration, int innerRadius, int coreSize, int toroidWidth, int liningThickness, Recipe recipe, CoolantRecipe coolantRecipe){
        super(configuration, getWidth(innerRadius, coreSize, toroidWidth, liningThickness), getHeight(innerRadius, coreSize, toroidWidth, liningThickness), getWidth(innerRadius, coreSize, toroidWidth, liningThickness));
        this.recipe = recipe==null?(exists()?getConfiguration().overhaul.fusion.allRecipes.get(0):null):recipe;
        this.coolantRecipe = coolantRecipe==null?(exists()?getConfiguration().overhaul.fusion.allCoolantRecipes.get(0):null):coolantRecipe;
        this.innerRadius = innerRadius;
        this.coreSize = coreSize;
        this.toroidWidth = toroidWidth;
        this.liningThickness = liningThickness;
        fillRequiredSpaces();
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
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    switch(getLocationCategory(x, y, z)){
                        case CORE:
                            setBlock(x, y, z, core);
                        case CONNECTOR:
                            setBlock(x, y, z, connector);
                            break;
                        case POLOID:
                        case TOROID:
                            setBlock(x, y, z, electromagnet);
                            break;
                        case PLASMA:
                        case NONE:
                        case INTERIOR:
                        case EXTERIOR:
                            break;
                    }
                }
            }
        }
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
    public Multiblock<Block> newInstance(Configuration configuration, int x, int y, int z){
        return new OverhaulFusionReactor(configuration, 1, 1, 1, 1, null, null);//I'm not even gonna try to convert that to reasonable settings
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getConfiguration()==null||getConfiguration().overhaul==null||getConfiguration().overhaul.fusion==null)return;
        for(multiblock.configuration.overhaul.fusion.Block block : getConfiguration().overhaul.fusion.allBlocks){
            blocks.add(new Block(getConfiguration(), -1, -1, -1, block));
        }
    }
    @Override
    public int getMinX(){
        return getWidth(getConfiguration().overhaul.fusion.minInnerRadius, getConfiguration().overhaul.fusion.minCoreSize, getConfiguration().overhaul.fusion.minToroidWidth, getConfiguration().overhaul.fusion.minLiningThickness);
    }
    @Override
    public int getMinY(){
        return getHeight(getConfiguration().overhaul.fusion.minInnerRadius, getConfiguration().overhaul.fusion.minCoreSize, getConfiguration().overhaul.fusion.minToroidWidth, getConfiguration().overhaul.fusion.minLiningThickness);
    }
    @Override
    public int getMinZ(){
        return getMinX();
    }
    @Override
    public int getMaxX(){
        return getWidth(getConfiguration().overhaul.fusion.maxInnerRadius, getConfiguration().overhaul.fusion.maxCoreSize, getConfiguration().overhaul.fusion.maxToroidWidth, getConfiguration().overhaul.fusion.maxLiningThickness);
    }
    @Override
    public int getMaxY(){
        return getHeight(getConfiguration().overhaul.fusion.maxInnerRadius, getConfiguration().overhaul.fusion.maxCoreSize, getConfiguration().overhaul.fusion.maxToroidWidth, getConfiguration().overhaul.fusion.maxLiningThickness);
    }
    @Override
    public int getMaxZ(){
        return getMaxX();
    }
    @Override
    public void calculate(List<Block> blocks){
        List<Block> allBlocks = getBlocks();
        for(Block block : blocks){
            block.calculateBreedingBlanket(this);
        }
        for(Block block : blocks){
            block.calculateHeatingBlanket(this);
        }
        boolean somethingChanged;
        do{
            somethingChanged = false;
            for(Block block : blocks){
                if(block.calculateHeatsink(this))somethingChanged = true;
            }
        }while(somethingChanged);
        clusters.clear();
        shieldinessFactor = 0;
        for(Block block : allBlocks){//detect clusters and shieldniness too
            if(block.isShielding())shieldinessFactor+=block.template.shieldiness;
            Cluster cluster = getCluster(block);
            if(cluster==null)continue;//that's not a cluster!
            synchronized(clusters){
                if(clusters.contains(cluster))continue;//already know about that one!
                clusters.add(cluster);
            }
        }
        shieldinessFactor/=getPlasmaSurfaceArea();
        synchronized(clusters){
            for(Cluster cluster : clusters){
                int heatingBlankets = 0;
                for(Block b : cluster.blocks){
                    if(b.isHeatingBlanketActive()){
                        heatingBlankets++;
                        cluster.totalOutput += recipe.heat*b.efficiency;
                        cluster.efficiency+=b.efficiency;
                        cluster.totalHeat+=recipe.heat*b.heatMult;
                        cluster.heatMult+=b.heatMult;
                    }
                    if(b.isHeatsinkActive()){
                        cluster.totalCooling+=b.template.cooling;
                    }
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
            }
        }
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
        int volume = 0;
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    LocationCategory cat = getLocationCategory(x, y, z);
                    if(cat==LocationCategory.INTERIOR||cat==LocationCategory.EXTERIOR)volume++;
                }
            }
        }
        sparsityMult = (float) (functionalBlocks/(float)volume>=getConfiguration().overhaul.fusion.sparsityPenaltyThreshold?1:getConfiguration().overhaul.fusion.sparsityPenaltyMult+(1-getConfiguration().overhaul.fusion.sparsityPenaltyMult)*Math.sin(Math.PI*functionalBlocks/(2*volume*getConfiguration().overhaul.fusion.sparsityPenaltyThreshold)));
        totalOutput*=sparsityMult;
        totalEfficiency*=sparsityMult;
        totalOutput/=coolantRecipe.heat/coolantRecipe.outputRatio;
    }
    @Override
    protected Block newCasing(int x, int y, int z){
        return null;
    }
    @Override
    public synchronized String getTooltip(){
        return tooltip(true);
    }
    @Override
    public String getExtraBotTooltip(){
        return tooltip(false);
    }
    public String tooltip(boolean showDetails){
        if(this.showDetails!=null)showDetails = this.showDetails;
        synchronized(clusters){
            int validClusters = 0;
            for(Cluster c : clusters){
                if(c.isValid())validClusters++;
            }
            String s = "Total output: "+totalOutput+" mb/t of "+coolantRecipe.output+"\n"
                    + "Total Heat: "+totalHeat+"H/t\n"
                    + "Total Cooling: "+totalCooling+"H/t\n"
                    + "Net Heat: "+netHeat+"H/t\n"
                    + "Overall Efficiency: "+percent(totalEfficiency, 0)+"\n"
                    + "Overall Heat Multiplier: "+percent(totalHeatMult, 0)+"\n"
                    + "Sparsity Penalty Multiplier: "+Math.round(sparsityMult*10000)/10000d+"\n"
                    + "Shieldiness Factor: "+percent(shieldinessFactor, 1)+"\n"
                    + "Clusters: "+(validClusters==clusters.size()?clusters.size():(validClusters+"/"+clusters.size()))+"\n";
            if(showDetails){
                for(Cluster c : clusters){
                    s+="\n\n"+c.getTooltip();
                }
            }
            return s;
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
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block block = getBlock(x, y, z);
                    if(block==null)blox.add(0);
                    else blox.add(configuration.overhaul.fusion.allBlocks.indexOf(block.template)+1);
                    System.out.print((block==null?"0":(configuration.overhaul.fusion.allBlocks.indexOf(block.template)+1))+" ");
                }
            }
        }
        System.out.println(blox.size());
        ConfigNumberList breedingBlanketRecipes = new ConfigNumberList();
        for(Block block : getBlocks()){
            if(block.template.breedingBlanket)breedingBlanketRecipes.add(configuration.overhaul.fusion.allBreedingBlanketRecipes.indexOf(block.breedingBlanketRecipe)+1);
        }
        config.set("blocks", blox);
        config.set("breedingBlanketRecipes", breedingBlanketRecipes);
        config.set("recipe", (byte)configuration.overhaul.fusion.allRecipes.indexOf(recipe));
        config.set("coolantRecipe", (byte)configuration.overhaul.fusion.allCoolantRecipes.indexOf(coolantRecipe));
    }
    @Override
    public void convertTo(Configuration to){
        if(to.overhaul==null||to.overhaul.fusion==null)return;
        for(Block block : getBlocks()){
            if(block.template.breedingBlanket)block.breedingBlanketRecipe = to.overhaul.fusion.convert(block.breedingBlanketRecipe);
            block.template = to.overhaul.fusion.convert(block.template);
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
    private HashMap<BreedingBlanketRecipe, MenuComponentFusionToggleBreedingBlanketRecipe> breedingBlanketRecipeToggles;
    @Override
    public void addGeneratorSettings(MenuComponentMinimaList multiblockSettings){
        if(breedingBlanketRecipeToggles==null)breedingBlanketRecipeToggles = new HashMap<>();
        for(BreedingBlanketRecipe r : getConfiguration().overhaul.fusion.allBreedingBlanketRecipes){
            MenuComponentFusionToggleBreedingBlanketRecipe toggle = new MenuComponentFusionToggleBreedingBlanketRecipe(r);
            breedingBlanketRecipeToggles.put(r, toggle);
            multiblockSettings.add(toggle);
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
    }
    @Override
    public void getGenerationPriorityPresets(ArrayList<Priority> priorities, ArrayList<Priority.Preset> presets){
        presets.add(new Priority.Preset("Efficiency", priorities.get(0), priorities.get(1), priorities.get(2), priorities.get(3)).addAlternative("Efficient"));
        presets.add(new Priority.Preset("Output", priorities.get(0), priorities.get(1), priorities.get(3), priorities.get(2)));
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
        for(multiblock.configuration.overhaul.fusion.Block b : getConfiguration().overhaul.fusion.allBlocks){
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
        blocks = new Block[getWidth(innerRadius, coreSize, toroidWidth, liningThickness)][getHeight(innerRadius, coreSize, toroidWidth, liningThickness)][getWidth(innerRadius, coreSize, toroidWidth, liningThickness)];
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
    }
    public void increaseCoreSize(){
        if(coreSize>=getConfiguration().overhaul.fusion.maxCoreSize)return;
        coreSize++;
        blocks = new Block[getWidth(innerRadius, coreSize, toroidWidth, liningThickness)][getHeight(innerRadius, coreSize, toroidWidth, liningThickness)][getWidth(innerRadius, coreSize, toroidWidth, liningThickness)];
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
    }
    public void increaseToroidWidth(){
        if(toroidWidth>=getConfiguration().overhaul.fusion.maxToroidWidth)return;
        toroidWidth++;
        blocks = new Block[getWidth(innerRadius, coreSize, toroidWidth, liningThickness)][getHeight(innerRadius, coreSize, toroidWidth, liningThickness)][getWidth(innerRadius, coreSize, toroidWidth, liningThickness)];
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
    }
    public void increaseLiningThickness(){
        if(liningThickness>=getConfiguration().overhaul.fusion.maxLiningThickness)return;
        liningThickness++;
        blocks = new Block[getWidth(innerRadius, coreSize, toroidWidth, liningThickness)][getHeight(innerRadius, coreSize, toroidWidth, liningThickness)][getWidth(innerRadius, coreSize, toroidWidth, liningThickness)];
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
    }
    public void decreaseInnerRadius(){
        if(innerRadius<=getConfiguration().overhaul.fusion.minInnerRadius)return;
        innerRadius--;
        blocks = new Block[getWidth(innerRadius, coreSize, toroidWidth, liningThickness)][getHeight(innerRadius, coreSize, toroidWidth, liningThickness)][getWidth(innerRadius, coreSize, toroidWidth, liningThickness)];
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
    }
    public void decreaseCoreSize(){
        if(coreSize<=getConfiguration().overhaul.fusion.minCoreSize)return;
        coreSize--;
        blocks = new Block[getWidth(innerRadius, coreSize, toroidWidth, liningThickness)][getHeight(innerRadius, coreSize, toroidWidth, liningThickness)][getWidth(innerRadius, coreSize, toroidWidth, liningThickness)];
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
    }
    public void decreaseToroidWidth(){
        if(toroidWidth<=getConfiguration().overhaul.fusion.minToroidWidth)return;
        toroidWidth--;
        blocks = new Block[getWidth(innerRadius, coreSize, toroidWidth, liningThickness)][getHeight(innerRadius, coreSize, toroidWidth, liningThickness)][getWidth(innerRadius, coreSize, toroidWidth, liningThickness)];
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
    }
    public void decreaseLiningThickness(){
        if(liningThickness<=getConfiguration().overhaul.fusion.minLiningThickness)return;
        liningThickness--;
        blocks = new Block[getWidth(innerRadius, coreSize, toroidWidth, liningThickness)][getHeight(innerRadius, coreSize, toroidWidth, liningThickness)][getWidth(innerRadius, coreSize, toroidWidth, liningThickness)];
        history.clear();
        future.clear();
        fillRequiredSpaces();
        //TODO don't delete all blocks in the reactor! translate them to the new size!
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
                for(Direction d : directions){
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
                + "Efficiency: "+percent(efficiency, 0)+"\n"
                + "Total Heating: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heating: "+netHeat+"H/t\n"
                + "Heat Multiplier: "+percent(heatMult, 0)+"\n"
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
        int maxDistance = getX()*getY()*getZ();//the algorithm requires a max search distance. Rather than changing that, I'll just be lazy and give it a big enough number
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
        return getConfiguration().overhaul!=null&&getConfiguration().overhaul.fusion!=null;
    }
    @Override
    public OverhaulFusionReactor blankCopy(){
        return new OverhaulFusionReactor(configuration, innerRadius, coreSize, toroidWidth, liningThickness, recipe, coolantRecipe);
    }
    @Override
    public OverhaulFusionReactor doCopy(){
        OverhaulFusionReactor copy = blankCopy();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block get = getBlock(x, y, z);
                    if(get!=null)copy.setBlockExact(x, y, z, get.copy());
                }
            }
        }
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
        int count = 0;
        if(o instanceof BreedingBlanketRecipe){
            BreedingBlanketRecipe r = (BreedingBlanketRecipe)o;
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block b = getBlock(x, y, z);
                        if(b==null)continue;
                        if(b.breedingBlanketRecipe==r)count++;
                    }
                }
            }
            return count;
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
    protected void getFluidOutputs(HashMap<String, Double> outputs){
        outputs.put(coolantRecipe.output, (double)totalOutput);
    }
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){}
    @Override
    public String getDescriptionTooltip(){
        return "A fusion reactor for Nuclearcraft: Overhauled\nTHESE DO NOT EXIST INGAME!\nThis is just a prototype, and is VERY different from what they will be in-game";
    }
    public LocationCategory getLocationCategory(int x, int y, int z){
        //old; make and use local variables >.>
        int coreMinX = (getX()-coreSize)/2;
        int coreMaxX = (getX()+coreSize)/2-1;
        int coreMinY = (getY()-coreSize)/2;
        int coreMaxY = (getY()+coreSize)/2-1;
        int coreMinZ = (getZ()-coreSize)/2;
        int coreMaxZ = (getZ()+coreSize)/2-1;
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
    @Override
    public float get3DPreviewScale(){
        return 1.95f;
    }
    @Override
    public void openResizeMenu(GUI gui, MenuEdit editor){
        gui.open(new MenuResizeFusion(gui, editor, this));
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
    public void setBlock(int x, int y, int z, multiblock.Block block){
        if(isLocationValid((Block)block, x,y,z))super.setBlock(x, y, z, block);
    }
    @Override
    public void setBlockExact(int x, int y, int z, multiblock.Block exact){
        if(isLocationValid((Block)exact, x,y,z))super.setBlockExact(x, y, z, exact);
    }
}
package net.ncplanner.plannerator.multiblock.overhaul.distiller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.PartCount;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.decal.BlockInvalidDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.BlockValidDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.MissingCasingDecal;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementStack;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentEditorGrid;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulDistillerConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.DistillerRecipe;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulDistillerDesign;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.settings.OverhaulDistillerSettingsModule;
@RegisterWith(module = OverhaulModule.class)
public class OverhaulDistiller extends CuboidalMultiblock<Block>{
    private static final OverhaulDistillerSettingsModule DEFAULT_SETTINGS = new OverhaulDistillerSettingsModule();
    public DistillerRecipe recipe;
    public int numControllers, missingCasings, traySieveCount, packedSieveCount;
    public int refluxUnitCount, reboilingUnitCount, liquidDistributorCount;
    public double traySieveEfficiency, packedSieveEfficiency, baseSpeedMultiplier, basePowerMultiplier;
    public double refluxUnitBonus, reboilingUnitBonus, liquidDistributorBonus, speedMultiplier;
    public double processTime, processPower, processEnergy;
    public boolean structureValid;
    public final List<String> errors = new ArrayList<>();
    private int calcStep;
    private Task calcCasing, calcLayers, calcStats;
    public OverhaulDistiller(){
        this(null);
    }
    public OverhaulDistiller(NCPFConfigurationContainer configuration){
        this(configuration, 3, 3, 3, null);
    }
    public OverhaulDistiller(NCPFConfigurationContainer configuration, int x, int y, int z, DistillerRecipe recipe){
        super(configuration, x, y, z);
        this.recipe = recipe==null?(exists()?(getSpecificConfiguration().recipes.isEmpty()?null:getSpecificConfiguration().recipes.get(0)):null):recipe;
    }
    @Override
    public OverhaulDistiller newInstance(NCPFConfigurationContainer configuration, int x, int y, int z){
        return new OverhaulDistiller(configuration, x, y, z, null);
    }
    @Override
    public boolean canBePlacedWithinCasing(Block b){
        return !b.isCasing();
    }
    @Override
    public boolean canBePlacedInCasingEdge(Block b){
        return b.isCasing()&&b.template.casing!=null&&b.template.casing.edge;
    }
    @Override
    public boolean canBePlacedInCasingFace(Block b){
        return b.isCasing();
    }
    @Override
    public void getEditorSpaces(ArrayList<EditorSpace<Block>> editorSpaces){
        ArrayList<EditorSpace<Block>> casingSpaces = new ArrayList<>();
        super.getEditorSpaces(casingSpaces);
        EditorSpace<Block> casingSpace = casingSpaces.get(0);
        editorSpaces.add(new EditorSpace<Block>(0, 0, 0, x+1, y+1, z+1){
            @Override
            public boolean isSpaceValid(Block block, BlockPos pos){
                if(block==null)return true;
                if(!casingSpace.isSpaceValid(block, pos))return false;
                if((block.isRefluxUnit()||block.isLiquidDistributor())&&pos.y!=getExternalHeight()-1)return false;
                if(block.isReboilingUnit()&&pos.y!=0)return false;
                return true;
            }
            @Override
            public void createComponents(MenuEdit editor, ArrayList<Component> comps, int cellSize){
                for(int y = 0; y<=OverhaulDistiller.this.y+1; y++){
                    comps.add(new MenuComponentEditorGrid(0, 0, cellSize, editor, OverhaulDistiller.this, this, 0, 0, OverhaulDistiller.this.x+1, OverhaulDistiller.this.z+1, Axis.Y, y));
                }
            }
        });
    }
    @Override
    public int getMinX(){
        return getSettings().minSize;
    }
    @Override
    public int getMinY(){
        return getSettings().minSize;
    }
    @Override
    public int getMinZ(){
        return getSettings().minSize;
    }
    @Override
    public int getMaxX(){
        return getSettings().maxSize;
    }
    @Override
    public int getMaxY(){
        return getSettings().maxSize;
    }
    @Override
    public int getMaxZ(){
        return getSettings().maxSize;
    }
    @Override
    public void buildDefaultCasing(){
        Block casing = null;
        Block window = null;
        Block controller = null;
        for(BlockElement template : getSpecificConfiguration().blocks){
            if(template.casing!=null&&template.casing.edge)casing = new Block(getConfiguration(), null, template);
            if(template.casing!=null&&!template.casing.edge&&template.controller==null&&template.reboilingUnit==null&&template.refluxUnit==null&&template.liquidDistributor==null)window = new Block(getConfiguration(), null, template);
            if(template.controller!=null)controller = new Block(getConfiguration(), null, template);
        }
        final Block theCasing = casing;
        final Block theWindow = window==null?casing:window;
        final Block theController = controller;
        boolean[] hasPlacedTheController = new boolean[1];
        for(Block block : getBlocks()){
            if(block.template.controller!=null)hasPlacedTheController[0] = true;
        }
        forEachCasingFacePosition((pos) -> {
            if(getBlock(pos)!=null){
                if(getBlock(pos).template!=theCasing.template&&getBlock(pos).template!=theWindow.template)return;
            }
            if(!hasPlacedTheController[0]){
                setBlock(pos, theController);
                hasPlacedTheController[0] = true;
                return;
            }
        });
        forEachCasingEdgePosition((pos) -> {
            if(getBlock(pos)!=null)return;
            setBlock(pos, theCasing);
        });
        forEachCasingFacePosition((pos) -> {
            if(getBlock(pos)!=null)return;
            setBlock(pos, theWindow);
        });
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul Distiller";
    }
    @Override
    public Multiblock<Block> newInstance(NCPFConfigurationContainer configuration){
        return new OverhaulDistiller(configuration);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getSpecificConfiguration()==null)return;
        for(BlockElement block : getSpecificConfiguration().blocks){
            blocks.add(new Block(getConfiguration(), null, block));
        }
    }
    @Override
    public void genCalcSubtasks(){
        calcStep = 0;
        calcCasing = calculateTask.addSubtask(new Task("Calculating Casing"));
        calcLayers = calculateTask.addSubtask(new Task("Calculating Sieve Layers"));
        calcStats = calculateTask.addSubtask(new Task("Calculating Stats"));
    }
    @Override
    public boolean doCalculationStep(List<Block> blocks, boolean addDecals){
        switch(calcStep++){
            case 0:
                // Always calculate the whole structure: a tray depends on the layer above it.
                clearData(getBlocks());
                structureValid = true;
                forEachCasingPosition((pos) -> {
                    Block block = getBlock(pos);
                    if(block==null){
                        missingCasings++;
                        if(addDecals)decals.enqueue(new MissingCasingDecal(pos));
                        return;
                    }
                    int boundaries = (pos.x==0||pos.x==getExternalWidth()-1?1:0)
                            +(pos.y==0||pos.y==getExternalHeight()-1?1:0)
                            +(pos.z==0||pos.z==getExternalDepth()-1?1:0);
                    boolean valid = boundaries>1?canBePlacedInCasingEdge(block):canBePlacedInCasingFace(block);
                    if(block.isController())numControllers++;
                    if(block.isRefluxUnit()||block.isLiquidDistributor()){
                        valid &= pos.y==getExternalHeight()-1;
                        if(!valid)addError("Reflux units and liquid distributors must be on the top face.");
                    }
                    if(block.isReboilingUnit()){
                        valid &= pos.y==0;
                        if(!valid)addError("Reboiling units must be on the bottom face.");
                    }
                    setValid(block, valid, addDecals);
                    if(!valid)addError("Invalid casing block placement.");
                    else{
                        if(block.isRefluxUnit())refluxUnitCount++;
                        if(block.isReboilingUnit())reboilingUnitCount++;
                        if(block.isLiquidDistributor())liquidDistributorCount++;
                    }
                });
                if(numControllers!=1)addError(numControllers==0?"No controller!":"Too many controllers!");
                if(missingCasings>0)addError("Casing incomplete! (Missing "+missingCasings+")");
                calcCasing.finish();
                return true;
            case 1:
                Block[] layers = new Block[getInternalHeight()+2];
                boolean[] symmetric = new boolean[layers.length];
                for(int y = 1; y<=getInternalHeight(); y++){
                    layers[y] = getBlock(new BlockPos(1, y, 1));
                    symmetric[y] = true;
                    for(int x = 1; x<=getInternalWidth(); x++){
                        for(int z = 1; z<=getInternalDepth(); z++){
                            Block block = getBlock(new BlockPos(x, y, z));
                            if(block==null?layers[y]!=null:layers[y]==null||block.template!=layers[y].template)symmetric[y] = false;
                        }
                    }
                }
                for(int y = 1; y<=getInternalHeight(); y++){
                    Block layer = layers[y];
                    boolean valid = symmetric[y]&&(layer==null||layer.isSieveTray()||layer.isSieveAssembly());
                    if(!symmetric[y])addError("Layer "+y+" must be uniform across its entire area.");
                    if(layer!=null&&layer.isSieveTray()){
                        boolean hasSieve = y<getInternalHeight()&&symmetric[y+1]&&layers[y+1]!=null&&layers[y+1].isSieveAssembly();
                        valid &= hasSieve;
                        if(!hasSieve)addError("Sieve tray on layer "+y+" needs a complete sieve assembly layer directly above it.");
                        if(valid){
                            traySieveCount++;
                            traySieveEfficiency += layers[y+1].template.sieveAssembly.efficiency;
                        }
                    }else if(valid&&layer!=null&&layer.isSieveAssembly()){
                        // A sieve on a tray is counted once, as part of that tray.
                        if(y==1||layers[y-1]==null||!layers[y-1].isSieveTray()){
                            packedSieveCount++;
                            packedSieveEfficiency += layer.template.sieveAssembly.efficiency;
                        }
                    }
                    for(int x = 1; x<=getInternalWidth(); x++){
                        for(int z = 1; z<=getInternalDepth(); z++){
                            Block block = getBlock(new BlockPos(x, y, z));
                            if(block!=null)setValid(block, valid, addDecals);
                        }
                    }
                    if(!valid)addError("Invalid interior layer "+y+".");
                }
                calcLayers.finish();
                return true;
            case 2:
                int area = getInternalWidth()*getInternalDepth();
                baseSpeedMultiplier = area*(traySieveCount==0?packedSieveEfficiency:
                        packedSieveCount==0?traySieveEfficiency:
                        (packedSieveCount*traySieveEfficiency+packedSieveEfficiency)/(1+packedSieveCount));
                basePowerMultiplier = reboilingUnitCount+(double)area*(traySieveCount+packedSieveCount);
                if(recipe==null||recipe.stats==null){
                    errors.add("No recipe selected!");
                }else{
                    int liquidCount = 0, gasCount = 0;
                    for(Set<NCPFElementStack> fluids : Arrays.asList(recipe.getRecipeDefinition().inputs, recipe.getRecipeDefinition().outputs)){
                        for(NCPFElementStack stack : fluids){
                            if(stack.amount<=0||!(stack.definition instanceof NCPFLegacyFluidElement))continue;
                            NCPFLegacyFluidElement fluid = (NCPFLegacyFluidElement)stack.definition;
                            if(fluid.name==null||fluid.name.isEmpty())continue;
                            if(fluid.gaseous)gasCount++;
                            else liquidCount++;
                        }
                    }
                    int totalCount = liquidCount+gasCount;
                    refluxUnitBonus = totalCount<=0?0:(double)refluxUnitCount/area*gasCount/totalCount;
                    reboilingUnitBonus = totalCount<=0?0:(double)reboilingUnitCount/area*liquidCount/totalCount;
                    liquidDistributorBonus = (double)liquidDistributorCount/area/(1+traySieveCount);
                    speedMultiplier = baseSpeedMultiplier*(1+refluxUnitBonus)*(1+reboilingUnitBonus)*(1+liquidDistributorBonus);
                    if(traySieveCount<recipe.stats.sieveTrayCount)errors.add("Insufficient sieve trays! ("+traySieveCount+"/"+recipe.stats.sieveTrayCount+")");
                    if(speedMultiplier<=0)errors.add("No effective sieve layers!");
                    processPower = getSettings().basePower*recipe.stats.powerMult*basePowerMultiplier;
                    if(speedMultiplier>0){
                        processTime = getSettings().baseTime*recipe.stats.timeMult/speedMultiplier;
                        processEnergy = processTime*processPower;
                    }
                }
                calcStats.finish();
                calcStep = 0;
                return false;
            default:
                throw new IllegalStateException("Invalid calculation step: "+(calcStep-1));
        }
    }
    private void addError(String error){
        structureValid = false;
        if(!errors.contains(error))errors.add(error);
    }
    private OverhaulDistillerSettingsModule getSettings(){
        // Older NuclearCraft exports constructed the settings but omitted the module.
        OverhaulDistillerSettingsModule settings = getSpecificConfiguration().settings;
        return settings==null?DEFAULT_SETTINGS:settings;
    }
    private void setValid(Block block, boolean valid, boolean addDecals){
        block.valid = valid;
        if(addDecals)decals.enqueue(valid?new BlockValidDecal(block.pos):new BlockInvalidDecal(block.pos));
    }
    @Override
    public void clearData(List<Block> blocks){
        super.clearData(blocks);
        numControllers = missingCasings = traySieveCount = packedSieveCount = 0;
        refluxUnitCount = reboilingUnitCount = liquidDistributorCount = 0;
        traySieveEfficiency = packedSieveEfficiency = baseSpeedMultiplier = basePowerMultiplier = 0;
        refluxUnitBonus = reboilingUnitBonus = liquidDistributorBonus = speedMultiplier = 0;
        processTime = processPower = processEnergy = 0;
        structureValid = false;
        errors.clear();
    }
    @Override
    public FormattedText getTooltip(boolean full){
        FormattedText text = new FormattedText();
        for(String error : errors)text.addText(error+"\n", Core.theme.getTooltipInvalidTextColor());
        text.addText("Sieve trays: "+traySieveCount+"\nPacked sieve layers: "+packedSieveCount
                +"\nSpeed multiplier: "+MathUtil.round(speedMultiplier, 3)
                +"\nPower multiplier: "+MathUtil.round(basePowerMultiplier, 3)
                +"\nReflux bonus: "+MathUtil.percent(refluxUnitBonus, 2)
                +"\nReboiling bonus: "+MathUtil.percent(reboilingUnitBonus, 2)
                +"\nLiquid distributor bonus: "+MathUtil.percent(liquidDistributorBonus, 2)
                +"\nPower use: "+MathUtil.round(processPower, 3)+" RF/t"
                +(recipe!=null&&recipe.stats!=null&&speedMultiplier>0?"\nProcess time: "+MathUtil.round(processTime, 3)+" ticks"
                +"\nEnergy per process: "+MathUtil.round(processEnergy, 3)+" RF":"")
                +getModuleTooltip(), Core.theme.getTooltipTextColor());
        return text;
    }
    @Override
    public boolean validate(){
        return false;
    }
    @Override
    public OverhaulDistiller blankCopy(){
        return new OverhaulDistiller(configuration, getInternalWidth(), getInternalHeight(), getInternalDepth(), recipe);
    }
    @Override
    public OverhaulDistiller doCopy(){
        OverhaulDistiller copy = blankCopy();
        forEachPosition((pos) -> {
            copy.setBlock(pos, getBlock(pos));
        });
        copy.numControllers = numControllers;
        copy.missingCasings = missingCasings;
        copy.traySieveCount = traySieveCount;
        copy.packedSieveCount = packedSieveCount;
        copy.refluxUnitCount = refluxUnitCount;
        copy.reboilingUnitCount = reboilingUnitCount;
        copy.liquidDistributorCount = liquidDistributorCount;
        copy.traySieveEfficiency = traySieveEfficiency;
        copy.packedSieveEfficiency = packedSieveEfficiency;
        copy.baseSpeedMultiplier = baseSpeedMultiplier;
        copy.basePowerMultiplier = basePowerMultiplier;
        copy.refluxUnitBonus = refluxUnitBonus;
        copy.reboilingUnitBonus = reboilingUnitBonus;
        copy.liquidDistributorBonus = liquidDistributorBonus;
        copy.speedMultiplier = speedMultiplier;
        copy.processTime = processTime;
        copy.processPower = processPower;
        copy.processEnergy = processEnergy;
        copy.structureValid = structureValid;
        copy.errors.addAll(errors);
        return copy;
    }
    @Override
    public String getGeneralName(){
        return "Distiller";
    }
    @Override
    protected boolean isCompatible(Multiblock<Block> other){
        return ((OverhaulDistiller)other).recipe==recipe;
    }
    @Override
    public OverhaulDistillerConfiguration getSpecificConfiguration(){
        NCPFConfigurationContainer conf = getConfiguration();
        if(conf==null)return null;
        return conf.getConfiguration(OverhaulDistillerConfiguration::new);
    }
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){
    }
    @Override
    public String getDescriptionTooltip(){
        return "Overhaul Distillers are Distillers in NuclearCraft: Overhauled\nWow, who could have guessed?";
    }
    @Override
    public void getSuggestors(ArrayList<Suggestor> suggestors){
    }
    @Override
    public String getPreviewTexture(){
        return "multiblocks/overhaul_distiller";
    }
    @Override
    public <T extends LiteMultiblock> T compile(){
        return null;
    }
    @Override
    public OverhaulDistillerDesign convertToDesign(){
        OverhaulDistillerDesign design = new OverhaulDistillerDesign(Core.project, x, y, z);
        forEachPosition((pos) -> {
            Block block = getBlock(pos);
            design.design[pos.x][pos.y][pos.z] = block==null?null:block.template;
        });
        design.recipe = recipe;
        return design;
    }
    @Override
    public NCPFElement[] getMultiblockRecipes(){
        return new NCPFElement[]{recipe};
    }
    @Override
    public void setMultiblockRecipe(int recipeType, NCPFElement recipe){
        this.recipe = (DistillerRecipe)recipe;
    }
    @Override
    public void applyMultiblockSymmetry(BlockPos pos, Consumer<BlockPos> consumer){
        if(pos.x==0||pos.y==0||pos.z==0||pos.x==getExternalWidth()-1||pos.y==getExternalHeight()-1||pos.z==getExternalDepth()-1)return;
        for(int X = 1; X<getExternalWidth()-1; X++){
            for(int Z = 1; Z<getExternalDepth()-1; Z++){
                if(pos.x==X&&pos.z==Z)continue; // skip the one you already have
                consumer.accept(new BlockPos(X, pos.y, Z));
            }
        }
    }
}

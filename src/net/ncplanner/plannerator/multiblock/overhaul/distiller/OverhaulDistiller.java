package net.ncplanner.plannerator.multiblock.overhaul.distiller;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.PartCount;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulDistillerConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.DistillerRecipe;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulDistillerDesign;
@RegisterWith(module = OverhaulModule.class)
public class OverhaulDistiller extends CuboidalMultiblock<Block>{
    public DistillerRecipe recipe;
    public OverhaulDistiller(){
        this(null);
    }
    public OverhaulDistiller(NCPFConfigurationContainer configuration){
        this(configuration, 3, 3, 3, null);
    }
    public OverhaulDistiller(NCPFConfigurationContainer configuration, int x, int y, int z, DistillerRecipe recipe){
        super(configuration, x, y, z);
        this.recipe = recipe==null?(exists()?getSpecificConfiguration().recipes.get(0):null):recipe;
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
    public void buildDefaultCasing(){
        Block casing = null;
        Block window = null;
        Block controller = null;
        for(BlockElement template : getSpecificConfiguration().blocks){
            if(template.casing!=null&&template.casing.edge)casing = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.casing!=null&&!template.casing.edge&&template.controller==null&&template.reboilingUnit==null&&template.refluxUnit==null&&template.liquidDistributer==null)window = new Block(getConfiguration(), 0, 0, 0, template);
            if(template.controller!=null)controller = new Block(getConfiguration(), 0, 0, 0, template);
        }
        final Block theCasing = casing;
        final Block theWindow = window==null?casing:window;
        final Block theController = controller;
        boolean[] hasPlacedTheController = new boolean[1];
        for(Block block : getBlocks()){
            if(block.template.controller!=null)hasPlacedTheController[0] = true;
        }
        forEachCasingFacePosition((x, y, z) -> {
            if(getBlock(x, y, z)!=null){
                if(getBlock(x, y, z).template!=theCasing.template&&getBlock(x, y, z).template!=theWindow.template)return;
            }
            if(!hasPlacedTheController[0]){
                setBlock(x, y, z, theController);
                hasPlacedTheController[0] = true;
                return;
            }
        });
        forEachCasingEdgePosition((x, y, z) -> {
            if(getBlock(x, y, z)!=null)return;
            setBlock(x, y, z, theCasing);
        });
        forEachCasingFacePosition((x, y, z) -> {
            if(getBlock(x, y, z)!=null)return;
            setBlock(x, y, z, theWindow);
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
            blocks.add(new Block(getConfiguration(), -1, -1, -1, block));
        }
    }
    @Override
    public void genCalcSubtasks(){
    }
    @Override
    public boolean doCalculationStep(List<Block> blocks, boolean addDecals){
        return false; //TODO calculations
    }
    @Override
    public FormattedText getTooltip(boolean full){
        return new FormattedText("TODO tooltip");
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
        forEachPosition((x, y, z) -> {
            copy.setBlock(x, y, z, getBlock(x, y, z));
        });
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
        forEachPosition((x, y, z) -> {
            Block block = getBlock(x, y, z);
            design.design[x][y][z] = block==null?null:block.template;
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
}

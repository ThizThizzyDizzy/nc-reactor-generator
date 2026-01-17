package net.ncplanner.plannerator.multiblock.overhaul.distiller;
import java.util.ArrayList;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.BlockElement;
public class Block extends AbstractBlock{
    public BlockElement template;
    public boolean valid;
    public Block(NCPFConfigurationContainer configuration, BlockPos pos, BlockElement template){
        super(configuration, pos);
        if(template==null)throw new IllegalArgumentException("Cannot create null block!");
        this.template = template;
    }
    @Override
    public AbstractBlock newInstance(BlockPos pos){
        return new Block(getConfiguration(), pos, template);
    }
    @Override
    public void copyProperties(AbstractBlock other){
    }
    @Override
    public NCPFElement getTemplate(){
        return template;
    }
    @Override
    public void clearData(){
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
        if(isController())tip+="\nController "+(valid?"Valid":"Invalid");
        if(isCasing())tip+="\nCasing "+(valid?"Valid":"Invalid");
        if(isSieveAssembly()){
            tip+="\nSieve Assembly "+(valid?"Valid":"Invalid");
        }
        if(isSieveTray()){
            tip+="\nSieve Tray "+(valid?"Valid":"Invalid");
        }
        if(isRefluxUnit()){
            tip+="\nReflux Unit "+(valid?"Valid":"Invalid");
        }
        if(isReboilingUnit()){
            tip+="\nReboiling Unit "+(valid?"Valid":"Invalid");
        }
        if(isLiquidDistributor()){
            tip+="\nLiquid Distributor "+(valid?"Valid":"Invalid");
        }
        return tip;
    }
    public boolean isController(){
        return template.controller!=null;
    }
    public boolean isCasing(){
        return template.casing!=null;
    }
    public boolean isSieveAssembly(){
        return template.sieveAssembly!=null;
    }
    public boolean isSieveTray(){
        return template.sieveTray!=null;
    }
    public boolean isRefluxUnit(){
        return template.refluxUnit!=null;
    }
    public boolean isReboilingUnit(){
        return template.reboilingUnit!=null;
    }
    public boolean isLiquidDistributor(){
        return template.liquidDistributer!=null;
    }
    @Override
    public void renderOverlay(Renderer renderer, float x, float y, float z, float width, float height, float depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        if(!isValid()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineInvalid(), faceRenderFunc);
        }
    }
    @Override
    public boolean isValid(){
        return valid;
    }
    @Override
    public boolean isActive(){
        return isValid();
    }
    @Override
    public boolean isCore(){
        return false;
    }
    @Override
    public boolean canRequire(AbstractBlock other){
        return false;
    }
    @Override
    public boolean canGroup(){
        return false;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.sieveAssembly!=null;
    }
    @Override
    public AbstractBlock copy(){
        Block copy = new Block(getConfiguration(), pos, template);
        copy.valid = valid;
        return copy;
    }
    @Override
    public ArrayList<? extends IBlockRecipe> getRecipes(){
        return new ArrayList<>();
    }
    @Override
    public NCPFElement getRecipe(){
        return null;
    }
    @Override
    public void setRecipe(NCPFElement recipe){}
}

package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
public abstract class SimpleBlock extends AbstractBlock{
    public SimpleBlock(NCPFConfigurationContainer configuration, int x, int y, int z){
        super(configuration, x, y, z);
    }
    @Override
    public void copyProperties(AbstractBlock other){}
    @Override
    public void clearData(){}
    @Override
    public String getTooltip(Multiblock multiblock){
        return getName();
    }
    @Override
    public void renderOverlay(Renderer renderer, float x, float y, float z, float width, float height, float depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){}
    @Override
    public boolean isValid(){
        return true;
    }
    @Override
    public boolean isActive(){
        return false;
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
    public boolean canBeQuickReplaced() {
        return false;
    }
    @Override
    public AbstractBlock copy(){
        return newInstance(x, y, z);
    }
    @Override
    public boolean isEqual(AbstractBlock other){
        return matches(other);
    }
    @Override
    public ArrayList<? extends IBlockRecipe> getRecipes() {
        return new ArrayList<>();
    }
    @Override
    public String getPinnedName(){
        return getName();
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames() {
        return getSearchableNames();
    }
    @Override
    public ArrayList<String> getSearchableNames() {
        ArrayList<String> strs = new ArrayList<>();
        strs.add(getName());
        return strs;
    }
    @Override
    public NCPFElement getRecipe(){
        return null;
    }
    @Override
    public void setRecipe(NCPFElement recipe){}
}
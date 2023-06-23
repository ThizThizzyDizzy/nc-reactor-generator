package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
public abstract class SimpleBlock extends Block{
    private static final Image UNSET = new Image(2, 2);
    static{
        UNSET.setRGB(0, 0, Color.BLACK.getRGB());
        UNSET.setRGB(1, 1, Color.BLACK.getRGB());
        UNSET.setRGB(0, 1, Color.MAGENTA.getRGB());
        UNSET.setRGB(1, 0, Color.MAGENTA.getRGB());
    }
    public SimpleBlock(Configuration configuration, int x, int y, int z){
        super(configuration, x, y, z);
    }
    @Override
    public void copyProperties(Block other){}
    @Override
    public Image getBaseTexture(){
        return getTexture();
    }
    @Override
    public Image getTexture(){
        return UNSET;
    }
    @Override
    public String getBaseName(){
        return getName();
    }
    @Override
    public void clearData(){}
    @Override
    public String getTooltip(Multiblock multiblock){
        return getName();
    }
    @Override
    public String getListTooltip(){
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
    public boolean hasRules(){
        return false;
    }
    @Override
    public boolean calculateRules(Multiblock multiblock){
        return true;
    }
    @Override
    public boolean matches(Block template){
        return template.getName().equals(getName());
    }
    @Override
    public boolean canRequire(Block other){
        return false;
    }
    @Override
    public boolean requires(Block other, Multiblock mb) {
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
    public Block copy(){
        return newInstance(x, y, z);
    }
    @Override
    public boolean isEqual(Block other){
        return matches(other);
    }
    @Override
    public void convertTo(Configuration to) throws MissingConfigurationEntryException {}
    @Override
    public boolean hasRecipes(){
        return false;
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
    
}
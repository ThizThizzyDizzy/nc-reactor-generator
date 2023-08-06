package net.ncplanner.plannerator.multiblock.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule;
public class Block extends AbstractBlock{
    public BlockElement template;
    public ActiveCoolerRecipe recipe;
    //fuel cell
    public int adjacentCells, adjacentModerators;
    public float energyMult, heatMult;
    //moderator
    public boolean moderatorValid;
    public boolean moderatorActive;
    //cooler
    public boolean coolerValid;
    boolean casingValid;//also for controllers
    public Block(NCPFConfigurationContainer configuration, int x, int y, int z, BlockElement template){
        super(configuration,x,y,z);
        if(template==null)throw new IllegalArgumentException("Cannot create null block!");
        this.template = template;
    }
    @Override
    public AbstractBlock newInstance(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, template);
    }
    @Override
    public void copyProperties(AbstractBlock other){}
    @Override
    public boolean isCore(){
        return isFuelCell()||isModerator();
    }
    public boolean isFuelCell(){
        return template.fuelCell!=null;
    }
    public boolean isModerator(){
        return template.moderator!=null;
    }
    public boolean isCooler(){
        return template.cooler!=null||(template.activeCooler!=null&&recipe!=null);
    }
    public boolean isCasing(){
        return template.casing!=null;
    }
    public boolean isController(){
        return template.controller!=null;
    }
    @Override
    public boolean isActive(){
        return isFuelCell()||moderatorActive||coolerValid||casingValid;
    }
    @Override
    public boolean isValid(){
        return isActive()||moderatorValid;
    }
    public int getCooling(){
        if(template.cooler!=null)return template.cooler.cooling;
        if(template.activeCooler!=null&&recipe!=null)return recipe.stats.cooling*getConfiguration().getConfiguration(UnderhaulSFRConfiguration::new).settings.activeCoolerRate/20;
        return 0;
    }
    @Override
    public void clearData(){
        adjacentCells = adjacentModerators = 0;
        energyMult = heatMult = 0;
        moderatorActive = coolerValid = moderatorValid = casingValid = false;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
        if(recipe!=null){
            tip+="\n"+recipe.getDisplayName();
        }
        if(isController())tip+="\nController "+(casingValid?"Valid":"Invalid");
        if(isCasing())tip+="\nCasing "+(casingValid?"Valid":"Invalid");
        if(isFuelCell()){
            tip+="\n"
            + " Adjacent Cells: "+adjacentCells+"\n"
            + " Adjacent Moderators: "+adjacentModerators+"\n"
            + " Energy Multiplier: "+MathUtil.percent(energyMult, 0)+"\n"
            + " Heat Multiplier: "+MathUtil.percent(heatMult, 0);
        }
        if(isModerator()){
            tip+="\nModerator "+(moderatorActive?"Active":(moderatorValid?"Valid":"Invalid"));
        }
        if(isCooler()){
            tip+="\nCooler "+(coolerValid?"Valid":"Invalid");
        }
        if(template.activeCooler!=null){
            tip+="\nActive Cooler "+(coolerValid?"Valid":"Invalid");
        }
        return tip;
    }
    @Override
    public void renderOverlay(Renderer renderer, float x, float y, float z, float width, float height, float depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        if(!isValid()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineInvalid(), faceRenderFunc);
        }
        if(isActive()&&isModerator()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineActive(), faceRenderFunc);
        }
    }
    @Override
    public List<PlacementRule> getRules(){
        if(template.cooler!=null)return template.cooler.rules;
        if(recipe!=null)return recipe.stats.rules;
        return new ArrayList<>();
    }
    @Override
    public boolean canRequire(AbstractBlock oth){
        if(template.cooler!=null||recipe!=null)return requires(oth, null);
        Block other = (Block) oth;
        if(template.fuelCell!=null||template.moderator!=null)return other.template.moderator!=null||other.template.fuelCell!=null;
        return false;
    }
    @Override
    public boolean canGroup(){
        return template.cooler!=null;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.cooler!=null;
    }
    @Override
    public boolean defaultEnabled(){
        return template.activeCooler==null;
    }
    @Override
    public Block copy(){
        Block copy = new Block(getConfiguration(), x, y, z, template);
        copy.adjacentCells = adjacentCells;
        copy.adjacentModerators = adjacentModerators;
        copy.energyMult = energyMult;
        copy.heatMult = heatMult;
        copy.moderatorValid = moderatorValid;
        copy.moderatorActive = moderatorActive;
        copy.coolerValid = coolerValid;
        return copy;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> searchables = template.getSearchableNames();
        for(String s : StringUtil.split(getListTooltip(), "\n"))searchables.add(s.trim());
        return searchables;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        return template.getSimpleSearchableNames();
    }
    @Override
    public BlockElement getTemplate(){
        return template;
    }
    @Override
    public boolean hasRecipes(){
        return template.activeCooler!=null;
    }
    @Override
    public List<? extends IBlockRecipe> getRecipes(){
        return template.activeCoolerRecipes;
    }
    @Override
    public ActiveCoolerRecipe getRecipe(){
        return recipe;
    }
    @Override
    public void setRecipe(NCPFElement recipe){
        if(template.activeCooler!=null)this.recipe = (ActiveCoolerRecipe)recipe;
        else throw new IllegalArgumentException("Tried to set block recipe, but this block can't have recipes!");
    }
    @Override
    public boolean isToggled(){
        return false;
    }
    @Override
    public void setToggled(boolean toggled){}
}
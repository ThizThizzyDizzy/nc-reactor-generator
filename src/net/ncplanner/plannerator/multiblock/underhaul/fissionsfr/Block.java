package net.ncplanner.plannerator.multiblock.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.multiblock.configuration.ITemplateAccess;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.graphics.image.Image;
public class Block extends net.ncplanner.plannerator.multiblock.Block implements ITemplateAccess<net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block> {
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block template;
    //fuel cell
    public int adjacentCells, adjacentModerators;
    public float energyMult, heatMult;
    //moderator
    public boolean moderatorValid;
    public boolean moderatorActive;
    //cooler
    public boolean coolerValid;
    boolean casingValid;//also for controllers
    public Block(Configuration configuration, int x, int y, int z, net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block template){
        super(configuration,x,y,z);
        if(template==null)throw new IllegalArgumentException("Cannot create null block!");
        this.template = template;
    }
    @Override
    public net.ncplanner.plannerator.multiblock.Block newInstance(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, template);
    }
    @Override
    public void copyProperties(net.ncplanner.plannerator.multiblock.Block other){}
    @Override
    public Image getBaseTexture(){
        return template.texture;
    }
    @Override
    public Image getTexture(){
        return template.displayTexture;
    }
    @Override
    public String getName(){
        return template.getDisplayName();
    }
    @Override
    public boolean isCore(){
        return isFuelCell()||isModerator();
    }
    public boolean isFuelCell(){
        return template.fuelCell;
    }
    public boolean isModerator(){
        return template.moderator;
    }
    public boolean isCooler(){
        if(template==null)return false;
        return template.cooling!=0;
    }
    public boolean isCasing(){
        return template.casing;
    }
    public boolean isController(){
        return template.controller;
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
        return template.active==null?template.cooling:(template.cooling*getConfiguration().underhaul.fissionSFR.activeCoolerRate/20);
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
        return tip;
    }
    @Override
    public String getListTooltip(){
        String tip = getName();
        if(isFuelCell())tip+="\nFuel Cell";
        if(isModerator())tip+="\nModerator";
        if(isCooler()){
            tip+="\nCooler"
                + "\nCooling: "+getCooling()+"H/t";
            if(template.active!=null)tip+="\nActive ("+template.active+")";
        }
        for(AbstractPlacementRule<PlacementRule.BlockType, net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block> rule : template.rules){
            tip+="\nRequires "+rule.toString();
        }
        return tip;
    }
    @Override
    public void renderOverlay(Renderer renderer, float x, float y, float width, float height, Multiblock multiblock){
        if(!isValid()){
            drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineInvalid());
        }
        if(isActive()&&isModerator()){
            drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineActive());
        }
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
    public boolean hasRules(){
        return !template.rules.isEmpty();
    }
    @Override
    public boolean calculateRules(Multiblock multiblock){
        for(AbstractPlacementRule<PlacementRule.BlockType, net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block> rule : template.rules){
            if(!rule.isValid(this, (UnderhaulSFR) multiblock)){
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean matches(net.ncplanner.plannerator.multiblock.Block template){
        if(template==null)return false;
        if(template instanceof Block){
            return ((Block) template).template==this.template;
        }
        return false;
    }
    @Override
    public boolean canRequire(net.ncplanner.plannerator.multiblock.Block oth){
        if(template.cooling!=0)return requires(oth, null);
        Block other = (Block) oth;
        if(template.fuelCell||template.moderator)return other.template.moderator||other.template.fuelCell;
//        if(template.casing)return other.template.casing;
        return false;
    }
    @Override
    public boolean requires(net.ncplanner.plannerator.multiblock.Block oth, Multiblock mb){
        if(template.cooling==0)return false;
        Block other = (Block) oth;
        int totalDist = Math.abs(oth.x-x)+Math.abs(oth.y-y)+Math.abs(oth.z-z);
        if(totalDist>1)return false;//too far away
        if(hasRules()){
            for(AbstractPlacementRule<PlacementRule.BlockType, net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block> rule : template.rules){
                if(ruleHas((PlacementRule) rule, other))return true;
            }
        }
        return false;
    }
    private boolean ruleHas(PlacementRule rule, Block b){
        if(rule.block==b.template)return true;
        if(rule.blockType!=null&&rule.blockType.blockMatches(null, b))return true;
        for(AbstractPlacementRule<PlacementRule.BlockType, net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block> rul : rule.rules){
            if(ruleHas((PlacementRule) rul, b))return true;
        }
        return false;
    }
    @Override
    public boolean canGroup(){
        return template.cooling!=0;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.cooling!=0;
    }
    @Override
    public boolean defaultEnabled(){
        return template.active==null;
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
    public boolean isEqual(net.ncplanner.plannerator.multiblock.Block other){
        return other instanceof Block&&((Block)other).template==template;
    }
    @Override
    public void convertTo(Configuration to) throws MissingConfigurationEntryException{
        template = to.underhaul.fissionSFR.convert(template);
        configuration = to;
    }
    @Override
    public boolean shouldRenderFace(net.ncplanner.plannerator.multiblock.Block against){
        if(super.shouldRenderFace(against))return true;
        if(template==((Block)against).template)return false;
        return Core.hasAlpha(against.getBaseTexture());
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
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block getTemplate() {
        return template;
    }
    @Override
    public String getPinnedName(){
        return template.getPinnedName();
    }
    @Override
    public boolean hasRecipes(){
        return false;
    }
    @Override
    public ArrayList<? extends IBlockRecipe> getRecipes(){
        return new ArrayList<>();
    }
}
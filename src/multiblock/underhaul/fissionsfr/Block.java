package multiblock.underhaul.fissionsfr;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Function;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import planner.Core;
import planner.exception.MissingConfigurationEntryException;
public class Block extends multiblock.Block{
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public multiblock.configuration.underhaul.fissionsfr.Block template;
    //fuel cell
    public int adjacentCells, adjacentModerators;
    public float energyMult, heatMult;
    //moderator
    public boolean moderatorValid;
    public boolean moderatorActive;
    //cooler
    public boolean coolerValid;
    boolean casingValid;//also for controllers
    public Block(Configuration configuration, int x, int y, int z, multiblock.configuration.underhaul.fissionsfr.Block template){
        super(configuration,x,y,z);
        if(template==null)throw new IllegalArgumentException("Cannot create null block!");
        this.template = template;
    }
    @Override
    public multiblock.Block newInstance(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, template);
    }
    @Override
    public void copyProperties(multiblock.Block other){}
    @Override
    public BufferedImage getBaseTexture(){
        return template.texture;
    }
    @Override
    public BufferedImage getTexture(){
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
                    + " Energy Multiplier: "+percent(energyMult, 0)+"\n"
                    + " Heat Multiplier: "+percent(heatMult, 0);
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
        for(PlacementRule rule : template.rules){
            tip+="\nRequires "+rule.toString();
        }
        return tip;
    }
    @Override
    public void renderOverlay(double x, double y, double width, double height, Multiblock multiblock){
        if(!isValid()){
            drawOutline(x, y, width, height, Core.theme.getRed());
        }
        if(isActive()&&isModerator()){
            drawOutline(x, y, width, height, Core.theme.getGreen());
        }
    }
    @Override
    public void renderOverlay(double x, double y, double z, double width, double height, double depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        if(!isValid()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getRed(), faceRenderFunc);
        }
        if(isActive()&&isModerator()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getGreen(), faceRenderFunc);
        }
    }
    @Override
    public boolean hasRules(){
        return !template.rules.isEmpty();
    }
    @Override
    public boolean calculateRules(Multiblock multiblock){
        for(PlacementRule rule : template.rules){
            if(!rule.isValid(this, (UnderhaulSFR) multiblock)){
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean matches(multiblock.Block template){
        if(template==null)return false;
        if(template instanceof Block){
            return ((Block) template).template==this.template;
        }
        return false;
    }
    @Override
    public boolean requires(multiblock.Block oth, Multiblock mb){
        if(template.cooling==0)return false;
        Block other = (Block) oth;
        int totalDist = Math.abs(oth.x-x)+Math.abs(oth.y-y)+Math.abs(oth.z-z);
        if(totalDist>1)return false;//too far away
        if(hasRules()){
            for(PlacementRule rule : template.rules){
                if(ruleHas(rule, other))return true;
            }
        }
        return false;
    }
    private boolean ruleHas(PlacementRule rule, Block b){
        if(rule.block==b.template)return true;
        for(PlacementRule rul : rule.rules){
            if(ruleHas(rul, b))return true;
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
    public boolean isEqual(multiblock.Block other){
        return other instanceof Block&&((Block)other).template==template;
    }
    @Override
    public void convertTo(Configuration to) throws MissingConfigurationEntryException{
        template = to.underhaul.fissionSFR.convert(template);
        configuration = to;
    }
    @Override
    public boolean shouldRenderFace(multiblock.Block against){
        if(super.shouldRenderFace(against))return true;
        if(template==((Block)against).template)return false;
        return Core.hasAlpha(against.getBaseTexture());
    }
    @Override
    public Iterable<String> getSearchableNames(){
        ArrayList<String> searchables = template.getSearchableNames();
        for(String s : getListTooltip().split("\n"))searchables.add(s.trim());
        return searchables;
    }
}
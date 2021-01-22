package multiblock.underhaul.fissionsfr;
import java.awt.Color;
import java.awt.image.BufferedImage;
import planner.Core;
import multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import simplelibrary.Queue;
public class Block extends multiblock.Block{
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public multiblock.configuration.underhaul.fissionsfr.Block template;
    //fuel cell
    public int adjacentCells, adjacentModerators;
    public float energyMult, heatMult;
    //moderator
    private boolean moderatorValid;
    private boolean moderatorActive;
    //cooler
    private boolean coolerValid;
    public Block(Configuration configuration, int x, int y, int z, multiblock.configuration.underhaul.fissionsfr.Block template){
        super(configuration,x,y,z);
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
        return isCasing()?"Casing":template.name;
    }
    @Override
    public boolean isCore(){
        return isFuelCell()||isModerator();
    }
    @Override
    public boolean isCasing(){
        return template==null;
    }
    public boolean isFuelCell(){
        if(template==null)return false;
        return template.fuelCell;
    }
    public boolean isModerator(){
        if(template==null)return false;
        return template.moderator;
    }
    public boolean isCooler(){
        if(template==null)return false;
        return template.cooling!=0;
    }
    @Override
    public boolean isActive(){
        return isCasing()||isFuelCell()||moderatorActive||coolerValid;
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
        moderatorActive = moderatorValid = false;
    }
    public void calculateCore(UnderhaulSFR reactor){
        if(!template.fuelCell)return;
        for(Direction d : directions){
            Queue<Block> toValidate = new Queue<>();
            for(int i = 1; i<=reactor.getConfiguration().underhaul.fissionSFR.neutronReach+1; i++){
                Block block = reactor.getBlock(x+d.x*i,y+d.y*i,z+d.z*i);
                if(block==null)break;
                if(block.isModerator()){
                    if(i==1){
                        block.moderatorActive = block.moderatorValid = true;
                        adjacentModerators++;
                    }
                    toValidate.enqueue(block);
                    continue;
                }
                if(block.isFuelCell()){
                    for(Block b : toValidate){
                        b.moderatorValid = true;
                    }
                    adjacentCells++;
                    break;
                }
                break;
            }
        }
        float baseEff = energyMult = adjacentCells+1;
        heatMult = (baseEff*(baseEff+1))/2;
        energyMult+=baseEff/6*reactor.getConfiguration().underhaul.fissionSFR.moderatorExtraPower*adjacentModerators;
        heatMult+=baseEff/6*reactor.getConfiguration().underhaul.fissionSFR.moderatorExtraHeat*adjacentModerators;
    }
    /**
     * Calculates the cooler
     * @param reactor the reactor
     * @return <code>true</code> if the cooler state has changed
     */
    public boolean calculateCooler(UnderhaulSFR reactor){
        if(template.cooling==0)return false;
        boolean wasValid = coolerValid;
        for(PlacementRule rule : template.rules){
            if(!rule.isValid(this, reactor)){
                coolerValid = false;
                return wasValid!=coolerValid;
            }
        }
        coolerValid = true;
        return wasValid!=coolerValid;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
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
    public void renderOverlay(double x, double y, double z, double width, double height, double depth, Multiblock multiblock){
        if(!isValid()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getRed());
        }
        if(isActive()&&isModerator()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getGreen());
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
    public void convertTo(Configuration to){
        template = to.underhaul.fissionSFR.convert(template);
        configuration = to;
    }
}
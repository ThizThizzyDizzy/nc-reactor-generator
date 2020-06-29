package planner.multiblock.underhaul.fissionsfr;
import java.awt.image.BufferedImage;
import planner.Core;
import planner.configuration.underhaul.fissionsfr.PlacementRule;
import planner.multiblock.Direction;
import simplelibrary.Queue;
public class Block extends planner.multiblock.Block{
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public planner.configuration.underhaul.fissionsfr.Block template;
    //fuel cell
    public int adjacentCells, adjacentModerators;
    public float energyMult, heatMult;
    //moderator
    private boolean moderatorValid;
    private boolean moderatorActive;
    //cooler
    private boolean coolerValid;
    public Block(int x, int y, int z, planner.configuration.underhaul.fissionsfr.Block template){
        super(x,y,z);
        this.template = template;
    }
    @Override
    public planner.multiblock.Block newInstance(int x, int y, int z){
        return new Block(x, y, z, template);
    }
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
        return template.name;
    }
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
    public int getCooling(){
        return template.cooling;
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
            for(int i = 1; i<=Core.configuration.underhaul.fissionSFR.neutronReach+1; i++){
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
        energyMult+=baseEff/6*Core.configuration.underhaul.fissionSFR.moderatorExtraPower*adjacentModerators;
        heatMult+=baseEff/6*Core.configuration.underhaul.fissionSFR.moderatorExtraHeat*adjacentModerators;
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
    public String getTooltip(){
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
}
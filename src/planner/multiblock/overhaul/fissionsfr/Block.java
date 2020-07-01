package planner.multiblock.overhaul.fissionsfr;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import planner.Core;
import planner.configuration.overhaul.fissionsfr.PlacementRule;
import planner.multiblock.Direction;
import planner.multiblock.Multiblock;
import simplelibrary.Queue;
public class Block extends planner.multiblock.Block{
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public planner.configuration.overhaul.fissionsfr.Block template;
    public planner.configuration.overhaul.fissionsfr.Fuel fuel;
    public planner.configuration.overhaul.fissionsfr.Source source;
    public planner.configuration.overhaul.fissionsfr.IrradiatorRecipe recipe;
    private boolean hasPropogated = false;
    public int moderatorLines;
    public int neutronFlux;
    public float positionalEfficiency;
    public boolean moderatorValid = false;
    public boolean moderatorActive = false;
    public boolean heatsinkValid = false;
    public boolean reflectorActive;
    public boolean shieldActive;
    public float efficiency;
    public boolean inCluster;
    public Block(int x, int y, int z, planner.configuration.overhaul.fissionsfr.Block template){
        super(x, y, z);
        this.template = template;
    }
    @Override
    public planner.multiblock.Block newInstance(int x, int y, int z){
        return new Block(x, y, z, template);
    }
    @Override
    public void copyProperties(planner.multiblock.Block other){
        ((Block)other).fuel = fuel;
        ((Block)other).source = source;
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
    @Override
    public void clearData(){
        hasPropogated = false;
        positionalEfficiency = efficiency = moderatorLines = neutronFlux = 0;
        moderatorValid = moderatorActive = heatsinkValid = reflectorActive = shieldActive = inCluster = false;
    }
    @Override
    public String getTooltip(){
        String tip = getName();
        if(isFuelCell()){
            tip+="\nFuel: "+fuel.name+"\n"
                    + "Fuel Cell "+(isFuelCellActive()?"Active":"Inactive");
            if(isFuelCellActive()){
                tip+="\nAdjacent moderator lines: "+moderatorLines+"\n"
                        + "Heat Multiplier: "+percent(moderatorLines, 0)+"\n"
                        + "Heat Produced: "+moderatorLines*fuel.heat+" H/t\n"
                        + "Efficiency: "+percent(efficiency, 0)+"\n"
                        + "Positional Efficiency: "+percent(positionalEfficiency, 0)+"\n"
                        + "Total Neutron Flux: "+neutronFlux+"\n"
                        + "Criticality Factor: "+fuel.criticality;
            }else{
                tip+="\nTotal Neutron Flux: "+neutronFlux+"\n"
                        + "Criticality Factor: "+fuel.criticality;
            }
            if(isPrimed()){
                tip+="\n"
                        + "Primed\n"
                        + "Neutron source: "+(source==null?"Self":source.name);
            }
        }
        if(isModerator()){
            tip+="\nModerator "+(moderatorActive?"Active":(moderatorValid?"Valid":"Invalid"))+"\n"
                    + "Flux Factor: "+template.flux+"\n"
                    + "Efficiency Factor: "+template.efficiency;
        }
        if(isReflector()){
            tip+="\nReflector "+(reflectorActive?"Active":"Inactive")+"\n"
                    + "Reflectivity: "+template.reflectivity+"\n"
                    + "Efficiency multiplier: "+template.efficiency;
        }
        if(isShield()){
            tip+="\nShield "+(shieldActive?"Valid":"Invalid")+"\n"
                    + "Total flux: "+neutronFlux+"\n"
                    + "Heat per flux: "+template.heatMult+"\n"
                    + "Total heat: "+(neutronFlux*template.heatMult)+"\n"
                    + "Efficiency factor: "+template.efficiency;
        }
        if(isIrradiator()){
            tip+="\nIrradiator flux: "+neutronFlux+"\n";
            if(recipe!=null){
                tip+="Heat per flux: "+recipe.heat+"\n"
                        + "Total heat: "+recipe.heat*neutronFlux;
            }
        }
        if(isHeatsink()){
            tip+="\nHeatsink "+(heatsinkValid?"Valid":"Invalid");
        }
        return tip;
    }
    @Override
    public String getListTooltip(){
        String tip = getName();
        if(isFuelCell())tip+="\nFuel Cell";
        if(isModerator())tip+="\nModerator"
                + "\nFlux: "+template.flux
                + "\nEfficiency: "+template.efficiency;
        if(isReflector())tip+="\nReflector"
                + "\nReflectivity: "+template.reflectivity
                + "\nEfficiency: "+template.efficiency;
        if(isShield())tip+="\nNeutron Shield"
                + "\nHeat per flux: "+template.heatMult
                + "\nEfficiency: "+template.efficiency;
        if(isIrradiator())tip+="\nIrradiator";
        if(isHeatsink())tip+="\nHeatsink"
                + "\nCooling: "+template.cooling+" H/t";
        for(PlacementRule rule : template.rules){
            tip+="\nRequires "+rule.toString();
        }
        return tip;
    }
    public boolean isPrimed(){
        if(!isFuelCell())return false;
        return source!=null||fuel.selfPriming;
    }
    public boolean isCasing(){
        return template==null;
    }
    @Override
    public boolean isActive(){
        return isCasing()||isConductor()||isModeratorActive()||isFuelCellActive()||isHeatsinkActive()||isIrradiatorActive()||reflectorActive||isShieldActive();
    }
    public boolean isValid(){
        return isInert()||isConductor()||isModeratorActive()||moderatorValid||isFuelCellActive()||isHeatsinkActive()||isIrradiatorActive()||reflectorActive||isShieldActive();
    }
    public boolean isModeratorActive(){
        return isModerator()&&moderatorActive&&template.activeModerator;
    }
    public boolean isFuelCellActive(){
        return isFuelCell()&&neutronFlux>=fuel.criticality;
    }
    public boolean isFuelCell(){
        if(isCasing())return false;
        return template.fuelCell;
    }
    public boolean isModerator(){
        if(isCasing())return false;
        return template.moderator;
    }
    public boolean isReflector(){
        if(isCasing())return false;
        return template.reflector;
    }
    public boolean isIrradiator(){
        if(isCasing())return false;
        return template.irradiator;
    }
    public boolean isIrradiatorActive(){
        return isIrradiator()&&neutronFlux>0;
    }
    public boolean isShield(){
        if(isCasing())return false;
        return template.shield;
    }
    public boolean isShieldActive(){
        return isShield()&&shieldActive;
    }
    public boolean isHeatsink(){
        if(isCasing())return false;
        return template.cooling>0;
    }
    public boolean isHeatsinkActive(){
        return isHeatsink()&&heatsinkValid;
    }
    public boolean isConductor(){
        if(isCasing())return false;
        return template.conductor;
    }
    public boolean isFunctional(){
        if(isCasing())return false;
        if(canCluster()&&!inCluster)return false;
        return template.functional&&(isActive()||moderatorValid);
    }
    public void propogateNeutronFlux(OverhaulSFR reactor){
        if(!isFuelCell())return;
        if(!isPrimed()&&neutronFlux<fuel.criticality)return;
        if(hasPropogated)return;
        hasPropogated = true;
        for(Direction d : directions){
            int flux = 0;
            int length = 0;
            float efficiency = 0;
            for(int i = 1; i<=Core.configuration.overhaul.fissionSFR.neutronReach+1; i++){
                Block block = reactor.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(block==null)break;
                if(block.isCasing())break;
                if(block.isModerator()){
                    flux+=block.template.flux;
                    efficiency+=block.template.efficiency;
                    length++;
                    continue;
                }
                if(block.isFuelCell()){
                    if(flux==0)break;
                    block.neutronFlux+=flux;
                    block.moderatorLines++;
                    block.positionalEfficiency+=efficiency/length;
                    block.propogateNeutronFlux(reactor);
                    break;
                }
                if(block.isReflector()){
                    if(flux==0)break;
                    neutronFlux+=flux*2*block.template.reflectivity;
                    positionalEfficiency+=efficiency/length*block.template.efficiency;
                    moderatorLines++;
                    break;
                }
                if(block.isIrradiator()){
                    if(flux==0)break;
                    moderatorLines++;
                    break;
                }
                break;
            }
        }
    }
    public void postFluxCalc(OverhaulSFR reactor){
        if(!isFuelCellActive())return;
        for(Direction d : directions){
            int flux = 0;
            HashMap<Block, Integer> shieldFluxes = new HashMap<>();
            Queue<Block> toActivate = new Queue<>();
            Queue<Block> toValidate = new Queue<>();
            for(int i = 1; i<=Core.configuration.overhaul.fissionSFR.neutronReach+1; i++){
                Block block = reactor.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(block==null)break;
                boolean skip = false;
                if(block.isModerator()){
                    flux+=block.template.flux;
                    if(i==1)toActivate.enqueue(block);
                    toValidate.enqueue(block);
                    skip = true;
                }
                if(block.isShield()){
                    block.shieldActive = true;
                    shieldFluxes.put(block, flux);
                    skip = true;
                }
                if(skip)continue;
                if(block.isFuelCell()){
                    if(flux==0)break;
                    for(Block b : shieldFluxes.keySet()){
                        b.neutronFlux+=shieldFluxes.get(b);
                    }
                    for(Block b : toActivate)b.moderatorActive = true;
                    for(Block b : toValidate)b.moderatorValid = true;
                    break;
                }
                if(block.isReflector()){
                    if(flux==0)break;
                    block.reflectorActive = true;
                    for(Block b : shieldFluxes.keySet()){
                        b.neutronFlux+=shieldFluxes.get(b)*(1+block.template.reflectivity);
                    }
                    for(Block b : toActivate)b.moderatorActive = true;
                    for(Block b : toValidate)b.moderatorValid = true;
                    break;
                }
                if(block.isIrradiator()){
                    if(flux==0)break;
                    for(Block b : shieldFluxes.keySet()){
                        b.neutronFlux+=shieldFluxes.get(b);
                    }
                    block.neutronFlux+=flux;
                    for(Block b : toActivate)b.moderatorActive = true;
                    for(Block b : toValidate)b.moderatorValid = true;
                    break;
                }
                break;
            }
        }
        hasPropogated = true;
    }
    /**
     * Calculates the heatsink
     * @param reactor the reactor
     * @return <code>true</code> if the heatsink state has changed
     */
    public boolean calculateHeatsink(OverhaulSFR reactor){
        if(template.cooling==0)return false;
        boolean wasValid = heatsinkValid;
        for(PlacementRule rule : template.rules){
            if(!rule.isValid(this, reactor)){
                heatsinkValid = false;
                return wasValid!=heatsinkValid;
            }
        }
        heatsinkValid = true;
        return wasValid!=heatsinkValid;
    }
    public boolean canCluster(){
        if(isCasing())return false;
        return isActive()&&template.cluster;
    }
    @Override
    public void renderOverlay(double x, double y, double width, double height){
        if(!isValid()){
            drawOutline(x, y, width, height, 1/32d, Core.theme.getRed());
        }
        if(isModeratorActive()){
            drawOutline(x, y, width, height, 1/32d, Core.theme.getGreen());
        }
        boolean self = fuel!=null&&fuel.selfPriming;
        if(source!=null||self){
            float fac = self?1:(float) Math.pow(source.efficiency, 10);
            float r = self?0:Math.min(1, -2*fac+2);
            float g = self?0:Math.min(1, fac*2);
            float b = self?1:0;
            drawCircle(x+width/2, y+height/2, width*(4/16d), width*(6/16d), Core.theme.getRGB(r, g, b));
        }
    }
    private boolean isInert(){
        if(isCasing())return true;
        return template.cluster&&!template.functional;
    }
    @Override
    public boolean hasRules(){
        return !template.rules.isEmpty();
    }
    @Override
    public boolean calculateRules(Multiblock multiblock){
        for(PlacementRule rule : template.rules){
            if(!rule.isValid(this, (OverhaulSFR) multiblock)){
                return false;
            }
        }
        return true;
    }
}
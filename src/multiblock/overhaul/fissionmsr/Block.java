package multiblock.overhaul.fissionmsr;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import planner.Core;
import planner.configuration.overhaul.fissionmsr.PlacementRule;
import multiblock.Direction;
import multiblock.Multiblock;
import simplelibrary.Queue;
public class Block extends multiblock.Block{
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public planner.configuration.overhaul.fissionmsr.Block template;
    public planner.configuration.overhaul.fissionmsr.Fuel fuel;
    public planner.configuration.overhaul.fissionmsr.Source source;
    public planner.configuration.overhaul.fissionmsr.IrradiatorRecipe recipe;
    private boolean hasPropogated = false;
    public int moderatorLines;
    public int neutronFlux;
    public float positionalEfficiency;
    public boolean moderatorValid = false;
    public boolean moderatorActive = false;
    public boolean heaterValid = false;
    public boolean reflectorActive;
    public boolean shieldActive;
    public float efficiency;
    public boolean inCluster;
    public boolean closed = false;
    public Block(int x, int y, int z, planner.configuration.overhaul.fissionmsr.Block template){
        super(x, y, z);
        this.template = template;
    }
    @Override
    public multiblock.Block newInstance(int x, int y, int z){
        return new Block(x, y, z, template);
    }
    @Override
    public void copyProperties(multiblock.Block other){
        ((Block)other).fuel = fuel;
        ((Block)other).source = source;
    }
    @Override
    public BufferedImage getBaseTexture(){
        return closed?template.closedTexture:template.texture;
    }
    @Override
    public BufferedImage getTexture(){
        return closed?template.closedDisplayTexture:template.displayTexture;
    }
    @Override
    public String getName(){
        return template.name;
    }
    @Override
    public void clearData(){
        hasPropogated = false;
        positionalEfficiency = efficiency = moderatorLines = neutronFlux = 0;
        moderatorValid = moderatorActive = heaterValid = reflectorActive = shieldActive = inCluster = false;
    }
    @Override
    public String getTooltip(){
        String tip = getName();
        if(isFuelVessel()){
            tip+="\nFuel: "+fuel.name+"\n"
                    + "Fuel Vessel "+(isFuelVesselActive()?"Active":"Inactive");
            if(isFuelVesselActive()){
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
        if(closed){
            tip+="\nClosed";
        }
        if(isIrradiator()){
            tip+="\nIrradiator flux: "+neutronFlux+"\n";
            if(recipe!=null){
                tip+="Heat per flux: "+recipe.heat+"\n"
                        + "Total heat: "+recipe.heat*neutronFlux;
            }
        }
        if(isHeater()){
            tip+="\nHeater "+(heaterValid?"Valid":"Invalid");
        }
        return tip;
    }
    @Override
    public String getListTooltip(){
        String tip = getName();
        if(isFuelVessel())tip+="\nFuel Vessel";
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
        if(isHeater())tip+="\nHeater"
                + "\nCooling: "+template.cooling+" H/t";
        for(PlacementRule rule : template.rules){
            tip+="\nRequires "+rule.toString();
        }
        return tip;
    }
    public boolean isPrimed(){
        if(!isFuelVessel())return false;
        return source!=null||fuel.selfPriming;
    }
    public boolean isCasing(){
        return template==null;
    }
    @Override
    public boolean isActive(){
        return isCasing()||isConductor()||isModeratorActive()||isFuelVesselActive()||isHeaterActive()||isIrradiatorActive()||reflectorActive||isShieldActive();
    }
    public boolean isValid(){
        return isInert()||isConductor()||isModeratorActive()||moderatorValid||isFuelVesselActive()||isHeaterActive()||isIrradiatorActive()||reflectorActive||isShieldActive();
    }
    public boolean isModeratorActive(){
        return isModerator()&&moderatorActive&&template.activeModerator;
    }
    public boolean isFuelVesselActive(){
        return isFuelVessel()&&neutronFlux>=fuel.criticality;
    }
    public boolean isFuelVessel(){
        if(isCasing())return false;
        return template.fuelVessel;
    }
    public boolean isModerator(){
        if(closed)return false;
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
        if(closed)return false;
        if(isCasing())return false;
        return template.shield;
    }
    public boolean isShieldActive(){
        return isShield()&&shieldActive&&moderatorValid;
    }
    public boolean isHeater(){
        if(isCasing())return false;
        return template.cooling>0;
    }
    public boolean isHeaterActive(){
        return isHeater()&&heaterValid;
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
    public void propogateNeutronFlux(OverhaulMSR reactor){
        if(!isFuelVessel())return;
        if(!isPrimed()&&neutronFlux<fuel.criticality)return;
        if(hasPropogated)return;
        hasPropogated = true;
        for(Direction d : directions){
            int flux = 0;
            int length = 0;
            float efficiency = 0;
            for(int i = 1; i<=Core.configuration.overhaul.fissionMSR.neutronReach+1; i++){
                Block block = reactor.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(block==null)break;
                if(block.isCasing())break;
                if(block.isModerator()){
                    flux+=block.template.flux;
                    efficiency+=block.template.efficiency;
                    length++;
                    continue;
                }
                if(block.isFuelVessel()){
                    if(length==0)break;
                    block.neutronFlux+=flux;
                    block.moderatorLines++;
                    block.positionalEfficiency+=efficiency/length;
                    block.propogateNeutronFlux(reactor);
                    break;
                }
                if(block.isReflector()){
                    if(length==0)break;
                    neutronFlux+=flux*2*block.template.reflectivity;
                    positionalEfficiency+=efficiency/length*block.template.efficiency;
                    moderatorLines++;
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0)break;
                    moderatorLines++;
                    break;
                }
                break;
            }
        }
    }
    public void postFluxCalc(OverhaulMSR reactor){
        if(!isFuelVesselActive())return;
        for(Direction d : directions){
            int flux = 0;
            int length = 0;
            HashMap<Block, Integer> shieldFluxes = new HashMap<>();
            Queue<Block> toActivate = new Queue<>();
            Queue<Block> toValidate = new Queue<>();
            for(int i = 1; i<=Core.configuration.overhaul.fissionMSR.neutronReach+1; i++){
                Block block = reactor.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(block==null)break;
                boolean skip = false;
                if(block.isModerator()){
                    length++;
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
                if(block.isFuelVessel()){
                    if(length==0)break;
                    for(Block b : shieldFluxes.keySet()){
                        b.neutronFlux+=shieldFluxes.get(b);
                    }
                    for(Block b : toActivate)b.moderatorActive = true;
                    for(Block b : toValidate)b.moderatorValid = true;
                    break;
                }
                if(block.isReflector()){
                    if(length==0)break;
                    block.reflectorActive = true;
                    for(Block b : shieldFluxes.keySet()){
                        b.neutronFlux+=shieldFluxes.get(b)*(1+block.template.reflectivity);
                    }
                    for(Block b : toActivate)b.moderatorActive = true;
                    for(Block b : toValidate)b.moderatorValid = true;
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0)break;
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
     * Calculates the heater
     * @param reactor the reactor
     * @return <code>true</code> if the heater state has changed
     */
    public boolean calculateHeater(OverhaulMSR reactor){
        if(template.cooling==0)return false;
        boolean wasValid = heaterValid;
        for(PlacementRule rule : template.rules){
            if(!rule.isValid(this, reactor)){
                heaterValid = false;
                return wasValid!=heaterValid;
            }
        }
        heaterValid = true;
        return wasValid!=heaterValid;
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
            drawCircle(x, y, width, height, Core.theme.getRGB(r, g, b));
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
            if(!rule.isValid(this, (OverhaulMSR) multiblock)){
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
        if(template.moderator)return false;
        return template.cooling>0;
    }
    public multiblock.overhaul.fissionsfr.Block convertToSFR(){
        multiblock.overhaul.fissionsfr.Block b = new multiblock.overhaul.fissionsfr.Block(x, y, z, Core.configuration.overhaul.fissionSFR.convertToSFR(template));
        b.fuel = Core.configuration.overhaul.fissionSFR.convertToSFR(fuel);
        b.source = Core.configuration.overhaul.fissionSFR.convertToSFR(source);
        return b;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.cooling>0;
    }
}
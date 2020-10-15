package multiblock.overhaul.fissionmsr;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import planner.Core;
import multiblock.configuration.overhaul.fissionmsr.PlacementRule;
import multiblock.Direction;
import multiblock.Multiblock;
import simplelibrary.Queue;
import simplelibrary.opengl.Renderer2D;
public class Block extends multiblock.Block{
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public multiblock.configuration.overhaul.fissionmsr.Block template;
    public multiblock.configuration.overhaul.fissionmsr.Fuel fuel;
    public multiblock.configuration.overhaul.fissionmsr.Source source;
    public multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe irradiatorRecipe;
    private boolean hasPropogated = false;
    public int flux;
    public boolean moderatorValid = false;
    public boolean moderatorActive = false;
    public boolean heaterValid = false;
    public boolean reflectorActive;
    public boolean shieldActive;
    public float efficiency;
    public OverhaulMSR.Cluster cluster;
    public boolean closed = false;
    public OverhaulMSR.VesselGroup vesselGroup;//only used when recalculating; doesn't even need to be reset
    public Block(int x, int y, int z, multiblock.configuration.overhaul.fissionmsr.Block template){
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
        ((Block)other).irradiatorRecipe = irradiatorRecipe;
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
        return isCasing()?"Casing":template.name;
    }
    @Override
    public void clearData(){
        hasPropogated = false;
        efficiency = flux = 0;
        moderatorValid = moderatorActive = heaterValid = reflectorActive = shieldActive = false;
        cluster = null;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
        if(isFuelVessel()){
            tip+="\nFuel: "+fuel.name+"\n";
            if(vesselGroup==null){
                tip +="Fuel Vessel "+(isFuelVesselActive()?"Active":"Inactive");
            }else{
                tip +="Vessel Group "+(isFuelVesselActive()?"Active":"Inactive")+" ("+vesselGroup.size()+" Vessels)";
                if(isFuelVesselActive()){
                    tip+="\nAdjacent moderator lines: "+vesselGroup.moderatorLines+"\n"
                            + "Open Faces: "+vesselGroup.getOpenFaces()+"\n"
                            + "Heat Multiplier: "+percent(vesselGroup.getHeatMult()/vesselGroup.size(), 0)+"\n"
                            + "Heat Produced: "+vesselGroup.getHeatMult()*fuel.heat+"H/t\n"
                            + "Efficiency: "+percent(efficiency/vesselGroup.size(), 0)+"\n"
                            + "Positional Efficiency: "+percent(vesselGroup.positionalEfficiency/vesselGroup.size(), 0)+"\n"
                            + "Total Neutron Flux: "+vesselGroup.neutronFlux+"\n"
                            + "Criticality Factor: "+vesselGroup.criticality;
                }else{
                    tip+="\nTotal Neutron Flux: "+vesselGroup.neutronFlux+"\n"
                            + "Criticality Factor: "+vesselGroup.criticality;
                }
            }
            if(isPrimed()){
                tip+="\n"
                        + "Vessel Primed\n"
                        + "Neutron source: "+(source==null?"Self":source.name);
            }
            if(vesselGroup!=null){
                if(vesselGroup.isPrimed()){
                    tip+="\nVessel Group Primed";
                }
                tip+="\nNeutron Sources: "+vesselGroup.getSources()+"/"+vesselGroup.getRequiredSources();
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
                    + "Total flux: "+flux+"\n"
                    + "Heat per flux: "+template.heatMult+"\n"
                    + "Total heat: "+(flux*template.heatMult)+"\n H/t"
                    + "Efficiency factor: "+template.efficiency;
        }
        if(closed){
            tip+="\nClosed";
        }
        if(isIrradiator()){
            if(irradiatorRecipe!=null){
                tip+="\nRecipe: "+irradiatorRecipe.name;
            }
            tip+="\nIrradiator flux: "+flux+"\n";
            if(irradiatorRecipe!=null){
                tip+="Heat per flux: "+irradiatorRecipe.heat+"\n"
                        + "Total heat: "+irradiatorRecipe.heat*flux+"H/t";
            }
        }
        if(isHeater()){
            tip+="\nHeater "+(heaterValid?"Valid":"Invalid");
        }
        OverhaulMSR.Cluster cluster = this.cluster;
        if(cluster!=null){
            if(!cluster.isCreated()){
                tip+="\nInvalid cluster!";
            }
            if(!cluster.isConnectedToWall){
                tip+="\nCluster is not connected to the casing!";
            }
            if(cluster.netHeat>0){
                tip+="\nCluster is heat-positive!";
            }
            if(cluster.coolingPenaltyMult<1){
                tip+="\nCluster is penalized for overcooling!";
            }
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
                + "\nCooling: "+template.cooling+"H/t";
        for(PlacementRule rule : template.rules){
            tip+="\nRequires "+rule.toString();
        }
        return tip;
    }
    public boolean isPrimed(){
        if(!isFuelVessel())return false;
        return source!=null||fuel.selfPriming;
    }
    @Override
    public boolean isCore(){//maybe I shouldn't specifically blacklist heaters...
        return (isModerator()&&!isHeater())||isFuelVessel()||isShield()||isIrradiator()||isReflector();
    }
    public boolean isCasing(){
        return template==null;
    }
    @Override
    public boolean isActive(){
        return isCasing()||isConductor()||isModeratorActive()||isFuelVesselActive()||isHeaterActive()||isIrradiatorActive()||reflectorActive||isShieldActive();
    }
    @Override
    public boolean isValid(){
        return isInert()||isActive()||moderatorValid;
    }
    public boolean isModeratorActive(){
        return isModerator()&&moderatorActive&&template.activeModerator;
    }
    public boolean isFuelVesselActive(){
        return isFuelVessel()&&(vesselGroup==null?false:vesselGroup.neutronFlux>=vesselGroup.criticality);
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
        return isIrradiator()&&flux>0;
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
        return template.cooling!=0;
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
        if(canCluster()&&cluster==null)return false;
        return template.functional&&(isActive()||moderatorValid);
    }
    public void propogateNeutronFlux(OverhaulMSR reactor){
        if(!isFuelVessel())return;
        if(hasPropogated)return;
        if(!vesselGroup.isPrimed()&&vesselGroup.neutronFlux<vesselGroup.criticality)return;
        hasPropogated = true;
        for(Direction d : directions){
            int flux = 0;
            int length = 0;
            float efficiency = 0;
            int nonshields = 0;
            for(int i = 1; i<=Core.configuration.overhaul.fissionMSR.neutronReach+1; i++){
                Block block = reactor.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(block==null)break;
                if(block.isCasing())break;
                if(block.isModerator()){
                    flux+=block.template.flux;
                    efficiency+=block.template.efficiency;
                    length++;
                    if(!block.isShield()){
                        nonshields++;
                    }
                    continue;
                }
                if(block.isFuelVessel()){
                    if(length==0||nonshields==0)break;
                    block.vesselGroup.neutronFlux+=flux;
                    block.vesselGroup.moderatorLines++;
                    block.vesselGroup.positionalEfficiency+=efficiency/length;
                    block.vesselGroup.propogateNeutronFlux(reactor);
                    break;
                }
                if(block.isReflector()){
                    if(length==0||nonshields==0)break;
                    if(length>Core.configuration.overhaul.fissionMSR.neutronReach/2)break;
                    vesselGroup.neutronFlux+=flux*2*block.template.reflectivity;
                    vesselGroup.positionalEfficiency+=efficiency/length*block.template.efficiency;
                    vesselGroup.moderatorLines++;
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0||nonshields==0)break;
                    vesselGroup.moderatorLines++;
                    break;
                }
                break;
            }
        }
    }
    public void rePropogateNeutronFlux(OverhaulMSR reactor){
        if(!isFuelVessel())return;
        if(!vesselGroup.wasActive)return;
        if(!vesselGroup.isPrimed()&&vesselGroup.neutronFlux<vesselGroup.criticality)return;
        if(hasPropogated)return;
        hasPropogated = true;
        for(Direction d : directions){
            int flux = 0;
            int length = 0;
            int nonshields = 0;
            float efficiency = 0;
            for(int i = 1; i<=Core.configuration.overhaul.fissionMSR.neutronReach+1; i++){
                Block block = reactor.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(block==null)break;
                if(block.isCasing())break;
                if(block.isModerator()){
                    flux+=block.template.flux;
                    efficiency+=block.template.efficiency;
                    length++;
                    if(!block.isShield()){
                        nonshields++;
                    }
                    continue;
                }
                if(block.isFuelVessel()){
                    if(length==0||nonshields==0)break;
                    block.vesselGroup.neutronFlux+=flux;
                    block.vesselGroup.moderatorLines++;
                    block.vesselGroup.positionalEfficiency+=efficiency/length;
                    block.vesselGroup.rePropogateNeutronFlux(reactor);
                    break;
                }
                if(block.isReflector()){
                    if(length==0||nonshields==0)break;
                    if(length>Core.configuration.overhaul.fissionMSR.neutronReach/2)break;
                    vesselGroup.neutronFlux+=flux*2*block.template.reflectivity;
                    vesselGroup.positionalEfficiency+=efficiency/length*block.template.efficiency;
                    vesselGroup.moderatorLines++;
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0||nonshields==0)break;
                    vesselGroup.moderatorLines++;
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
            int nonshields = 0;
            HashMap<Block, Integer> shieldFluxes = new HashMap<>();
            Queue<Block> toActivate = new Queue<>();
            Queue<Block> toValidate = new Queue<>();
            for(int i = 1; i<=Core.configuration.overhaul.fissionMSR.neutronReach+1; i++){
                Block block = reactor.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(block==null)break;
                boolean skip = false;
                if(block.isModerator()){
                    length++;
                    if(!block.isShield()){
                        nonshields++;
                    }
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
                if(block.isFuelVesselActive()){
                    if(length==0||nonshields==0)break;
                    for(Block b : shieldFluxes.keySet()){
                        b.flux+=shieldFluxes.get(b);
                    }
                    for(Block b : toActivate)b.moderatorActive = true;
                    for(Block b : toValidate)b.moderatorValid = true;
                    break;
                }
                if(block.isReflector()){
                    if(length==0||nonshields==0)break;
                    block.reflectorActive = true;
                    for(Block b : shieldFluxes.keySet()){
                        b.flux+=shieldFluxes.get(b)*(1+block.template.reflectivity);
                    }
                    for(Block b : toActivate)b.moderatorActive = true;
                    for(Block b : toValidate)b.moderatorValid = true;
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0||nonshields==0)break;
                    for(Block b : shieldFluxes.keySet()){
                        b.flux+=shieldFluxes.get(b);
                    }
                    block.flux+=flux;
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
        return (isActive()||isInert())&&template.cluster;
    }
    @Override
    public void renderOverlay(double x, double y, double width, double height, Multiblock multiblock){
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
            drawCircle(x, y, width, height, Core.theme.getRGBA(r, g, b, 1));
        }
        OverhaulMSR.Cluster cluster = this.cluster;
        if(cluster!=null){
            Color primaryColor = null;
            if(cluster.netHeat>0){
                primaryColor = Color.red;
            }
            if(cluster.coolingPenaltyMult<1){
                primaryColor = Color.blue;
            }
            if(primaryColor!=null){
                Core.applyColor(Core.theme.getRGBA(primaryColor), .125f);
                Renderer2D.drawRect(x, y, x+width, y+height, 0);
                Core.applyColor(Core.theme.getRGBA(primaryColor), .75f);
                double border = width/8;
                boolean top = cluster.contains(this.x, this.y, z-1);
                boolean right = cluster.contains(this.x+1, this.y, z);
                boolean bottom = cluster.contains(this.x, this.y, z+1);
                boolean left = cluster.contains(this.x-1, this.y, z);
                if(!top||!left||!cluster.contains(this.x-1, this.y, z-1)){//top left
                    Renderer2D.drawRect(x, y, x+border, y+border, 0);
                }
                if(!top){//top
                    Renderer2D.drawRect(x+width/2-border, y, x+width/2+border, y+border, 0);
                }
                if(!top||!right||!cluster.contains(this.x+1, this.y, z-1)){//top right
                    Renderer2D.drawRect(x+width-border, y, x+width, y+border, 0);
                }
                if(!right){//right
                    Renderer2D.drawRect(x+width-border, y+height/2-border, x+width, y+height/2+border, 0);
                }
                if(!bottom||!right||!cluster.contains(this.x+1, this.y, z+1)){//bottom right
                    Renderer2D.drawRect(x+width-border, y+height-border, x+width, y+height, 0);
                }
                if(!bottom){//bottom
                    Renderer2D.drawRect(x+width/2-border, y+height-border, x+width/2+border, y+height, 0);
                }
                if(!bottom||!left||!cluster.contains(this.x-1, this.y, z+1)){//bottom left
                    Renderer2D.drawRect(x, y+height-border, x+border, y+height, 0);
                }
                if(!left){//left
                    Renderer2D.drawRect(x, y+height/2-border, x+border, y+height/2+border, 0);
                }
            }
            Color secondaryColor = null;
            if(!cluster.isConnectedToWall){
                secondaryColor = Color.white;
            }
            if(!cluster.isCreated()){
                secondaryColor = Color.pink;
            }
            if(secondaryColor!=null){
                Core.applyAverageColor(secondaryColor, Core.theme.getRGBA(secondaryColor), .75f);
                double border = width/8;
                boolean top = cluster.contains(this.x, this.y, z-1);
                boolean right = cluster.contains(this.x+1, this.y, z);
                boolean bottom = cluster.contains(this.x, this.y, z+1);
                boolean left = cluster.contains(this.x-1, this.y, z);
                if(!top){//top
                    Renderer2D.drawRect(x+border, y, x+width/2-border, y+border, 0);
                    Renderer2D.drawRect(x+width/2+border, y, x+width-border, y+border, 0);
                }
                if(!right){//right
                    Renderer2D.drawRect(x+width-border, y+border, x+width, y+height/2-border, 0);
                    Renderer2D.drawRect(x+width-border, y+height/2+border, x+width, y+height-border, 0);
                }
                if(!bottom){//bottom
                    Renderer2D.drawRect(x+border, y+height-border, x+width/2-border, y+height, 0);
                    Renderer2D.drawRect(x+width/2+border, y+height-border, x+width-border, y+height, 0);
                }
                if(!left){//left
                    Renderer2D.drawRect(x, y+border, x+border, y+height/2-border, 0);
                    Renderer2D.drawRect(x, y+height/2+border, x+border, y+height-border, 0);
                }
            }
        }
    }
    public boolean isInert(){
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
        return template.cooling!=0;
    }
    public multiblock.overhaul.fissionsfr.Block convertToSFR(){
        multiblock.overhaul.fissionsfr.Block b = new multiblock.overhaul.fissionsfr.Block(x, y, z, Core.configuration.overhaul.fissionSFR.convertToSFR(template));
        b.fuel = Core.configuration.overhaul.fissionSFR.convertToSFR(fuel);
        b.source = Core.configuration.overhaul.fissionSFR.convertToSFR(source);
        return b;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.cooling!=0;
    }
    @Override
    public Block copy(){
        Block copy = new Block(x, y, z, template);
        copy.fuel = fuel;
        copy.source = source;
        copy.irradiatorRecipe = irradiatorRecipe;
        copy.hasPropogated = hasPropogated;
        copy.flux = flux;
        copy.moderatorValid = moderatorValid;
        copy.moderatorActive = moderatorActive;
        copy.heaterValid = heaterValid;
        copy.reflectorActive = reflectorActive;
        copy.shieldActive = shieldActive;
        copy.efficiency = efficiency;
        copy.cluster = cluster;//TODO probably shouldn't do that
        copy.closed = closed;
        //TODO vessel groups on the copy?
        return copy;
    }
    @Override
    public boolean isEqual(multiblock.Block other){
        return other instanceof Block&&((Block)other).template==template;
    }
}
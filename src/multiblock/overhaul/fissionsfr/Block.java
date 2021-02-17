package multiblock.overhaul.fissionsfr;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.function.Function;
import planner.Core;
import multiblock.configuration.overhaul.fissionsfr.PlacementRule;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionsfr.Source;
import planner.vr.VRCore;
import simplelibrary.Queue;
import simplelibrary.opengl.Renderer2D;
public class Block extends multiblock.Block{
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public multiblock.configuration.overhaul.fissionsfr.Block template;
    public multiblock.configuration.overhaul.fissionsfr.Fuel fuel;
    public multiblock.configuration.overhaul.fissionsfr.Source source;
    public multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irradiatorRecipe;
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
    public boolean closed = false;
    public boolean wasActive;
    public int hadFlux;
    public OverhaulSFR.Cluster cluster;
    public Block(Configuration configuration, int x, int y, int z, multiblock.configuration.overhaul.fissionsfr.Block template){
        super(configuration, x, y, z);
        this.template = template;
    }
    @Override
    public multiblock.Block newInstance(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, template);
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
        positionalEfficiency = efficiency = moderatorLines = neutronFlux = 0;
        wasActive = moderatorValid = moderatorActive = heatsinkValid = reflectorActive = shieldActive = false;
        cluster = null;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
        if(isFuelCell()){
            tip+="\nFuel: "+fuel.name+"\n"
                    + "Fuel Cell "+(isFuelCellActive()?"Active":"Inactive");
            if(isFuelCellActive()){
                tip+="\nAdjacent moderator lines: "+moderatorLines+"\n"
                        + "Heat Multiplier: "+percent(moderatorLines, 0)+"\n"
                        + "Heat Produced: "+moderatorLines*fuel.heat+"H/t\n"
                        + "Efficiency: "+percent(efficiency, 0)+"\n"
                        + "Positional Efficiency: "+percent(positionalEfficiency, 0)+"\n"
                        + "Total Neutron Flux: "+neutronFlux+"\n"
                        + "Criticality Factor: "+fuel.criticality;
            }else{
                tip+="\nTotal Neutron Flux: "+neutronFlux+"\n"
                        + "Criticality Factor: "+fuel.criticality;
            }
            if(isPrimed()){
                Source src = source;
                tip+="\n"
                        + "Primed\n"
                        + "Neutron source: "+(src==null?"Self":src.name);
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
                    + "Total heat: "+(neutronFlux*template.heatMult)+" H/t\n"
                    + "Efficiency factor: "+template.efficiency;
        }
        if(closed){
            tip+="\nClosed";
        }
        if(isIrradiator()){
            if(irradiatorRecipe!=null){
                tip+="\nRecipe: "+irradiatorRecipe.name;
            }
            tip+="\nIrradiator flux: "+neutronFlux+"\n";
            if(irradiatorRecipe!=null){
                tip+="Efficiency bonus: "+percent(irradiatorRecipe.efficiency,0)+"\n"
                        + "Heat per flux: "+irradiatorRecipe.heat+"\n"
                        + "Total heat: "+irradiatorRecipe.heat*neutronFlux+"H/t";
            }
        }
        if(isHeatsink()){
            tip+="\nHeatsink "+(heatsinkValid?"Valid":"Invalid");
        }
        OverhaulSFR.Cluster cluster = this.cluster;
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
                + "\nCooling: "+template.cooling+"H/t";
        for(PlacementRule rule : template.rules){
            tip+="\nRequires "+rule.toString();
        }
        return tip;
    }
    public boolean isPrimed(){
        if(!isFuelCell())return false;
        return source!=null||fuel.selfPriming;
    }
    @Override
    public boolean isCore(){
        return isModerator()||isFuelCell()||isShield()||isIrradiator()||isReflector();
    }
    @Override
    public boolean isCasing(){
        return template==null;
    }
    @Override
    public boolean isActive(){
        return isCasing()||isConductor()||isModeratorActive()||isFuelCellActive()||isHeatsinkActive()||isIrradiatorActive()||reflectorActive||isShieldActive();
    }
    @Override
    public boolean isValid(){
        return isInert()||isActive()||moderatorValid;
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
    public boolean isHeatsink(){
        if(isCasing())return false;
        return template.cooling!=0;
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
        if(canCluster()&&(cluster==null||!cluster.isCreated()))return false;
        return template.functional&&(isActive()||moderatorValid);
    }
    public void propogateNeutronFlux(OverhaulSFR reactor, boolean force){
        if(!isFuelCell())return;
        if(!force&&!isPrimed()&&neutronFlux<fuel.criticality)return;
        if(hasPropogated)return;
        hasPropogated = true;
        for(Direction d : directions){
            int flux = 0;
            int length = 0;
            float efficiency = 0;
            for(int i = 1; i<=reactor.getConfiguration().overhaul.fissionSFR.neutronReach+1; i++){
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
                    if(length==0)break;
                    block.neutronFlux+=flux;
                    block.moderatorLines++;
                    if(flux>0)block.positionalEfficiency+=efficiency/length;
                    block.propogateNeutronFlux(reactor, false);
                    break;
                }
                if(block.isReflector()){
                    if(length==0)break;
                    if(length>reactor.getConfiguration().overhaul.fissionSFR.neutronReach/2)break;
                    neutronFlux+=flux*2*block.template.reflectivity;
                    if(flux>0)positionalEfficiency+=efficiency/length*block.template.efficiency;
                    moderatorLines++;
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0)break;
                    moderatorLines++;
                    if(block.irradiatorRecipe!=null){
                        if(flux>0)positionalEfficiency+=efficiency/length*block.irradiatorRecipe.efficiency;
                    }
                    break;
                }
                break;
            }
        }
    }
    public void rePropogateNeutronFlux(OverhaulSFR reactor, boolean force){
        if(!isFuelCell())return;
        if(!wasActive)return;
        if(!force&&!isPrimed()&&neutronFlux<fuel.criticality)return;
        if(hasPropogated)return;
        hasPropogated = true;
        for(Direction d : directions){
            int flux = 0;
            int length = 0;
            float efficiency = 0;
            for(int i = 1; i<=reactor.getConfiguration().overhaul.fissionSFR.neutronReach+1; i++){
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
                    if(length==0)break;
                    block.neutronFlux+=flux;
                    block.moderatorLines++;
                    if(flux>0)block.positionalEfficiency+=efficiency/length;
                    block.rePropogateNeutronFlux(reactor, false);
                    break;
                }
                if(block.isReflector()){
                    if(length==0)break;
                    if(length>reactor.getConfiguration().overhaul.fissionSFR.neutronReach/2)break;
                    neutronFlux+=flux*2*block.template.reflectivity;
                    if(flux>0)positionalEfficiency+=efficiency/length*block.template.efficiency;
                    moderatorLines++;
                    break;
                }
                if(block.isIrradiator()){
                    if(length==0)break;
                    moderatorLines++;
                    if(block.irradiatorRecipe!=null){
                        if(flux>0)positionalEfficiency+=efficiency/length*block.irradiatorRecipe.efficiency;
                    }
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
            int length = 0;
            HashMap<Block, Integer> shieldFluxes = new HashMap<>();
            Queue<Block> toActivate = new Queue<>();
            Queue<Block> toValidate = new Queue<>();
            for(int i = 1; i<=reactor.getConfiguration().overhaul.fissionSFR.neutronReach+1; i++){
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
                if(block.isFuelCellActive()){
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
                        b.neutronFlux+=flux*(1+block.template.reflectivity);
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
        return (isActive()||isInert())&&template.cluster;
    }
    @Override
    public void renderOverlay(double x, double y, double width, double height, Multiblock multiblock){
        if(!isValid()){
            drawOutline(x, y, width, height, Core.theme.getRed());
        }
        if(isModeratorActive()){
            drawOutline(x, y, width, height, Core.theme.getGreen());
        }
        boolean self = fuel!=null&&fuel.selfPriming;
        if(source!=null||self){
            float fac = self?1:(float) Math.pow(source.efficiency, 10);
            float r = self?0:Math.min(1, -2*fac+2);
            float g = self?0:Math.min(1, fac*2);
            float b = self?1:0;
            drawCircle(x, y, width, height, Core.theme.getRGBA(r, g, b, 1));
        }
        OverhaulSFR.Cluster cluster = this.cluster;
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
    @Override
    public void renderOverlay(double x, double y, double z, double width, double height, double depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        if(!isValid()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getRed(), faceRenderFunc);
        }
        if(isModeratorActive()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getGreen(), faceRenderFunc);
        }
        boolean self = fuel!=null&&fuel.selfPriming;
        if(source!=null||self){
            float fac = self?1:(float) Math.pow(source.efficiency, 10);
            float r = self?0:Math.min(1, -2*fac+2);
            float g = self?0:Math.min(1, fac*2);
            float b = self?1:0;
            drawCircle(x, y, z, width, height, depth, Core.theme.getRGBA(r, g, b, 1), faceRenderFunc);
        }
        OverhaulSFR.Cluster cluster = this.cluster;
        if(cluster!=null){
            double border = width/16;
            Color primaryColor = null;
            if(cluster.netHeat>0){
                primaryColor = Color.red;
            }
            if(cluster.coolingPenaltyMult<1){
                primaryColor = Color.blue;
            }
            if(primaryColor!=null){
                Core.applyColor(Core.theme.getRGBA(primaryColor));
                VRCore.drawPrimaryCubeOutline(x-border, y-border, z-border, x+width+border, y+height+border, z+depth+border, border, border*3, (t) -> {
                    boolean d1 = cluster.contains(this.x+t[0].x, this.y+t[0].y, this.z+t[0].z);
                    boolean d2 = cluster.contains(this.x+t[1].x, this.y+t[1].y, this.z+t[1].z);
                    boolean d3 = cluster.contains(this.x+t[0].x+t[1].x, this.y+t[0].y+t[1].y, this.z+t[0].z+t[1].z);
                    if(d1&&d2&&!d3)return true;//both sides, but not the corner
                    if(!d1&&!d2)return true;//neither side
                    return false;
                });
            }
            Color secondaryColor = null;
            if(!cluster.isConnectedToWall){
                secondaryColor = Color.white;
            }
            if(!cluster.isCreated()){
                secondaryColor = Color.pink;
            }
            if(secondaryColor!=null){
                Core.applyAverageColor(secondaryColor, Core.theme.getRGBA(secondaryColor));
                VRCore.drawSecondaryCubeOutline(x-border, y-border, z-border, x+width+border, y+height+border, z+depth+border, border, border*3, (t) -> {
                    boolean d1 = cluster.contains(this.x+t[0].x, this.y+t[0].y, this.z+t[0].z);
                    boolean d2 = cluster.contains(this.x+t[1].x, this.y+t[1].y, this.z+t[1].z);
                    boolean d3 = cluster.contains(this.x+t[0].x+t[1].x, this.y+t[0].y+t[1].y, this.z+t[0].z+t[1].z);
                    if(d1&&d2&&!d3)return true;//both sides, but not the corner
                    if(!d1&&!d2)return true;//neither side
                    return false;
                });
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
            if(!rule.isValid(this, (OverhaulSFR) multiblock)){
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
    public multiblock.overhaul.fissionmsr.Block convertToMSR(){
        multiblock.overhaul.fissionmsr.Block b = new multiblock.overhaul.fissionmsr.Block(getConfiguration(), x, y, z, getConfiguration().overhaul.fissionMSR.convertToMSR(template));
        b.fuel = getConfiguration().overhaul.fissionMSR.convertToMSR(fuel);
        b.source = getConfiguration().overhaul.fissionMSR.convertToMSR(source);
        return b;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.cooling!=0;
    }
    @Override
    public Block copy(){
        Block copy = new Block(getConfiguration(), x, y, z, template);
        copy.fuel = fuel;
        copy.source = source;
        copy.irradiatorRecipe = irradiatorRecipe;
        copy.hasPropogated = hasPropogated;
        copy.moderatorLines = moderatorLines;
        copy.neutronFlux = neutronFlux;
        copy.positionalEfficiency = positionalEfficiency;
        copy.moderatorValid = moderatorValid;
        copy.moderatorActive = moderatorActive;
        copy.heatsinkValid = heatsinkValid;
        copy.reflectorActive = reflectorActive;
        copy.shieldActive = shieldActive;
        copy.efficiency = efficiency;
        copy.cluster = cluster;//TODO probably shouldn't do that
        copy.closed = closed;
        return copy;
    }
    @Override
    public boolean isEqual(multiblock.Block other){
        return other instanceof Block&&((Block)other).template==template;
    }
    @Override
    public void convertTo(Configuration to){
        if(template.fuelCell)fuel = to.overhaul.fissionSFR.convert(fuel);
        if(template.fuelCell)source = to.overhaul.fissionSFR.convert(source);
        if(template.irradiator)irradiatorRecipe = to.overhaul.fissionSFR.convert(irradiatorRecipe);
        template = to.overhaul.fissionSFR.convert(template);
        configuration = to;
    }
}
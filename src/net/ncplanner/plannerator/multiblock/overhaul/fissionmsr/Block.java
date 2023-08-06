package net.ncplanner.plannerator.multiblock.overhaul.fissionmsr;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe;
public class Block extends AbstractBlock{
    public BlockElement template;
    public Fuel fuel;
    public IrradiatorRecipe irradiatorRecipe;
    public HeaterRecipe heaterRecipe;
    public boolean hasPropogated = false;
    public int neutronFlux;
    public boolean moderatorValid = false;
    public boolean moderatorActive = false;
    public boolean heaterValid = false;
    public boolean reflectorActive;
    public boolean shieldActive;
    public float efficiency;
    public OverhaulMSR.Cluster cluster;
    public OverhaulMSR.VesselGroup vesselGroup;//only used when recalculating; doesn't even need to be reset
    public Block source;
    public boolean casingValid;
    public Block(NCPFConfigurationContainer configuration, int x, int y, int z, BlockElement template){
        super(configuration, x, y, z);
        if(template==null)throw new IllegalArgumentException("Cannot create null block!");
        this.template = template;
    }
    @Override
    public AbstractBlock newInstance(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, template);
    }
    @Override
    public void copyProperties(AbstractBlock other){
        ((Block)other).fuel = fuel;
        ((Block)other).irradiatorRecipe = irradiatorRecipe;
        ((Block)other).heaterRecipe = heaterRecipe;
    }
    @Override
    public void clearData(){
        hasPropogated = false;
        efficiency = neutronFlux = 0;
        moderatorValid = moderatorActive = heaterValid = reflectorActive = shieldActive = casingValid = false;
        cluster = null;
        source = null;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
        if(fuel!=null){
            tip+="\n"+fuel.getDisplayName();
        }
        if(irradiatorRecipe!=null){
            tip+="\n"+irradiatorRecipe.getDisplayName();
        }
        if(heaterRecipe!=null){
            tip+="\n"+heaterRecipe.getDisplayName();
        }
        if(isController())tip+="\nController "+(casingValid?"Valid":"Invalid");
        if(isCasing())tip+="\nCasing "+(casingValid?"Valid":"Invalid");
        if(isFuelVessel()){
            OverhaulMSR.VesselGroup group = vesselGroup;
            if(fuel==null)tip+="\nNo Fuel";
            if(group==null){
                tip +="\nFuel Vessel "+(isFuelVesselActive()?"Active":"Inactive");
            }else{
                tip +="\nVessel Group "+(isFuelVesselActive()?"Active":"Inactive")+" ("+group.size()+" Vessels)";
                if(isFuelVesselActive()){
                    tip+="\nAdjacent moderator lines: "+group.moderatorLines+"\n"
                            + "Open Faces: "+group.getOpenFaces()+"\n"
                            + "Bunching Factor: "+group.getBunchingFactor()+"\n"
                            + "Surface Factor: "+group.getSurfaceFactor()+"\n"
                            + "Heat Multiplier: "+MathUtil.percent(group.getHeatMult()/group.size(), 0)+"\n"
                            + "Heat Produced: "+group.getHeatMult()*fuel.stats.heat+"H/t\n"
                            + "Efficiency: "+MathUtil.percent(efficiency/group.size(), 0)+"\n"
                            + "Positional Efficiency: "+MathUtil.percent(group.positionalEfficiency/group.size(), 0)+"\n"
                            + "Total Neutron Flux: "+group.neutronFlux+"\n"
                            + "Criticality Factor: "+group.criticality;
                }else{
                    tip+="\nTotal Neutron Flux: "+group.neutronFlux+"\n"
                            + "Criticality Factor: "+group.criticality;
                }
            }
            if(isPrimed()){
                tip+="\nPrimed"
                        + "\nNeutron source: "+(source==null?"Self":source.template.getDisplayName());
            }
            if(group!=null){
                if(group.isPrimed()){
                    tip+="\nVessel Group Primed";
                }
                tip+="\nNeutron Sources: "+group.getSources()+"/"+group.getRequiredSources();
            }
        }
        if(isModerator()){
            tip+="\nModerator "+(moderatorActive?"Active":(moderatorValid?"Valid":"Invalid"));
            tip+="\nFlux Factor: "+template.moderator.flux
               + "\nEfficiency Factor: "+template.moderator.efficiency;
        }
        if(isReflector()){
            tip+="\nReflector "+(reflectorActive?"Active":"Inactive");
            tip+="\nReflectivity: "+template.reflector.reflectivity
               + "\nEfficiency multiplier: "+template.reflector.efficiency;
        }
        if(isShield()){
            tip+="\nShield "+(shieldActive?"Valid":"Invalid");
            tip+="\nTotal flux: "+neutronFlux
               + "\nHeat per flux: "+template.neutronShield.heatPerFlux
               + "\nTotal heat: "+neutronFlux*template.neutronShield.heatPerFlux+" H/t"
               + "\nEfficiency factor: "+template.neutronShield.efficiency;
        }
        if(isIrradiator()){
            if(irradiatorRecipe==null)tip+="\nNo Recipe";
            tip+="\nIrradiator flux: "+neutronFlux;
            if(irradiatorRecipe!=null){
                tip+="\nEfficiency bonus: "+MathUtil.percent(irradiatorRecipe.stats.efficiency,0)
                        + "\nHeat per flux: "+irradiatorRecipe.stats.heat
                        + "\nTotal heat: "+irradiatorRecipe.stats.heat*neutronFlux+"H/t";
            }
        }
        if(isHeater()){
            if(heaterRecipe==null)tip+="\nNo Recipe";
            else tip+="\nHeater "+(heaterValid?"Valid":"Invalid");
            //TODO show heater validity check errors
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
    public boolean isPrimed(){
        if(!isFuelVessel())return false;
        if(fuel!=null&&fuel.stats.selfPriming)return true;
        return source!=null;
    }
    @Override
    public boolean isCore(){
        return isModerator()||isFuelVessel()||isShield()||isIrradiator()||isReflector();
    }
    @Override
    public boolean isActive(){
        return isConductor()||isModeratorActive()||isFuelVesselActive()||isHeaterActive()||isIrradiatorActive()||reflectorActive||isShieldActive()||casingValid;
    }
    @Override
    public boolean isValid(){
        return isConductor()||isActive()||moderatorValid;
    }
    public boolean isModeratorActive(){
        return isModerator()&&moderatorActive;
    }
    public boolean isFuelVesselActive(){
        if(fuel==null)return false;
        return isFuelVessel()&&(vesselGroup==null?false:vesselGroup.neutronFlux>=vesselGroup.criticality);
    }
    public boolean isFuelVessel(){
        return template.fuelVessel!=null;
    }
    public boolean isModerator(){
        return template.moderator!=null;
    }
    public boolean isReflector(){
        return template.reflector!=null;
    }
    public boolean isIrradiator(){
        return template.irradiator!=null;
    }
    public boolean isIrradiatorActive(){
        if(irradiatorRecipe==null)return false;
        return isIrradiator()&&neutronFlux>0;
    }
    public boolean isShield(){
        return template.neutronShield!=null;
    }
    public boolean isShieldActive(){
        return isShield()&&shieldActive&&moderatorValid;
    }
    public boolean isHeater(){
        return template.heater!=null;
    }
    public boolean isHeaterActive(){
        if(heaterRecipe==null)return false;
        return isHeater()&&heaterValid;
    }
    public boolean isConductor(){
        return template.conductor!=null;
    }
    public boolean isNeutronSource(){
        return template.neutronSource!=null;
    }
    public boolean isFunctional(){
        if(canCluster()&&(cluster==null||!cluster.isCreated()))return false;
        return (isFuelVessel()||isModerator()||isReflector()||isIrradiator()||isHeater()||isShield())&&(isActive()||moderatorValid);
    }
    public boolean canCluster(){
        return isConductor()||(isActive()&&(isFuelVessel()||isIrradiator()||isHeater()||isShield()));
    }
    @Override
    public void renderOverlay(Renderer renderer, float x, float y, float z, float width, float height, float depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        if(!isValid()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineInvalid(), faceRenderFunc);
        }
        if(isModeratorActive()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineActive(), faceRenderFunc);
        }
        if(template.fuelVessel!=null&&fuel!=null){
            boolean self = fuel.stats.selfPriming;
            if(source!=null||self){
                drawCircle(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorSourceCircle(source==null?1:source.template.neutronSource.efficiency, self), faceRenderFunc);
            }
        }
        OverhaulMSR.Cluster cluster = this.cluster;
        if(cluster!=null){
            float border = width/16;
            Color primaryColor = null;
            if(cluster.netHeat>0){
                primaryColor = Core.theme.getClusterOverheatingColor();
            }
            if(cluster.coolingPenaltyMult<1){
                primaryColor = Core.theme.getClusterOvercoolingColor();
            }
            if(primaryColor!=null){
                renderer.setColor(primaryColor);
                renderer.drawPrimaryCubeOutline(x-border, y-border, z-border, x+width+border, y+height+border, z+depth+border, border, border*3, (t) -> {
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
                secondaryColor = Core.theme.getClusterDisconnectedColor();
            }
            if(!cluster.isCreated()){
                secondaryColor = Core.theme.getClusterInvalidColor();
            }
            if(secondaryColor!=null){
                renderer.setColor(secondaryColor);
                renderer.drawSecondaryCubeOutline(x-border, y-border, z-border, x+width+border, y+height+border, z+depth+border, border, border*3, (t) -> {
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
    @Override
    public List<? extends NCPFPlacementRule> getRules(){
        if(template.heater!=null)return template.heater.rules;
        return new ArrayList<>();
    }
    @Override
    public boolean canRequire(AbstractBlock oth){
        if(template.heater!=null)return requires(oth, null);
        Block other = (Block) oth;
        if(template.fuelVessel!=null||template.reflector!=null||template.irradiator!=null||template.moderator!=null)return other.template.moderator!=null;
        if(template.conductor!=null)return other.canCluster();
        return false;
    }
    @Override
    public boolean canGroup(){
        if(template.moderator!=null)return false;
        return template.heater!=null;
    }
    public net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block convertToSFR() throws MissingConfigurationEntryException{
        if(template.parent!=null&&template.parent.heater!=null)return null;
        net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(getConfiguration(), x, y, z, getConfiguration().overhaul.fissionSFR.convertToSFR(template));
        b.recipe = b.template.convertToSFR(recipe);
        return b;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.heater!=null;
    }
    @Override
    public Block copy(){
        Block copy = new Block(getConfiguration(), x, y, z, template);
        copy.fuel = fuel;
        copy.irradiatorRecipe = irradiatorRecipe;
        copy.heaterRecipe = heaterRecipe;
        copy.source = source;
        copy.hasPropogated = hasPropogated;
        copy.neutronFlux = neutronFlux;
        copy.moderatorValid = moderatorValid;
        copy.moderatorActive = moderatorActive;
        copy.heaterValid = heaterValid;
        copy.reflectorActive = reflectorActive;
        copy.shieldActive = shieldActive;
        copy.efficiency = efficiency;
        copy.cluster = cluster;//TODO probably shouldn't do that
        //TODO vessel groups on the copy?
        return copy;
    }
    public boolean isCasing(){
        return template.casing!=null||template.port!=null;//is explicitly casing or if it's a port
    }
    public boolean isController(){
        return template.controller!=null;
    }
    /**
     * Adds a neutron source to this block
     * @param msr the parent MSR
     * @param source the source to add
     * @return If no block was replaced, the new source. Otherwise, the replaced block.
     */
    public Block addNeutronSource(OverhaulMSR msr, BlockElement source){
        HashMap<int[], Integer> possible = new HashMap<>();
        for(Direction d : Direction.values()){
            int i = 0;
            while(true){
                i++;
                if(!msr.contains(x+d.x*i, y+d.y*i, z+d.z*i)){
                    possible.put(new int[]{x+d.x*(i-1),y+d.y*(i-1),z+d.z*(i-1)}, i);
                    break;
                }
                Block b = msr.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(b==null)continue;//air
                if(b.isFuelVessel()||b.isIrradiator()||b.isReflector())break;
            }
        }
        ArrayList<int[]> keys = new ArrayList<>(possible.keySet());
        Collections.sort(keys, (o1, o2) -> {
            return possible.get(o1)-possible.get(o2);
        });
        for(int[] key : keys){
            Block was = msr.getBlock(key[0], key[1], key[2]);
            if(tryAddNeutronSource(msr, source, key[0], key[1], key[2]))return was==null?msr.getBlock(key[0], key[1], key[2]):was;
        }
        return null;
    }
    private boolean tryAddNeutronSource(OverhaulMSR msr, BlockElement source, int X, int Y, int Z){
        Block b = msr.getBlock(X, Y, Z);
        if(b!=null&&(b.isController()||b.template.port!=null||b.template.neutronSource!=null))return false;
        msr.setBlock(X, Y, Z, new Block(msr.getConfiguration(), X, Y, Z, source));
        return true;
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
    public NCPFElement getTemplate(){
        return template;
    }
    @Override
    public boolean hasRecipes(){
        if(template.parent!=null)return template.parent.fuelVessel!=null||template.parent.irradiator!=null||template.parent.heater!=null;
        return template.fuelVessel!=null||template.irradiator!=null||template.heater!=null;
    }
    @Override
    public List<? extends IBlockRecipe> getRecipes(){
        if(template.parent!=null){
            if(template.parent.fuelVessel!=null)return template.parent.fuels;
            if(template.parent.irradiator!=null)return template.parent.irradiatorRecipes;
            if(template.parent.heater!=null)return template.parent.heaterRecipes;
            return null;
        }
        if(template.fuelVessel!=null)return template.parent.fuels;
        if(template.irradiator!=null)return template.parent.irradiatorRecipes;
        if(template.heater!=null)return template.parent.heaterRecipes;
        return null;
    }
    @Override
    public NCPFElement getRecipe(){
        if(template.parent!=null){
            if(template.parent.fuelVessel!=null)return fuel;
            if(template.parent.irradiator!=null)return irradiatorRecipe;
            if(template.parent.heater!=null)return heaterRecipe;
            return null;
        }
        if(template.fuelVessel!=null)return fuel;
        if(template.irradiator!=null)return irradiatorRecipe;
        if(template.heater!=null)return heaterRecipe;
        return null;
    }
    @Override
    public void setRecipe(NCPFElement recipe){
        if(template.fuelVessel!=null||template.parent!=null&&template.parent.fuelVessel!=null)fuel = (Fuel)recipe;
        else if(template.irradiator!=null||template.parent!=null&&template.parent.irradiator!=null)irradiatorRecipe = (IrradiatorRecipe)recipe;
        else if(template.heater!=null||template.parent!=null&&template.parent.heater!=null)heaterRecipe = (HeaterRecipe)recipe;
        else throw new IllegalArgumentException("Tried to set block recipe to "+template.definition.toString()+", but it can't have recipes!");
    }
    @Override
    public boolean isToggled(){
        return template.unToggled!=null;
    }
    @Override
    public void setToggled(boolean toggled){
        if(toggled==isToggled())return;
        BlockElement newTemplate;
        if(toggled)newTemplate = template.toggled;
        else newTemplate = template.unToggled;
        if(newTemplate==null)throw new IllegalArgumentException("Tried to "+(toggled?"":"un")+"toggle non-toggleable block: "+template.definition.toString());
        template = newTemplate;
    }
}
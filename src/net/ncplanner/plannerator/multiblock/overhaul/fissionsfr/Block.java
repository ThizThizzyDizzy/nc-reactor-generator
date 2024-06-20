package net.ncplanner.plannerator.multiblock.overhaul.fissionsfr;
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
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
public class Block extends AbstractBlock{
    public BlockElement template;
    public Fuel fuel;
    public IrradiatorRecipe irradiatorRecipe;
    public boolean hasPropogated = false;
    public int moderatorLines;
    public int neutronFlux;
    public float positionalEfficiency;
    public boolean moderatorValid = false;
    public boolean moderatorActive = false;
    public boolean heatsinkValid = false;
    public boolean reflectorActive;
    public boolean shieldActive;
    public float efficiency;
    public boolean wasActive;
    public int hadFlux;
    public OverhaulSFR.Cluster cluster;
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
    }
    @Override
    public void clearData(){
        hasPropogated = false;
        positionalEfficiency = efficiency = moderatorLines = neutronFlux = 0;
        wasActive = moderatorValid = moderatorActive = heatsinkValid = reflectorActive = shieldActive = casingValid = false;
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
        if(isController())tip+="\nController "+(casingValid?"Valid":"Invalid");
        if(isCasing())tip+="\nCasing "+(casingValid?"Valid":"Invalid");
        if(isFuelCell()){
            if(fuel==null)tip+="\nNo Fuel";
            tip+="\nFuel Cell "+(isFuelCellActive()?"Active":"Inactive");
            if(isFuelCellActive()){
                tip+="\nAdjacent moderator lines: "+moderatorLines+"\n"
                        + "Heat Multiplier: "+MathUtil.percent(moderatorLines, 0)+"\n"
                        + "Heat Produced: "+moderatorLines*fuel.stats.heat+"H/t\n"
                        + "Efficiency: "+MathUtil.percent(efficiency, 0)+"\n"
                        + "Positional Efficiency: "+MathUtil.percent(positionalEfficiency, 0)+"\n"
                        + "Total Neutron Flux: "+neutronFlux+"\n"
                        + "Criticality Factor: "+fuel.stats.criticality;
            }else{
                tip+="\nTotal Neutron Flux: "+neutronFlux;
                if(fuel!=null)tip+="\nCriticality Factor: "+fuel.stats.criticality;
            }
            if(isPrimed()){
                tip+="\nPrimed"
                        + "\nNeutron source: "+(source==null?"Self":source.template.getDisplayName());
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
        if(isHeatsink()){
            tip+="\nHeatsink "+(heatsinkValid?"Valid":"Invalid");
            //TODO show heatsink validity check errors
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
    public boolean isPrimed(){
        if(!isFuelCell())return false;
        if(fuel!=null&&fuel.stats.selfPriming)return true;
        return source!=null;
    }
    @Override
    public boolean isCore(){
        return isModerator()||isFuelCell()||isShield()||isIrradiator()||isReflector();
    }
    @Override
    public boolean isActive(){
        return isConductor()||isModeratorActive()||isFuelCellActive()||isHeatsinkActive()||isIrradiatorActive()||reflectorActive||isShieldActive()||casingValid;
    }
    @Override
    public boolean isValid(){
        return isConductor()||isActive()||moderatorValid;
    }
    public boolean isModeratorActive(){
        return isModerator()&&moderatorActive;
    }
    public boolean isFuelCellActive(){
        if(fuel==null)return false;
        return isFuelCell()&&neutronFlux>=fuel.stats.criticality;
    }
    public boolean isFuelCell(){
        return template.fuelCell!=null;
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
    public boolean isHeatsink(){
        return template.heatsink!=null;
    }
    public boolean isHeatsinkActive(){
        return isHeatsink()&&heatsinkValid;
    }
    public boolean isConductor(){
        return template.conductor!=null;
    }
    public boolean isNeutronSource(){
        return template.neutronSource!=null;
    }
    public boolean isFunctional(){
        if(canCluster()&&(cluster==null||!cluster.isCreated()))return false;
        return (isFuelCell()||isModerator()||isReflector()||isIrradiator()||isHeatsink()||isShield())&&(isActive()||moderatorValid);
    }
    public boolean canCluster(){
        return isConductor()||(isActive()&&(isFuelCell()||isIrradiator()||isHeatsink()||isShield()));
    }
    @Override
    public void renderOverlay(Renderer renderer, float x, float y, float z, float width, float height, float depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        if(!isValid()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineInvalid(), faceRenderFunc);
        }
        if(isModeratorActive()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineActive(), faceRenderFunc);
        }
        if(template.fuelCell!=null&&fuel!=null){
            boolean self = fuel.stats.selfPriming;
            if(source!=null||self){
                drawCircle(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorSourceCircle(source==null?1:source.template.neutronSource.efficiency, self), faceRenderFunc);
            }
        }
        OverhaulSFR.Cluster cluster = this.cluster;
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
    public boolean canRequire(AbstractBlock oth){
        if(template.heatsink!=null)return requires(oth, null);
        Block other = (Block) oth;
        if(template.fuelCell!=null||template.reflector!=null||template.irradiator!=null||template.moderator!=null)return other.template.moderator!=null;
        if(template.conductor!=null)return other.canCluster();
        return false;
    }
    @Override
    public boolean canGroup(){
        if(template.moderator!=null)return false;
        return template.heatsink!=null;
    }
    public net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block convertToMSR() throws MissingConfigurationEntryException{
        if(template.coolantVent!=null)return null;//remove vents
        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement newTemplate = null;
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement elem : getConfiguration().getConfiguration(OverhaulMSRConfiguration::new).blocks){
            if(elem.definition.toString().equals(StringUtil.superReplace(template.definition.toString(), "solid", "salt", "cell", "vessel", "sink", "heater", "water", "standard")))newTemplate = elem;
        }
        if(newTemplate==null)return null;
        net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(getConfiguration(), x, y, z, newTemplate);
        if(irradiatorRecipe!=null){
           for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe recipe : b.template.irradiatorRecipes){
               if(recipe.definition.matches(irradiatorRecipe.definition))b.irradiatorRecipe = recipe;
           }
        }
        if(fuel!=null){
           for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel recipe : b.template.fuels){
               for(String name : recipe.getLegacyNames()){
                   if(name.equals(fuel.getName())||StringUtil.toLowerCase(name.trim()).startsWith(StringUtil.superReplace(StringUtil.superRemove(StringUtil.toLowerCase(fuel.getDisplayName().trim()), " oxide", " nitride", "-zirconium alloy", ""), "mox", "mf4", "mni", "mza")))b.fuel = recipe;
               }
           }
        }
        if(b.template.heater!=null)b.heaterRecipe = b.template.heaterRecipes.get(0);
        return b;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.heatsink!=null;
    }
    @Override
    public Block copy(){
        Block copy = new Block(getConfiguration(), x, y, z, template);
        copy.fuel = fuel;
        copy.irradiatorRecipe = irradiatorRecipe;
        copy.source = source;
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
     * @param sfr the parent SFR
     * @param source the source to add
     * @return If no block was replaced, the new source. Otherwise, the replaced block.
     */
    public Block addNeutronSource(OverhaulSFR sfr, BlockElement source){
        HashMap<int[], Integer> possible = new HashMap<>();
        for(Direction d : Direction.values()){
            int i = 0;
            while(true){
                i++;
                if(!sfr.contains(x+d.x*i, y+d.y*i, z+d.z*i)){
                    possible.put(new int[]{x+d.x*(i-1),y+d.y*(i-1),z+d.z*(i-1)}, i);
                    break;
                }
                Block b = sfr.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(b==null)continue;//air
                if(b.isFuelCell()||b.isIrradiator()||b.isReflector())break;
            }
        }
        ArrayList<int[]> keys = new ArrayList<>(possible.keySet());
        Collections.sort(keys, (o1, o2) -> {
            return possible.get(o1)-possible.get(o2);
        });
        for(int[] key : keys){
            Block was = sfr.getBlock(key[0], key[1], key[2]);
            if(tryAddNeutronSource(sfr, source, key[0], key[1], key[2]))return was==null?sfr.getBlock(key[0], key[1], key[2]):was;
        }
        return null;
    }
    private boolean tryAddNeutronSource(OverhaulSFR sfr, BlockElement source, int X, int Y, int Z){
        Block b = sfr.getBlock(X, Y, Z);
        if(b!=null&&(b.isController()||b.template.coolantVent!=null||b.template.port!=null||b.template.neutronSource!=null))return false;
        sfr.setBlock(X, Y, Z, new Block(sfr.getConfiguration(), X, Y, Z, source));
        return true;
    }
    @Override
    public BlockElement getTemplate(){
        return template;
    }
    @Override
    public boolean hasRecipes(){
        if(template.parent!=null)return template.parent.fuelCell!=null||template.parent.irradiator!=null;
        return template.fuelCell!=null||template.irradiator!=null;
    }
    @Override
    public List<? extends IBlockRecipe> getRecipes(){
        if(template.parent!=null)return template.parent.fuelCell!=null?template.parent.fuels:template.parent.irradiatorRecipes;
        return isFuelCell()?template.fuels:template.irradiatorRecipes;
    }
    @Override
    public NCPFElement getRecipe(){
        if(template.parent!=null)return template.parent.fuelCell!=null?fuel:irradiatorRecipe;
        return isFuelCell()?fuel:irradiatorRecipe;
    }
    @Override
    public void setRecipe(NCPFElement recipe){
        if(template.fuelCell!=null||template.parent!=null&&template.parent.fuelCell!=null)fuel = (Fuel)recipe;
        else if(template.irradiator!=null||template.parent!=null&&template.parent.irradiator!=null)irradiatorRecipe = (IrradiatorRecipe)recipe;
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
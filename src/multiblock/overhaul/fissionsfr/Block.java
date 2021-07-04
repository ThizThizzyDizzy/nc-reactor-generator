package multiblock.overhaul.fissionsfr;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.ITemplateAccess;
import multiblock.configuration.overhaul.fissionsfr.PlacementRule;
import planner.Core;
import planner.exception.MissingConfigurationEntryException;
import planner.vr.VRCore;
import simplelibrary.image.Color;
import simplelibrary.image.Image;
import simplelibrary.opengl.Renderer2D;
public class Block extends multiblock.Block implements ITemplateAccess<multiblock.configuration.overhaul.fissionsfr.Block> {
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public multiblock.configuration.overhaul.fissionsfr.Block template;
    public multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe;
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
    public boolean isToggled = false;//if true, shields are closed and ports/vents are in output mode
    public boolean wasActive;
    public int hadFlux;
    public OverhaulSFR.Cluster cluster;
    public Block source;
    public boolean casingValid;
    public Block(Configuration configuration, int x, int y, int z, multiblock.configuration.overhaul.fissionsfr.Block template){
        super(configuration, x, y, z);
        if(template==null)throw new IllegalArgumentException("Cannot create null block!");
        this.template = template;
    }
    @Override
    public multiblock.Block newInstance(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, template);
    }
    @Override
    public void copyProperties(multiblock.Block other){
        ((Block)other).recipe = recipe;
        ((Block)other).isToggled = isToggled;
    }
    @Override
    public Image getBaseTexture(){
        if(isToggled){
            if(template.shield)return template.shieldClosedTexture;
            if(template.coolantVent)return template.coolantVentOutputTexture;
            if(template.parent!=null)return template.portOutputTexture;
        }
        return template.texture;
    }
    @Override
    public Image getTexture(){
        if(isToggled){
            if(template.shield)return template.shieldClosedDisplayTexture;
            if(template.coolantVent)return template.coolantVentOutputDisplayTexture;
            if(template.parent!=null)return template.portOutputDisplayTexture;
        }
        return template.displayTexture;
    }
    @Override
    public String getName(){
        if(isToggled&&template.coolantVent)return template.getCoolantVentOutputDisplayName();
        if(isToggled&&template.parent!=null)return template.getPortOutputDisplayName();
        return template.getDisplayName();
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
        if(recipe!=null){
            tip+="\n"+recipe.getInputDisplayName();
        }
        if(isController())tip+="\nController "+(casingValid?"Valid":"Invalid");
        if(isCasing())tip+="\nCasing "+(casingValid?"Valid":"Invalid");
        if(isFuelCell()){
            if(recipe==null&&!template.fuelCellHasBaseStats)tip+="\nNo Fuel";
            tip+="\nFuel Cell "+(isFuelCellActive()?"Active":"Inactive");
            if(isFuelCellActive()){
                tip+="\nAdjacent moderator lines: "+moderatorLines+"\n"
                        + "Heat Multiplier: "+percent(moderatorLines, 0)+"\n"
                        + "Heat Produced: "+moderatorLines*(recipe==null?template.fuelCellHeat:recipe.fuelCellHeat)+"H/t\n"
                        + "Efficiency: "+percent(efficiency, 0)+"\n"
                        + "Positional Efficiency: "+percent(positionalEfficiency, 0)+"\n"
                        + "Total Neutron Flux: "+neutronFlux+"\n"
                        + "Criticality Factor: "+(recipe==null?template.fuelCellCriticality:recipe.fuelCellCriticality);
            }else{
                tip+="\nTotal Neutron Flux: "+neutronFlux;
                if(template.fuelCellHasBaseStats||recipe!=null){
                    tip+="\nCriticality Factor: "+(recipe==null?template.fuelCellCriticality:recipe.fuelCellCriticality);
                }
            }
            if(isPrimed()){
                tip+="\nPrimed"
                        + "\nNeutron source: "+(source==null?"Self":source.template.getDisplayName());
            }
        }
        if(isModerator()){
            if(recipe==null&&!template.moderatorHasBaseStats)tip+="\nNo Recipe";
            tip+="\nModerator "+(moderatorActive?"Active":(moderatorValid?"Valid":"Invalid"));
            if(template.moderatorHasBaseStats||recipe!=null){
                tip+="\nFlux Factor: "+(recipe==null?template.moderatorFlux:recipe.moderatorFlux)
                        + "\nEfficiency Factor: "+(recipe==null?template.moderatorEfficiency:recipe.moderatorEfficiency);
            }
        }
        if(isReflector()){
            if(recipe==null&&!template.reflectorHasBaseStats)tip+="\nNo Recipe";
            tip+="\nReflector "+(reflectorActive?"Active":"Inactive");
            if(template.reflectorHasBaseStats||recipe!=null){
                tip+="\nReflectivity: "+(recipe==null?template.reflectorReflectivity:recipe.reflectorReflectivity)
                        + "\nEfficiency multiplier: "+(recipe==null?template.reflectorEfficiency:recipe.reflectorEfficiency);
            }
        }
        if(isShield()){
            if(recipe==null&&!template.shieldHasBaseStats)tip+="\nNo Recipe";
            tip+="\nShield "+(shieldActive?"Valid":"Invalid");
            if(template.shieldHasBaseStats||recipe!=null){
                tip+="\nTotal flux: "+neutronFlux
                        + "\nHeat per flux: "+(recipe==null?template.shieldHeat:recipe.shieldHeat)
                        + "\nTotal heat: "+(neutronFlux*(recipe==null?template.shieldHeat:recipe.shieldHeat))+" H/t"
                        + "\nEfficiency factor: "+(recipe==null?template.shieldEfficiency:recipe.shieldEfficiency);
            }
            if(isToggled){
                tip+="\nClosed";
            }
        }
        if(isIrradiator()){
            if(recipe==null&&!template.irradiatorHasBaseStats)tip+="\nNo Recipe";
            tip+="\nIrradiator flux: "+neutronFlux;
            if(template.irradiatorHasBaseStats||recipe!=null){
                tip+="\nEfficiency bonus: "+percent(recipe==null?template.irradiatorEfficiency:recipe.irradiatorEfficiency,0)
                        + "\nHeat per flux: "+(recipe==null?template.irradiatorHeat:recipe.irradiatorHeat)
                        + "\nTotal heat: "+(recipe==null?template.irradiatorHeat:recipe.irradiatorHeat)*neutronFlux+"H/t";
            }
        }
        if(isHeatsink()){
            if(recipe==null&&!template.heatsinkHasBaseStats)tip+="\nNo Recipe";
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
    @Override
    public String getListTooltip(){
        String tip = getName();
        if(isFuelCell()){
            tip+="\nFuel Cell";
            if(template.fuelCellHasBaseStats){
                tip+="\nEfficiency: "+template.fuelCellEfficiency
                        + "\nBase Heat: "+template.fuelCellHeat
                        + "\nCriticality: "+template.fuelCellCriticality;
                if(template.fuelCellSelfPriming)tip+="\nSelf-Priming";
            }else if(template.allRecipes.size()==1){
                tip+="\nEfficiency: "+template.allRecipes.get(0).fuelCellEfficiency
                        + "\nBase Heat: "+template.allRecipes.get(0).fuelCellHeat
                        + "\nCriticality: "+template.allRecipes.get(0).fuelCellCriticality;
                if(template.allRecipes.get(0).fuelCellSelfPriming)tip+="\nSelf-Priming";
            }
        }
        if(isModerator()){
            tip+="\nModerator";
            if(template.moderatorHasBaseStats){
                tip+="\nFlux: "+template.moderatorFlux
                        + "\nEfficiency: "+template.moderatorEfficiency;
            }else if(template.allRecipes.size()==1){
                tip+="\nFlux: "+template.allRecipes.get(0).moderatorFlux
                        + "\nEfficiency: "+template.allRecipes.get(0).moderatorEfficiency;
            }
        }
        if(isReflector()){
            tip+="\nReflector";
            if(template.reflectorHasBaseStats){
                tip+="\nReflectivity: "+template.reflectorReflectivity
                        + "\nEfficiency: "+template.reflectorEfficiency;
            }else if(template.allRecipes.size()==1){
                tip+="\nReflectivity: "+template.allRecipes.get(0).reflectorReflectivity
                        + "\nEfficiency: "+template.allRecipes.get(0).reflectorEfficiency;
            }
        }
        if(isShield()){
            tip+="\nNeutron Shield";
            if(template.shieldHasBaseStats){
                tip+="\nHeat per flux: "+template.shieldHeat
                        + "\nEfficiency: "+template.shieldEfficiency;
            }else if(template.allRecipes.size()==1){
                tip+="\nHeat per flux: "+template.allRecipes.get(0).shieldHeat
                        + "\nEfficiency: "+template.allRecipes.get(0).shieldEfficiency;
            }
        }
        if(isIrradiator()){
            tip+="\nIrradiator";
            if(template.irradiatorHasBaseStats){
                tip+="\nEfficiency: "+template.irradiatorEfficiency
                        +"\nHeat: "+template.irradiatorHeat;
            }else if(template.allRecipes.size()==1){
                tip+="\nEfficiency: "+template.allRecipes.get(0).irradiatorEfficiency
                        +"\nHeat: "+template.allRecipes.get(0).irradiatorHeat;
            }
        }
        if(isHeatsink()){
            tip+="\nHeatsink";
            if(template.heatsinkHasBaseStats){
                tip+="\nCooling: "+template.heatsinkCooling+"H/t";
            }else if(template.allRecipes.size()==1){
                tip+="\nCooling: "+template.allRecipes.get(0).heatsinkCooling+"H/t";
            }
        }
        for(AbstractPlacementRule<?, ?> rule : template.rules){
            tip+="\nRequires "+rule.toString();
        }
        return tip;
    }
    public boolean isPrimed(){
        if(!isFuelCell())return false;
        if(template.fuelCellHasBaseStats||recipe!=null){
            if(recipe==null?template.fuelCellSelfPriming:recipe.fuelCellSelfPriming)return true;//self-priming
        }
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
        return isInert()||isActive()||moderatorValid;
    }
    public boolean isModeratorActive(){
        if(!template.moderatorHasBaseStats&&recipe==null)return false;
        return isModerator()&&moderatorActive&&(recipe==null?template.moderatorActive:recipe.moderatorActive);
    }
    public boolean isFuelCellActive(){
        if(!template.fuelCellHasBaseStats&&recipe==null)return false;
        return isFuelCell()&&neutronFlux>=(recipe==null?template.fuelCellCriticality:recipe.fuelCellCriticality);
    }
    public boolean isFuelCell(){
        return template.fuelCell;
    }
    public boolean isModerator(){
        if(isToggled)return false;
        return template.moderator;
    }
    public boolean isReflector(){
        return template.reflector;
    }
    public boolean isIrradiator(){
        return template.irradiator;
    }
    public boolean isIrradiatorActive(){
        if(!template.irradiatorHasBaseStats&&recipe==null)return false;
        return isIrradiator()&&neutronFlux>0;
    }
    public boolean isShield(){
        if(isToggled)return false;
        return template.shield;
    }
    public boolean isShieldActive(){
        if(!template.shieldHasBaseStats&&recipe==null)return false;
        return isShield()&&shieldActive&&moderatorValid;
    }
    public boolean isHeatsink(){
        return template.heatsink;
    }
    public boolean isHeatsinkActive(){
        if(!template.heatsinkHasBaseStats&&recipe==null)return false;
        return isHeatsink()&&heatsinkValid;
    }
    public boolean isConductor(){
        return template.conductor;
    }
    public boolean isFunctional(){
        if(canCluster()&&(cluster==null||!cluster.isCreated()))return false;
        return template.functional&&(isActive()||moderatorValid);
    }
    public boolean canCluster(){
        return (isActive()||isInert())&&template.cluster;
    }
    @Override
    public void renderOverlay(double x, double y, double width, double height, Multiblock multiblock){
        if(!isValid()){
            drawOutline(x, y, width, height, Core.theme.getBlockColorOutlineInvalid());
        }
        if(isModeratorActive()){
            drawOutline(x, y, width, height, Core.theme.getBlockColorOutlineActive());
        }
        if(recipe!=null&&(template.parent==null?template.allRecipes:template.parent.allRecipes).size()>1){
            Core.applyWhite(template.parent==null?1:0.75f);
            drawRect(x+width*.125, y+height*.125, x+width*.875, y+height*.875, Core.getTexture(recipe.inputDisplayTexture));
        }
        if(template.fuelCell&&(template.fuelCellHasBaseStats||recipe!=null)){
            boolean self = recipe==null?template.fuelCellSelfPriming:recipe.fuelCellSelfPriming;
            Block src = source;
            if(src!=null||self){
                drawCircle(x, y, width, height, Core.theme.getBlockColorSourceCircle(src==null?1:src.template.sourceEfficiency, self));
            }
        }
        OverhaulSFR.Cluster cluster = this.cluster;
        if(cluster!=null){
            Color primaryColor = null;
            if(cluster.netHeat>0){
                primaryColor = Core.theme.getClusterOverheatingColor();
            }
            if(cluster.coolingPenaltyMult<1){
                primaryColor = Core.theme.getClusterOvercoolingColor();
            }
            if(primaryColor!=null){
                Core.applyColor(primaryColor, .125f);
                Renderer2D.drawRect(x, y, x+width, y+height, 0);
                Core.applyColor(primaryColor, .75f);
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
                secondaryColor = Color.PINK;
            }
            if(!cluster.isCreated()){
                secondaryColor = Color.WHITE;
            }
            if(secondaryColor!=null){
                Core.applyColor(secondaryColor, .75f);
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
            drawOutline(x, y, z, width, height, depth, Core.theme.getBlockColorOutlineInvalid(), faceRenderFunc);
        }
        if(isModeratorActive()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getBlockColorOutlineActive(), faceRenderFunc);
        }
        if(template.fuelCell&&(template.fuelCellHasBaseStats||recipe!=null)){
            boolean self = recipe==null?template.fuelCellSelfPriming:recipe.fuelCellSelfPriming;
            if(source!=null||self){
                drawCircle(x, y, z, width, height, depth, Core.theme.getBlockColorSourceCircle(source==null?1:source.template.sourceEfficiency, self), faceRenderFunc);
            }
        }
        OverhaulSFR.Cluster cluster = this.cluster;
        if(cluster!=null){
            double border = width/16;
            Color primaryColor = null;
            if(cluster.netHeat>0){
                primaryColor = Core.theme.getClusterOverheatingColor();
            }
            if(cluster.coolingPenaltyMult<1){
                primaryColor = Core.theme.getClusterOvercoolingColor();
            }
            if(primaryColor!=null){
                Core.applyColor(primaryColor);
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
                secondaryColor = Color.PINK;
            }
            if(!cluster.isCreated()){
                secondaryColor = Color.WHITE;
            }
            if(secondaryColor!=null){
                Core.applyColor(secondaryColor);
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
        return template.cluster&&!template.functional;
    }
    @Override
    public boolean hasRules(){
        return !template.rules.isEmpty();
    }
    @Override
    public boolean calculateRules(Multiblock multiblock){
        for(AbstractPlacementRule<PlacementRule.BlockType, multiblock.configuration.overhaul.fissionsfr.Block> rule : template.rules){
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
        if(!template.heatsink)return false;
        Block other = (Block) oth;
        int totalDist = Math.abs(oth.x-x)+Math.abs(oth.y-y)+Math.abs(oth.z-z);
        if(totalDist>1)return false;//too far away
        if(hasRules()){
            for(AbstractPlacementRule<?, ?> rule : template.rules){
                if(ruleHas((PlacementRule) rule, other))return true;
            }
        }
        return false;
    }
    private boolean ruleHas(AbstractPlacementRule<?, ?> rule, Block b){
        if(rule.block ==b.template)return true;
        for(AbstractPlacementRule<?, ?> rul : rule.rules){
            if(ruleHas(rul, b))return true;
        }
        return false;
    }
    @Override
    public boolean canGroup(){
        if(template.moderator)return false;
        return template.heatsink;
    }
    public multiblock.overhaul.fissionmsr.Block convertToMSR() throws MissingConfigurationEntryException{
        if(template.coolantVent)return null;//remove vents
        multiblock.overhaul.fissionmsr.Block b = new multiblock.overhaul.fissionmsr.Block(getConfiguration(), x, y, z, getConfiguration().overhaul.fissionMSR.convertToMSR(template));
        b.recipe = b.template.convertToMSR(recipe);
        if(b.template.heater&&b.recipe==null&&!b.template.allRecipes.isEmpty())b.recipe = b.template.allRecipes.get(0);
        return b;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.heatsink;
    }
    @Override
    public Block copy(){
        Block copy = new Block(getConfiguration(), x, y, z, template);
        copy.recipe = recipe;
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
        copy.isToggled = isToggled;
        return copy;
    }
    @Override
    public boolean isEqual(multiblock.Block other){
        return other instanceof Block&&((Block)other).template==template;
    }
    @Override
    public void convertTo(Configuration to) throws MissingConfigurationEntryException{
        template = to.overhaul.fissionSFR.convert(template);
        recipe = template.convert(recipe);
        configuration = to;
    }
    public boolean isCasing(){
        return template.casing||template.parent!=null;//is explicitly casing or if it's a port
    }
    public boolean isController(){
        return template.controller;
    }
    /**
     * Adds a neutron source to this block
     * @param sfr the parent SFR
     * @param source the source to add
     * @return If no block was replaced, the new source. Otherwise, the replaced block.
     */
    public Block addNeutronSource(OverhaulSFR sfr, multiblock.configuration.overhaul.fissionsfr.Block source){
        HashMap<int[], Integer> possible = new HashMap<>();
        for(Direction d : directions){
            int i = 0;
            while(true){
                i++;
                if(!sfr.contains(x+d.x*i, y+d.y*i, z+d.z*i)){
                    possible.put(new int[]{x+d.x*(i-1),y+d.y*(i-1),z+d.z*(i-1)}, i);
                    break;
                }
                Block b = sfr.getBlock(x+d.x*i, y+d.y*i, z+d.z*i);
                if(b==null)continue;//air
                if(b.template.blocksLOS)break;
            }
        }
        ArrayList<int[]> keys = new ArrayList<>(possible.keySet());
        keys.sort((o1, o2) -> {
            return possible.get(o1)-possible.get(o2);
        });
        for(int[] key : keys){
            Block was = sfr.getBlock(key[0], key[1], key[2]);
            if(tryAddNeutronSource(sfr, source, key[0], key[1], key[2]))return was==null?sfr.getBlock(key[0], key[1], key[2]):was;
        }
        return null;
    }
    private boolean tryAddNeutronSource(OverhaulSFR sfr, multiblock.configuration.overhaul.fissionsfr.Block source, int X, int Y, int Z){
        Block b = sfr.getBlock(X, Y, Z);
        if(b!=null&&(b.isController()||b.template.coolantVent||b.template.parent!=null||b.template.source))return false;
        sfr.setBlock(X, Y, Z, new Block(sfr.getConfiguration(), X, Y, Z, source));
        return true;
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

    @Override
    public multiblock.configuration.overhaul.fissionsfr.Block getTemplate() {
        return template;
    }
}
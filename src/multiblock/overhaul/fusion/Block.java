package multiblock.overhaul.fusion;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Function;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fusion.PlacementRule;
import planner.Core;
import planner.exception.MissingConfigurationEntryException;
import planner.vr.VRCore;
import simplelibrary.opengl.Renderer2D;
public class Block extends multiblock.Block{
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public multiblock.configuration.overhaul.fusion.Block template;
    public multiblock.configuration.overhaul.fusion.BlockRecipe recipe;
    private boolean breedingBlanketValid;
    private boolean breedingBlanketAugmented;
    private boolean heatsinkValid;
    private boolean reflectorValid;
    public float efficiencyMult;//breeding blankets
    public float heatMult;//heating blankets
    public float efficiency;//heating blankets
    public OverhaulFusionReactor.Cluster cluster;
    public Block(Configuration configuration, int x, int y, int z, multiblock.configuration.overhaul.fusion.Block template){
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
        return template.getDisplayName();
    }
    @Override
    public void clearData(){
        breedingBlanketValid = breedingBlanketAugmented = heatsinkValid = reflectorValid = false;
        cluster = null;
        efficiency = heatMult = efficiencyMult = 0;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
        if(recipe!=null){
            tip+="\n"+recipe.getInputDisplayName();
        }
        OverhaulFusionReactor fusion = (OverhaulFusionReactor)multiblock;
        if(isHeatingBlanket()){
            tip+="\nHeating Blanket "+(isHeatingBlanketActive()?"Active":"Inactive");
            if(isHeatingBlanketActive()){
                tip+="\nHeat Multiplier: "+percent(heatMult, 0)+"\n"
                        + "Heat Produced: "+heatMult*fusion.recipe.heat+"H/t\n"
                        + "Efficiency: "+percent(efficiency, 0)+"\n";
            }
        }
        if(isBreedingBlanket()){
            if(recipe==null||!template.breedingBlanketHasBaseStats)tip+="\nNo Recipe";
            tip+="\nBreeding Blanket "+(breedingBlanketAugmented?"Augmented":(breedingBlanketValid?"Valid":"Invalid"))
                    + "\nEfficiency multiplier: "+round(efficiencyMult, 2)+"\n";
        }
        if(isReflector()){
            if(recipe==null&&!template.reflectorHasBaseStats)tip+="\nNo Recipe";
            tip+="\nReflector "+(reflectorValid?"Valid":"Invalid");
            if(template.reflectorHasBaseStats||recipe!=null){
                tip+="\nEfficiency: "+(recipe==null?template.reflectorEfficiency:recipe.reflectorEfficiency);
            }
        }
        if(isHeatsink()){
            if(recipe==null&&!template.heatsinkHasBaseStats)tip+="\nNo Recipe";
            tip+="\nHeatsink "+(heatsinkValid?"Valid":"Invalid");
            //TODO show heatsink validity check errors
        }
        OverhaulFusionReactor.Cluster cluster = this.cluster;
        if(cluster!=null){
            if(!cluster.isCreated()){
                tip+="\nInvalid cluster!";
            }
            if(!cluster.isConnectedToWall){
                tip+="\nCluster is not connected to a connector!";
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
        if(template.core&&template.getDisplayName().contains("ium")){
            tip+="\nThe stuff that Cores are made out of\n(Not to be confused with Corium)";
        }
        if(isConnector())tip+="\nConnector";
        if(isElectromagnet())tip+="\nElectromagnet";
        if(isConductor())tip+="\nConductor";
        if(isHeatingBlanket())tip+="\nHeating Blanket";
        if(isBreedingBlanket()){
            tip+="\nBreeding Blanket";
            if(template.breedingBlanketHasBaseStats){
                tip+="\nEfficiency: "+template.breedingBlanketEfficiency;
                tip+="\nHeat: "+template.breedingBlanketHeat;
            }
        }
        if(isReflector()){
            tip+="\nReflector";
            if(template.reflectorHasBaseStats){
                tip+="\nEfficiency: "+template.reflectorEfficiency;
            }
        }
        if(isShielding()){
            tip+="\nShielding";
            if(template.shieldingHasBaseStats){
                tip+="\nShieldiness: "+template.shieldingShieldiness;
            }
        }
        if(isHeatsink()){
            tip+="\nHeatsink";
            if(template.heatsinkHasBaseStats){
                tip+="\nCooling: "+template.heatsinkCooling+"H/t";
            }
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
        if(isBreedingBlanketAugmented()){
            drawOutline(x, y, width, height, Core.theme.getGreen());
        }
        OverhaulFusionReactor.Cluster cluster = this.cluster;
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
        if(isBreedingBlanketAugmented()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getGreen(), faceRenderFunc);
        }
        OverhaulFusionReactor.Cluster cluster = this.cluster;
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
        return template.cluster&&!template.functional;
    }
    @Override
    public boolean isValid(){
        return isInert()||isActive()||breedingBlanketValid;
    }
    @Override
    public boolean isActive(){
        return isConductor()||isBreedingBlanketAugmented()||isHeatingBlanketActive()||isHeatsinkActive()||isReflectorActive()||isShielding()||template.core||isConnector()||isElectromagnet();
    }
    public boolean isConductor(){
        return template.conductor;
    }
    public boolean isConnector(){
        return template.connector;
    }
    public boolean isBreedingBlanketAugmented(){
        if(!template.breedingBlanketHasBaseStats&&recipe==null)return false;
        return isBreedingBlanket()&&breedingBlanketAugmented&&(recipe==null?template.breedingBlanketAugmented:recipe.breedingBlanketAugmented);
    }
    public boolean isBreedingBlanket(){
        return template.breedingBlanket;
    }
    public boolean isHeatingBlanketActive(){
        return isHeatingBlanket();
    }
    public boolean isHeatingBlanket(){
        return template.heatingBlanket;
    }
    public boolean isHeatsinkActive(){
        if(!template.heatsinkHasBaseStats&&recipe==null)return false;
        return isHeatsink()&&heatsinkValid;
    }
    public boolean isHeatsink(){
        return template.heatsink;
    }
    public boolean isShielding(){
        return template.shielding;
    }
    public boolean isReflector(){
        return template.reflector;
    }
    public boolean isReflectorActive(){
        return isReflector()&&reflectorValid;
    }
    @Override
    public boolean isCore(){
        return isReflector()||isBreedingBlanket()||isHeatingBlanket();
    }
    @Override
    public boolean hasRules(){
        return !template.rules.isEmpty();
    }
    @Override
    public boolean calculateRules(Multiblock multiblock){
        for(PlacementRule rule : template.rules){
            if(!rule.isValid(this, (OverhaulFusionReactor) multiblock)){
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
        if(template.breedingBlanket)return false;
        return template.heatsink;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.heatsink;
    }
    @Override
    public multiblock.Block copy(){
        Block copy = new Block(getConfiguration(), x, y, z, template);
        copy.recipe = recipe;
        copy.breedingBlanketValid = breedingBlanketValid;
        copy.breedingBlanketAugmented = breedingBlanketAugmented;
        copy.heatsinkValid = heatsinkValid;
        copy.reflectorValid = reflectorValid;
        copy.efficiencyMult = efficiencyMult;
        copy.heatMult = heatMult;
        copy.efficiency = efficiency;
        copy.cluster = cluster;//TODO probably shouldn't do that
        return copy;
    }
    @Override
    public boolean isEqual(multiblock.Block other){
        return other instanceof Block&&((Block)other).template==template;
    }
    public boolean canCluster(){
        return template.cluster;
    }
    public boolean isElectromagnet(){
        return template.electromagnet;
    }
    public void calculateBreedingBlanket(OverhaulFusionReactor reactor){
        if(!template.breedingBlanket)return;
        if(!template.breedingBlanketHasBaseStats&&recipe==null)return;//empty breeding blanket
        breedingBlanketValid = true;//shrug
        efficiencyMult = 1;
        FOR:for(Direction d : directions){
            int X = x+d.x;
            int Y = y+d.y;
            int Z = z+d.z;
            boolean foundPlasma = false;
            while(reactor.getLocationCategory(X, Y, Z)==OverhaulFusionReactor.LocationCategory.PLASMA){
                foundPlasma = true;
                X+=d.x;
                Y+=d.y;
                Z+=d.z;
            }
            if(!foundPlasma)continue;
            Block b = reactor.getBlock(X, Y, Z);
            if(b==null)continue;
            if(b.isReflector()){
                if(!b.template.reflectorHasBaseStats&&b.recipe==null)continue;//empty reflector
                b.reflectorValid = true;
                efficiencyMult += (b.recipe==null?b.template.reflectorEfficiency:b.recipe.reflectorEfficiency)-1;
                breedingBlanketAugmented = true;
            }
        }
    }
    public void calculateHeatingBlanket(OverhaulFusionReactor reactor){
        if(!template.heatingBlanket)return;
        heatMult = 1;
        efficiency = 1;
        for(Direction d : directions){
            Block b = reactor.getBlock(x+d.x, y+d.y, z+d.z);
            if(b==null)continue;
            if(b.isBreedingBlanket()){
                if(!b.template.breedingBlanketHasBaseStats&&b.recipe==null)continue;//empty reflector
                efficiency+=(b.recipe==null?b.template.breedingBlanketEfficiency:b.recipe.breedingBlanketEfficiency)*b.efficiencyMult;
                heatMult+=(b.recipe==null?b.template.breedingBlanketEfficiency:b.recipe.breedingBlanketEfficiency)*b.efficiencyMult;
            }
        }
    }
    /**
     * Calculates the heatsink
     * @param reactor the reactor
     * @return <code>true</code> if the heatsink state has changed
     */
    public boolean calculateHeatsink(OverhaulFusionReactor reactor){
        if(!isHeatsink())return false;
        if(!template.heatsinkHasBaseStats&&recipe==null)return false;//empty heatsink
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
    public boolean isFunctional(){
        if(canCluster()&&(cluster==null||!cluster.isCreated()))return false;
        return template.functional&&(isActive()||breedingBlanketValid);
    }
    @Override
    public void convertTo(Configuration to) throws MissingConfigurationEntryException{
        template = to.overhaul.fusion.convert(template);
        recipe = template.convert(recipe);
        configuration = to;
    }
    @Override
    public Iterable<String> getSearchableNames(){
        ArrayList<String> searchables = template.getSearchableNames();
        for(String s : getListTooltip().split("\n"))searchables.add(s.trim());
        return searchables;
    }
}
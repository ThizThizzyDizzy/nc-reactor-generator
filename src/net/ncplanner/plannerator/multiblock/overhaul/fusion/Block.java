package net.ncplanner.plannerator.multiblock.overhaul.fusion;
import java.util.ArrayList;
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
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BreedingBlanketRecipe;
public class Block extends AbstractBlock{
    public BlockElement template;
    public BreedingBlanketRecipe breedingBlanketRecipe;
    private boolean breedingBlanketValid;
    private boolean breedingBlanketAugmented;
    private boolean heatsinkValid;
    private boolean reflectorValid;
    public float efficiencyMult;//breeding blankets
    public float heatMult;//heating blankets
    public float efficiency;//heating blankets
    public OverhaulFusionReactor.Cluster cluster;
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
    public void copyProperties(net.ncplanner.plannerator.multiblock.AbstractBlock other){
        ((Block)other).breedingBlanketRecipe = breedingBlanketRecipe;
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
        if(breedingBlanketRecipe!=null){
            tip+="\n"+breedingBlanketRecipe.getDisplayName();
        }
        OverhaulFusionReactor fusion = (OverhaulFusionReactor)multiblock;
        if(isHeatingBlanket()){
            tip+="\nHeating Blanket "+(isHeatingBlanketActive()?"Active":"Inactive");
            if(isHeatingBlanketActive()){
                tip+="\nHeat Multiplier: "+MathUtil.percent(heatMult, 0)+"\n"
                        + "Heat Produced: "+heatMult*fusion.recipe.heat+"H/t\n"
                        + "Efficiency: "+MathUtil.percent(efficiency, 0)+"\n";
            }
        }
        if(isBreedingBlanket()){
            if(breedingBlanketRecipe==null)tip+="\nNo Recipe";
            tip+="\nBreeding Blanket "+(breedingBlanketAugmented?"Augmented":(breedingBlanketValid?"Valid":"Invalid"))
                    + "\nEfficiency multiplier: "+MathUtil.round(efficiencyMult, 2)+"\n";
        }
        if(isReflector()){
            tip+="\nReflector "+(reflectorValid?"Valid":"Invalid");
            tip+="\nEfficiency: "+template.reflector.efficiency;
        }
        if(isHeatsink()){
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
        String tip = super.getListTooltip();
        if(template.core!=null&&template.getDisplayName().contains("ium")){
            tip+="\nThe stuff that Cores are made out of\n(Not to be confused with Corium)";
        }
        return tip;
    }
    @Override
    public void renderOverlay(Renderer renderer, float x, float y, float z, float width, float height, float depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        if(!isValid()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineInvalid(), faceRenderFunc);
        }
        if(isBreedingBlanketAugmented()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineActive(), faceRenderFunc);
        }
        OverhaulFusionReactor.Cluster cluster = this.cluster;
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
    public boolean isValid(){
        return isConductor()||isActive()||breedingBlanketValid;
    }
    @Override
    public boolean isActive(){
        return isConductor()||isBreedingBlanketAugmented()||isHeatingBlanketActive()||isHeatsinkActive()||isReflectorActive()||isShielding()||template.core!=null||isConnector()||isElectromagnet();
    }
    public boolean isConductor(){
        return template.conductor!=null;
    }
    public boolean isConnector(){
        return template.connector!=null;
    }
    public boolean isBreedingBlanketAugmented(){
        if(breedingBlanketRecipe==null)return false;
        return isBreedingBlanket()&&breedingBlanketAugmented&&breedingBlanketRecipe.stats.augmented;
    }
    public boolean isBreedingBlanket(){
        return template.breedingBlanket!=null;
    }
    public boolean isHeatingBlanketActive(){
        return isHeatingBlanket();
    }
    public boolean isHeatingBlanket(){
        return template.heatingBlanket!=null;
    }
    public boolean isHeatsinkActive(){
        return isHeatsink()&&heatsinkValid;
    }
    public boolean isHeatsink(){
        return template.heatsink!=null;
    }
    public boolean isShielding(){
        return template.shielding!=null;
    }
    public boolean isReflector(){
        return template.reflector!=null;
    }
    public boolean isReflectorActive(){
        return isReflector()&&reflectorValid;
    }
    @Override
    public boolean isCore(){
        return isReflector()||isBreedingBlanket()||isHeatingBlanket();
    }
    @Override
    public List<? extends NCPFPlacementRule> getRules(){
        if(template.heatsink!=null)return template.heatsink.rules;
        return new ArrayList<>();
    }
    @Override
    public boolean canRequire(net.ncplanner.plannerator.multiblock.AbstractBlock oth){
        if(template.heatsink!=null)return requires(oth, null);
        Block other = (Block) oth;
        if(template.conductor!=null)return other.canCluster();
        return false;
    }
    @Override
    public boolean canGroup(){
        return template.heatsink!=null;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.heatsink!=null;
    }
    @Override
    public net.ncplanner.plannerator.multiblock.AbstractBlock copy(){
        Block copy = new Block(getConfiguration(), x, y, z, template);
        copy.breedingBlanketRecipe = breedingBlanketRecipe;
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
    public boolean isEqual(net.ncplanner.plannerator.multiblock.AbstractBlock other){
        return other instanceof Block&&((Block)other).template==template;
    }
    public boolean canCluster(){
        return isConductor()||(isActive()&&(isHeatingBlanket()||isBreedingBlanket()||isHeatsink()));
    }
    public boolean isElectromagnet(){
        return template.poloid!=null||template.toroid!=null;
    }
    public void calculateBreedingBlanket(OverhaulFusionReactor reactor){
        if(template.breedingBlanket==null)return;
        if(breedingBlanketRecipe==null)return;//empty breeding blanket
        breedingBlanketValid = true;//shrug
        efficiencyMult = 1;
        FOR:for(Direction d : Direction.values()){
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
                b.reflectorValid = true;
                efficiencyMult += b.template.reflector.efficiency-1;
                breedingBlanketAugmented = true;
            }
        }
    }
    public void calculateHeatingBlanket(OverhaulFusionReactor reactor){
        if(template.heatingBlanket==null)return;
        heatMult = 1;
        efficiency = 1;
        for(Direction d : Direction.values()){
            Block b = reactor.getBlock(x+d.x, y+d.y, z+d.z);
            if(b==null)continue;
            if(b.isBreedingBlanket()){
                if(b.breedingBlanketRecipe==null)continue;//empty blanket
                efficiency+=b.breedingBlanketRecipe.stats.efficiency*b.efficiencyMult;
                heatMult+=b.breedingBlanketRecipe.stats.efficiency*b.efficiencyMult;
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
        boolean wasValid = heatsinkValid;
        for(NCPFPlacementRule rule : getRules()){
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
        return (isHeatingBlanket()||isBreedingBlanket()||isReflector()||isHeatsink()||isShielding())&&(isActive()||breedingBlanketValid);
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
        return template.breedingBlanket!=null;
    }
    @Override
    public List<? extends IBlockRecipe> getRecipes(){
        return template.breedingBlanketRecipes;
    }
    @Override
    public NCPFElement getRecipe(){
        return breedingBlanketRecipe;
    }
    @Override
    public void setRecipe(NCPFElement recipe){
        if(template.breedingBlanket!=null)breedingBlanketRecipe = (BreedingBlanketRecipe)recipe;
        else throw new IllegalArgumentException("Tried to set block recipe to "+template.definition.toString()+", but it can't have recipes!");
    }
    @Override
    public boolean isToggled(){
        return false;
    }
    @Override
    public void setToggled(boolean toggled){}
}
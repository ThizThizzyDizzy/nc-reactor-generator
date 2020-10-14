package multiblock.overhaul.fusion;
import java.awt.Color;
import java.awt.image.BufferedImage;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.configuration.overhaul.fusion.PlacementRule;
import planner.Core;
import simplelibrary.opengl.Renderer2D;
public class Block extends multiblock.Block{
    /**
     * MUST ONLY BE SET WHEN MERGING CONFIGURATIONS!!!
     */
    public multiblock.configuration.overhaul.fusion.Block template;
    public multiblock.configuration.overhaul.fusion.BreedingBlanketRecipe breedingBlanketRecipe;
    private boolean breedingBlanketValid;
    private boolean breedingBlanketAugmented;
    private boolean heatsinkValid;
    private boolean reflectorValid;
    public float efficiencyMult;//breeding blankets
    public float heatMult;//heating blankets
    public float efficiency;//heating blankets
    boolean inCluster;
    public Block(int x, int y, int z, multiblock.configuration.overhaul.fusion.Block template){
        super(x, y, z);
        this.template = template;
    }
    @Override
    public multiblock.Block newInstance(int x, int y, int z){
        return new Block(x, y, z, template);
    }
    @Override
    public void copyProperties(multiblock.Block other){
        ((Block)other).breedingBlanketRecipe = breedingBlanketRecipe;
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
        breedingBlanketValid = breedingBlanketAugmented = heatsinkValid = reflectorValid = inCluster = false;
        efficiency = heatMult = efficiencyMult = 0;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
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
            tip+="\nBreeding Blanket "+(breedingBlanketAugmented?"Augmented":(breedingBlanketValid?"Valid":"Invalid"))+"\n"
                    +"Recipe: "+breedingBlanketRecipe.name
                    + "Efficiency multiplier: "+round(efficiencyMult, 2)+"\n";
        }
        if(isReflector()){
            tip+="\nReflector "+(reflectorValid?"Valid":"Invalid")+"\n"
                    + "Efficiency: "+template.efficiency;
        }
        if(isHeatsink()){
            tip+="\nHeatsink "+(heatsinkValid?"Valid":"Invalid");
        }
        OverhaulFusionReactor.Cluster cluster = fusion.getCluster(this);
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
        if(template.core&&template.name.contains("ium")){
            tip+="\nThe stuff that Cores are made out of\n(Not to be confused with Corium)";
        }
        if(isConnector())tip+="\nConnector";
        if(isElectromagnet())tip+="\nElectromagnet";
        if(isConductor())tip+="\nConductor";
        if(isHeatingBlanket())tip+="\nHeating Blanket";
        if(isBreedingBlanket())tip+="\nBreeding Blanket";
        if(isReflector())tip+="\nReflector"
                + "\nEfficiency: "+template.efficiency;
        if(isShielding())tip+="\nShielding"
                + "\nShieldiness: "+template.shieldiness;
        if(isHeatsink())tip+="\nHeatsink"
                + "\nCooling: "+template.cooling+"H/t";
        for(PlacementRule rule : template.rules){
            tip+="\nRequires "+rule.toString();
        }
        return tip;
    }
    @Override
    public void renderOverlay(double x, double y, double width, double height, Multiblock multiblock){
        if(!isValid()){
            drawOutline(x, y, width, height, 1/32d, Core.theme.getRed());
        }
        if(isBreedingBlanketAugmented()){
            drawOutline(x, y, width, height, 1/32d, Core.theme.getGreen());
        }
        OverhaulFusionReactor fusion = (OverhaulFusionReactor)multiblock;
        OverhaulFusionReactor.Cluster cluster = fusion.getCluster(this);
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
        return isBreedingBlanket()&&breedingBlanketAugmented&&template.augmentedBreedingBlanket;
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
        return isHeatsink()&&heatsinkValid;
    }
    public boolean isHeatsink(){
        return template.cooling!=0;
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
    public boolean isCasing(){
        return false;//no casings!
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
        if(template.breedingBlanket)return false;
        return template.cooling!=0;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.cooling!=0;
    }
    @Override
    public multiblock.Block copy(){
        Block copy = new Block(x, y, z, template);
        copy.breedingBlanketValid = breedingBlanketValid;
        copy.breedingBlanketAugmented = breedingBlanketAugmented;
        copy.heatsinkValid = heatsinkValid;
        copy.reflectorValid = reflectorValid;
        copy.efficiencyMult = efficiencyMult;
        copy.heatMult = heatMult;
        copy.efficiency = efficiency;
        copy.inCluster = inCluster;
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
                b.reflectorValid = true;
                efficiencyMult += (b.template.efficiency)-1;
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
                efficiency+=b.breedingBlanketRecipe.efficiency*b.efficiencyMult;
                heatMult+=b.breedingBlanketRecipe.efficiency*b.efficiencyMult;
            }
        }
    }
    /**
     * Calculates the heatsink
     * @param reactor the reactor
     * @return <code>true</code> if the heatsink state has changed
     */
    public boolean calculateHeatsink(OverhaulFusionReactor reactor){
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
    public boolean isFunctional(){
        if(isCasing())return false;
        if(canCluster()&&!inCluster)return false;
        return template.functional&&(isActive()||breedingBlanketValid);
    }
}
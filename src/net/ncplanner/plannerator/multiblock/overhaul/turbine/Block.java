package net.ncplanner.plannerator.multiblock.overhaul.turbine;
import java.util.ArrayList;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.multiblock.configuration.ITemplateAccess;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.PlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
public class Block extends net.ncplanner.plannerator.multiblock.Block implements ITemplateAccess<net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block> {
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block template;
    public boolean valid;
    public Block(Configuration configuration, int x, int y, int z, net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block block){
        super(configuration, x, y, z);
        this.template = block;
        if(template==null)throw new IllegalArgumentException("Cannot create null block!");
    }
    @Override
    public net.ncplanner.plannerator.multiblock.Block newInstance(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, template);
    }
    @Override
    public void copyProperties(net.ncplanner.plannerator.multiblock.Block other){}
    @Override
    public Image getBaseTexture(){
        return template.texture;
    }
    @Override
    public Image getTexture(){
        return template.displayTexture;
    }
    @Override
    public String getBaseName(){
        return template.name;
    }
    @Override
    public String getName(){
        return template.getDisplayName();
    }
    @Override
    public void clearData(){
        valid = false;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
        if(template.casing)tip+="\nCasing "+(isActive()?"Active":"Invalid");
        if(isConnector())tip+="\nConnector "+(isActive()?"Active":"Invalid");
        if(isCoil())tip+="\nCoil "+(isActive()?"Active":"Invalid");
        return tip;
    }
    @Override
    public String getListTooltip(){
        String tip = getName();
        if(isBearing())tip+="\nBearing";
        if(isBlade()){
            if(template.bladeStator){
                tip+="\nStator"
                    + "\nExpansion Coefficient: "+template.bladeExpansion;
                if(template.bladeEfficiency>0){
                    tip+="\nEfficiency: "+template.bladeEfficiency;
                }
            }else{
                tip+="\nBlade"
                    + "\nExpansion Coefficient: "+template.bladeExpansion
                    + "\nEfficiency: "+template.bladeEfficiency;
            }
        }
        if(isConnector())tip+="\nConnector";
        if(isCoil()){
            tip+="\nCoil"
                + "\nEfficiency: "+template.coilEfficiency;
        }
        if(template!=null){
            for(AbstractPlacementRule<PlacementRule.BlockType, net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block> rule : template.rules){
                tip+="\nRequires "+rule.toString();
            }
        }
        return tip;
    }
    @Override
    public void renderOverlay(Renderer renderer, float x, float y, float z, float width, float height, float depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        if(!isValid()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineInvalid(), faceRenderFunc);
        }
        if(isActive()&&isCoil()){
            drawOutline(renderer, x, y, z, width, height, depth, Core.theme.getBlockColorOutlineActive(), faceRenderFunc);
        }
    }
    @Override
    public boolean isValid(){
        return valid;
    }
    @Override
    public boolean isActive(){
        return valid;
    }
    @Override
    public boolean isCore(){
        return isBearing()||isBlade();
    }
    @Override
    public boolean hasRules(){
        return !template.rules.isEmpty();
    }
    @Override
    public boolean calculateRules(Multiblock multiblock){
        for(AbstractPlacementRule<PlacementRule.BlockType, net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block> rule : template.rules){
            if(!rule.isValid(this, multiblock)){
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean matches(net.ncplanner.plannerator.multiblock.Block template){
        if(template==null)return false;
        if(template instanceof Block){
            return ((Block)template).template==this.template;
        }
        return false;
    }
    @Override
    public boolean canRequire(net.ncplanner.plannerator.multiblock.Block oth){
        if(template.coil||template.connector)return requires(oth, null);
        return false;
    }
    @Override
    public boolean requires(net.ncplanner.plannerator.multiblock.Block oth, Multiblock mb){
        if(!isCoil()&&!isConnector())return false;
        Block other = (Block) oth;
        int totalDist = Math.abs(oth.x-x)+Math.abs(oth.y-y)+Math.abs(oth.z-z);
        if(totalDist>1)return false;//too far away
        if(hasRules()){
            for(AbstractPlacementRule<PlacementRule.BlockType, net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block> rule : template.rules){
                if(ruleHas((PlacementRule) rule, other))return true;
            }
        }
        return false;
    }
    private boolean ruleHas(PlacementRule rule, Block b){
        if(rule.block==b.template)return true;
        if(rule.blockType!=null&&rule.blockType.blockMatches(null, b))return true;
        for(AbstractPlacementRule<PlacementRule.BlockType, net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block> rul : rule.rules){
            if(ruleHas((PlacementRule) rul, b))return true;
        }
        return false;
    }
    @Override
    public boolean canGroup(){
        return template.coil||template.connector;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.coil||template.connector||(template.casing&&!template.controller&&!template.inlet&&!template.outlet);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.Block copy(){
        Block copy = new Block(getConfiguration(),x,y,z,template);
        copy.valid = valid;
        return copy;
    }
    @Override
    public boolean isEqual(net.ncplanner.plannerator.multiblock.Block other){
        return other instanceof Block&&((Block)other).template==template;
    }
    public boolean isBlade(){
        return template.blade;
    }
    public boolean isBearing(){
        return template.bearing;
    }
    public boolean isCoil(){
        return template.coil;
    }
    public boolean isConnector(){
        return template.connector;
    }
    @Override
    public boolean isFullBlock(){
        return !isBlade();
    }
    @Override
    public void convertTo(Configuration to) throws MissingConfigurationEntryException{
        template = to.overhaul.turbine.convert(template);
        configuration = to;
    }
    @Override
    public boolean shouldRenderFace(net.ncplanner.plannerator.multiblock.Block against){
        if(super.shouldRenderFace(against))return true;
        if(template.blade||((Block)against).template.blade)return true;
        if(template==((Block)against).template)return false;
        return Core.hasAlpha(against.getBaseTexture());
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
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block getTemplate() {
        return template;
    }
    @Override
    public String getPinnedName(){
        return template.getPinnedName();
    }
    @Override
    public boolean hasRecipes(){
        return false;
    }
    @Override
    public ArrayList<? extends IBlockRecipe> getRecipes(){
        return new ArrayList<>();
    }
}
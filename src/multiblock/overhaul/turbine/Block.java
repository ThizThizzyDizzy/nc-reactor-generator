package multiblock.overhaul.turbine;
import java.util.ArrayList;
import java.util.function.Function;
import multiblock.Direction;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.turbine.PlacementRule;
import planner.Core;
import planner.exception.MissingConfigurationEntryException;
import simplelibrary.image.Image;
public class Block extends multiblock.Block{
    public multiblock.configuration.overhaul.turbine.Block template;
    public boolean valid;
    public Block(Configuration configuration, int x, int y, int z, multiblock.configuration.overhaul.turbine.Block block){
        super(configuration, x, y, z);
        this.template = block;
        if(template==null)throw new IllegalArgumentException("Cannot create null block!");
    }
    @Override
    public multiblock.Block newInstance(int x, int y, int z){
        return new Block(getConfiguration(), x, y, z, template);
    }
    @Override
    public void copyProperties(multiblock.Block other){}
    @Override
    public Image getBaseTexture(){
        return template.texture;
    }
    @Override
    public Image getTexture(){
        return template.displayTexture;
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
            for(PlacementRule rule : template.rules){
                tip+="\nRequires "+rule.toString();
            }
        }
        return tip;
    }
    @Override
    public void renderOverlay(double x, double y, double width, double height, Multiblock multiblock){
        if(!isValid()){
            drawOutline(x, y, width, height, Core.theme.getRed());
        }
        if(isActive()&&isCoil()){
            drawOutline(x, y, width, height, Core.theme.getGreen());
        }
    }
    @Override
    public void renderOverlay(double x, double y, double z, double width, double height, double depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        if(!isValid()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getRed(), faceRenderFunc);
        }
        if(isActive()&&isCoil()){
            drawOutline(x, y, z, width, height, depth, Core.theme.getGreen(), faceRenderFunc);
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
        for(PlacementRule rule : template.rules){
            if(!rule.isValid(this, (OverhaulTurbine) multiblock)){
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean matches(multiblock.Block template){
        if(template==null)return false;
        if(template instanceof Block){
            return ((Block)template).template==this.template;
        }
        return false;
    }
    @Override
    public boolean requires(multiblock.Block oth, Multiblock mb){
        if(!isCoil()&&!isConnector())return false;
        Block other = (Block) oth;
        int totalDist = Math.abs(oth.x-x)+Math.abs(oth.y-y)+Math.abs(oth.z-z);
        if(totalDist>1)return false;//too far away
        if(isConnector()&&other.isCoil())return true;
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
        return template.coil||template.connector;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.coil||template.connector||(template.casing&&!template.controller&&!template.inlet&&!template.outlet);
    }
    @Override
    public multiblock.Block copy(){
        Block copy = new Block(getConfiguration(),x,y,z,template);
        copy.valid = valid;
        return copy;
    }
    @Override
    public boolean isEqual(multiblock.Block other){
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
    public boolean shouldRenderFace(multiblock.Block against){
        if(super.shouldRenderFace(against))return true;
        if(template.blade||((Block)against).template.blade)return true;
        if(template==((Block)against).template)return false;
        return Core.hasAlpha(against.getBaseTexture());
    }
    @Override
    public Iterable<String> getSearchableNames(){
        ArrayList<String> searchables = template.getSearchableNames();
        for(String s : getListTooltip().split("\n"))searchables.add(s.trim());
        return searchables;
    }
}
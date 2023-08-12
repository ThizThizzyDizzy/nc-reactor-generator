package net.ncplanner.plannerator.multiblock.overhaul.turbine;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement;
public class Block extends AbstractBlock{
    public BlockElement template;
    public boolean valid;
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
    public void copyProperties(net.ncplanner.plannerator.multiblock.AbstractBlock other){}
    @Override
    public void clearData(){
        valid = false;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
        if(template.casing!=null)tip+="\nCasing "+(isActive()?"Active":"Invalid");
        if(isConnector())tip+="\nConnector "+(isActive()?"Active":"Invalid");
        if(isCoil())tip+="\nCoil "+(isActive()?"Active":"Invalid");
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
    public List<? extends NCPFPlacementRule> getRules(){
        if(template.coil!=null)return template.coil.rules;
        if(template.connector!=null)return template.connector.rules;
        return new ArrayList<>();
    }
    @Override
    public boolean canRequire(net.ncplanner.plannerator.multiblock.AbstractBlock oth){
        if(template.coil!=null||template.connector!=null)return requires(oth, null);
        return false;
    }
    @Override
    public boolean canGroup(){
        return template.coil!=null||template.connector!=null;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return template.coil!=null||template.connector!=null||
                (template.casing!=null&&template.controller==null&&template.inlet==null&&template.outlet==null);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.AbstractBlock copy(){
        Block copy = new Block(getConfiguration(),x,y,z,template);
        copy.valid = valid;
        return copy;
    }
    @Override
    public boolean isEqual(net.ncplanner.plannerator.multiblock.AbstractBlock other){
        return other instanceof Block&&((Block)other).template==template;
    }
    public boolean isBlade(){
        return template.blade!=null||template.stator!=null;
    }
    public boolean isBearing(){
        return template.bearing!=null;
    }
    public boolean isCoil(){
        return template.coil!=null;
    }
    public boolean isConnector(){
        return template.connector!=null;
    }
    @Override
    public boolean isFullBlock(){
        return !isBlade();
    }
    @Override
    public boolean shouldRenderFace(net.ncplanner.plannerator.multiblock.AbstractBlock against){
        if(super.shouldRenderFace(against))return true;
        if(template.blade!=null||((Block)against).template.blade!=null)return true;
        if(template.stator!=null||((Block)against).template.stator!=null)return true;
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
    public NCPFElement getTemplate(){
        return template;
    }
    @Override
    public boolean hasRecipes(){
        return false;
    }
    @Override
    public ArrayList<? extends IBlockRecipe> getRecipes(){
        return new ArrayList<>();
    }
    @Override
    public NCPFElement getRecipe(){
        return null;
    }
    @Override
    public void setRecipe(NCPFElement recipe){}
    @Override
    public boolean isToggled(){
        return false;
    }
    @Override
    public void setToggled(boolean toggled){}
}
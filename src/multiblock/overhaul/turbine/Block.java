package multiblock.overhaul.turbine;
import java.awt.image.BufferedImage;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.turbine.PlacementRule;
import planner.Core;
public class Block extends multiblock.Block{
    public static Block casing(Configuration configuration, int x, int y, int z){
        Block casing = new Block(configuration, x, y, z);
        return casing;
    }
    public multiblock.configuration.overhaul.turbine.Blade blade;
    public multiblock.configuration.overhaul.turbine.Coil coil;
    private boolean valid;
    private Block(Configuration configuration, int x, int y, int z){
        super(configuration, x,y,z);
    }
    public Block(Configuration configuration, int z, multiblock.configuration.overhaul.turbine.Blade blade){
        this(configuration, 0, 0, z);
        this.blade = blade;
    }
    public Block(Configuration configuration, int x, int y, int z, multiblock.configuration.overhaul.turbine.Coil coil){
        this(configuration, x, y, z);
        this.coil = coil;
    }
    @Override
    public multiblock.Block newInstance(int x, int y, int z){
        if(blade!=null)return new Block(getConfiguration(), z, blade);
        return new Block(getConfiguration(), x, y, z, coil);
    }
    @Override
    public void copyProperties(multiblock.Block other){}
    @Override
    public BufferedImage getBaseTexture(){
        if(blade==null&&coil==null)return OverhaulTurbine.shaftTexture;
        return blade==null?coil.texture:blade.texture;
    }
    @Override
    public BufferedImage getTexture(){
        if(blade==null&&coil==null)return OverhaulTurbine.shaftTexture;
        return blade==null?coil.displayTexture:blade.displayTexture;
    }
    @Override
    public String getName(){
        if(coil==null&&blade==null)return "Rotor Shaft";
        return blade==null?coil.name:blade.name;
    }
    @Override
    public void clearData(){
        valid = false;
    }
    /**
     * Calculates the coil
     * @param reactor the reactor
     * @return <code>true</code> if the coil state has changed
     */
    public boolean calculateCoil(OverhaulTurbine reactor){
        if(isCoil()||isConnector()){
            boolean wasValid = valid;
            for(PlacementRule rule : coil.rules){
                if(!rule.isValid(this, reactor)){
                    valid = false;
                    return wasValid!=valid;
                }
            }
            valid = true;
            return wasValid!=valid;
        }
        return false;
    }
    @Override
    public String getTooltip(Multiblock multiblock){
        String tip = getName();
        if(isConnector())tip+="\nConnector "+(isActive()?"Active":"Invalid");
        if(isCoil())tip+="\nCoil "+(isActive()?"Active":"Invalid");
        return tip;
    }
    @Override
    public String getListTooltip(){
        String tip = getName();
        if(isBearing())tip+="\nBearing";
        if(isBlade()){
            if(blade.stator){
                tip+="\nStator"
                    + "\nExpansion Coefficient: "+blade.expansion;
                if(blade.efficiency>0){
                    tip+="\nEfficiency: "+blade.efficiency;
                }
            }else{
                tip+="\nBlade"
                    + "\nExpansion Coefficient: "+blade.expansion
                    + "\nEfficiency: "+blade.efficiency;
            }
        }
        if(isConnector())tip+="\nConnector";
        if(isCoil()){
            tip+="\nCoil"
                + "\nEfficiency: "+coil.efficiency;
        }
        if(coil!=null){
            for(PlacementRule rule : coil.rules){
                tip+="\nRequires "+rule.toString();
            }
        }
        return tip;
    }
    @Override
    public void renderOverlay(double x, double y, double width, double height, Multiblock multiblock){
        if(!isValid()){
            drawOutline(x, y, width, height, 1/32d, Core.theme.getRed());
        }
        if(isActive()&&!isBearing()){
            drawOutline(x, y, width, height, 1/32d, Core.theme.getGreen());
        }
    }
    @Override
    public boolean isValid(){
        return isCasing()||isBearing()||isBlade()||valid;
    }
    @Override
    public boolean isActive(){
        return isBearing()||valid;
    }
    @Override
    public boolean isCore(){
        return isBearing()||isBlade();
    }
    @Override
    public boolean isCasing(){
        return coil==null&&blade==null;
    }
    @Override
    public boolean hasRules(){
        return coil!=null&&!coil.rules.isEmpty();
    }
    @Override
    public boolean calculateRules(Multiblock multiblock){
        for(PlacementRule rule : coil.rules){
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
            return ((Block)template).blade==this.blade&&((Block)template).coil==this.coil;
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
            for(PlacementRule rule : coil.rules){
                if(ruleHas(rule, other))return true;
            }
        }
        return false;
    }
    private boolean ruleHas(PlacementRule rule, Block b){
        if(rule.coil==b.coil)return true;
        for(PlacementRule rul : rule.rules){
            if(ruleHas(rul, b))return true;
        }
        return false;
    }
    @Override
    public boolean canGroup(){
        return coil!=null;
    }
    @Override
    public boolean canBeQuickReplaced(){
        return coil!=null;
    }
    @Override
    public multiblock.Block copy(){
        Block copy;
        if(blade!=null)copy = new Block(getConfiguration(),z,blade);
        else copy = new Block(getConfiguration(),x,y,z,coil);
        copy.valid = valid;
        return copy;
    }
    @Override
    public boolean isEqual(multiblock.Block other){
        return other instanceof Block&&((Block)other).blade==blade&&((Block)other).coil==coil;
    }
    public boolean isBlade(){
        return blade!=null;
    }
    public boolean isBearing(){
        return coil!=null&&coil.bearing;
    }
    public boolean isCoil(){
        return coil!=null&&!coil.bearing&&!coil.connector;
    }
    public boolean isConnector(){
        return coil!=null&&coil.connector;
    }
    @Override
    public boolean isFullBlock(){
        return !isBlade();
    }
}
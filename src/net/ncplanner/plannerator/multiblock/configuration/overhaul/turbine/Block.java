package net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine;
import java.util.ArrayList;
import java.util.Objects;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockTemplate;
import net.ncplanner.plannerator.multiblock.configuration.RuleContainer;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.file.writer.LegacyNCPFWriter;
public class Block extends RuleContainer<PlacementRule.BlockType, Block> implements Pinnable, IBlockTemplate {
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public boolean blade = false;
    public float bladeEfficiency;
    public float bladeExpansion;
    public boolean bladeStator;//not just stator cuz it's the stator stat of the blade. makes sense.
    public boolean coil = false;
    public float coilEfficiency;
    public boolean bearing = false;
    public boolean shaft = false;
    public boolean connector = false;
    public boolean controller = false;
    public boolean casing = false;
    public boolean casingEdge = false;
    public boolean inlet = false;
    public boolean outlet = false;
    public Image texture;
    public Image displayTexture;
    public Block(String name){
        this.name = name;
    }
    public Image getTexture(){
        return texture;
    }
    public void setTexture(Image image){
        texture = image;
        displayTexture = TextureManager.convert(image);
    }
    @Override
    public boolean stillEquals(RuleContainer rc){
        Block b = (Block)rc;
        return Objects.equals(b.name, name)
                &&Objects.equals(b.displayName, displayName)
                &&b.legacyNames.equals(legacyNames)
                &&b.blade==blade
                &&b.bladeEfficiency==bladeEfficiency
                &&b.bladeExpansion==bladeExpansion
                &&b.bladeStator==bladeStator
                &&b.coil==coil
                &&b.coilEfficiency==coilEfficiency
                &&b.bearing==bearing
                &&b.shaft==shaft
                &&b.connector==connector
                &&b.controller==controller
                &&b.casing==casing
                &&b.casingEdge==casingEdge
                &&b.inlet==inlet
                &&b.outlet==outlet
                &&Core.areImagesEqual(b.texture, texture);
    }
    @Override
    public ArrayList<String> getLegacyNames(){
        ArrayList<String> allNames = new ArrayList<>(legacyNames);
        allNames.add(name);
        return allNames;
    }
    @Override
    public String getName(){
        return name;
    }
    public String getDisplayName(){
        return displayName==null?name:displayName;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> nams = getSimpleSearchableNames();
        for(AbstractPlacementRule<PlacementRule.BlockType, Block> r : rules)nams.addAll(r.getSearchableNames());
        return nams;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        ArrayList<String> nams = getLegacyNames();
        nams.add(getDisplayName());
        return nams;
    }
    @Override
    public Image getDisplayTexture() {
        return displayTexture;
    }
    @Override
    public String getPinnedName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }
    public void setLegacyNames(ArrayList<String> legacyNames){
        this.legacyNames = new ArrayList<>(legacyNames);
    }
    public boolean isBlade(){
        return blade;
    }
    public float getBladeEfficiency(){
        return bladeEfficiency;
    }
    public float getBladeExpansion(){
        return bladeExpansion;
    }
    public boolean isBladeStator(){
        return bladeStator;
    }
    public boolean isCoil(){
        return coil;
    }
    public float getCoilEfficiency(){
        return coilEfficiency;
    }
    public boolean isBearing(){
        return bearing;
    }
    public boolean isShaft(){
        return shaft;
    }
    public boolean isConnector(){
        return connector;
    }
    public boolean isController(){
        return controller;
    }
    public boolean isCasing(){
        return casing;
    }
    public boolean isCasingEdge(){
        return casingEdge;
    }
    public boolean isInlet(){
        return inlet;
    }
    public boolean isOutlet(){
        return outlet;
    }
    public void setBlade(boolean blade){
        this.blade = blade;
    }
    public void setBladeEfficiency(float bladeEfficiency){
        this.bladeEfficiency = bladeEfficiency;
    }
    public void setBladeExpansion(float bladeExpansion){
        this.bladeExpansion = bladeExpansion;
    }
    public void setBladeStator(boolean bladeStator){
        this.bladeStator = bladeStator;
    }
    public void setCoil(boolean coil){
        this.coil = coil;
    }
    public void setCoilEfficiency(float coilEfficiency){
        this.coilEfficiency = coilEfficiency;
    }
    public void setBearing(boolean bearing){
        this.bearing = bearing;
    }
    public void setShaft(boolean shaft){
        this.shaft = shaft;
    }
    public void setConnector(boolean connector){
        this.connector = connector;
    }
    public void setController(boolean controller){
        this.controller = controller;
    }
    public void setCasing(boolean casing){
        this.casing = casing;
    }
    public void setCasingEdge(boolean casingEdge){
        this.casingEdge = casingEdge;
    }
    public void setInlet(boolean inlet){
        this.inlet = inlet;
    }
    public void setOutlet(boolean outlet){
        this.outlet = outlet;
    }
    @Override
    public String toString(){
        return getDisplayName();
    }
}
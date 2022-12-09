package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
public abstract class Module<T>{
    private boolean active;
    public final String name;
    public ArrayList<Configuration> ownConfigs = new ArrayList<>();//used for loading configs on startup
    public Module(String name){
        this(name, false);
    }
    public Module(String name, boolean defaultActive){
        this.name = name;
        active = defaultActive;
    }
    public final void activate(){
        active = true;
        onActivated();
        Core.refreshModules();
    }
    public final void deactivate(){
        active = false;
        onDeactivated();
        Core.refreshModules();
    }
    public boolean isActive(){
        return active;
    }
    protected void onActivated(){}
    protected void onDeactivated(){}
    public abstract String getDisplayName();
    public abstract String getDescription();
    /**
     * Calculate this module for a specified multiblock
     * @param m the multiblock to calculate
     * @return a String to add to the tooltip, or `null` if there is none
     */
    public T calculateMultiblock(Multiblock m){
        return null;
    }
    public String getTooltip(Multiblock m, T o){
        return null;
    }
    public void addConfiguration(Configuration c){
        Configuration.configurations.add(c);
        c.path = "modules/"+name+"/"+c.name;
        ownConfigs.add(c);
    }
    public void getGenerationPriorities(Multiblock multiblock, ArrayList<Priority> priorities){}
    public void getSuggestors(Multiblock multiblock, ArrayList<Suggestor> suggestors){}
    public void getEditorOverlays(Multiblock multiblock, ArrayList<EditorOverlay> overlays){}
    public void addMultiblockTypes(ArrayList<Multiblock> multiblockTypes){}
    public void setActive(boolean active){
        if(active)activate();
        else deactivate();
    }
    public void addTutorials(){}
    public void addConfigurations(){}
}
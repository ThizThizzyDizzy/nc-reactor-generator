package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFDesign;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFModuleContainer;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.design.NCPFDesignDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
public abstract class Module<T>{
    private boolean active;
    public final String name;
    public ArrayList<Configuration> ownConfigs = new ArrayList<>();//used for loading configs on startup
    public boolean unlocked = true;
    public String secretKey;
    public Module(String name){
        this(name, false);
    }
    public Module(String name, boolean defaultActive){
        this.name = name;
        active = defaultActive;
    }
    public Module(String name, String secretKey){
        this(name, false);
        this.secretKey = secretKey;
        unlocked = false;
    }
    public final void activate(){
        active = true;
        unlocked = true;
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
    public final void addConfiguration(Configuration c){
        Configuration.configurations.add(c);
        c.path = "modules/"+name+"/"+c.name;
        ownConfigs.add(c);
    }
    public final void registerNCPFConfiguration(Supplier<NCPFConfiguration> configuration, Supplier<NCPFDesignDefinition> design){
        String key = design.get().type;
        NCPFConfigurationContainer.recognizedConfigurations.put(key, configuration);
        NCPFDesign.recognizedDesigns.put(key, design);
    }
    public final void registerNCPFElement(String key, Supplier<NCPFElementDefinition> element){
        NCPFElement.recognizedElements.put(key, element);
    }
    public final void registerNCPFModule(Supplier<NCPFModule> module){
        NCPFModuleContainer.recognizedModules.put(module.get().name, module);
    }
    public void getGenerationPriorities(Multiblock multiblock, ArrayList<Priority> priorities){}
    public void getSuggestors(Multiblock multiblock, ArrayList<Suggestor> suggestors){}
    public void getEditorOverlays(Multiblock multiblock, ArrayList<EditorOverlay> overlays){}
    public void addMultiblockTypes(ArrayList<Multiblock> multiblockTypes){}
    public void registerNCPF(){}
    public void setActive(boolean active){
        if(active)activate();
        else deactivate();
    }
    public void addTutorials(){}
    public void addConfigurations(){}
}
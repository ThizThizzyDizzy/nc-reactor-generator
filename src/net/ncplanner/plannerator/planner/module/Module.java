package net.ncplanner.plannerator.planner.module;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.GeneratorMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.Constant;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.Operator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Parameter;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFDesign;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.NCPFModuleContainer;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.design.NCPFDesignDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.ncpf.Addon;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Design;
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
        activate(true);
    }
    public final void activate(boolean refresh){
        active = true;
        unlocked = true;
        onActivated();
        if(refresh)Core.refreshModules();
    }
    public final void deactivate(){
        deactivate(true);
    }
    public final void deactivate(boolean refresh){
        active = false;
        onDeactivated();
        if(refresh)Core.refreshModules();
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
        c.path = "modules/"+name+"/"+c.getName();
        ownConfigs.add(c);
    }
    @Deprecated //this is just to mark addons that haven't been updated to ncpf.json yet
    public final void addLegacyAddon(Addon addon, String link){
        Configuration.addLegacyInternalAddon(addon, link);
    }
    public final void addAddon(Addon addon, String link){
        Configuration.addLegacyInternalAddon(addon, link);
    }
    public final void registerNCPFConfiguration(Supplier<NCPFConfiguration> configuration){
        NCPFConfigurationContainer.recognizedConfigurations.put(configuration.get().name, configuration);
        NCPFConfigurationContainer.configOrder.add(configuration.get().name);
    }
    public final void registerNCPFDesign(Supplier<NCPFDesignDefinition> design, Function<NCPFFile, Design> specificDesign){
        NCPFDesign.recognizedDesigns.put(design.get().type, design);
        Design.registeredDesigns.put(design.get().type, specificDesign);
    }
    public final void registerNCPFElement(Supplier<NCPFElementDefinition> element){
        NCPFElement.recognizedElements.put(element.get().type, element);
    }
    public final void registerNCPFModule(Supplier<NCPFModule> module){
        NCPFModuleContainer.recognizedModules.put(module.get().name, module);
    }
    public final void registerGeneratorMutator(Supplier<GeneratorMutator> mutator){
        GeneratorMutator.registeredMutators.put(mutator.get().type, mutator);
    }
    public final void registerMutator(Supplier<Mutator> mutator){
        Mutator.registeredMutators.put(mutator.get().type, mutator);
    }
    public final void registerOperator(Supplier<Operator> operator){
        Operator.registeredOperators.put(operator.get().getType(), operator);
    }
    public final void registerConstant(Supplier<Constant> constant){
        Constant.registeredConstants.put(constant.get().getType(), constant);
    }
    public final void registerCondition(Supplier<Condition> condition){
        Condition.registeredConditions.put(condition.get().type, condition);
    }
    public final void registerParameter(Supplier<Parameter> parameter){
        Parameter.registeredParameters.put(parameter.get().type, parameter);
    }
    public void getGenerationPriorities(Multiblock multiblock, ArrayList<Priority> priorities){}
    public void getSuggestors(Multiblock multiblock, ArrayList<Suggestor> suggestors){}
    public void getEditorOverlays(Multiblock multiblock, ArrayList<EditorOverlay> overlays){}
    public void addMultiblockTypes(ArrayList<Multiblock> multiblockTypes){}//TODO replace with NCPF design registry
    public void registerNCPF(){}
    public void setActive(boolean active){
        if(active)activate();
        else deactivate();
    }
    public void addTutorials(){}
    public void addConfigurations(Task task){}
    public void getGenerators(LiteMultiblock multiblock, ArrayList<Supplier<InputStream>> generators){}
}
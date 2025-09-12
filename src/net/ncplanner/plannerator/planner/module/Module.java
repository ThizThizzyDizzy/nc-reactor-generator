package net.ncplanner.plannerator.planner.module;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
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
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.ncpf.Addon;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import org.reflections.Reflections;
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
    protected void onActivated(){
    }
    protected void onDeactivated(){
    }
    public abstract String getDisplayName();
    public abstract String getDescription();
    /**
     * Calculate this module for a specified multiblock
     *
     * @param m the multiblock to calculate
     * @return a String to add to the tooltip, or `null` if there is none
     */
    public T calculateMultiblock(Multiblock m){
        return null;
    }
    public String getTooltip(Multiblock m, T o){
        return null;
    }
    public final void addConfiguration(Configuration c, String link, String author){
        Configuration.addInternalConfiguration(c, link, author);
        c.path = "modules/"+name+"/"+c.getName();
        ownConfigs.add(c);
    }
    public final void addAddon(Addon addon, String link, String author){
        Configuration.addInternalAddon(addon, link, author);
    }
    public void getSuggestors(Multiblock multiblock, ArrayList<Suggestor> suggestors){
    }
    public void getEditorOverlays(Multiblock multiblock, ArrayList<EditorOverlay> overlays){
    }
    public final void addMultiblockTypes(ArrayList<Multiblock> multiblockTypes){
        for(Multiblock m : registeredMultiblocks)multiblockTypes.add(m.newInstance(null));
    }
    private final ArrayList<Multiblock> registeredMultiblocks = new ArrayList<>();
    public final void registerNCPF(){
        autoRegister(NCPFConfiguration.class, (config) -> config.name, NCPFConfigurationContainer.recognizedConfigurations, NCPFConfigurationContainer.configOrder);
        autoRegister(NCPFDesignDefinition.class, (design) -> design.type, NCPFDesign.recognizedDesigns);
        autoRegister(NCPFModule.class, (module) -> module.name, NCPFModuleContainer.recognizedModules);
        autoRegister(NCPFElementDefinition.class, (element) -> element.type, NCPFElement.recognizedElements);
        autoRegister(GeneratorMutator.class, (mutator) -> mutator.type, GeneratorMutator.registeredMutators);
        autoRegister(Mutator.class, (mutator) -> mutator.type, Mutator.registeredMutators);
        autoRegister(Operator.class, (operator) -> operator.getType(), Operator.registeredOperators);
        autoRegister(Constant.class, (constant) -> constant.getType(), Constant.registeredConstants);
        autoRegister(Condition.class, (condition) -> condition.type, Condition.registeredConditions);
        autoRegister(Parameter.class, (parameter) -> parameter.type, Parameter.registeredParameters);
        autoRegister(Multiblock.class, registeredMultiblocks);

        Reflections reflections = new Reflections("net.ncplanner.plannerator");

        try{
            for(Class<?> type : reflections.getTypesAnnotatedWith(RegisterWith.class)){
                if(type.getAnnotation(RegisterWith.class).module()!=getClass())continue;
                if(Design.class.isAssignableFrom(type)){
                    Class<? extends Design> specificType = (Class<? extends Design>)type;
                    ParameterizedType superType = (ParameterizedType)specificType.getGenericSuperclass();
                    Class<? extends NCPFDesignDefinition> designDefinitionType = (Class<? extends NCPFDesignDefinition>)superType.getActualTypeArguments()[0];
                    Constructor<? extends Design> constructor = specificType.getConstructor(NCPFFile.class);
                    Function<NCPFFile, Design> function = (ncpf) -> {
                        try{
                            return constructor.newInstance(ncpf);
                        }catch(InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
                            throw new RuntimeException(ex);
                        }
                    };
                    Design.registeredDesigns.put(designDefinitionType.getConstructor().newInstance().type, function);
                }
            }
        }catch(NoSuchMethodException|SecurityException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
            throw new RuntimeException(ex);
        }
    }
    private <T> void autoRegister(Class<T> classType, ArrayList<T> instanceList){
        autoRegister(classType, null, null, null, instanceList);
    }
    private <T> void autoRegister(Class<T> classType, Function<T, String> keyFunc, HashMap<String, Supplier<T>> registry){
        autoRegister(classType, keyFunc, registry, null, null);
    }
    @Deprecated // this was for the old generator, unused now.
    public void getGenerationPriorities(Multiblock multiblock, ArrayList<Priority> priorities){
    }
    private <T> void autoRegister(Class<T> classType, Function<T, String> keyFunc, HashMap<String, Supplier<T>> registry, ArrayList<String> keyList){
        autoRegister(classType, keyFunc, registry, keyList, null);
    }
    private <T> void autoRegister(Class<T> classType, Function<T, String> keyFunc, HashMap<String, Supplier<T>> registry, ArrayList<String> keyList, ArrayList<T> instanceList){
        Reflections reflections = new Reflections("net.ncplanner.plannerator");
        try{
            for(Class<?> type : reflections.getTypesAnnotatedWith(RegisterWith.class)){
                if(type.getAnnotation(RegisterWith.class).module()!=getClass())continue;
                if(classType.isAssignableFrom(type)){
                    Class<? extends T> specificType = (Class<? extends T>)type;
                    Constructor<? extends T> constructor = specificType.getConstructor();
                    Supplier<T> supplier = () -> {
                        try{
                            return constructor.newInstance();
                        }catch(InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
                            throw new RuntimeException(ex);
                        }
                    };
                    T instance = supplier.get();
                    String key = keyFunc==null?null:keyFunc.apply(instance);
                    if(registry!=null)registry.put(key, supplier);
                    if(keyList!=null)keyList.add(key);
                    if(instanceList!=null)instanceList.add(instance);
                }
            }
        }catch(NoSuchMethodException|SecurityException|IllegalArgumentException ex){
            throw new RuntimeException(ex);
        }
    }
    public void setActive(boolean active){
        if(active)activate();
        else
            deactivate();
    }
    public void addTutorials(){
    }
    public void addConfigurations(Task task){
    }
    public void getGenerators(LiteMultiblock multiblock, ArrayList<Supplier<InputStream>> generators){
    }
    private ArrayList<Runnable> tasks = new ArrayList<>();
    protected void addConfigurationTask(Task t, String name, String filepath, String link, String author, String... alternatives){
        Task task = t.addSubtask(name);
        tasks.add(() -> {
            Configuration config = new Configuration(FileReader.read(() -> Core.getInputStream(filepath)));
            for(String alt : alternatives){
                config.addAlternative(alt);
            }
            addConfiguration(config, link, author);
            task.finish();
        });
    }
    protected void addAddonTask(Task t, String name, String filepath, String link, String author){
        Task task = t.addSubtask(name);
        tasks.add(() -> {
            addAddon(FileReader.read(() -> Core.getInputStream(filepath)).addons.get(0), link, author);
            task.finish();
        });
    }
    protected void runTasks(){
        for(Runnable r : tasks)r.run();
        tasks.clear();
    }
}

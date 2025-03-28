package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.configuration.UnknownNCPFConfiguration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFListElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.planner.ncpf.Addon;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
public class NCPFConfigurationContainer extends DefinedNCPFObject{
    public static ArrayList<String> configOrder = new ArrayList<>();
    public static HashMap<String, Supplier<NCPFConfiguration>> recognizedConfigurations = new HashMap<>();
    public HashMap<String, NCPFConfiguration> configurations = new HashMap<>();
    public static boolean isRecognized(Supplier<NCPFConfiguration> config){
        return recognizedConfigurations.containsKey(config.get().name);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        for(String key : ncpf.keySet()){
            configurations.put(key, ncpf.getDefinedNCPFObject(key, recognizedConfigurations.getOrDefault(key, UnknownNCPFConfiguration::new)));
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        for(String key : configurations.keySet()){
            ncpf.setDefinedNCPFObject(key, configurations.get(key));
        }
    }
    public <T extends NCPFConfiguration> T getConfiguration(Supplier<T> config){
        NCPFConfiguration c = configurations.get(config.get().name);
        if(c instanceof UnknownNCPFConfiguration)return null;
        return (T)c;
    }
    public boolean hasConfiguration(Supplier<NCPFConfiguration> config){
        return configurations.containsKey(config.get().name);
    }
    public void setConfiguration(NCPFConfiguration config){
        if(config==null)return;
        if(!recognizedConfigurations.containsKey(config.name))throw new IllegalArgumentException("Cannot set unrecognized configuration: "+config.name+"!");
        configurations.put(config.name, config);
    }
    public <T extends NCPFConfiguration> void withConfiguration(Supplier<T> config, Consumer<T> func){
        T t = getConfiguration(config);
        if(t!=null){
            func.accept(t);
        }
    }
    /**
     * Add all parts of another configuration to this one
     *
     * @param addon The addon to add
     */
    public void conglomerate(NCPFConfigurationContainer addon){
        for(String key : addon.configurations.keySet()){
            NCPFConfiguration addonConfig = addon.configurations.get(key);
            if(configurations.containsKey(key)){
                configurations.get(key).conglomerate(addonConfig);
            }else
                configurations.put(key, addonConfig.copyTo(recognizedConfigurations.get(addonConfig.name)));
        }
    }
    public void setReferences(boolean soft){
        configurations.values().forEach(ncpfConfiguration -> ncpfConfiguration.setReferences(soft));
    }
    public void makePartial(List<Design> designs){
        for(String key : configurations.keySet()){
            NCPFConfiguration cfg = configurations.get(key);
            cfg.makePartial(designs);
        }
    }
    public String getNameAndVersion(){
        for(String key : NCPFConfigurationContainer.configOrder){
            if(configurations.containsKey(key)){
                NCPFConfiguration cfg = configurations.get(key);
                ConfigurationMetadataModule module = cfg.getModule(ConfigurationMetadataModule::new);
                if(module!=null&&module.name!=null)return module.name+" "+module.version;
            }
        }
        for(NCPFConfiguration cfg : configurations.values()){
            ConfigurationMetadataModule module = cfg.getModule(ConfigurationMetadataModule::new);
            if(module!=null&&module.name!=null)return module.name+" "+module.version;
        }
        return "Unknown Configuration";
    }
    // Actually converts to addon, with other as parent
    public void subtract(NCPFConfigurationContainer other, List<Addon> addons){
        HashMap<String, NCPFConfiguration> replaceConfigs = new HashMap<>();
        for(Iterator<String> cit = configurations.keySet().iterator(); cit.hasNext();){
            String key = cit.next();
            ArrayList<NCPFConfiguration> otherConfigs = new ArrayList<>();
            if(other.configurations.containsKey(key))otherConfigs.add(other.configurations.get(key));
            for(Addon addon : addons){
                if(addon.configuration.configurations.containsKey(key))otherConfigs.add(addon.configuration.configurations.get(key));
            }
            NCPFConfiguration mainCfg = configurations.get(key);
            for(NCPFConfiguration otherCfg : otherConfigs){
                List<NCPFElement>[] mainElementLists = mainCfg.getAllElements();
                List<NCPFElement>[] otherElementLists = otherCfg.getAllElements();
                for(int i = 0; i<mainElementLists.length; i++){
                    List<NCPFElement> mainElements = mainElementLists[i];
                    List<NCPFElement> otherElements = i<otherElementLists.length?otherElementLists[i]:new ArrayList<>();
                    List<NCPFElement> replacedElements = new ArrayList<>();
                    for(Iterator<NCPFElement> it = mainElements.iterator(); it.hasNext();){
                        NCPFElement element = it.next();
                        NCPFElement otherMatch = null;
                        for(NCPFElement otherElement : otherElements){
                            if(element.definition.matches(otherElement.definition)){
                                otherMatch = otherElement;
                            }
                        }
                        if(otherMatch==null){
                            //try matching the element in mainConfig to a list in otherCfg containing it
                            for(NCPFElement otherElement : otherElements){
                                if(otherElement.definition instanceof NCPFListElement){
                                    NCPFListElement list = (NCPFListElement)otherElement.definition;
                                    for(NCPFElementDefinition otherElem : list.elements){
                                        if(element.definition.matches(otherElem)){
                                            otherMatch = otherElement;
                                        }
                                    }
                                }
                            }
                        }
                        if(otherMatch==null)continue;

                        // if it's a port, don't check for recipes
                        boolean isPort = false;
                        for(String moduleKey : element.modules.modules.keySet()){
                            if(moduleKey.endsWith(":port")){
                                isPort = true;
                                break;
                            }
                        }
                        if(isPort){
                            it.remove();
                            continue;
                        }

                        NCPFBlockRecipesModule mainRecipes = element.getModule(NCPFBlockRecipesModule::new);
                        NCPFBlockRecipesModule otherRecipes = otherMatch.getModule(NCPFBlockRecipesModule::new);
                        if(mainRecipes!=null&&otherRecipes!=null){
                            Recipe:
                            for(Iterator<NCPFElement> rit = mainRecipes.recipes.iterator(); rit.hasNext();){
                                NCPFElement recipe = rit.next();
                                for(NCPFElement otherRecipe : otherRecipes.recipes){
                                    if(recipe.definition.matches(otherRecipe.definition)){
                                        rit.remove();
                                        continue Recipe;
                                    }
                                }

                                //try matching the element in mainRecipes to a list in otherRecipes containing it
                                for(NCPFElement otherRecipe : otherRecipes.recipes){
                                    if(otherRecipe.definition instanceof NCPFListElement){
                                        NCPFListElement list = (NCPFListElement)otherRecipe.definition;
                                        for(NCPFElementDefinition otherElem : list.elements){
                                            if(recipe.definition.matches(otherElem)){
                                                rit.remove();
                                                continue Recipe;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(mainRecipes!=null&&!mainRecipes.recipes.isEmpty()){
                            NCPFElement replacedElement = new NCPFElement(element.definition);
                            replacedElement.setModule(mainRecipes);
                            replacedElements.add(replacedElement);
                        }
                        it.remove();
                    }
                    mainElements.addAll(replacedElements);
                }

                mainCfg.withModule(ConfigurationMetadataModule::new, (meta) -> {
                    meta.name = meta.version = null;
                });
                // Remove all configuration settings modules
                for(Iterator<String> it = mainCfg.modules.modules.keySet().iterator(); it.hasNext();){
                    if(it.next().endsWith("configuration_settings"))it.remove();
                }
                // Keep them from coming back
                mainCfg.removeSettings();

                boolean empty = true;
                for(List<NCPFElement> list : mainElementLists)empty &= list.isEmpty();
                if(empty)cit.remove();
            }
        }
        configurations.putAll(replaceConfigs);
    }
    /**
     * Remove things that do not belong in an addon's parent configuration
     */
    public void makeAddon(){
        // Currently only removes textures & display names
        for(NCPFConfiguration config : configurations.values()){
            for(List<NCPFElement> elements : config.getAllElementsISaidAllElements()){
                for(NCPFElement element : elements){
                    element.makeAddon();
                }
            }
        }
    }
}

package net.ncplanner.plannerator.ncpf;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.configuration.UnknownNCPFConfiguration;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFConfigurationContainer extends DefinedNCPFObject{
    public static HashMap<String, Supplier<NCPFConfiguration>> recognizedConfigurations = new HashMap<>();
    public HashMap<String, NCPFConfiguration> configurations = new HashMap<>();
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
        return (T) configurations.get(config.get().name);
    }
    public void setConfiguration(NCPFConfiguration config){
        configurations.put(config.name, config);
    }
    /**
     * Add all parts of another configuration to this one
     * @param addon The addon to add
     */
    public void conglomerate(NCPFConfigurationContainer addon){
        for(String key : addon.configurations.keySet()){
            NCPFConfiguration addonConfig = addon.configurations.get(key);
            if(configurations.containsKey(key)){
                configurations.get(key).conglomerate(addonConfig);
            }else configurations.put(key, addonConfig);
        }
    }
    public void setReferences(){
        configurations.values().forEach(NCPFConfiguration::setReferences);
    }
}
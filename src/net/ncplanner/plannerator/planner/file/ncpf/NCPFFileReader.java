package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFListElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.LegacyNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.NuclearCraftGeneratedModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public class NCPFFileReader{
    public static final ArrayList<NCPFFormatReader> formats = new ArrayList<>();
    private static JSONNCPFReader JSON;
    static{
        formats.add(JSON = new JSONNCPFReader());
    }
    public static Project read(Supplier<InputStream> provider){
        Project project = new Project();
        NCPFObject ncpf = null;
        for(NCPFFormatReader reader : formats){
            try{
                ncpf = reader.read(provider.get());
                break;
            }catch(Throwable t){
            }//TODO properly separate error handling and incorrect format
        }
        if(ncpf==null)throw new IllegalArgumentException("Unknown file format!");
        project.convertFromObject(ncpf);
        project.withModule(NuclearCraftGeneratedModule::new, (generatedModule) -> {
            // Populate the generated configuration with textures/etc from internal configs
            for(Configuration configuration : Configuration.configurations){
                for(NCPFConfiguration config : project.configuration.configurations.values()){
                    NCPFConfiguration internal = configuration.configuration.configurations.get(config.name);
                    if(internal==null)continue;
                    for(List<NCPFElement> elements : config.getElements()){
                        for(NCPFElement element : elements){
                            boolean foundMatch = false;
                            for(List<NCPFElement> internalElements : internal.getElements()){
                                for(NCPFElement internalElement : internalElements){
                                    if(element.definition.matches(internalElement.definition)){
                                        foundMatch = true;
                                        for(NCPFModule module : internalElement.modules.modules.values()){
                                            if(element.modules.modules.containsKey(module.name))continue; // only fill if they don't exist already
                                            if(module instanceof TextureModule){
                                                element.modules.setModule(module.copyTo(TextureModule::new));
                                            }
                                            if(module instanceof DisplayNameModule){
                                                element.modules.setModule(module.copyTo(DisplayNameModule::new));
                                            }
                                            if(module instanceof LegacyNamesModule){
                                                element.modules.setModule(module.copyTo(LegacyNamesModule::new));
                                            }
                                        }
                                    }
                                }
                            }
                            if(!foundMatch&&element.definition instanceof NCPFListElement){
                                for(NCPFElementDefinition definition : ((NCPFListElement)element.definition).elements){
                                    for(List<NCPFElement> internalElements : internal.getElements()){
                                        for(NCPFElement internalElement : internalElements){
                                            if(definition.matches(internalElement.definition)){
                                                foundMatch = true;
                                                for(NCPFModule module : internalElement.modules.modules.values()){
                                                    if(element.modules.modules.containsKey(module.name))continue; // only fill if they don't exist already
                                                    if(module instanceof TextureModule){
                                                        element.modules.setModule(module.copyTo(TextureModule::new));
                                                    }
                                                    if(module instanceof DisplayNameModule){
                                                        element.modules.setModule(module.copyTo(DisplayNameModule::new));
                                                    }
                                                    if(module instanceof LegacyNamesModule){
                                                        element.modules.setModule(module.copyTo(LegacyNamesModule::new));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            project.modules.removeModule(generatedModule);
        });
        return project;
    }
    public static Project read(File file){
        return read(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        });
    }
}

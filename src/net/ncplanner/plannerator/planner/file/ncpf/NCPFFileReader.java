package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFItemElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyItemElement;
import net.ncplanner.plannerator.ncpf.element.NCPFListElement;
import net.ncplanner.plannerator.ncpf.element.NCPFOredictElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ImageIO;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.LegacyNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.NuclearCraftGeneratedModule;
import net.ncplanner.plannerator.planner.ncpf.module.TagsModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public class NCPFFileReader{
    public static final ArrayList<NCPFFormatReader> formats = new ArrayList<>();
    private static JSONNCPFReader json;
    static{
        formats.add(json = new JSONNCPFReader());
    }
    public static Project read(Supplier<InputStream> provider, File fileContext){
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
            // Populate the generated configureation with config metadata (name/version)
            for(NCPFConfiguration config : project.configuration.configurations.values()){
                if(config.hasModule(ConfigurationMetadataModule::new))continue;
                config.withModuleOrCreate(ConfigurationMetadataModule::new, (metadata) -> {
                    metadata.name = "NuclearCraft (Exported)";
                    metadata.version = generatedModule.ncVersion;
                });
            }

            if(fileContext!=null){
                // Populate addon textures/display names using fileContext, if available
                File resourcesDir = new File(fileContext.getAbsoluteFile().getParentFile().getParentFile(), "resources");
                if(resourcesDir.exists()){
                    for(File namespaceDir : resourcesDir.listFiles()){
                        if(!namespaceDir.isDirectory())continue;
                        String namespace = namespaceDir.getName();

                        for(NCPFConfiguration config : project.configuration.configurations.values()){
                            // Global Elements

                            for(List<NCPFElement> elements : config.getAllElementsISaidAllElements()){
                                for(NCPFElement element : elements){
                                    boolean loadTexture = !element.hasModule(TextureModule::new);
                                    boolean loadDisplayName = !element.hasModule(DisplayNameModule::new);
                                    Image texture = null;
                                    String namePrefix = "";
                                    String nameSuffix = ".name";
                                    boolean includeNamespace = true;
                                    boolean valid = false;
                                    if(element.definition.typeMatches(NCPFLegacyItemElement::new)||element.definition.typeMatches(NCPFItemElement::new)){
                                        if(!element.definition.getName().startsWith(namespace+":"))continue;
                                        valid = true;
                                        if(loadTexture){
                                            try{
                                                File model = new File(namespaceDir, "models"+File.separatorChar+"item"+File.separatorChar+element.getName().substring(namespace.length()+1)+".json");
                                                JSON.JSONObject jsonTextures = JSON.parse(model).getJSONObject("textures");
                                                if(jsonTextures.size()!=1)continue;
                                                String texturePath = jsonTextures.getString(new ArrayList<>(jsonTextures.keySet()).getFirst());
                                                String[] textureParts = texturePath.split(":");
                                                File textureFile = new File(resourcesDir, textureParts[0]+File.separatorChar+"textures"+File.separatorChar+textureParts[1]+".png");
                                                if(!textureFile.exists())continue;
                                                texture = ImageIO.read(textureFile);
                                            }catch(Exception ex){
                                            }
                                        }
                                        namePrefix = "item.";
                                    }
                                    if(element.definition.typeMatches(NCPFLegacyBlockElement::new)||element.definition.typeMatches(NCPFBlockElement::new)){
                                        if(!element.definition.getName().startsWith(namespace+":"))continue;
                                        valid = true;
                                        if(loadTexture){
                                            try{
                                                File blockstate = new File(namespaceDir, "blockstates"+File.separatorChar+element.getName().substring(namespace.length()+1)+".json");
                                                JSON.JSONObject jsonBlockstate = JSON.parse(blockstate);
                                                String texturePath;
                                                if(jsonBlockstate.containsKey("forge_marker")){
                                                    JSON.JSONObject jsonTextures = jsonBlockstate.getJSONObject("defaults").getJSONObject("textures");
                                                    if(jsonTextures.size()!=1)continue;
                                                    texturePath = jsonTextures.getString(new ArrayList<>(jsonTextures.keySet()).getFirst());
                                                }else{
                                                    String modelPath = jsonBlockstate.getJSONObject("variants").getJSONObject("").getString("model");
                                                    String[] modelParts = modelPath.split(":");

                                                    File model = new File(resourcesDir, modelParts[0]+File.separatorChar+"models"+File.separatorChar+modelParts[1]+".json");
                                                    JSON.JSONObject jsonTextures = JSON.parse(model).getJSONObject("textures");
                                                    if(jsonTextures.size()!=1)continue;
                                                    texturePath = jsonTextures.getString(new ArrayList<>(jsonTextures.keySet()).getFirst());
                                                }

                                                String[] textureParts = texturePath.split(":");
                                                File textureFile = new File(resourcesDir, textureParts[0]+File.separatorChar+"textures"+File.separatorChar+textureParts[1]+".png");
                                                if(!textureFile.exists())continue;
                                                texture = ImageIO.read(textureFile);
                                            }catch(Exception ex){
                                            }
                                        }
                                        namePrefix = "tile.";
                                    }
                                    if(element.definition.typeMatches(NCPFLegacyFluidElement::new)){
                                        valid = true;
                                        namePrefix = "fluid.";
                                        nameSuffix = "";
                                        includeNamespace = false;
                                    }
                                    if(!valid)continue;
                                    if(loadTexture&&texture!=null){
                                        TextureModule textureModule = new TextureModule();
                                        textureModule.texture = texture;
                                        element.setModule(textureModule);
                                    }

                                    String displayName = null;
                                    if(loadDisplayName){
                                        String namePath = namePrefix+(includeNamespace?namespace+".":"")+element.getName().substring(includeNamespace?namespace.length()+1:0)+nameSuffix+"=";
                                        try{
                                            File langFile = new File(namespaceDir, "lang"+File.separatorChar+"en_us.lang");
                                            for(String s : Files.readAllLines(langFile.toPath())){
                                                if(s.startsWith(namePath)){
                                                    displayName = s.substring(namePath.length()).trim();
                                                }
                                            }
                                        }catch(Exception ex){
                                        }
                                    }
                                    if(loadDisplayName&&displayName!=null){
                                        DisplayNameModule displayNameModule = new DisplayNameModule();
                                        displayNameModule.displayName = displayName;
                                        element.setModule(displayNameModule);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Populate the generated configuration with textures/etc from internal configs
            for(Configuration configuration : Configuration.configurations){
                for(NCPFConfiguration config : project.configuration.configurations.values()){
                    NCPFConfiguration internal = configuration.configuration.configurations.get(config.name);
                    if(internal==null)continue;
                    // Global Elements

                    for(List<NCPFElement> elements : config.getAllElements()){
                        for(NCPFElement element : elements){
                            NCPFElement match = null;
                            for(List<NCPFElement> internalElements : internal.getAllElements()){
                                for(NCPFElement internalElement : internalElements){
                                    if(element.definition.matches(internalElement.definition)){
                                        if(match==null)match = internalElement;
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
                            if(match==null&&element.definition instanceof NCPFListElement){
                                for(NCPFElementDefinition definition : ((NCPFListElement)element.definition).elements){
                                    for(List<NCPFElement> internalElements : internal.getAllElements()){
                                        for(NCPFElement internalElement : internalElements){
                                            if(definition.matches(internalElement.definition)){
                                                if(match==null)match = internalElement;
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
                            if(match==null)continue;
                            NCPFElement internalMatch = match;
                            // Block Recipes
                            if(element instanceof BlockRecipesElement&&internalMatch instanceof BlockRecipesElement){
                                BlockRecipesElement brelement = (BlockRecipesElement)element;
                                BlockRecipesElement internalBrelement = (BlockRecipesElement)internalMatch;
                                List<? extends NCPFElement> recipes = brelement.getBlockRecipes();
                                List<? extends NCPFElement> internalRecipes = internalBrelement.getBlockRecipes();
                                if(recipes==null||internalRecipes==null)continue;
                                for(NCPFElement recipe : recipes){
                                    boolean foundMatch = false;
                                    for(NCPFElement internalRecipe : internalRecipes){
                                        if(recipe.definition.matches(internalRecipe.definition)){
                                            foundMatch = true;
                                            for(NCPFModule module : internalRecipe.modules.modules.values()){
                                                if(recipe.modules.modules.containsKey(module.name))continue; // only fill if they don't exist already
                                                if(module instanceof TextureModule){
                                                    recipe.modules.setModule(module.copyTo(TextureModule::new));
                                                }
                                                if(module instanceof DisplayNameModule){
                                                    recipe.modules.setModule(module.copyTo(DisplayNameModule::new));
                                                }
                                                if(module instanceof LegacyNamesModule){
                                                    recipe.modules.setModule(module.copyTo(LegacyNamesModule::new));
                                                }
                                            }
                                        }
                                    }
                                    if(!foundMatch&&recipe.definition instanceof NCPFListElement){
                                        for(NCPFElementDefinition definition : ((NCPFListElement)recipe.definition).elements){
                                            for(NCPFElement internalRecipe : internalRecipes){
                                                if(definition.matches(internalRecipe.definition)){
                                                    foundMatch = true;
                                                    for(NCPFModule module : internalRecipe.modules.modules.values()){
                                                        if(recipe.modules.modules.containsKey(module.name))continue; // only fill if they don't exist already
                                                        if(module instanceof TextureModule){
                                                            recipe.modules.setModule(module.copyTo(TextureModule::new));
                                                        }
                                                        if(module instanceof DisplayNameModule){
                                                            recipe.modules.setModule(module.copyTo(DisplayNameModule::new));
                                                        }
                                                        if(module instanceof LegacyNamesModule){
                                                            recipe.modules.setModule(module.copyTo(LegacyNamesModule::new));
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

                    // Set textures & display names of any oredict elements that can get their textures from references
                    for(List<NCPFElement> elements1 : config.getAllElementsISaidAllElements()){
                        for(NCPFElement element1 : elements1){
                            if(!(element1.definition instanceof NCPFOredictElement))continue;
                            NCPFOredictElement oredict = (NCPFOredictElement)element1.definition;
                            if(element1.hasModule(TextureModule::new)&&element1.hasModule(DisplayNameModule::new))continue;
                            ArrayList<NCPFElement> potentials = new ArrayList<>();
                            for(List<NCPFElement> elements2 : config.getAllElementsISaidAllElements()){
                                for(NCPFElement element2 : elements2){
                                    element2.withModule(TagsModule::new, (tags) -> {
                                        if(tags.tags.contains(oredict.oredict)){
                                            potentials.add(element2);
                                        }
                                    });
                                }
                            }
                            if(potentials.size()==1){
                                for(NCPFModule module : potentials.get(0).modules.modules.values()){
                                    if(element1.modules.modules.containsKey(module.name))continue; // only fill if they don't exist already
                                    if(module instanceof TextureModule){
                                        element1.modules.setModule(module.copyTo(TextureModule::new));
                                    }
                                    if(module instanceof DisplayNameModule){
                                        element1.modules.setModule(module.copyTo(DisplayNameModule::new));
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
        }, file);
    }
}

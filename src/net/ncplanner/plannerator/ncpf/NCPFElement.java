package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import java.util.Arrays;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNameModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
import net.ncplanner.plannerator.planner.ncpf.module.LegacyNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public class NCPFElement extends DefinedNCPFModularObject implements Pinnable, Supplier<NCPFElement>{
    public static HashMap<String, Supplier<NCPFElementDefinition>> recognizedElements = new HashMap<>();
    public NCPFElementDefinition definition = new UnknownNCPFElement();
    public NCPFElement(){}
    public NCPFElement(NCPFElementDefinition definition){
        this.definition = definition;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        definition = recognizedElements.getOrDefault(ncpf.getString("type"), UnknownNCPFElement::new).get();
        definition.convertFromObject(ncpf);
        super.convertFromObject(ncpf);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("type", definition.type);
        definition.convertToObject(ncpf);
        super.convertToObject(ncpf);
    }
    @Override
    public String getPinnedName(){
        return getDisplayName();
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        return getLegacyNames();
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> names = getSimpleSearchableNames();
        for(NCPFModule module : modules.modules.values()){
            if(module instanceof ElementStatsModule){
                ElementStatsModule stats = (ElementStatsModule)module;
                names.addAll(Arrays.asList(stats.getTooltip().trim().split("\n")));
            }
        }
        return names;
    }
    //TODO include placement rules somehow?
    public String getName(){
        return definition.getName();
    }
    public String getDisplayName(){
        DisplayNameModule names = getModule(DisplayNameModule::new);
        if(names!=null&&names.displayName!=null)return names.displayName;
        return definition.getName();
    }
    public Image getTexture(){
        TextureModule tex = getModule(TextureModule::new);
        if(tex!=null&&tex.texture!=null)return tex.texture;
        return null;
    }
    public Image getDisplayTexture(){
        TextureModule tex = getModule(TextureModule::new);
        if(tex!=null&&tex.displayTexture!=null)return tex.displayTexture;
        if(tex!=null&&tex.texture!=null)return tex.texture;
        return null;
    }
    public ArrayList<String> getLegacyNames(){
        ArrayList<String> nams = new ArrayList<>();
        nams.add(getDisplayName());
        nams.addAll(definition.getLegacyNames());
        withModule(LegacyNamesModule::new, (names)-> nams.addAll(names.legacyNames));
        return nams;
    }
    public String getTitle(){
        return "NCPF Element";
    }
    public Supplier<NCPFModule>[] getPreferredModules(){
        return new Supplier[]{};
    }
    @Override
    public String toString(){
        return getDisplayName();
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject addon){
        try{
            super.conglomerate(addon);
        }catch(ConglomerationError ex){
            throw new ConglomerationError("Failed to conglomerate "+getDisplayName()+" with "+addon.toString()+"!", ex);
        }
    }
    public void removeModule(NCPFModule module){
        modules.removeModule(module);
    }
    @Override
    public NCPFElement get(){
        return this;
    }
}
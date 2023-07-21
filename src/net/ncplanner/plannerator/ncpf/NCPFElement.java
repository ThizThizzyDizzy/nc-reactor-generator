package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public class NCPFElement extends DefinedNCPFModularObject implements Pinnable{
    public static HashMap<String, Supplier<NCPFElementDefinition>> recognizedElements = new HashMap<>();
    public NCPFElementDefinition definition;
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
        return getSimpleSearchableNames();
    }
    //TODO include placement rules somehow?
    public String getName(){
        return definition.getName();
    }
    public String getDisplayName(){
        DisplayNamesModule names = getModule(DisplayNamesModule::new);
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
        withModule(DisplayNamesModule::new, (names)-> nams.addAll(names.legacyNames));
        return nams;
    }
    public NCPFElement asElement(){//for IBlockTemplate
        return this;
    }
    @Override
    public String toString(){
        return getDisplayName();
    }
}
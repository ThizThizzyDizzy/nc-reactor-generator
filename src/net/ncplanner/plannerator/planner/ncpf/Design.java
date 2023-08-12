package net.ncplanner.plannerator.planner.ncpf;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import net.ncplanner.plannerator.ncpf.NCPFDesign;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFDesignDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.MetadataModule;
public class Design<T extends NCPFDesignDefinition> extends NCPFDesign<T>{
    public static HashMap<String, Function<NCPFFile, Design>> registeredDesigns = new HashMap<>();
    public MetadataModule metadata = new MetadataModule();
    public Design(NCPFFile file){
        super(file);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(MetadataModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModule(metadata);
        super.convertToObject(ncpf);
    }
    public Set<NCPFElementDefinition> getElements(){
        return new HashSet<>();
    }
    public <T extends NCPFElement> void getElements(T[][][] arr, Set<NCPFElementDefinition> elems){
        for(T[][] ar : arr){
            for(T[] a : ar){
                for(T t : a){
                    if(t!=null)elems.add(t.definition);
                }
            }
        }
    }
}
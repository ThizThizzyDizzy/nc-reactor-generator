package net.ncplanner.plannerator.ncpf;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.design.NCPFDesignDefinition;
import net.ncplanner.plannerator.ncpf.design.UnknownNCPFDesign;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFDesign<T extends NCPFDesignDefinition> extends DefinedNCPFModularObject{
    public final NCPFFile file;
    public NCPFDesign(NCPFFile file){
        this.file = file;
    }
    public static HashMap<String, Supplier<NCPFDesignDefinition>> recognizedDesigns = new HashMap<>();
    public T definition;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        definition = (T)recognizedDesigns.getOrDefault(ncpf.getString("type"), UnknownNCPFDesign::new).get();
        definition.file = file;
        definition.convertFromObject(ncpf);
        super.convertFromObject(ncpf);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("type", definition.type);
        definition.convertToObject(ncpf);
        super.convertToObject(ncpf);
    }
}
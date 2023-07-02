package net.ncplanner.plannerator.ncpf.design;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
/**
 * A utility class for the plannerator; in NCPF, this is still part of NCPFDesign
 * @author thiz
 */
public abstract class NCPFDesignDefinition extends DefinedNCPFObject{
    public final String type;
    public NCPFFile file;
    public NCPFDesignDefinition(String type){
        this.type = type;
    }
    @Override
    public final void convertFromObject(NCPFObject ncpf){
        convertFromObject(ncpf, file);
    }
    public abstract void convertFromObject(NCPFObject ncpf, NCPFFile file);
    @Override
    public final void convertToObject(NCPFObject ncpf){
        convertToObject(ncpf, file);
    }
    public abstract void convertToObject(NCPFObject ncpf, NCPFFile file);
    public NCPFConfiguration getConfiguration(NCPFFile file){
        return type==null?null:file.configuration.configurations.get(type);
    }
}
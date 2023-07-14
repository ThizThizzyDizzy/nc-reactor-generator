package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
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
    public <T extends NCPFConfiguration> T getConfiguration(){
        return (T) (type==null?null:file.configuration.configurations.get(type));
    }
}
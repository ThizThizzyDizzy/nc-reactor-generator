package net.ncplanner.plannerator.ncpf;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFElementReferenceStack extends DefinedNCPFObject{
    public NCPFElementReference reference = new NCPFElementReference();
    public int amount = 0;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        reference.convertFromObject(ncpf);
        amount = ncpf.getInteger("amount");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        reference.convertToObject(ncpf);
        ncpf.setInteger("amount", amount);
    }
    @Override
    public void setReferences(List<NCPFElement> lst, boolean soft){
        reference.setReferences(lst, soft);
    }
}

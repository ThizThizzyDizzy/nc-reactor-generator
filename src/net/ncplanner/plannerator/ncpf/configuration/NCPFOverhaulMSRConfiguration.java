package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
public class NCPFOverhaulMSRConfiguration extends NCPFConfiguration{
    public List<NCPFElement> blocks = new ArrayList<>();
    public NCPFOverhaulMSRConfiguration(){
        super("nuclearcraft:overhaul_msr");
        defineNCPFField(getClass(), "blocks", (c) -> c.blocks, (lst) -> blocks = lst);
    }
    @Override
    public String getName(){
        return "Overhaul MSR Configuration";
    }
}

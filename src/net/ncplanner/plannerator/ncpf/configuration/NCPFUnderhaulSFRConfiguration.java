package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
public class NCPFUnderhaulSFRConfiguration extends NCPFConfiguration{
    public List<NCPFElement> blocks = new ArrayList<>();
    public List<NCPFElement> fuels = new ArrayList<>();
    public NCPFUnderhaulSFRConfiguration(){
        super("nuclearcraft:underhaul_sfr");
        defineNCPFField(getClass(), "blocks", (c) -> c.blocks, (lst) -> blocks = lst);
        defineNCPFField(getClass(), "fuels", (c) -> c.fuels, (lst) -> fuels = lst);
    }
    @Override
    public String getName(){
        return "Underhaul SFR Configuration";
    }
}

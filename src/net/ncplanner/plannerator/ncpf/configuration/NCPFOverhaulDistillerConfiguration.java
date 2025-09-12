package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
public class NCPFOverhaulDistillerConfiguration extends NCPFConfiguration{
    public List<NCPFElement> blocks = new ArrayList<>();
    public List<NCPFElement> recipes = new ArrayList<>();
    public NCPFOverhaulDistillerConfiguration(){
        super("nuclearcraft:overhaul_distiller");
        defineNCPFField(getClass(), "blocks", (config) -> config.blocks, (lst) -> blocks = lst);
        defineNCPFField(getClass(), "recipes", (config) -> config.recipes, (lst) -> recipes = lst);
    }
    @Override
    public String getName(){
        return "Overhaul Distiller Configuration";
    }
}

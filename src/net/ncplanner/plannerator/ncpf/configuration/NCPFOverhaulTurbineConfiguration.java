package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
public class NCPFOverhaulTurbineConfiguration extends NCPFConfiguration{
    public List<NCPFElement> blocks = new ArrayList<>();
    public List<NCPFElement> recipes = new ArrayList<>();
    public NCPFOverhaulTurbineConfiguration(){
        super("nuclearcraft:overhaul_turbine");
        defineNCPFField(getClass(), "blocks", (c) -> c.blocks, (lst) -> blocks = lst);
        defineNCPFField(getClass(), "recipes", (c) -> c.recipes, (lst) -> recipes = lst);
    }
    @Override
    public String getName(){
        return "Overhaul Turbine Configuration";
    }
}

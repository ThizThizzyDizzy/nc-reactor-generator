package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
public class NCPFOverhaulSFRConfiguration extends NCPFConfiguration{
    public List<NCPFElement> blocks = new ArrayList<>();
    public List<NCPFElement> coolantRecipes = new ArrayList<>();
    public NCPFOverhaulSFRConfiguration(){
        super("nuclearcraft:overhaul_sfr");
        defineNCPFField(getClass(), "blocks", (c) -> c.blocks, (lst) -> blocks = lst);
        defineNCPFField(getClass(), "coolant_recipes", (c) -> c.coolantRecipes, (lst) -> coolantRecipes = lst);
    }
    @Override
    public String getName(){
        return "Overhaul SFR Configuration";
    }
}

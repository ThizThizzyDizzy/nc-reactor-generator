package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulTurbineConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@Deprecated
@RegisterWith(module = OverhaulModule.class)
public class NCPFOverhaulTurbineDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement recipe;
    public final java.util.ArrayList<Integer> inputIndices = new java.util.ArrayList<>();
    public NCPFOverhaulTurbineDesign(){
        super("nuclearcraft:overhaul_turbine");
    }
    public NCPFOverhaulTurbineDesign(NCPFFile file){
        this();
        this.file = file;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        inputIndices.clear();
        net.ncplanner.plannerator.ncpf.io.NCPFList list = ncpf.getNCPFList("inputs");
        if(list!=null)for(int i=0;i<list.size();i++)inputIndices.add(list.getInteger(i));
        NCPFOverhaulTurbineConfiguration config = getConfiguration();
        ncpf.getDefined3DArray("design", design, config.blocks);
        recipe = ncpf.getIndex("recipe", config.recipes);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        NCPFOverhaulTurbineConfiguration config = getConfiguration();
        ncpf.setDefined3DArray("design", design, config.blocks);
        ncpf.setIndex("recipe", recipe, config.recipes);
        if(!inputIndices.isEmpty()){
            net.ncplanner.plannerator.ncpf.io.NCPFList<Integer> list = new net.ncplanner.plannerator.ncpf.io.NCPFList<>();
            list.addAll(inputIndices);
            ncpf.setNCPFList("inputs", list);
        }
        super.convertToObject(ncpf);
    }
}

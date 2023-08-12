package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulTurbineConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFOverhaulTurbineDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement recipe;
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
        NCPFOverhaulTurbineConfiguration config = getConfiguration();
        ncpf.getDefined3DArray("design", design, config.blocks);
        recipe = ncpf.getIndex("recipe", config.recipes);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        NCPFOverhaulTurbineConfiguration config = getConfiguration();
        ncpf.setDefined3DArray("design", design, config.blocks);
        ncpf.setIndex("recipe", recipe, config.recipes);
        super.convertToObject(ncpf);
    }
}
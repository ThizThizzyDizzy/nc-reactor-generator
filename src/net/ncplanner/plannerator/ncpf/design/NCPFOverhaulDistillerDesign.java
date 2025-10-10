package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulDistillerConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class NCPFOverhaulDistillerDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement recipe;
    public NCPFOverhaulDistillerDesign(){
        super("nuclearcraft:overhaul_distiller");
    }
    public NCPFOverhaulDistillerDesign(NCPFFile file){
        this();
        this.file = file;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        NCPFOverhaulDistillerConfiguration config = getConfiguration();
        ncpf.getDefined3DArray("design", design, config.blocks);
        recipe = ncpf.getIndex("recipe", config.recipes);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        super.convertToObject(ncpf);
        NCPFOverhaulDistillerConfiguration config = getConfiguration();
        ncpf.setDefined3DArray("design", design, config.blocks);
        ncpf.setIndex("recipe", recipe, config.recipes);
    }
}

package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.design.*;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = FusionTestModule.class)
public class OverhaulFusionDefinition extends NCPFDesignDefinition{
    private final NCPFObject document = new NCPFObject();
    public OverhaulFusionDefinition(){
        super("plannerator:fusion_test");
    }//all the save/load logic is in OverhaulFusionDesign
    @Override
    public void convertFromObject(NCPFObject ncpf){
        document.clear();
        document.putAll(ncpf);
        document.remove("modules");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        // Generic design copies must carry the payload; typed wrappers supply edited values first.
        document.forEach(ncpf::putIfAbsent);
    }
}

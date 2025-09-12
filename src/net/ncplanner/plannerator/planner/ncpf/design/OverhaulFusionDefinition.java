package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.design.*;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = FusionTestModule.class)
public class OverhaulFusionDefinition extends NCPFDesignDefinition{
    public OverhaulFusionDefinition(){
        super("plannerator:fusion_test");
    }//all the save/load logic is in OverhaulFusionDesign
    @Override
    public void convertFromObject(NCPFObject ncpf){
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
    }
}

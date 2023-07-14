package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.design.*;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class OverhaulFusionDefinition extends NCPFDesignDefinition{
    public OverhaulFusionDefinition(){
        super("plannerator:fusion_test");
    }//all the save/load logic is in OverhaulFusionDesign
    @Override
    public void convertFromObject(NCPFObject ncpf){}
    @Override
    public void convertToObject(NCPFObject ncpf){}
}
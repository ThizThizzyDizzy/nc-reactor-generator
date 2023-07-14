package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class ShieldingModule extends BlockFunctionModule{
    public float shieldiness;
    public ShieldingModule(){
        super("plannerator:fusion_test:shielding");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        shieldiness = ncpf.getFloat("shieldiness");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("shieldiness", shieldiness);
    }
}
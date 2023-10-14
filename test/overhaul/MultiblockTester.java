package overhaul;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.Core;
public class MultiblockTester{
    public void test(){
        for(Configuration config : Configuration.configurations){
            Core.configuration = config;
            if(config.underhaul!=null&&config.underhaul.fissionSFR!=null){
                MultiblockGenerator.getGenerators(new UnderhaulSFR());
            }
            if(config.overhaul!=null&&config.overhaul.fissionSFR!=null){
                MultiblockGenerator.getGenerators(new OverhaulSFR());
            }
            if(config.overhaul!=null&&config.overhaul.fissionMSR!=null){
                MultiblockGenerator.getGenerators(new OverhaulMSR());
            }
            if(config.overhaul!=null&&config.overhaul.turbine!=null){
                MultiblockGenerator.getGenerators(new OverhaulTurbine());
            }
        }
    }
}

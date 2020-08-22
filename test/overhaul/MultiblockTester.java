package overhaul;
import generator.MultiblockGenerator;
import multiblock.configuration.Configuration;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.turbine.OverhaulTurbine;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import org.junit.Test;
import planner.Core;
public class MultiblockTester{
    @Test
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

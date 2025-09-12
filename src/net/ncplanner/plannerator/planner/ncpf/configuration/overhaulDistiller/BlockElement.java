package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller;
import net.ncplanner.plannerator.planner.ncpf.configuration.NamedTexturedNCPFElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.LiquidDistributerModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.PowerPortModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.ProcessPortModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.ReboilingUnitModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.RefluxUnitModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.ReservoirPortModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.SieveAssemblyModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller.SieveTrayModule;
public class BlockElement extends NamedTexturedNCPFElement{
    public CasingModule casing;
    public ControllerModule controller;
    public PowerPortModule powerPort;
    public ProcessPortModule processPort;
    public ReservoirPortModule reservoirPort;
    public LiquidDistributerModule liquidDistributer;
    public RefluxUnitModule refluxUnit;
    public ReboilingUnitModule reboilingUnit;
    public SieveTrayModule sieveTray;
    public SieveAssemblyModule sieveAssembly;
    public BlockElement(){
        definePlanneratorModule(() -> casing, (m) -> casing = m, CasingModule::new);
        definePlanneratorModule(() -> controller, (m) -> controller = m, ControllerModule::new);
        definePlanneratorModule(() -> powerPort, (m) -> powerPort = m, PowerPortModule::new);
        definePlanneratorModule(() -> processPort, (m) -> processPort = m, ProcessPortModule::new);
        definePlanneratorModule(() -> reservoirPort, (m) -> reservoirPort = m, ReservoirPortModule::new);
        definePlanneratorModule(() -> liquidDistributer, (m) -> liquidDistributer = m, LiquidDistributerModule::new);
        definePlanneratorModule(() -> refluxUnit, (m) -> refluxUnit = m, RefluxUnitModule::new);
        definePlanneratorModule(() -> reboilingUnit, (m) -> reboilingUnit = m, ReboilingUnitModule::new);
        definePlanneratorModule(() -> sieveTray, (m) -> sieveTray = m, SieveTrayModule::new);
        definePlanneratorModule(() -> sieveAssembly, (m) -> sieveAssembly = m, SieveAssemblyModule::new);
    }
    @Override
    public String getTitle(){
        return "Block";
    }
}

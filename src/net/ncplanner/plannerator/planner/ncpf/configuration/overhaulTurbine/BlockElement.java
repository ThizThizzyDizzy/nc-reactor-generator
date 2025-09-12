package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine;
import net.ncplanner.plannerator.planner.ncpf.configuration.NamedTexturedNCPFElement;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BearingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BladeModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CoilModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ConnectorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ControllerModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.InletModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.OutletModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ShaftModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.StatorModule;
public class BlockElement extends NamedTexturedNCPFElement{
    public BladeModule blade;
    public StatorModule stator;
    public CoilModule coil;
    public BearingModule bearing;
    public ShaftModule shaft;
    public ConnectorModule connector;
    public ControllerModule controller;
    public CasingModule casing;
    public InletModule inlet;
    public OutletModule outlet;
    public BlockElement(){
        definePlanneratorModule(() -> blade, (m) -> blade = m, BladeModule::new);
        definePlanneratorModule(() -> stator, (m) -> stator = m, StatorModule::new);
        definePlanneratorModule(() -> coil, (m) -> coil = m, CoilModule::new);
        definePlanneratorModule(() -> bearing, (m) -> bearing = m, BearingModule::new);
        definePlanneratorModule(() -> shaft, (m) -> shaft = m, ShaftModule::new);
        definePlanneratorModule(() -> connector, (m) -> connector = m, ConnectorModule::new);
        definePlanneratorModule(() -> controller, (m) -> controller = m, ControllerModule::new);
        definePlanneratorModule(() -> casing, (m) -> casing = m, CasingModule::new);
        definePlanneratorModule(() -> inlet, (m) -> inlet = m, InletModule::new);
        definePlanneratorModule(() -> outlet, (m) -> outlet = m, OutletModule::new);
    }
    @Override
    public String getTitle(){
        return "Block";
    }
}

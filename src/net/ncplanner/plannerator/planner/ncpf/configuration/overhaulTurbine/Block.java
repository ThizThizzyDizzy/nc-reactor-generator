package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
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
public class Block extends NCPFElement{
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
    public BladeModule blade = new BladeModule();
    public StatorModule stator = new StatorModule();
    public CoilModule coil = new CoilModule();
    public BearingModule bearing = new BearingModule();
    public ShaftModule shaft = new ShaftModule();
    public ConnectorModule connector = new ConnectorModule();
    public ControllerModule controller = new ControllerModule();
    public CasingModule casing = new CasingModule();
    public InletModule inlet = new InletModule();
    public OutletModule outlet = new OutletModule();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        names = getModule(DisplayNamesModule::new);
        texture = getModule(TextureModule::new);
        blade = getModule(BladeModule::new);
        stator = getModule(StatorModule::new);
        coil = getModule(CoilModule::new);
        bearing = getModule(BearingModule::new);
        shaft = getModule(ShaftModule::new);
        connector = getModule(ConnectorModule::new);
        controller = getModule(ControllerModule::new);
        casing = getModule(CasingModule::new);
        inlet = getModule(InletModule::new);
        outlet = getModule(OutletModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModules(names, texture, blade, stator, coil, bearing, shaft, connector, controller, casing, inlet, outlet);
        super.convertToObject(ncpf);
    }
}
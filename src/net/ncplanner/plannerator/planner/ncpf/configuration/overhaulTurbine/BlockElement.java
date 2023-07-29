package net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
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
public class BlockElement extends NCPFElement{
    public DisplayNamesModule names = new DisplayNamesModule();
    public TextureModule texture = new TextureModule();
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
    public BlockElement(){}
    public BlockElement(NCPFElementDefinition definition){
        super(definition);
    }
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
package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class FuelStatsModule extends NCPFModule{
    public float power;
    public float heat;
    public int time;
    public FuelStatsModule(){
        super("nuclearcraft:underhaul_sfr:fuel_stats");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        power = ncpf.getFloat("power");
        heat = ncpf.getFloat("heat");
        time = ncpf.getInteger("time");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("power", power);
        ncpf.setFloat("heat", heat);
        ncpf.setInteger("time", time);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Fuel stats may not be overwritten!");
    }
}
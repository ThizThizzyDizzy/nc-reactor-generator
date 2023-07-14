package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFUnderhaulSFRDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement fuel;
    public NCPFUnderhaulSFRDesign(){
        super("nuclearcraft:underhaul_sfr");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        NCPFUnderhaulSFRConfiguration config = getConfiguration();
        ncpf.getDefined3DArray("design", design, config.blocks);
        fuel = config.fuels.get(ncpf.getInteger("fuel"));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        NCPFUnderhaulSFRConfiguration config = getConfiguration();
        ncpf.setDefined3DArray("design", design, config.blocks);
        ncpf.setInteger("fuel", config.fuels.indexOf(fuel));
        super.convertToObject(ncpf);
    }
}
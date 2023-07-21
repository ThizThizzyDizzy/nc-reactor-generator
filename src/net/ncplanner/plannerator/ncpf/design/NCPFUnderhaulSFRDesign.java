package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFUnderhaulSFRDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement fuel;
    public NCPFUnderhaulSFRDesign(){
        super("nuclearcraft:underhaul_sfr");
    }
    public NCPFUnderhaulSFRDesign(NCPFFile file){
        this();
        this.file = file;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        NCPFUnderhaulSFRConfiguration config = getConfiguration();
        ncpf.getDefined3DArray("design", design, config.blocks);
        fuel = ncpf.getIndex("fuel", config.fuels);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        NCPFUnderhaulSFRConfiguration config = getConfiguration();
        ncpf.setDefined3DArray("design", design, config.blocks);
        ncpf.setIndex("fuel", fuel, config.fuels);
        super.convertToObject(ncpf);
    }
}
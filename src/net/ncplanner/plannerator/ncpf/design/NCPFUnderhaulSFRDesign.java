package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFUnderhaulSFRDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement fuel;
    public NCPFUnderhaulSFRDesign(){
        super("nuclearcraft:underhaul_sfr");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf, NCPFFile file){
        super.convertFromObject(ncpf, file);
        NCPFUnderhaulSFRConfiguration config = (NCPFUnderhaulSFRConfiguration) getConfiguration(file);
        NCPFList des = ncpf.getNCPFList("design");
        int i = 0;
        for(int x = 0; x<=design.length; x++){
            for(int y = 0; y<=design[x].length; y++){
                for(int z = 0; z<=design[z].length; z++){
                    design[x][y][z] = config.blocks.get(des.getInteger(i++));
                }
            }
        }
        fuel = config.fuels.get(ncpf.getInteger("fuel"));
    }
    @Override
    public void convertToObject(NCPFObject ncpf, NCPFFile file){
        super.convertToObject(ncpf, file);
        NCPFUnderhaulSFRConfiguration config = (NCPFUnderhaulSFRConfiguration) getConfiguration(file);
        NCPFList<Integer> des = new NCPFList<>();
        for(int x = 0; x<=design.length; x++){
            for(int y = 0; y<=design[x].length; y++){
                for(int z = 0; z<=design[z].length; z++){
                    des.add(config.blocks.indexOf(design[x][y][z]));
                }
            }
        }
        ncpf.setNCPFList("design", des);
        ncpf.setInteger("fuel", config.fuels.indexOf(fuel));
    }
}
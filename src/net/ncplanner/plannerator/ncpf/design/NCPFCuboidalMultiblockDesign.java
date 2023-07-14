package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFCuboidalMultiblockDesign extends NCPFDesignDefinition{
    public NCPFElement[][][] design;
    public NCPFCuboidalMultiblockDesign(String type){
        super(type);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        NCPFList dims = ncpf.getNCPFList("dimensions");
        design = new NCPFElement[dims.getInteger(0)][dims.getInteger(1)][dims.getInteger(2)];
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        NCPFList dims = new NCPFList();
        dims.add(design.length);
        dims.add(design[0].length);
        dims.add(design[0][0].length);
        ncpf.setNCPFList("dimensions", dims);
    }
}
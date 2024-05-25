package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFDesignDefinition;
import net.ncplanner.plannerator.planner.ncpf.Design;
public abstract class MultiblockDesign<Definition extends NCPFDesignDefinition, T extends Multiblock> extends Design<Definition>{
    public MultiblockDesign(NCPFFile file){
        super(file);
    }
    public T toMultiblock(){
        T mb = convertToMultiblock();
        if(metadata!=null)mb.metadata.putAll(metadata.metadata);
        return mb;
    }
    public abstract T convertToMultiblock();
    public abstract void convertElements();
}
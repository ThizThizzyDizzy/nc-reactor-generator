package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFDesignDefinition;
import net.ncplanner.plannerator.planner.ncpf.Design;
public abstract class MultiblockDesign<Definition extends NCPFDesignDefinition, T extends Multiblock> extends Design<Definition>{
    public MultiblockDesign(NCPFFile file){
        super(file);
    }
    private static final ThreadLocal<java.util.IdentityHashMap<MultiblockDesign,Multiblock>> conversions = new ThreadLocal<>();
    public T toMultiblock(){
        boolean root = conversions.get()==null;
        if(root)conversions.set(new java.util.IdentityHashMap<>());
        try{
            Multiblock existing = conversions.get().get(this);
            if(existing!=null)return (T)existing;
            T mb = convertToMultiblock();
            conversions.get().put(this, mb);
            if(metadata!=null)mb.metadata.putAll(metadata.metadata);
            if(this instanceof OverhaulTurbineDesign && mb instanceof net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine){
                OverhaulTurbineDesign design = (OverhaulTurbineDesign)this;
                net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine turbine = (net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine)mb;
                for(MultiblockDesign input : design.inputs)turbine.inputs.add(input.toMultiblock());
                turbine.inputDesignIndices.addAll(design.definition.inputIndices);
            }
            return mb;
        }finally{
            if(root)conversions.remove();
        }
    }
    public abstract T convertToMultiblock();
    public abstract void convertElements();
}

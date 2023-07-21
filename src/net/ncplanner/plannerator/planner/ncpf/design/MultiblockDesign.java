package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.multiblock.Multiblock;
public interface MultiblockDesign<T extends Multiblock>{
    public T toMultiblock();
}
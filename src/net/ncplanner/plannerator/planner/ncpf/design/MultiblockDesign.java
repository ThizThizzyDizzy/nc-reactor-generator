package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.ncpf.Project;
public interface MultiblockDesign<T extends Multiblock>{
    public T toMultiblock();
    public void convertElements();
}
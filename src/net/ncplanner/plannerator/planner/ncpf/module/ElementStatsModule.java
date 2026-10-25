package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.NCPFElement;
@Deprecated
public interface ElementStatsModule{
    public String getTooltip();
    public default String getTooltip(NCPFElement element){
        return getTooltip();
    }
}

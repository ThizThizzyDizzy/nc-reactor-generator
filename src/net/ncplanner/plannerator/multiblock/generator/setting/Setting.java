package net.ncplanner.plannerator.multiblock.generator.setting;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public interface Setting<T>{
    public T getValue();
    public void setValue(T value);
    public void buildComponents(SingleColumnList generatorSettings);
}
package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public interface Setting<T> extends Variable<T>{
    public void set(T value);
    public void addSettings(SingleColumnList list, MenuGenerator menu);
}
package net.ncplanner.plannerator.multiblock.generator.setting;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimaList;
public interface Setting<T>{
    public T getValue();
    public void setValue(T value);
    public void buildComponents(MenuComponentMinimaList generatorSettings);
}
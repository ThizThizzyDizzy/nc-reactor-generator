package generator.setting;
import planner.menu.component.MenuComponentMinimaList;
public interface Setting<T>{
    public T getValue();
    public void setValue(T value);
    public void buildComponents(MenuComponentMinimaList generatorSettings);
}
package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public class SettingVariable<T> implements Setting<Variable<T>>{
    private final String name;
    private Variable<T> value;
    public SettingVariable(String name, Variable<T> value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public void set(Variable<T> value){
        this.value = value;
    }
    @Override
    public Variable<T> get(){
        return value;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        list.add(new Label(0, 0, 0, 28, "TODO SettingVariable settings"));//TODO SettingVariable settings
    }
}
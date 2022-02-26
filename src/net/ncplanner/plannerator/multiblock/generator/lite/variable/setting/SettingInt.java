package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNumber;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class SettingInt extends VariableNumber<Integer> implements Setting<Integer>{
    private final String name;
    private int value = 0;
    public SettingInt(String name, int value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Integer get(){
        return value;
    }
    @Override
    public void set(Integer value){
        this.value = value;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        list.add(new TextBox(0, 0, 0, 32, value+"", true, name, 10){
            {
                onChange(() -> {
                    set(Integer.parseInt(text));
                });
            }
        }.setIntFilter());
    }
}
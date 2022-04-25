package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNumber;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class SettingPercent extends VariableNumber<Float> implements Setting<Float>{
    private final String name;
    private float value = 0;
    public SettingPercent(String name, float value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Float get(){
        return value;
    }
    @Override
    public void set(Float value){
        this.value = value;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        list.add(new TextBox(0, 0, 0, 32, value*100+"", true, name, 10){
            {
                onChange(() -> {
                    set(Float.parseFloat(text)/100);
                });
            }
        }.setFloatFilter().setSuffix("%"));
    }
}
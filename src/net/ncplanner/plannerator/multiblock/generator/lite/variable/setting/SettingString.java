package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class SettingString implements Setting<String>{
    private final String name;
    private String value;
    public SettingString(String name, String value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String get(){
        return value;
    }
    @Override
    public void set(String value){
        this.value = value;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        list.add(new TextBox(0, 0, 0, 32, value+"", true, name, 10){
            {
                onChange(() -> {
                    set(text);
                });
            }
        });
    }
}
package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
public class SettingBoolean implements Setting<Boolean>{
    private final String name;
    private boolean value;
    public SettingBoolean(String name, boolean value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Boolean get(){
        return value;
    }
    @Override
    public void set(Boolean value){
        this.value = value;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        list.add(new ToggleBox(0, 0, 0, 28, name, value){
            {
                onChange(() -> {
                    set(isToggledOn);
                });
            }
        });
    }
}
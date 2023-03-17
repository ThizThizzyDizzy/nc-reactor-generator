package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.OptionButton;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
public class SettingBoolean implements Setting<Boolean>{
    private final String name;
    private boolean value;
    private final String otherName;
    private boolean allowSliding = false;
    public SettingBoolean(String name, boolean value){
        this(name, value, null);
    }
    public SettingBoolean(String name, boolean value, String otherName){
        this.name = name;
        this.value = value;
        this.otherName = otherName;
    }
    public SettingBoolean allowSliding(){
        allowSliding = true;
        return this;
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
        if(otherName!=null){
            list.add(new OptionButton(0, 0, 0, 28, null, true, value?1:0, name, otherName){
                {
                    onChange(() -> {
                        set(currentIndex==1);
                    });
                }
            });
        }else{
            list.add(new ToggleBox(0, 0, 0, 28, name, value){
                {
                    onChange(() -> {
                        set(isToggledOn);
                    });
                }
            }).allowSliding(allowSliding);
        }
    }
}
package net.ncplanner.plannerator.multiblock.generator.setting;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
public class SettingBoolean implements Setting<Boolean>{
    private final String name;
    private final String tooltip;
    private boolean value;
    public SettingBoolean(String name, boolean defaultValue){
        this(name, defaultValue, null);
    }
    public SettingBoolean(String name, boolean defaultValue, String tooltip){
        this.name = name;
        this.tooltip = tooltip;
        this.value = defaultValue;
    }
    @Override
    public Boolean getValue(){
        return value;
    }
    @Override
    public void setValue(Boolean value){
        this.value = value;
    }
    @Override
    public void buildComponents(SingleColumnList generatorSettings){
        generatorSettings.add(new ToggleBox(0, 0, 0, 32, name, value){
            @Override
            public void onMouseButton(double x, double y, int button, int action, int mods){
                super.onMouseButton(x, y, button, action, mods);
                value = isToggledOn;
            }
        }.setTooltip(tooltip));
    }
}
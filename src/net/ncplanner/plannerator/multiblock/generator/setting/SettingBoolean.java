package net.ncplanner.plannerator.multiblock.generator.setting;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimaList;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentToggleBox;
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
    public void buildComponents(MenuComponentMinimaList generatorSettings){
        generatorSettings.add(new MenuComponentToggleBox(0, 0, 0, 32, name, value){
            @Override
            public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                super.onMouseButton(x, y, button, pressed, mods);
                value = isToggledOn;
            }
        }.setTooltip(tooltip));
    }
}
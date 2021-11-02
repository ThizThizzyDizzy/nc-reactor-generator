package net.ncplanner.plannerator.multiblock.generator.setting;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimaList;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistTextBox;
public class SettingInt implements Setting<Integer>{
    private final String name;
    private final Integer min;
    private final Integer max;
    private final String tooltip;
    private int value;
    public SettingInt(String name, Integer min, Integer max, int defaultValue, String tooltip){
        this.name = name;
        this.min = min;
        this.max = max;
        this.tooltip = tooltip;
        this.value = defaultValue;
    }
    @Override
    public Integer getValue(){
        return value;
    }
    @Override
    public void setValue(Integer value){
        this.value = value;
    }
    @Override
    public void buildComponents(MenuComponentMinimaList generatorSettings){
        generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, value+"", true, name){
            @Override
            public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
                super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
                value = Integer.parseInt(text);
            }
            @Override
            public void onCharTyped(char c){
                super.onCharTyped(c);
                value = Integer.parseInt(text);
            }
        }.setIntFilter(min, max).setTooltip(tooltip));
    }
}
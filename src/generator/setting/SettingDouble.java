package generator.setting;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistTextBox;
public class SettingDouble implements Setting<Double>{
    protected final String name;
    protected final Double min;
    protected final Double max;
    protected final String tooltip;
    protected double value;
    public SettingDouble(String name, Double min, Double max, double defaultValue, String tooltip){
        this.name = name;
        this.min = min;
        this.max = max;
        this.tooltip = tooltip;
        this.value = defaultValue;
    }
    @Override
    public Double getValue(){
        return value;
    }
    @Override
    public void setValue(Double value){
        this.value = value;
    }
    @Override
    public void buildComponents(MenuComponentMinimaList generatorSettings){
        generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, value+"", true, name){
            @Override
            public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
                super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
                value = Double.parseDouble(text);
            }
            @Override
            public void onCharTyped(char c){
                super.onCharTyped(c);
                value = Double.parseDouble(text);
            }
        }.setDoubleFilter(min, max).setTooltip(tooltip));
    }
}
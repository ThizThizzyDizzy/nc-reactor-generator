package planner.menu.configuration.overhaul.turbine;
import org.lwjgl.opengl.Display;
import planner.Core;
import multiblock.configuration.overhaul.turbine.PlacementRule;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistSlider;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuPlacementRuleConfiguration extends Menu{
    private final MenuComponentMinimalistOptionButton type = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Type", true, true, 0, PlacementRule.RuleType.getStringList()));
    private final MenuComponentMinimalistOptionButton coilType = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Type", true, true, 0, PlacementRule.CoilType.getStringList()));
    private final MenuComponentMinimalistOptionButton coil = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Coil", true, true, 0, Core.configuration.overhaul.turbine.getCoilStringList()));
    private final MenuComponentMinimalistSlider min = add(new MenuComponentMinimalistSlider(0, 0, 0, 0, "Minimum", 0, 6, 1, true));
    private final MenuComponentMinimalistSlider max = add(new MenuComponentMinimalistSlider(0, 0, 0, 0, "Maximum", 0, 6, 6, true));
    private final MenuComponentMinimalistButton rules = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Rules", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final PlacementRule rule;
    public MenuPlacementRuleConfiguration(GUI gui, Menu parent, PlacementRule rule){
        super(gui, parent);
        rules.addActionListener((e) -> {
            gui.open(new MenuPlacementRulesConfiguration(gui, this, rule));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.rule = rule;
    }
    @Override
    public void onGUIOpened(){
        type.setIndex(rule.ruleType.ordinal());
        coilType.setIndex(rule.coilType.ordinal());
        coil.setIndex(rule.coil==null?0:Core.configuration.overhaul.turbine.coils.indexOf(rule.coil));
        min.setValue(rule.min);
        max.setValue(rule.max);
    }
    @Override
    public void onGUIClosed(){
        rule.ruleType = PlacementRule.RuleType.values()[type.getIndex()];
        rule.coilType = PlacementRule.CoilType.values()[coilType.getIndex()];
        rule.coil = Core.configuration.overhaul.turbine.coils.get(coil.getIndex());
        rule.min = (byte) min.getValue();
        rule.max = (byte) max.getValue();
    }
    @Override
    public void render(int millisSinceLastTick){
        type.width = coilType.width = coil.width = min.width = max.width = rules.width = back.width = Display.getWidth();
        type.height = coilType.height = coil.height = min.height = max.height = rules.height = back.height = Display.getHeight()/16;
        coilType.y = coil.y = min.y = max.y = rules.y = -Display.getHeight()/8;
        switch(PlacementRule.RuleType.values()[type.getIndex()]){
            case AXIAL:
            case BETWEEN:
                coil.y = type.height;
                min.y = coil.y+coil.height;
                max.y = min.y+min.height;
                break;
            case AXIAL_GROUP:
            case BETWEEN_GROUP:
                coilType.y = type.height;
                min.y = coilType.y+coilType.height;
                max.y = min.y+min.height;
                break;
            case EDGE:
                coil.y = type.height;
                break;
            case EDGE_GROUP:
                coilType.y = type.height;
                break;
            case AND:
            case OR:
                rules.y = type.height;
                break;
            default:
                throw new IllegalArgumentException("Unknown rule type: "+PlacementRule.RuleType.values()[type.getIndex()].name());
        }
        back.y = Display.getHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}
package planner.menu.configuration.underhaul.fissionsfr;
import planner.Core;
import multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistSlider;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
public class MenuPlacementRuleConfiguration extends Menu{
    private final MenuComponentMinimalistOptionButton type = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Type", true, true, 0, PlacementRule.RuleType.getStringList()));
    private final MenuComponentMinimalistOptionButton blockType = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Type", true, true, 0, PlacementRule.BlockType.getStringList()));
    private final MenuComponentMinimalistOptionButton block = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Block", true, true, 0, Core.configuration.underhaul.fissionSFR.getAllBlocksStringList()));
    private final MenuComponentMinimalistSlider min = add(new MenuComponentMinimalistSlider(0, 0, 0, 0, "Minimum", 0, 6, 1, true).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
    private final MenuComponentMinimalistSlider max = add(new MenuComponentMinimalistSlider(0, 0, 0, 0, "Maximum", 0, 6, 6, true).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
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
        blockType.setIndex(rule.blockType.ordinal());
        block.setIndex(rule.block==null?0:Core.configuration.underhaul.fissionSFR.allBlocks.indexOf(rule.block));
        min.setValue(rule.min);
        max.setValue(rule.max);
    }
    @Override
    public void onGUIClosed(){
        rule.ruleType = PlacementRule.RuleType.values()[type.getIndex()];
        rule.blockType = PlacementRule.BlockType.values()[blockType.getIndex()];
        rule.block = Core.configuration.underhaul.fissionSFR.allBlocks.get(block.getIndex());
        rule.min = (byte) min.getValue();
        rule.max = (byte) max.getValue();
    }
    @Override
    public void render(int millisSinceLastTick){
        type.width = blockType.width = block.width = min.width = max.width = rules.width = back.width = Core.helper.displayWidth();
        type.height = blockType.height = block.height = min.height = max.height = rules.height = back.height = Core.helper.displayHeight()/16;
        blockType.y = block.y = min.y = max.y = rules.y = -Core.helper.displayHeight()/8;
        switch(PlacementRule.RuleType.values()[type.getIndex()]){
            case AXIAL:
            case BETWEEN:
                block.y = type.height;
                min.y = block.y+block.height;
                max.y = min.y+min.height;
                break;
            case AXIAL_GROUP:
            case BETWEEN_GROUP:
                blockType.y = type.height;
                min.y = blockType.y+blockType.height;
                max.y = min.y+min.height;
                break;
            case VERTEX:
                block.y = type.height;
                break;
            case VERTEX_GROUP:
                blockType.y = type.height;
                break;
            case AND:
            case OR:
                rules.y = type.height;
                break;
            default:
                throw new IllegalArgumentException("Unknown rule type: "+PlacementRule.RuleType.values()[type.getIndex()].name());
        }
        back.y = Core.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}
package net.ncplanner.plannerator.planner.gui.menu.configuration;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.AbstractBlockContainer;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockTemplate;
import net.ncplanner.plannerator.multiblock.configuration.IBlockType;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.DropdownList;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Slider;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import static org.lwjgl.glfw.GLFW.*;

public class MenuPlacementRuleConfiguration<BlockType extends IBlockType,
        Template extends IBlockTemplate,
        PlacementRule extends AbstractPlacementRule<BlockType, Template>> extends ConfigurationMenu {

    private final PlacementRule rule;
    private final DropdownList type, block;
    private final Slider min, max;
    private final Label placementRulesLabel;
    private final SingleColumnList placementRules;
    private final Button addRule;
    private final AbstractBlockContainer<Template> blockList;
    private final BlockType[] blockTypes;
    private boolean refreshNeeded = false;

    public MenuPlacementRuleConfiguration(GUI gui, Menu parent, Configuration configuration, PlacementRule rule,
                                          AbstractBlockContainer<Template> blockList, BlockType[] blockTypes){
        super(gui, parent, configuration, "Placement Rule");
        this.blockList = blockList;
        this.blockTypes = blockTypes;
        type = add(new DropdownList(sidebar.width, 0, 0, 64));
        for(AbstractPlacementRule.RuleType ruleType : AbstractPlacementRule.RuleType.values()){
            type.add(new Label(0, 0, 0, 0, ruleType.name){
                @Override
                public void onMouseButton(double x, double y, int button, int action, int mods){
                    super.onMouseButton(x, y, button, action, mods);
                    if(button==0&&action==GLFW_PRESS){
                        rule.ruleType = ruleType;
                        MenuPlacementRuleConfiguration.this.onOpened();
                        isFocused = false;
                        type.isFocused = false;
                        MenuPlacementRuleConfiguration.this.focusedComponent = null;
                    }
                }
            });
        }
        block = add(new DropdownList(sidebar.width, type.height, 0, 64, true));
        min = add(new Slider(sidebar.width, block.y+block.height, 0, 64, "Minimum", 0, 6, 1, true).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
        max = add(new Slider(sidebar.width, block.y+block.height, 0, 64, "Maximum", 0, 6, 6, true).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
        placementRulesLabel = add(new Label(sidebar.width, min.y+min.height, 0, 48, "Placement Rules", true));
        placementRules = add(new SingleColumnList(sidebar.width, placementRulesLabel.y+placementRulesLabel.height, 0, 0, 16));
        addRule = add(new Button(sidebar.width, 0, 0, 48, "New Rule", true, true));
        addRule.addAction(() -> {
            PlacementRule rul;
            rule.rules.add(rul = (PlacementRule) rule.newRule());
            gui.open(new MenuPlacementRuleConfiguration<>(gui, this, configuration, rul, blockList, blockTypes));
        });
        this.rule = rule;
    }

    @Override
    public void onOpened(){
        type.setSelectedIndex(rule.ruleType.ordinal());
        block.clear();
        switch(rule.ruleType){
            case BETWEEN:
            case AXIAL:
                setupBlocks();
                block.preferredHeight = min.height = max.height = 64;
                addRule.x = placementRules.x = placementRulesLabel.x = -50000;//unless the screen's over 50k wide, this should be good

                int maximum = rule.ruleType == AbstractPlacementRule.RuleType.AXIAL ? 3 : 6;
                min.maximum = max.maximum = maximum;
                if (min.getValue() > maximum) min.setValue(maximum);
                if (max.getValue() > maximum) max.setValue(maximum);
                break;
            case VERTEX:
            case EDGE:
                setupBlocks();
                block.preferredHeight = 64;
                min.height = max.height = 0;
                addRule.x = placementRules.x = placementRulesLabel.x = -50000;//unless the screen's over 50k wide, this should be good
                break;
            case AND:
            case OR:
                block.preferredHeight = min.height = max.height = 0;
                addRule.x = placementRules.x = placementRulesLabel.x = sidebar.width;
                break;
        }
        placementRules.components.clear();
        for(AbstractPlacementRule<BlockType, Template> rul : rule.rules){
            placementRules.add(new MenuComponentPlacementRule(rul, ()->{//edit
                gui.open(new MenuPlacementRuleConfiguration(gui, this, configuration, rul, blockList, blockTypes));
            }, ()->{//delete
                rule.rules.remove(rul);
                refreshNeeded = true;
            }));
        }
        min.setValue(rule.min);
        max.setValue(rule.max);
    }
    private void setupBlocks() {
        for(BlockType type : blockTypes){
            block.add(new Label(0, 0, 0, 0, "Any "+type.getDisplayName()){
                @Override
                public void onMouseButton(double x, double y, int button, int action, int mods){
                    super.onMouseButton(x, y, button, action, mods);
                    if(button==0&&action==GLFW_PRESS){
                        isFocused = false;
                        block.isFocused = false;
                        MenuPlacementRuleConfiguration.this.focusedComponent = null;
                    }
                }
            });
        }
        for(Template b : blockList.allBlocks){
            block.add(new Label(0, 0, 0, 0, b.getDisplayName()){
                @Override
                public void drawText(Renderer renderer){
                    float textLength = renderer.getStringWidth(text, height);
                    float scale = Math.min(1, (width-height-textInset*2)/textLength);
                    float textHeight = (int)((height-textInset*2)*scale)-4;
                    renderer.drawText(x+height+textInset, y+height/2-textHeight/2, x+width-textInset, y+height/2+textHeight/2, text);
                    renderer.setWhite();
                    Image displayTexture = b.getDisplayTexture();
                    if(displayTexture!=null)renderer.drawImage(displayTexture, x, y, x+height, y+height);
                }
                @Override
                public void onMouseButton(double x, double y, int button, int action, int mods){
                    super.onMouseButton(x, y, button, action, mods);
                    if(button==0&&action==GLFW_PRESS){
                        isFocused = false;
                        block.isFocused = false;
                        MenuPlacementRuleConfiguration.this.focusedComponent = null;
                    }
                }
            });
        }

        if (rule.isSpecificBlock) {
            block.setSelectedIndex(rule.block == null ? 0 : blockTypes.length + blockList.allBlocks.indexOf(rule.block));
        } else {
            block.setSelectedIndex(rule.blockType == null ? 0 : rule.blockType.ordinal());
        }
    }

    private void setRuleBlock() {
        rule.rules.clear();

        int selectedIndex = block.getSelectedIndex();
        if (selectedIndex < blockTypes.length) {
            rule.isSpecificBlock = false;
            rule.block = null;
            rule.blockType = blockTypes[selectedIndex];
        } else {
            rule.isSpecificBlock = true;
            rule.block = blockList.allBlocks.get(selectedIndex - blockTypes.length);
            rule.blockType = null;
        }
    }

    @Override
    public void onClosed(){
        rule.ruleType = PlacementRule.RuleType.values()[type.getSelectedIndex()];
        switch(rule.ruleType) {
            case BETWEEN:
                setRuleBlock();
                rule.min = (byte) Math.min(min.getValue(), max.getValue());
                rule.max = (byte) Math.max(min.getValue(), max.getValue());
                break;
            case AXIAL:
                setRuleBlock();
                rule.min = (byte) Math.min(3, Math.min(min.getValue(), max.getValue()));
                rule.max = (byte) Math.min(3, Math.max(min.getValue(), max.getValue()));
                break;
            case VERTEX:
                setRuleBlock();
                break;
            case AND:
            case OR:
                rule.isSpecificBlock = false;
                rule.block = null;
                rule.blockType = null;
                break;
        }
    }
    @Override
    public void render2d(double deltaTime){
        if(refreshNeeded){
            onOpened();
            refreshNeeded = false;
        }
        type.width = block.width = placementRulesLabel.width = placementRules.width = addRule.width = gui.getWidth()-sidebar.width;
        min.width = max.width = block.width/2;
        max.x = min.x+min.width;
        block.y = type.y+type.height;
        min.y = max.y = block.y+block.height;
        placementRulesLabel.y = min.y+min.height;
        placementRules.y = placementRulesLabel.y+placementRulesLabel.height;
        addRule.y = gui.getHeight()-addRule.height;
        placementRules.height = addRule.y-placementRules.y;
        super.render2d(deltaTime);
    }
}
package planner.menu.configuration.underhaul.fissionsfr;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.Block;
import multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import planner.Core;
import planner.menu.component.MenuComponentDropdownList;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistSlider;
import planner.menu.configuration.ConfigurationMenu;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuPlacementRuleConfiguration extends ConfigurationMenu{
    private final PlacementRule rule;
    private final MenuComponentDropdownList type, block;
    private MenuComponentMinimalistSlider min, max;
    private final MenuComponentLabel placementRulesLabel;
    private final MenuComponentMinimaList placementRules;
    private final MenuComponentMinimalistButton addRule;
    private boolean refreshNeeded = false;
    public MenuPlacementRuleConfiguration(GUI gui, Menu parent, Configuration configuration, PlacementRule rule){
        super(gui, parent, configuration, "Placement Rule");
        type = add(new MenuComponentDropdownList(sidebar.width, 0, 0, 64));
        for(PlacementRule.RuleType ruleType : PlacementRule.RuleType.values()){
            type.add(new MenuComponentLabel(0, 0, 0, 0, ruleType.name){
                @Override
                public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                    super.onMouseButton(x, y, button, pressed, mods);
                    if(button==0&&pressed){
                        rule.ruleType = ruleType;
                        MenuPlacementRuleConfiguration.this.onGUIOpened();
                        isSelected = false;
                        type.isSelected = false;
                        MenuPlacementRuleConfiguration.this.selected = null;
                    }
                }
            });
        }
        block = add(new MenuComponentDropdownList(sidebar.width, type.height, 0, 64, true));
        min = add(new MenuComponentMinimalistSlider(sidebar.width, block.y+block.height, 0, 64, "Minimum", 0, 6, 1, true).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
        max = add(new MenuComponentMinimalistSlider(sidebar.width, block.y+block.height, 0, 64, "Maximum", 0, 6, 6, true).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
        placementRulesLabel = add(new MenuComponentLabel(sidebar.width, min.y+min.height, 0, 48, "Placement Rules", true));
        placementRules = add(new MenuComponentMinimaList(sidebar.width, placementRulesLabel.y+placementRulesLabel.height, 0, 0, 16));
        addRule = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "New Rule", true, true));
        addRule.addActionListener((e) -> {
            PlacementRule rul;
            rule.rules.add(rul = new PlacementRule());
            gui.open(new MenuPlacementRuleConfiguration(gui, parent, configuration, rul));
        });
        this.rule = rule;
    }
    @Override
    public void onGUIOpened(){
        type.setSelectedIndex(rule.ruleType.ordinal());
        block.clear();
        switch(rule.ruleType){
            case BETWEEN_GROUP:
            case AXIAL_GROUP:
                for(PlacementRule.BlockType type : PlacementRule.BlockType.values()){
                    block.add(new MenuComponentLabel(0, 0, 0, 0, type.name){
                        @Override
                        public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                            super.onMouseButton(x, y, button, pressed, mods);
                            if(button==0&&pressed){
                                isSelected = false;
                                block.isSelected = false;
                                MenuPlacementRuleConfiguration.this.selected = null;
                            }
                        }
                    });
                }
                block.setSelectedIndex(rule.blockType.ordinal());
                block.preferredHeight = min.height = max.height = 64;
                addRule.x = placementRules.x = placementRulesLabel.x = -50000;//unless the screen's over 50k wide, this should be good
                break;
            case VERTEX_GROUP:
                for(PlacementRule.BlockType type : PlacementRule.BlockType.values()){
                    block.add(new MenuComponentLabel(0, 0, 0, 0, type.name){
                        @Override
                        public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                            super.onMouseButton(x, y, button, pressed, mods);
                            if(button==0&&pressed){
                                isSelected = false;
                                block.isSelected = false;
                                MenuPlacementRuleConfiguration.this.selected = null;
                            }
                        }
                    });
                }
                block.setSelectedIndex(rule.blockType.ordinal());
                block.preferredHeight = 64;
                min.height = max.height = 0;
                addRule.x = placementRules.x = placementRulesLabel.x = -50000;//unless the screen's over 50k wide, this should be good
                break;
            case BETWEEN:
            case AXIAL:
                for(Block b : Core.configuration.underhaul.fissionSFR.allBlocks){
                    block.add(new MenuComponentLabel(0, 0, 0, 0, b.getDisplayName()){
                        @Override
                        public void drawText(){
                            double textLength = FontManager.getLengthForStringWithHeight(text, height);
                            double scale = Math.min(1, (width-height-textInset*2)/textLength);
                            double textHeight = (int)((height-textInset*2)*scale)-4;
                            drawText(x+height+textInset, y+height/2-textHeight/2, x+width-textInset, y+height/2+textHeight/2, text);
                            Core.applyWhite();
                            if(b.texture!=null)drawRect(x, y, x+height, y+height, Core.getTexture(b.displayTexture));
                        }
                        @Override
                        public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                            super.onMouseButton(x, y, button, pressed, mods);
                            if(button==0&&pressed){
                                isSelected = false;
                                block.isSelected = false;
                                MenuPlacementRuleConfiguration.this.selected = null;
                            }
                        }
                    });
                }
                block.setSelectedIndex(rule.block==null?0:Core.configuration.underhaul.fissionSFR.allBlocks.indexOf(rule.block));
                block.preferredHeight = min.height = max.height = 64;
                addRule.x = placementRules.x = placementRulesLabel.x = -50000;//unless the screen's over 50k wide, this should be good
                break;
            case VERTEX:
                for(Block b : Core.configuration.underhaul.fissionSFR.allBlocks){
                    block.add(new MenuComponentLabel(0, 0, 0, 0, b.getDisplayName()){
                        @Override
                        public void drawText(){
                            double textLength = FontManager.getLengthForStringWithHeight(text, height);
                            double scale = Math.min(1, (width-height-textInset*2)/textLength);
                            double textHeight = (int)((height-textInset*2)*scale)-4;
                            drawText(x+height+textInset, y+height/2-textHeight/2, x+width-textInset, y+height/2+textHeight/2, text);
                            Core.applyWhite();
                            if(b.texture!=null)drawRect(x, y, x+height, y+height, Core.getTexture(b.displayTexture));
                        }
                        @Override
                        public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                            super.onMouseButton(x, y, button, pressed, mods);
                            if(button==0&&pressed){
                                isSelected = false;
                                block.isSelected = false;
                                MenuPlacementRuleConfiguration.this.selected = null;
                            }
                        }
                    });
                }
                block.setSelectedIndex(rule.block==null?0:Core.configuration.underhaul.fissionSFR.allBlocks.indexOf(rule.block));
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
        for(AbstractPlacementRule<PlacementRule.BlockType, Block> rul : rule.rules){
            placementRules.add(new MenuComponentPlacementRule((PlacementRule) rul));
        }
        min.setValue(rule.min);
        max.setValue(rule.max);
    }
    @Override
    public void onGUIClosed(){
        rule.ruleType = PlacementRule.RuleType.values()[type.getSelectedIndex()];
        switch(rule.ruleType){
            case BETWEEN_GROUP:
                rule.blockType = PlacementRule.BlockType.values()[block.getSelectedIndex()];
                rule.min = (byte) Math.min(min.getValue(), max.getValue());
                rule.max = (byte) Math.max(min.getValue(), max.getValue());
                break;
            case AXIAL_GROUP:
                rule.blockType = PlacementRule.BlockType.values()[block.getSelectedIndex()];
                rule.min = (byte) Math.min(3, Math.min(min.getValue(), max.getValue()));
                rule.max = (byte) Math.min(3, Math.max(min.getValue(), max.getValue()));
                break;
            case VERTEX_GROUP:
                rule.blockType = PlacementRule.BlockType.values()[block.getSelectedIndex()];
                break;
            case BETWEEN:
                rule.block = Core.configuration.underhaul.fissionSFR.allBlocks.get(block.getSelectedIndex());
                rule.min = (byte) Math.min(min.getValue(), max.getValue());
                rule.max = (byte) Math.max(min.getValue(), max.getValue());
                break;
            case AXIAL:
                rule.block = Core.configuration.underhaul.fissionSFR.allBlocks.get(block.getSelectedIndex());
                rule.min = (byte) Math.min(3, Math.min(min.getValue(), max.getValue()));
                rule.max = (byte) Math.min(3, Math.max(min.getValue(), max.getValue()));
                break;
            case VERTEX:
                rule.block = Core.configuration.underhaul.fissionSFR.allBlocks.get(block.getSelectedIndex());
                break;
            case AND:
            case OR:
                break;
        }
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            onGUIOpened();
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        type.width = block.width = placementRulesLabel.width = placementRules.width = addRule.width = Core.helper.displayWidth()-sidebar.width;
        min.width = max.width = block.width/2;
        max.x = min.x+min.width;
        block.y = type.y+type.height;
        min.y = max.y = block.y+block.height;
        placementRulesLabel.y = min.y+min.height;
        placementRules.y = placementRulesLabel.y+placementRulesLabel.height;
        addRule.y = gui.helper.displayHeight()-addRule.height;
        placementRules.height = addRule.y-placementRules.y;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : placementRules.components){
            if(c instanceof MenuComponentPlacementRule){
                if(button==((MenuComponentPlacementRule) c).delete){
                    rule.rules.remove(((MenuComponentPlacementRule)c).rule);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentPlacementRule) c).edit){
                    gui.open(new MenuPlacementRuleConfiguration(gui, this, configuration, ((MenuComponentPlacementRule) c).rule));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}
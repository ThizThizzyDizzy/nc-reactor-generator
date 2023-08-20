package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.DropdownList;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.Slider;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import static org.lwjgl.glfw.GLFW.*;
public class MenuPlacementRuleConfiguration extends ConfigurationMenu{
    public MenuPlacementRuleConfiguration(Menu parent, NCPFConfigurationContainer configuration, NCPFConfiguration config, NCPFPlacementRule rule){
        super(parent, configuration, "Placement Rule", new SplitLayout(SplitLayout.Y_AXIS, 0).fitSize());
        ListLayout definition = add(new ListLayout(48));
        DropdownList type = definition.add(new DropdownList());
        for(NCPFPlacementRule.RuleType ruleType : NCPFPlacementRule.RuleType.values()){
            type.add(new Label(0, 0, 0, 0, ruleType.name()){
                @Override
                public void onMouseButton(double x, double y, int button, int action, int mods){
                    super.onMouseButton(x, y, button, action, mods);
                    if(button==0&&action==GLFW_PRESS){
                        rule.rule = ruleType;
                        refresh();
                        isFocused = false;
                        type.isFocused = false;
                        MenuPlacementRuleConfiguration.this.focusedComponent = null;
                    }
                }
            });
        }
        DropdownList target = definition.add(new DropdownList(true));
        definition.add(new Slider(sidebar.width, target.y+target.height, 0, 64, "Minimum", 0, 6, 1, true).onChangeAsInt((t) -> {
            rule.min = t;
        }).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
        definition.add(new Slider(sidebar.width, target.y+target.height, 0, 64, "Maximum", 0, 6, 6, true).onChangeAsInt((t) -> {
            rule.max = t;
        }).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
        BorderLayout rulesListContainer = add(new BorderLayout());
        SingleColumnList rulesList = rulesListContainer.add(new SingleColumnList(16), BorderLayout.CENTER);
        
        onOpen(() -> {
            rulesListContainer.components.clear();
            rulesList.components.clear();
            List<NCPFPlacementRule> rules = rule.rules;
            for(NCPFPlacementRule rul : rules){
                rulesList.add(new NCPFPlacementRuleComponent(rul).addButton("delete", "Delete Rule", () -> {
                    rules.remove(rul);
                    refresh();
                }).addButton("pencil", "Modify Rule", () -> {
                    gui.open(new MenuPlacementRuleConfiguration(this, configuration, config, rul));
                })).height = 48;
            }
            rulesListContainer.add(new Label("Placement Rules"), BorderLayout.TOP, 48);
            rulesListContainer.add(rulesList, BorderLayout.CENTER);
            rulesListContainer.add(new Button("Add Rule", true).addAction(() -> {
                NCPFPlacementRule rul;
                rules.add(rul = new NCPFPlacementRule());
                gui.open(new MenuPlacementRuleConfiguration(this, configuration, config, rul));
            }), BorderLayout.BOTTOM, 48);
        });
    }
}
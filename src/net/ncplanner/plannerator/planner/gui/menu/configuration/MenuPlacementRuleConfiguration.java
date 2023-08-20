package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.ncpf.NCPFModuleReference;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.LayoutPanel;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.Slider;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.LayeredLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickReference;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuSelect;
public class MenuPlacementRuleConfiguration extends ConfigurationMenu{
    public MenuPlacementRuleConfiguration(Menu parent, NCPFConfigurationContainer configuration, NCPFConfiguration config, NCPFPlacementRule rule){
        super(parent, configuration, "Placement Rule", new SplitLayout(SplitLayout.Y_AXIS, 0.5f).fitSize());
        ListLayout definition = add(new ListLayout());
        Button type = definition.add(new Button(Objects.toString(rule.rule), true).addAction(() -> {
            new MenuSelect<>(gui, this, Arrays.asList(NCPFPlacementRule.RuleType.values()), null, (typ)->{
                rule.rule = typ;
                refresh();
            }).open();
        }));
        onOpen(() -> {
            type.text = Objects.toString(rule.rule);
        });
        type.height = 48;
        LayoutPanel panel = definition.add(new LayoutPanel(new LayeredLayout()));
        GridLayout sliders = definition.add(new GridLayout(2, 1));
        onOpen(() -> {
            panel.components.get(0).components.clear();
            panel.height = 0;
            sliders.components.clear();
            if(rule.rule!=null&&!rule.rule.hasSubRules){
                NCPFElementReference reference = rule.target;
                NCPFElement target = reference==null?null:reference.target;
                if(reference instanceof NCPFModuleReference){
                    target = new NCPFElement(new NCPFModuleElement(((NCPFModuleReference)reference).module));
                }
                panel.add(new NCPFElementComponent(target).addIconButton("pencil", "Change Target", () -> {
                    new MenuPickReference(this, config, true, (ref)->{
                        rule.target = ref;
                        refresh();
                    }).open();
                }));
                panel.height = 96;
            }
            if(rule.rule!=null&&rule.rule.hasQuantity){
                sliders.height = 48;
                sliders.add(new Slider("Minimum", 0, 6, rule.min, true).onChangeAsInt((t) -> {
                    rule.min = t;
                }).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
                sliders.add(new Slider("Maximum", 0, 6, rule.max, true).onChangeAsInt((t) -> {
                    rule.max = t;
                }).setTooltip("For Axial, this is the number of axial pairs (not single blocks)"));
            }else sliders.height = 0;
        });
        BorderLayout rulesListContainer = add(new BorderLayout());
        SingleColumnList rulesList = rulesListContainer.add(new SingleColumnList(16), BorderLayout.CENTER);
        
        onOpen(() -> {
            rulesListContainer.components.clear();
            rulesList.components.clear();
            if(rule.rule!=null&&rule.rule.hasSubRules){
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
            }
        });
    }
}
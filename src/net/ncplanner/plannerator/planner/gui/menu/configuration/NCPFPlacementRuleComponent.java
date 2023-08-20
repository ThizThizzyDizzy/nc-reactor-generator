package net.ncplanner.plannerator.planner.gui.menu.configuration;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.LayoutPanel;
import net.ncplanner.plannerator.planner.gui.menu.component.Panel;
import net.ncplanner.plannerator.planner.gui.menu.component.TextDisplay;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.LayeredLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListButtonsLayout;
public class NCPFPlacementRuleComponent extends LayoutPanel{
    private final ListButtonsLayout buttons;
    public NCPFPlacementRuleComponent(NCPFPlacementRule rule){
        super(new LayeredLayout());
        add(new Panel().setBackgroundColor(Core.theme::getTextViewBackgroundColor));
        TextDisplay display = add(new TextDisplay().fitText());
        display.addText(rule.toTooltipString());
        buttons = add(new ListButtonsLayout());
    }
    public NCPFPlacementRuleComponent addButton(String icon, String tooltip, Runnable onClick){
        buttons.add(new Button("", true, true){
            @Override
            public void drawForeground(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
                renderer.drawElement(icon, x, y, width, height);
            }
        }.setTooltip(tooltip).addAction(onClick));
        return this;
    }
}
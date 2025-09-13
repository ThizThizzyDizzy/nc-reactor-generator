package net.ncplanner.plannerator.planner.gui.menu.dialog;
import net.ncplanner.plannerator.ncpf.NCPFElementStack;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
public class MenuModifyElementStack extends MenuModifyElementDefinition{
    public MenuModifyElementStack(GUI gui, Menu parent, NCPFElementStack stack, Runnable onConfirm, Runnable onCancel){
        super(gui, parent, stack.definition, onConfirm, onCancel);
        Component mainContent = content;
        BorderLayout layout = setContent(new BorderLayout());
        layout.add(mainContent, BorderLayout.CENTER);
        layout.add(new TextBox(0, 0, 0, 48, stack.amount+"", true, "Amount").setIntFilter().onChange((s)->{
            try{
                stack.amount = Integer.parseInt(s);
            }catch(NumberFormatException ex){}
        }), BorderLayout.BOTTOM);
        layout.width = 500;
        layout.height = 248;
    }
}

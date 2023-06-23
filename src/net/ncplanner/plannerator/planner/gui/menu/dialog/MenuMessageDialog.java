package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuMessageDialog extends MenuDialog{
    public MenuMessageDialog(String text){
        this(Core.gui, Core.gui.menu, text);
    }
    private int asyncResult = -1;
    public MenuMessageDialog(GUI gui, Menu parent, String text){
        super(gui, parent);
        textBox.setText(text);
    }
    @Override
    public MenuMessageDialog addButton(String text, Runnable onClick){
        return (MenuMessageDialog)super.addButton(text, onClick);
    }
    @Override
    public MenuMessageDialog addButton(String text, Runnable onClick, boolean closeOnClick){
        return (MenuMessageDialog)super.addButton(text, onClick, closeOnClick);
    }
    @Override
    public MenuMessageDialog addButton(String text){
        return (MenuMessageDialog)super.addButton(text);
    }
    @Override
    public MenuMessageDialog addButton(String text, boolean closeOnClick){
        return (MenuMessageDialog)super.addButton(text, closeOnClick);
    }
    public MenuMessageDialog addButton(String text, Consumer<MenuMessageDialog> onClick){
        return addButton(text, onClick, false);
    }
    public MenuMessageDialog addButton(String text, Consumer<MenuMessageDialog> onClick, boolean closeOnClick){
        super.addButton(text, () -> {
            if(closeOnClick)close();
            if(onClick!=null)onClick.accept(this);
        });
        return this;
    }
    public int openAsync(){
        for(int i = 0; i<buttons.size(); i++){
            int result = i;
            Button b = buttons.get(i);
            b.addPriorityAction(() -> {
                asyncResult = result;
            });
        }
        open();
        while(gui.menu==this){
            try{Thread.sleep(10);}catch(InterruptedException ex){}
        }
        return asyncResult;
    }
}
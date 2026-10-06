package net.ncplanner.plannerator.planner.gui.menu.dialog;
import com.thizthizzydizzy.dizzyengine.ui.component.Button;
import java.util.function.Consumer;
public class MenuMessageDialog extends MenuDialog{
    private int asyncResult = -1;
    public MenuMessageDialog(String text){
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
        while(getUIContext().menu==this){
            try{Thread.sleep(10);}catch(InterruptedException ex){}
        }
        return asyncResult;
    }
}
package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class MenuInputDialog extends MenuDialog{
    public final TextBox inputField;
    public MenuInputDialog(GUI gui, Menu parent, String text, String title){
        super(gui, parent);
        inputField = new TextBox(0, 0, 384, 64, text, true, title);
        setContent(inputField);
    }
    @Override
    public MenuInputDialog addButton(String text){
        return (MenuInputDialog)super.addButton(text);
    }
    @Override
    public MenuInputDialog addButton(String text, boolean closeOnClick){
        return (MenuInputDialog)super.addButton(text, closeOnClick);
    }
    @Override
    public MenuInputDialog addButton(String text, Runnable onClick){
        return (MenuInputDialog)super.addButton(text, onClick);
    }
    @Override
    public MenuInputDialog addButton(String text, Runnable onClick, boolean closeOnClick){
        return (MenuInputDialog)super.addButton(text, onClick, closeOnClick);
    }
    public MenuInputDialog addButton(String text, Consumer<String> onClick){
        return addButton(text, onClick, false);
    }
    public MenuInputDialog addButton(String text, Consumer<String> onClick, boolean closeOnClick){
        super.addButton(text, () -> {
            if(closeOnClick)close();
            if(onClick!=null)onClick.accept(inputField.text);
        });
        return this;
    }
    public MenuInputDialog addButton(String text, BiConsumer<MenuInputDialog, String> onClick){
        return addButton(text, onClick, false);
    }
    public MenuInputDialog addButton(String text, BiConsumer<MenuInputDialog, String> onClick, boolean closeOnClick){
        super.addButton(text, () -> {
            if(closeOnClick)close();
            if(onClick!=null)onClick.accept(this, inputField.text);
        });
        return this;
    }
}
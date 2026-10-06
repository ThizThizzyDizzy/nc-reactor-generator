package net.ncplanner.plannerator.planner.gui.menu.dialog;
import com.thizthizzydizzy.dizzyengine.DizzyEngine;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import com.thizthizzydizzy.dizzyengine.ui.FlatUI;
import com.thizthizzydizzy.dizzyengine.ui.Menu;
import com.thizthizzydizzy.dizzyengine.ui.component.Button;
import com.thizthizzydizzy.dizzyengine.ui.component.Component;
import com.thizthizzydizzy.dizzyengine.ui.component.Label;
import com.thizthizzydizzy.dizzyengine.ui.component.Scrollable;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.TextDisplay;
import net.ncplanner.plannerator.planner.ui.component.layer.SecondaryComponentBackgroundLayer;
public class MenuDialog extends Menu{
    private Scrollable textPanel = add(new Scrollable(16, 16));
    public TextDisplay textBox = textPanel.add(new TextDisplay(""));
    private Label title;
    public Component content = textBox;
    public float maxWidth = 0.5f;
    public float maxHeight = 0.5f;
    public float minWidth = 0.25f;
    public float minHeight = 0.1f;
    public int border = 8;
    public int buttonHeight = 64;
    public int titleHeight = 48;
    public ArrayList<Button> buttons = new ArrayList<>();
    private boolean isClosed;
    public float scrollBarWidth = 0;
    public Menu parentMenu;
    @Override
    public void draw(double deltaTime){
        FlatUI ui = DizzyEngine.getLayer(FlatUI.class);
        if(isClosed&&ui.menu==this){
            ui.menu = parentMenu;
            closeListeners.forEach(Runnable::run);
        }
        try{
            if(parentMenu!=null)parentMenu.draw(deltaTime);
        }catch(Exception ignored){
        }
        Renderer.setColor(Core.theme.getDialogBorderColor());
        scrollBarWidth = Math.max(scrollBarWidth, textPanel.vertScrollbarSize*(textPanel.allowVerticalScrolling?1:0));
        float w = Math.max(getWidth()*minWidth, Math.min(getWidth()*maxWidth, content.getWidth()+scrollBarWidth));
        float h = Math.max(getHeight()*minHeight, Math.min(getHeight()*maxHeight, content.getHeight()+scrollBarWidth));
        Renderer.fillRect(getWidth()/2-w/2-border, getHeight()/2-h/2-border-(title==null?0:titleHeight), getWidth()/2+w/2+border, getHeight()/2+h/2+border+buttonHeight);
        Renderer.setColor(Core.theme.getDialogBackgroundColor());
        Renderer.fillRect(getWidth()/2-w/2, getHeight()/2-h/2, getWidth()/2+w/2, getHeight()/2+h/2);
        Renderer.setColor(Color.WHITE);

        textPanel.x = getWidth()/2-w/2;
        textPanel.y = getHeight()/2-h/2;
        textPanel.setSize(w, h);
        for(int i = 0; i<buttons.size(); i++){
            buttons.get(i).setWidth(w/buttons.size());
            buttons.get(i).x = getWidth()/2-w/2+buttons.get(i).getWidth()*i;
            buttons.get(i).y = getHeight()/2+h/2;
        }
        if(title!=null){
            title.setWidth(w);
            title.x = getWidth()/2-w/2;
            title.y = getHeight()/2-h/2-titleHeight;
        }
        super.draw(deltaTime);
    }
    public void close(){
        FlatUI ui = DizzyEngine.getLayer(FlatUI.class);
        if(ui.menu==this){
            ui.menu = parentMenu;
            closeListeners.forEach(Runnable::run);
        }
        isClosed = true;
    }
    public void open(){
        FlatUI ui = DizzyEngine.getLayer(FlatUI.class);
        parentMenu = ui.menu;
        setSize(ui.size);
        ui.menu = this;
        onMenuOpened();
//        content.focus();
    }
    protected final ArrayList<Runnable> closeListeners = new ArrayList<>();
    public MenuDialog onClose(Runnable action){
        closeListeners.add(action);
        return this;
    }
    public MenuDialog addButton(String text, Runnable onClick){
        return addButton(text, onClick, false);
    }
    public MenuDialog addButton(String text, Runnable onClick, boolean closeOnClick){
        Button b = new Button(text, true);
        b.background = new SecondaryComponentBackgroundLayer();
        b.addAction(() -> {
            if(closeOnClick)close();
            if(onClick!=null)onClick.run();
        });
        buttons.add(add(b));
        return this;
    }
    public MenuDialog addButton(String text){
        return addButton(text, null, true);
    }
    public MenuDialog addButton(String text, boolean closeOnClick){
        return addButton(text, null, closeOnClick);
    }
    public MenuDialog setTitle(String text){
        if(title==null){
            title = add(new Label(text));
            title.background = new SecondaryComponentBackgroundLayer();
        }
        else
            title.label.setLabel(text);
        title.setHeight(titleHeight);
        return this;
    }
    public <T extends Component> T setContent(T component){
        textPanel.components.remove(content);
        content = textPanel.add(component);
        return component;
    }
}

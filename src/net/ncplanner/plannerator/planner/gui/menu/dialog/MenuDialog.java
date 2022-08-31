package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.Scrollable;
import net.ncplanner.plannerator.planner.gui.menu.component.TextDisplay;
public class MenuDialog extends Menu{
    private Scrollable textPanel = add(new Scrollable(0, 0, 0, 0, 16, 16));
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
    public MenuDialog(GUI gui, Menu parent){
        super(gui, parent);
    }
    float scrollBarWidth = 0;
    @Override
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        try{
            if(parent!=null)parent.render2d(deltaTime);
        }catch(Exception ignored){}
        renderer.setColor(Core.theme.getDialogBorderColor());
        scrollBarWidth = Math.max(scrollBarWidth, textPanel.vertScrollbarWidth*(textPanel.vertScrollbarPresent?1:0));
        float w = Math.max(gui.getWidth()*minWidth, Math.min(gui.getWidth()*maxWidth, content.width+scrollBarWidth));
        float h = Math.max(gui.getHeight()*minHeight, Math.min(gui.getHeight()*maxHeight, content.height+scrollBarWidth));
        renderer.fillRect(gui.getWidth()/2-w/2-border, gui.getHeight()/2-h/2-border-(title==null?0:titleHeight), gui.getWidth()/2+w/2+border, gui.getHeight()/2+h/2+border+buttonHeight);
        renderer.setColor(Core.theme.getDialogBackgroundColor());
        renderer.fillRect(gui.getWidth()/2-w/2, gui.getHeight()/2-h/2, gui.getWidth()/2+w/2, gui.getHeight()/2+h/2);
        renderer.setWhite();
        textPanel.x = gui.getWidth()/2-w/2;
        textPanel.y = gui.getHeight()/2-h/2;
        textPanel.width = w;
        textPanel.height = h;
        for(int i = 0; i<buttons.size(); i++){
            buttons.get(i).width = w/buttons.size();
            buttons.get(i).x = gui.getWidth()/2-w/2+buttons.get(i).width*i;
            buttons.get(i).y = gui.getHeight()/2+h/2;
        }
        if(title!=null){
            title.width = w;
            title.x = gui.getWidth()/2-w/2;
            title.y = gui.getHeight()/2-h/2-titleHeight;
        }
        super.render2d(deltaTime);
    }
    @Override
    public void render3d(double deltaTime){
        try{
            if(parent!=null)parent.render3d(deltaTime);
        }catch(Exception ignored){}
        super.render3d(deltaTime);
    }
    public void close(){
        gui.menu = parent;
        closeListeners.forEach(Runnable::run);
    }
    public void open(){
        gui.menu = this;
        onOpened();
    }
    private final ArrayList<Runnable> closeListeners = new ArrayList<>();
    public MenuDialog onClose(Runnable action){
        closeListeners.add(action);
        return this;
    }
    public MenuDialog addButton(String text, Runnable onClick){
        return addButton(text, onClick, false);
    }
    public MenuDialog addButton(String text, Runnable onClick, boolean closeOnClick){
        Button b = new Button(0, 0, 0, buttonHeight, text, true, true);
        b.addAction(() -> {
            if(closeOnClick)close();
            if(onClick!=null)onClick.run();
        });
        buttons.add(add(b));
        return this;
    }
    public MenuDialog addButton(String text){
        return addButton(text, null, false);
    }
    public MenuDialog addButton(String text, boolean closeOnClick){
        return addButton(text, null, closeOnClick);
    }
    public MenuDialog setTitle(String text){
        if(title==null)title = add(new Label(0, 0, 0, titleHeight, text, true));
        else title.text = text;
        return this;
    }
    public <T extends Component> T setContent(T component){
        textPanel.components.remove(content);
        content = textPanel.add(component);
        return component;
    }
}
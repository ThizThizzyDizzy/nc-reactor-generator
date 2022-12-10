package net.ncplanner.plannerator.planner.gui.menu.dssl;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import org.lwjgl.glfw.GLFW;
public class EditorTabComponent extends Label{
    public final EditorTab tab;
    public Runnable onClick = null;
    public EditorTabComponent(EditorTab tab){
        super(0, 0, 128, 32, tab.getName());
        this.tab = tab;
    }
    @Override
    public void render2d(double deltaTime) {
        text = tab.getName();
        darker = isFocused;
        Renderer r = new Renderer();
        width = r.getStringWidth(text, height-textInset*2)+textInset*2;
        super.render2d(deltaTime);
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods) {
        super.onMouseButton(x, y, button, action, mods);
        if(onClick!=null&&action==GLFW.GLFW_PRESS&&button==GLFW.GLFW_MOUSE_BUTTON_LEFT)onClick.run();
    }
    public EditorTabComponent onClick(Runnable r){
        onClick = r;
        return this;
    }
}
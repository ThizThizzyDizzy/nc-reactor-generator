package net.ncplanner.plannerator.planner.gui;
import net.ncplanner.plannerator.planner.gui.menu.MenuCalibrateCursor;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWDropCallback;
public abstract class GUI{
    private final long window;
    public GUI(long window){
        this.window = window;
    }
    public Menu menu;
    public double mouseX, mouseY;
    public synchronized void render2d(double deltaTime){
        menu.width = getWidth();//TODO special onResize event for this
        menu.height = getHeight();
        menu.render2d(deltaTime);
    }
    public synchronized void render3d(double deltaTime){
        menu.render3d(deltaTime);
    }
    public void initInput(){
        glfwSetCharCallback(window, (window, codepoint) -> {
            onChar(codepoint);
        });
        glfwSetCharModsCallback(window, (window, codepoint, mods) -> {
            onCharMods(codepoint, mods);
        });
        glfwSetCursorEnterCallback(window, (window, entered) -> {
            onCursorEnter(entered);
        });
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            if(xpos<0&&ypos<0)onCursorPos(xpos, ypos);
            else onCursorPos(xpos*MenuCalibrateCursor.xMult+MenuCalibrateCursor.xOff, ypos*MenuCalibrateCursor.yMult+MenuCalibrateCursor.yOff);
        });
        glfwSetDropCallback(window, (window, count, names) -> {
            onDrop(count, names);
        });
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            onKey(key, scancode, action, mods);
        });
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            onMouseButton(button, action, mods);
        });
        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            onScroll(xoffset, yoffset);
        });
        glfwSetWindowCloseCallback(window, (window) -> {
            onWindowClosed();
        });
        glfwSetWindowFocusCallback(window, (window, focused) -> {
            onWindowFocus(focused);
        });
        glfwSetWindowIconifyCallback(window, (window, iconified) -> {
            onWindowIconify(iconified);
        });
        glfwSetWindowMaximizeCallback(window, (window, maximized) -> {
            onWindowMaximize(maximized);
        });
        glfwSetWindowPosCallback(window, (window, xpos, ypos) -> {
            onWindowPos(xpos, ypos);
        });
    }
    public <T extends Menu> T open(T menu){
        if(this.menu!=null)this.menu.onClosed();
        this.menu = menu;
        if(menu!=null){
            menu.width = getWidth();
            menu.height = getHeight();
            menu.onOpened();
        }
        return menu;
    }
    public abstract int getWidth();
    public abstract int getHeight();
    private synchronized void onChar(int codepoint){
        menu.onCharTyped((char)codepoint);
    }
    private synchronized void onCharMods(int codepoint, int mods){
        menu.onCharTypedWithModifiers((char)codepoint, mods);
    }
    private synchronized void onCursorEnter(boolean entered){
        if(entered)menu.onCursorEnteredWindow();
        else{
            menu.onCursorExitedWindow();
            mouseX = mouseY = -1;
        }
    }
    private synchronized void onCursorPos(double xpos, double ypos){
        mouseX = xpos;
        mouseY = ypos;
        menu.onCursorMoved(xpos, ypos);
    }
    private synchronized void onDrop(int count, long names){
        String[] files = new String[count];
        for(int i = 0; i<files.length; i++){
            files[i] = GLFWDropCallback.getName(names, i);
        }
        menu.onFilesDropped(files);
    }
    private synchronized void onKey(int key, int scancode, int action, int mods){
        menu.onKeyEvent(key, scancode, action, mods);
    }
    private synchronized void onMouseButton(int button, int action, int mods){
        menu.onCursorMoved(mouseX, mouseY);
        menu.onMouseButton(mouseX, mouseY, button, action, mods);
    }
    private synchronized void onScroll(double xoffset, double yoffset){
        menu.onScroll(xoffset, yoffset);
        menu.onCursorMoved(mouseX, mouseY);
    }
    private synchronized void onWindowClosed(){
        menu.onWindowClosed();
    }
    private synchronized void onWindowFocus(boolean focused){
        if(focused)menu.onWindowFocusGained();
        else menu.onWindowFocusLost();
    }
    private synchronized void onWindowIconify(boolean iconified){
        if(iconified)menu.onWindowIconified();
        else menu.onWindowUniconified();
    }
    private synchronized void onWindowMaximize(boolean maximized){
        if(maximized)menu.onWindowMaximized();
        else menu.onWindowUnmaximized();
    }
    private synchronized void onWindowPos(int xpos, int ypos){
        menu.onWindowMoved(xpos, ypos);
    }
}
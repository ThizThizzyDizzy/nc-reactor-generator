package net.ncplanner.plannerator.planner.gui.menu;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.ui.FlatUI;
import com.thizthizzydizzy.dizzyengine.ui.Menu;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
public class MenuCalibrateCursor extends Menu{
    public static double xMult = 1;
    public static double yMult = 1;
    public static double xGUIScale = 1;
    public static double yGUIScale = 1;
    public static int xOff = 0;
    public static int yOff = 0;
    public static boolean calibrationChanged = false;
    private Menu parentMenu;
    @Override
    public void open(){
        parentMenu = getUIContext().menu;
        super.open();
    }
    @Override
    public void onKey(int id, int key, int scancode, int action, int mods){
        if(action==GLFW.GLFW_PRESS&&key==GLFW.GLFW_KEY_ESCAPE){
            parentMenu.open();
            onClose();
        }
        if(action==GLFW.GLFW_PRESS||action==GLFW.GLFW_REPEAT){
            if(key==GLFW.GLFW_KEY_W||key==GLFW.GLFW_KEY_UP){
                switch(selected){
                    case 0:
                        if(Core.isControlPressed())xMult+=0.1;
                        else xMult*=2;
                        break;
                    case 1:
                        if(Core.isControlPressed())yMult+=0.1;
                        else yMult*=2;
                        break;
                    case 2:
                        xOff+=Core.isControlPressed()?1:10;
                        break;
                    case 3:
                        yOff+=Core.isControlPressed()?1:10;
                        break;
                }
                calibrationChanged = true;
            }
            if(key==GLFW.GLFW_KEY_S||key==GLFW.GLFW_KEY_DOWN){
                switch(selected){
                    case 0:
                        if(Core.isControlPressed())xMult = Math.max(1/1024d, xMult-0.1);
                        else xMult/=2;
                        break;
                    case 1:
                        if(Core.isControlPressed())yMult = Math.max(1/1024d, yMult-0.1);
                        else yMult/=2;
                        break;
                    case 2:
                        xOff-=Core.isControlPressed()?1:10;
                        break;
                    case 3:
                        yOff-=Core.isControlPressed()?1:10;
                        break;
                }
                calibrationChanged = true;
            }
            if(action==GLFW.GLFW_PRESS){
                if(key==GLFW.GLFW_KEY_DELETE){
                    if(Core.isShiftPressed())xGUIScale = yGUIScale = 1;
                    xMult = yMult = 1;
                    xOff = yOff = 0;
                    calibrationChanged = true;
                }
                if(key==GLFW.GLFW_KEY_A||key==GLFW.GLFW_KEY_LEFT){
                    selected--;
                    if(selected<0)selected = 0;
                }
                if(key==GLFW.GLFW_KEY_D||key==GLFW.GLFW_KEY_RIGHT){
                    selected++;
                    if(selected>3)selected = 3;
                }
            }
        }
        super.onKey(id, key, scancode, action, mods);
    }
    int selected = 0;
    int auto = 0;
    double[][] calibMouse = new double[2][2];
    int[][] calibScreen = new int[2][2];
    @Override
    public void onMouseButton(int id, Vector2d pos, int button, int action, int mods){
        super.onMouseButton(id, pos, button, action, mods);
        double x = (pos.x-xOff)/xMult;
        double y = (pos.y-yOff)/yMult;
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&action==GLFW.GLFW_PRESS){
            parentMenu.open();
            onClose();
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&action==GLFW.GLFW_PRESS){
            calibMouse[auto][0] = x;
            calibMouse[auto][1] = y;
            switch(auto){
                case 0:
                    calibScreen[0][0] = (int)(getWidth()/4);
                    calibScreen[0][1] = (int)(getHeight()/4);
                    break;
                case 1:
                    calibScreen[1][0] = (int)(getWidth()*3/4);
                    calibScreen[1][1] = (int)(getHeight()*3/4);
                    break;
            }
            auto++;
            if(auto==2){
                calibrate();
                auto = 0;
            }
        }
    }
    public void calibrate(){
        calibrationChanged = true;
        xMult = Math.max(1/1024d,multiplify(calibMult(calibMouse[0][0], calibMouse[1][0], calibScreen[0][0], calibScreen[1][0])));
        yMult = Math.max(1/1024d,multiplify(calibMult(calibMouse[0][1], calibMouse[1][1], calibScreen[0][1], calibScreen[1][1])));
        xOff = offify(calibOff(calibMouse[0][0], calibMouse[1][0], calibScreen[0][0], calibScreen[1][0]));
        yOff = offify(calibOff(calibMouse[0][1], calibMouse[1][1], calibScreen[0][1], calibScreen[1][1]));
    }
    public double multiplify(double d){
        if(d<=0)return 0;
        if(d<1)return 1/multiplify(1/d);
        return Math.round(d);
    }
    public int offify(double d){
        return (int)(Math.round(d/10)*10);
    }
    public double calibMult(double s1, double m1, double s2, double m2){
        return (s2-m2)/(s1-m1);
    }
    public double calibOff(double s1, double m1, double s2, double m2){
        return (-m1*s2+m1*m2)/(s1-m1)+m2;
    }
    @Override
    public void draw(double deltaTime){
        super.draw(deltaTime);
        Vector2d mousePos = ((FlatUI)getUIContext()).cursorPosition[0];
        double x = (mousePos.x-xOff)/xMult;
        double y = (mousePos.y-yOff)/yMult;
        Renderer.setColor(Core.theme.getRecoveryModeTextColor());
        Renderer.drawCenteredText(0, 0, getWidth(), getHeight()/16, "CURSOR CALIBRATION");
        Renderer.drawCenteredText(0, getHeight()/16, getWidth(), getHeight()*4/32, "Press escape or right click to exit.");
        Renderer.drawCenteredText(0, getHeight()*4/32, getWidth(), getHeight()*5/32, "This is for correcting a cursor offset issue on macOS");
        Renderer.drawCenteredText(0, getHeight()*5/32, getWidth(), getHeight()*6/32, "To auto-calibrate, click the squares that appear onscreen.");
        Renderer.drawCenteredText(0, getHeight()*6/32, getWidth(), getHeight()*7/32, "Use arrow keys or WASD to manually adjust calibration (Press control for granular adjustment)");
        Renderer.drawCenteredText(0, getHeight()*7/32, getWidth(), getHeight()*8/32, "Press Delete to reset calibration");
        Renderer.drawCenteredText(0, getHeight()*15/32, getWidth(), getHeight()*17/32, get(selected));
        Renderer.drawCenteredText(0, getHeight()*14/32, getWidth(), getHeight()*15/32, "If you have any idea what actually causes the cursor offset, please let me know");
        Renderer.setColor(Core.theme.getConvertButtonTextColor());
        Renderer.fillRect((float)mousePos.x-20, (float)mousePos.y-20, (float)mousePos.x-5, (float)mousePos.y-16);
        Renderer.fillRect((float)mousePos.x-20, (float)mousePos.y-20, (float)mousePos.x-16, (float)mousePos.y-5);
        Renderer.fillRect((float)mousePos.x+5, (float)mousePos.y-20, (float)mousePos.x+20, (float)mousePos.y-16);
        Renderer.fillRect((float)mousePos.x+16, (float)mousePos.y-20, (float)mousePos.x+20, (float)mousePos.y-5);
        Renderer.fillRect((float)mousePos.x-20, (float)mousePos.y+5, (float)mousePos.x-16, (float)mousePos.y+20);
        Renderer.fillRect((float)mousePos.x-20, (float)mousePos.y+16, (float)mousePos.x-5, (float)mousePos.y+20);
        Renderer.fillRect((float)mousePos.x+5, (float)mousePos.y+16, (float)mousePos.x+20, (float)mousePos.y+20);
        Renderer.fillRect((float)mousePos.x+16, (float)mousePos.y+5, (float)mousePos.x+20, (float)mousePos.y+20);
        Renderer.drawCenteredText(0, getHeight()*15/16, getWidth(), getHeight(), "("+(int)x+", "+(int)y+")");
        switch(auto){
            case 0:
                Renderer.fillRect(getWidth()/4-16, getHeight()/4-16, getWidth()/4+16, getHeight()/4+16);
                Renderer.setColor(Core.theme.getComponentTextColor(0));
                Renderer.drawElement("delete", getWidth()/4-16, getHeight()/4-16, 32, 32);
                break;
            case 1:
                Renderer.fillRect(getWidth()*3/4-16, getHeight()*3/4-16, getWidth()*3/4+16, getHeight()*3/4+16);
                Renderer.setColor(Core.theme.getComponentTextColor(0));
                Renderer.drawElement("delete", getWidth()*3/4-16, getHeight()*3/4-16, 32, 32);
                break;
        }
    }
    private String get(int selected){
        switch(selected){
            case 0:
                return "X Multiplier: "+xMult+"x >";
            case 1:
                return "< Y Multiplier: "+yMult+"x >";
            case 2:
                return "< X Offset: "+xOff+" >";
            case 3:
                return "< Y Offset: "+yOff+" ";
            default:
                return "Something has gone horribly wrong!";
        }
    }
    private void onClose() {
        if(calibrationChanged&&xMult!=xGUIScale&&yMult!=yGUIScale){
            new MenuMessageDialog("Calibration changed! Would you like to adjust GUI scale to match?").addButton("Yes", () -> {
                xGUIScale = xMult;
                yGUIScale = yMult;
            }, true).addButton("No", true).open();
        }
    }
}
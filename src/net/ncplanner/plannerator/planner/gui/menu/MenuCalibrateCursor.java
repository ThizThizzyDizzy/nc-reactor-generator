package net.ncplanner.plannerator.planner.gui.menu;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
import org.lwjgl.glfw.GLFW;
public class MenuCalibrateCursor extends Menu{
    public static double xMult = 1;
    public static double yMult = 1;
    public static double xGUIScale = 1;
    public static double yGUIScale = 1;
    public static int xOff = 0;
    public static int yOff = 0;
    public static boolean calibrationChanged = false;
    public MenuCalibrateCursor(GUI gui, Menu parent){
        super(gui, parent);
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        if(action==GLFW.GLFW_PRESS&&key==GLFW.GLFW_KEY_ESCAPE){
            gui.open(parent);
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
        super.onKeyEvent(key, scancode, action, mods);
    }
    int selected = 0;
    int auto = 0;
    double[][] calibMouse = new double[2][2];
    int[][] calibScreen = new int[2][2];
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        x = (x-xOff)/xMult;
        y = (y-yOff)/yMult;
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&action==GLFW.GLFW_PRESS){
            gui.open(parent);
            onClose();
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&action==GLFW.GLFW_PRESS){
            calibMouse[auto][0] = x;
            calibMouse[auto][1] = y;
            switch(auto){
                case 0:
                    calibScreen[0][0] = gui.getWidth()/4;
                    calibScreen[0][1] = gui.getHeight()/4;
                    break;
                case 1:
                    calibScreen[1][0] = gui.getWidth()*3/4;
                    calibScreen[1][1] = gui.getHeight()*3/4;
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
    public void render2d(double deltaTime){
        super.render2d(deltaTime);
        double x = gui.mouseX;
        double y= gui.mouseY;
        x = (x-xOff)/xMult;
        y = (y-yOff)/yMult;
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getRecoveryModeTextColor());
        renderer.drawCenteredText(0, 0, gui.getWidth(), gui.getHeight()/16, "CURSOR CALIBRATION");
        renderer.drawCenteredText(0, gui.getHeight()/16, gui.getWidth(), gui.getHeight()*4/32, "Press escape or right click to exit.");
        renderer.drawCenteredText(0, gui.getHeight()*4/32, gui.getWidth(), gui.getHeight()*5/32, "This is for correcting a cursor offset issue on macOS");
        renderer.drawCenteredText(0, gui.getHeight()*5/32, gui.getWidth(), gui.getHeight()*6/32, "To auto-calibrate, click the squares that appear onscreen.");
        renderer.drawCenteredText(0, gui.getHeight()*6/32, gui.getWidth(), gui.getHeight()*7/32, "Use arrow keys or WASD to manually adjust calibration (Press control for granular adjustment)");
        renderer.drawCenteredText(0, gui.getHeight()*7/32, gui.getWidth(), gui.getHeight()*8/32, "Press Delete to reset calibration");
        renderer.drawCenteredText(0, gui.getHeight()*15/32, gui.getWidth(), gui.getHeight()*17/32, get(selected));
        renderer.drawCenteredText(0, gui.getHeight()*14/32, gui.getWidth(), gui.getHeight()*15/32, "If you have any idea what actually causes the cursor offset, please let me know");
        renderer.setColor(Core.theme.getConvertButtonTextColor());
        renderer.fillRect((float)gui.mouseX-20, (float)gui.mouseY-20, (float)gui.mouseX-5, (float)gui.mouseY-16);
        renderer.fillRect((float)gui.mouseX-20, (float)gui.mouseY-20, (float)gui.mouseX-16, (float)gui.mouseY-5);
        renderer.fillRect((float)gui.mouseX+5, (float)gui.mouseY-20, (float)gui.mouseX+20, (float)gui.mouseY-16);
        renderer.fillRect((float)gui.mouseX+16, (float)gui.mouseY-20, (float)gui.mouseX+20, (float)gui.mouseY-5);
        renderer.fillRect((float)gui.mouseX-20, (float)gui.mouseY+5, (float)gui.mouseX-16, (float)gui.mouseY+20);
        renderer.fillRect((float)gui.mouseX-20, (float)gui.mouseY+16, (float)gui.mouseX-5, (float)gui.mouseY+20);
        renderer.fillRect((float)gui.mouseX+5, (float)gui.mouseY+16, (float)gui.mouseX+20, (float)gui.mouseY+20);
        renderer.fillRect((float)gui.mouseX+16, (float)gui.mouseY+5, (float)gui.mouseX+20, (float)gui.mouseY+20);
        renderer.drawCenteredText(0, gui.getHeight()*15/16, gui.getWidth(), gui.getHeight(), "("+(int)x+", "+(int)y+")");
        switch(auto){
            case 0:
                renderer.fillRect(gui.getWidth()/4-16, gui.getHeight()/4-16, gui.getWidth()/4+16, gui.getHeight()/4+16);
                renderer.setColor(Core.theme.getComponentTextColor(0));
                renderer.drawElement("delete", gui.getWidth()/4-16, gui.getHeight()/4-16, 32, 32);
                break;
            case 1:
                renderer.fillRect(gui.getWidth()*3/4-16, gui.getHeight()*3/4-16, gui.getWidth()*3/4+16, gui.getHeight()*3/4+16);
                renderer.setColor(Core.theme.getComponentTextColor(0));
                renderer.drawElement("delete", gui.getWidth()*3/4-16, gui.getHeight()*3/4-16, 32, 32);
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
            new MenuMessageDialog(gui, gui.menu, "Calibration changed! Would you like to adjust GUI scale to match?").addButton("Yes", () -> {
                xGUIScale = xMult;
                yGUIScale = yMult;
            }, true).addButton("No", true).open();
        }
    }
}
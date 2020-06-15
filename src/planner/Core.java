package planner;
import planner.multiblock.UnderhaulSFR;
import planner.menu.MenuMain;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.configuration.Configuration;
import planner.multiblock.Multiblock;
import simplelibrary.Sys;
import simplelibrary.error.ErrorAdapter;
import simplelibrary.error.ErrorCategory;
import simplelibrary.font.FontManager;
import simplelibrary.game.GameHelper;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.texture.TexturePack;
import simplelibrary.texture.TexturePackManager;
public class Core extends Renderer2D{
    public static GUI gui;
    public static GameHelper helper;
    public static ArrayList<Long> FPStracker = new ArrayList<>();
    public static boolean debugMode = false;
    public static final boolean is3D = true;
    public static boolean enableCullFace = true;
    public static final boolean fullscreen = false;
    public static final boolean supportTyping = true;
    public static final ArrayList<Multiblock> multiblocks = new ArrayList<>();
    public static final ArrayList<Multiblock> multiblockTypes = new ArrayList<>();
    public static HashMap<String, String> metadata = new HashMap<>();
    public static Configuration configuration = new Configuration("Temporary", "-1");
    static{
        multiblockTypes.add(new UnderhaulSFR());
        resetMetadata();
    }
    public static void resetMetadata(){
        metadata.clear();
        metadata.put("Name", "");
        metadata.put("Author", "");
    }
    public static void main(String[] args) throws NoSuchMethodException{
        helper = new GameHelper();
        helper.setBackground(new Color(40,50,100));
        helper.setDisplaySize(800, 600);
        helper.setRenderInitMethod(Core.class.getDeclaredMethod("renderInit", new Class<?>[0]));
        helper.setTickInitMethod(Core.class.getDeclaredMethod("tickInit", new Class<?>[0]));
        helper.setFinalInitMethod(Core.class.getDeclaredMethod("finalInit", new Class<?>[0]));
        helper.setMaximumFramerate(60);
        helper.setRenderMethod(Core.class.getDeclaredMethod("render", int.class));
        helper.setTickMethod(Core.class.getDeclaredMethod("tick", boolean.class));
        helper.setUsesControllers(true);
        helper.setWindowTitle(Main.applicationName+" "+VersionManager.currentVersion);
        helper.setMode(is3D?GameHelper.MODE_HYBRID:GameHelper.MODE_2D);
        if(fullscreen){
            helper.setFullscreen(true);
            helper.setAutoExitFullscreen(false);
        }
        helper.setRenderRange(0, 1000);
        helper.setFrameOfView(90);
        Sys.initLWJGLGame(new File("errors/"), new ErrorAdapter(){
            @Override
            public void warningError(String message, Throwable error, ErrorCategory catagory){
                if(message==null){
                    return;
                }
                if(message.contains(".png!")){
                    System.err.println(message);
                }
            }
            @Override
            public void criticalError(String message, Throwable error, ErrorCategory category){
                super.criticalError(message, error, category);
                helper.running = false;
            }
        }, null, helper);
    }
    public static void renderInit() throws LWJGLException{
        helper.frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                helper.running = false;
            }
        });
        FontManager.addFont("/planner/font/high resolution");
        FontManager.setFont("high resolution");
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        if(is3D){
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            if(enableCullFace) GL11.glEnable(GL11.GL_CULL_FACE);
        }
        if(supportTyping){
            Keyboard.enableRepeatEvents(true);
        }
        new TexturePackManager(null, new TexturePack(){
            @Override
            public InputStream getResourceAsStream(String name){
                if(name.startsWith("/")){
                    return super.getResourceAsStream(name);
                }
                try{
                    return new FileInputStream(new File(name));
                }catch(FileNotFoundException ex){}
                return super.getResourceAsStream(name);
            }
        });
        gui = new GUI(is3D?GameHelper.MODE_HYBRID:GameHelper.MODE_2D, helper);
        gui.open(new MenuMain(gui));
    }
    public static void tickInit() throws LWJGLException{}
    public static void finalInit() throws LWJGLException{
    }
    public static void tick(boolean isLastTick){
        if(!isLastTick){
            gui.tick();
        }
    }
    public static void render(int millisSinceLastTick){
        if(is3D&&enableCullFace) GL11.glDisable(GL11.GL_CULL_FACE);
        gui.render(millisSinceLastTick);
        if(is3D&&enableCullFace) GL11.glEnable(GL11.GL_CULL_FACE);
        FPStracker.add(System.currentTimeMillis());
        while(FPStracker.get(0)<System.currentTimeMillis()-5_000){
            FPStracker.remove(0);
        }
    }
    public static long getFPS(){
        return FPStracker.size()/5;
    }
    public static double distance(MenuComponent o1, MenuComponent o2){
        return Math.sqrt(Math.pow((o1.x+o1.width/2)-(o2.x+o2.width/2), 2)+Math.pow((o1.y+o1.height/2)-(o2.y+o2.height/2), 2));
    }
    public static double distance(MenuComponent component, double x, double y) {
        return distance(component, new MenuComponentButton(x, y, 0, 0, "", false));
    }
    public static double distance(double x1, double y1, double x2, double y2) {
        return distance(new MenuComponentButton(x1, y1, 0, 0, "", false), new MenuComponentButton(x2, y2, 0, 0, "", false));
    }
    public static boolean isMouseWithinComponent(MenuComponent component){
        return isClickWithinBounds(Mouse.getX(), Display.getHeight()-Mouse.getY(), component.x, component.y, component.x+component.width, component.y+component.height);
    }
    public static boolean isMouseWithinComponent(MenuComponent component, MenuComponent... parents){
        double x = component.x;
        double y = component.y;
        for(MenuComponent c : parents){
            x+=c.x;
            y+=c.y;
        }
        return isClickWithinBounds(Mouse.getX(), Display.getHeight()-Mouse.getY(), x, y, x+component.width, y+component.height);
    }
    public static boolean isPointWithinComponent(double x, double y, MenuComponent component){
        return isClickWithinBounds(x, y, component.x, component.y, component.x+component.width, component.y+component.height);
    }
    public static double getValueBetweenTwoValues(double pos1, double val1, double pos2, double val2, double pos){
        if(pos1>pos2){
            return getValueBetweenTwoValues(pos2, val2, pos1, val1, pos);
        }
        double posDiff = pos2-pos1;
        double percent = pos/posDiff;
        double valDiff = val2-val1;
        return percent*valDiff+val1;
    }
    public static void drawLine(double x1, double y1, double x2, double y2, int width){
        Renderer2D.drawLine(x1, y1, x2, y2);
        for(int i = 0; i<width/2; i++){
            Renderer2D.drawLine(x1+i, y1, x2+i, y2);
            Renderer2D.drawLine(x1-i, y1, x2-i, y2);
            Renderer2D.drawLine(x1, y1+i, x2, y2+i);
            Renderer2D.drawLine(x1, y1-i, x2, y2-i);
        }
    }
}
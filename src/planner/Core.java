package planner;
import planner.file.FileFormat;
import discord.Bot;
import java.awt.Color;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.menu.MenuMain;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import multiblock.configuration.Configuration;
import multiblock.Multiblock;
import multiblock.configuration.TextureManager;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.overhaul.turbine.OverhaulTurbine;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.openvr.VR;
import org.lwjgl.system.Callback;
import planner.menu.error.MenuCriticalError;
import planner.menu.MenuDiscord;
import planner.menu.MenuLoadFile;
import planner.menu.MenuTutorial;
import planner.menu.error.MenuMinorError;
import planner.menu.error.MenuModerateError;
import planner.menu.error.MenuSevereError;
import planner.editor.module.Module;
import planner.editor.module.RainbowFactorModule;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorHandler;
import simplelibrary.error.ErrorLevel;
import simplelibrary.font.FontManager;
import simplelibrary.game.Framebuffer;
import simplelibrary.game.GameHelper;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.texture.TexturePack;
import simplelibrary.texture.TexturePackManager;
public class Core extends Renderer2D{
    public static GUI gui;
    public static GameHelper helper;
    public static ArrayList<Long> FPStracker = new ArrayList<>();
    public static boolean debugMode = false;
    public static final boolean is3D = true;
    public static final boolean enableCullFace = true;
    public static final float maxYRot = 80f;
    public static float xRot = 30;
    public static float yRot = 30;
    public static final ArrayList<Multiblock> multiblocks = new ArrayList<>();
    public static final ArrayList<Multiblock> multiblockTypes = new ArrayList<>();
    public static HashMap<String, String> metadata = new HashMap<>();
    public static Configuration configuration = new Configuration(null, null, null);
    public static Theme theme = Theme.themes.get(0);
    private static boolean tutorialShown = false;
    public static int sourceCircle = -1;
    public static int outlineSquare = -1;
    public static boolean delCircle = false;
    public static int circleSize = 64;
    public static final ArrayList<Module> modules = new ArrayList<>();
    public static boolean vr = false;
    private static Callback callback;
    public static boolean invertUndoRedo;
    static{
        for(Configuration configuration : Configuration.configurations){
            if(configuration.overhaul!=null&&configuration.overhaul.fissionMSR!=null){
                for(multiblock.configuration.overhaul.fissionmsr.Block b : configuration.overhaul.fissionMSR.allBlocks){
                    if(b.cooling!=0&&!b.name.contains("Standard")){
                        try{
                            b.setInternalTexture(TextureManager.getImage("overhaul/"+b.name.toLowerCase(Locale.ENGLISH).replace(" coolant heater", "").replace("liquid ", "")));
                        }catch(Exception ex){
                            Sys.error(ErrorLevel.warning, "Failed to load internal texture for MSR Block: "+b.name, ex, ErrorCategory.fileIO);
                        }
                    }
                }
            }
        }
        Configuration.configurations.get(0).impose(configuration);
        multiblockTypes.add(new UnderhaulSFR());
        multiblockTypes.add(new OverhaulSFR());
        multiblockTypes.add(new OverhaulMSR());
        multiblockTypes.add(new OverhaulTurbine());
        multiblockTypes.add(new OverhaulFusionReactor());
        resetMetadata();
        modules.add(new RainbowFactorModule());
    }
    public static void addModule(Module m){
        modules.add(m);
    }
    public static void resetMetadata(){
        metadata.clear();
        metadata.put("Name", "");
        metadata.put("Author", "");
    }
    public static void main(String[] args) throws NoSuchMethodException{
        if(VR.VR_IsRuntimeInstalled()&&VR.VR_IsHmdPresent())vr = true;
        System.out.println("Initializing GameHelper");
        helper = new GameHelper();
        helper.setBackground(theme.getBackgroundColor());
        helper.setDisplaySize(1200/(Main.isBot?10:1), 700/(Main.isBot?10:1));
        helper.setRenderInitMethod(Core.class.getDeclaredMethod("renderInit", new Class<?>[0]));
        helper.setTickInitMethod(Core.class.getDeclaredMethod("tickInit", new Class<?>[0]));
        helper.setFinalInitMethod(Core.class.getDeclaredMethod("finalInit", new Class<?>[0]));
        helper.setRenderMethod(Core.class.getDeclaredMethod("render", int.class));
        helper.setTickMethod(Core.class.getDeclaredMethod("tick", boolean.class));
        helper.setWindowTitle(Main.applicationName+" "+VersionManager.currentVersion);
        helper.setMode(is3D?GameHelper.MODE_HYBRID:GameHelper.MODE_2D);
        helper.setAntiAliasing(4);
        helper.setFrameOfView(90);
        if(Main.isBot)Bot.start(args);
        System.out.println("Starting up");
        Thread debug = new Thread(() -> {
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while(helper.running){
                    String line = reader.readLine();
                    switch(line.trim()){
                        case "fps":
                            System.out.println("FPS: "+getFPS());
                            break;
                    }
                }
                reader.close();
            }catch(IOException ex){}
        });
        debug.setDaemon(true);
        debug.start();
        Sys.initLWJGLGame(new File("errors/"), new ErrorHandler() {
            private final Logger logger = Logger.getLogger(Core.class.getName());
            @Override
            public void log(String message, Throwable error, ErrorCategory category){
                System.err.println(Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Log");
                logger.log(Level.INFO, message, error);
            }
            @Override
            public void warningError(String message, Throwable error, ErrorCategory category){
                System.err.println(Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Warning");
                logger.log(Level.WARNING, message, error);
            }
            @Override
            public void minorError(String message, Throwable error, ErrorCategory category){
                System.err.println("Minor "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error");
                logger.log(Level.SEVERE, message, error);
                if(Main.isBot)return;
                if(Main.hasAWT){
                    String details = "";
                    Throwable t = error;
                    while(t!=null){
                        details+=t.getClass().getName()+" "+t.getMessage();
                        StackTraceElement[] stackTrace = t.getStackTrace();
                        for(StackTraceElement e : stackTrace){
                            if(e.getClassName().startsWith("net."))continue;
                            if(e.getClassName().startsWith("com."))continue;
                            String[] splitClassName = e.getClassName().split("\\Q.");
                            String filename = splitClassName[splitClassName.length-1]+".java";
                            String nextLine = "\nat "+e.getClassName()+"."+e.getMethodName()+"("+filename+":"+e.getLineNumber()+")";
                            if((details+nextLine).length()+4>1024){
                                details+="\n...";
                                break;
                            }else details+=nextLine;
                        }
                        t = t.getCause();
                        if(t!=null)details+="\nCaused by ";
                    }
                    String[] options = new String[]{"Main Menu", "Ignore", "Exit"};
                    switch(javax.swing.JOptionPane.showOptionDialog(null, details, "Minor "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error: "+message, javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0])){
                        case 0:
                            gui.open(new MenuMain(gui));
                            break;
                        case 2:
                            helper.running = false;
                            break;
                        case 1:
                        default:
                            break;
                    }
                }else{
                    gui.open(new MenuMinorError(gui, gui.menu, message, error, category));
                }
            }
            @Override
            public void moderateError(String message, Throwable error, ErrorCategory category){
                System.err.println("Moderate "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error");
                logger.log(Level.SEVERE, message, error);
                if(Main.isBot)return;
                if(Main.hasAWT){
                    String details = "";
                    Throwable t = error;
                    while(t!=null){
                        details+=t.getClass().getName()+" "+t.getMessage();
                        StackTraceElement[] stackTrace = t.getStackTrace();
                        for(StackTraceElement e : stackTrace){
                            if(e.getClassName().startsWith("net."))continue;
                            if(e.getClassName().startsWith("com."))continue;
                            String[] splitClassName = e.getClassName().split("\\Q.");
                            String filename = splitClassName[splitClassName.length-1]+".java";
                            String nextLine = "\nat "+e.getClassName()+"."+e.getMethodName()+"("+filename+":"+e.getLineNumber()+")";
                            if((details+nextLine).length()+4>1024){
                                details+="\n...";
                                break;
                            }else details+=nextLine;
                        }
                        t = t.getCause();
                        if(t!=null)details+="\nCaused by ";
                    }
                    String[] options = new String[]{"Main Menu", "Ignore", "Exit"};
                    switch(javax.swing.JOptionPane.showOptionDialog(null, details, "Moderate "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error: "+message, javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0])){
                        case 0:
                            gui.open(new MenuMain(gui));
                            break;
                        case 2:
                            helper.running = false;
                            break;
                        case 1:
                        default:
                            break;
                    }
                }else{
                    gui.open(new MenuModerateError(gui, gui.menu, message, error, category));
                }
            }
            @Override
            public void severeError(String message, Throwable error, ErrorCategory category){
                System.err.println("Severe "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error");
                logger.log(Level.SEVERE, message, error);
                if(Main.isBot)return;
                if(Main.hasAWT){
                    String details = "";
                    Throwable t = error;
                    while(t!=null){
                        details+=t.getClass().getName()+" "+t.getMessage();
                        StackTraceElement[] stackTrace = t.getStackTrace();
                        for(StackTraceElement e : stackTrace){
                            if(e.getClassName().startsWith("net."))continue;
                            if(e.getClassName().startsWith("com."))continue;
                            String[] splitClassName = e.getClassName().split("\\Q.");
                            String filename = splitClassName[splitClassName.length-1]+".java";
                            String nextLine = "\nat "+e.getClassName()+"."+e.getMethodName()+"("+filename+":"+e.getLineNumber()+")";
                            if((details+nextLine).length()+4>1024){
                                details+="\n...";
                                break;
                            }else details+=nextLine;
                        }
                        t = t.getCause();
                        if(t!=null)details+="\nCaused by ";
                    }
                    String[] options = new String[]{"Main Menu", "Ignore", "Exit"};
                    switch(javax.swing.JOptionPane.showOptionDialog(null, details, "Severe "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error: "+message, javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0])){
                        case 0:
                            gui.open(new MenuMain(gui));
                            break;
                        case 2:
                            helper.running = false;
                            break;
                        case 1:
                        default:
                            break;
                    }
                }else{
                    gui.open(new MenuSevereError(gui, gui.menu, message, error, category));
                }
            }
            @Override
            public void criticalError(String message, Throwable error, ErrorCategory category){
                System.err.println("Critical "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error");
                logger.log(Level.SEVERE, message, error);
                if(Main.isBot)return;
                if(Main.hasAWT){
                    String details = "";
                    Throwable t = error;
                    while(t!=null){
                        details+=t.getClass().getName()+" "+t.getMessage();
                        StackTraceElement[] stackTrace = t.getStackTrace();
                        for(StackTraceElement e : stackTrace){
                            if(e.getClassName().startsWith("net."))continue;
                            if(e.getClassName().startsWith("com."))continue;
                            String[] splitClassName = e.getClassName().split("\\Q.");
                            String filename = splitClassName[splitClassName.length-1]+".java";
                            String nextLine = "\nat "+e.getClassName()+"."+e.getMethodName()+"("+filename+":"+e.getLineNumber()+")";
                            if((details+nextLine).length()+4>1024){
                                details+="\n...";
                                break;
                            }else details+=nextLine;
                        }
                        t = t.getCause();
                        if(t!=null)details+="\nCaused by ";
                    }
                    String[] options = new String[]{"Main Menu", "Exit"};
                    switch(javax.swing.JOptionPane.showOptionDialog(null, details, "Critical "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error: "+message, javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0])){
                        case 0:
                            gui.open(new MenuMain(gui));
                            break;
                        case 1:
                        default:
                            helper.running = false;
                            break;
                    }
                }else{
                    gui.open(new MenuCriticalError(gui, message, error, category));
                }
            }
        }, null, helper);
    }
    public static void renderInit(){
        System.out.println("Loading fonts");
        FontManager.addFont("/simplelibrary/font");
        FontManager.addFont("/planner/font/high resolution");
        FontManager.addFont("/planner/font/small");
        FontManager.addFont("/planner/font/slim");
        FontManager.setFont("high resolution");
        System.out.println("Initializing render engine");
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        if(is3D){
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            if(enableCullFace) GL11.glEnable(GL11.GL_CULL_FACE);
        }
        System.out.println("Creating texture pack manager");
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
        System.out.println("Creating GL Debug Callback");
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        callback = GLUtil.setupDebugMessageCallback();
        System.out.println("Initializing GUI");
        gui = new GUI(is3D?GameHelper.MODE_HYBRID:GameHelper.MODE_2D, helper);
        if(Main.isBot)gui.open(new MenuDiscord(gui));
        else gui.open(new MenuMain(gui));
        System.out.println("Render initialization complete!");
    }
    public static void tickInit(){}
    public static void finalInit() throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
        if(Main.headless)GLFW.glfwHideWindow(helper.getWindow());
        System.out.println("Activating GUI");
        helper.assignGUI(gui);
        System.out.println("Loading settings");
        File f = new File("settings.dat").getAbsoluteFile();
        if(f.exists()){
            Config settings = Config.newConfig(f);
            settings.load();
            System.out.println("Loading theme");
            setTheme(Theme.themes.get(settings.get("theme", 0)));
            ConfigList modules = settings.get("modules", new ConfigList());
            for(Iterator<String> it = modules.iterator(); it.hasNext();){
                String str = it.next();
                for(Module m : Core.modules){
                    if(m.getName().equals(str))m.activate();
                }
            }
            tutorialShown = settings.get("tutorialShown", false);
            invertUndoRedo = settings.get("invertUndoRedo", false);
        }
        if(Main.hasAWTAfterStartup){
            Main.hasAWT = true;
        }
        Main.setLookAndFeel();
        System.out.println("Startup complete!");
        if(!tutorialShown&&!Main.isBot&&!Main.headless){
            gui.open(new MenuTutorial(gui, gui.menu));
            tutorialShown = true;
        }
    }
    public static void tick(boolean isLastTick){
        if(!isLastTick){
            if(helper.isKeyDown(GLFW.GLFW_KEY_LEFT))xRot-=2;
            if(helper.isKeyDown(GLFW.GLFW_KEY_RIGHT))xRot+=2;
            if(helper.isKeyDown(GLFW.GLFW_KEY_UP))yRot = Math.min(maxYRot, Math.max(-maxYRot, yRot-2));
            if(helper.isKeyDown(GLFW.GLFW_KEY_DOWN))yRot = Math.min(maxYRot, Math.max(-maxYRot, yRot+2));
            gui.tick();
        }else{
            File f = new File("settings.dat").getAbsoluteFile();
            Config settings = Config.newConfig(f);
            settings.set("theme", Theme.themes.indexOf(theme));
            ConfigList modules = new ConfigList();
            for(Module m : Core.modules){
                if(m.isActive())modules.add(m.getName());//TODO a programmer-friendly ID please
            }
            settings.set("modules", modules);
            settings.set("tutorialShown", tutorialShown);
            settings.set("invertUndoRedo", invertUndoRedo);
            settings.save();
            if(Main.isBot){
                Bot.stop();
                System.exit(0);//TODO Shouldn't have to do this! :(
            }
        }
    }
    public static void render(int millisSinceLastTick){
        if(delCircle&&sourceCircle!=-1){
            ImageStash.instance.deleteTexture(sourceCircle);
            ImageStash.instance.deleteTexture(outlineSquare);
            sourceCircle = -1;
            outlineSquare = -1;
            delCircle = false;
        }
        if(sourceCircle==-1){
            BufferedImage image = Core.makeImage(circleSize, circleSize, (buff) -> {
                Core.drawCircle(buff.width/2, buff.height/2, buff.width*(4/16d), buff.width*(6/16d), Color.white);
            });
            sourceCircle = ImageStash.instance.allocateAndSetupTexture(image);
        }
        if(outlineSquare==-1){
            BufferedImage image = Core.makeImage(32, 32, (buff) -> {
                Core.applyWhite();
                double inset = buff.width/32d;
                drawRect(inset, inset, buff.width-inset, inset+buff.width/16, 0);
                drawRect(inset, buff.width-inset-buff.width/16, buff.width-inset, buff.width-inset, 0);
                drawRect(inset, inset+buff.width/16, inset+buff.width/16, buff.width-inset-buff.width/16, 0);
                drawRect(buff.width-inset-buff.width/16, inset+buff.width/16, buff.width-inset, buff.width-inset-buff.width/16, 0);
            });
            outlineSquare = ImageStash.instance.allocateAndSetupTexture(image);
        }
        applyWhite();
        if(gui.menu instanceof MenuMain){
            GL11.glPushMatrix();
            GL11.glTranslated(.4, 0, -1.5);
            GL11.glRotated(yRot, 1, 0, 0);
            GL11.glRotated(xRot, 0, 1, 0);
            Multiblock mb = ((MenuMain)gui.menu).getSelectedMultiblock();
            if(mb!=null){
                double size = Math.max(mb.getX(), Math.max(mb.getY(), mb.getZ()));
                size/=mb.get3DPreviewScale();
                GL11.glScaled(1/size, 1/size, 1/size);
                GL11.glTranslated(-mb.getX()/2d, -mb.getY()/2d, -mb.getZ()/2d);
                mb.draw3D();
            }
            GL11.glPopMatrix();
        }
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
    public static double getValueBetweenTwoValues(double pos1, double val1, double pos2, double val2, double pos){
        if(pos1>pos2){
            return getValueBetweenTwoValues(pos2, val2, pos1, val1, pos);
        }
        double posDiff = pos2-pos1;
        double percent = pos/posDiff;
        double valDiff = val2-val1;
        return percent*valDiff+val1;
    }
    private static final HashMap<BufferedImage, Integer> imgs = new HashMap<>();
    public static int getTexture(BufferedImage image){
        if(image==null)return -1;
        if(!imgs.containsKey(image)){
            imgs.put(image, ImageStash.instance.allocateAndSetupTexture(image));
        }
        return imgs.get(image);
    }
    public static void setTheme(Theme t){
        t.onSet();
        theme = t;
        helper.setBackground(theme.getBackgroundColor());
    }
    public static void applyWhite(){
        applyColor(theme.getWhite());
    }
    public static void applyWhite(float alpha){
        applyColor(theme.getWhite(), alpha);
    }
    public static void applyColor(Color c){
        GL11.glColor4f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f);
    }
    public static void applyColor(Color c, float alpha){
        GL11.glColor4f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f*alpha);
    }
    public static void applyAverageColor(Color c1, Color c2){
        GL11.glColor4f((c1.getRed()+c2.getRed())/510f, (c1.getGreen()+c2.getGreen())/510f, (c1.getBlue()+c2.getBlue())/510f, (c1.getAlpha()+c2.getAlpha())/510f);
    }
    public static void applyAverageColor(Color c1, Color c2, float alpha){
        GL11.glColor4f((c1.getRed()+c2.getRed())/510f, (c1.getGreen()+c2.getGreen())/510f, (c1.getBlue()+c2.getBlue())/510f, (c1.getAlpha()+c2.getAlpha())/510f*alpha);
    }
    public static boolean isAltPressed(){
        return helper.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)||helper.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
    }
    public static boolean isControlPressed(){
        return helper.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)||helper.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }
    public static boolean isShiftPressed(){
        return helper.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)||helper.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
    public static void drawCircle(double x, double y, double innerRadius, double outerRadius, Color color){
        Core.applyColor(color);
        int resolution = (int)(2*Math.PI*outerRadius);
        ImageStash.instance.bindTexture(0);
        GL11.glBegin(GL11.GL_QUADS);
        double angle = 0;
        for(int i = 0; i<resolution; i++){
            double inX = x+Math.cos(Math.toRadians(angle-90))*innerRadius;
            double inY = y+Math.sin(Math.toRadians(angle-90))*innerRadius;
            GL11.glVertex2d(inX, inY);
            double outX = x+Math.cos(Math.toRadians(angle-90))*outerRadius;
            double outY = y+Math.sin(Math.toRadians(angle-90))*outerRadius;
            GL11.glVertex2d(outX,outY);
            angle+=(360d/resolution);
            if(angle>=360)angle-=360;
            outX = x+Math.cos(Math.toRadians(angle-90))*outerRadius;
            outY = y+Math.sin(Math.toRadians(angle-90))*outerRadius;
            GL11.glVertex2d(outX,outY);
            inX = x+Math.cos(Math.toRadians(angle-90))*innerRadius;
            inY = y+Math.sin(Math.toRadians(angle-90))*innerRadius;
            GL11.glVertex2d(inX, inY);
        }
        GL11.glEnd();
    }
    public static void drawRegularPolygon(double x, double y, double radius, int quality, double angle, int texture){
        if(quality<3){
            throw new IllegalArgumentException("A polygon must have at least 3 sides!");
        }
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_TRIANGLES);
        for(int i = 0; i<quality; i++){
            GL11.glVertex2d(x, y);
            double X = x+Math.cos(Math.toRadians(angle-90))*radius;
            double Y = y+Math.sin(Math.toRadians(angle-90))*radius;
            GL11.glVertex2d(X, Y);
            angle+=(360D/quality);
            X = x+Math.cos(Math.toRadians(angle-90))*radius;
            Y = y+Math.sin(Math.toRadians(angle-90))*radius;
            GL11.glVertex2d(X, Y);
        }
        GL11.glEnd();
    }
    public static void drawOval(double x, double y, double xRadius, double yRadius, double xThickness, double yThickness, int quality, int texture){
        drawOval(x, y, xRadius, yRadius, xThickness, yThickness, quality, texture, 0, quality-1);
    }
    public static void drawOval(double x, double y, double xRadius, double yRadius, double thickness, int quality, int texture){
        drawOval(x, y, xRadius, yRadius, thickness, thickness, quality, texture, 0, quality-1);
    }
    public static void drawOval(double x, double y, double xRadius, double yRadius, double thickness, int quality, int texture, int left, int right){
        drawOval(x, y, xRadius, yRadius, thickness, thickness, quality, texture, left, right);
    }
    public static void drawOval(double x, double y, double xRadius, double yRadius, double xThickness, double yThickness, int quality, int texture, int left, int right){
        if(quality<3){
            throw new IllegalArgumentException("Quality must be >=3!");
        }
        while(left<0)left+=quality;
        while(right<0)right+=quality;
        while(left>quality)left-=quality;
        while(right>quality)right-=quality;
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        double angle = 0;
        for(int i = 0; i<quality; i++){
            boolean inRange = false;
            if(left>right)inRange = i>=left||i<=right;
            else inRange = i>=left&&i<=right;
            if(inRange){
                double X = x+Math.cos(Math.toRadians(angle-90))*xRadius;
                double Y = y+Math.sin(Math.toRadians(angle-90))*yRadius;
                GL11.glVertex2d(X, Y);
                X = x+Math.cos(Math.toRadians(angle-90))*(xRadius-xThickness);
                Y = y+Math.sin(Math.toRadians(angle-90))*(yRadius-yThickness);
                GL11.glVertex2d(X, Y);
            }
            angle+=(360D/quality);
            if(inRange){
                double X = x+Math.cos(Math.toRadians(angle-90))*(xRadius-xThickness);
                double Y = y+Math.sin(Math.toRadians(angle-90))*(yRadius-yThickness);
                GL11.glVertex2d(X, Y);
                X = x+Math.cos(Math.toRadians(angle-90))*xRadius;
                Y = y+Math.sin(Math.toRadians(angle-90))*yRadius;
                GL11.glVertex2d(X, Y);
            }
        }
        GL11.glEnd();
    }
    public static BufferedImage makeImage(int width, int height, BufferRenderer r){
        boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        if(cull)GL11.glDisable(GL11.GL_CULL_FACE);
        if(depth)GL11.glDisable(GL11.GL_DEPTH_TEST);
        ByteBuffer bufferer = ImageStash.createDirectByteBuffer(width*height*4);
        Framebuffer buff = new Framebuffer(helper, null, width, height);
        buff.bindRenderTarget2D();
        r.render(buff);
        bufferer.clear();
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bufferer);
        buff.releaseRenderTarget();
        ImageStash.instance.deleteBuffer(ImageStash.instance.getBuffer(buff.name));
        ImageStash.instance.deleteTexture(buff.getTexture());
        int[] imgRGBData = new int[width*height];
        byte[] imgData = new byte[width*height*4];
        bufferer.rewind();
        bufferer.get(imgData);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        for(int i=0;i<imgRGBData.length;i++){
            imgRGBData[i]=(f(imgData[i*4])<<16)+(f(imgData[i*4+1])<<8)+(f(imgData[i*4+2]))+(f(imgData[i*4+3])<<24);//DO NOT Use RED, GREEN, or BLUE channel (here BLUE) for alpha data
        }
        img.setRGB(0, 0, width, height, imgRGBData, 0, width);
        if(cull)GL11.glEnable(GL11.GL_CULL_FACE);
        if(depth)GL11.glEnable(GL11.GL_DEPTH_TEST);
        return img;
    }
    public static File askForOverwrite(File file){
        if(!file.exists())return file; 
        if(Main.hasAWT){
            if(javax.swing.JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "File already exists!", javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE)!=javax.swing.JOptionPane.OK_OPTION)return null;
            file.delete();
        }else{
            while(file.exists()){
                String path = file.getPath();
                String[] split = path.split("\\.");
                String extension = "."+split[split.length-1];
                file = new File(path.substring(0, path.length()-extension.length())+"_"+extension);//TODO TEST THIS
            }
        }
        return file;
    }
    public static interface BufferRenderer{
        void render(Framebuffer buff);
    }
    private static int f(byte imgData){
        return (imgData+256)&255;
    }
    public static File lastOpenFolder = new File("file").getAbsoluteFile().getParentFile();
    public static void createFileChooser(FileChooserResultListener listener, FileFormat... formats){
        if(Main.hasAWT){
            new Thread(() -> {
                javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(lastOpenFolder);
                HashMap<javax.swing.filechooser.FileFilter, FileFormat> filters = new HashMap<>();
                for(FileFormat format : formats){
                    javax.swing.filechooser.FileFilter filter = format.getFileFilter();
                    filters.put(filter, format);
                    chooser.addChoosableFileFilter(filter);
                    if(Core.isShiftPressed()&&format==FileFormat.PNG)chooser.setFileFilter(filter);
                }
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        lastOpenFolder = chooser.getSelectedFile().getAbsoluteFile().getParentFile();
                        listener.approved(chooser.getSelectedFile(), filters.get(chooser.getFileFilter()));
                    }
                });
                chooser.showOpenDialog(null);
            }).start();
        }else{
            gui.open(new MenuLoadFile(gui, gui.menu, listener, formats));
        }
    }
    public static void createFileChooser(File selectedFile, FileChooserResultListener listener, FileFormat... formats){
        if(Main.hasAWT){
            new Thread(() -> {
                javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(lastOpenFolder);
                if(selectedFile!=null)chooser.setSelectedFile(selectedFile);
                HashMap<javax.swing.filechooser.FileFilter, FileFormat> filters = new HashMap<>();
                for(FileFormat format : formats){
                    javax.swing.filechooser.FileFilter filter = format.getFileFilter();
                    filters.put(filter, format);
                    chooser.addChoosableFileFilter(filter);
                    if(Core.isShiftPressed()&&format==FileFormat.PNG)chooser.setFileFilter(filter);
                }
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        lastOpenFolder = chooser.getSelectedFile().getAbsoluteFile().getParentFile();
                        listener.approved(chooser.getSelectedFile(), filters.get(chooser.getFileFilter()));
                    }
                });
                chooser.showSaveDialog(null);
            }).start();
        }else{
            if(selectedFile!=null){
                FileFormat form = null;
                for(FileFormat format : formats){
                    for(String ext : format.extensions){
                        if(selectedFile.getName().endsWith("."+ext))form = format;
                    }
                }
                if(form!=null){
                    listener.approved(selectedFile, form);
                    return;
                }
            }
            listener.approved(new File("export."+formats[0].extensions[0]), formats[0]);//TODO proper save
        }
    }
}

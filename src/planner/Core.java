package planner;
import discord.Bot;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import multiblock.BoundingBox;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.TextureManager;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.overhaul.turbine.OverhaulTurbine;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.openvr.VR;
import org.lwjgl.system.Callback;
import planner.file.FileFormat;
import planner.file.FileWriter;
import planner.file.NCPFFile;
import planner.menu.MenuCredits;
import planner.menu.MenuDiscord;
import planner.menu.MenuEdit;
import planner.menu.MenuLoadFile;
import planner.menu.MenuMain;
import planner.menu.MenuTutorial;
import planner.menu.error.MenuCriticalError;
import planner.menu.error.MenuMinorError;
import planner.menu.error.MenuModerateError;
import planner.menu.error.MenuSevereError;
import planner.module.FusionTestModule;
import planner.module.Module;
import planner.module.OverhaulModule;
import planner.module.RainbowFactorModule;
import planner.module.UnderhaulModule;
import planner.tutorial.Tutorial;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
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
    public static boolean autoBuildCasing = true;
    public static boolean recoveryMode = false;
    static{
        resetMetadata();
        modules.add(new UnderhaulModule());
        modules.add(new OverhaulModule());
        modules.add(new FusionTestModule());
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
                            autoSaveAndExit();
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
                            autoSaveAndExit();
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
                            autoSaveAndExit();
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
                            autoSaveAndExit();
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
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
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
        System.out.println("Loading settings");
        File f = new File("settings.dat").getAbsoluteFile();
        if(f.exists()){
            Config settings = Config.newConfig(f);
            settings.load();
            System.out.println("Loading theme");
            setTheme(Theme.themes.get(settings.get("theme", 0)));
            try{
                Config modules = settings.get("modules", Config.newConfig());
                HashMap<Module, Boolean> moduleStates = new HashMap<>();
                for(String key : modules.properties()){
                    for(Module m : Core.modules){
                        if(m.name.equals(key))moduleStates.put(m, modules.getBoolean(key));
                    }
                }
                for(Module m : Core.modules){
                    if(!moduleStates.containsKey(m))continue;
                    if(m.isActive()){
                        if(!moduleStates.get(m))m.deactivate();
                    }else{
                        if(moduleStates.get(m))m.activate();
                    }
                }
            }catch(Exception ex){}
            tutorialShown = settings.get("tutorialShown", false);
            invertUndoRedo = settings.get("invertUndoRedo", false);
            autoBuildCasing = settings.get("autoBuildCasing", true);
        }
        refreshModules();
        for(Configuration configuration : Configuration.configurations){
            if(configuration.overhaul!=null&&configuration.overhaul.fissionMSR!=null){
                for(multiblock.configuration.overhaul.fissionmsr.Block b : configuration.overhaul.fissionMSR.allBlocks){
                    if(b.heater&&!b.getDisplayName().contains("Standard")){
                        try{
                            b.setInternalTexture(TextureManager.getImage("overhaul/"+b.getDisplayName().toLowerCase(Locale.ENGLISH).replace(" coolant heater", "").replace("liquid ", "")));
                        }catch(Exception ex){
                            Sys.error(ErrorLevel.warning, "Failed to load internal texture for MSR Block: "+b.name, ex, ErrorCategory.fileIO);
                        }
                    }
                }
            }
        }
        Configuration.configurations.get(0).impose(configuration);
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
            Config modules = Config.newConfig();
            for(Module m : Core.modules){
                modules.set(m.name, m.isActive());
            }
            settings.set("modules", modules);
            settings.set("tutorialShown", tutorialShown);
            settings.set("invertUndoRedo", invertUndoRedo);
            settings.set("autoBuildCasing", autoBuildCasing);
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
                BoundingBox bbox = mb.getBoundingBox();
                double size = Math.max(bbox.getWidth(), Math.max(bbox.getHeight(), bbox.getDepth()));
                size/=mb.get3DPreviewScale();
                GL11.glScaled(1/size, 1/size, 1/size);
                GL11.glTranslated(-bbox.getWidth()/2d, -bbox.getHeight()/2d, -bbox.getDepth()/2d);
                mb.draw3D();
            }
            GL11.glPopMatrix();
        }
        if(gui.menu instanceof MenuEdit){
            MenuEdit editor = (MenuEdit)gui.menu;
            if(editor.toggle3D.isToggledOn){
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glPushMatrix();
                GL11.glLoadIdentity();
                GL11.glOrtho(0, gui.helper.displayWidth()*gui.helper.guiScale, 0, gui.helper.displayHeight()*gui.helper.guiScale, 1f, 10000F);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glPushMatrix();
                GL11.glTranslated(editor.toggle3D.x+editor.toggle3D.width/2, gui.helper.displayHeight()-(editor.toggle3D.y-editor.toggle3D.width/2), -1000);
//                GL11.glTranslated((double)gui.helper.displayWidth()/gui.helper.displayHeight()-.25, 0, -1);
                GL11.glScaled(.625, .625, .625);
                GL11.glScaled(editor.toggle3D.width, editor.toggle3D.width, editor.toggle3D.width);
                GL11.glRotated(yRot, 1, 0, 0);
                GL11.glRotated(xRot, 0, 1, 0);
                Multiblock mb = editor.getMultiblock();
                if(mb!=null){
                    BoundingBox bbox = mb.getBoundingBox();
                    double size = Math.max(bbox.getWidth(), Math.max(bbox.getHeight(), bbox.getDepth()));
                    size/=mb.get3DPreviewScale();
                    GL11.glScaled(1/size, 1/size, 1/size);
                    GL11.glTranslated(-bbox.getWidth()/2d, -bbox.getHeight()/2d, -bbox.getDepth()/2d);
                    editor.draw3D();
                }
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
            }
        }
        if(gui.menu instanceof MenuCredits){
            ((MenuCredits)gui.menu).render3D(millisSinceLastTick);
        }
        clearBoundStack();
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
    public static float getValueBetweenTwoValues(float pos1, float val1, float pos2, float val2, float pos){
        if(pos1>pos2){
            return getValueBetweenTwoValues(pos2, val2, pos1, val1, pos);
        }
        float posDiff = pos2-pos1;
        float percent = pos/posDiff;
        float valDiff = val2-val1;
        return percent*valDiff+val1;
    }
    private static final HashMap<BufferedImage, Integer> imgs = new HashMap<>();
    private static final HashMap<BufferedImage, Boolean> alphas = new HashMap<>();
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
    public static void refreshModules(){
        multiblockTypes.clear();
        Tutorial.init();
        Configuration.clearConfigurations();
        for(Module m : modules){
            if(m.isActive()){
                m.addMultiblockTypes(multiblockTypes);
                m.addTutorials();
                m.addConfigurations();
            }
        }
    }
    public static boolean hasUnderhaulSFR(){
        for(Multiblock m : multiblockTypes){
            if(m instanceof UnderhaulSFR)return true;
        }
        return false;
    }
    public static boolean hasOverhaulSFR(){
        for(Multiblock m : multiblockTypes){
            if(m instanceof OverhaulSFR)return true;
        }
        return false;
    }
    public static boolean hasOverhaulMSR(){
        for(Multiblock m : multiblockTypes){
            if(m instanceof OverhaulMSR)return true;
        }
        return false;
    }
    public static boolean hasOverhaulTurbine(){
        for(Multiblock m : multiblockTypes){
            if(m instanceof OverhaulTurbine)return true;
        }
        return false;
    }
    public static boolean hasOverhaulFusion(){
        for(Multiblock m : multiblockTypes){
            if(m instanceof OverhaulFusionReactor)return true;
        }
        return false;
    }
    public static boolean hasAlpha(BufferedImage image){
        if(image==null)return false;
        if(!alphas.containsKey(image)){
            boolean hasAlpha = false;
            FOR:for(int x = 0; x<image.getWidth(); x++){
                for(int y = 0; y<image.getHeight(); y++){
                    if(new Color(image.getRGB(x, y), true).getAlpha()!=255){
                        hasAlpha = true;
                        break FOR;
                    }
                }
            }
            alphas.put(image, hasAlpha);
        }
        return alphas.get(image);
    }
    public static int autosave(){
        File file = new File("autosave.ncpf");
        File cfgFile = new File("config_autosave.ncpf");
        int num = 1;
        while(file.exists()||cfgFile.exists()){
            file = new File("autosave"+num+".ncpf");
            cfgFile = new File("config_autosave"+num+".ncpf");
            num++;
        }
        {//multiblocks
            NCPFFile ncpf = new NCPFFile();
            ncpf.configuration = PartialConfiguration.generate(Core.configuration, Core.multiblocks);
            ncpf.multiblocks.addAll(Core.multiblocks);
            ncpf.metadata.putAll(Core.metadata);
            FileWriter.write(ncpf, file, FileWriter.NCPF);
        }
        {//configuration
            try(FileOutputStream stream = new FileOutputStream(cfgFile)){
                Config header = Config.newConfig();
                header.set("version", NCPFFile.SAVE_VERSION);
                header.set("count", 0);
                header.save(stream);
                Core.configuration.save(null, Config.newConfig()).save(stream);
            }catch(IOException ex){
                throw new RuntimeException(ex);
            }
        }
        return num;
    }
    public static void openWebpage(String link){
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try{
                Desktop.getDesktop().browse(new URI(link));
            }catch(URISyntaxException|IOException ex){
                if(Main.hasAWT){
                    javax.swing.JOptionPane.showMessageDialog(null, link, "Failed to open webpage", javax.swing.JOptionPane.ERROR_MESSAGE);
                }else{
                    Sys.error(ErrorLevel.minor, "Failed to open webpage\n"+link, null, ErrorCategory.InternetIO, false);
                }
            }
        }else{
            Sys.error(ErrorLevel.minor, "Desktop Browse is not supported\n"+link, null, ErrorCategory.InternetIO, false);
        }
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
    /**
     * Draws formatted text.
     * @param left left edge
     * @param top top edge
     * @param right right edge
     * @param bottom bottom edge
     * @param text The <code>FormattedText</code> to draw.
     * @param snap Which side to snap the text to. Defaults to left. -1 = left. 0 = center. 1 = right
     */
    public static void drawFormattedText(double left, double top, double right, double bottom, FormattedText text, int snap){
        if(FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)>right-left){
            text.trimSlightly();
            drawFormattedText(left, top, right, bottom, text, snap);
            return;
        }
        if(snap==0){
            left = (left+right)/2-FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)/2;
        }
        if(snap>0){
            left = right-FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)/2;
        }
        while(text!=null){
            if(text.color!=null)GL11.glColor3f(text.color.getRed()/255f, text.color.getGreen()/255f, text.color.getBlue()/255f);
            double textWidth = FontManager.getLengthForStringWithHeight(text.text, bottom-top);
            if(text.italic){
                drawItalicText(left, top, right, bottom, text.text);
            }else{
                drawText(left, top, right, bottom, text.text);
            }
            if(text.bold){
                double offset = (bottom-top)/20;
                for(int x = 0; x<offset+1; x++){
                    for(int y = 0; y<offset+1; y++){
                        if(text.italic){
                            drawItalicText(left+x, top, right+x, bottom, text.text);
                            drawItalicText(left+x, top-y, right+x, bottom-y, text.text);
                            drawItalicText(left, top-y, right, bottom-y, text.text);
                        }else{
                            drawText(left+x, top, right+x, bottom, text.text);
                            drawText(left+x, top-y, right+x, bottom-y, text.text);
                            drawText(left, top-y, right, bottom-y, text.text);
                        }
                    }
                }
            }
            if(text.strikethrough){
                double topIndent = (bottom-top)*.6;
                double bottomIndent = (bottom-top)*.3;
                drawRect(left, top+topIndent, left+textWidth, bottom-bottomIndent, 0);
            }
            if(text.underline){
                double indent = (bottom-top)*.9;
                drawRect(left, top+indent, left+textWidth, bottom, 0);
            }
            left+=textWidth;
            text = text.next;
        }
    }
    public static FormattedText drawFormattedTextWithWrap(double left, double top, double right, double bottom, FormattedText text, int snap){
        if(FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)>right-left){
            String txt = text.text;
            text.trimSlightlyWithoutElipses();
            FormattedText also = drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
            txt = txt.substring(text.text.length());
            return new FormattedText(also!=null?also.text+txt:txt, text.color, text.bold, text.italic, text.underline, text.strikethrough);
        }
        if(snap==0){
            left = (left+right)/2-FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)/2;
        }
        if(snap>0){
            left = right-FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)/2;
        }
        if(text.color!=null)GL11.glColor3f(text.color.getRed()/255f, text.color.getGreen()/255f, text.color.getBlue()/255f);
        double textWidth = FontManager.getLengthForStringWithHeight(text.text, bottom-top);
        if(text.italic){
            drawItalicText(left, top, right, bottom, text.text);
        }else{
            drawText(left, top, right, bottom, text.text);
        }
        if(text.bold){
            double offset = (bottom-top)/20;
            for(int x = 0; x<offset+1; x++){
                for(int y = 0; y<offset+1; y++){
                    if(text.italic){
                        drawItalicText(left+x, top, right+x, bottom, text.text);
                        drawItalicText(left+x, top-y, right+x, bottom-y, text.text);
                        drawItalicText(left, top-y, right, bottom-y, text.text);
                    }else{
                        drawText(left+x, top, right+x, bottom, text.text);
                        drawText(left+x, top-y, right+x, bottom-y, text.text);
                        drawText(left, top-y, right, bottom-y, text.text);
                    }
                }
            }
        }
        if(text.strikethrough){
            double topIndent = (bottom-top)*.6;
            double bottomIndent = (bottom-top)*.3;
            drawRect(left, top+topIndent, left+textWidth, bottom-bottomIndent, 0);
        }
        if(text.underline){
            double indent = (bottom-top)*.9;
            drawRect(left, top+indent, left+textWidth, bottom, 0);
        }
        left+=textWidth;
        if(text.next!=null){
            return drawFormattedTextWithWrap(left+textWidth, top, right, bottom, text, snap);
        }
        return null;
    }
    /**
     * Draws formatted text with word-wrapping.
     * @param leftEdge left edge
     * @param topEdge top edge
     * @param rightPossibleEdge right possible edge
     * @param bottomEdge bottom edge
     * @param text The <code>FormattedText</code> to draw.
     * @param snap Which side to snap the text to. Defaults to left. -1 = left. 0 = center. 1 = right
     * @return the portion of text wrapped to the next line
     */
    public static FormattedText drawFormattedTextWithWordWrap(double leftEdge, double topEdge, double rightPossibleEdge, double bottomEdge, FormattedText text, int snap){
        ArrayList<FormattedText> words = text.split(" ");
        if(words.isEmpty())return drawFormattedTextWithWrap(leftEdge, topEdge, rightPossibleEdge, bottomEdge, text, snap);
        String str = words.get(0).text;
        double height = bottomEdge-topEdge;
        double length = rightPossibleEdge-leftEdge;
        for(int i = 1; i<words.size(); i++){
            String string = str+" "+words.get(i).text;
            if(FontManager.getLengthForStringWithHeight(string.trim(), height)>=length){
                drawFormattedTextWithWrap(leftEdge, topEdge, rightPossibleEdge, bottomEdge, new FormattedText(str, text.color, text.bold, text.italic, text.underline, text.strikethrough), snap);
                return new FormattedText(text.text.replaceFirst("\\Q"+str, "").trim());
            }else{
                str = string;
            }
        }
        return drawFormattedTextWithWrap(leftEdge, topEdge, rightPossibleEdge, bottomEdge, text, snap);
    }
    private static void drawItalicText(double left, double top, double right, double bottom, String text){
        ImageStash.instance.bindTexture(FontManager.getFontImage());
        GL11.glBegin(GL11.GL_QUADS);
        for(char c : text.toCharArray()){
            double[] texLoc = FontManager.getTextureLocationForChar(c);
            double tilt = (bottom-top)*.25;
            double len = FontManager.getLengthForStringWithHeight(c+"", bottom-top);
            GL11.glTexCoord2d(texLoc[0], texLoc[1]);
            GL11.glVertex2d(left+tilt, top);
            GL11.glTexCoord2d(texLoc[2], texLoc[1]);
            GL11.glVertex2d(left+len+tilt, top);
            GL11.glTexCoord2d(texLoc[2], texLoc[3]);
            GL11.glVertex2d(left+len, bottom);
            GL11.glTexCoord2d(texLoc[0], texLoc[3]);
            GL11.glVertex2d(left, bottom);
            left+=len;
        }
        GL11.glEnd();
    }
    public static boolean areImagesEqual(BufferedImage img1, BufferedImage img2) {
        if(img1==img2)return true;
        if(img1==null||img2==null)return false;
        if(img1.getWidth()!=img2.getWidth())return false;
        if(img1.getHeight()!=img2.getHeight())return false;
        for(int x = 0; x<img1.getWidth(); x++){
            for(int y = 0; y<img1.getHeight(); y++){
                if(img1.getRGB(x, y)!=img2.getRGB(x, y))return false;
            }
        }
        return true;
    }
    public static int logBase(int base, int n){
        return (int)(Math.log(n)/Math.log(base));
    }
    public static void autoSaveAndExit(){
        Throwable error = null;
        int num = 0;
        try{
            num = autosave();
        }catch(Throwable t){error = t;}
        if(error==null){
            if(Main.hasAWT){
                javax.swing.JOptionPane.showMessageDialog(null, "Saved to autosave"+num+".ncpf", "Autosave successful!", javax.swing.JOptionPane.ERROR_MESSAGE);
            }else{
                System.err.println("Autosave failed!");
                error.printStackTrace();
            }
            
        }else{
            if(Main.hasAWT){
                String details = "";
                while(error!=null){
                    details+=error.getClass().getName()+" "+error.getMessage();
                    StackTraceElement[] stackTrace = error.getStackTrace();
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
                    error = error.getCause();
                    if(error!=null)details+="\nCaused by ";
                }
                javax.swing.JOptionPane.showMessageDialog(null, details, "Autosave failed!", javax.swing.JOptionPane.ERROR_MESSAGE);
            }else{
                System.err.println("Autosave failed!");
                error.printStackTrace();
            }
        }
        helper.running = false;
    }
}
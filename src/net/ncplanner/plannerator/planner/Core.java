package net.ncplanner.plannerator.planner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.discord.Bot;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.PartialConfiguration;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileWriter;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import net.ncplanner.plannerator.planner.menu.MenuCredits;
import net.ncplanner.plannerator.planner.menu.MenuEdit;
import net.ncplanner.plannerator.planner.menu.MenuInit;
import net.ncplanner.plannerator.planner.menu.MenuMain;
import net.ncplanner.plannerator.planner.menu.MenuTransition;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimaList;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMulticolumnMinimaList;
import net.ncplanner.plannerator.planner.menu.dialog.MenuCriticalError;
import net.ncplanner.plannerator.planner.menu.dialog.MenuMinorError;
import net.ncplanner.plannerator.planner.menu.dialog.MenuModerateError;
import net.ncplanner.plannerator.planner.menu.dialog.MenuSevereError;
import net.ncplanner.plannerator.planner.menu.dialog.MenuWarningMessage;
import net.ncplanner.plannerator.planner.module.Module;
import net.ncplanner.plannerator.planner.theme.Theme;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentMultiblockSettingsPanel;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentSpecialPanel;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentToolPanel;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.openvr.VR;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NativeFileDialog;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorHandler;
import simplelibrary.error.ErrorLevel;
import simplelibrary.font.FontManager;
import simplelibrary.game.Framebuffer;
import simplelibrary.game.GameHelper;
import simplelibrary.image.Color;
import simplelibrary.image.Image;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.texture.TexturePack;
import simplelibrary.texture.TexturePackManager;
public class Core{
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
    public static Theme theme = Theme.themes.get(0).get(0);
    public static boolean tutorialShown = false;
    public static Image sourceCircle = null;
    public static Image outlineSquare = null;
    public static boolean delCircle = false;
    public static int circleSize = 64;
    public static final ArrayList<Module> modules = new ArrayList<>();
    public static boolean vr = false;
    private static Callback callback;
    public static boolean invertUndoRedo;
    public static boolean autoBuildCasing = true;
    public static boolean recoveryMode = false;
    public static final ArrayList<String> pinnedStrs = new ArrayList<>();
    private static Random rand = new Random();
    public static String str = "";
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
        helper.setBackground(theme.getMenuBackgroundColor());
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
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
                while(helper.running){
                    String line = reader.readLine();
                    switch(line.trim()){
                        case "fps":
                            System.out.println("FPS: "+getFPS());
                            break;
                    }
                }
            }catch(IOException ex){}
        });
        debug.setName("Console debug thread");
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
                if(Main.isBot)return;
                gui.menu = new MenuWarningMessage(gui, gui.menu, message, error, category);
            }
            @Override
            public void minorError(String message, Throwable error, ErrorCategory category){
                System.err.println("Minor "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error");
                logger.log(Level.SEVERE, message, error);
                if(Main.isBot)return;
                gui.menu = new MenuMinorError(gui, gui.menu, message, error, category);
            }
            @Override
            public void moderateError(String message, Throwable error, ErrorCategory category){
                System.err.println("Moderate "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error");
                logger.log(Level.SEVERE, message, error);
                if(Main.isBot)return;
                gui.menu = new MenuModerateError(gui, gui.menu, message, error, category);
            }
            @Override
            public void severeError(String message, Throwable error, ErrorCategory category){
                System.err.println("Severe "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error");
                logger.log(Level.SEVERE, message, error);
                if(Main.isBot)return;
                gui.menu = new MenuSevereError(gui, gui.menu, message, error, category);
            }
            @Override
            public void criticalError(String message, Throwable error, ErrorCategory category){
                System.err.println("Critical "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error");
                logger.log(Level.SEVERE, message, error);
                if(Main.isBot)return;
                gui.menu = new MenuCriticalError(gui, message, error, category);
            }
        }, null, helper);
    }
    public static void renderInit(){
        System.out.println("Loading fonts");
        FontManager.addFont("/simplelibrary/font");
        FontManager.addFont("/net/ncplanner/plannerator/font/high resolution");
        FontManager.addFont("/net/ncplanner/plannerator/font/small");
        FontManager.addFont("/net/ncplanner/plannerator/font/slim");
        FontManager.addFont("/net/ncplanner/plannerator/font/monospaced");
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
        System.out.println("Initializing GUI");
        gui = new GUI(is3D?GameHelper.MODE_HYBRID:GameHelper.MODE_2D, helper){
            private boolean b;
            private double x,y,o,to;
            @Override
            public synchronized void onCharTyped(char c){
                try{
                    super.onCharTyped(c);
                }catch(Throwable t){
                    Sys.error(ErrorLevel.severe, "Error caught in onCharTyped!", t, ErrorCategory.uncaught);
                }
            }
            @Override
            public synchronized void onFileDropped(String[] files){
                try{
                    super.onFileDropped(files);
                }catch(Throwable t){
                    Sys.error(ErrorLevel.severe, "Error caught in onFileDropped!", t, ErrorCategory.uncaught);
                }
            }
            @Override
            public synchronized void onMouseButton(int button, int action, int mods){
                try{
                    super.onMouseButton(button, action, mods);
                }catch(Throwable t){
                    Sys.error(ErrorLevel.severe, "Error caught in onMouseButton!", t, ErrorCategory.uncaught);
                }
            }
            @Override
            public synchronized void onMouseMoved(double xpos, double ypos){
                try{
                    super.onMouseMoved(xpos, ypos);
                }catch(Throwable t){
                    Sys.error(ErrorLevel.severe, "Error caught in onMouseMoved!", t, ErrorCategory.uncaught);
                }
            }
            @Override
            public synchronized void onMouseScrolled(double xoffset, double yoffset){
                try{
                    super.onMouseScrolled(xoffset, yoffset);
                }catch(Throwable t){
                    Sys.error(ErrorLevel.severe, "Error caught in onMouseScrolled!", t, ErrorCategory.uncaught);
                }
            }
            @Override
            public synchronized void onWindowFocused(boolean focused){
                try{
                    super.onWindowFocused(focused);
                }catch(Throwable t){
                    Sys.error(ErrorLevel.severe, "Error caught in onWindowFocused!", t, ErrorCategory.uncaught);
                }
            }
            @Override
            public synchronized void onKeyEvent(int key, int scancode, int event, int modifiers){
                try{
                    super.onKeyEvent(key, scancode, event, modifiers);
                    if(event==GLFW.GLFW_PRESS&&key==GLFW.GLFW_KEY_C&&Core.isControlPressed()&&Core.isShiftPressed()&&Core.isAltPressed()){
                        throw new RuntimeException("Manually triggered debug error");
                    }
                }catch(Throwable t){
                    Sys.error(ErrorLevel.severe, "Error caught in onKeyEvent!", t, ErrorCategory.uncaught);
                }
            }
            @Override
            public <V extends Menu> V open(Menu menu){
                if(!(menu instanceof MenuTransition)){
                    if(rand.nextDouble()<.0001){
                        to = MathUtil.max(0,MathUtil.min(1,rand.nextGaussian()/3));
                        x = rand.nextDouble()*helper.displayWidth();
                        y = rand.nextDouble()*helper.displayHeight();
                    }
                    else to = 0;
                }else to = 0;
                return super.open(menu);
            }
            @Override
            public synchronized void render(int millisSinceLastTick){
                helper.make2D();
                Renderer renderer = new Renderer();
                o = o*.999+to*.001;
                int min = 1;
                int max = 4;
                for(int i = min; i<=max; i++){
                    GL11.glColor4d(1, 1, 1, ((-1/(max-min))*(i-min)+1)*o);
                    renderer.drawRegularPolygon(x-10, y, i, 10, 0, 0);
                    renderer.drawRegularPolygon(x+10, y, i, 10, 0, 0);
                }
                super.render(millisSinceLastTick);
            }
        };
        gui.open(new MenuInit(gui));
        System.out.println("Render initialization complete!");
    }
    public static void tickInit(){}
    public static void finalInit() throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
        if(Main.headless)GLFW.glfwHideWindow(helper.getWindow());
        System.out.println("Activating GUI");
        helper.assignGUI(gui);
    }
    public static void tick(boolean isLastTick){
        if(!isLastTick){
            if(helper.isKeyDown(GLFW.GLFW_KEY_LEFT))xRot-=2;
            if(helper.isKeyDown(GLFW.GLFW_KEY_RIGHT))xRot+=2;
            if(helper.isKeyDown(GLFW.GLFW_KEY_UP))yRot = MathUtil.min(maxYRot, MathUtil.max(-maxYRot, yRot-2));
            if(helper.isKeyDown(GLFW.GLFW_KEY_DOWN))yRot = MathUtil.min(maxYRot, MathUtil.max(-maxYRot, yRot+2));
            gui.tick();
        }else{
            File f = new File("settings.dat").getAbsoluteFile();
            Config settings = Config.newConfig(f);
            settings.set("theme", theme.name);
            Config modules = Config.newConfig();
            for(Module m : Core.modules){
                modules.set(m.name, m.isActive());
            }
            settings.set("modules", modules);
            settings.set("tutorialShown", tutorialShown);
            settings.set("invertUndoRedo", invertUndoRedo);
            settings.set("autoBuildCasing", autoBuildCasing);
            ConfigList pins = new ConfigList();
            for(String s : pinnedStrs)pins.add(s);
            settings.set("pins", pins);
            settings.save();
            if(Main.isBot){
                Bot.stop();
                System.exit(0);//TODO Shouldn't have to do this! :(
            }
        }
    }
    public static void render(int millisSinceLastTick){
        Renderer renderer = new Renderer();
        if(theme.shouldContantlyUpdateBackground())helper.setBackground(theme.getMenuBackgroundColor());
        if(delCircle&&sourceCircle!=null){
            Core.deleteTexture(sourceCircle);
            Core.deleteTexture(outlineSquare);
            sourceCircle = outlineSquare = null;
            delCircle = false;
        }
        if(sourceCircle==null){
            sourceCircle = Core.makeImage(circleSize, circleSize, (buff) -> {
                renderer.setColor(Color.WHITE);
                renderer.drawCircle(buff.width/2, buff.height/2, buff.width*(4/16d), buff.width*(6/16d));
            });
        }
        if(outlineSquare==null){
            outlineSquare = Core.makeImage(32, 32, (buff) -> {
                renderer.setColor(Color.WHITE);
                double inset = buff.width/32d;
                renderer.fillRect(inset, inset, buff.width-inset, inset+buff.width/16);
                renderer.fillRect(inset, buff.width-inset-buff.width/16, buff.width-inset, buff.width-inset);
                renderer.fillRect(inset, inset+buff.width/16, inset+buff.width/16, buff.width-inset-buff.width/16);
                renderer.fillRect(buff.width-inset-buff.width/16, inset+buff.width/16, buff.width-inset, buff.width-inset-buff.width/16);
            });
        }
        renderer.setWhite();
        if(gui.menu instanceof MenuMain){
            GL11.glPushMatrix();
            GL11.glTranslated(.4, 0, -1.5);
            GL11.glRotated(yRot, 1, 0, 0);
            GL11.glRotated(xRot, 0, 1, 0);
            Multiblock mb = ((MenuMain)gui.menu).getSelectedMultiblock();
            if(mb!=null){
                BoundingBox bbox = mb.getBoundingBox();
                double size = MathUtil.max(bbox.getWidth(), MathUtil.max(bbox.getHeight(), bbox.getDepth()));
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
                    double size = MathUtil.max(bbox.getWidth(), MathUtil.max(bbox.getHeight(), bbox.getDepth()));
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
        Renderer2D.clearBoundStack();
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
    private static final HashMap<Image, Integer> imgs = new HashMap<>();
    private static final HashMap<Image, Boolean> alphas = new HashMap<>();
    public static int getTexture(Image image){
        if(image==null)return -1;
        if(!imgs.containsKey(image)){
            imgs.put(image, ImageStash.instance.allocateAndSetupTexture(image));
        }
        return imgs.get(image);
    }
    public static void deleteTexture(Image image){
        imgs.remove(image);
    }
    public static void setTheme(Theme t){
        t.onSet();
        theme = t;
        str+=t.name.charAt(0);
        if(str.length()>5)str = str.substring(1);
        helper.setBackground(theme.getMenuBackgroundColor());
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
    public static Image makeImage(int width, int height, BufferRenderer r){
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
        Image img = new Image(width, height);
        for(int i=0;i<imgRGBData.length;i++){
            imgRGBData[i]=(f(imgData[i*4])<<16)+(f(imgData[i*4+1])<<8)+(f(imgData[i*4+2]))+(f(imgData[i*4+3])<<24);//DO NOT Use RED, GREEN, or BLUE channel (here BLUE) for alpha data
        }
        img.setRGB(0, 0, width, height, imgRGBData, 0, width);
        if(cull)GL11.glEnable(GL11.GL_CULL_FACE);
        if(depth)GL11.glEnable(GL11.GL_DEPTH_TEST);
        return img;
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
    public static boolean hasAlpha(Image image){
        if(image==null)return false;
        if(!alphas.containsKey(image)){
            boolean hasAlpha = false;
            FOR:for(int x = 0; x<image.getWidth(); x++){
                for(int y = 0; y<image.getHeight(); y++){
                    if(new Color(image.getRGB(x, y)).getAlpha()!=255){
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
    public static boolean openURL(String link){
        Runtime rt = Runtime.getRuntime();
        try{
            switch(Main.os){
                case Main.OS_WINDOWS:
                    rt.exec("rundll32 url.dll,FileProtocolHandler "+link);
                    return true;
                case Main.OS_MACOS:
                    rt.exec("open "+link);
                    return true;
                case Main.OS_LINUX:
                    rt.exec("xdg-open "+link);
                    return true;
                default:
                    Sys.error(ErrorLevel.minor, "Failed to open webpage: Unkown OS\n"+link, null, ErrorCategory.InternetIO);
                    return false;
            }
        }catch(IOException ex){
            Sys.error(ErrorLevel.minor, "Failed to open webpage\n"+link, ex, ErrorCategory.InternetIO);
            return false;
        }
    }
    public static String getCrashReportData(){
        String s = "";
        s+=Core.configuration.getCrashReportData()+"\n";
        s+="Theme: "+theme.getClass().getName()+" "+theme.name+"\n\n";
        s += "GUI menu stack:\n";
        if(gui!=null){
            Menu m = gui.menu;
            if(m==null)s+="null\n";
            while(m!=null){
                s+=m.getClass().getName()+"\n";
                if(m instanceof DebugInfoProvider){
                    s+=DebugInfoProvider.asString(1, ((DebugInfoProvider)m).getDebugInfo(new HashMap<>()));
                }
                m = m.parent;
            }
        }
        return s;
    }
    public static void setWindowTitle(String title){
        GLFW.glfwSetWindowTitle(helper.getWindow(), title);
    }
    public static void resetWindowTitle(){
        GLFW.glfwSetWindowTitle(helper.getWindow(), Main.applicationName+" "+VersionManager.currentVersion);
    }
    public static interface BufferRenderer{
        void render(Framebuffer buff);
    }
    private static int f(byte imgData){
        return (imgData+256)&255;
    }
    public static File lastOpenFolder = new File("file").getAbsoluteFile().getParentFile();
    public static void createFileChooser(Consumer<File> onAccepted, FileFormat format) throws IOException{
        PointerBuffer path = MemoryUtil.memAllocPointer(1);
        String filter = "";
        for(String ext : format.extensions)filter+=","+ext;
        if(!filter.isEmpty())filter = filter.substring(1);
        try{
            int result = NativeFileDialog.NFD_OpenDialog(filter, lastOpenFolder.getAbsolutePath(), path);
            switch(result){
                case NativeFileDialog.NFD_OKAY:
                    String str = path.getStringUTF8();
                    File file = new File(str);
                    onAccepted.accept(file);
                    break;
                case NativeFileDialog.NFD_CANCEL:
                    break;
                default: //NFD_ERROR
                    throw new IOException(NativeFileDialog.NFD_GetError());
            }
        }finally{
            MemoryUtil.memFree(path);
        }
    }
    public static void createFileChooser(File selectedFile, Consumer<File> onAccepted, FileFormat format) throws IOException{
        PointerBuffer path = MemoryUtil.memAllocPointer(1);
        String filter = "";
        for(String ext : format.extensions)filter+=","+ext;
        if(!filter.isEmpty())filter = filter.substring(1);
        try{
            int result = NativeFileDialog.NFD_SaveDialog(filter, lastOpenFolder.getAbsolutePath(), path);
            switch(result){
                case NativeFileDialog.NFD_OKAY:
                    String str = path.getStringUTF8();
                    File file = new File(str);
                    onAccepted.accept(file);
                    break;
                case NativeFileDialog.NFD_CANCEL:
                    break;
                default: //NFD_ERROR
                    throw new IOException(NativeFileDialog.NFD_GetError());
            }
        }finally{
            MemoryUtil.memFree(path);
        }
    }
    public static boolean areImagesEqual(Image img1, Image img2) {
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
    public static void autoSaveAndExit(){
        Throwable error = null;
        int num = 0;
        try{
            num = autosave();
        }catch(Throwable t){error = t;}
        if(error==null){
            System.out.println("Saved to autosave"+num+".ncpf");
        }else{
            System.err.println("Autosave Failed!");
        }
        Main.generateCrashReport("Manually closed on error", null);
        helper.running = false;
    }
    public static int getThemeIndex(MenuComponent comp){
        if(comp.parent instanceof MenuComponentMinimaList)return comp.parent.components.indexOf(comp);
        if(comp.parent instanceof MenuComponentMulticolumnMinimaList)return comp.parent.components.indexOf(comp);
        return 0;
    }
    public static int getThemeIndex(VRMenuComponent comp){
        if(comp.parent instanceof VRMenuComponentSpecialPanel)return comp.parent.components.indexOf(comp);
        if(comp.parent instanceof VRMenuComponentToolPanel)return comp.parent.components.indexOf(comp);
        if(comp.parent instanceof VRMenuComponentMultiblockSettingsPanel)return comp.parent.components.indexOf(comp);
        return 0;
    }
}
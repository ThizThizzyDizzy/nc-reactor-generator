package net.ncplanner.plannerator.planner;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.discord.Bot;
import net.ncplanner.plannerator.graphics.Font;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.Shader;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
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
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.MenuCalibrateCursor;
import net.ncplanner.plannerator.planner.gui.menu.MenuInit;
import net.ncplanner.plannerator.planner.gui.menu.component.MulticolumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuCriticalError;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuDialog;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuError;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuUnsavedChanges;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuWarningMessage;
import net.ncplanner.plannerator.planner.module.Module;
import net.ncplanner.plannerator.planner.theme.Theme;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentMultiblockSettingsPanel;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentSpecialPanel;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentToolPanel;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.openvr.VR;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NativeFileDialog;
public class Core{
    public static Logger logger = Logger.getLogger(Core.class.getName());
    public static GUI gui;
    public static ArrayList<Long> FPStracker = new ArrayList<>();
    public static boolean debugMode = false;
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
    public static final HashMap<String, Integer> overlays = new HashMap<>();
    public static boolean vr = false;
    private static Callback glCallback;
    public static boolean invertUndoRedo;
    public static boolean autoBuildCasing = true;
    public static boolean vsync = true;
    public static boolean recoveryMode = false;
    public static boolean editor3dView = false;
    public static String filename; //saved filename to default to when saving
    public static final ArrayList<String> pinnedStrs = new ArrayList<>();
    private static Random rand = new Random();
    public static String str = "";
    public static long window = 0;
    public static double lastFrame = -1;
    private static int screenWidth = 1, screenHeight = 1;
    public static Font FONT_20;
    public static Font FONT_40;
    public static Font FONT_10;
    public static Font FONT_MONO_20;
    private static boolean is3D = false;
    public static boolean imageExport3DView = true;
    public static boolean imageExportCasing = true;
    public static boolean imageExportCasing3D = true;
    public static boolean imageExportCasingParts = true;
    public static boolean saved = true;
    public static boolean dssl = false;
    public static boolean rememberConfig;
    public static String lastLoadedConfig = null;
    public static void addModule(Module m){
        modules.add(m);
    }
    public static void resetMetadata(){
        metadata.clear();
        metadata.put("Name", "");
        metadata.put("Author", "");
    }
    public static void main(String[] args) throws NoSuchMethodException{
        if(Main.novr){
            System.out.println("Skipping VR runtime");
        }else{
            System.out.println("Checking for VR runtime");
            if(VR.VR_IsRuntimeInstalled()&&VR.VR_IsHmdPresent()){
                vr = true;
                System.out.println("VR runtime found!");
            }
        }
        if(Main.isBot){
            System.out.println("Loading discord bot");
            Bot.start(args);
        }
        System.out.println("Initializing GLFW");
        if(!glfwInit())throw new RuntimeException("Failed to initialize GLFW!");
        glfwSetErrorCallback(new GLFWErrorCallbackI() {
            @Override
            public void invoke(int error, long description){
                String desc = MemoryUtil.memUTF8(description);
                System.err.println("GLFW ERROR "+error+": "+desc);//TODO proper error handling
            }
        });
        System.out.println("Initializing window");
        //window
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //multisampling
        glfwWindowHint(GLFW_STENCIL_BITS, 4);
        glfwWindowHint(GLFW_SAMPLES, 4);
        //openGL
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        if(Main.headless)glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        System.out.println("Creating window");
        window = glfwCreateWindow(1200/(Main.isBot?10:1), 700/(Main.isBot?10:1), "Nuclearcraft Reactor Plannerator "+VersionManager.currentVersion, 0, 0);
        if(window==0){
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window!");
        }
        System.out.println("Loading Icon");
        GLFWImage.Buffer iconBuffer = GLFWImage.create(1);
        GLFWImage icon = GLFWImage.create();
        ByteBuffer imageData = null;
        IntBuffer iconWidth = BufferUtils.createIntBuffer(1);
        IntBuffer iconHeight = BufferUtils.createIntBuffer(1);
        try(InputStream input = getInputStream("/textures/icon.png")){
            imageData = stbi_load_from_memory(loadData(input), iconWidth, iconHeight, BufferUtils.createIntBuffer(1), 4);
        }catch(IOException ex){
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(imageData==null)throw new RuntimeException("Failed to load image: "+stbi_failure_reason());
        icon.set(iconWidth.get(0), iconHeight.get(0), imageData);
        iconBuffer.put(icon);
        iconBuffer.rewind();
        glfwSetWindowIcon(window, iconBuffer);
        System.out.println("Initializing Console interface");
        Thread console = new Thread(() -> {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
                while(!glfwWindowShouldClose(window)){
                    String line = reader.readLine();
                    switch(line.trim()){
                        case "fps":
                            System.out.println("FPS: "+getFPS());
                            break;
                    }
                }
            }catch(IOException ex){}
        });
        console.setName("Console interface thread");
        console.setDaemon(true);
        console.start();

        glfwMakeContextCurrent(window);
        glfwSwapInterval(vsync?1:0);
        int[] ww = new int[1];
        int[] wh = new int[1];
        glfwGetFramebufferSize(window, ww, wh);
        screenWidth = ww[0];
        screenHeight = wh[0];
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            screenWidth = width;
            screenHeight = height;
            glViewport(0, 0, width, height);
        });
        GL.createCapabilities();
        
        System.out.println("Initializing render engine");
        glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        glEnable(GL_MULTISAMPLE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        if(debugMode){
            System.out.println("Creating GL Debug Callback");
            glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
            glCallback = GLUtil.setupDebugMessageCallback();
        }
        System.out.println("Loading fonts");
        FONT_20 = Font.loadFont("standard");
        FONT_40 = Font.loadFont("high_resolution");
        FONT_10 = Font.loadFont("small");
        FONT_MONO_20 = Font.loadFont("monospaced");
        System.out.println("Initializing elements");
        Renderer.initElements();
        System.out.println("Initializing GUI");
        gui = new GUI(window){
            private boolean b;
            private float x,y,o,to;
            @Override
            public void render2d(double deltaTime){
                Renderer renderer = new Renderer();
                o = o*.999f+to*.001f;
                int min = 1;
                int max = 4;
                for(int i = min; i<=max; i++){
                    renderer.setColor(1, 1, 1, ((-1/(max-min))*(i-min)+1)*o);
                    renderer.drawRegularPolygon(x-10, y, i, 10, 0);
                    renderer.drawRegularPolygon(x+10, y, i, 10, 0);
                }
                super.render2d(deltaTime);
            }
            @Override
            public int getWidth(){
                return (int) (screenWidth/MenuCalibrateCursor.xGUIScale);
            }
            @Override
            public int getHeight(){
                return (int) (screenHeight/MenuCalibrateCursor.yGUIScale);
            }
        };
        gui.open(new MenuInit(gui));
        System.out.println("Render initialization complete!");
        
        Shader shader = new Shader("vert.shader", "frag.shader");
        
        stbi_set_flip_vertically_on_load(true);
        Renderer renderer = new Renderer();
        gui.initInput();
        while(true){
            boolean shouldClose = glfwWindowShouldClose(window);
            if(shouldClose){
                if(saved)break;
                else{
                    if(gui.menu instanceof MenuUnsavedChanges)break;//clicked close twice, might as well listen this time
                    glfwSetWindowShouldClose(window, false);
                    new MenuUnsavedChanges(gui, gui.menu).open();
                }
            }
            Matrix4f orthoProjection = new Matrix4f().setOrtho(0, (screenWidth/(float)MenuCalibrateCursor.xGUIScale), (screenHeight/(float)MenuCalibrateCursor.yGUIScale), 0, 0.1f, 10f);//new Matrix4f().setPerspective(45, screenWidth/screenHeight, 0.1f, 100);
            Matrix4f perspectiveProjection = new Matrix4f().setPerspective(45, (screenWidth/(float)MenuCalibrateCursor.xGUIScale)/Math.max(1f,(screenHeight/(float)MenuCalibrateCursor.yGUIScale)), 0.1f, 100);
            Color color = theme.getMenuBackgroundColor();
            glClearColor(0, 0, 0, 0);
            glStencilMask(0xff);
            glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
            glStencilMask(0x00);
            glClearColor(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f);
            glClear(GL_COLOR_BUFFER_BIT);
            double dt = 0;
            double time = glfwGetTime();
            if(lastFrame>-1){
                dt = time-lastFrame;
            }
            lastFrame = time;
            renderer.setShader(shader);
            Matrix4f modelMatrix = new Matrix4f();//.setTranslation(0, 0, 0).setRotationXYZ(0, 0, 0);
            Matrix4f viewMatrix = new Matrix4f().setTranslation(0, 0, -5);
            renderer.model(modelMatrix);
            renderer.view(viewMatrix);
            renderer.projection(perspectiveProjection);
            is3D = true;
            try{
                render3d(renderer, dt);
            }catch(Throwable t){
                error("Caught exception rendering 3D background!", t);
            }
            //DRAW GUI
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            renderer.projection(orthoProjection);
            is3D = false;
            try{
                render2d(renderer, dt);
            }catch(Throwable t){
                error("Caught exception rendering GUI!", t);
            }
            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);
            renderer.clearTranslationsAndBounds();
            
            FPStracker.add(System.currentTimeMillis());
            while(FPStracker.get(0)<System.currentTimeMillis()-5_000){
                FPStracker.remove(0);
            }
            
            glfwSwapBuffers(window);
            try{
                glfwPollEvents();
            }catch(Throwable t){
                error("Caught exception processing input!", t);
            }
        }
        Renderer.cleanupElements();
        
        glfwDestroyWindow(window);
        glfwTerminate();
        
        File f = new File("settings.dat").getAbsoluteFile();
        Config settings = Config.newConfig(f);
        settings.set("theme", theme.name);
        Config modules = Config.newConfig();
        for(Module m : Core.modules){
            modules.set(m.name, m.isActive());
        }
        settings.set("modules", modules);
        Config overlays = Config.newConfig();
        for(String key : Core.overlays.keySet()){
            overlays.set(key, Core.overlays.get(key));
        }
        settings.set("overlays", overlays);
        settings.set("tutorialShown", tutorialShown);
        settings.set("invertUndoRedo", invertUndoRedo);
        settings.set("autoBuildCasing", autoBuildCasing);
        settings.set("vsync", vsync);
        settings.set("editor3dView", editor3dView);
        settings.set("imageExport3DView", imageExport3DView);
        settings.set("imageExportCasing", imageExportCasing);
        settings.set("imageExportCasing3D", imageExportCasing3D);
        settings.set("imageExportCasingParts", imageExportCasingParts);
        settings.set("dssl", dssl);
        settings.set("rememberConfig", rememberConfig);
        settings.set("lastLoadedConfig", lastLoadedConfig);
        Config cursor = Config.newConfig();
        cursor.set("xMult", MenuCalibrateCursor.xMult);
        cursor.set("yMult", MenuCalibrateCursor.yMult);
        cursor.set("xGUIScale", MenuCalibrateCursor.xGUIScale);
        cursor.set("yGUIScale", MenuCalibrateCursor.yGUIScale);
        cursor.set("xOff", MenuCalibrateCursor.xOff);
        cursor.set("yOff", MenuCalibrateCursor.yOff);
        settings.set("cursor", cursor);
        ConfigList pins = new ConfigList();
        for(String s : pinnedStrs)pins.add(s);
        settings.set("pins", pins);
        settings.save();
        if(debugMode)glCallback.free();
        if(Main.isBot){
            Bot.stop();
            System.exit(0);//TODO Shouldn't have to do this! :(
        }
    }
    public static void render3d(Renderer renderer, double deltaTime){
        renderer.setWhite();
        gui.render3d(deltaTime);
    }
    public static void render2d(Renderer renderer, double deltaTime){
        renderer.setWhite();
        if(delCircle&&sourceCircle!=null){
            Core.deleteTexture(sourceCircle);
            Core.deleteTexture(outlineSquare);
            sourceCircle = outlineSquare = null;
            delCircle = false;
        }
        if(sourceCircle==null){
            sourceCircle = Core.makeImage(circleSize, circleSize, (bufferRenderer, bufferWidth, bufferHeight) -> {
                bufferRenderer.setColor(Color.WHITE);
                bufferRenderer.drawCircle(bufferWidth/2, bufferHeight/2, bufferWidth*(4/16f), bufferWidth*(6/16f));
            });
        }
        if(outlineSquare==null){
            outlineSquare = Core.makeImage(32, 32, (bufferRenderer, bufferWidth, bufferHeight) -> {
                bufferRenderer.setColor(Color.WHITE);
                float inset = bufferWidth/32f;
                bufferRenderer.fillRect(inset, inset, bufferWidth-inset, inset+bufferWidth/16);
                bufferRenderer.fillRect(inset, bufferWidth-inset-bufferWidth/16, bufferWidth-inset, bufferWidth-inset);
                bufferRenderer.fillRect(inset, inset+bufferWidth/16, inset+bufferWidth/16, bufferWidth-inset-bufferWidth/16);
                bufferRenderer.fillRect(bufferWidth-inset-bufferWidth/16, inset+bufferWidth/16, bufferWidth-inset, bufferWidth-inset-bufferWidth/16);
            });
        }
        gui.render2d(deltaTime);
    }
    public static long getFPS(){
        return FPStracker.size()/5;
    }
    private static final HashMap<Image, Integer> imgs = new HashMap<>();
    private static final HashMap<Image, Boolean> alphas = new HashMap<>();
    public static int getTexture(Image image){
        if(image==null)return 0;
        if(!imgs.containsKey(image)){
            imgs.put(image, loadTexture(image.getWidth(), image.getHeight(), image.getGLData()));
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
    }
    public static boolean isAltPressed(){
        return glfwGetKey(window, GLFW_KEY_LEFT_ALT)==GLFW_PRESS||glfwGetKey(window, GLFW_KEY_RIGHT_ALT)==GLFW_PRESS;
    }
    public static boolean isControlPressed(){
        return glfwGetKey(window, GLFW_KEY_LEFT_CONTROL)==GLFW_PRESS||glfwGetKey(window, GLFW_KEY_RIGHT_CONTROL)==GLFW_PRESS;
    }
    public static boolean isShiftPressed(){
        return glfwGetKey(window, GLFW_KEY_LEFT_SHIFT)==GLFW_PRESS||glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT)==GLFW_PRESS;
    }
    public static Image makeImage(int width, int height, BufferRenderer r){
        boolean cull = glIsEnabled(GL_CULL_FACE);
        boolean depth = glIsEnabled(GL_DEPTH_TEST);
        if(cull)glDisable(GL_CULL_FACE);
        if(depth)glDisable(GL_DEPTH_TEST);
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(width*height*4);
        
        int framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        
        int textureColorBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureColorBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorBuffer, 0);
        
        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if(status!=GL_FRAMEBUFFER_COMPLETE)throw new RuntimeException("Could not create FBO: "+status);
        
        glViewport(0, 0, width, height);
        glClearColor(0f, 0f, 0f, 0f);
        glStencilMask(0xff);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glStencilMask(0x00);
        
        Renderer renderer = new Renderer();
        renderer.projection(new Matrix4f().setOrtho(0, width, height, 0, 0.1f, 10f));
        
        r.render(renderer, width, height);
        
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        glViewport(0, 0, screenWidth, screenHeight);
        
        if(is3D)renderer.projection(new Matrix4f().setPerspective(45, screenWidth/(float)screenHeight, 0.1f, 100));
        else renderer.projection(new Matrix4f().setOrtho(0, screenWidth, screenHeight, 0, 0.1f, 10f));
        
        glDeleteFramebuffers(framebuffer);
        glDeleteBuffers(rbo);
        glDeleteTextures(textureColorBuffer);
        
        int[] imgRGBData = new int[width*height];
        byte[] imgData = new byte[width*height*4];
        imageBuffer.rewind();
        imageBuffer.get(imgData);
        Image img = new Image(width, height);
        for(int i=0;i<imgRGBData.length;i++){
            imgRGBData[i]=(f(imgData[i*4])<<16)+(f(imgData[i*4+1])<<8)+(f(imgData[i*4+2]))+(f(imgData[i*4+3])<<24);//DO NOT Use RED, GREEN, or BLUE channel (here BLUE) for alpha data
        }
        img.setRGB(0, 0, width, height, imgRGBData, 0, width);
        if(cull)glEnable(GL_CULL_FACE);
        if(depth)glEnable(GL_DEPTH_TEST);
        return img.flip();
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
                    throw new RuntimeException("Failed to open webpage: Unkown OS\n"+link);
            }
        }catch(IOException ex){
            throw new RuntimeException("Failed to open webpage\n"+link, ex);
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
        glfwSetWindowTitle(window, title);
    }
    public static void resetWindowTitle(){
        glfwSetWindowTitle(window, "Nuclearcraft Reactor Plannerator "+VersionManager.currentVersion);
    }
    public static void setVsync(boolean vs){
        if(vsync!=vs)glfwSwapInterval(vs?1:0);
        vsync = vs;
    }
    public static interface BufferRenderer{
        void render(Renderer renderer, int width, int height);
    }
    private static int f(byte imgData){
        return (imgData+256)&255;
    }
    public static File defaultFolder = new File("file").getAbsoluteFile().getParentFile();
    public static HashMap<String, File> lastFolders = new HashMap<>();
    /**
     * OPEN
     */
    public static void createFileChooser(Consumer<File> onAccepted, FileFormat format) throws IOException{
        createFileChooser(onAccepted, format, "");
    }
    /**
     * OPEN
     */
    public static void createFileChooser(Consumer<File> onAccepted, FileFormat format, String hint) throws IOException{
        hint = "OPEN_"+hint;
        PointerBuffer path = MemoryUtil.memAllocPointer(1);
        String filter = "";
        for(String ext : format.extensions)filter+=","+ext;
        if(!filter.isEmpty())filter = filter.substring(1);
        try{
            int result = NativeFileDialog.NFD_OpenDialog(filter, lastFolders.getOrDefault(hint, defaultFolder).getAbsolutePath(), path);
            switch(result){
                case NativeFileDialog.NFD_OKAY:
                    String str = path.getStringUTF8();
                    File file = new File(str);
                    lastFolders.put(hint, file.getAbsoluteFile().getParentFile());
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
    /**
     * SAVE
     */
    public static void createFileChooser(File selectedFile, Consumer<File> onAccepted, FileFormat format) throws IOException{
        createFileChooser(selectedFile, onAccepted, format, "");
    }
    /**
     * SAVE
     */
    public static void createFileChooser(File selectedFile, Consumer<File> onAccepted, FileFormat format, String hint) throws IOException{
        hint = "SAVE_"+hint;
        PointerBuffer path = MemoryUtil.memAllocPointer(1);
        String filter = "";
        for(String ext : format.extensions)filter+=","+ext;
        if(!filter.isEmpty())filter = filter.substring(1);
        try{
            int result = NativeFileDialog.NFD_SaveDialog(filter, lastFolders.getOrDefault(hint, defaultFolder).getAbsolutePath(), path);
            switch(result){
                case NativeFileDialog.NFD_OKAY:
                    String str = path.getStringUTF8();
                    File file = new File(str);
                    lastFolders.put(hint, file.getAbsoluteFile().getParentFile());
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
        glfwSetWindowShouldClose(window, true);
    }
    public static int getThemeIndex(Component comp){
        if(comp.parent instanceof SingleColumnList)return comp.parent.components.indexOf(comp);
        if(comp.parent instanceof MulticolumnList)return comp.parent.components.indexOf(comp);
        if(comp.parent instanceof MenuDialog)return ((MenuDialog)comp.parent).buttons.indexOf(comp);
        return 0;
    }
    public static int getThemeIndex(VRMenuComponent comp){
        if(comp.parent instanceof VRMenuComponentSpecialPanel)return comp.parent.components.indexOf(comp);
        if(comp.parent instanceof VRMenuComponentToolPanel)return comp.parent.components.indexOf(comp);
        if(comp.parent instanceof VRMenuComponentMultiblockSettingsPanel)return comp.parent.components.indexOf(comp);
        return 0;
    }
    public static InputStream getInputStream(String path){
        if(!path.startsWith("/"))path = "/"+path;
        return Core.class.getResourceAsStream(path);
    }
    public static ByteBuffer loadData(String path){
        return loadData(getInputStream(path));
    }
    public static ByteBuffer loadData(InputStream input){
        try(ByteArrayOutputStream output = new ByteArrayOutputStream()){
            int b;
            while((b = input.read())!=-1){
                output.write(b);
            }
            output.close();
            byte[] data = output.toByteArray();
            ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
            buffer.put(data);
            buffer.flip();
            return buffer;
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    private static HashMap<String, Integer> texturesCache = new HashMap<>();
    public static int loadTexture(String path){
        if(texturesCache.containsKey(path))return texturesCache.get(path);
        //read image
        ByteBuffer imageData = null;
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        try(InputStream input = getInputStream(path)){
            imageData = stbi_load_from_memory(loadData(input), width, height, BufferUtils.createIntBuffer(1), 4);
        }catch(IOException ex){
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(imageData==null)throw new RuntimeException("Failed to load image: "+stbi_failure_reason());
        //finish read image
        int texture = loadTexture(width.get(0), height.get(0), imageData);
        stbi_image_free(imageData);
        texturesCache.put(path, texture);
        return texture;
    }
    public static int loadTexture(int width, int height, ByteBuffer imageData){
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
        glGenerateMipmap(GL_TEXTURE_2D);
        return texture;
    }
    public static void warning(String message, Throwable error){
        System.err.println("Warning:");
        logger.log(Level.WARNING, message, error);
        if(Main.isBot)return;
        new MenuWarningMessage(gui, gui.menu, message, error).open();
    }
    public static void error(String message, Throwable error){
        System.err.println("Severe Error");
        logger.log(Level.SEVERE, message, error);
        if(Main.isBot)return;
        new MenuError(gui, gui.menu, message, error).open();
    }
    public static void criticalError(String message, Throwable error){
        System.err.println("Critical Error");
        logger.log(Level.SEVERE, message, error);
        if(Main.isBot)return;
        new MenuCriticalError(gui, message, error).open();
    }
}
package net.ncplanner.plannerator.planner;
import com.thizthizzydizzy.dizzyengine.DizzyEngine;
import com.thizthizzydizzy.dizzyengine.ResourceManager;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import com.thizthizzydizzy.dizzyengine.graphics.image.Image;
import com.thizthizzydizzy.dizzyengine.graphics.text.Font;
import com.thizthizzydizzy.dizzyengine.logging.Logger;
import com.thizthizzydizzy.dizzyengine.ui.FlatUI;
import com.thizthizzydizzy.dizzyengine.ui.component.Component;
import com.thizthizzydizzy.dizzyengine.ui.component.Panel;
import com.thizthizzydizzy.dizzyengine.ui.layout.ListLayout;
import com.thizthizzydizzy.dizzyengine.updater.DizzyUpdater;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.ncpf.structure.NcpfRoot;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.discord.Bot;
import net.ncplanner.plannerator.graphics.PlanneratorRenderer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.configuration.CannedConfiguration;
import net.ncplanner.plannerator.planner.configuration.ConfigurationManager;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.gui.menu.MenuCalibrateCursor;
import net.ncplanner.plannerator.planner.gui.menu.MenuInit;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuCriticalError;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuDialog;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuError;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuUnsavedChanges;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuWarningMessage;
import net.ncplanner.plannerator.planner.module.Module;
import net.ncplanner.plannerator.planner.theme.Theme;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.ui.component.layer.ComponentBackgroundLayer;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentMultiblockSettingsPanel;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentSpecialPanel;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentToolPanel;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.openvr.VR;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NativeFileDialog;
public class Core{
    public static DizzyUpdater updater = new DizzyUpdater(Core.class);
    public static boolean debugMode = false;

    public static final ArrayList<Multiblock> multiblocks = new ArrayList<>();
    public static final ArrayList<Multiblock> multiblockTypes = new ArrayList<>();
    public static NcpfRoot project = new NcpfRoot();
    public static final HashMap<String, String> metadata = new HashMap<>();

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
    @Deprecated //TODO move to DizzyEngine
    public static boolean vsync = true;
    public static boolean recoveryMode = false;
    public static boolean editor3dView = false;
    public static String filename; //saved filename to default to when saving
    public static final ArrayList<String> pinnedStrs = new ArrayList<>();
    private static Random rand = new Random();
    public static String str = "";
    public static Font FONT_20;
    public static Font FONT_40;
    public static Font FONT_10;
    public static Font FONT_MONO_20;
    public static boolean imageExport3DView = true;
    public static boolean imageExportCasing = true;
    public static boolean imageExportCasing3D = true;
    public static boolean imageExportCasingParts = true;
    public static boolean saved = true;
    public static boolean dssl = false;
    public static boolean rememberConfig;
    public static boolean mainMenu3dView = true;
    public static String lastLoadedConfig = null;
    public static void addModule(Module m){
        modules.add(m);
    }
    public static void resetMetadata(){
        metadata.clear();
        metadata.put("Name", "");
        metadata.put("Author", "");
    }
    public static void main(String[] args){
        if(Main.novr){
            Logger.info("Skipping VR runtime");
        }else{
            Logger.info("Checking for VR runtime");
            if(VR.VR_IsRuntimeInstalled()&&VR.VR_IsHmdPresent()){
                vr = true;
                Logger.info("VR runtime found!");
            }
        }
        if(Main.isBot){
            Logger.info("Loading discord bot");
            Bot.start(args);
        }
        Logger.info("Initializing NFD");
        NativeFileDialog.NFD_Init();
        PlanneratorRenderer.addCustomElements();
        DizzyEngine.init("NuclearCraft Plannerator "+updater.currentVersion);
        Logger.info("Loading Icon");
        GLFWImage.Buffer iconBuffer = GLFWImage.create(1);
        GLFWImage icon = GLFWImage.create();
        ByteBuffer imageData = null;
        IntBuffer iconWidth = BufferUtils.createIntBuffer(1);
        IntBuffer iconHeight = BufferUtils.createIntBuffer(1);
        try(InputStream input = getInputStream("/textures/icon.png")){
            imageData = stbi_load_from_memory(loadData(input), iconWidth, iconHeight, BufferUtils.createIntBuffer(1), 4);
        }catch(IOException ex){
            Logger.error(ex);
        }
        if(imageData==null)
            throw new RuntimeException("Failed to load image: "+stbi_failure_reason());
        icon.set(iconWidth.get(0), iconHeight.get(0), imageData);
        iconBuffer.put(icon);
        iconBuffer.rewind();
        glfwSetWindowIcon(DizzyEngine.window, iconBuffer);
//        glfwSwapInterval(vsync?1:0);
        if(debugMode){
            System.out.println("Creating GL Debug Callback");
            glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
            glCallback = GLUtil.setupDebugMessageCallback();
        }
        System.out.println("Loading fonts");
        FONT_20 = Font.loadFont(ResourceManager.loadData(ResourceManager.getInternalResource("/assets/fonts/standard.ttf")));
        FONT_40 = Font.loadFont(ResourceManager.loadData(ResourceManager.getInternalResource("/assets/fonts/high_resolution.ttf")));
        FONT_10 = Font.loadFont(ResourceManager.loadData(ResourceManager.getInternalResource("/assets/fonts/small.ttf")));
        FONT_MONO_20 = Font.loadFont(ResourceManager.loadData(ResourceManager.getInternalResource("/assets/fonts/monospaced.ttf")));
        FlatUI ui = new FlatUI();
        ui.setDefaultComponentBackground(ComponentBackgroundLayer::new);
        DizzyEngine.addLayer(ui).open(new MenuInit());

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            error("Uncaught Exception in Thread "+t.getName()+"!", e);
        });

        DizzyEngine.addCloseHook(() -> {
            if(saved)return;
            if(DizzyEngine.getLayer(FlatUI.class).menu instanceof MenuUnsavedChanges)
                return;//clicked close twice, might as well listen this time
            glfwSetWindowShouldClose(DizzyEngine.window, false);
            new MenuUnsavedChanges().open();
        });
        DizzyEngine.addShutdownHook(() -> {
            try{
                Core.autosave();
                Logger.info("Autosave successful!");
            }catch(Exception ex){
                Logger.error("Autosave failed!", ex);
            }
        });
        DizzyEngine.addShutdownHook(() -> {
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
            settings.set("mainMenu3dView", mainMenu3dView);
            if(lastLoadedConfig!=null)
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
        });
        DizzyEngine.start();
        if(debugMode)glCallback.free();
        if(Main.isBot){
            Bot.stop();
            System.exit(0);//TODO Shouldn't have to do this! :(
        }
    }
    public static void render2d(Renderer renderer, double deltaTime){
        renderer.setColor(Color.WHITE);
        if(delCircle&&sourceCircle!=null){
            Core.deleteTexture(sourceCircle);
            Core.deleteTexture(outlineSquare);
            sourceCircle = outlineSquare = null;
            delCircle = false;
        }
        if(sourceCircle==null){
            sourceCircle = Core.makeImage(circleSize, circleSize, (bufferWidth, bufferHeight) -> {
                Renderer.setColor(Color.WHITE);
                Renderer.fillHollowRegularPolygon(bufferWidth/2, bufferHeight/2, 24, bufferWidth*(4/16f), bufferWidth*(6/16f));
            });
        }
        if(outlineSquare==null){
            outlineSquare = Core.makeImage(32, 32, (bufferWidth, bufferHeight) -> {
                Renderer.setColor(Color.WHITE);
                float inset = bufferWidth/32f;
                Renderer.fillRect(inset, inset, bufferWidth-inset, inset+bufferWidth/16);
                Renderer.fillRect(inset, bufferWidth-inset-bufferWidth/16, bufferWidth-inset, bufferWidth-inset);
                Renderer.fillRect(inset, inset+bufferWidth/16, inset+bufferWidth/16, bufferWidth-inset-bufferWidth/16);
                Renderer.fillRect(bufferWidth-inset-bufferWidth/16, inset+bufferWidth/16, bufferWidth-inset, bufferWidth-inset-bufferWidth/16);
            });
        }
//        gui.render2d(deltaTime);
        if(Main.isBot)Bot.render2D();
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
        str += t.name.charAt(0);
        if(str.length()>5)str = str.substring(1);
    }
    public static boolean isAltPressed(){
        return glfwGetKey(DizzyEngine.window, GLFW_KEY_LEFT_ALT)==GLFW_PRESS||glfwGetKey(DizzyEngine.window, GLFW_KEY_RIGHT_ALT)==GLFW_PRESS;
    }
    public static boolean isControlPressed(){
        return glfwGetKey(DizzyEngine.window, GLFW_KEY_LEFT_CONTROL)==GLFW_PRESS||glfwGetKey(DizzyEngine.window, GLFW_KEY_RIGHT_CONTROL)==GLFW_PRESS;
    }
    public static boolean isShiftPressed(){
        return glfwGetKey(DizzyEngine.window, GLFW_KEY_LEFT_SHIFT)==GLFW_PRESS||glfwGetKey(DizzyEngine.window, GLFW_KEY_RIGHT_SHIFT)==GLFW_PRESS;
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
        if(status!=GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Could not create FBO: "+status);

        glViewport(0, 0, width, height);
        glClearColor(0f, 0f, 0f, 0f);
        glStencilMask(0xff);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
        glStencilMask(0x00);

        Renderer.setTemporaryProjection(new Matrix4f().setOrtho(0, width, height, 0, 0.1f, 10f));

        r.render(width, height);

        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glViewport(0, 0, DizzyEngine.screenSize.x, DizzyEngine.screenSize.y);

        Renderer.restoreProjection();

        glDeleteFramebuffers(framebuffer);
        glDeleteBuffers(rbo);
        glDeleteTextures(textureColorBuffer);

        int[] imgRGBData = new int[width*height];
        byte[] imgData = new byte[width*height*4];
        ((Buffer)imageBuffer).rewind();
        imageBuffer.get(imgData);
        Image img = new Image(width, height);
        for(int i = 0; i<imgRGBData.length; i++){
            imgRGBData[i] = (f(imgData[i*4])<<16)+(f(imgData[i*4+1])<<8)+(f(imgData[i*4+2]))+(f(imgData[i*4+3])<<24);//DO NOT Use RED, GREEN, or BLUE channel (here BLUE) for alpha data
        }
        img.setRGB(0, 0, width, height, imgRGBData, 0, width);
        if(cull)glEnable(GL_CULL_FACE);
        if(depth)glEnable(GL_DEPTH_TEST);
        return img.flip();
    }
    public static void refreshModules(){
        refreshModules(new Task(""));
    }
    public static void refreshModules(Task task){
        Task clean = task.addSubtask("Cleaning up");
        Task nt = task.addSubtask("Registering NCPF objects");
        Task mt = task.addSubtask("Adding multiblock types");
        Task tt = task.addSubtask("Adding Tutorials");
        Task ct = task.addSubtask("Adding configurations");

        multiblockTypes.clear();
        Tutorial.init();
        ConfigurationManager.clearConfigurations();
        clean.finish();

        ArrayList<Module> activeModules = new ArrayList<>();
        for(Module m : modules)if(m.isActive())activeModules.add(m);
        {
            ArrayList<Task> moduleTasks = new ArrayList<>();
            for(Module m : activeModules){
                moduleTasks.add(nt.addSubtask(m.name));
            }
            for(int i = 0; i<activeModules.size(); i++){
                Module m = activeModules.get(i);
                Task t = moduleTasks.get(i);
                m.registerNCPF();
                t.finish();
            }
        }
        nt.finish();
        {
            ArrayList<Task> moduleTasks = new ArrayList<>();
            for(Module m : activeModules){
                moduleTasks.add(mt.addSubtask(m.name));
            }
            for(int i = 0; i<activeModules.size(); i++){
                Module m = activeModules.get(i);
                Task t = moduleTasks.get(i);
                m.addMultiblockTypes(multiblockTypes);
                t.finish();
            }
        }
        mt.finish();
        {
            ArrayList<Task> moduleTasks = new ArrayList<>();
            for(Module m : activeModules){
                moduleTasks.add(tt.addSubtask(m.name));
            }
            for(int i = 0; i<activeModules.size(); i++){
                Module m = activeModules.get(i);
                Task t = moduleTasks.get(i);
                m.addTutorials();
                t.finish();
            }
        }
        tt.finish();
        {
            ArrayList<Task> moduleTasks = new ArrayList<>();
            for(Module m : activeModules){
                moduleTasks.add(ct.addSubtask(m.name));
            }
            for(int i = 0; i<activeModules.size(); i++){
                Module m = activeModules.get(i);
                Task t = moduleTasks.get(i);
                m.addConfigurations(ct);
                t.finish();
            }
        }
        ct.finish();
        task.finish();
    }
    public static boolean hasAlpha(Image image){
        if(image==null)return false;
        if(!alphas.containsKey(image)){
            boolean hasAlpha = false;
            FOR:
            for(int x = 0; x<image.getWidth(); x++){
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
    public static int autosave() throws IOException{
        File file = new File("autosave.ncpf.json");
        int num = 1;
        while(file.exists()){
            file = new File("autosave"+num+".ncpf.json");
            num++;
        }
        NcpfJsonConverter.writeJson(project, file);
        return num;
    }
    public static boolean openURL(String link){
        Runtime rt = Runtime.getRuntime();
        try{
            switch(DizzyUpdater.identifyOperatingSystem()){
                case WINDOWS:
                    rt.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", link});
                    return true;
                case MACOS:
                    rt.exec(new String[]{"open", link});
                    return true;
                case LINUX:
                    rt.exec(new String[]{"xdg-open", link});
                    return true;
                default:
                    throw new RuntimeException("Failed to open webpage: Unknown OS\n"+link);
            }
        }catch(IOException ex){
            throw new RuntimeException("Failed to open webpage\n"+link, ex);
        }
    }
    public static boolean openFolder(File dir){
        Runtime rt = Runtime.getRuntime();
        try{
            switch(DizzyUpdater.identifyOperatingSystem()){
                case WINDOWS:
                    rt.exec(new String[]{"explorer", dir.getAbsolutePath()});
                    return true;
                case MACOS:
                    rt.exec(new String[]{"open", dir.getAbsolutePath()});
                    return true;
                case LINUX:
                    rt.exec(new String[]{"xdg-open", dir.getAbsolutePath()});
                    return true;
                default:
                    throw new RuntimeException("Failed to open folder: Unknown OS\n"+dir.getAbsolutePath());
            }
        }catch(IOException ex){
            throw new RuntimeException("Failed to open folder\n"+dir.getAbsolutePath(), ex);
        }
    }
    public static String getCrashReportData(){
        String s = "";
//        s += Core.project.getCrashReportData()+"\n";
        s += "Theme: "+theme.getClass().getName()+" "+theme.name+"\n\n";
        s += "GUI menu stack:\n";
//        if(gui!=null){
//            Menu m = gui.menu;
//            if(m==null)s += "null\n";
//            while(m!=null){
//                s += m.getClass().getName()+"\n";
//                if(m instanceof DebugInfoProvider){
//                    s += DebugInfoProvider.asString(1, ((DebugInfoProvider)m).getDebugInfo(new HashMap<>()));
//                }
//                m = m.parent;
//            }
//        }
        return s;
    }
    public static void resetWindowTitle(){
        DizzyEngine.setTitle("Nuclearcraft Plannerator "+updater.currentVersion);
    }
    public static void setVsync(boolean vs){
        if(vsync!=vs)glfwSwapInterval(vs?1:0);
        vsync = vs;
    }
    public static void setConfiguration(CannedConfiguration configuration){
        configuration = configuration.safeCopy();
        configuration.impose(project);
    }
    public static void setConfigurationAndConvertMultiblocks(CannedConfiguration config){
        //TODO when rewriting how multiblocks work, design them in such a way that strict references are not required- and so it doesn't crash if you delete blocks from the config- it just fails to save and shows as missing/errors.

//        ArrayList<MultiblockDesign> designs = new ArrayList<>();
//        for(Multiblock multi : multiblocks)designs.add(multi.toDesign());
//        multiblocks.clear();
        setConfiguration(config);
//        for(MultiblockDesign design : designs){
//            design.file = project;
//            design.convertElements();
//            multiblocks.add(design.toMultiblock());
//        }
    }
    public static interface BufferRenderer{
        void render(int width, int height);
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
        try(MemoryStack stack = MemoryStack.stackPush()){
            PointerBuffer path = stack.mallocPointer(1);
            NFDFilterItem.Buffer filter = NFDFilterItem.malloc(1);
            String extensions = "";
            for(String s : format.extensions)extensions += ","+s;
            if(!extensions.isEmpty())extensions = extensions.substring(1);
            filter.get(0).name(stack.UTF8(format.name)).spec(stack.UTF8(extensions));
            int result = NativeFileDialog.NFD_OpenDialog(path, filter, lastFolders.getOrDefault(hint, defaultFolder).getAbsolutePath());
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
        }
    }
    /**
     * SAVE
     */
    public static void createFileChooser(File selectedFile, Consumer<File> onAccepted, String[] extensions) throws IOException{
        createFileChooser(selectedFile, onAccepted, extensions, "");
    }
    /**
     * SAVE
     */
    public static void createFileChooser(File selectedFile, Consumer<File> onAccepted, String[] format, String hint) throws IOException{
        hint = "SAVE_"+hint;
        try(MemoryStack stack = MemoryStack.stackPush()){
            PointerBuffer path = stack.mallocPointer(1);
            NFDFilterItem.Buffer filter = NFDFilterItem.malloc(1);
            String extensions = "";
            for(String s : format)extensions += ","+s;
            if(!extensions.isEmpty())extensions = extensions.substring(1);
            filter.get(0).name(stack.UTF8("")).spec(stack.UTF8(extensions));
            int result = NativeFileDialog.NFD_SaveDialog(path, filter, lastFolders.getOrDefault(hint, defaultFolder).getAbsolutePath(), selectedFile==null?hint:selectedFile.getName());
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
        }
    }
    public static boolean areImagesEqual(Image img1, Image img2){
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
        }catch(Throwable t){
            error = t;
        }
        if(error==null){
            System.out.println("Saved to autosave"+num+".ncpf");
        }else{
            System.err.println("Autosave Failed!");
        }
        Main.generateCrashReport("Manually closed on error", null);
        glfwSetWindowShouldClose(DizzyEngine.window, true);
    }
    public static int getThemeIndex(Component comp){
        if(comp.parent instanceof Panel&&((Panel)comp.parent).layout instanceof ListLayout){
            return comp.parent.components.indexOf(comp);
        }
        if(comp.parent instanceof MenuDialog)
            return ((MenuDialog)comp.parent).buttons.indexOf(comp);
        return 0;
    }
    public static int getThemeIndex(VRMenuComponent comp){
        if(comp.parent instanceof VRMenuComponentSpecialPanel)
            return comp.parent.components.indexOf(comp);
        if(comp.parent instanceof VRMenuComponentToolPanel)
            return comp.parent.components.indexOf(comp);
        if(comp.parent instanceof VRMenuComponentMultiblockSettingsPanel)
            return comp.parent.components.indexOf(comp);
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
            ((Buffer)buffer).flip();
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
            Logger.error(ex);
        }
        if(imageData==null)
            throw new RuntimeException("Failed to load image: "+stbi_failure_reason());
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
        Logger.warn(message, error);
        if(Main.isBot)return;
        new MenuWarningMessage(message, error).open();
    }
    public static void error(String message, Throwable error){
        System.err.println("Severe Error");
        Logger.error(message, error);
        if(Main.isBot)return;
        new MenuError(message, error).open();
    }
    public static void criticalError(String message, Throwable error){
        System.err.println("Critical Error");
        Logger.error(message, error);
        if(Main.isBot)return;
        new MenuCriticalError(message, error).open();
    }
}

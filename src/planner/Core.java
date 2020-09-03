package planner;
import discord.Bot;
import java.awt.Color;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.menu.MenuMain;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import multiblock.configuration.Configuration;
import multiblock.Multiblock;
import multiblock.configuration.TextureManager;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.turbine.OverhaulTurbine;
import org.lwjgl.glfw.GLFW;
import planner.menu.MenuDiscord;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.error.ErrorAdapter;
import simplelibrary.error.ErrorCategory;
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
    public static boolean enableCullFace = true;
    public static final float maxYRot = 80f;
    public static float xRot = 30;
    public static float yRot = 30;
    public static final ArrayList<Multiblock> multiblocks = new ArrayList<>();
    public static final ArrayList<Multiblock> multiblockTypes = new ArrayList<>();
    public static HashMap<String, String> metadata = new HashMap<>();
    public static Configuration configuration = new Configuration(null, null, null);
    public static Theme theme = Theme.themes.get(0);
    private static long window;
    static{
        for(Configuration configuration : Configuration.configurations){
            if(configuration.overhaul!=null&&configuration.overhaul.fissionMSR!=null){
                for(multiblock.configuration.overhaul.fissionmsr.Block b : configuration.overhaul.fissionMSR.allBlocks){
                    if(b.cooling>0&&!b.name.contains("Standard")){
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
        resetMetadata();
    }
    public static void resetMetadata(){
        metadata.clear();
        metadata.put("Name", "");
        metadata.put("Author", "");
    }
    public static void main(String[] args) throws NoSuchMethodException{
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
        System.out.println("Starting up...");
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
    public static void renderInit(){
        System.out.println("Loading fonts...");
        FontManager.addFont("/simplelibrary/font");
        FontManager.addFont("/planner/font/high resolution");
        FontManager.addFont("/planner/font/small");
        FontManager.addFont("/planner/font/slim");
        FontManager.setFont("high resolution");
        System.out.println("Loading render engine...");
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
        System.out.println("Creating texture pack manager...");
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
        System.out.println("Loading GUI...");
        gui = new GUI(is3D?GameHelper.MODE_HYBRID:GameHelper.MODE_2D, helper);
        if(Main.isBot)gui.open(new MenuDiscord(gui));
        else gui.open(new MenuMain(gui));
        System.out.println("Render initialization complete!");
    }
    public static void tickInit(){}
    public static void finalInit() throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
        System.out.println("Activating GUI...");
        Field helperWindowField = GameHelper.class.getDeclaredField("window");
        helperWindowField.setAccessible(true);
        window = (long)helperWindowField.get(helper);
        helper.assignGUI(gui);
        System.out.println("Loading settings...");
        File f = new File("settings.dat").getAbsoluteFile();
        if(!f.exists())return;
        Config settings = Config.newConfig(f);
        settings.load();
        System.out.println("Loading theme...");
        setTheme(Theme.themes.get(settings.get("theme", 0)));
        System.out.println("Startup complete!");
    }
    public static void tick(boolean isLastTick){
        if(!isLastTick){
            if(isKeyDown(GLFW.GLFW_KEY_LEFT))xRot-=2;
            if(isKeyDown(GLFW.GLFW_KEY_RIGHT))xRot+=2;
            if(isKeyDown(GLFW.GLFW_KEY_UP))yRot = Math.min(maxYRot, Math.max(-maxYRot, yRot-2));
            if(isKeyDown(GLFW.GLFW_KEY_DOWN))yRot = Math.min(maxYRot, Math.max(-maxYRot, yRot+2));
            gui.tick();
        }else{
            File f = new File("settings.dat").getAbsoluteFile();
            Config settings = Config.newConfig(f);
            settings.set("theme", Theme.themes.indexOf(theme));
            settings.save();
            if(Main.isBot){
                Bot.stop();
                System.exit(0);//TODO Shouldn't have to do this! :(
            }
        }
    }
    public static void render(int millisSinceLastTick){
        applyWhite();
        if(gui.menu instanceof MenuMain){
            GL11.glPushMatrix();
            GL11.glTranslated(.4, 0, -1.5);
            GL11.glRotated(yRot, 1, 0, 0);
            GL11.glRotated(xRot, 0, 1, 0);
            Multiblock mb = ((MenuMain)gui.menu).getSelectedMultiblock();
            if(mb!=null){
                double size = Math.max(mb.getX(), Math.max(mb.getY(), mb.getZ()));
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
    public static boolean isAltPressed(){
        return isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)||isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
    }
    public static boolean isControlPressed(){
        return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)||isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }
    public static boolean isShiftPressed(){
        return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)||isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
    public static boolean isKeyDown(int key){
        return GLFW.glfwGetKey(window, key)==GLFW.GLFW_PRESS;
    }
    public static void drawCircle(double x, double y, double innerRadius, double outerRadius, Color color){
        Core.applyColor(color);
        int resolution = (int)(2*Math.PI*outerRadius);//an extra *2 to account for wavy surface?
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
    public static BufferedImage makeImage(int width, int height, BufferRenderer r){
        ByteBuffer bufferer = ImageStash.createDirectByteBuffer(width*height*4);
        Framebuffer buff = new Framebuffer(Core.helper, null, width, height);
        buff.bindRenderTarget2D();
        GL11.glScaled(1, -1, 1);
        GL11.glTranslated(0, -buff.height, 0);
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
        return img;
    }
    public static interface BufferRenderer{
        void render(Framebuffer buff);
    }
    private static int f(byte imgData){
        return (imgData+256)&255;
    }
}
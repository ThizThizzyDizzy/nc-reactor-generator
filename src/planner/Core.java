package planner;
import java.awt.Color;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.menu.MenuMain;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import planner.configuration.Configuration;
import multiblock.Multiblock;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import simplelibrary.Sys;
import simplelibrary.error.ErrorAdapter;
import simplelibrary.error.ErrorCategory;
import simplelibrary.font.FontManager;
import simplelibrary.game.Framebuffer;
import simplelibrary.game.GameHelper;
import simplelibrary.opengl.ImageStash;
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
    public static boolean SEPARATE_BRIGHT_TEXTURES = true;
    public static final float IMG_FAC = .003925f;
    public static final float IMG_POW = 2f;
    public static final float IMG_STRAIGHT_FAC = 1.5f;
    public static final float maxYRot = 80f;
    public static float xRot = 30;
    public static float yRot = 30;
    public static final ArrayList<Multiblock> multiblocks = new ArrayList<>();
    public static final ArrayList<Multiblock> multiblockTypes = new ArrayList<>();
    public static HashMap<String, String> metadata = new HashMap<>();
    public static Configuration configuration = new Configuration(null, null);
    public static Theme theme = Theme.themes.get(0);
    static{
        Configuration.configurations.get(0).impose(configuration);
        for(planner.configuration.overhaul.fissionmsr.Block b : configuration.overhaul.fissionMSR.blocks){
            if(b.cooling>0&&!b.name.contains("Standard")){
                b.setInternalTexture(Core.getImage("overhaul/"+b.name.toLowerCase().replace(" coolant heater", "").replace("liquid ", "")));
            }
        }
        multiblockTypes.add(new UnderhaulSFR());
        multiblockTypes.add(new OverhaulSFR());
        multiblockTypes.add(new OverhaulMSR());
        resetMetadata();
    }
    public static void resetMetadata(){
        metadata.clear();
        metadata.put("Name", "");
        metadata.put("Author", "");
    }
    public static void main(String[] args) throws NoSuchMethodException{
        try{
            for(javax.swing.UIManager.LookAndFeelInfo info:javax.swing.UIManager.getInstalledLookAndFeels()){
                if("Nimbus".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(ClassNotFoundException|InstantiationException|IllegalAccessException|javax.swing.UnsupportedLookAndFeelException ex){}
        try{
            for(javax.swing.UIManager.LookAndFeelInfo info:javax.swing.UIManager.getInstalledLookAndFeels()){
                if("Windows".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(ClassNotFoundException|InstantiationException|IllegalAccessException|javax.swing.UnsupportedLookAndFeelException ex){}
        helper = new GameHelper();
        helper.setBackground(theme.getBackgroundColor());
        helper.setDisplaySize(1200, 700);
        helper.setRenderInitMethod(Core.class.getDeclaredMethod("renderInit", new Class<?>[0]));
        helper.setTickInitMethod(Core.class.getDeclaredMethod("tickInit", new Class<?>[0]));
        helper.setFinalInitMethod(Core.class.getDeclaredMethod("finalInit", new Class<?>[0]));
        helper.setMaximumFramerate(60);
        helper.setRenderMethod(Core.class.getDeclaredMethod("render", int.class));
        helper.setTickMethod(Core.class.getDeclaredMethod("tick", boolean.class));
        helper.setUsesControllers(true);
        helper.setWindowTitle(Main.applicationName+" "+VersionManager.currentVersion);
        helper.setMode(is3D?GameHelper.MODE_HYBRID:GameHelper.MODE_2D);
        helper.setAntiAliasing(4);
        if(fullscreen){
            helper.setFullscreen(true);
            helper.setAutoExitFullscreen(false);
        }
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
        FontManager.addFont("/simplelibrary/font");
        FontManager.addFont("/planner/font/high resolution");
        FontManager.addFont("/planner/font/small");
        FontManager.addFont("/planner/font/slim");
        FontManager.setFont("high resolution");
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
    public static void finalInit() throws LWJGLException{}
    public static void tick(boolean isLastTick){
        if(!isLastTick){
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))xRot-=2;
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))xRot+=2;
            if(Keyboard.isKeyDown(Keyboard.KEY_UP))yRot = Math.min(maxYRot, Math.max(-maxYRot, yRot-2));
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))yRot = Math.min(maxYRot, Math.max(-maxYRot, yRot+2));
            gui.tick();
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
    public static BufferedImage getImage(String texture){
        try{
            if(new File("nbproject").exists()){
                return ImageIO.read(new File("src/textures/"+texture+".png"));
            }else{
                JarFile jar = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")));
                Enumeration enumEntries = jar.entries();
                while(enumEntries.hasMoreElements()){
                    JarEntry file = (JarEntry)enumEntries.nextElement();
                    if(file.getName().equals("textures/"+texture+".png")){
                        return ImageIO.read(jar.getInputStream(file));
                    }
                }
            }
            throw new IllegalArgumentException("Cannot find file: "+texture);
        }catch(IOException ex){
            System.err.println("Couldn't read file: "+texture);
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
    }
    public static InputStream getInputStream(String path){
        try{
            if(new File("nbproject").exists()){
                return new FileInputStream(new File("src/"+path.replace("/", "/")));
            }else{
                JarFile jar = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")));
                Enumeration enumEntries = jar.entries();
                while(enumEntries.hasMoreElements()){
                    JarEntry file = (JarEntry)enumEntries.nextElement();
                    if(file.getName().equals(path.replace("/", "/"))){
                        return jar.getInputStream(file);
                    }
                }
            }
            throw new IllegalArgumentException("Cannot find file: "+path);
        }catch(IOException ex){
            System.err.println("Couldn't read file: "+path);
            return null;
        }
    }
    private static final HashMap<BufferedImage, Integer> imgs = new HashMap<>();
    public static int getTexture(BufferedImage image){
        if(image==null)return -1;
        if(!imgs.containsKey(image)){
            imgs.put(image, ImageStash.instance.allocateAndSetupTexture(image));
        }
        return imgs.get(image);
    }
    public static int img_convert(int c){
        if(SEPARATE_BRIGHT_TEXTURES){
            double f = IMG_FAC*Math.pow(c, IMG_POW);
            float g = c/255f;
            double h = f*Math.pow(g, IMG_STRAIGHT_FAC)+c*(1-Math.pow(g, IMG_STRAIGHT_FAC));
            c = (int)h;
        }
        return c;
    }
    public static void setTheme(Theme t){
        theme = t;
        helper.setBackground(theme.getBackgroundColor());
    }
    public static void applyWhite(){
        applyColor(Color.white);
    }
    public static void applyWhite(float alpha){
        applyColor(Color.white, alpha);
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
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU)||Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }
    public static boolean isControlPressed(){
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)||Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }
    public static boolean isShiftPressed(){
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)||Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
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
        System.out.println();
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
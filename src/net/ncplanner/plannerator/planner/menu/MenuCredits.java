package net.ncplanner.plannerator.planner.menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentLabel;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import simplelibrary.Queue;
import simplelibrary.font.FontManager;
import simplelibrary.image.Image;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuCredits extends Menu{
    private double lastX = -1, lastY = -1;
    private double lastMovement;
    private int pressEscToEndTimer = 0;
    private float pressEscToExitHeight = 30;
    public static final int creditsSpeed = 2;
    public double creditY = 0;
    public double initialYOff = creditsSpeed*20;
    public double yOff = initialYOff;
    public HashMap<MenuComponent, Double> offsets = new HashMap<>();
    public HashMap<MenuComponent, Double> widths = new HashMap<>();
    public HashMap<MenuComponent, Double> xSnaps = new HashMap<>();
    public ArrayList<MenuComponent> snapRights = new ArrayList<>();
    double defaultSize = 32;
    private final ArrayList<BackgroundElement> possibleBackgroundElements = new ArrayList<>();
    private final Queue<BackgroundElement> backgroundElements = new Queue<>();
    private Random rand = new Random();
    private double backgroundElemRotSpeedMult = 1;
    private double backgroundElemSpeedMult = 5;
    private double backgroundElemChance = 0.125;
    private static double backgroundElementScale = .375;
    public MenuCredits(GUI gui){
        super(gui, null);
        text("NuclearCraft Reactor Generator", 1.5);
        text();
        text("Made by ThizThizzyDizzy");
        divider();
        text("Addons", 1.25);
        text("Click on any addon to navigate to its CurseForge page");
        text();
        text("QMD", 1.25, "https://www.curseforge.com/minecraft/mc-mods/qmd");
        text("made by Lach_01298");
        text();
        text("Trinity", 1.25, "https://www.curseforge.com/minecraft/mc-mods/trinity");
        text("made by Pu-238");
        text();
        text("NCOUTO", 1.25, "https://www.curseforge.com/minecraft/customization/nuclearcraft-overhauled-unrealistic-turbine");
        text("made by Thalzamar and FishingPole");
        text();
        text("Moar Heat Sinks", 1.25, "https://www.curseforge.com/minecraft/customization/moar-heat-sinks");
        text("made by QuantumTraverse");
        text();
        text("Moar Fuels", 1.25, "https://www.curseforge.com/minecraft/customization/moarfuels");
        text("made by QuantumTraverse");
        text();
        text("Moar Reactor Functionality", 1.25, "https://www.curseforge.com/minecraft/customization/moar-reactor-functionality");
        text("made by QuantumTraverse");
        text();
        text("Nuclear Oil Refining", 1.25, "https://www.curseforge.com/minecraft/customization/nuclear-oil-refining");
        text("made by Thalzamar");
        text();
        text("Nuclear Tree Factory", 1.25, "https://www.curseforge.com/minecraft/customization/nuclear-tree-factory");
        text("made by joendter");
        text();
        text("Binary's Extra Stuff", 1.25, "https://www.curseforge.com/minecraft/customization/binarys-extra-stuff-bes");
        text("made by binary_nexus");
        text();
        text("AOP", 1.25, "https://www.curseforge.com/minecraft/customization/aop");
        text("made by Thalzamar");
        text();
        text("NCO Confectionery", 1.25, "https://www.curseforge.com/minecraft/customization/nco-confectionery");
        text("made by FishingPole");
        text();
        text("Thorium Mixed Fuels", 1.25, "https://www.curseforge.com/minecraft/customization/thorium-mixed-fuels");
        text("made by Thalzamar");
        text();
        text("Inert Matrix Fuels", 1.25, "https://www.curseforge.com/minecraft/customization/inert-matrix-fuels");
        text("made by Cassandra");
        text();
        text("Alloy Heat Sinks", 1.25, "https://www.curseforge.com/minecraft/customization/alloy-heat-sinks");
        text("made by Cn-285");
        text();
        text("Spicy Heat Sinks", 1.25, "https://www.curseforge.com/minecraft/customization/spicy-heat-sinks");
        text("made by Cn-285");
        divider();
        text("Hut images", 1.25);
        gap();
        addSecondary(new MenuComponent(1/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/standard/outside.png"));
            }
        });
        addSecondary(new MenuComponent(2/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/night/outside.png"));
            }
        });
        addSecondary(new MenuComponent(3/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/winter/outside.png"));
            }
        });
        addSecondary(new MenuComponent(4/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/tropical/outside.png"));
            }
        });
        addSecondary(new MenuComponent(5/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/wasteland/outside.png"));
            }
        });
        add(new MenuComponent(6/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/space/outside.png"));
            }
        });
        gap();
        addSecondary(new MenuComponent(1/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/standard/inside.png"));
            }
        });
        addSecondary(new MenuComponent(2/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/night/inside.png"));
            }
        });
        addSecondary(new MenuComponent(3/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/winter/inside.png"));
            }
        });
        addSecondary(new MenuComponent(4/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/tropical/inside.png"));
            }
        });
        addSecondary(new MenuComponent(5/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/wasteland/inside.png"));
            }
        });
        add(new MenuComponent(6/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/credits/space/inside.png"));
            }
        });
        text();
        text("by Gliese 832 c");
        divider();
        text("Fusion test blanket textures", 1.25);
        gap();
        addSecondary(new MenuComponent(4/9f, 0, defaultSize*2, defaultSize*2){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/overhaul/fusion/heating_blanket.png"));
            }
        });
        add(new MenuComponent(5/9f, 0, defaultSize*2, defaultSize*2){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/overhaul/fusion/breeding_blanket.png"));
            }
        });
        text();
        text("by Cn-285");
        divider();
        text("Libraries", 1.25);
        gap(2);
        text("LWJGL", 1.125);
        text();
        text("SimpleLibraryPlus", 1.125);
        text("(a fork of Simplelibrary by computerneek)");
        divider();
        text("Early Concept Art", 1.25);
        text();
        text("Main Menu");
        add(new MenuComponent(0.5, 0, 1078/2, 824/2){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/credits/main_menu.png"));
            }
        });
        gap(3);
        text("Modify Configuration");
        add(new MenuComponent(0.5, 0, 1078/2, 824/2){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/credits/config.png"));
            }
        });
        gap(3);
        text("Modify Block (Overhaul SFR)");
        add(new MenuComponent(0.5, 0, 1078/2, 824/2){
            @Override
            public void render(int millisSinceLastTick){
                x-=width/2;
                super.render(millisSinceLastTick);
                x+=width/2;
            }
            @Override
            public void render(){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsImageColor());
                drawRect(x, y, x+width, y+height, ImageStash.instance.getTexture("/textures/credits/block_config.png"));
            }
        });
        gap(3);
        divider();
        text("Thank you to my patrons:", 1.25, "patreon.com/thizthizzydizzy");
        text();
        text("Thalzamar", 1.25);
        text();
        text("Mstk", 1.25);
        divider();
        text("Thank you to tomdodd4598 for creating such an amazing mod", 1.25, "https://www.curseforge.com/minecraft/mc-mods/nuclearcraft-overhauled");
        divider();
        text("Thank you to eveyone in the eVault for helping make this planner into what it is", 1.25);
        divider();
        text();
        possibleBackgroundElements.add(new BackgroundElement(TextureManager.getImage("overhaul/item/glowshroom"), false));
        possibleBackgroundElements.add(new BackgroundElement(TextureManager.getImage("overhaul/item/smore"), false));
        possibleBackgroundElements.add(new BackgroundElement(TextureManager.getImage("overhaul/item/moresmore"), false));
        possibleBackgroundElements.add(new BackgroundElement(TextureManager.getImage("overhaul/item/foursmore"), false));
        if(Core.configuration.overhaul!=null){
            if(Core.configuration.overhaul.fissionSFR!=null){
                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(b.texture!=null)possibleBackgroundElements.add(new BackgroundElement(b.texture, true));
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r : b.allRecipes){
                        if(r.inputTexture!=null)possibleBackgroundElements.add(new BackgroundElement(r.inputTexture, false));
                    }
                }
            }
            if(Core.configuration.overhaul.fissionMSR!=null){
                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(b.texture!=null)possibleBackgroundElements.add(new BackgroundElement(b.texture, true));
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r : b.allRecipes){
                        if(r.inputTexture!=null)possibleBackgroundElements.add(new BackgroundElement(r.inputTexture, false));
                    }
                }
            }
            if(Core.configuration.overhaul.turbine!=null){
                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b : Core.configuration.overhaul.turbine.allBlocks){
                    if(b.texture!=null)possibleBackgroundElements.add(new BackgroundElement(b.texture, true));
                }
            }
        }
        if(Core.configuration.underhaul!=null){
            if(Core.configuration.underhaul.fissionSFR!=null){
                for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block b : Core.configuration.underhaul.fissionSFR.allBlocks){
                    if(b.texture!=null)possibleBackgroundElements.add(new BackgroundElement(b.texture, true));
                }
            }
        }
    }
    private void divider(){
        gap(5);
    }
    private void text(){
        gap();
    }
    private void text(String text){
        text(text, 1);
    }
    private void text(String text, double size){
        add(new MenuComponentLabel(0, 0, 0, size*defaultSize, text).noBackground());
    }
    private void text(String text, double size, String link){
        add(new MenuComponentLabel(0, 0, 0, size*defaultSize, text){
            @Override
            public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                double textWidth = FontManager.getLengthForStringWithHeight(text, height);
                if(x>width/2-textWidth/2&&x<width/2+textWidth/2&&button==0&&pressed)Core.openURL(link);
                super.onMouseButton(x, y, button, pressed, mods);
            }
        }.noBackground());
    }
    private void gap(){
        gap(1);
    }
    private void gap(double size){
        creditY+=size*defaultSize;
    }
    @Override
    public <V extends MenuComponent> V add(V component){
        if(component.width==0)component.width = 1;
        if(component.width<=1)widths.put(component, component.width);
        if(component.x>=gui.helper.displayWidth()-1)snapRights.add(component);
        else xSnaps.put(component, component.x);
        component.y = creditY;
        creditY+=component.height;
        offsets.put(component, component.y);
        return super.add(component);
    }
    public <V extends MenuComponent> V addSecondary(V component){
        if(component.width==0)component.width = 1;
        if(component.width<=1)widths.put(component, component.width);
        if(component.x>=gui.helper.displayWidth()-1)snapRights.add(component);
        else xSnaps.put(component, component.x);
        component.y = creditY;
        offsets.put(component, component.y);
        return super.add(component);
    }
    @Override
    public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
        super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
        if(isPress&&key==GLFW.GLFW_KEY_ESCAPE)gui.open(new MenuTransition(gui, this, new MenuMain(gui), MenuTransition.SplitTransitionY.slideIn(0.5), 10));
    }
    @Override
    public void tick(){
        if(lastMovement>50)pressEscToEndTimer = 40;
        lastMovement = 0;
        pressEscToEndTimer = Math.max(0, pressEscToEndTimer-1);
        yOff-=creditsSpeed;
        if(yOff<-(creditY+gui.helper.displayHeight()))yOff = initialYOff;
        BackgroundElement elem;
        for(Iterator<BackgroundElement> it = backgroundElements.iterator(); it.hasNext();){
            BackgroundElement e = it.next();
            e.y+=creditsSpeed*backgroundElemSpeedMult/e.z;
            if(e.y>gui.helper.displayHeight()*e.z)it.remove();
            if(e.threeD)e.rot+=creditsSpeed*backgroundElemRotSpeedMult;
        }
        if(rand.nextDouble()<backgroundElemChance){
            backgroundElements.enqueue(elem = possibleBackgroundElements.get(rand.nextInt(possibleBackgroundElements.size())).copy());
            elem.z = rand.nextDouble()*3+1;
            elem.x = (rand.nextDouble()*3-1)*elem.z*gui.helper.displayWidth();
            elem.y = -gui.helper.displayHeight()*elem.z;
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        Renderer renderer = new Renderer();
        millisSinceLastTick = Math.min(50, millisSinceLastTick);
        double yOff = this.yOff-creditsSpeed*(millisSinceLastTick/50f);
        for(MenuComponent c : offsets.keySet()){
            c.y = gui.helper.displayHeight()+offsets.get(c)+yOff;
        }
        for(MenuComponent c : widths.keySet()){
            c.width = gui.helper.displayWidth()*widths.get(c);
        }
        for(MenuComponent c : xSnaps.keySet()){
            c.x = gui.helper.displayWidth()*xSnaps.get(c);
        }
        for(MenuComponent c : snapRights){
            c.x = gui.helper.displayWidth()-c.width;
        }
        super.render(millisSinceLastTick);
        renderer.setColor(Core.theme.getCreditsTextColor());
        float actualPressEscToEndTimer = Math.max(0, pressEscToEndTimer-millisSinceLastTick/50f);
        float prog = 1-Math.max(0,Math.min(1,(actualPressEscToEndTimer)/20f));
        drawCenteredText(0, -pressEscToExitHeight*prog, gui.helper.displayWidth(), pressEscToExitHeight-pressEscToExitHeight*prog, "Press escape to exit credits");
    }
    @Override
    public void onMouseMove(double x, double y){
        if(lastX!=-1&&lastY!=-1){
            lastMovement = Math.sqrt((x-lastX)*(x-lastX)+(y-lastY)*(y-lastY));
        }
        lastX = x;
        lastY = y;
        super.onMouseMove(x, y);
    }
    public void render3D(int millisSinceLastTick){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getCreditsImageColor());
        for(BackgroundElement element : backgroundElements){
            GL11.glPushMatrix();
            GL11.glTranslated(element.x/(gui.helper.displayHeight()/2), (element.y+((creditsSpeed*backgroundElemSpeedMult/element.z)*millisSinceLastTick/50f))/(gui.helper.displayHeight()/2), -element.z);
            GL11.glRotated(element.rot+(element.threeD?((creditsSpeed*backgroundElemRotSpeedMult)*millisSinceLastTick/50f):0), 0, 1, 0);
            element.render(millisSinceLastTick);
            GL11.glPopMatrix();
        }
    }
    private static class BackgroundElement{
        private final Image texture;
        private final boolean threeD;
        private double x, y, z, rot;
        public BackgroundElement(Image texture, boolean threeD){
            this.texture = texture;
            this.threeD = threeD;
        }
        public BackgroundElement copy(){
            return new BackgroundElement(texture, threeD);
        }
        private void render(int millisSinceLastTick){
            Renderer renderer = new Renderer();
            if(threeD)renderer.drawCube(-backgroundElementScale/2, -backgroundElementScale/2, -backgroundElementScale/2, backgroundElementScale/2, backgroundElementScale/2, backgroundElementScale/2, Core.getTexture(texture));
            else{
                double x1 = -backgroundElementScale/2;
                double y1 = -backgroundElementScale/2;
                double x2 = backgroundElementScale/2;
                double y2 = backgroundElementScale/2;
                ImageStash.instance.bindTexture(Core.getTexture(texture));
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2d(0, 1);
                GL11.glVertex3d(x1, y1, 0);
                GL11.glTexCoord2d(1, 1);
                GL11.glVertex3d(x2, y1, 0);
                GL11.glTexCoord2d(1, 0);
                GL11.glVertex3d(x2, y2, 0);
                GL11.glTexCoord2d(0, 0);
                GL11.glVertex3d(x1, y2, 0);
                GL11.glEnd();
            }
        }
    }
}
package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import static org.lwjgl.glfw.GLFW.*;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import org.joml.Matrix4f;
public class MenuCredits extends Menu{
    private double lastX = -1, lastY = -1;
    private double lastMovement;
    private double pressEscToEndTimer = 0;
    private float pressEscToExitHeight = 30;
    public static final int creditsSpeed = 2;
    public float creditY = 0;
    public float initialYOff = creditsSpeed*20;
    public float yOff = initialYOff;
    public HashMap<Component, Float> offsets = new HashMap<>();
    public HashMap<Component, Float> widths = new HashMap<>();
    public HashMap<Component, Float> xSnaps = new HashMap<>();
    public ArrayList<Component> snapRights = new ArrayList<>();
    float defaultSize = 32;
    private final ArrayList<BackgroundElement> possibleBackgroundElements = new ArrayList<>();
    private final Queue<BackgroundElement> backgroundElements = new Queue<>();
    private Random rand = new Random();
    private float backgroundElemRotSpeedMult = 1;
    private float backgroundElemSpeedMult = 5;
    private float backgroundElemChance = 0.125f;
    private static float backgroundElementScale = .375f;
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
        addSecondary(new Component(1/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/standard/outside.png", x, y, x+width, y+height);
            }
        });
        addSecondary(new Component(2/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/night/outside.png", x, y, x+width, y+height);
            }
        });
        addSecondary(new Component(3/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/winter/outside.png", x, y, x+width, y+height);
            }
        });
        addSecondary(new Component(4/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/tropical/outside.png", x, y, x+width, y+height);
            }
        });
        addSecondary(new Component(5/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/wasteland/outside.png", x, y, x+width, y+height);
            }
        });
        add(new Component(6/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/space/outside.png", x, y, x+width, y+height);
            }
        });
        gap();
        addSecondary(new Component(1/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/standard/inside.png", x, y, x+width, y+height);
            }
        });
        addSecondary(new Component(2/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/night/inside.png", x, y, x+width, y+height);
            }
        });
        addSecondary(new Component(3/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/winter/inside.png", x, y, x+width, y+height);
            }
        });
        addSecondary(new Component(4/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/tropical/inside.png", x, y, x+width, y+height);
            }
        });
        addSecondary(new Component(5/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/wasteland/inside.png", x, y, x+width, y+height);
            }
        });
        add(new Component(6/7f, 0, defaultSize*4, defaultSize*4){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/smivilization/buildings/huts/gliese/credits/space/inside.png", x, y, x+width, y+height);
            }
        });
        text();
        text("by Gliese 832 c");
        divider();
        text("Fusion test blanket textures", 1.25);
        gap();
        addSecondary(new Component(4/9f, 0, defaultSize*2, defaultSize*2){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/overhaul/fusion/heating_blanket.png", x, y, x+width, y+height);
            }
        });
        add(new Component(5/9f, 0, defaultSize*2, defaultSize*2){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsBrightImageColor());
                renderer.drawImage("/textures/overhaul/fusion/breeding_blanket.png", x, y, x+width, y+height);
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
        add(new Component(0.5f, 0, 1078/2, 824/2){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsImageColor());
                renderer.drawImage("/textures/credits/main_menu.png", x, y, x+width, y+height);
            }
        });
        gap(3);
        text("Modify Configuration");
        add(new Component(0.5f, 0, 1078/2, 824/2){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsImageColor());
                renderer.drawImage("/textures/credits/config.png", x, y, x+width, y+height);
            }
        });
        gap(3);
        text("Modify Block (Overhaul SFR)");
        add(new Component(0.5f, 0, 1078/2, 824/2){
            @Override
            public void render2d(double deltaTime){
                x-=width/2;
                super.render2d(deltaTime);
                x+=width/2;
            }
            @Override
            public void draw(double deltaTime){
                Renderer renderer = new Renderer();
                renderer.setColor(Core.theme.getCreditsImageColor());
                renderer.drawImage("/textures/credits/block_config.png", x, y, x+width, y+height);
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
        add(new Label(0, 0, 0, (float)(size*defaultSize), text).noBackground());
    }
    private void text(String text, double size, String link){
        add(new Label(0, 0, 0, (float)(size*defaultSize), text){
            @Override
            public void onMouseButton(double x, double y, int button, int action, int mods){
                Renderer renderer = new Renderer();
                double textWidth = renderer.getStringWidth(text, height);
                if(x>width/2-textWidth/2&&x<width/2+textWidth/2&&button==0&&action==GLFW_PRESS)Core.openURL(link);
                super.onMouseButton(x, y, button, action, mods);
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
    public <T extends Component> T add(T component){
        if(component.width==0)component.width = 1;
        if(component.width<=1)widths.put(component, component.width);
        if(component.x>=gui.getWidth()-1)snapRights.add(component);
        else xSnaps.put(component, component.x);
        component.y = creditY;
        creditY+=component.height;
        offsets.put(component, component.y);
        return super.add(component);
    }
    public <T extends Component> T addSecondary(T component){
        if(component.width==0)component.width = 1;
        if(component.width<=1)widths.put(component, component.width);
        if(component.x>=gui.getWidth()-1)snapRights.add(component);
        else xSnaps.put(component, component.x);
        component.y = creditY;
        offsets.put(component, component.y);
        return super.add(component);
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        super.onKeyEvent(key, scancode, action, mods);
        if(action==GLFW_PRESS&&key==GLFW_KEY_ESCAPE)gui.open(new MenuTransition(gui, this, new MenuMain(gui), MenuTransition.SplitTransitionY.slideIn(0.5f), 10));
    }
    @Override
    public void render2d(double deltaTime){
        if(lastMovement>50)pressEscToEndTimer = 40;
        lastMovement = 0;
        pressEscToEndTimer = Math.max(0, pressEscToEndTimer-deltaTime*20);
        yOff-=creditsSpeed*deltaTime*20;
        if(yOff<-(creditY+gui.getHeight()))yOff = initialYOff;
        BackgroundElement elem;
        for(Iterator<BackgroundElement> it = backgroundElements.iterator(); it.hasNext();){
            BackgroundElement e = it.next();
            e.y+=creditsSpeed*deltaTime*20*backgroundElemSpeedMult/e.z;
            if(e.y>gui.getHeight()*e.z)it.remove();
            if(e.threeD)e.rot+=creditsSpeed*deltaTime*20*backgroundElemRotSpeedMult;
        }
        if(rand.nextDouble()<backgroundElemChance){
            backgroundElements.enqueue(elem = possibleBackgroundElements.get(rand.nextInt(possibleBackgroundElements.size())).copy());
            elem.z = (float)rand.nextDouble()*3+1;
            elem.x = (float)(rand.nextDouble()*3-1)*elem.z*gui.getWidth();
            elem.y = -gui.getHeight()*elem.z;
        }
        Renderer renderer = new Renderer();
        for(Component c : offsets.keySet()){
            c.y = gui.getHeight()+offsets.get(c)+yOff;
        }
        for(Component c : widths.keySet()){
            c.width = gui.getWidth()*widths.get(c);
        }
        for(Component c : xSnaps.keySet()){
            c.x = gui.getWidth()*xSnaps.get(c);
        }
        for(Component c : snapRights){
            c.x = gui.getWidth()-c.width;
        }
        super.render2d(deltaTime);
        renderer.setColor(Core.theme.getCreditsTextColor());
        float prog = 1-Math.max(0,Math.min(1,(float)(pressEscToEndTimer)/20f));
        renderer.drawCenteredText(0, -pressEscToExitHeight*prog, gui.getWidth(), pressEscToExitHeight-pressEscToExitHeight*prog, "Press escape to exit credits");
    }
    @Override
    public void onCursorMoved(double x, double y){
        if(lastX!=-1&&lastY!=-1){
            lastMovement = Math.sqrt((x-lastX)*(x-lastX)+(y-lastY)*(y-lastY));
        }
        lastX = x;
        lastY = y;
        super.onCursorMoved(x, y);
    }
    public void render3D(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getCreditsImageColor());
        for(BackgroundElement element : backgroundElements){
            renderer.model(new Matrix4f().translate(element.x/(gui.getHeight()/2), (float)(element.y+((creditsSpeed*backgroundElemSpeedMult/element.z)*deltaTime*20))/(gui.getHeight()/2), -element.z)
                    .rotate(element.rot+(element.threeD?((creditsSpeed*backgroundElemRotSpeedMult)*(float)deltaTime*20):0), 0, 1, 0));
            element.render2d(deltaTime);
        }
    }
    private static class BackgroundElement{
        private final Image texture;
        private final boolean threeD;
        private float x, y, z, rot;
        public BackgroundElement(Image texture, boolean threeD){
            this.texture = texture;
            this.threeD = threeD;
        }
        public BackgroundElement copy(){
            return new BackgroundElement(texture, threeD);
        }
        private void render2d(double deltaTime){
            Renderer renderer = new Renderer();
            if(threeD)renderer.drawCube(-backgroundElementScale/2, -backgroundElementScale/2, -backgroundElementScale/2, backgroundElementScale/2, backgroundElementScale/2, backgroundElementScale/2, texture);
            else{
                float x1 = -backgroundElementScale/2;
                float y1 = -backgroundElementScale/2;
                float x2 = backgroundElementScale/2;
                float y2 = backgroundElementScale/2;
                renderer.bindTexture(texture);
                renderer.drawScreenQuad(x1, y1, x2, y1, x2, y2, x1, y2, 1, 0, 1, 1, 1, 1, 0, 0, 0);
            }
        }
    }
}
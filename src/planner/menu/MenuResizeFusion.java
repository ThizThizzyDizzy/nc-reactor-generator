package planner.menu;
import java.util.ArrayList;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.editor.MenuComponentVisibleBlock;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuResizeFusion extends Menu{
    private final OverhaulFusionReactor multiblock;
    private final MenuComponentMinimalistScrollable multibwauk = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 32, 32));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true).setTooltip("Finish resizing and return to the editor screen"));
    private final MenuComponentMinimalistButton increaseInnerRadius = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true).setTooltip("increase the reactor's Inner Radius"));
    private final MenuComponentMinimalistButton decreaseInnerRadius = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "-", true, true).setTooltip("decrease the reactor's Inner Radius"));
    private final MenuComponentMinimalistButton increaseCoreSize = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true).setTooltip("increase the reactor's Core Size"));
    private final MenuComponentMinimalistButton decreaseCoreSize = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "-", true, true).setTooltip("decrease the reactor's Core Size"));
    private final MenuComponentMinimalistButton increaseToroidWidth = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true).setTooltip("increase the reactor's Toroid Width"));
    private final MenuComponentMinimalistButton decreaseToroidWidth = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "-", true, true).setTooltip("decrease the reactor's Toroid Width"));
    private final MenuComponentMinimalistButton increaseLiningThickness = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true).setTooltip("increase the reactor's Lining Thickness"));
    private final MenuComponentMinimalistButton decreaseLiningThickness = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "-", true, true).setTooltip("decrease the reactor's Lining Thickness"));
    private boolean refreshNeeded;
    {
        increaseInnerRadius.setForegroundColor(Core.theme.getGreen());
        increaseCoreSize.setForegroundColor(Core.theme.getGreen());
        increaseToroidWidth.setForegroundColor(Core.theme.getGreen());
        increaseLiningThickness.setForegroundColor(Core.theme.getGreen());
        decreaseInnerRadius.setForegroundColor(Core.theme.getRed());
        decreaseCoreSize.setForegroundColor(Core.theme.getRed());
        decreaseToroidWidth.setForegroundColor(Core.theme.getRed());
        decreaseLiningThickness.setForegroundColor(Core.theme.getRed());
    }
    private int CELL_SIZE = 16;
    public MenuResizeFusion(GUI gui, Menu parent, OverhaulFusionReactor multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        multibwauk.setScrollMagnitude(CELL_SIZE/2);
        done.addActionListener((e) -> {
            gui.open(parent);
        });
        increaseInnerRadius.addActionListener((e) -> {
            multiblock.increaseInnerRadius();
            onGUIOpened();
        });
        increaseCoreSize.addActionListener((e) -> {
            multiblock.increaseCoreSize();
            onGUIOpened();
        });
        increaseToroidWidth.addActionListener((e) -> {
            multiblock.increaseToroidWidth();
            onGUIOpened();
        });
        increaseLiningThickness.addActionListener((e) -> {
            multiblock.increaseLiningThickness();
            onGUIOpened();
        });
        decreaseInnerRadius.addActionListener((e) -> {
            multiblock.decreaseInnerRadius();
            onGUIOpened();
        });
        decreaseCoreSize.addActionListener((e) -> {
            multiblock.decreaseCoreSize();
            onGUIOpened();
        });
        decreaseToroidWidth.addActionListener((e) -> {
            multiblock.decreaseToroidWidth();
            onGUIOpened();
        });
        decreaseLiningThickness.addActionListener((e) -> {
            multiblock.decreaseLiningThickness();
            onGUIOpened();
        });
    }
    @Override
    public void onGUIOpened(){
        refreshNeeded = true;
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            multibwauk.components.clear();
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    for(int x = 0; x<multiblock.getX(); x++){
                        multibwauk.add(new MenuComponentVisibleBlock((x+1)*CELL_SIZE, (1+z+(y*(multiblock.getZ()+1)))*CELL_SIZE, CELL_SIZE, CELL_SIZE, multiblock, x, y, z));
                    }
                }
            }
            multibwauk.add(new MenuComponent(0, 0, 0, 0){
                @Override
                public void render(){}
                @Override
                public void render(int millisSinceLastTick){
                    drawRects();
                }
            });
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        done.width = gui.helper.displayWidth()/4;
        increaseInnerRadius.width = decreaseInnerRadius.width = increaseCoreSize.width = decreaseCoreSize.width = increaseToroidWidth.width = decreaseToroidWidth.width = increaseLiningThickness.width = decreaseLiningThickness.width = gui.helper.displayWidth()/8;
        multibwauk.width = done.x = increaseInnerRadius.x = increaseCoreSize.x = increaseToroidWidth.x = increaseLiningThickness.x = gui.helper.displayWidth()-done.width;
        decreaseInnerRadius.x = decreaseCoreSize.x = decreaseToroidWidth.x = decreaseLiningThickness.x = gui.helper.displayWidth()-done.width/2;
        done.height = increaseInnerRadius.height = decreaseInnerRadius.height = increaseCoreSize.height = decreaseCoreSize.height = increaseToroidWidth.height = decreaseToroidWidth.height = increaseLiningThickness.height = decreaseLiningThickness.height = 64;
        multibwauk.height = gui.helper.displayHeight();
        increaseInnerRadius.y = decreaseInnerRadius.y = done.y+done.height+140;
        increaseCoreSize.y = decreaseCoreSize.y = increaseInnerRadius.y+increaseInnerRadius.height+60;
        increaseToroidWidth.y = decreaseToroidWidth.y = increaseCoreSize.y+increaseCoreSize.height+60;
        increaseLiningThickness.y = decreaseLiningThickness.y = increaseToroidWidth.y+increaseToroidWidth.height+60;
        super.render(millisSinceLastTick);
        Core.applyColor(Core.theme.getTextColor());
        drawCenteredText(done.x, done.height, done.x+done.width, done.height+40, "["+multiblock.innerRadius+","+multiblock.coreSize+","+multiblock.toroidWidth+","+multiblock.liningThickness+"]");
        drawCenteredText(done.x, done.height+40, done.x+done.width, done.height+80, multiblock.getX()+"x"+multiblock.getY()+"x"+multiblock.getDisplayZ());
        drawCenteredText(increaseInnerRadius.x, increaseInnerRadius.y+-40, increaseInnerRadius.x+done.width, increaseInnerRadius.y, "Inner Radius");
        drawCenteredText(increaseCoreSize.x, increaseCoreSize.y+-40, increaseCoreSize.x+done.width, increaseCoreSize.y, "Core Size");
        drawCenteredText(increaseToroidWidth.x, increaseToroidWidth.y+-40, increaseToroidWidth.x+done.width, increaseToroidWidth.y, "Toroid Width");
        drawCenteredText(increaseLiningThickness.x, increaseLiningThickness.y+-40, increaseLiningThickness.x+done.width, increaseLiningThickness.y, "Lining Thickness");
    }
    public void expand(int x, int y, int z){
        if(x>0)multiblock.expandRight(x);
        if(x<0)multiblock.expandLeft(-x);
        if(y>0)multiblock.expandUp(y);
        if(y<0)multiblock.exandDown(-y);
        if(z>0)multiblock.expandToward(z);
        if(z<0)multiblock.expandAway(-z);
        onGUIOpened();
    }
    private void deleteX(int x){
        multiblock.deleteX(x);
        onGUIOpened();
    }
    private void deleteY(int y){
        multiblock.deleteY(y);
        onGUIOpened();
    }
    private void deleteZ(int z){
        multiblock.deleteZ(z);
        onGUIOpened();
    }
    private void insertX(int x){
        multiblock.insertX(x);
        onGUIOpened();
    }
    private void insertY(int y){
        multiblock.insertY(y);
        onGUIOpened();
    }
    private void insertZ(int z){
        multiblock.insertZ(z);
        onGUIOpened();
    }
    public static ArrayList<double[]> rects = new ArrayList<>();
    public static void addRect(double r, double g, double b, double a, double left, double top, double right, double bottom){
        rects.add(new double[]{r, g, b, a, left, top, right, bottom});
    }
    public static void drawRects(){
        for(double[] rect : rects){
            GL11.glColor4d(rect[0], rect[1], rect[2], rect[3]);
            drawRect(rect[4], rect[5], rect[6], rect[7], 0);
        }
        rects.clear();
    }
}
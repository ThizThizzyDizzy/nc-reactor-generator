package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Scrollable;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentVisibleBlock;
public class MenuResizeFusion extends Menu{
    private final OverhaulFusionReactor multiblock;
    private final Scrollable multibwauk = add(new Scrollable(0, 0, 0, 0, 32, 32));
    private final Button done = add(new Button(0, 0, 0, 0, "Done", true).setTooltip("Finish resizing and return to the editor screen"));
    private final Button increaseInnerRadius = add(new Button(0, 0, 0, 0, "+", true).setTooltip("increase the reactor's Inner Radius").setTextColor(Core.theme::getAddButtonTextColor));
    private final Button decreaseInnerRadius = add(new Button(0, 0, 0, 0, "-", true).setTooltip("decrease the reactor's Inner Radius").setTextColor(Core.theme::getDeleteButtonTextColor));
    private final Button increaseCoreSize = add(new Button(0, 0, 0, 0, "+", true).setTooltip("increase the reactor's Core Size").setTextColor(Core.theme::getAddButtonTextColor));
    private final Button decreaseCoreSize = add(new Button(0, 0, 0, 0, "-", true).setTooltip("decrease the reactor's Core Size").setTextColor(Core.theme::getDeleteButtonTextColor));
    private final Button increaseToroidWidth = add(new Button(0, 0, 0, 0, "+", true).setTooltip("increase the reactor's Toroid Width").setTextColor(Core.theme::getAddButtonTextColor));
    private final Button decreaseToroidWidth = add(new Button(0, 0, 0, 0, "-", true).setTooltip("decrease the reactor's Toroid Width").setTextColor(Core.theme::getDeleteButtonTextColor));
    private final Button increaseLiningThickness = add(new Button(0, 0, 0, 0, "+", true).setTooltip("increase the reactor's Lining Thickness").setTextColor(Core.theme::getAddButtonTextColor));
    private final Button decreaseLiningThickness = add(new Button(0, 0, 0, 0, "-", true).setTooltip("decrease the reactor's Lining Thickness").setTextColor(Core.theme::getDeleteButtonTextColor));
    private boolean refreshNeeded;
    private int CELL_SIZE = 16;
    public MenuResizeFusion(GUI gui, Menu parent, OverhaulFusionReactor multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        multibwauk.scrollMagnitude = CELL_SIZE/2;
        done.addAction(() -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(1, 0), 5));
        });
        increaseInnerRadius.addAction(() -> {
            multiblock.increaseInnerRadius();
            onOpened();
        });
        increaseCoreSize.addAction(() -> {
            multiblock.increaseCoreSize();
            onOpened();
        });
        increaseToroidWidth.addAction(() -> {
            multiblock.increaseToroidWidth();
            onOpened();
        });
        increaseLiningThickness.addAction(() -> {
            multiblock.increaseLiningThickness();
            onOpened();
        });
        decreaseInnerRadius.addAction(() -> {
            multiblock.decreaseInnerRadius();
            onOpened();
        });
        decreaseCoreSize.addAction(() -> {
            multiblock.decreaseCoreSize();
            onOpened();
        });
        decreaseToroidWidth.addAction(() -> {
            multiblock.decreaseToroidWidth();
            onOpened();
        });
        decreaseLiningThickness.addAction(() -> {
            multiblock.decreaseLiningThickness();
            onOpened();
        });
    }
    @Override
    public void onOpened(){
        multibwauk.components.clear();
        BoundingBox bbox = multiblock.getBoundingBox();
        int depth = bbox.getDepth();
        multiblock.forEachPosition((x, y, z) -> {
            multibwauk.add(new MenuComponentVisibleBlock((x+1)*CELL_SIZE, (1+z+(y*(depth+1)))*CELL_SIZE, CELL_SIZE, CELL_SIZE, multiblock, x, y, z));
        });
        multibwauk.add(new Component(0, 0, 0, 0){
            @Override
            public void render2d(double deltaTime){
                drawRects();
            }
        });
        refreshNeeded = false;
    }
    @Override
    public void render2d(double deltaTime){
        if(refreshNeeded){
            onOpened();
        }
        Renderer renderer = new Renderer();
        done.width = gui.getWidth()/4;
        increaseInnerRadius.width = decreaseInnerRadius.width = increaseCoreSize.width = decreaseCoreSize.width = increaseToroidWidth.width = decreaseToroidWidth.width = increaseLiningThickness.width = decreaseLiningThickness.width = gui.getWidth()/8;
        multibwauk.width = done.x = increaseInnerRadius.x = increaseCoreSize.x = increaseToroidWidth.x = increaseLiningThickness.x = gui.getWidth()-done.width;
        decreaseInnerRadius.x = decreaseCoreSize.x = decreaseToroidWidth.x = decreaseLiningThickness.x = gui.getWidth()-done.width/2;
        done.height = increaseInnerRadius.height = decreaseInnerRadius.height = increaseCoreSize.height = decreaseCoreSize.height = increaseToroidWidth.height = decreaseToroidWidth.height = increaseLiningThickness.height = decreaseLiningThickness.height = 64;
        multibwauk.height = gui.getHeight();
        increaseInnerRadius.y = decreaseInnerRadius.y = done.y+done.height+140;
        increaseCoreSize.y = decreaseCoreSize.y = increaseInnerRadius.y+increaseInnerRadius.height+60;
        increaseToroidWidth.y = decreaseToroidWidth.y = increaseCoreSize.y+increaseCoreSize.height+60;
        increaseLiningThickness.y = decreaseLiningThickness.y = increaseToroidWidth.y+increaseToroidWidth.height+60;
        super.render2d(deltaTime);
        renderer.setColor(Core.theme.getResizeMenuTextColor());
        renderer.drawCenteredText(done.x, done.height, done.x+done.width, done.height+40, "["+multiblock.innerRadius+","+multiblock.coreSize+","+multiblock.toroidWidth+","+multiblock.liningThickness+"]");
        BoundingBox bbox = multiblock.getBoundingBox();
        renderer.drawCenteredText(done.x, done.height+40, done.x+done.width, done.height+80, bbox.getWidth()+"x"+bbox.getHeight()+"x"+bbox.getDepth());
        renderer.drawCenteredText(increaseInnerRadius.x, increaseInnerRadius.y+-40, increaseInnerRadius.x+done.width, increaseInnerRadius.y, "Inner Radius");
        renderer.drawCenteredText(increaseCoreSize.x, increaseCoreSize.y+-40, increaseCoreSize.x+done.width, increaseCoreSize.y, "Core Size");
        renderer.drawCenteredText(increaseToroidWidth.x, increaseToroidWidth.y+-40, increaseToroidWidth.x+done.width, increaseToroidWidth.y, "Toroid Width");
        renderer.drawCenteredText(increaseLiningThickness.x, increaseLiningThickness.y+-40, increaseLiningThickness.x+done.width, increaseLiningThickness.y, "Lining Thickness");
    }
    public static ArrayList<float[]> rects = new ArrayList<>();
    public static void addRect(float r, float g, float b, float a, float left, float top, float right, float bottom){
        rects.add(new float[]{r, g, b, a, left, top, right, bottom});
    }
    public static void drawRects(){
        Renderer renderer = new Renderer();
        for(float[] rect : rects){
            renderer.setColor(rect[0], rect[1], rect[2], rect[3]);
            renderer.fillRect(rect[4], rect[5], rect[6], rect[7]);
        }
        rects.clear();
    }
}
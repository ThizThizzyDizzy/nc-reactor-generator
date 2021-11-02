package net.ncplanner.plannerator.planner.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistScrollable;
import net.ncplanner.plannerator.planner.menu.component.editor.MenuComponentVisibleBlock;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuResize extends Menu{
    private final CuboidalMultiblock<Block> multiblock;
    private final MenuComponentMinimalistScrollable multibwauk = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 32, 32));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true).setTooltip("Finish resizing and return to the editor screen"));
    private int CELL_SIZE = 48;
    public MenuResize(GUI gui, Menu parent, CuboidalMultiblock multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        multibwauk.setScrollMagnitude(CELL_SIZE/2);
        done.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(1, 0), 5));
        });
    }
    @Override
    public void onGUIOpened(){
        multibwauk.components.clear();
        for(int y = 0; y<multiblock.getInternalHeight(); y++){
            final int layer = y;
            MenuComponentMinimalistButton insertLayer = multibwauk.add(new MenuComponentMinimalistButton(0, CELL_SIZE/2+y*(multiblock.getInternalDepth()+5)*CELL_SIZE, CELL_SIZE*(multiblock.getInternalWidth()+4), CELL_SIZE, "+", multiblock.getInternalHeight()<multiblock.getMaxY(), true)).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Insert a blank layer");
            MenuComponentMinimalistButton del = multibwauk.add(new MenuComponentMinimalistButton(0, CELL_SIZE*2+y*(multiblock.getInternalDepth()+5)*CELL_SIZE, CELL_SIZE*2, CELL_SIZE*2, "-", multiblock.getInternalHeight()>multiblock.getMinY(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        addRect(1, 0, 0, .25, x+width, y+height, x+width+CELL_SIZE*multiblock.getInternalWidth(), (layer+1)*(multiblock.getInternalDepth()+5)*CELL_SIZE);
                    }
                }
            }).setTextColor(Core.theme::getDeleteButtonTextColor).setTooltip("Delete this layer");
            MenuComponentMinimalistButton top = multibwauk.add(new MenuComponentMinimalistButton(CELL_SIZE*2, CELL_SIZE*2+y*(multiblock.getInternalDepth()+5)*CELL_SIZE, CELL_SIZE*multiblock.getInternalWidth(), CELL_SIZE, "+", multiblock.getInternalDepth()<multiblock.getMaxZ(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                            addRect(0, 1, 0, .25, CELL_SIZE*2, CELL_SIZE*2+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, CELL_SIZE*2+CELL_SIZE*multiblock.getInternalWidth(), CELL_SIZE*4+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE);
                        }
                    }
                }
            }).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Add a blank row");//add top
            MenuComponentMinimalistButton bottom = multibwauk.add(new MenuComponentMinimalistButton(CELL_SIZE*2, (y+1)*(multiblock.getInternalDepth()+5)*CELL_SIZE-CELL_SIZE, CELL_SIZE*multiblock.getInternalWidth(), CELL_SIZE, "+", multiblock.getInternalDepth()<multiblock.getMaxZ(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                            addRect(0, 1, 0, .25, CELL_SIZE*2, (Y+1)*(multiblock.getInternalDepth()+5)*CELL_SIZE-CELL_SIZE, CELL_SIZE*2+CELL_SIZE*multiblock.getInternalWidth(), (Y+1)*(multiblock.getInternalDepth()+5)*CELL_SIZE);
                        }
                    }
                }
            }).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Add a blank row");//add bottom
            MenuComponentMinimalistButton left = multibwauk.add(new MenuComponentMinimalistButton(0, CELL_SIZE*3+y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, CELL_SIZE, CELL_SIZE*multiblock.getInternalDepth(), "+", multiblock.getInternalWidth()<multiblock.getMaxX(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                            addRect(0, 1, 0, .25, CELL_SIZE, CELL_SIZE*3+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, CELL_SIZE*2, CELL_SIZE*3+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE+CELL_SIZE*multiblock.getInternalDepth());
                        }
                    }
                }
            }).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Add a blank column");//add left
            MenuComponentMinimalistButton right = multibwauk.add(new MenuComponentMinimalistButton(multiblock.getInternalWidth()*CELL_SIZE+CELL_SIZE*2, CELL_SIZE*3+y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, CELL_SIZE, CELL_SIZE*multiblock.getInternalDepth(), "+", multiblock.getInternalWidth()<multiblock.getMaxX(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                            addRect(0, 1, 0, .25, multiblock.getInternalWidth()*CELL_SIZE+CELL_SIZE*2, CELL_SIZE*3+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, multiblock.getInternalWidth()*CELL_SIZE+CELL_SIZE*2+CELL_SIZE, CELL_SIZE*3+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE+CELL_SIZE*multiblock.getInternalDepth());
                        }
                    }
                }
            }).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Add a blank column");//add right
            insertLayer.addActionListener((e) -> {
                if(layer==0)expand(0, -1, 0);
                else insertY(layer);
            });
            del.addActionListener((e) -> {
                deleteY(layer);
            });
            top.addActionListener((e) -> {
                expand(0,0,-1);
            });
            bottom.addActionListener((e) -> {
                expand(0,0,1);
            });
            left.addActionListener((e) -> {
                expand(-1,0,0);
            });
            right.addActionListener((e) -> {
                expand(1,0,0);
            });
            for(int z = 0; z<multiblock.getInternalDepth(); z++){
                final int row = z;
                MenuComponentMinimalistButton delRow = multibwauk.add(new MenuComponentMinimalistButton(CELL_SIZE, CELL_SIZE*3+(int)((1+z+(y*(multiblock.getInternalDepth()+5)))*CELL_SIZE), CELL_SIZE, CELL_SIZE, "-", multiblock.getInternalDepth()>multiblock.getMinZ(), true){
                    @Override
                    public void render(){
                        super.render();
                        if(enabled&&isMouseOver){
                            for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                                addRect(1, 0, 0, .25, CELL_SIZE*2, CELL_SIZE*3+(int)((1+row+(Y*(multiblock.getInternalDepth()+5)))*CELL_SIZE), CELL_SIZE*2+multiblock.getInternalWidth()*CELL_SIZE, CELL_SIZE*4+(int)((1+row+(Y*(multiblock.getInternalDepth()+5)))*CELL_SIZE));
                            }
                        }
                    }
                }).setTextColor(Core.theme::getDeleteButtonTextColor).setTooltip("Delete this row");
                delRow.addActionListener((e) -> {
                    deleteZ(row);
                });
                for(int x = 0; x<multiblock.getInternalWidth(); x++){
                    final int column = x;
                    if(z==0){
                        MenuComponentMinimalistButton delColumn = multibwauk.add(new MenuComponentMinimalistButton((x+2)*CELL_SIZE, CELL_SIZE*3+y*(multiblock.getInternalDepth()+5)*CELL_SIZE, CELL_SIZE, CELL_SIZE, "-", multiblock.getInternalWidth()>multiblock.getMinX(), true){
                            @Override
                            public void render(){
                                super.render();
                                if(enabled&&isMouseOver){
                                    for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                                        addRect(1, 0, 0, .25, (column+2)*CELL_SIZE, CELL_SIZE*4+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE, (column+3)*CELL_SIZE, CELL_SIZE*4+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+multiblock.getInternalDepth()*CELL_SIZE);
                                    }
                                }
                            }
                        }).setTextColor(Core.theme::getDeleteButtonTextColor).setTooltip("Delete this column");
                        delColumn.addActionListener((e) -> {
                            deleteX(column);
                        });
                    }
                    multibwauk.add(new MenuComponentVisibleBlock((x+2)*CELL_SIZE, CELL_SIZE*3+(int)((1+z+(y*(multiblock.getInternalDepth()+5)))*CELL_SIZE), CELL_SIZE, CELL_SIZE, multiblock, x+1, y+1, z+1));
                }
            }
        }
        MenuComponentMinimalistButton layerTop = multibwauk.add(new MenuComponentMinimalistButton(0, CELL_SIZE+CELL_SIZE*(multiblock.getInternalHeight()*(multiblock.getInternalDepth()+5)), CELL_SIZE*(multiblock.getInternalWidth()+4), CELL_SIZE, "+", multiblock.getInternalHeight()<multiblock.getMaxY(), true)).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Insert a blank layer");
        layerTop.addActionListener((e) -> {
            expand(0,1,0);
        });
        multibwauk.add(new MenuComponent(0, 0, 0, 0){
            @Override
            public void render(){}
            @Override
            public void render(int millisSinceLastTick){
                drawRects();
            }
        });
    }
    @Override
    public void render(int millisSinceLastTick){
        Renderer renderer = new Renderer();
        done.width = gui.helper.displayWidth()/4;
        multibwauk.width = done.x = gui.helper.displayWidth()-done.width;
        done.height = 64;
        multibwauk.height = gui.helper.displayHeight();
        super.render(millisSinceLastTick);
        renderer.setColor(Core.theme.getResizeMenuTextColor());
        drawCenteredText(done.x, done.height, done.x+done.width, done.height+40, multiblock.getDimensionsStr());
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
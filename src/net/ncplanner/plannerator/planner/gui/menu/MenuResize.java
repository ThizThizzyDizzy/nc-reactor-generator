package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Scrollable;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentVisibleBlock;
public class MenuResize extends Menu{
    private final CuboidalMultiblock<AbstractBlock> multiblock;
    private final Scrollable multibwauk = add(new Scrollable(0, 0, 0, 0, 32, 32));
    private final Button done = add(new Button("Done", true).setTooltip("Finish resizing and return to the editor screen"));
    private int CELL_SIZE = 48;
    public MenuResize(GUI gui, Menu parent, CuboidalMultiblock multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        multibwauk.scrollMagnitude = CELL_SIZE/2;
        done.addAction(() -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(1, 0), 5));
        });
    }
    @Override
    public void onOpened(){
        multibwauk.components.clear();
        for(int y = 0; y<multiblock.getInternalHeight(); y++){
            final int layer = y;
            Button insertLayer = multibwauk.add(new Button(0, CELL_SIZE/2+y*(multiblock.getInternalDepth()+5)*CELL_SIZE, CELL_SIZE*(multiblock.getInternalWidth()+4), CELL_SIZE, "+", multiblock.getInternalHeight()<multiblock.getMaxY())).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Insert a blank layer");
            Button del = multibwauk.add(new Button(0, CELL_SIZE*2+y*(multiblock.getInternalDepth()+5)*CELL_SIZE, CELL_SIZE*2, CELL_SIZE*2, "-", multiblock.getInternalHeight()>multiblock.getMinY()){
                @Override
                public void draw(double deltaTime){
                    super.draw(deltaTime);
                    if(enabled&&isMouseFocused){
                        addRect(1, 0, 0, .25f, x+width, y+height, x+width+CELL_SIZE*multiblock.getInternalWidth(), (layer+1)*(multiblock.getInternalDepth()+5)*CELL_SIZE);
                    }
                }
            }).setTextColor(Core.theme::getDeleteButtonTextColor).setTooltip("Delete this layer");
            Button top = multibwauk.add(new Button(CELL_SIZE*2, CELL_SIZE*2+y*(multiblock.getInternalDepth()+5)*CELL_SIZE, CELL_SIZE*multiblock.getInternalWidth(), CELL_SIZE, "+", multiblock.getInternalDepth()<multiblock.getMaxZ()){
                @Override
                public void draw(double deltaTime){
                    super.draw(deltaTime);
                    if(enabled&&isMouseFocused){
                        for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                            addRect(0, 1, 0, .25f, CELL_SIZE*2, CELL_SIZE*2+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, CELL_SIZE*2+CELL_SIZE*multiblock.getInternalWidth(), CELL_SIZE*4+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE);
                        }
                    }
                }
            }).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Add a blank row");//add top
            Button bottom = multibwauk.add(new Button(CELL_SIZE*2, (y+1)*(multiblock.getInternalDepth()+5)*CELL_SIZE-CELL_SIZE, CELL_SIZE*multiblock.getInternalWidth(), CELL_SIZE, "+", multiblock.getInternalDepth()<multiblock.getMaxZ()){
                @Override
                public void draw(double deltaTime){
                    super.draw(deltaTime);
                    if(enabled&&isMouseFocused){
                        for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                            addRect(0, 1, 0, .25f, CELL_SIZE*2, (Y+1)*(multiblock.getInternalDepth()+5)*CELL_SIZE-CELL_SIZE, CELL_SIZE*2+CELL_SIZE*multiblock.getInternalWidth(), (Y+1)*(multiblock.getInternalDepth()+5)*CELL_SIZE);
                        }
                    }
                }
            }).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Add a blank row");//add bottom
            Button left = multibwauk.add(new Button(0, CELL_SIZE*3+y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, CELL_SIZE, CELL_SIZE*multiblock.getInternalDepth(), "+", multiblock.getInternalWidth()<multiblock.getMaxX()){
                @Override
                public void draw(double deltaTime){
                    super.draw(deltaTime);
                    if(enabled&&isMouseFocused){
                        for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                            addRect(0, 1, 0, .25f, CELL_SIZE, CELL_SIZE*3+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, CELL_SIZE*2, CELL_SIZE*3+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE+CELL_SIZE*multiblock.getInternalDepth());
                        }
                    }
                }
            }).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Add a blank column");//add left
            Button right = multibwauk.add(new Button(multiblock.getInternalWidth()*CELL_SIZE+CELL_SIZE*2, CELL_SIZE*3+y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, CELL_SIZE, CELL_SIZE*multiblock.getInternalDepth(), "+", multiblock.getInternalWidth()<multiblock.getMaxX()){
                @Override
                public void draw(double deltaTime){
                    super.draw(deltaTime);
                    if(enabled&&isMouseFocused){
                        for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                            addRect(0, 1, 0, .25f, multiblock.getInternalWidth()*CELL_SIZE+CELL_SIZE*2, CELL_SIZE*3+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE, multiblock.getInternalWidth()*CELL_SIZE+CELL_SIZE*2+CELL_SIZE, CELL_SIZE*3+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+CELL_SIZE+CELL_SIZE*multiblock.getInternalDepth());
                        }
                    }
                }
            }).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Add a blank column");//add right
            insertLayer.addAction(() -> {
                if(layer==0)expand(0, -1, 0);
                else insertY(layer);
            });
            del.addAction(() -> {
                deleteY(layer);
            });
            top.addAction(() -> {
                expand(0,0,-1);
            });
            bottom.addAction(() -> {
                expand(0,0,1);
            });
            left.addAction(() -> {
                expand(-1,0,0);
            });
            right.addAction(() -> {
                expand(1,0,0);
            });
            for(int z = 0; z<multiblock.getInternalDepth(); z++){
                final int row = z;
                Button delRow = multibwauk.add(new Button(CELL_SIZE, CELL_SIZE*3+(int)((1+z+(y*(multiblock.getInternalDepth()+5)))*CELL_SIZE), CELL_SIZE, CELL_SIZE, "-", multiblock.getInternalDepth()>multiblock.getMinZ()){
                    @Override
                    public void draw(double deltaTime){
                        super.draw(deltaTime);
                        if(enabled&&isMouseFocused){
                            for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                                addRect(1, 0, 0, .25f, CELL_SIZE*2, CELL_SIZE*3+(int)((1+row+(Y*(multiblock.getInternalDepth()+5)))*CELL_SIZE), CELL_SIZE*2+multiblock.getInternalWidth()*CELL_SIZE, CELL_SIZE*4+(int)((1+row+(Y*(multiblock.getInternalDepth()+5)))*CELL_SIZE));
                            }
                        }
                    }
                }).setTextColor(Core.theme::getDeleteButtonTextColor).setTooltip("Delete this row");
                delRow.addAction(() -> {
                    deleteZ(row);
                });
                for(int x = 0; x<multiblock.getInternalWidth(); x++){
                    final int column = x;
                    if(z==0){
                        Button delColumn = multibwauk.add(new Button((x+2)*CELL_SIZE, CELL_SIZE*3+y*(multiblock.getInternalDepth()+5)*CELL_SIZE, CELL_SIZE, CELL_SIZE, "-", multiblock.getInternalWidth()>multiblock.getMinX()){
                            @Override
                            public void draw(double deltaTime){
                                super.draw(deltaTime);
                                if(enabled&&isMouseFocused){
                                    for(int Y = 0; Y<multiblock.getInternalHeight(); Y++){
                                        addRect(1, 0, 0, .25f, (column+2)*CELL_SIZE, CELL_SIZE*4+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE, (column+3)*CELL_SIZE, CELL_SIZE*4+Y*(multiblock.getInternalDepth()+5)*CELL_SIZE+multiblock.getInternalDepth()*CELL_SIZE);
                                    }
                                }
                            }
                        }).setTextColor(Core.theme::getDeleteButtonTextColor).setTooltip("Delete this column");
                        delColumn.addAction(() -> {
                            deleteX(column);
                        });
                    }
                    multibwauk.add(new MenuComponentVisibleBlock((x+2)*CELL_SIZE, CELL_SIZE*3+(int)((1+z+(y*(multiblock.getInternalDepth()+5)))*CELL_SIZE), CELL_SIZE, CELL_SIZE, multiblock, x+1, y+1, z+1));
                }
            }
        }
        Button layerTop = multibwauk.add(new Button(0, CELL_SIZE+CELL_SIZE*(multiblock.getInternalHeight()*(multiblock.getInternalDepth()+5)), CELL_SIZE*(multiblock.getInternalWidth()+4), CELL_SIZE, "+", multiblock.getInternalHeight()<multiblock.getMaxY())).setTextColor(Core.theme::getAddButtonTextColor).setTooltip("Insert a blank layer");
        layerTop.addAction(() -> {
            expand(0,1,0);
        });
        multibwauk.add(new Component(0, 0, 0, 0){
            @Override
            public void draw(double deltaTime){}
            @Override
            public void render2d(double deltaTime){
                drawRects();
            }
        });
    }
    @Override
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        done.width = gui.getWidth()/4;
        multibwauk.width = done.x = gui.getWidth()-done.width;
        done.height = 64;
        multibwauk.height = gui.getHeight();
        super.render2d(deltaTime);
        renderer.setColor(Core.theme.getResizeMenuTextColor());
        renderer.drawCenteredText(done.x, done.height, done.x+done.width, done.height+40, multiblock.getDimensionsStr());
        renderer.drawCenteredText(done.x, done.height+40, done.x+done.width, done.height+80, "Volume: "+multiblock.getVolume());
        renderer.drawCenteredText(done.x, done.height+80, done.x+done.width, done.height+120, "Internal: "+multiblock.getInternalVolume());
    }
    public void expand(int x, int y, int z){
        if(x>0)multiblock.expandRight(x);
        if(x<0)multiblock.expandLeft(-x);
        if(y>0)multiblock.expandUp(y);
        if(y<0)multiblock.exandDown(-y);
        if(z>0)multiblock.expandToward(z);
        if(z<0)multiblock.expandAway(-z);
        onOpened();
    }
    private void deleteX(int x){
        multiblock.deleteX(x);
        onOpened();
    }
    private void deleteY(int y){
        multiblock.deleteY(y);
        onOpened();
    }
    private void deleteZ(int z){
        multiblock.deleteZ(z);
        onOpened();
    }
    private void insertX(int x){
        multiblock.insertX(x);
        onOpened();
    }
    private void insertY(int y){
        multiblock.insertY(y);
        onOpened();
    }
    private void insertZ(int z){
        multiblock.insertZ(z);
        onOpened();
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
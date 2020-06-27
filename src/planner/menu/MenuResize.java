package planner.menu;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.MenuComponentVisibleBlock;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuResize extends Menu{
    private final Multiblock<Block> multiblock;
    private final MenuComponentMinimalistScrollable multibwauk = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 32, 32));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
    private int CELL_SIZE = 48;
    public MenuResize(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        multibwauk.setScrollMagnitude(CELL_SIZE/2);
        done.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        multibwauk.components.clear();
        MenuComponentMinimalistButton layerTop = multibwauk.add(new MenuComponentMinimalistButton(0, 0, CELL_SIZE*(multiblock.getX()+4), CELL_SIZE, "+", multiblock.getY()<multiblock.getMaxSize(), true));
        for(int y = 0; y<multiblock.getY(); y++){
            final int layer = y;
            MenuComponentMinimalistButton del = multibwauk.add(new MenuComponentMinimalistButton(0, CELL_SIZE*2+y*(multiblock.getZ()+4)*CELL_SIZE, CELL_SIZE*2, CELL_SIZE*2, "-", multiblock.getY()>multiblock.getMinSize(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        addRect(1, 0, 0, .25, x+width, y+height, x+width+CELL_SIZE*multiblock.getX(), (layer+1)*(multiblock.getZ()+4)*CELL_SIZE);
                    }
                }
            });
            MenuComponentMinimalistButton top = multibwauk.add(new MenuComponentMinimalistButton(CELL_SIZE*2, CELL_SIZE*2+y*(multiblock.getZ()+4)*CELL_SIZE, CELL_SIZE*multiblock.getX(), CELL_SIZE, "+", multiblock.getZ()<multiblock.getMaxSize(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        for(int Y = 0; Y<multiblock.getY(); Y++){
                            addRect(0, 1, 0, .25, CELL_SIZE*2, CELL_SIZE*2+Y*(multiblock.getZ()+4)*CELL_SIZE+CELL_SIZE, CELL_SIZE*2+CELL_SIZE*multiblock.getX(), CELL_SIZE*4+Y*(multiblock.getZ()+4)*CELL_SIZE);
                        }
                    }
                }
            });//add top
            MenuComponentMinimalistButton bottom = multibwauk.add(new MenuComponentMinimalistButton(CELL_SIZE*2, (y+1)*(multiblock.getZ()+4)*CELL_SIZE, CELL_SIZE*multiblock.getX(), CELL_SIZE, "+", multiblock.getZ()<multiblock.getMaxSize(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        for(int Y = 0; Y<multiblock.getY(); Y++){
                            addRect(0, 1, 0, .25, CELL_SIZE*2, (Y+1)*(multiblock.getZ()+4)*CELL_SIZE, CELL_SIZE*2+CELL_SIZE*multiblock.getX(), (Y+1)*(multiblock.getZ()+4)*CELL_SIZE+CELL_SIZE);
                        }
                    }
                }
            });//add bottom
            MenuComponentMinimalistButton left = multibwauk.add(new MenuComponentMinimalistButton(0, CELL_SIZE*3+y*(multiblock.getZ()+4)*CELL_SIZE+CELL_SIZE, CELL_SIZE, CELL_SIZE*multiblock.getZ(), "+", multiblock.getX()<multiblock.getMaxSize(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        for(int Y = 0; Y<multiblock.getY(); Y++){
                            addRect(0, 1, 0, .25, CELL_SIZE, CELL_SIZE*3+Y*(multiblock.getZ()+4)*CELL_SIZE+CELL_SIZE, CELL_SIZE*2, CELL_SIZE*3+Y*(multiblock.getZ()+4)*CELL_SIZE+CELL_SIZE+CELL_SIZE*multiblock.getZ());
                        }
                    }
                }
            });//add left
            MenuComponentMinimalistButton right = multibwauk.add(new MenuComponentMinimalistButton(multiblock.getX()*CELL_SIZE+CELL_SIZE*2, CELL_SIZE*3+y*(multiblock.getZ()+4)*CELL_SIZE+CELL_SIZE, CELL_SIZE, CELL_SIZE*multiblock.getZ(), "+", multiblock.getX()<multiblock.getMaxSize(), true){
                @Override
                public void render(){
                    super.render();
                    if(enabled&&isMouseOver){
                        for(int Y = 0; Y<multiblock.getY(); Y++){
                            addRect(0, 1, 0, .25, multiblock.getX()*CELL_SIZE+CELL_SIZE*2, CELL_SIZE*3+Y*(multiblock.getZ()+4)*CELL_SIZE+CELL_SIZE, multiblock.getX()*CELL_SIZE+CELL_SIZE*2+CELL_SIZE, CELL_SIZE*3+Y*(multiblock.getZ()+4)*CELL_SIZE+CELL_SIZE+CELL_SIZE*multiblock.getZ());
                        }
                    }
                }
            });//add right
            del.setForegroundColor(Core.theme.getRed());top.setForegroundColor(Core.theme.getGreen());bottom.setForegroundColor(Core.theme.getGreen());left.setForegroundColor(Core.theme.getGreen());right.setForegroundColor(Core.theme.getGreen());
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
            for(int z = 0; z<multiblock.getZ(); z++){
                final int row = z;
                MenuComponentMinimalistButton delRow = multibwauk.add(new MenuComponentMinimalistButton(CELL_SIZE, CELL_SIZE*3+(int)((1+z+(y*(multiblock.getZ()+4)))*CELL_SIZE), CELL_SIZE, CELL_SIZE, "-", multiblock.getZ()>multiblock.getMinSize(), true){
                    @Override
                    public void render(){
                        super.render();
                        if(enabled&&isMouseOver){
                            for(int Y = 0; Y<multiblock.getY(); Y++){
                                addRect(1, 0, 0, .25, CELL_SIZE*2, CELL_SIZE*3+(int)((1+row+(Y*(multiblock.getZ()+4)))*CELL_SIZE), CELL_SIZE*2+multiblock.getX()*CELL_SIZE, CELL_SIZE*4+(int)((1+row+(Y*(multiblock.getZ()+4)))*CELL_SIZE));
                            }
                        }
                    }
                });
                delRow.setForegroundColor(Core.theme.getRed());
                delRow.addActionListener((e) -> {
                    deleteZ(row);
                });
                for(int x = 0; x<multiblock.getX(); x++){
                    final int column = x;
                    if(z==0){
                        MenuComponentMinimalistButton delColumn = multibwauk.add(new MenuComponentMinimalistButton((x+2)*CELL_SIZE, CELL_SIZE*3+y*(multiblock.getZ()+4)*CELL_SIZE, CELL_SIZE, CELL_SIZE, "-", multiblock.getX()>multiblock.getMinSize(), true){
                            @Override
                            public void render(){
                                super.render();
                                if(enabled&&isMouseOver){
                                    for(int Y = 0; Y<multiblock.getY(); Y++){
                                        addRect(1, 0, 0, .25, (column+2)*CELL_SIZE, CELL_SIZE*4+Y*(multiblock.getZ()+4)*CELL_SIZE, (column+3)*CELL_SIZE, CELL_SIZE*4+Y*(multiblock.getZ()+4)*CELL_SIZE+multiblock.getZ()*CELL_SIZE);
                                    }
                                }
                            }
                        });
                        delColumn.setForegroundColor(Core.theme.getRed());
                        delColumn.addActionListener((e) -> {
                            deleteX(column);
                        });
                    }
                    multibwauk.add(new MenuComponentVisibleBlock((x+2)*CELL_SIZE, CELL_SIZE*3+(int)((1+z+(y*(multiblock.getZ()+4)))*CELL_SIZE), CELL_SIZE, CELL_SIZE, multiblock, x, y, z));
                }
            }
        }
        MenuComponentMinimalistButton layerBottom = multibwauk.add(new MenuComponentMinimalistButton(0, CELL_SIZE*2+CELL_SIZE*(multiblock.getY()*(multiblock.getZ()+4)), CELL_SIZE*(multiblock.getX()+4), CELL_SIZE, "+", multiblock.getY()<multiblock.getMaxSize(), true));
        layerTop.setForegroundColor(Core.theme.getGreen());layerBottom.setForegroundColor(Core.theme.getGreen());
        layerTop.addActionListener((e) -> {
            expand(0,-1,0);
        });
        layerBottom.addActionListener((e) -> {
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
        done.width = Display.getWidth()/4;
        multibwauk.width = done.x = Display.getWidth()-done.width;
        done.height = 64;
        multibwauk.height = Display.getHeight();
        super.render(millisSinceLastTick);
        Core.applyColor(Core.theme.getTextColor());
        drawCenteredText(done.x, done.height, done.x+done.width, done.height+40, multiblock.getX()+"x"+multiblock.getY()+"x"+multiblock.getZ());
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
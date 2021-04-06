package multiblock;
import java.util.ArrayList;
import planner.menu.MenuEdit;
import simplelibrary.opengl.gui.components.MenuComponent;
public abstract class EditorSpace<T extends Block>{
    public final int x1;
    public final int y1;
    public final int z1;
    public final int x2;
    public final int y2;
    public final int z2;
    public EditorSpace(int x1, int y1, int z1, int x2, int y2, int z2){
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }
    public boolean contains(int x, int y, int z){
        return !(x<x1||y<y1||z<z1||x>x2||y>y2||z>z2);
    }
    public abstract boolean isSpaceValid(T block, int x, int y, int z);
    public abstract void createComponents(MenuEdit editor, ArrayList<MenuComponent> comps, int cellSize);
}
package net.ncplanner.plannerator.planner.editor.overlay;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Queue;
public class EditorOverlay<T extends Block>{
    public boolean active;
    public int mode;
    public final String name;
    public final String description;
    public ArrayList<String> modes = new ArrayList<>();
    public ArrayList<String> modeTooltips = new ArrayList<>();
    public Queue<Decal> decals = new Queue<>();
    public EditorOverlay(String name, String description, boolean defaultActive){
        this.name = name;
        this.description = description;
        active = defaultActive;
    }
    public EditorOverlay addMode(String nam, String tooltip){
        modes.add(nam);
        modeTooltips.add(tooltip);
        return this;
    }
    public void refresh(Multiblock<T> multiblock){}
    public void render(Renderer renderer, float x, float y, float width, float height, T block, Multiblock<T> multiblock){}
}
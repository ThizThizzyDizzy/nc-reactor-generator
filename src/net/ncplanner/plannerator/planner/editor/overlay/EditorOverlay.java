package net.ncplanner.plannerator.planner.editor.overlay;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Queue;
public class EditorOverlay<T extends Block>{
    private boolean active;
    public int mode;
    public final String name;
    public final String description;
    public ArrayList<String> modes = new ArrayList<>();
    public ArrayList<String> modeTooltips = new ArrayList<>();
    public Queue<Decal> decals = new Queue<>();
    public EditorOverlay(String name, String description, boolean defaultActive){
        this.name = name;
        this.description = description;
        int m = Core.overlays.getOrDefault(name, defaultActive?0:-1);
        active = m!=-1;
        if(active)mode = m;
    }
    public EditorOverlay addMode(String nam, String tooltip){
        modes.add(nam);
        modeTooltips.add(tooltip);
        return this;
    }
    public void refresh(Multiblock<T> multiblock){}
    public void render(Renderer renderer, float x, float y, float width, float height, T block, Multiblock<T> multiblock){}
    public boolean isActive(){
        return active;
    }
    public void setActive(boolean active){
        this.active = active;
        Core.overlays.put(name, active?mode:-1);
    }
    public int getMode(){
        return mode;
    }
    public void setMode(int mode){
        this.mode = mode;
        Core.overlays.put(name, active?mode:-1);
    }
}
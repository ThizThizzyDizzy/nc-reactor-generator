package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.List;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import org.lwjgl.openvr.TrackedDevicePose;
public class VRMenuComponentMultiblockSettingsPanel extends VRMenuComponent{
    private final VRMenuEdit editor;
    private boolean refreshNeeded = true;
    public VRMenuComponentMultiblockSettingsPanel(VRMenuEdit editor, float x, float y, float z, float width, float height, float depth, float rx, float ry, float rz){
        super(x, y, z, width, height, depth, rx, ry, rz);
        this.editor = editor;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        if(refreshNeeded)refresh();
        renderer.setColor(Core.theme.getVRPanelOutlineColor());
        renderer.drawCubeOutline(-.005f, -.005f, -.005f, width+.005f, height+.005f, depth+.005f, .005f);//half cm
    }
    public synchronized void refresh(){
        components.clear();
        Multiblock multiblock = editor.getMultiblock();
        List<NCPFElement>[] recipes = multiblock.getSpecificConfiguration().getMultiblockRecipes();
        for(int r = 0; r<recipes.length; r++){
            float size = Math.min(depth, height/recipes[r].size());
            for(int i = 0; i<recipes[r].size(); i++){
                NCPFElement recipe = recipes[r].get(i);
                add(new VRMenuComponentMultiblockRecipe(editor, width/recipes.length*r, height-size*(i+1), 0, width/recipes.length, size, depth, r, recipe));
            }
        }
        refreshNeeded = false;
    }
}
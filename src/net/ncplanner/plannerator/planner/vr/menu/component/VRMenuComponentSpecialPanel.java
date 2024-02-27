package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.List;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.vr.Multitool;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.openvr.TrackedDevicePose;
public class VRMenuComponentSpecialPanel extends VRMenuComponent{
    public int activeTool = -1;
    private final VRMenuEdit editor;
    public AbstractBlock last = null;
    private boolean refreshNeeded;
    public VRMenuComponentSpecialPanel(VRMenuEdit editor, float x, float y, float z, float width, float height, float depth, float rx, float ry, float rz){
        super(x, y, z, width, height, depth, rx, ry, rz);
        this.editor = editor;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        if(activeTool>0){
            AbstractBlock b = editor.getSelectedBlock(activeTool);
            if(last==null||!b.matches(last))refreshNeeded = true;//selected block changed
            last = b;
        }else last = null;
        if(refreshNeeded)refresh();
        //<editor-fold defaultstate="collapsed" desc="Tracked Devices">
        int closest = -1;
        float closestDistance = 0;
        for(int i = 1; i<tdpb.limit(); i++){//don't include HMD
            TrackedDevicePose pose = tdpb.get(i);
            if(pose.bDeviceIsConnected()&&pose.bPoseIsValid()){
                Matrix4f matrix = new Matrix4f(MathUtil.convertHmdMatrix(pose.mDeviceToAbsoluteTracking())).mul(Multitool.editOffsetmatrix);
                Vector3f translation = matrix.getTranslation(new Vector3f());
                float distance = (float)MathUtil.distance(translation, new Vector3d(x-depth/2, y+height/2, z-width/2));
                if(closest==-1||distance<closestDistance){
                    closest = i;
                    closestDistance = distance;
                }
            }
        }
        if(closestDistance>Math.sqrt(Math.pow(width/2,2)+Math.pow(height/2,2)+Math.pow(depth/2,2)))closest = -1;//too far away
        if(activeTool!=closest){
            activeTool = closest;
            refreshNeeded = true;
        }
//</editor-fold>
        renderer.setColor(Core.theme.getVRPanelOutlineColor());
        renderer.drawCubeOutline(-.005f, -.005f, -.005f, width+.005f, height+.005f, depth+.005f, .005f);//half cm
    }
    public void refresh(){
        components.clear();
        if(activeTool<0)return;
        AbstractBlock b = editor.getSelectedBlock(activeTool);
        if(b.hasRecipes()){
            List<? extends IBlockRecipe> recipes = b.getRecipes();
            float size = Math.min(depth, height/recipes.size());
            for(int i = 0; i<recipes.size(); i++){
                NCPFElement recipe = (NCPFElement)recipes.get(i);
                add(new VRMenuComponentBlockRecipe(editor, activeTool, 0, height-size*(i+1), 0, width, size, depth, recipe, i));
            }
        }
        refreshNeeded = false;
    }
}
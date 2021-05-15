package planner.vr.menu.component;
import multiblock.Block;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.openvr.TrackedDevicePose;
import planner.Core;
import planner.vr.Multitool;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
public class VRMenuComponentSpecialPanel extends VRMenuComponent{
    public int activeTool = -1;
    private final VRMenuEdit editor;
    public Block last = null;
    private boolean refreshNeeded;
    public VRMenuComponentSpecialPanel(VRMenuEdit editor, double x, double y, double z, double width, double height, double depth, double rx, double ry, double rz){
        super(x, y, z, width, height, depth, rx, ry, rz);
        this.editor = editor;
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        //<editor-fold defaultstate="collapsed" desc="Tracked Devices">
        int closest = -1;
        double closestDistance = 0;
        for(int i = 1; i<tdpb.limit(); i++){//don't include HMD
            TrackedDevicePose pose = tdpb.get(i);
            if(pose.bDeviceIsConnected()&&pose.bPoseIsValid()){
                Matrix4f matrix = new Matrix4f(VRCore.convert(pose.mDeviceToAbsoluteTracking())).mul(Multitool.editOffsetmatrix);
                Vector3f translation = matrix.getTranslation(new Vector3f());
                double distance = VRCore.distance(translation, new Vector3d(x-depth/2, y+height/2, z-width/2));
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
        Core.applyColor(Core.theme.getVRPanelOutlineColor());
        VRCore.drawCubeOutline(-.005, -.005, -.005, width+.005, height+.005, depth+.005, .005);//half cm
    }
    @Override
    public void tick(){
        super.tick();
        if(activeTool>0){
            Block b = editor.getSelectedBlock(activeTool);
            if(last==null||!b.matches(last))refreshNeeded = true;//selected block changed
            last = b;
        }else last = null;
        if(refreshNeeded)refresh();
    }
    public void refresh(){
        components.clear();
        if(activeTool<0)return;
        Block b = editor.getSelectedBlock(activeTool);
        if(b instanceof multiblock.overhaul.fusion.Block&&!((multiblock.overhaul.fusion.Block)b).template.allRecipes.isEmpty()){
            double size = Math.min(depth, height/((multiblock.overhaul.fusion.Block)b).template.allRecipes.size());
            for(int i = 0; i<((multiblock.overhaul.fusion.Block)b).template.allRecipes.size(); i++){
                multiblock.configuration.overhaul.fusion.BlockRecipe recipe = ((multiblock.overhaul.fusion.Block)b).template.allRecipes.get(i);
                add(new VRMenuComponentFusionBlockRecipe(editor, activeTool, 0, height-size*(i+1), 0, width, size, depth, ((multiblock.overhaul.fusion.Block)b).template, recipe, i));
            }
        }
        if(b instanceof multiblock.overhaul.fissionsfr.Block&&!((multiblock.overhaul.fissionsfr.Block)b).template.allRecipes.isEmpty()){
            double size = Math.min(depth, height/((multiblock.overhaul.fissionsfr.Block)b).template.allRecipes.size());
            for(int i = 0; i<((multiblock.overhaul.fissionsfr.Block)b).template.allRecipes.size(); i++){
                multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe = ((multiblock.overhaul.fissionsfr.Block)b).template.allRecipes.get(i);
                add(new VRMenuComponentOverhaulSFRBlockRecipe(editor, activeTool, 0, height-size*(i+1), 0, width, size, depth, ((multiblock.overhaul.fissionsfr.Block)b).template, recipe, i));
            }
        }
        if(b instanceof multiblock.overhaul.fissionmsr.Block&&!((multiblock.overhaul.fissionmsr.Block)b).template.allRecipes.isEmpty()){
            double size = Math.min(depth, height/((multiblock.overhaul.fissionmsr.Block)b).template.allRecipes.size());
            for(int i = 0; i<((multiblock.overhaul.fissionmsr.Block)b).template.allRecipes.size(); i++){
                multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = ((multiblock.overhaul.fissionmsr.Block)b).template.allRecipes.get(i);
                add(new VRMenuComponentOverhaulMSRBlockRecipe(editor, activeTool, 0, height-size*(i+1), 0, width, size, depth, ((multiblock.overhaul.fissionmsr.Block)b).template, recipe, i));
            }
        }
        refreshNeeded = false;
    }
}
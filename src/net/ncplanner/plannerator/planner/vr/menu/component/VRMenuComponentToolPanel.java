package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.editor.tool.EditorTool;
import net.ncplanner.plannerator.planner.vr.Multitool;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.openvr.TrackedDevicePose;
public class VRMenuComponentToolPanel extends VRMenuComponent{
    public int activeTool = -1;
    private final VRMenuEdit editor;
    private boolean refreshNeeded;
    public VRMenuComponentToolPanel(VRMenuEdit editor, double x, double y, double z, double width, double height, double depth, double rx, double ry, double rz){
        super(x, y, z, width, height, depth, rx, ry, rz);
        this.editor = editor;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        //<editor-fold defaultstate="collapsed" desc="Tracked Devices">
        int closest = -1;
        double closestDistance = 0;
        for(int i = 1; i<tdpb.limit(); i++){//don't include HMD
            TrackedDevicePose pose = tdpb.get(i);
            if(pose.bDeviceIsConnected()&&pose.bPoseIsValid()){
                Matrix4f matrix = new Matrix4f(MathUtil.convertHmdMatrix(pose.mDeviceToAbsoluteTracking())).mul(Multitool.editOffsetmatrix);
                Vector3f translation = matrix.getTranslation(new Vector3f());
                double distance = MathUtil.distance(translation, new Vector3d(x+width/2, y+height/2, z+depth/2));
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
        renderer.drawCubeOutline(-.005, -.005, -.005, width+.005, height+.005, depth+.005, .005);//half cm
    }
    @Override
    public void tick(){
        super.tick();
        if(refreshNeeded)refresh();
    }
    public synchronized void refresh(){
        components.clear();
        if(activeTool<0)return;
        ArrayList<EditorTool> tools = editor.getTools(activeTool);
        double size = Math.min(depth,height/tools.size());
        for(int i = 0; i<tools.size(); i++){
            EditorTool tool = tools.get(i);
            add(new VRMenuComponentEditorTool(editor, 0, height-size*(i+1), depth/2-size/2, size, tool));
        }
        ArrayList<Block> blocks = new ArrayList<>();
        editor.getMultiblock().getAvailableBlocks(blocks);
        int blocksWide = Math.max((int)Math.sqrt(blocks.size())+1,(int)((width-size)/depth));
        double blockSize = Math.min((width-size)/blocksWide,depth);
        for(int i = 0; i<blocks.size(); i++){
            Block availableBlock = blocks.get(i);
            int X = i%blocksWide;
            int Y = i/blocksWide;
            add(new VRMenuComponentEditorListBlock(editor, activeTool, size+blockSize*X,height-blockSize*(Y+1),depth/2-blockSize/2,blockSize,availableBlock, i));
        }
        refreshNeeded = false;
    }
}
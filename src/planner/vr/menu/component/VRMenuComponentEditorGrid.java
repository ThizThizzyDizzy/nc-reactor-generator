package planner.vr.menu.component;
import java.util.ArrayList;
import java.util.HashMap;
import multiblock.Block;
import multiblock.BoundingBox;
import multiblock.Decal;
import multiblock.EditorSpace;
import multiblock.Multiblock;
import multiblock.action.MSRAllShieldsAction;
import multiblock.action.MSRToggleAction;
import multiblock.action.SFRAllShieldsAction;
import multiblock.action.SFRToggleAction;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import planner.Core;
import planner.editor.suggestion.Suggestion;
import planner.editor.tool.EditorTool;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
import simplelibrary.opengl.ImageStash;
public class VRMenuComponentEditorGrid extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final Multiblock multiblock;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .75f;
    private int resonatingTick = 0;
    private float resonatingAlpha = 0;
    private long lastTick = -1;
    private final HashMap<Integer, int[]> deviceover = new HashMap<>();
    public final double blockSize;
    private final EditorSpace editorSpace;
    public VRMenuComponentEditorGrid(double x, double y, double z, double size, VRMenuEdit editor, Multiblock multiblock, EditorSpace editorSpace){
        super(x, y, z, 0, 0, 0, 0, 0, 0);
        BoundingBox bbox = multiblock.getBoundingBox();
        blockSize = size/Math.max(bbox.getWidth(), Math.max(bbox.getHeight(), bbox.getDepth()));
        width = bbox.getWidth()*blockSize;
        height = bbox.getHeight()*blockSize;
        depth = bbox.getDepth()*blockSize;
        this.x-=width/2;
        this.y-=height/2;
        this.z-=depth/2;
        this.editor = editor;
        this.multiblock = multiblock;
        this.editorSpace = editorSpace;
    }
    @Override
    public void tick(){
        for(int i = 0; i<gui.buttonsWereDown.size(); i++){
            ArrayList<Integer> lst = gui.buttonsWereDown.get(i);
            EditorTool tool = editor.getSelectedTool(i);
            if(tool!=null){
                if(!lst.contains(VR.EVRButtonId_k_EButton_SteamVR_Trigger))tool.mouseReset(editorSpace, 0);
                if(!lst.contains(VR.EVRButtonId_k_EButton_SteamVR_Touchpad))tool.mouseReset(editorSpace, 1);
            }
        }
        resonatingTick++;
        if(resonatingTick>resonatingTime)resonatingTick-=resonatingTime;
        lastTick = System.nanoTime();
    }
    @Override
    public void render(TrackedDevicePose.Buffer tdpb){
        long millisSinceLastTick = lastTick==-1?0:(System.nanoTime()-lastTick)/1_000_000;
        float tick = resonatingTick+(Math.max(0, Math.min(1, millisSinceLastTick/50)));
        resonatingAlpha = (float) (-Math.cos(2*Math.PI*tick/resonatingTime)/(2/(resonatingMax-resonatingMin))+(resonatingMax+resonatingMin)/2);
        super.render(tdpb);
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        synchronized(deviceover){
            Core.applyColor(Core.theme.get3DMultiblockOutlineColor());
            VRCore.drawCubeOutline(-blockSize/32,-blockSize/32,-blockSize/32,width+blockSize/32,height+blockSize/32,depth+blockSize/32,blockSize/24);
            for(int id : deviceover.keySet()){
                int[] mouseover = deviceover.get(id);
                if(!isDeviceOver.contains(id))mouseover = null;
                if(mouseover!=null){
                    if(!multiblock.contains(mouseover[0],mouseover[1],mouseover[2]))mouseover = null;
                }
                if(mouseover==null)deviceover.remove(id);
                else deviceover.put(id, mouseover);
            }
        }
        multiblock.forEachPosition((x, y, z) -> {//solid stuff
            Block block = multiblock.getBlock(x, y, z);
            int xx = x;
            int yy = y;
            int zz = z;
            double X = x*blockSize;
            double Y = y*blockSize;
            double Z = z*blockSize;
            double border = blockSize/16;
            if(block!=null){
                block.render(X, Y, Z, blockSize, blockSize, blockSize, true, 1, multiblock, (t) -> {
                    if(!multiblock.contains(xx+t.x, yy+t.y, zz+t.z))return true;
                    Block b = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                    return b==null;
                });
            }
            for(int id : editor.editorTools.keySet()){
                if(isSelected(id, x, y, z)){
                    Core.applyColor(editor.convertToolColor(Core.theme.getSelectionColor(), id));
                    VRCore.drawCubeOutline(X-border, Y-border, Z-border, X+blockSize+border, Y+blockSize+border, Z+blockSize+border, border, (t) -> {
                        boolean d1 = isSelected(id, xx+t[0].x, yy+t[0].y, zz+t[0].z);
                        boolean d2 = isSelected(id, xx+t[1].x, yy+t[1].y, zz+t[1].z);
                        boolean d3 = isSelected(id, xx+t[0].x+t[1].x, yy+t[0].y+t[1].y, zz+t[0].z+t[1].z);
                        if(d1&&d2&&!d3)return true;//both sides, but not the corner
                        if(!d1&&!d2)return true;//neither side
                        return false;
                    });
                }
            }
            //TODO There's a better way to do this, but this'll do for now
            for(Suggestion s : editor.getSuggestions()){
                if(s.affects(x, y, z)){
                    if(s.selected&&s.result!=null){
                        Block b = s.result.getBlock(x, y, z);
                        Core.applyWhite(resonatingAlpha+.5f);
                        double brdr = blockSize/64;
                        if(b==null){
                            VRCore.drawCube(X-brdr, Y-brdr, Z-brdr, blockSize+brdr, blockSize+brdr, blockSize+brdr, 0);
                        }else{
                            b.render(X, Y, Z, blockSize, blockSize, blockSize, false, resonatingAlpha+.5f, s.result, (t) -> {
                                return true;
                            });
                        }
                    }
                    Core.applyColor(Core.theme.getSuggestionOutlineColor());
                    border = blockSize/40f;
                    if(s.selected)border*=3;
                    VRCore.drawCubeOutline(X-border, Y-border, Z-border, X+blockSize+border, Y+blockSize+border, Z+blockSize+border, border, (t) -> {
                        boolean d1 = s.affects(xx+t[0].x, yy+t[0].y, zz+t[0].z);
                        boolean d2 = s.affects(xx+t[1].x, yy+t[1].y, zz+t[1].z);
                        boolean d3 = s.affects(xx+t[0].x+t[1].x, yy+t[0].y+t[1].y, zz+t[0].z+t[1].z);
                        if(d1&&d2&&!d3)return true;//both sides, but not the corner
                        if(!d1&&!d2)return true;//neither side
                        return false;
                    });
                }
            }
        });
        synchronized(deviceover){
            for(int id : deviceover.keySet()){
                if(id==VR.k_unTrackedDeviceIndex_Hmd)continue;//don't do mouseover for headset
                int[] mouseover = deviceover.get(id);
                if(mouseover!=null){
                    double X = mouseover[0]*blockSize;
                    double Y = mouseover[1]*blockSize;
                    double Z = mouseover[2]*blockSize;
                    double border = blockSize/16;
                    Core.applyColor(Core.theme.get3DMultiblockOutlineColor());
                    VRCore.drawCubeOutline(X-border/2, Y-border/2, Z-border/2, X+blockSize+border/2, Y+blockSize+border/2, Z+blockSize+border/2, border);
                    Core.applyColor(Core.theme.getEditorMouseoverLineColor());
                    X+=blockSize/2;
                    Y+=blockSize/2;
                    Z+=blockSize/2;
                    VRCore.drawCube(0, Y-border/2, Z-border/2, X-blockSize/2, Y+border/2, Z+border/2, 0);//NX
                    VRCore.drawCube(X-border/2, 0, Z-border/2, X+border/2, Y-blockSize/2, Z+border/2, 0);//NY
                    VRCore.drawCube(X-border/2, Y-border/2, 0, X+border/2, Y+border/2, Z-blockSize/2, 0);//NZ
                    VRCore.drawCube(X+blockSize/2, Y-border/2, Z-border/2, width, Y+border/2, Z+border/2, 0);//PX
                    VRCore.drawCube(X-border/2, Y+blockSize/2, Z-border/2, X+border/2, height, Z+border/2, 0);//PY
                    VRCore.drawCube(X-border/2, Y-border/2, Z+blockSize/2, X+border/2, Y+border/2, depth, 0);//PZ
                }
            }
        }
        multiblock.forEachPosition((x, y, z) -> {//transparent stuff
            Block block = multiblock.getBlock(x, y, z);
            int xx = x;
            int yy = y;
            int zz = z;
            double X = x*blockSize;
            double Y = y*blockSize;
            double Z = z*blockSize;
            double border = blockSize/16;
            if(block!=null){
                for(int id : editor.editorTools.keySet()){
                    boolean recipeMatches = false;
                    if(multiblock instanceof OverhaulSFR){
                        multiblock.overhaul.fissionsfr.Block bl = (multiblock.overhaul.fissionsfr.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulSFRBlockRecipe(id))recipeMatches = true;
                    }
                    if(multiblock instanceof OverhaulMSR){
                        multiblock.overhaul.fissionmsr.Block bl = (multiblock.overhaul.fissionmsr.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulMSRBlockRecipe(id))recipeMatches = true;
                    }
                    if(multiblock instanceof OverhaulFusionReactor){
                        multiblock.overhaul.fusion.Block bl = (multiblock.overhaul.fusion.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulFusionBlockRecipe(id))recipeMatches = true;
                    }
                    if(recipeMatches){
                        Core.applyColor(editor.convertToolColor(Core.theme.getSelectionColor(), id), resonatingAlpha);
                        VRCore.drawCube(X-border/4, Y-border/4, Z-border/4, X+blockSize+border/4, Y+blockSize+border/4, Z+blockSize+border/4, 0);
                    }
                }
            }
            if(multiblock instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multiblock).getLocationCategory(x, y, z)==OverhaulFusionReactor.LocationCategory.PLASMA){
                Core.applyWhite();
                VRCore.drawCube(X, Y, Z, X+blockSize, Y+blockSize, Z+blockSize, ImageStash.instance.getTexture("/textures/overhaul/fusion/plasma.png"), (t) -> {
                    Block b = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                    return b==null&&((OverhaulFusionReactor)multiblock).getLocationCategory(xx+t.x, yy+t.y, zz+t.z)!=OverhaulFusionReactor.LocationCategory.PLASMA;
                });
            }
            for(int id : editor.editorTools.keySet()){
                if(editor.isControlPressed(id)){
                    if(block==null||(editor.isShiftPressed(id)&&block.canBeQuickReplaced())){
                        if(editorSpace.isSpaceValid(editor.getSelectedBlock(id), x, y, z)&&multiblock.isValid(editor.getSelectedBlock(id), x, y, z)){
                            editor.getSelectedBlock(id).render(X, Y, Z, blockSize, blockSize, blockSize, false, resonatingAlpha, null, (t) -> {
                                return true;
                            });
                        }
                    }
                }
            }
            for(Object o : multiblock.decals){
                Decal decal = (Decal)o;
                if(decal.x==x&&decal.y==y&&decal.z==z){
                    decal.render3D(X, Y, Z, blockSize);
                }
            }
            for(int id : editor.editorTools.keySet()){
                if(isSelected(id, x, y, z)){
                    Core.applyColor(editor.convertToolColor(Core.theme.getSelectionColor(), id), .5f);
                    VRCore.drawCube(X-border/4, Y-border/4, Z-border/4, X+blockSize+border/4, Y+blockSize+border/4, Z+blockSize+border/4, 0, (t) -> {
                        Block o = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                        return !isSelected(id, xx+t.x, yy+t.y, zz+t.z)&&o==null;
                    });
                }
            }
        });
        for(int i : editor.editorTools.keySet()){
            editor.getSelectedTool(i).drawVRGhosts(editorSpace, 0, 0, 0, width, height, depth, blockSize, (editor.getSelectedBlock(i)==null?0:Core.getTexture(editor.getSelectedBlock(i).getTexture())));
        }
    }
    @Override
    public void onDeviceMoved(int device, Matrix4f matrix){
        super.onDeviceMoved(device, matrix);
        Vector3f pos = matrix.getTranslation(new Vector3f());
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        synchronized(deviceover){
            deviceover.put(device, new int[]{(int)(x/blockSize),(int)(y/blockSize),(int)(z/blockSize)});
        }
        for(int i : gui.buttonsWereDown.get(device)){
            deviceDragged(device, matrix, i);
        }
        if(Double.isNaN(x)||Double.isNaN(y)||Double.isNaN(z))return;
        BoundingBox bbox = multiblock.getBoundingBox();
        int blockX = Math.max(bbox.x1, Math.min(bbox.x2, (int)(x/blockSize)));
        int blockY = Math.max(bbox.y1, Math.min(bbox.y2, (int)(y/blockSize)));
        int blockZ = Math.max(bbox.z1, Math.min(bbox.z2, (int)(z/blockSize)));
        editor.getSelectedTool(device).mouseMoved(this, editorSpace, blockX, blockY, blockZ);
    }
    @Override
    public void onDeviceMovedElsewhere(int device, Matrix4f matrix){
        super.onDeviceMovedElsewhere(device, matrix);
        synchronized(deviceover){
            if(deviceover.containsKey(device))editor.getSelectedTool(device).mouseMovedElsewhere(this, editorSpace);
            deviceover.remove(device);
        }
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        int mButton = -1;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger)mButton = 0;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Touchpad)mButton = 1;
        if(mButton==-1)return;
        int[] mouseover;
        synchronized(deviceover){
            if(!deviceover.containsKey(device))return;
            mouseover = deviceover.get(device);
        }
        int blockX = mouseover[0];
        int blockY = mouseover[1];
        int blockZ = mouseover[2];
        if(pressed){
            if(editor.getSelectedTool(device).isEditTool()&&multiblock instanceof OverhaulSFR&&editor.isShiftPressed(device)&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, blockY, blockZ))!=null&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, blockY, blockZ)).template.shield){
                multiblock.overhaul.fissionsfr.Block b = (multiblock.overhaul.fissionsfr.Block) multiblock.getBlock(blockX, blockY, blockZ);
                if(b!=null){
                    if(editor.isControlPressed(device))editor.action(new SFRAllShieldsAction(!b.isToggled), true);
                    else editor.action(new SFRToggleAction(b), true);
                }
            }else if(editor.getSelectedTool(device).isEditTool()&&multiblock instanceof OverhaulMSR&&editor.isShiftPressed(device)&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, blockY, blockZ))!=null&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, blockY, blockZ)).template.shield){
                multiblock.overhaul.fissionmsr.Block b = (multiblock.overhaul.fissionmsr.Block) multiblock.getBlock(blockX, blockY, blockZ);
                if(b!=null){
                    if(editor.isControlPressed(device))editor.action(new MSRAllShieldsAction(!b.isToggled), true);
                    else editor.action(new MSRToggleAction(b), true);
                }
            }else{
                //TODO VR: PICK BLOCK
                editor.getSelectedTool(device).mousePressed(this, editorSpace, blockX, blockY, blockZ, mButton);
            }
        }else{
            editor.getSelectedTool(device).mouseReleased(this, editorSpace, blockX, blockY, blockZ, mButton);
        }
    }
    private void deviceDragged(int device, Matrix4f matrix, int button){
        int mButton = -1;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger)mButton = 0;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Touchpad)mButton = 1;
        if(mButton==-1)return;
        Vector3f pos = matrix.getTranslation(new Vector3f());
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        BoundingBox bbox = multiblock.getBoundingBox();
        int blockX = Math.max(bbox.x1, Math.min(bbox.x2, (int)(x/blockSize)));
        int blockY = Math.max(bbox.y1, Math.min(bbox.y2, (int)(y/blockSize)));
        int blockZ = Math.max(bbox.z1, Math.min(bbox.z2, (int)(z/blockSize)));
        editor.getSelectedTool(device).mouseDragged(this, editorSpace, blockX, blockY, blockZ, mButton);
    }
    public boolean isSelected(int id, int x, int y, int z){
        return editor.isSelected(id, x, y, z);
    }
    @Override
    public String getTooltip(int device){
        if(!deviceover.containsKey(device))return null;
        int[] mouseover = deviceover.get(device);
        Block block = multiblock.getBlock(mouseover[0],mouseover[1],mouseover[2]);
        return block==null?null:block.getTooltip(multiblock);
    }
}
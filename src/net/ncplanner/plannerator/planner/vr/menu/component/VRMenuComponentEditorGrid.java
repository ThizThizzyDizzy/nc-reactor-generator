package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.MSRAllShieldsAction;
import net.ncplanner.plannerator.multiblock.editor.action.MSRToggleAction;
import net.ncplanner.plannerator.multiblock.editor.action.SFRAllShieldsAction;
import net.ncplanner.plannerator.multiblock.editor.action.SFRToggleAction;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.tool.EditorTool;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
public class VRMenuComponentEditorGrid extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final Multiblock multiblock;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .75f;
    private float resonatingTick = 0;
    private float resonatingAlpha = 0;
    private final HashMap<Integer, int[]> deviceover = new HashMap<>();
    public final float blockSize;
    private final EditorSpace editorSpace;
    private final ArrayList<EditorOverlay> overlays = new ArrayList<>();
    public VRMenuComponentEditorGrid(float x, float y, float z, float size, VRMenuEdit editor, Multiblock multiblock, EditorSpace editorSpace){
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
    public void render(Renderer renderer, TrackedDevicePose.Buffer tdpb, double deltaTime){
        for(int i = 0; i<gui.buttonsWereDown.size(); i++){
            ArrayList<Integer> lst = gui.buttonsWereDown.get(i);
            EditorTool tool = editor.getSelectedTool(i);
            if(tool!=null){
                if(!lst.contains(VR.EVRButtonId_k_EButton_SteamVR_Trigger))tool.mouseReset(editorSpace, 0);
                if(!lst.contains(VR.EVRButtonId_k_EButton_SteamVR_Touchpad))tool.mouseReset(editorSpace, 1);
            }
        }
        resonatingTick+=deltaTime*20;
        if(resonatingTick>resonatingTime)resonatingTick-=resonatingTime;
        resonatingAlpha = (float) (-Math.cos(2*Math.PI*resonatingTick/resonatingTime)/(2/(resonatingMax-resonatingMin))+(resonatingMax+resonatingMin)/2);
        super.render(renderer, tdpb, deltaTime);
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        synchronized(deviceover){
            renderer.setColor(Core.theme.get3DMultiblockOutlineColor());
            renderer.drawCubeOutline(-blockSize/32,-blockSize/32,-blockSize/32,width+blockSize/32,height+blockSize/32,depth+blockSize/32,blockSize/24);
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
            AbstractBlock block = multiblock.getBlock(x, y, z);
            int xx = x;
            int yy = y;
            int zz = z;
            float X = x*blockSize;
            float Y = y*blockSize;
            float Z = z*blockSize;
            float border = blockSize/16;
            if(block!=null){
                block.render(renderer, X, Y, Z, blockSize, blockSize, blockSize, overlays, 1, multiblock, (t) -> {
                    if(!multiblock.contains(xx+t.x, yy+t.y, zz+t.z))return true;
                    AbstractBlock b = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                    return b==null;
                });
            }
            for(int id : editor.editorTools.keySet()){
                if(isSelected(id, x, y, z)){
                    renderer.setColor(editor.convertToolColor(Core.theme.getSelectionColor(), id));
                    renderer.drawCubeOutline(X-border, Y-border, Z-border, X+blockSize+border, Y+blockSize+border, Z+blockSize+border, border, (t) -> {
                        boolean d1 = isSelected(id, xx+t[0].x, yy+t[0].y, zz+t[0].z);
                        boolean d2 = isSelected(id, xx+t[1].x, yy+t[1].y, zz+t[1].z);
                        boolean d3 = isSelected(id, xx+t[0].x+t[1].x, yy+t[0].y+t[1].y, zz+t[0].z+t[1].z);
                        if(d1&&d2&&!d3)return true;//both sides, but not the corner
                        if(!d1&&!d2)return true;//neither side
                        return false;
                    });
                }
            }
            {
                ArrayList<Function<Direction[], Boolean>> edgeFuncs = new ArrayList<>();
                boolean selected = false;
                for(Suggestion s : editor.getSuggestions()){
                    if(s.affects(x, y, z)){
                        if(s.selected&&s.result!=null){
                            AbstractBlock b = s.result.getBlock(x, y, z);
                            renderer.setWhite(resonatingAlpha+.5f);
                            float brdr = blockSize/64;
                            if(b==null){
                                renderer.drawCube(X-brdr, Y-brdr, Z-brdr, blockSize+brdr, blockSize+brdr, blockSize+brdr, null);
                            }else{
                                b.render(renderer, X, Y, Z, blockSize, blockSize, blockSize, null, resonatingAlpha+.5f, s.result, (t) -> {
                                    return true;
                                });
                            }
                        }
                        if(s.selected)selected = true;
                        edgeFuncs.add((t) -> {
                            boolean d1 = s.affects(xx+t[0].x, yy+t[0].y, zz+t[0].z);
                            boolean d2 = s.affects(xx+t[1].x, yy+t[1].y, zz+t[1].z);
                            boolean d3 = s.affects(xx+t[0].x+t[1].x, yy+t[0].y+t[1].y, zz+t[0].z+t[1].z);
                            if(d1&&d2&&!d3)return true;//both sides, but not the corner
                            if(!d1&&!d2)return true;//neither side
                            return false;
                        });
                    }
                }
                renderer.setColor(Core.theme.getSuggestionOutlineColor());
                border = blockSize/40f;
                if(selected)border*=3;
                renderer.drawCubeOutline(X-border, Y-border, Z-border, X+blockSize+border, Y+blockSize+border, Z+blockSize+border, border, (t) -> {
                    for(Function<Direction[], Boolean> func : edgeFuncs){
                        if(func.apply(t))return true;
                    }
                    return false;
                });
            }
        });
        synchronized(deviceover){
            for(int id : deviceover.keySet()){
                if(id==VR.k_unTrackedDeviceIndex_Hmd)continue;//don't do mouseover for headset
                int[] mouseover = deviceover.get(id);
                if(mouseover!=null){
                    renderer.setColor(Core.theme.get3DMultiblockOutlineColor());
                    editor.getSymmetry().apply(mouseover[0], mouseover[1], mouseover[2], multiblock.getBoundingBox(), (bx, by, bz) -> {
                        float X = bx*blockSize;
                        float Y = by*blockSize;
                        float Z = bz*blockSize;
                        float border = blockSize/16;
                        renderer.drawCubeOutline(X-border/2, Y-border/2, Z-border/2, X+blockSize+border/2, Y+blockSize+border/2, Z+blockSize+border/2, border);
                    });
                    float X = mouseover[0]*blockSize;
                    float Y = mouseover[1]*blockSize;
                    float Z = mouseover[2]*blockSize;
                    float border = blockSize/16;
                    renderer.setColor(Core.theme.getEditorMouseoverLineColor());
                    X+=blockSize/2;
                    Y+=blockSize/2;
                    Z+=blockSize/2;
                    renderer.drawCube(0, Y-border/2, Z-border/2, X-blockSize/2, Y+border/2, Z+border/2, null);//NX
                    renderer.drawCube(X-border/2, 0, Z-border/2, X+border/2, Y-blockSize/2, Z+border/2, null);//NY
                    renderer.drawCube(X-border/2, Y-border/2, 0, X+border/2, Y+border/2, Z-blockSize/2, null);//NZ
                    renderer.drawCube(X+blockSize/2, Y-border/2, Z-border/2, width, Y+border/2, Z+border/2, null);//PX
                    renderer.drawCube(X-border/2, Y+blockSize/2, Z-border/2, X+border/2, height, Z+border/2, null);//PY
                    renderer.drawCube(X-border/2, Y-border/2, Z+blockSize/2, X+border/2, Y+border/2, depth, null);//PZ
                }
            }
        }
        multiblock.forEachPosition((x, y, z) -> {//transparent stuff
            AbstractBlock block = multiblock.getBlock(x, y, z);
            int xx = x;
            int yy = y;
            int zz = z;
            float X = x*blockSize;
            float Y = y*blockSize;
            float Z = z*blockSize;
            float border = blockSize/16;
            if(block!=null){
                for(int id : editor.editorTools.keySet()){
                    boolean recipeMatches = false;
                    if(multiblock instanceof OverhaulSFR){
                        net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block bl = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulSFRBlockRecipe(id))recipeMatches = true;
                    }
                    if(multiblock instanceof OverhaulMSR){
                        net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block bl = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulMSRBlockRecipe(id))recipeMatches = true;
                    }
                    if(multiblock instanceof OverhaulFusionReactor){
                        net.ncplanner.plannerator.multiblock.overhaul.fusion.Block bl = (net.ncplanner.plannerator.multiblock.overhaul.fusion.Block)block;
                        if(bl.recipe!=null&&bl.recipe==editor.getSelectedOverhaulFusionBlockRecipe(id))recipeMatches = true;
                    }
                    if(recipeMatches){
                        renderer.setColor(editor.convertToolColor(Core.theme.getSelectionColor(), id), resonatingAlpha);
                        renderer.drawCube(X-border/4, Y-border/4, Z-border/4, X+blockSize+border/4, Y+blockSize+border/4, Z+blockSize+border/4, null);
                    }
                }
            }
            if(multiblock instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multiblock).getLocationCategory(x, y, z)==OverhaulFusionReactor.LocationCategory.PLASMA){
                renderer.setWhite();
                renderer.drawCube(X, Y, Z, X+blockSize, Y+blockSize, Z+blockSize, TextureManager.getImageRaw("/textures/overhaul/fusion/plasma.png"), (t) -> {
                    AbstractBlock b = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                    return b==null&&((OverhaulFusionReactor)multiblock).getLocationCategory(xx+t.x, yy+t.y, zz+t.z)!=OverhaulFusionReactor.LocationCategory.PLASMA;
                });
            }
            for(int id : editor.editorTools.keySet()){
                if(editor.isControlPressed(id)){
                    if(block==null||(editor.isShiftPressed(id)&&block.canBeQuickReplaced())){
                        if(editorSpace.isSpaceValid(editor.getSelectedBlock(id), x, y, z)&&multiblock.isValid(editor.getSelectedBlock(id), x, y, z)){
                            editor.getSelectedBlock(id).render(renderer, X, Y, Z, blockSize, blockSize, blockSize, null, resonatingAlpha, null, (t) -> {
                                return true;
                            });
                        }
                    }
                }
            }
            for(Object o : multiblock.decals){
                Decal decal = (Decal)o;
                if(decal.x==x&&decal.y==y&&decal.z==z){
                    decal.render3D(renderer, X, Y, Z, blockSize);
                }
            }
            for(int id : editor.editorTools.keySet()){
                if(isSelected(id, x, y, z)){
                    renderer.setColor(editor.convertToolColor(Core.theme.getSelectionColor(), id), .5f);
                    renderer.drawCube(X-border/4, Y-border/4, Z-border/4, X+blockSize+border/4, Y+blockSize+border/4, Z+blockSize+border/4, null, (t) -> {
                        AbstractBlock o = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                        return !isSelected(id, xx+t.x, yy+t.y, zz+t.z)&&o==null;
                    });
                }
            }
        });
        for(int i : editor.editorTools.keySet()){
            editor.getSelectedTool(i).drawVRGhosts(renderer, editorSpace, 0, 0, 0, width, height, depth, blockSize, (editor.getSelectedBlock(i)==null?null:editor.getSelectedBlock(i).getTexture()));
        }
    }
    @Override
    public void onDeviceMoved(int device, Matrix4f matrix){
        super.onDeviceMoved(device, matrix);
        Vector3f pos = matrix.getTranslation(new Vector3f());
        float x = pos.x;
        float y = pos.y;
        float z = pos.z;
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
            if(editor.getSelectedTool(device).isEditTool()&&multiblock instanceof OverhaulSFR&&editor.isShiftPressed(device)&&((net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, blockY, blockZ))!=null&&((net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, blockY, blockZ)).template.shield){
                net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block) multiblock.getBlock(blockX, blockY, blockZ);
                if(b!=null){
                    if(editor.isControlPressed(device))editor.action(new SFRAllShieldsAction(!b.isToggled), true);
                    else editor.action(new SFRToggleAction(b), true);
                }
            }else if(editor.getSelectedTool(device).isEditTool()&&multiblock instanceof OverhaulMSR&&editor.isShiftPressed(device)&&((net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, blockY, blockZ))!=null&&((net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, blockY, blockZ)).template.shield){
                net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block) multiblock.getBlock(blockX, blockY, blockZ);
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
        float x = pos.x;
        float y = pos.y;
        float z = pos.z;
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
        AbstractBlock block = multiblock.getBlock(mouseover[0],mouseover[1],mouseover[2]);
        return block==null?null:block.getTooltip(multiblock);
    }
}
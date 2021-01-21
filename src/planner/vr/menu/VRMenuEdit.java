package planner.vr.menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.action.ClearSelectionAction;
import multiblock.action.CopyAction;
import multiblock.action.DeselectAction;
import multiblock.action.MoveAction;
import multiblock.action.PasteAction;
import multiblock.action.SelectAction;
import multiblock.action.SetSelectionAction;
import multiblock.action.SetblocksAction;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import planner.Core;
import planner.editor.ClipboardEntry;
import planner.editor.Editor;
import planner.editor.tool.CopyTool;
import planner.editor.tool.CutTool;
import planner.editor.tool.EditorTool;
import planner.editor.tool.LineTool;
import planner.editor.tool.MoveTool;
import planner.editor.tool.PasteTool;
import planner.editor.tool.PencilTool;
import planner.editor.tool.RectangleTool;
import planner.editor.tool.SelectionTool;
import planner.vr.VRGUI;
import planner.vr.VRMenu;
public class VRMenuEdit extends VRMenu implements Editor{
    private final HashMap<Integer, ArrayList<EditorTool>> editorTools = new HashMap<>();
    private final Multiblock multiblock;
    public HashMap<Integer, ArrayList<ClipboardEntry>> clipboard = new HashMap<>();
    public final HashMap<Integer, ArrayList<int[]>> selection = new HashMap<>();
    private HashMap<Integer, EditorTool> copy = new HashMap<>();
    private HashMap<Integer, EditorTool> cut = new HashMap<>();
    private HashMap<Integer, EditorTool> paste = new HashMap<>();
    public VRMenuEdit(VRGUI gui, VRMenu parent, Multiblock multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
    }
    @Override
    public Multiblock getMultiblock(){
        return multiblock;
    }
    @Override
    public ArrayList<ClipboardEntry> getClipboard(int id){
        if(!clipboard.containsKey(id)){
            createTools(id);
        }
        return clipboard.get(id);
    }
    @Override
    public ArrayList<int[]> getSelection(int id){
        if(!selection.containsKey(id)){
            createTools(id);
        }
        return selection.get(id);
    }
    private ArrayList<EditorTool> getTools(int id){
        if(!selection.containsKey(id)){
            createTools(id);
        }
        return editorTools.get(id);
    }
    private void createTools(int id){
        ArrayList<EditorTool> tools = new ArrayList<>();
        tools.add(new MoveTool(this, id));
        tools.add(new SelectionTool(this, id));
        tools.add(new PencilTool(this, id));
        tools.add(new LineTool(this, id));
        tools.add(new RectangleTool(this, id));
        editorTools.put(id, tools);
        copy.put(id, new CopyTool(this, id));
        cut.put(id, new CutTool(this, id));
        paste.put(id, new PasteTool(this, id));
    }
    @Override
    public void addSelection(int id, ArrayList<int[]> sel){
        synchronized(selection){
            for(int[] is : selection.get(id)){
                for(Iterator<int[]> it = sel.iterator(); it.hasNext();){
                    int[] i = it.next();
                    if(i[0]==is[0]&&i[1]==is[1]&&i[2]==is[2]){
                        it.remove();
                    }
                }
            }
            selection.get(id).addAll(sel);
        }
    }
    @Override
    public boolean isSelected(int id, int x, int y, int z){
        synchronized(selection){
            for(int[] s : selection.get(id)){
                if(s==null)continue;//THIS SHOULD NEVER HAPPEN but it does anyway
                if(s[0]==x&&s[1]==y&&s[2]==z)return true;
            }
        }
        return false;
    }
    @Override
    public void setCoolantRecipe(int idx){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void setUnderhaulFuel(int idx){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void setFusionCoolantRecipe(int idx){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void setFusionRecipe(int idx){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void setTurbineRecipe(int idx){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void clearSelection(int id){
        multiblock.action(new ClearSelectionAction(this, id), true);
    }
    @Override
    public void select(int id, int x1, int y1, int z1, int x2, int y2, int z2){
        ArrayList<int[]> is = new ArrayList<>();
        for(int x = Math.min(x1,x2); x<=Math.max(x1,x2); x++){
            for(int y = Math.min(y1,y2); y<=Math.max(y1,y2); y++){
                for(int z = Math.min(z1,z2); z<=Math.max(z1,z2); z++){
                    is.add(new int[]{x,y,z});
                }
            }
        }
        select(id, is);
    }
    @Override
    public void copySelection(int id, int x, int y, int z){
        synchronized(clipboard){
            clipboard.get(id).clear();
            synchronized(selection){
                if(selection.get(id).isEmpty()){
                    if(!editorTools.get(id).contains(copy.get(id))){
                        editorTools.get(id).add(copy.get(id));
                        throw new UnsupportedOperationException("Add copy component");
                    }
                    return;
                }
                if(x==-1||y==-1||z==-1)return;
                for(int[] is : selection.get(id)){
                    Block b = multiblock.getBlock(is[0], is[1], is[2]);
                    clipboard.get(id).add(new ClipboardEntry(is[0]-x, is[1]-y, is[2]-z, b==null?null:b.copy(b.x-x, b.y-y, b.z-z)));
                }
            }
        }
        if(!editorTools.get(id).contains(paste.get(id))){
            editorTools.get(id).add(paste.get(id));
            throw new UnsupportedOperationException("Add paste component");
        }
    }
    @Override
    public void cutSelection(int id, int x, int y, int z){
        synchronized(clipboard){
            clipboard.get(id).clear();
        }
        synchronized(selection){
            if(selection.get(id).isEmpty()){
                if(!editorTools.get(id).contains(cut.get(id))){
                    editorTools.get(id).add(cut.get(id));
                    throw new UnsupportedOperationException("Add cut component");
                }
                return;
            }
        }
        copySelection(id, x,y,z);
        SetblocksAction ac = new SetblocksAction(null);
        synchronized(selection){
            for(int[] i : selection.get(id)){
                ac.add(i[0], i[1], i[2]);
            }
        }
        multiblock.action(ac, true);
        clearSelection(id);
    }
    @Override
    public Block getSelectedBlock(int id){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void setblocks(int id, SetblocksAction set){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void cloneSelection(int id, int x, int y, int z){
        multiblock.action(new CopyAction(this, id, selection.get(id), x, y, z), true);
    }
    @Override
    public void moveSelection(int id, int x, int y, int z){
        multiblock.action(new MoveAction(this, id, selection.get(id), x, y, z), true);
    }
    @Override
    public void pasteSelection(int id, int x, int y, int z){
        synchronized(clipboard){
            multiblock.action(new PasteAction(clipboard.get(id), x, y, z), true);
        }
    }
    @Override
    public void selectGroup(int id, int x, int y, int z){
        ArrayList<Block> g = multiblock.getGroup(multiblock.getBlock(x, y, z));
        if(g==null){
            select(id, 0, 0, 0, multiblock.getX()-1, multiblock.getY()-1, multiblock.getZ()-1);
            return;
        }
        ArrayList<int[]> is = new ArrayList<>();
        for(Block b : g){
            is.add(new int[]{b.x,b.y,b.z});
        }
        select(id, is);
    }
    @Override
    public void deselectGroup(int id, int x, int y, int z){
        ArrayList<Block> g = multiblock.getGroup(multiblock.getBlock(x, y, z));
        if(g==null){
            deselect(id, 0, 0, 0, multiblock.getX()-1, multiblock.getY()-1, multiblock.getZ()-1);
            return;
        }
        ArrayList<int[]> is = new ArrayList<>();
        for(Block b : g){
            is.add(new int[]{b.x,b.y,b.z});
        }
        deselect(id, is);
    }
    @Override
    public void selectCluster(int id, int x, int y, int z){
        if(multiblock instanceof OverhaulSFR){
            OverhaulSFR osfr = (OverhaulSFR) multiblock;
            OverhaulSFR.Cluster c = osfr.getCluster(osfr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            select(id, is);
        }
        if(multiblock instanceof OverhaulMSR){
            OverhaulMSR omsr = (OverhaulMSR) multiblock;
            OverhaulMSR.Cluster c = omsr.getCluster(omsr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            select(id, is);
        }
        if(multiblock instanceof OverhaulFusionReactor){
            OverhaulFusionReactor ofr = (OverhaulFusionReactor) multiblock;
            OverhaulFusionReactor.Cluster c = ofr.getCluster(ofr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            select(id, is);
        }
    }
    @Override
    public void deselectCluster(int id, int x, int y, int z){
        if(multiblock instanceof OverhaulSFR){
            OverhaulSFR osfr = (OverhaulSFR) multiblock;
            OverhaulSFR.Cluster c = osfr.getCluster(osfr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            deselect(id, is);
        }
        if(multiblock instanceof OverhaulMSR){
            OverhaulMSR omsr = (OverhaulMSR) multiblock;
            OverhaulMSR.Cluster c = omsr.getCluster(omsr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            deselect(id, is);
        }
        if(multiblock instanceof OverhaulFusionReactor){
            OverhaulFusionReactor ofr = (OverhaulFusionReactor) multiblock;
            OverhaulFusionReactor.Cluster c = ofr.getCluster(ofr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            deselect(id, is);
        }
    }
    @Override
    public void deselect(int id, int x1, int y1, int z1, int x2, int y2, int z2){
        ArrayList<int[]> is = new ArrayList<>();
        for(int x = Math.min(x1,x2); x<=Math.max(x1,x2); x++){
            for(int y = Math.min(y1,y2); y<=Math.max(y1,y2); y++){
                for(int z = Math.min(z1,z2); z<=Math.max(z1,z2); z++){
                    is.add(new int[]{x,y,z});
                }
            }
        }
        deselect(id, is);
    }
    public void select(int id, ArrayList<int[]> is){
        if(isControlPressed(id)){
            multiblock.action(new SelectAction(this, id, is), true);
        }else{
            multiblock.action(new SetSelectionAction(this, id, is), true);
        }
    }
    public void setSelection(int id, ArrayList<int[]> is){
        multiblock.action(new SetSelectionAction(this, id, is), true);
    }
    public void deselect(int id, ArrayList<int[]> is){
        if(!isControlPressed(id)){
            clearSelection(id);
            return;
        }
        multiblock.action(new DeselectAction(this, id, is), true);
    }
    private boolean isControlPressed(int id){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
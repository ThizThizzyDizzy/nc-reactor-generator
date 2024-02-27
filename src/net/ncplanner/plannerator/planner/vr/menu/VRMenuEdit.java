package net.ncplanner.plannerator.planner.vr.menu;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.Symmetry;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.ClearSelectionAction;
import net.ncplanner.plannerator.multiblock.editor.action.DeselectAction;
import net.ncplanner.plannerator.multiblock.editor.action.SelectAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetSelectionAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.DebugInfoProvider;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.editor.ClipboardEntry;
import net.ncplanner.plannerator.planner.editor.Editor;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.editor.suggestion.SuggestorTask;
import net.ncplanner.plannerator.planner.editor.tool.CopyTool;
import net.ncplanner.plannerator.planner.editor.tool.CutTool;
import net.ncplanner.plannerator.planner.editor.tool.EditorTool;
import net.ncplanner.plannerator.planner.editor.tool.LineTool;
import net.ncplanner.plannerator.planner.editor.tool.MoveTool;
import net.ncplanner.plannerator.planner.editor.tool.PasteTool;
import net.ncplanner.plannerator.planner.editor.tool.PencilTool;
import net.ncplanner.plannerator.planner.editor.tool.RectangleTool;
import net.ncplanner.plannerator.planner.editor.tool.SelectionTool;
import net.ncplanner.plannerator.planner.vr.Multitool;
import net.ncplanner.plannerator.planner.vr.VRGUI;
import net.ncplanner.plannerator.planner.vr.VRMenu;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentButton;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentEditorGrid;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentMultiblockOutputPanel;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentMultiblockSettingsPanel;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentSpecialPanel;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentToolPanel;
import org.joml.Matrix4f;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import static org.lwjgl.openvr.VR.ETrackedControllerRole_TrackedControllerRole_LeftHand;
import static org.lwjgl.openvr.VR.ETrackedControllerRole_TrackedControllerRole_RightHand;
import static org.lwjgl.openvr.VR.ETrackedDeviceProperty_Prop_ControllerRoleHint_Int32;
import org.lwjgl.openvr.VRSystem;
public class VRMenuEdit extends VRMenu implements Editor, DebugInfoProvider{
    public VRMenuComponentButton done = add(new VRMenuComponentButton(-.25f, 1.75f, -1, .5f, .125f, .1f, 0, 0, 0, "Done", true, false));
    public VRMenuComponentButton resize = add(new VRMenuComponentButton(1, 1.625f, -.5f, 1, .125f, .1f, 0, -90, 0, "Resize", true, false));
    public final HashMap<Integer, ArrayList<EditorTool>> editorTools = new HashMap<>();
    private final Multiblock multiblock;
    public HashMap<Integer, ArrayList<ClipboardEntry>> clipboard = new HashMap<>();
    public final HashMap<Integer, ArrayList<int[]>> selection = new HashMap<>();
    private HashMap<Integer, EditorTool> copy = new HashMap<>();
    private HashMap<Integer, EditorTool> cut = new HashMap<>();
    private HashMap<Integer, EditorTool> paste = new HashMap<>();
    private HashMap<Integer, Integer> selectedTool = new HashMap<>();
    public HashMap<Integer, Integer> selectedBlock = new HashMap<>();
    public HashMap<Integer, Integer> selectedBlockRecipe = new HashMap<>();
    private VRMenuComponentToolPanel leftToolPanel  = add(new VRMenuComponentToolPanel(this, -.625f, 1, -1, .5f, .5f, .1f, 0, 0, 0));
    private VRMenuComponentToolPanel rightToolPanel  = add(new VRMenuComponentToolPanel(this, .125f, 1, -1, .5f, .5f, .1f, 0, 0, 0));
    private VRMenuComponentSpecialPanel leftSpecialPanel  = add(new VRMenuComponentSpecialPanel(this, -1, .625f, -.125f, .5f, 1, .1f, 0, 90, 0));
    private VRMenuComponentSpecialPanel rightSpecialPanel  = add(new VRMenuComponentSpecialPanel(this, -1, .625f, .625f, .5f, 1, .1f, 0, 90, 0));
    private VRMenuComponentMultiblockSettingsPanel multiblockSettingsPanel  = add(new VRMenuComponentMultiblockSettingsPanel(this, 1, .625f, -.5f, 1, 1, .1f, 0, -90, 0));
    private VRMenuComponentMultiblockOutputPanel multiblockOutputPanel  = add(new VRMenuComponentMultiblockOutputPanel(this, .5f, .25f, 1.2f, 1, 1.5f, .05f, 0, 180, 0));
    public VRMenuComponentEditorGrid grid;
    private boolean closing = false;//used for the closing menu animation
    private long lastTick = -1;
    private float openProgress = 0;
    private static final int openTime = 40;
    private static final int openDist = 100;//fly in from 100m away
    private static final float openSmooth = 5;//smooths out the incoming
    private final float openTargetZ;
    private long lastChange = 0;
    public ArrayList<Suggestion> suggestions = new ArrayList<>();
    private ArrayList<Suggestor> suggestors = new ArrayList<>();
    private Task suggestionTask;
    private Symmetry symmetry = new Symmetry();
    public VRMenuEdit(VRGUI gui, Multiblock multiblock){
        super(gui, null);
        this.multiblock = multiblock;
        grid = add(new VRMenuComponentEditorGrid(0, 1, 0, 1, this, multiblock, (EditorSpace)multiblock.getEditorSpaces().get(0)));
        openTargetZ = grid.z;
        done.setTooltip("Stop editing this multiblock and return to the main menu");
        resize.setTooltip("Resize the multiblock\nWARNING: This clears the edit history! (undo/redo)");
        done.addActionListener(() -> {
            closing = true;
        });
        resize.addActionListener(() -> {
            multiblock.openVRResizeMenu(gui, this);
        });
    }
    @Override
    public void onOpened(){
        super.onOpened();
        multiblock.recalculate();
    }
    @Override
    public void render(Renderer renderer, TrackedDevicePose.Buffer tdpb, double deltaTime){
        lastTick = System.nanoTime();
        if(lastChange!=multiblock.lastChangeTime){
            lastChange = multiblock.lastChangeTime;
            recalculateSuggestions();
        }
        if(closing){
            openProgress-=deltaTime*20;
            if(openProgress<=0)gui.open(new VRMenuMain(gui));
        }else if(openProgress<openTime){
            openProgress++;
        }
        long millisSinceLastTick = lastTick==-1?0:(System.nanoTime()-lastTick)/1_000_000;
        float partialTick = millisSinceLastTick/50f;
        float progress = closing?Math.max((openProgress-partialTick)/openTime,0):(Math.min((openProgress+partialTick)/openTime,1));
        float dist = (float)(openDist-openDist*Math.pow(Math.sin(Math.PI*progress/2), 1/openSmooth));
        grid.z = openTargetZ-dist;
        super.render(renderer, tdpb, deltaTime);
        //<editor-fold defaultstate="collapsed" desc="Tracked Devices">
        for(int i = 1; i<tdpb.limit(); i++){
            TrackedDevicePose pose = tdpb.get(i);
            if(pose.bDeviceIsConnected()&&pose.bPoseIsValid()){
                IntBuffer pError = IntBuffer.allocate(1);
                int role = VRSystem.VRSystem_GetInt32TrackedDeviceProperty(i, ETrackedDeviceProperty_Prop_ControllerRoleHint_Int32, pError);
                if(role==ETrackedControllerRole_TrackedControllerRole_LeftHand||role==ETrackedControllerRole_TrackedControllerRole_RightHand){
                    Matrix4f matrix = new Matrix4f(MathUtil.convertHmdMatrix(pose.mDeviceToAbsoluteTracking()));
                    renderer.pushModel(matrix.mul(Multitool.editOffsetmatrix));
                    if(getSelectedBlock(i)!=null){
                        renderer.setWhite();
                        float radius = grid.blockSize/4;
                        renderer.drawCube(-radius, -radius, -radius, radius, radius, radius, getSelectedBlock(i).getTexture());
                    }
                    renderer.popModel();
                }
            }
        }
//</editor-fold>
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
    public ArrayList<EditorTool> getTools(int id){
        if(!editorTools.containsKey(id)){
            createTools(id);
        }
        return editorTools.get(id);
    }
    private void createTools(int id){
        if(id<0)throw new IllegalArgumentException("Cannot create tools with a negative ID! ("+id+")");
        ArrayList<EditorTool> tools = new ArrayList<>();
        tools.add(new MoveTool(this, id));
        tools.add(new SelectionTool(this, id));
        tools.add(new PencilTool(this, id));
        tools.add(new LineTool(this, id));
        tools.add(new RectangleTool(this, id));
        selectedTool.put(id, 2);//pencil
        selectedBlock.put(id, 0);
        selectedBlockRecipe.put(id, 0);
        editorTools.put(id, tools);
        selection.put(id, new ArrayList<>());
        copy.put(id, new CopyTool(this, id));
        cut.put(id, new CutTool(this, id));
        paste.put(id, new PasteTool(this, id));
        refreshToolPanels();
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
    public boolean hasSelection(int id){
        return !selection.get(id).isEmpty();
    }
    @Override
    public void setMultiblockRecipe(int recipeType, int idx){}
    @Override
    public void clearSelection(int id){
        action(new ClearSelectionAction(this, id), true);
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
                    if(!getTools(id).contains(copy.get(id))){
                        getTools(id).add(copy.get(id));
                        throw new UnsupportedOperationException("Add copy component");
                    }
                    return;
                }
                if(x==-1||y==-1||z==-1)return;
                for(int[] is : selection.get(id)){
                    AbstractBlock b = multiblock.getBlock(is[0], is[1], is[2]);
                    clipboard.get(id).add(new ClipboardEntry(is[0]-x, is[1]-y, is[2]-z, b==null?null:b.copy(b.x-x, b.y-y, b.z-z)));
                }
            }
        }
        if(!getTools(id).contains(paste.get(id))){
            getTools(id).add(paste.get(id));
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
                if(!getTools(id).contains(cut.get(id))){
                    getTools(id).add(cut.get(id));
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
        action(ac, true);
        clearSelection(id);
    }
    @Override
    public AbstractBlock getSelectedBlock(int id){
        if(!selectedBlock.containsKey(id))return null;
        ArrayList<AbstractBlock> blocks = new ArrayList<>();
        multiblock.getAvailableBlocks(blocks);
        return blocks.get(selectedBlock.get(id));
    }
    @Override
    public void setblocks(int id, SetblocksAction set){
        for(Iterator<BlockPos> it = set.locations.iterator(); it.hasNext();){
            BlockPos b = it.next();
            if(hasSelection(id)&&!isSelected(id, b.x, b.y, b.z))it.remove();
            else if(isControlPressed(id)){
                if(set.block==null){
                    if(multiblock.getBlock(b.x, b.y, b.z)!=null&&!multiblock.getBlock(b.x, b.y, b.z).matches(getSelectedBlock(0)))it.remove();
                }else{
                    if(multiblock.getBlock(b.x, b.y, b.z)!=null&&!isShiftPressed(id)){
                        it.remove();
                    }else if(multiblock.getBlock(b.x, b.y, b.z)!=null&&!multiblock.getBlock(b.x, b.y, b.z).canBeQuickReplaced()){
                        it.remove();
                    }else if(multiblock.getBlock(b.x, b.y, b.z)==null||multiblock.getBlock(b.x, b.y, b.z)!=null&&isShiftPressed(id)){
                        if(!multiblock.isValid(set.block, b.x, b.y, b.z))it.remove();
                    }
                }
            }
        }
        if(set.block!=null&&set.block.hasRecipes()){
            set.block.setRecipe(getSelectedBlockRecipe(id));
        }
        action(set, true);
    }
    @Override
    public void selectGroup(int id, int x, int y, int z){
        ArrayList<AbstractBlock> g = multiblock.getGroup(multiblock.getBlock(x, y, z));
        if(g==null){
            selectAll(id);
            return;
        }
        ArrayList<int[]> is = new ArrayList<>();
        for(AbstractBlock b : g){
            is.add(new int[]{b.x,b.y,b.z});
        }
        select(id, is);
    }
    @Override
    public void deselectGroup(int id, int x, int y, int z){
        ArrayList<AbstractBlock> g = multiblock.getGroup(multiblock.getBlock(x, y, z));
        if(g==null){
            deselectAll(id);
            return;
        }
        ArrayList<int[]> is = new ArrayList<>();
        for(AbstractBlock b : g){
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
            for(AbstractBlock b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            select(id, is);
        }
        if(multiblock instanceof OverhaulMSR){
            OverhaulMSR omsr = (OverhaulMSR) multiblock;
            OverhaulMSR.Cluster c = omsr.getCluster(omsr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(AbstractBlock b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            select(id, is);
        }
        if(multiblock instanceof OverhaulFusionReactor){
            OverhaulFusionReactor ofr = (OverhaulFusionReactor) multiblock;
            OverhaulFusionReactor.Cluster c = ofr.getCluster(ofr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(AbstractBlock b : c.blocks){
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
            for(AbstractBlock b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            deselect(id, is);
        }
        if(multiblock instanceof OverhaulMSR){
            OverhaulMSR omsr = (OverhaulMSR) multiblock;
            OverhaulMSR.Cluster c = omsr.getCluster(omsr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(AbstractBlock b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            deselect(id, is);
        }
        if(multiblock instanceof OverhaulFusionReactor){
            OverhaulFusionReactor ofr = (OverhaulFusionReactor) multiblock;
            OverhaulFusionReactor.Cluster c = ofr.getCluster(ofr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(AbstractBlock b : c.blocks){
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
            action(new SelectAction(this, id, is), true);
        }else{
            action(new SetSelectionAction(this, id, is), true);
        }
    }
    public void setSelection(int id, ArrayList<int[]> is){
        action(new SetSelectionAction(this, id, is), true);
    }
    public void deselect(int id, ArrayList<int[]> is){
        if(!isControlPressed(id)){
            clearSelection(id);
            return;
        }
        action(new DeselectAction(this, id, is), true);
    }
    @Override
    public boolean isControlPressed(int id){
        return gui.buttonsWereDown.get(id).contains(VR.EVRButtonId_k_EButton_IndexController_A);
    }
    @Override
    public boolean isShiftPressed(int id){
        return gui.buttonsWereDown.get(id).contains(VR.EVRButtonId_k_EButton_IndexController_B);
    }
    @Override
    public boolean isAltPressed(int id){
        return false;//TODO VR: what button for this?
    }
    @Override
    public EditorTool getSelectedTool(int id){
        EditorTool tool = getTools(id).get(selectedTool.get(id));
        if(!(tool instanceof CutTool)){//selecting a non-copy tool, remove the cut tool!
            if(getTools(id).remove(cut.get(id)))refreshToolPanels();
        }
        if(!(tool instanceof CopyTool)){//selecting a non-copy tool, remove the copy tool!
            if(getTools(id).remove(copy.get(id)))refreshToolPanels();
        }
        if(!(tool instanceof PasteTool)){//selecting a non-paste tool, remove the paste tool!
            if(getTools(id).remove(paste.get(id)))refreshToolPanels();
        }
        return tool;
    }
    private void refreshToolPanels(){
        leftToolPanel.activeTool = -2;
        rightToolPanel.activeTool = -2;
        leftSpecialPanel.activeTool = -2;
        rightSpecialPanel.activeTool = -2;
    }
    public void setSelectedTool(EditorTool tool){
        selectedTool.put(tool.id, getTools(tool.id).indexOf(tool));
    }
    @Override
    public Color convertToolColor(Color color, int id){
        float hue = color.getHue();
        if(id%2==0)hue+=20/360f;//is this 0-1?
        else hue-=20/360f;//is this 0-1?
        return Color.fromHSB(hue, color.getSaturation(), color.getBrightness());
    }//doesn't do alpha
    @Override
    public NCPFElement getSelectedBlockRecipe(int id){
        AbstractBlock block = getSelectedBlock(id);
        return (NCPFElement)(block.hasRecipes()?block.getRecipes().get(selectedBlockRecipe.get(id)):null);
    }
    public VRMenu alreadyOpen(){
        openProgress = openTime;
        return this;
    }
    @Override
    public ArrayList<Suggestion> getSuggestions(){
        return suggestions;
    }
    private void recalculateSuggestions(){
        suggestions.clear();
        //TODO VR: clear suggestions list
        Thread thread = new Thread(() -> {
            ArrayList<Suggestion> suggestions = new ArrayList<>();
            Task theTask = suggestionTask = new Task("Calculating suggestions");
            Task genTask = suggestionTask.addSubtask(new Task("Generating suggestions"));
            HashMap<Suggestor, SuggestorTask> genTasks = new HashMap<>();
            ArrayList<Suggestor> suggestors = new ArrayList<>(this.suggestors);//no modifying mid-suggestion
            for(Suggestor s : suggestors){
                if(s.isActive()){
                    genTasks.put(s, genTask.addSubtask(new SuggestorTask(s)));
                }
            }
            Task consolidateTask = suggestionTask.addSubtask(new Task("Consolidating suggestions"));
            Task sortTask = suggestionTask.addSubtask(new Task("Sorting suggestions"));
            for(Suggestor s : suggestors){
                if(suggestionTask!=theTask)return;//somethin' else is making suggestions!
                if(s.isActive()){
                    SuggestorTask task = genTasks.get(s);
                    s.generateSuggestions(multiblock, s.new SuggestionAcceptor(multiblock, task){
                        @Override
                        protected void accepted(Suggestion suggestion){
                            suggestions.add(suggestion);
                        }
                        @Override
                        protected void denied(Suggestion suggestion){}
                    });
                    task.finish();
                }
            }
            //why test them again? they've already been accepted.
//            for(Iterator<Suggestion> it = suggestions.iterator(); it.hasNext();){
//                Suggestion s = it.next();
//                if(!s.test(multiblock))it.remove();
//            }
            int total = suggestions.size();
            for(int i = 0; i<suggestions.size(); i++){
                if(suggestionTask!=theTask)return;//somethin' else is making suggestions!
                Suggestion suggestion = suggestions.get(i);
                for(Iterator<Suggestion> it = suggestions.iterator(); it.hasNext();){
                    Suggestion s = it.next();
                    if(s==suggestion)continue;//literally the same exact thing
                    if(s.equals(suggestion))it.remove();
                    consolidateTask.progress = 1-(suggestions.size()/(float)total);
                }
            }
            consolidateTask.finish();
            if(suggestionTask!=theTask)return;//somethin' else is making suggestions!
            Collections.sort(suggestions);
            if(suggestionTask!=theTask)return;//somethin' else is making suggestions!
            sortTask.finish();
            for(Suggestion s : suggestions){
                //TODO VR: add to suggestions list
            }
            this.suggestions = suggestions;
            suggestionTask = null;
        }, "Suggestion calculation thread");
        thread.setDaemon(true);
        thread.start();
    }
    @Override
    public Task getTask(){
        Task task = multiblock.getTask();
        if(task==null)task = suggestionTask;
        return task;
    }
    @Override
    public void action(Action action, boolean allowUndo){
        if(multiblock.calculationPaused)multiblock.recalculate();
        multiblock.action(action, true, allowUndo);
        if(Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
            ((CuboidalMultiblock)multiblock).buildDefaultCasing();
            multiblock.recalculate();
        }
    }
    private void selectAll(int id){
        ArrayList<int[]> sel = new ArrayList<>();
        multiblock.forEachPosition((x, y, z) -> {
            sel.add(new int[]{x,y,z});
        });
        setSelection(id, sel);
    }
    private void deselectAll(int id){
        setSelection(id, new ArrayList<>());
    }
    @Override
    public HashMap<String, Object> getDebugInfo(HashMap<String, Object> debugInfo){
        debugInfo.put("multiblock-type", multiblock.getDefinitionName());
        return debugInfo;
    }
    @Override
    public Symmetry getSymmetry(){
        return symmetry;
    }
}
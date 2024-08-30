package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.PartCount;
import net.ncplanner.plannerator.multiblock.Symmetry;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.ClearSelectionAction;
import net.ncplanner.plannerator.multiblock.editor.action.DeselectAction;
import net.ncplanner.plannerator.multiblock.editor.action.SelectAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetMultiblockRecipeAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetSelectionAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.DebugInfoProvider;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.editor.ClipboardEntry;
import net.ncplanner.plannerator.planner.editor.Editor;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor.SuggestionAcceptor;
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
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.DropdownList;
import net.ncplanner.plannerator.planner.gui.menu.component.DropdownPile;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.MulticolumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.Scrollable;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.TextView;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentEditorGrid;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentEditorListBlock;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentEditorTool;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentMultiblockProgressBar;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentElement;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentSuggestion;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentSuggestor;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentTurbineRotorGraph;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.legacy.LegacyGridLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuDialog;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuOverlaySettings;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuSymmetrySettings;
import net.ncplanner.plannerator.planner.module.Module;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.MultiblockRecipeElement;
import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;
public class MenuEdit extends Menu implements Editor, DebugInfoProvider{
    private final ArrayList<EditorTool> editorTools = new ArrayList<>();
    public ArrayList<ClipboardEntry> clipboard = new ArrayList<>();
    private EditorTool copy = new CopyTool(this, 0);
    private MenuComponentEditorTool copyComp = new MenuComponentEditorTool(copy);
    private EditorTool cut = new CutTool(this, 0);
    private MenuComponentEditorTool cutComp = new MenuComponentEditorTool(cut);
    private EditorTool paste = new PasteTool(this, 0);
    private MenuComponentEditorTool pasteComp = new MenuComponentEditorTool(paste);
    private Task suggestionTask;
    private float scrollMagnitude = 1;
    private double zoomScrollMagnitude = 0.5;
    private double scaleFac = 1.5;
    private AbstractBlock lastSelectedBlock;
    private boolean autoRecalc = true;
    {
        editorTools.add(new MoveTool(this, 0));
        editorTools.add(new SelectionTool(this, 0));
        editorTools.add(new PencilTool(this, 0));
        editorTools.add(new LineTool(this, 0));
        editorTools.add(new RectangleTool(this, 0));
    }
    public final Multiblock multiblock;
    public static final int partSize = 48;
    public static final int partsWide = 7;
    private final Button back = add(new Button("Back", true).setTooltip("Stop editing this multiblock and return to the main menu"));
    private final Button undo = add(new Button("Undo", false){
        @Override
        public void drawText(Renderer renderer, double deltaTime){
            float tallness = height*3/2;
            renderer.drawOval(x+width/2, y+height/2+tallness-height/16, width, tallness, height/8, 160, 151, 10);
            renderer.drawRegularPolygon(x+width/4, y+height*.5625f, width/4, 3, -5);
        }
    }.setTooltip("Undo (Ctrl+"+(Core.invertUndoRedo?"Y":"Z")+")"));
    private final Button redo = add(new Button("Redo", false){
        @Override
        public void drawText(Renderer renderer, double deltaTime){
            float tallness = height*3/2;
            renderer.drawOval(x+width/2, y+height/2+tallness-height/16, width, tallness, height/8, 160, 150, 9);
            renderer.drawRegularPolygon(x+width*3/4, y+height*.5625f, width/4, 3, 5);
        }
    }.setTooltip("Redo (Ctrl+"+(Core.invertUndoRedo?"Z":"Y")+")"));
    public final MulticolumnList parts = add(new MulticolumnList(0, 0, 0, 0, partSize, partSize, partSize/2));
    public final TextBox partsSearch = add(new TextBox(0, 0, 0, 0, Core.autoBuildCasing?"-port":"", true, "Search"){
        @Override
        public void onCharTyped(char c){
            super.onCharTyped(c);
            refreshPartsList();
        }
        @Override
        public void onKeyEvent(int key, int scancode, int action, int mods){
            super.onKeyEvent(key, scancode, action, mods);
            refreshPartsList();
        }
        @Override
        public void onMouseButton(double x, double y, int button, int action, int mods){
            super.onMouseButton(x, y, button, action, mods);
            if(button==GLFW_MOUSE_BUTTON_RIGHT&&action==GLFW_PRESS){
                text = "";
                refreshPartsList();
                MenuEdit.this.focusedComponent = this;
                isFocused = true;
            }
        }
    });
    public final Scrollable multibwauk = add(new Scrollable(0, 0, 0, 0, 32, 32));
    private final Button zoomOut = add(new Button("Zoom out", true));
    private final Button zoomIn = add(new Button("Zoom in", true));
    private final Button resize = add(new Button("Resize", true).setTooltip("Resize the multiblock\nWARNING: This clears the edit history! (undo/redo)"));
    public final DropdownPile dropdownSelectors = add(new DropdownPile(32));
    private final DropdownList blockRecipe;
    private final TextView textBox = add(new TextView(0, 0, 0, 0, 24, 24));
    private final Button editMetadata = add(new Button("", true).setTooltip("Modify the multiblock metadata"));
    private final Button symmetrySettings = add(new Button("Symmetry", true));
    private final Button overlaySettings = add(new Button("Overlays", true));
    public final SingleColumnList tools = add(new SingleColumnList(0, 0, 0, 0, partSize/2));
    private final MenuComponentTurbineRotorGraph graph;
    private final Button partsList = add(new Button("Parts List", true, true).setTooltip("View a parts list for this multiblock"));
    private final Button generate = add(new Button("Generate", true, true).setTooltip("Generate or improve this multiblock"));
    private final Button recalc = add(new Button("Recalculate", true).setTooltip("Recalculate the entire multiblock\nCtrl-click to queue multiple actions without recalculating"));
    private final Button calcStep = add(new Button("Step", true).setTooltip("Perform one step of calculation"));
    public final ToggleBox toggle3D = add(new ToggleBox(0, 0, 0, 0, "3D View", false).setTooltip("Toggle 3D multiblock view\n(Rotate with arrow keys)"));
    private final MenuComponentMultiblockProgressBar progress = add(new MenuComponentMultiblockProgressBar(this, 0, 0, 0, 0));;
    private final SingleColumnList suggestionList = add(new SingleColumnList(0, 0, 0, 0, partSize/2));
    private final DropdownList suggestorSettings = add(new DropdownList(0, 0, 0, 0){
        @Override
        public void draw(double deltaTime){
            Renderer renderer = new Renderer();
            if(!isDown){
                Color col = isMouseFocused?Core.theme.getComponentMouseoverColor(0):Core.theme.getComponentColor(0);
                renderer.setColor(col);
                renderer.fillRect(x, y, x+width, y+height);
                renderer.setColor(Core.theme.getComponentTextColor(0));
                float border = height/6;
                float lineThickness = height/9;
                renderer.fillRect(x+border, y+border, x+width-border, y+border+lineThickness);
                renderer.fillRect(x+border, y+height/2-lineThickness/2, x+width-border, y+height/2+lineThickness/2);
                renderer.fillRect(x+border, y+height-border-lineThickness, x+width-border, y+height-border);
            }
        }
    });
    public final ArrayList<int[]> selection = new ArrayList<>();
    private ArrayList<Suggestion> suggestions = new ArrayList<>();
    private ArrayList<Suggestor> suggestors = new ArrayList<>();
    public ArrayList<EditorOverlay> overlays = new ArrayList<>();
    private double scale = 4;
    private double minScale = 0.5;
    private double maxScale = 16;
    public int CELL_SIZE = (int) (16*scale);
    private int LAYER_GAP = CELL_SIZE/2;
    private long lastChange = 0;
    public float maxYRot = 80f;
    public float xRot = 30;
    public float yRot = 30;
    public Symmetry symmetry = new Symmetry();
    public MenuEdit(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
        suggestionList.optimizeForLargeComponentCount = true;
        if(Core.recoveryMode){
            autoRecalc = false;
        }
        this.multiblock = multiblock;
        graph = add(new MenuComponentTurbineRotorGraph(0, 0, 0, 0, 32, (multiblock instanceof OverhaulTurbine)?(OverhaulTurbine)multiblock:null));
        multibwauk.scrollMagnitude = CELL_SIZE*scrollMagnitude;
        back.addAction(() -> {
            suggestionTask = null;
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SplitTransitionX.slideOut((parts.x+parts.width)/gui.getWidth()), 5));
        });
        undo.addAction(() -> {
            multiblock.undo(autoRecalc);
            if(Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
                if(autoRecalc){
                    ((CuboidalMultiblock)multiblock).buildDefaultCasing();
                    multiblock.recalculate();
                }
            }
        });
        redo.addAction(() -> {
            multiblock.redo(autoRecalc);
            if(Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
                if(autoRecalc){
                    ((CuboidalMultiblock)multiblock).buildDefaultCasing();
                    multiblock.recalculate();
                }
            }
        });
        resize.addAction(() -> {
            Menu resizeMenu = multiblock.getResizeMenu(gui, this);
            if(resizeMenu!=null)gui.open(new MenuTransition(gui, this, resizeMenu, MenuTransition.SlideTransition.slideFrom(1, 0), 5));
        });
        zoomOut.addAction(() -> {
            zoomOut(1);
        });
        zoomIn.addAction(() -> {
            zoomIn(1);
        });
        editMetadata.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuMultiblockMetadata(gui, this, multiblock), MenuTransition.SlideTransition.slideTo(0, 1), 4));
        });
        symmetrySettings.addAction(() -> {
            new MenuSymmetrySettings(gui, this, symmetry).open(); 
        });
        overlaySettings.addAction(() -> {
            new MenuOverlaySettings(gui, this, overlays, multiblock).open(); 
        });
        partsList.addAction(() -> {
            new MenuDialog(gui, this){
                {
                    LegacyGridLayout gl = new LegacyGridLayout(48, 1);
                    gl.width = 512;
                    ArrayList<PartCount> parts = multiblock.getPartsList();
                    parts.forEach((t) -> {
                        gl.add(new Label(t.getImage(), t.count+"x "+t.name).alignLeft());
                    });
                    setContent(gl);
                    addButton("Close", true);
                }
            }.open();
        });
        generate.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuGenerator(gui, this, multiblock), MenuTransition.SlideTransition.slideFrom(0, 1), 5));
        });
        recalc.addAction(() -> {
            if(autoRecalc){
                if(isControlPressed(0)){
                    autoRecalc = false;
                }else{
                    multiblock.recalculate();
                }
            }else{
                if(Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
                    ((CuboidalMultiblock)multiblock).buildDefaultCasing();
                }
                multiblock.recalculate();
                autoRecalc = true;
            }
            refreshOverlays();
        });
        calcStep.addAction(() -> {
            multiblock.recalcStep();
            refreshOverlays();
        });
        refreshPartsList();
        List<NCPFElement>[] recipeLists = multiblock.getSpecificConfiguration().getMultiblockRecipes();
        for(List<NCPFElement> recipes : recipeLists){
            DropdownList list;
            dropdownSelectors.add(list = new DropdownList(0, 0, 0, 32, true));
            for(NCPFElement elem : recipes)list.add(new MenuComponentElement(elem));
        }
        blockRecipe = dropdownSelectors.add(new DropdownList(0, 0, 0, 32, true));
        refreshBlockRecipes();
        for(EditorTool tool : editorTools){
            tools.add(new MenuComponentEditorTool(tool));
        }
        tools.setSelectedIndex(2);
        multiblock.getSuggestors(suggestors);
        for(Module m : Core.modules){
            if(m.isActive()){
                m.getSuggestors(multiblock, suggestors);
                m.getEditorOverlays(multiblock, overlays);
            }
        }
        for(Suggestor suggestor : suggestors){
            suggestorSettings.add(new MenuComponentSuggestor(this, suggestor));
        }
        refreshOverlays();
    }
    private boolean recalculateOnOpen = true;
    @Override
    public synchronized void onOpened(){
        toggle3D.isToggledOn = Core.editor3dView&&!Core.recoveryMode;
        Core.delCircle = true;
        Core.circleSize = CELL_SIZE;
        editMetadata.text = multiblock.getName().isEmpty()?"Edit Metadata":(multiblock.getName()+" | Edit Metadata");
        List<NCPFElement>[] recipeLists = multiblock.getSpecificConfiguration().getMultiblockRecipes();
        NCPFElement[] recipes = multiblock.getMultiblockRecipes();
        for(int i = 0; i<recipeLists.length; i++){
            DropdownList list = (DropdownList)dropdownSelectors.components.get(i);
            int idx = -1;
            for(int id = 0; id<recipeLists[i].size(); id++){
                for(NCPFElement recipe : recipes){
                    if(recipeLists[i].get(id).definition.matches(recipe.definition))idx = id;
                }
            }
            if(idx>-1)list.setSelectedIndex(idx);
        }
        multibwauk.components.clear();
        ArrayList<EditorSpace> editorSpaces = multiblock.getEditorSpaces();
        float lastX = 0;
        float lastY = 0;
        float nextY = 0;
        for(EditorSpace space : editorSpaces){
            ArrayList<Component> comps = new ArrayList<>();
            space.createComponents(this, comps, CELL_SIZE);
            for(int i = 0; i<comps.size(); i++){
                Component comp = comps.get(i);
                multibwauk.add(comp);
                if(lastX!=0&&lastX+LAYER_GAP+comp.width>multibwauk.width-multibwauk.horizScrollbarHeight){
                    lastY = nextY;
                    lastX = 0;
                }
                comp.x = lastX+LAYER_GAP/2;
                lastX += comp.width+LAYER_GAP;
                comp.y = lastY+LAYER_GAP/2;
                nextY = lastY+comp.height+LAYER_GAP;
            }
        }
        if(recalculateOnOpen&&!Core.recoveryMode){
            autoRecalc = true;
            multiblock.recalculate();
        }
        recalculateOnOpen = true;
    }
    @Override
    public void onClosed(){
        if(!Core.recoveryMode)Core.editor3dView = toggle3D.isToggledOn;
        if(multiblock.calculationPaused)multiblock.recalculate();
        super.onClosed();
    }
    @Override
    public void render3d(double deltaTime){
        super.render3d(deltaTime);
        if(toggle3D.isToggledOn){
            Multiblock mb = getMultiblock();
            if(mb!=null){
                if(glfwGetKey(Core.window, GLFW_KEY_LEFT)==GLFW_PRESS)xRot-=deltaTime*40;
                if(glfwGetKey(Core.window, GLFW_KEY_RIGHT)==GLFW_PRESS)xRot+=deltaTime*40;
                if(glfwGetKey(Core.window, GLFW_KEY_UP)==GLFW_PRESS)yRot = MathUtil.min(maxYRot, MathUtil.max(-maxYRot, yRot-=deltaTime*40));
                if(glfwGetKey(Core.window, GLFW_KEY_DOWN)==GLFW_PRESS)yRot = MathUtil.min(maxYRot, MathUtil.max(-maxYRot, yRot+=deltaTime*40));
                Renderer renderer = new Renderer();
                renderer.projection(new Matrix4f().setOrtho(0, gui.getWidth(), 0, gui.getHeight(), 1f, 10000f));
                BoundingBox bbox = mb.getBoundingBox();
                float size = MathUtil.max(bbox.getWidth(), MathUtil.max(bbox.getHeight(), bbox.getDepth()));
                size/=mb.get3DPreviewScale();
                renderer.pushModel(new Matrix4f().setTranslation(toggle3D.x+toggle3D.width/2, gui.getHeight()-(toggle3D.y-toggle3D.width/2), -1000)
                        .scale(.625f, .625f, .625f)
                        .scale(toggle3D.width, toggle3D.width, toggle3D.width)
                        .rotate((float)MathUtil.toRadians(yRot), 1, 0, 0)
                        .rotate((float)MathUtil.toRadians(xRot), 0, 1, 0)
                        .scale(1/size, 1/size, 1/size)
                        .translate(-bbox.getWidth()/2f, -bbox.getHeight()/2f, -bbox.getDepth()/2f));
                draw3D();
                renderer.popModel();
                renderer.projection(new Matrix4f().setPerspective(45, gui.getWidth()/(float)gui.getHeight(), 0.1f, 100));
            }
        }
    }
    @Override
    public synchronized void render2d(double deltaTime){
        if(lastChange!=multiblock.lastChangeTime){
            lastChange = multiblock.lastChangeTime;
            recalculateSuggestions();
        }
        recalc.text = isControlPressed(0)&&autoRecalc?"Queue Actions":"Recalculate";
        textBox.setText(multiblock.getFullTooltip());
        float lastX = 0;
        float lastY = 0;
        float nextY = 0;
        for(int i = 0; i<multibwauk.components.size(); i++){
            Component comp = multibwauk.components.get(i);
            if(lastX!=0&&lastX+LAYER_GAP+comp.width>multibwauk.width-multibwauk.horizScrollbarHeight){
                lastY = nextY;
                lastX = 0;
            }
            comp.x = lastX+LAYER_GAP/2;
            lastX += comp.width+LAYER_GAP;
            comp.y = lastY+LAYER_GAP/2;
            nextY = lastY+comp.height+LAYER_GAP;
        }
        tools.x = textBox.x = back.x = progress.x = 0;
        partsSearch.width = parts.width = partsWide*partSize+parts.vertScrollbarWidth*(parts.hasVertScrollbar()?1:0);
        tools.width = partSize;
        partsSearch.x = parts.x = tools.width+partSize/4;
        partsList.x = symmetrySettings.x = textBox.width = graph.width = multibwauk.x = parts.x+parts.width;
        recalc.width = calcStep.width = textBox.width/2;
        toggle3D.height = partsSearch.y = recalc.height = calcStep.height = suggestorSettings.preferredHeight = partsList.height = generate.height = tools.y = multibwauk.y = symmetrySettings.height = overlaySettings.height = editMetadata.height = back.height = 48;
        partsSearch.height = partSize;
        parts.y = tools.y+partSize;
        calcStep.x = recalc.width;
        back.width = parts.x+parts.width-back.height*2;
        undo.width = undo.height = redo.width = redo.height = back.height;
        undo.x = back.width;
        redo.x = undo.x+undo.width;
        undo.enabled = !multiblock.history.isEmpty();
        redo.enabled = !multiblock.future.isEmpty();
        toggle3D.y = recalc.y = calcStep.y = partsList.y = generate.y = gui.getHeight()-generate.height;
        tools.height = Math.max(6, editorTools.size())*partSize;
        parts.height = Math.max(tools.height-partSize, Math.min((gui.getHeight()-parts.y-progress.height-recalc.height)/2, ((parts.components.size()+partsWide-1)/partsWide)*partSize));
        tools.height = parts.height+partSize;
        resize.width = 320;
        multibwauk.width = gui.getWidth()-parts.x-parts.width-resize.width;
        symmetrySettings.width = overlaySettings.width = multibwauk.width/3;
        editMetadata.width = multibwauk.width-symmetrySettings.width*2;
        editMetadata.x = symmetrySettings.x+symmetrySettings.width;
        overlaySettings.x = editMetadata.x+editMetadata.width;
        generate.width = partsList.width = (multibwauk.width-generate.height)/2;
        generate.x = partsList.x+partsList.width;
        suggestorSettings.x = generate.x+generate.width;
        zoomIn.height = zoomOut.height = resize.height = back.height;
        zoomIn.width = zoomOut.width = resize.width/2;
        zoomIn.y = zoomOut.y = resize.height;
        resize.x = gui.getWidth()-resize.width;
        zoomIn.x = resize.x;
        zoomOut.x = zoomIn.x+zoomIn.width;
        toggle3D.x = suggestionList.x = dropdownSelectors.x = resize.x;
        dropdownSelectors.y = resize.height*2;
        toggle3D.width = suggestionList.width = dropdownSelectors.width = resize.width;
        if(suggestorSettings.isDown){
            suggestorSettings.width = gui.getWidth()-suggestorSettings.x;
            suggestorSettings.y = gui.getHeight()/2;
        }else{
            suggestorSettings.width = suggestorSettings.preferredHeight;
            suggestorSettings.y = generate.y;
        }
        for(Component c : tools.components){
            c.width = c.height = partSize;
        }
        suggestionList.y = dropdownSelectors.y+dropdownSelectors.height;
        suggestionList.height = gui.getHeight()-suggestionList.y-(generate.height+(toggle3D.isToggledOn?toggle3D.width:0));
        multibwauk.height = gui.getHeight()-multibwauk.y-generate.height;
        progress.width = textBox.width;
        progress.height = progress.getTaskHeight();//generate.height*2
        progress.y = generate.y-progress.height;
        textBox.y = parts.y+parts.height;
        graph.height = (((multiblock instanceof OverhaulTurbine)&&((OverhaulTurbine)multiblock).rotorValid)?generate.height*3:0);
        graph.y = progress.y-graph.height;
        textBox.height = gui.getHeight()-textBox.y-progress.height-generate.height-graph.height;
        super.render2d(deltaTime);
    }
    @Override    
    public void drawForeground(double deltaTime){
        Renderer renderer = new Renderer();
        List<NCPFElement>[] recipeLists = multiblock.getSpecificConfiguration().getMultiblockRecipes();
        for(int i = 0; i<recipeLists.length; i++){
            DropdownList list = dropdownSelectors.get(i);
            renderer.setColor(Core.theme.getSecondaryComponentColor(i));
            renderer.fillRect(dropdownSelectors.x+list.x, dropdownSelectors.y+list.y-list.preferredHeight, dropdownSelectors.x+list.x+list.width, dropdownSelectors.y+list.y);
            renderer.setColor(Core.theme.getComponentTextColor(i));
            renderer.drawCenteredText(dropdownSelectors.x+list.x, dropdownSelectors.y+list.y-list.preferredHeight, dropdownSelectors.x+list.x+list.width, dropdownSelectors.y+list.y, ((MultiblockRecipeElement)recipeLists[i].get(0)).getTitle());
        }
        renderer.setColor(Core.theme.getSecondaryComponentColor(0));
        renderer.fillRect(dropdownSelectors.x+blockRecipe.x, dropdownSelectors.y+blockRecipe.y-blockRecipe.preferredHeight, dropdownSelectors.x+blockRecipe.x+blockRecipe.width, dropdownSelectors.y+blockRecipe.y);
        renderer.setColor(Core.theme.getComponentTextColor(0));
        renderer.drawCenteredText(dropdownSelectors.x+blockRecipe.x, dropdownSelectors.y+blockRecipe.y-blockRecipe.preferredHeight, dropdownSelectors.x+blockRecipe.x+blockRecipe.width, dropdownSelectors.y+blockRecipe.y, "Block Recipe");
        renderer.setWhite();
        super.drawForeground(deltaTime);
    }
    @Override
    public AbstractBlock getSelectedBlock(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        if(parts.components.isEmpty())return ((Multiblock<AbstractBlock>)multiblock).getAvailableBlocks().get(0);
        if(parts.getSelectedIndex()==-1)return null;
        return ((MenuComponentEditorListBlock) parts.components.get(parts.getSelectedIndex())).block;
    }
    public void setSelectedBlock(AbstractBlock block){//and recipe too
        if(block==null)return;
        boolean hasPart = false;
        for(Component c : parts.components){
            MenuComponentEditorListBlock comp = (MenuComponentEditorListBlock)c;
            if(comp.block.isEqual(block)){
                hasPart = true;
                break;
            }
        }
        if(!hasPart){
            partsSearch.text = "";
            refreshPartsList();
        }
        for(int i = 0; i<parts.components.size(); i++){
            MenuComponentEditorListBlock comp = (MenuComponentEditorListBlock)parts.components.get(i);
            if(comp.block.isEqual(block))parts.setSelectedIndex(i);
        }
        boolean hasRecipe = false;
        for(int i = 0; i<blockRecipe.allComponents.size(); i++){
            MenuComponentElement comp = (MenuComponentElement)blockRecipe.allComponents.get(i);
            if(comp.element==block.getRecipe())hasRecipe = true;
        }
        if(!hasRecipe){
            for(int i = 0; i<dropdownSelectors.components.size(); i++)dropdownSelectors.get(i).searchBox.text = "";
            refreshBlockRecipes();
        }
        if(block.getRecipe()!=null){
            for(int i = 0; i<blockRecipe.allComponents.size(); i++){
                MenuComponentElement comp = (MenuComponentElement)blockRecipe.allComponents.get(i);
                if(comp.element.definition.matches(block.getRecipe().definition))blockRecipe.setSelectedIndex(i);
            }
        }
        lastSelectedBlock = getSelectedBlock(0);
    }
    @Override
    public EditorTool getSelectedTool(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        if(tools.getSelectedIndex()==-1)return null;
        EditorTool tool = ((MenuComponentEditorTool) tools.components.get(tools.getSelectedIndex())).tool;
        if(!(tool instanceof CutTool)){//selecting a non-copy tool, remove the cut tool!
            editorTools.remove(cut);
            tools.components.remove(cutComp);
        }
        if(!(tool instanceof CopyTool)){//selecting a non-copy tool, remove the copy tool!
            editorTools.remove(copy);
            tools.components.remove(copyComp);
        }
        if(!(tool instanceof PasteTool)){//selecting a non-paste tool, remove the paste tool!
            editorTools.remove(paste);
            tools.components.remove(pasteComp);
        }
        return tool;
    }
    @Override
    public NCPFElement getSelectedBlockRecipe(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        Component comp = blockRecipe.getSelectedComponent();
        if(comp==null)return null;
        return ((MenuComponentElement)comp).element;
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        super.onKeyEvent(key, scancode, action, mods);
        if(action==GLFW_PRESS){
//            if(key==GLFW.GLFW_KEY_G&&Core.isControlPressed()&&Core.isShiftPressed()&&Core.isAltPressed()){
//                multiblock.generateCrazyGraph();
//            }
            boolean aSearchBoxIsSelected = partsSearch.isFocused;
            for(Component c : components){
                if(c instanceof DropdownList){
                    if(((DropdownList)c).searchBox.isFocused){
                        aSearchBoxIsSelected = true;
                    }
                }
            }
            if(key==GLFW_KEY_ESCAPE){
                for(Component c : components){
                    if(c instanceof DropdownList){
                        if(((DropdownList)c).searchBox.isFocused){
                            ((DropdownList)c).searchBox.isFocused = false;
                            c.focusedComponent = null;
                        }
                    }
                }
                partsSearch.isFocused = false;
                focusedComponent = null;
                if(!aSearchBoxIsSelected){
                    if(getSelectedTool(0) instanceof PasteTool||getSelectedTool(0) instanceof CopyTool||getSelectedTool(0) instanceof CutTool){
                        tools.setSelectedIndex(1);
                    }else{
                        clearSelection(0);
                    }
                }
            }
            if(key==GLFW_KEY_DELETE){
                SetblocksAction ac = new SetblocksAction(null);
                synchronized(selection){
                    for(int[] i : selection){
                        ac.add(i[0], i[1], i[2]);
                    }
                }
                action(ac, true);
                clearSelection(0);
            }
            if(!aSearchBoxIsSelected){
                if(key==GLFW_KEY_M||key==GLFW_KEY_1)tools.setSelectedIndex(0);
                if(key==GLFW_KEY_S||key==GLFW_KEY_2)tools.setSelectedIndex(1);
                if(key==GLFW_KEY_P||key==GLFW_KEY_3)tools.setSelectedIndex(2);
                if(key==GLFW_KEY_L||key==GLFW_KEY_4)tools.setSelectedIndex(3);
                if(key==GLFW_KEY_B||key==GLFW_KEY_5)tools.setSelectedIndex(4);
            }
            if(Core.isControlPressed()){
                if(key==GLFW_KEY_A){
                    selectAll(0);
                }
                if(key==(Core.invertUndoRedo?GLFW_KEY_Y:GLFW_KEY_Z)){
                    multiblock.undo(autoRecalc);
                    if(autoRecalc&&Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
                        ((CuboidalMultiblock)multiblock).buildDefaultCasing();
                        multiblock.recalculate();
                    }
                }
                if(key==(Core.invertUndoRedo?GLFW_KEY_Z:GLFW_KEY_Y)){
                    multiblock.redo(autoRecalc);
                    if(autoRecalc&&Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
                        ((CuboidalMultiblock)multiblock).buildDefaultCasing();
                        multiblock.recalculate();
                    }
                }
                MenuComponentEditorGrid grid = null;
                for(Component c : multibwauk.components){
                    if(!c.isMouseFocused)continue;
                    if(c instanceof MenuComponentEditorGrid)grid = (MenuComponentEditorGrid)c;
                }
                int x = -1, y = -1, z = -1;
                if(grid!=null){
                    double mx = gui.mouseX-(multibwauk.x+grid.x-multibwauk.getHorizScroll());
                    double my = gui.mouseY-(multibwauk.y+grid.y-multibwauk.getVertScroll());
                    int sx = (int) (mx/grid.blockSize);
                    int sy = (int) (my/grid.blockSize);
                    x = sx*grid.xAxis.x+sy*grid.yAxis.x+grid.layer*grid.axis.x;
                    y = sx*grid.xAxis.y+sy*grid.yAxis.y+grid.layer*grid.axis.y;
                    z = sx*grid.xAxis.z+sy*grid.yAxis.z+grid.layer*grid.axis.z;
                    if(sx<grid.x1||sx>grid.x2||sy<grid.y1||sy>grid.y2)x = y = z = -1;
                }
                if(key==GLFW_KEY_C){
                    copySelection(0, x, y, z);
                }
                if(key==GLFW_KEY_X){
                    cutSelection(0, x, y, z);
                }
                if(key==GLFW_KEY_V){
                    if(!clipboard.isEmpty()&&!editorTools.contains(paste)){
                        editorTools.add(paste);
                        tools.add(pasteComp);
                        tools.setSelectedIndex(tools.components.size()-1);
                    }
                }
            }
        }
    }
    @Override
    public void setblocks(int id, SetblocksAction set){
        for(Iterator<BlockPos> it = set.locations.iterator(); it.hasNext();){
            BlockPos b = it.next();
            if(hasSelection(id)&&!isSelected(id, b.x, b.y, b.z))it.remove();
            else if(Core.isControlPressed()){
                if(set.block==null){
                    if(multiblock.getBlock(b.x, b.y, b.z)!=null&&!multiblock.getBlock(b.x, b.y, b.z).matches(getSelectedBlock(0)))it.remove();
                }else{
                    if(multiblock.getBlock(b.x, b.y, b.z)!=null&&!Core.isShiftPressed()){
                        it.remove();
                    }else if(multiblock.getBlock(b.x, b.y, b.z)!=null&&!multiblock.getBlock(b.x, b.y, b.z).canBeQuickReplaced()){
                        it.remove();
                    }else if(multiblock.getBlock(b.x, b.y, b.z)==null||multiblock.getBlock(b.x, b.y, b.z)!=null&&Core.isShiftPressed()){
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
    public void select(int id, int x1, int y1, int z1, int x2, int y2, int z2){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
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
    public void deselect(int id, int x1, int y1, int z1, int x2, int y2, int z2){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
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
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        if(Core.isControlPressed()){
            action(new SelectAction(this, id, is), true);
        }else{
            action(new SetSelectionAction(this, id, is), true);
        }
    }
    public void setSelection(int id, ArrayList<int[]> is){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        action(new SetSelectionAction(this, id, is), true);
    }
    public void deselect(int id, ArrayList<int[]> is){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        if(!Core.isControlPressed()){
            clearSelection(id);
            return;
        }
        action(new DeselectAction(this, id, is), true);
    }
    @Override
    public boolean isSelected(int id, int x, int y, int z){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        synchronized(selection){
            for(int[] s : selection){
                if(s==null)continue;//THIS SHOULD NEVER HAPPEN but it does anyway
                if(s[0]==x&&s[1]==y&&s[2]==z)return true;
            }
        }
        return false;
    }
    @Override
    public boolean hasSelection(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        synchronized(selection){
            return !selection.isEmpty();
        }
    }
    @Override
    public void selectCluster(int id, int x, int y, int z){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
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
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
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
    public void selectGroup(int id, int x, int y, int z){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
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
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
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
    public void clearSelection(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        action(new ClearSelectionAction(this, id), true);
    }
    @Override
    public void addSelection(int id, ArrayList<int[]> sel){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        synchronized(selection){
            for(int[] is : selection){
                for(Iterator<int[]> it = sel.iterator(); it.hasNext();){
                    int[] i = it.next();
                    if(i[0]==is[0]&&i[1]==is[1]&&i[2]==is[2]){
                        it.remove();
                    }
                }
            }
            selection.addAll(sel);
        }
    }
    @Override
    public void copySelection(int id, int x, int y, int z){//like copySelection, but clipboardier
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        synchronized(clipboard){
            clipboard.clear();
            synchronized(selection){
                if(selection.isEmpty()){
                    if(!editorTools.contains(copy)){
                        editorTools.add(copy);
                        tools.add(copyComp);
                        tools.setSelectedIndex(tools.components.size()-1);
                    }
                    return;
                }
                if(x==-1||y==-1||z==-1)return;
                for(int[] is : selection){
                    AbstractBlock b = multiblock.getBlock(is[0], is[1], is[2]);
                    clipboard.add(new ClipboardEntry(is[0]-x, is[1]-y, is[2]-z, b==null?null:b.copy(b.x-x, b.y-y, b.z-z)));
                }
            }
        }
        if(!editorTools.contains(paste)){
            editorTools.add(paste);
            tools.add(pasteComp);
            tools.setSelectedIndex(tools.components.size()-1);
        }
    }
    @Override
    public void cutSelection(int id, int x, int y, int z){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        synchronized(clipboard){
            clipboard.clear();
        }
        synchronized(selection){
            if(selection.isEmpty()){
                if(!editorTools.contains(cut)){
                    editorTools.add(cut);
                    tools.add(cutComp);
                    tools.setSelectedIndex(tools.components.size()-1);
                }
                return;
            }
        }
        copySelection(id, x,y,z);
        SetblocksAction ac = new SetblocksAction(null);
        synchronized(selection){
            for(int[] i : selection){
                ac.add(i[0], i[1], i[2]);
            }
        }
        action(ac, true);
        clearSelection(id);
    }
    public void recalculateSuggestions(){
        suggestions.clear();
        suggestionList.components.clear();
        Thread thread = new Thread(() -> {
            Task theTask = suggestionTask = new Task("Calculating suggestions");
            Task genTask = suggestionTask.addSubtask(new Task("Generating suggestions"));
            HashMap<Suggestor, SuggestorTask> genTasks = new HashMap<>();
            HashMap<Suggestor, Task> sortTasks = new HashMap<>();
            HashMap<Suggestor, Task> consolidateTasks = new HashMap<>();
            ArrayList<Suggestor> suggestors = new ArrayList<>(this.suggestors);//no modifying mid-suggestion
            for(Suggestor s : suggestors){
                if(s.isActive()){
                    genTasks.put(s, genTask.addSubtask(new SuggestorTask(s)));
                    consolidateTasks.put(s, genTask.addSubtask(new Task("Consolidating suggestions")));
                    sortTasks.put(s, genTask.addSubtask(new Task("Sorting suggestions")));
                }
            }
            ArrayList<Suggestion> allSuggestions = new ArrayList<>();
            for(Suggestor s : suggestors){
                ArrayList<Suggestion> suggestions = new ArrayList<>();
                if(suggestionTask!=theTask)return;//somethin' else is making suggestions!
                if(s.isActive()){
                    SuggestorTask task = genTasks.get(s);
                    Task consolidateTask = consolidateTasks.get(s);
                    Task sortTask = sortTasks.get(s);
                    s.generateSuggestions(multiblock, s.new SuggestionAcceptor(multiblock, task){
                        int passed = 0;
                        int total = 0;
                        @Override
                        protected void accepted(Suggestion suggestion){
                            suggestions.add(suggestion);
                            passed++;
                            total++;
                            if(passed>s.pruneAt){
                                task.name = "Pruning Suggestions";
                                Collections.sort(suggestions);
                                while(passed>s.pruneTo){
                                    suggestions.remove(suggestions.size()-1);
                                    passed--;
                                }
                            }
                            task.name = s.name+" ("+passed+"|"+(total)+")";
                        }
                        @Override
                        protected void denied(Suggestion suggestion){
                            total++;
                            task.name = s.name+" ("+passed+"|"+(total)+")";
                        }
                        @Override
                        public boolean acceptingSuggestions(){
                            return super.acceptingSuggestions()&&suggestionTask==theTask;
                        }
                    });
                    task.finish();
                    int total = suggestions.size();
                    for(int i = 0; i<suggestions.size(); i++){
                        if(suggestionTask!=theTask)return;//somethin' else is making suggestions!
                        Suggestion suggestion = suggestions.get(i);
                        for(Iterator<Suggestion> it = suggestions.iterator(); it.hasNext();){
                            Suggestion sugg = it.next();
                            if(sugg==suggestion)continue;//literally the same exact thing
                            if(sugg.equals(suggestion))it.remove();
                            consolidateTask.progress = 1-(suggestions.size()/(double)total);
                        }
                    }
                    consolidateTask.finish();
                    if(suggestionTask!=theTask)return;//somethin' else is making suggestions!
                    Collections.sort(suggestions);
                    if(suggestionTask!=theTask)return;//somethin' else is making suggestions!
                    sortTask.finish();
                    allSuggestions.addAll(suggestions);
                }
            }
            for(Suggestion s : allSuggestions){
                suggestionList.add(new MenuComponentSuggestion(this, s));
            }
            this.suggestions = allSuggestions;
            suggestionTask = null;
        }, "Suggestion calculation thread");
        thread.setDaemon(true);
        thread.start();
    }
    @Override
    public ArrayList<int[]> getSelection(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports 1 cursor!");
        return selection;
    }
    @Override
    public Multiblock getMultiblock(){
        return multiblock;
    }
    @Override
    public void setMultiblockRecipe(int recipeType, int idx){
        dropdownSelectors.get(recipeType).setSelectedIndex(idx);
    }
    @Override
    public ArrayList<ClipboardEntry> getClipboard(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports 1 cursor!");
        return clipboard;
    }
    @Override
    public boolean isControlPressed(int id){
        return Core.isControlPressed();
    }
    @Override
    public boolean isShiftPressed(int id){
        return Core.isShiftPressed();
    }
    @Override
    public boolean isAltPressed(int id){
        return Core.isAltPressed();
    }
    @Override
    public Color convertToolColor(Color color, int id){
        return color;//standard colors
    }
    @Override
    public ArrayList<Suggestion> getSuggestions(){
        return new ArrayList<>(suggestions);
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
        multiblock.action(action, autoRecalc, allowUndo);
        if(autoRecalc&&Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
            ((CuboidalMultiblock)multiblock).buildDefaultCasing();
            multiblock.recalculate();
        }
        refreshOverlays();
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(getSelectedBlock(0)!=lastSelectedBlock){
            refreshBlockRecipes();
            lastSelectedBlock = getSelectedBlock(0);
        }
        
        List<NCPFElement>[] recipeLists = multiblock.getSpecificConfiguration().getMultiblockRecipes();
        NCPFElement[] recipes = multiblock.getMultiblockRecipes();
        for(int i = 0; i<recipeLists.length; i++){
            DropdownList list = (DropdownList)dropdownSelectors.components.get(i);
            if(list.getSelectedIndex()>-1){
                NCPFElement element = recipeLists[i].get(list.getSelectedIndex());
                boolean match = false;
                for(NCPFElement recipe : recipes){
                    if(element.definition.matches(recipe.definition)){
                        match = true;
                        break;
                    }
                }
                if(!match){
                    action(new SetMultiblockRecipeAction(this, i, element), true);
                }
            }
        }
    }
    @Override
    public boolean onScroll(double dx, double dy){
        if(Core.isControlPressed()&&dy!=0){
            zoom(dy*zoomScrollMagnitude);
            return true;
        }
        return super.onScroll(dx, dy);
    }
    private void zoom(double zoom){
        for(int i = 0; i<Math.abs(zoom); i++){
            scale = Math.max(minScale, Math.min(maxScale, scale*Math.pow(scaleFac, zoom)));
        }
        CELL_SIZE = (int) (16*scale);
        LAYER_GAP = CELL_SIZE/2;
        multibwauk.scrollMagnitude = multibwauk.scrollWheelMagnitude = CELL_SIZE*scrollMagnitude;
        recalculateOnOpen = false;
        onOpened();
    }
    private void zoomOut(double amount){
        zoom(-amount);
    }
    private void zoomIn(double amount){
        zoom(amount);
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
    private synchronized void refreshBlockRecipes(){
        NCPFElement was = null;
        MenuComponentElement comp = (MenuComponentElement)blockRecipe.getSelectedComponent();
        if(comp!=null)was = comp.element;
        blockRecipe.clear();
        NCPFElement currentBlock = getSelectedBlock(0).getTemplate();
        if(currentBlock instanceof BlockRecipesElement){
            List<? extends NCPFElement> recipes = ((BlockRecipesElement)currentBlock).getBlockRecipes();
            if(recipes!=null){
                for(NCPFElement recipe : recipes){
                    blockRecipe.add(new MenuComponentElement(recipe));
                }
            }
        }
        if(!blockRecipe.list.components.isEmpty())blockRecipe.setSelectedIndex(blockRecipe.allComponents.indexOf(blockRecipe.list.components.get(0)));
        for(int i = 0; i<blockRecipe.allComponents.size(); i++)if(was==((MenuComponentElement)blockRecipe.allComponents.get(i)).element)blockRecipe.setSelectedIndex(i);
    }
    public synchronized void refreshPartsList(){
        List<AbstractBlock> availableBlocks = ((Multiblock<AbstractBlock>)multiblock).getAvailableBlocks();
        ArrayList<AbstractBlock> searchedAvailable = Pinnable.searchAndSort(availableBlocks, partsSearch.text);
        AbstractBlock selectedBlock = getSelectedBlock(0);
        int i = 0;
        int idx = 0;
        parts.components.clear();
        for(AbstractBlock availableBlock : searchedAvailable){
            parts.add(new MenuComponentEditorListBlock(this, availableBlock));
            if(selectedBlock.isEqual(availableBlock))idx = i;
            i++;
        }
        parts.setSelectedIndex(idx);
    }
    public void draw3D(){
        Renderer renderer = new Renderer();
        BoundingBox bbox = multiblock.getBoundingBox();
        float resonatingAlpha = 0.25f;
        float blockSize = 1;
        renderer.setColor(Core.theme.get3DMultiblockOutlineColor());
        renderer.drawCubeOutline(-blockSize/32,-blockSize/32,-blockSize/32,bbox.getWidth()+blockSize/32,bbox.getHeight()+blockSize/32,bbox.getDepth()+blockSize/32,blockSize/24);
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
                    return block.shouldRenderFace(b);
                });
            }
            if(isSelected(0, x, y, z)){
                renderer.setColor(Core.theme.getSelectionColor());
                renderer.drawCubeOutline(X-border, Y-border, Z-border, X+blockSize+border, Y+blockSize+border, Z+blockSize+border, border, (t) -> {
                    boolean d1 = isSelected(0, xx+t[0].x, yy+t[0].y, zz+t[0].z);
                    boolean d2 = isSelected(0, xx+t[1].x, yy+t[1].y, zz+t[1].z);
                    boolean d3 = isSelected(0, xx+t[0].x+t[1].x, yy+t[0].y+t[1].y, zz+t[0].z+t[1].z);
                    if(d1&&d2&&!d3)return true;//both sides, but not the corner
                    if(!d1&&!d2)return true;//neither side
                    return false;
                });
            }
            {
                ArrayList<Function<Direction[], Boolean>> edgeFuncs = new ArrayList<>();
                boolean selected = false;
                for(Suggestion s : getSuggestions()){
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
        for(Component comp : multibwauk.components){
            if(comp instanceof MenuComponentEditorGrid){
                MenuComponentEditorGrid grid = (MenuComponentEditorGrid)comp;
                if(grid.mouseover==null)continue;
                int[] coords = grid.toBlockCoords(grid.mouseover[0], grid.mouseover[1]);
                renderer.setColor(Core.theme.get3DDeviceoverOutlineColor());
                getSymmetry().apply(coords[0], coords[1], coords[2], bbox, (bx, by, bz) -> {
                    float X = bx*blockSize;
                    float Y = by*blockSize;
                    float Z = bz*blockSize;
                    float border = blockSize/16;
                    renderer.drawCubeOutline(X-border/2, Y-border/2, Z-border/2, X+blockSize+border/2, Y+blockSize+border/2, Z+blockSize+border/2, border);
                });
                float X = coords[0]*blockSize;
                float Y = coords[1]*blockSize;
                float Z = coords[2]*blockSize;
                float border = blockSize/16;
                renderer.setColor(Core.theme.getEditorMouseoverLineColor());
                X+=blockSize/2;
                Y+=blockSize/2;
                Z+=blockSize/2;
                renderer.drawCube(0, Y-border/2, Z-border/2, X-blockSize/2, Y+border/2, Z+border/2, null);//NX
                renderer.drawCube(X-border/2, 0, Z-border/2, X+border/2, Y-blockSize/2, Z+border/2, null);//NY
                renderer.drawCube(X-border/2, Y-border/2, 0, X+border/2, Y+border/2, Z-blockSize/2, null);//NZ
                renderer.drawCube(X+blockSize/2, Y-border/2, Z-border/2, bbox.getWidth()*blockSize, Y+border/2, Z+border/2, null);//PX
                renderer.drawCube(X-border/2, Y+blockSize/2, Z-border/2, X+border/2, bbox.getHeight()*blockSize, Z+border/2, null);//PY
                renderer.drawCube(X-border/2, Y-border/2, Z+blockSize/2, X+border/2, Y+border/2, bbox.getDepth()*blockSize, null);//PZ
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
            if(multiblock instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multiblock).getLocationCategory(x, y, z)==OverhaulFusionReactor.LocationCategory.PLASMA){
                renderer.setWhite();
                renderer.drawCube(X, Y, Z, X+blockSize, Y+blockSize, Z+blockSize, TextureManager.getImage("overhaul/fusion/plasma"), (t) -> {
                    if(!multiblock.contains(xx+t.x, yy+t.y, zz+t.z))return true;
                    AbstractBlock b = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                    if(((OverhaulFusionReactor)multiblock).getLocationCategory(xx+t.x, yy+t.y, zz+t.z)!=OverhaulFusionReactor.LocationCategory.PLASMA)return true;
                    return b==null||Core.hasAlpha(b.getBaseTexture());
                });
            }
            if(isControlPressed(0)&&getSelectedTool(0).isEditTool()){
                if(block==null||(isShiftPressed(0)&&block.canBeQuickReplaced())){
                    for(EditorSpace space : ((Multiblock<AbstractBlock>)multiblock).getEditorSpaces()){
                        if(space.isSpaceValid(getSelectedBlock(0), x, y, z)&&multiblock.isValid(getSelectedBlock(0), x, y, z)){
                            getSelectedBlock(0).render(renderer, X, Y, Z, blockSize, blockSize, blockSize, null, resonatingAlpha, multiblock, (t) -> {
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
            if(isSelected(0, x, y, z)){
                renderer.setColor(convertToolColor(Core.theme.getSelectionColor(), 0), .5f);
                renderer.drawCube(X-border/4, Y-border/4, Z-border/4, X+blockSize+border/4, Y+blockSize+border/4, Z+blockSize+border/4, null, (t) -> {
                    if(!multiblock.contains(xx+t.x, yy+t.y, zz+t.z))return true;
                    AbstractBlock o = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                    return !isSelected(0, xx+t.x, yy+t.y, zz+t.z)&&o==null;
                });
            }
        });
        for(EditorSpace space : ((Multiblock<AbstractBlock>)multiblock).getEditorSpaces()){
            getSelectedTool(0).drawVRGhosts(renderer, space, 0, 0, 0, 1, 1, 1, blockSize, getSelectedBlock(0)==null?null:getSelectedBlock(0).getTexture());
        }
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
    private void refreshOverlays(){
        for(EditorOverlay overlay : overlays){
            if(overlay.isActive())overlay.refresh(multiblock);
        }
    }
}
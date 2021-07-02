package planner.menu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import multiblock.Action;
import multiblock.Block;
import multiblock.BoundingBox;
import multiblock.CuboidalMultiblock;
import multiblock.Decal;
import multiblock.EditorSpace;
import multiblock.Multiblock;
import multiblock.action.ClearSelectionAction;
import multiblock.action.DeselectAction;
import multiblock.action.SelectAction;
import multiblock.action.SetCoolantRecipeAction;
import multiblock.action.SetFuelAction;
import multiblock.action.SetFusionCoolantRecipeAction;
import multiblock.action.SetFusionRecipeAction;
import multiblock.action.SetSelectionAction;
import multiblock.action.SetTurbineRecipeAction;
import multiblock.action.SetblocksAction;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.overhaul.turbine.OverhaulTurbine;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.Task;
import planner.editor.ClipboardEntry;
import planner.editor.Editor;
import planner.editor.suggestion.Suggestion;
import planner.editor.suggestion.Suggestor;
import planner.editor.suggestion.Suggestor.SuggestionAcceptor;
import planner.editor.suggestion.SuggestorTask;
import planner.editor.tool.CopyTool;
import planner.editor.tool.CutTool;
import planner.editor.tool.EditorTool;
import planner.editor.tool.LineTool;
import planner.editor.tool.MoveTool;
import planner.editor.tool.PasteTool;
import planner.editor.tool.PencilTool;
import planner.editor.tool.RectangleTool;
import planner.editor.tool.SelectionTool;
import planner.menu.component.MenuComponentDropdownList;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentMinimalistTextView;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.MenuComponentToggleBox;
import planner.menu.component.Searchable;
import planner.menu.component.editor.MenuComponentCoolantRecipe;
import planner.menu.component.editor.MenuComponentEditorGrid;
import planner.menu.component.editor.MenuComponentEditorListBlock;
import planner.menu.component.editor.MenuComponentEditorTool;
import planner.menu.component.editor.MenuComponentFusionCoolantRecipe;
import planner.menu.component.editor.MenuComponentMultiblockProgressBar;
import planner.menu.component.editor.MenuComponentOverhaulFusionBlockRecipe;
import planner.menu.component.editor.MenuComponentOverhaulFusionRecipe;
import planner.menu.component.editor.MenuComponentOverhaulMSRBlockRecipe;
import planner.menu.component.editor.MenuComponentOverhaulSFRBlockRecipe;
import planner.menu.component.editor.MenuComponentSuggestion;
import planner.menu.component.editor.MenuComponentSuggestor;
import planner.menu.component.editor.MenuComponentTurbineRecipe;
import planner.menu.component.editor.MenuComponentUnderFuel;
import planner.module.Module;
import planner.vr.VRCore;
import simplelibrary.game.Framebuffer;
import simplelibrary.image.Color;
import simplelibrary.opengl.ImageStash;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuEdit extends Menu implements Editor{
    private final ArrayList<EditorTool> editorTools = new ArrayList<>();
    public Framebuffer turbineGraph;
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
    private Block lastSelectedBlock;
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
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true).setTooltip("Stop editing this multiblock and return to the main menu"));
    private final MenuComponentMinimalistButton undo = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Undo", false, true){
        @Override
        public void drawText(){
            double tallness = height*3/2;
            Core.drawOval(x+width/2, y+height/2+tallness-height/16, width, tallness, height/8, 160, 0, 151, 10);
            Core.drawRegularPolygon(x+width/4, y+height*.5625, width/4, 3, -5, 0);
        }
    }.setTooltip("Undo (Ctrl+"+(Core.invertUndoRedo?"Y":"Z")+")"));
    private final MenuComponentMinimalistButton redo = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Redo", false, true){
        @Override
        public void drawText(){
            double tallness = height*3/2;
            Core.drawOval(x+width/2, y+height/2+tallness-height/16, width, tallness, height/8, 160, 0, 150, 9);
            Core.drawRegularPolygon(x+width*3/4, y+height*.5625, width/4, 3, 5, 0);
        }
    }.setTooltip("Redo (Ctrl+"+(Core.invertUndoRedo?"Z":"Y")+")"));
    public final MenuComponentMulticolumnMinimaList parts = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, partSize, partSize, partSize/2));
    public final MenuComponentMinimalistTextBox partsSearch = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, Core.autoBuildCasing?"-port":"", true, "Search"){
        @Override
        public void onCharTyped(char c){
            super.onCharTyped(c);
            refreshPartsList();
        }
        @Override
        public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
            super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
            refreshPartsList();
        }
        @Override
        public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
            super.onMouseButton(x, y, button, pressed, mods);
            if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&pressed){
                text = "";
                refreshPartsList();
                MenuEdit.this.selected = this;
                isSelected = true;
            }
        }
    });
    public final MenuComponentMinimalistScrollable multibwauk = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 32, 32));
    private final MenuComponentMinimalistButton zoomOut = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Zoom out", true, true));
    private final MenuComponentMinimalistButton zoomIn = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Zoom in", true, true));
    private final MenuComponentMinimalistButton resize = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Resize", true, true).setTooltip("Resize the multiblock\nWARNING: This clears the edit history! (undo/redo)"));
    public final MenuComponentDropdownList underFuelOrCoolantRecipe = new MenuComponentDropdownList(0, 0, 0, 32, true);
    public final MenuComponentDropdownList fusionRecipe = new MenuComponentDropdownList(0, 0, 0, 32, true);
    public final MenuComponentDropdownList blockRecipe = new MenuComponentDropdownList(0, 0, 0, 32, true);
    private final MenuComponentMinimalistTextView textBox = add(new MenuComponentMinimalistTextView(0, 0, 0, 0, 24, 24));
    private final MenuComponentMinimalistButton editMetadata = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true).setTooltip("Modify the multiblock metadata"));
    public final MenuComponentMinimaList tools = add(new MenuComponentMinimaList(0, 0, 0, 0, partSize/2));
    private final MenuComponentMinimalistButton generate = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Generate", true, true, true).setTooltip("Generate or improve this multiblock"));
    private final MenuComponentMinimalistButton recalc = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Recalculate", true, true).setTooltip("Recalculate the entire multiblock\nCtrl-click to queue multiple actions without recalculating"));
    private final MenuComponentMinimalistButton calcStep = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Step", true, true).setTooltip("Perform one step of calculation"));
    public final MenuComponentToggleBox toggle3D = add(new MenuComponentToggleBox(0, 0, 0, 0, "3D View", false).setTooltip("Toggle 3D multiblock view\n(Rotate with arrow keys)"));
    private final MenuComponentMultiblockProgressBar progress = add(new MenuComponentMultiblockProgressBar(this, 0, 0, 0, 0));;
    private final MenuComponentMinimaList suggestionList = add(new MenuComponentMinimaList(0, 0, 0, 0, partSize/2));
    private final MenuComponentDropdownList suggestorSettings = add(new MenuComponentDropdownList(0, 0, 0, 0){
        @Override
        public void render(){
            if(!isDown){
                Color col = isMouseOver?Core.theme.getComponentMouseoverColor(0):Core.theme.getComponentColor(0);
                Core.applyColor(col);
                drawRect(x, y, x+width, y+height, 0);
                Core.applyColor(Core.theme.getComponentTextColor(0));
                double border = height/6;
                double lineThickness = height/9;
                drawRect(x+border, y+border, x+width-border, y+border+lineThickness, 0);
                drawRect(x+border, y+height/2-lineThickness/2, x+width-border, y+height/2+lineThickness/2, 0);
                drawRect(x+border, y+height-border-lineThickness, x+width-border, y+height-border, 0);
            }
        }
    });
    public final ArrayList<int[]> selection = new ArrayList<>();
    private ArrayList<Suggestion> suggestions = new ArrayList<>();
    private ArrayList<Suggestor> suggestors = new ArrayList<>();
    private double scale = 4;
    private double minScale = 0.5;
    private double maxScale = 16;
    public int CELL_SIZE = (int) (16*scale);
    private int LAYER_GAP = CELL_SIZE/2;
    private long lastChange = 0;
    public MenuEdit(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
        if(Core.recoveryMode){
            autoRecalc = false;
        }
        this.multiblock = multiblock;
        multibwauk.setScrollMagnitude(CELL_SIZE*scrollMagnitude);
        back.addActionListener((e) -> {
            suggestionTask = null;
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SplitTransitionX.slideOut((parts.x+parts.width)/gui.helper.displayWidth()), 5));
        });
        undo.addActionListener((e) -> {
            multiblock.undo(autoRecalc);
            if(Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
                ((CuboidalMultiblock)multiblock).buildDefaultCasing();
                if(autoRecalc)multiblock.recalculate();
            }
        });
        redo.addActionListener((e) -> {
            multiblock.redo(autoRecalc);
            if(Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
                ((CuboidalMultiblock)multiblock).buildDefaultCasing();
                if(autoRecalc)multiblock.recalculate();
            }
        });
        resize.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, multiblock.getResizeMenu(gui, this), MenuTransition.SlideTransition.slideFrom(1, 0), 5));
        });
        zoomOut.addActionListener((e) -> {
            zoomOut(1);
        });
        zoomIn.addActionListener((e) -> {
            zoomIn(1);
        });
        editMetadata.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, new MenuMultiblockMetadata(gui, this, multiblock), MenuTransition.SlideTransition.slideTo(0, 1), 4));
        });
        generate.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, new MenuGenerator(gui, this, multiblock), MenuTransition.SlideTransition.slideFrom(0, 1), 5));
        });
        refreshPartsList();
        if(multiblock instanceof UnderhaulSFR){
            add(underFuelOrCoolantRecipe);
            for(multiblock.configuration.underhaul.fissionsfr.Fuel fuel : Core.configuration.underhaul.fissionSFR.allFuels){
                underFuelOrCoolantRecipe.add(new MenuComponentUnderFuel(fuel));
            }
        }
        if(multiblock instanceof OverhaulSFR){
            add(underFuelOrCoolantRecipe);
            for(multiblock.configuration.overhaul.fissionsfr.CoolantRecipe recipe : Core.configuration.overhaul.fissionSFR.allCoolantRecipes){
                underFuelOrCoolantRecipe.add(new MenuComponentCoolantRecipe(recipe));
            }
            add(blockRecipe);
        }
        if(multiblock instanceof OverhaulTurbine){
            add(underFuelOrCoolantRecipe);
            for(multiblock.configuration.overhaul.turbine.Recipe recipe : Core.configuration.overhaul.turbine.allRecipes){
                underFuelOrCoolantRecipe.add(new MenuComponentTurbineRecipe(recipe));
            }
        }
        if(multiblock instanceof OverhaulMSR){
            add(blockRecipe);
        }
        if(multiblock instanceof OverhaulFusionReactor){
            add(underFuelOrCoolantRecipe);
            for(multiblock.configuration.overhaul.fusion.CoolantRecipe recipe : Core.configuration.overhaul.fusion.allCoolantRecipes){
                underFuelOrCoolantRecipe.add(new MenuComponentFusionCoolantRecipe(recipe));
            }
            add(fusionRecipe);
            for(multiblock.configuration.overhaul.fusion.Recipe recipe : Core.configuration.overhaul.fusion.allRecipes){
                fusionRecipe.add(new MenuComponentOverhaulFusionRecipe(recipe));
            }
            fusionRecipe.setSelectedIndex(0);
            add(blockRecipe);
        }
        refreshBlockRecipes();
        for(EditorTool tool : editorTools){
            tools.add(new MenuComponentEditorTool(tool));
        }
        tools.setSelectedIndex(2);
        multiblock.getSuggestors(suggestors);
        for(Module m : Core.modules){
            if(m.isActive()){
                m.getSuggestors(multiblock, suggestors);
            }
        }
        for(Suggestor suggestor : suggestors){
            suggestorSettings.add(new MenuComponentSuggestor(this, suggestor));
        }
    }
    private boolean recalculateOnOpen = true;
    @Override
    public synchronized void onGUIOpened(){
        Core.delCircle = true;
        Core.circleSize = CELL_SIZE;
        editMetadata.label = multiblock.getName().isEmpty()?"Edit Metadata":(multiblock.getName()+" | Edit Metadata");
        if(multiblock instanceof UnderhaulSFR){
            underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.underhaul.fissionSFR.allFuels.indexOf(((UnderhaulSFR)multiblock).fuel));
        }
        if(multiblock instanceof OverhaulSFR){
            underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.overhaul.fissionSFR.allCoolantRecipes.indexOf(((OverhaulSFR)multiblock).coolantRecipe));
        }
        if(multiblock instanceof OverhaulTurbine){
            underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.overhaul.turbine.allRecipes.indexOf(((OverhaulTurbine)multiblock).recipe));
        }
        if(multiblock instanceof OverhaulFusionReactor){
            underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.overhaul.fusion.allCoolantRecipes.indexOf(((OverhaulFusionReactor)multiblock).coolantRecipe));
        }
        multibwauk.components.clear();
        ArrayList<EditorSpace> editorSpaces = multiblock.getEditorSpaces();
        double lastX = 0;
        double lastY = 0;
        double nextY = 0;
        for(EditorSpace space : editorSpaces){
            ArrayList<MenuComponent> comps = new ArrayList<>();
            space.createComponents(this, comps, CELL_SIZE);
            for(int i = 0; i<comps.size(); i++){
                MenuComponent comp = comps.get(i);
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
    public void onGUIClosed(){
        if(multiblock.calculationPaused)multiblock.recalculate();
        super.onGUIClosed();
    }
    @Override
    public synchronized void render(int millisSinceLastTick){
        recalc.label = isControlPressed(0)&&autoRecalc?"Queue Actions":"Recalculate";
        textBox.setText(multiblock.getFullTooltip());
        double lastX = 0;
        double lastY = 0;
        double nextY = 0;
        for(int i = 0; i<multibwauk.components.size(); i++){
            MenuComponent comp = multibwauk.components.get(i);
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
        generate.x = editMetadata.x = textBox.width = multibwauk.x = parts.x+parts.width;
        recalc.width = calcStep.width = textBox.width/2;
        toggle3D.height = partsSearch.y = recalc.height = calcStep.height = suggestorSettings.preferredHeight = generate.height = tools.y = multibwauk.y = editMetadata.height = back.height = 48;
        partsSearch.height = partSize;
        parts.y = tools.y+partSize;
        calcStep.x = recalc.width;
        back.width = parts.x+parts.width-back.height*2;
        undo.width = undo.height = redo.width = redo.height = back.height;
        undo.x = back.width;
        redo.x = undo.x+undo.width;
        undo.enabled = !multiblock.history.isEmpty();
        redo.enabled = !multiblock.future.isEmpty();
        toggle3D.y = recalc.y = calcStep.y = generate.y = gui.helper.displayHeight()-generate.height;
        tools.height = Math.max(6, editorTools.size())*partSize;
        parts.height = Math.max(tools.height-partSize, Math.min((gui.helper.displayHeight()-parts.y-progress.height-recalc.height)/2, ((parts.components.size()+partsWide-1)/partsWide)*partSize));
        tools.height = parts.height+partSize;
        resize.width = 320;
        editMetadata.width = multibwauk.width = gui.helper.displayWidth()-parts.x-parts.width-resize.width;
        generate.width = multibwauk.width-generate.height;
        suggestorSettings.x = generate.x+generate.width;
        zoomIn.height = zoomOut.height = resize.height = back.height;
        zoomIn.width = zoomOut.width = resize.width/2;
        zoomIn.y = zoomOut.y = resize.height;
        resize.x = gui.helper.displayWidth()-resize.width;
        zoomIn.x = resize.x;
        zoomOut.x = zoomIn.x+zoomIn.width;
        toggle3D.x = suggestionList.x = blockRecipe.x = fusionRecipe.x = underFuelOrCoolantRecipe.x = resize.x;
        underFuelOrCoolantRecipe.y = resize.height*2+underFuelOrCoolantRecipe.preferredHeight;
        toggle3D.width = suggestionList.width = blockRecipe.width = fusionRecipe.width = underFuelOrCoolantRecipe.width = resize.width;
        if(suggestorSettings.isDown){
            suggestorSettings.width = gui.helper.displayWidth()-suggestorSettings.x;
            suggestorSettings.y = gui.helper.displayHeight()/2;
        }else{
            suggestorSettings.width = suggestorSettings.preferredHeight;
            suggestorSettings.y = generate.y;
        }
        for(simplelibrary.opengl.gui.components.MenuComponent c : tools.components){
            c.width = c.height = partSize;
        }
        if(multiblock instanceof OverhaulSFR){
            blockRecipe.y = underFuelOrCoolantRecipe.y+underFuelOrCoolantRecipe.height+blockRecipe.preferredHeight;
        }
        if(multiblock instanceof OverhaulFusionReactor){
            fusionRecipe.y = underFuelOrCoolantRecipe.y+underFuelOrCoolantRecipe.height+fusionRecipe.preferredHeight;
            blockRecipe.y = fusionRecipe.y+fusionRecipe.height+blockRecipe.preferredHeight;
        }
        if(multiblock instanceof OverhaulMSR){
            underFuelOrCoolantRecipe.x = -5000;
            blockRecipe.y = resize.height*2+blockRecipe.preferredHeight;
        }
        suggestionList.y = Math.max(underFuelOrCoolantRecipe.y+underFuelOrCoolantRecipe.height, Math.max(blockRecipe.y+blockRecipe.height, fusionRecipe.y+fusionRecipe.height));
        suggestionList.height = gui.helper.displayHeight()-suggestionList.y-(generate.height+(toggle3D.isToggledOn?toggle3D.width:0));
        multibwauk.height = gui.helper.displayHeight()-multibwauk.y-generate.height;
        progress.y = generate.y-generate.height*2;
        progress.width = textBox.width;
        progress.height = generate.height*2;
        textBox.y = parts.y+parts.height;
        textBox.height = gui.helper.displayHeight()-textBox.y-progress.height-generate.height;
        if(multiblock instanceof OverhaulTurbine){
            OverhaulTurbine turbine = (OverhaulTurbine)multiblock;
            double width = turbine.getInternalDepth()*CELL_SIZE;
            double height = CELL_SIZE*4;
            if(turbine.rotorValid){
                double max = 0;
                double min = Double.MAX_VALUE;
                for(double d : turbine.idealExpansion){
                    max = Math.max(max,d);
                    min = Math.min(min,d);
                }
                for(double d : turbine.actualExpansion){
                    max = Math.max(max,d);
                    min = Math.min(min,d);
                }
                ImageStash.instance.bindTexture(0);
                if(turbineGraph==null||(int)width!=turbineGraph.width||(int)height!=turbineGraph.height)turbineGraph = new Framebuffer(gui.helper, null, (int)width, (int)height);
                turbineGraph.bindRenderTarget2D();
                GL11.glColor4d(0, 0, 0, 1);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2d(0, 0);
                GL11.glVertex2d(turbineGraph.width, 0);
                GL11.glVertex2d(turbineGraph.width, turbineGraph.height);
                GL11.glVertex2d(0, turbineGraph.height);
                float tint = .75f;
                for(float eff = 0; eff<=1; eff+=.1){
                    GL11.glColor4d(tint*Math.max(0,Math.min(1,-Math.abs(3*eff-1.5)+1.5)), tint*Math.max(0,Math.min(1,3*eff-1)), 0, 1);
                    for(int i = 1; i<turbine.idealExpansion.length; i++){
                        double prevLowerBound = Math.min(turbine.idealExpansion[i-1]*eff,turbine.idealExpansion[i-1]/eff);
                        double prevUpperBound = Math.max(turbine.idealExpansion[i-1]*eff,turbine.idealExpansion[i-1]/eff);
                        double lowerBound = Math.min(turbine.idealExpansion[i]*eff,turbine.idealExpansion[i]/eff);
                        double upperBound = Math.max(turbine.idealExpansion[i]*eff,turbine.idealExpansion[i]/eff);
                        if(!Double.isFinite(prevLowerBound))prevLowerBound = min;
                        if(!Double.isFinite(lowerBound))lowerBound = min;
                        if(!Double.isFinite(prevUpperBound))prevUpperBound = max;
                        if(!Double.isFinite(upperBound))upperBound = max;
                        GL11.glVertex2d((i-1)*width/(turbine.idealExpansion.length-1), turbineGraph.height-turbineGraph.height*((prevUpperBound-min)/(max-min)));//upper
                        GL11.glVertex2d((i)*width/(turbine.idealExpansion.length-1), turbineGraph.height-turbineGraph.height*((upperBound-min)/(max-min)));

                        GL11.glVertex2d((i)*width/(turbine.idealExpansion.length-1), turbineGraph.height-turbineGraph.height*((lowerBound-min)/(max-min)));//lower
                        GL11.glVertex2d((i-1)*width/(turbine.idealExpansion.length-1), turbineGraph.height-turbineGraph.height*((prevLowerBound-min)/(max-min)));
                    }
                }
                GL11.glColor4f(0, 0, 1, 1);
                int thickness = 3;
                for(int i = 1; i<turbine.idealExpansion.length; i++){
                    GL11.glVertex2d((i-1)*width/(turbine.idealExpansion.length-1), turbineGraph.height-turbineGraph.height*((turbine.idealExpansion[i-1]-min)/(max-min)));
                    GL11.glVertex2d((i)*width/(turbine.idealExpansion.length-1), turbineGraph.height-turbineGraph.height*((turbine.idealExpansion[i]-min)/(max-min)));

                    GL11.glVertex2d((i)*width/(turbine.idealExpansion.length-1), turbineGraph.height+thickness-turbineGraph.height*((turbine.idealExpansion[i]-min)/(max-min)));
                    GL11.glVertex2d((i-1)*width/(turbine.idealExpansion.length-1), turbineGraph.height+thickness-turbineGraph.height*((turbine.idealExpansion[i-1]-min)/(max-min)));
                }
                GL11.glColor4f(1, 1, 1, 1);
                for(int i = 1; i<turbine.actualExpansion.length; i++){
                    GL11.glVertex2d((i-1)*width/(turbine.idealExpansion.length-1), turbineGraph.height-turbineGraph.height*((turbine.actualExpansion[i-1]-min)/(max-min)));
                    GL11.glVertex2d((i)*width/(turbine.idealExpansion.length-1), turbineGraph.height-turbineGraph.height*((turbine.actualExpansion[i]-min)/(max-min)));

                    GL11.glVertex2d((i)*width/(turbine.idealExpansion.length-1), turbineGraph.height+thickness-turbineGraph.height*((turbine.actualExpansion[i]-min)/(max-min)));
                    GL11.glVertex2d((i-1)*width/(turbine.idealExpansion.length-1), turbineGraph.height+thickness-turbineGraph.height*((turbine.actualExpansion[i-1]-min)/(max-min)));
                }
                GL11.glEnd();
                turbineGraph.releaseRenderTarget();
                Core.applyWhite();
            }
        }
        super.render(millisSinceLastTick);
    }
    @Override    
    public void renderForeground(){
        if(multiblock instanceof UnderhaulSFR){//so this is below the tooltip
            Core.applyColor(Core.theme.getSecondaryComponentColor(0));
            drawRect(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, 0);
            Core.applyColor(Core.theme.getComponentTextColor(0));
            drawCenteredText(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, "Fuel");
        }
        if(multiblock instanceof OverhaulSFR){
            Core.applyColor(Core.theme.getSecondaryComponentColor(0));
            drawRect(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, 0);
            drawRect(blockRecipe.x, blockRecipe.y-blockRecipe.preferredHeight, blockRecipe.x+blockRecipe.width, blockRecipe.y, 0);
            Core.applyColor(Core.theme.getComponentTextColor(0));
            drawCenteredText(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, "Coolant Recipe");
            drawCenteredText(blockRecipe.x, blockRecipe.y-blockRecipe.preferredHeight, blockRecipe.x+blockRecipe.width, blockRecipe.y, "Block Recipe");
        }
        if(multiblock instanceof OverhaulMSR){
            Core.applyColor(Core.theme.getSecondaryComponentColor(0));
            drawRect(blockRecipe.x, blockRecipe.y-blockRecipe.preferredHeight, blockRecipe.x+blockRecipe.width, blockRecipe.y, 0);
            Core.applyColor(Core.theme.getComponentTextColor(0));
            drawCenteredText(blockRecipe.x, blockRecipe.y-blockRecipe.preferredHeight, blockRecipe.x+blockRecipe.width, blockRecipe.y, "Block Recipe");
        }
        if(multiblock instanceof OverhaulTurbine){
            Core.applyColor(Core.theme.getSecondaryComponentColor(0));
            drawRect(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, 0);
            Core.applyColor(Core.theme.getComponentTextColor(0));
            drawCenteredText(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, "Recipe");
        }
        if(multiblock instanceof OverhaulFusionReactor){
            Core.applyColor(Core.theme.getSecondaryComponentColor(0));
            drawRect(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, 0);
            drawRect(fusionRecipe.x, fusionRecipe.y-fusionRecipe.preferredHeight, fusionRecipe.x+fusionRecipe.width, fusionRecipe.y, 0);
            drawRect(blockRecipe.x, blockRecipe.y-blockRecipe.preferredHeight, blockRecipe.x+blockRecipe.width, blockRecipe.y, 0);
            Core.applyColor(Core.theme.getComponentTextColor(0));
            drawCenteredText(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, "Coolant Recipe");
            drawCenteredText(fusionRecipe.x, fusionRecipe.y-fusionRecipe.preferredHeight, fusionRecipe.x+fusionRecipe.width, fusionRecipe.y, "Recipe");
            drawCenteredText(blockRecipe.x, blockRecipe.y-blockRecipe.preferredHeight, blockRecipe.x+blockRecipe.width, blockRecipe.y, "Block Recipe");
        }
        Core.applyWhite();
        super.renderForeground();
    }
    @Override
    public void tick(){
        super.tick();
        if(lastChange!=multiblock.lastChangeTime){
            lastChange = multiblock.lastChangeTime;
            recalculateSuggestions();
        }
    }
    @Override
    public Block getSelectedBlock(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        if(parts.components.isEmpty())return ((Multiblock<Block>)multiblock).getAvailableBlocks().get(0);
        if(parts.getSelectedIndex()==-1)return null;
        return ((MenuComponentEditorListBlock) parts.components.get(parts.getSelectedIndex())).block;
    }
    public void setSelectedBlock(Block block){//and recipe too
        if(block==null)return;
        partsSearch.text = "";
        blockRecipe.searchBox.text = "";
        underFuelOrCoolantRecipe.searchBox.text = "";
        fusionRecipe.searchBox.text = "";
        refreshPartsList();
        for(int i = 0; i<parts.components.size(); i++){
            MenuComponentEditorListBlock comp = (MenuComponentEditorListBlock)parts.components.get(i);
            if(comp.block.isEqual(block))parts.setSelectedIndex(i);
        }
        refreshBlockRecipes();
        for(int i = 0; i<blockRecipe.allComponents.size(); i++){
            MenuComponent comp = blockRecipe.allComponents.get(i);
            if(comp instanceof MenuComponentOverhaulSFRBlockRecipe){
                MenuComponentOverhaulSFRBlockRecipe bcomp = (MenuComponentOverhaulSFRBlockRecipe)comp;
                if(bcomp.recipe==((multiblock.overhaul.fissionsfr.Block)block).recipe)blockRecipe.setSelectedIndex(i);
            }
            if(comp instanceof MenuComponentOverhaulMSRBlockRecipe){
                MenuComponentOverhaulMSRBlockRecipe bcomp = (MenuComponentOverhaulMSRBlockRecipe)comp;
                if(bcomp.recipe==((multiblock.overhaul.fissionmsr.Block)block).recipe)blockRecipe.setSelectedIndex(i);
            }
            if(comp instanceof MenuComponentOverhaulFusionBlockRecipe){
                MenuComponentOverhaulFusionBlockRecipe bcomp = (MenuComponentOverhaulFusionBlockRecipe)comp;
                if(bcomp.recipe==((multiblock.overhaul.fusion.Block)block).recipe)blockRecipe.setSelectedIndex(i);
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
    public multiblock.configuration.overhaul.fusion.BlockRecipe getSelectedOverhaulFusionBlockRecipe(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        MenuComponent comp = blockRecipe.getSelectedComponent();
        if(comp==null)return null;
        return ((MenuComponentOverhaulFusionBlockRecipe)comp).recipe;
    }
    @Override
    public multiblock.configuration.overhaul.fissionsfr.BlockRecipe getSelectedOverhaulSFRBlockRecipe(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        MenuComponent comp = blockRecipe.getSelectedComponent();
        if(comp==null)return null;
        return ((MenuComponentOverhaulSFRBlockRecipe)comp).recipe;
    }
    @Override
    public multiblock.configuration.overhaul.fissionmsr.BlockRecipe getSelectedOverhaulMSRBlockRecipe(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        MenuComponent comp = blockRecipe.getSelectedComponent();
        if(comp==null)return null;
        return ((MenuComponentOverhaulMSRBlockRecipe)comp).recipe;
    }
    @Override
    public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
        super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
        if(isPress){
            boolean aSearchBoxIsSelected = partsSearch.isSelected;
            for(MenuComponent c : components){
                if(c instanceof MenuComponentDropdownList){
                    if(((MenuComponentDropdownList)c).searchBox.isSelected){
                        aSearchBoxIsSelected = true;
                    }
                }
            }
            if(key==GLFW.GLFW_KEY_ESCAPE){
                for(MenuComponent c : components){
                    if(c instanceof MenuComponentDropdownList){
                        if(((MenuComponentDropdownList)c).searchBox.isSelected){
                            ((MenuComponentDropdownList)c).searchBox.isSelected = false;
                            c.selected = null;
                        }
                    }
                }
                partsSearch.isSelected = false;
                selected = null;
                if(!aSearchBoxIsSelected){
                    if(getSelectedTool(0) instanceof PasteTool||getSelectedTool(0) instanceof CopyTool||getSelectedTool(0) instanceof CutTool){
                        tools.setSelectedIndex(1);
                    }else{
                        clearSelection(0);
                    }
                }
            }
            if(key==GLFW.GLFW_KEY_DELETE){
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
                if(key==GLFW.GLFW_KEY_M||key==GLFW.GLFW_KEY_1)tools.setSelectedIndex(0);
                if(key==GLFW.GLFW_KEY_S||key==GLFW.GLFW_KEY_2)tools.setSelectedIndex(1);
                if(key==GLFW.GLFW_KEY_P||key==GLFW.GLFW_KEY_3)tools.setSelectedIndex(2);
                if(key==GLFW.GLFW_KEY_L||key==GLFW.GLFW_KEY_4)tools.setSelectedIndex(3);
                if(key==GLFW.GLFW_KEY_B||key==GLFW.GLFW_KEY_5)tools.setSelectedIndex(4);
            }
            if(Core.isControlPressed()){
                if(key==GLFW.GLFW_KEY_A){
                    selectAll(0);
                }
                if(key==(Core.invertUndoRedo?GLFW.GLFW_KEY_Y:GLFW.GLFW_KEY_Z)){
                    multiblock.undo(autoRecalc);
                    if(Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
                        ((CuboidalMultiblock)multiblock).buildDefaultCasing();
                        if(autoRecalc)multiblock.recalculate();
                    }
                }
                if(key==(Core.invertUndoRedo?GLFW.GLFW_KEY_Z:GLFW.GLFW_KEY_Y)){
                    multiblock.redo(autoRecalc);
                    if(Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
                        ((CuboidalMultiblock)multiblock).buildDefaultCasing();
                        if(autoRecalc)multiblock.recalculate();
                    }
                }
                MenuComponentEditorGrid grid = null;
                for(MenuComponent c : multibwauk.components){
                    if(!c.isMouseOver)continue;
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
                if(key==GLFW.GLFW_KEY_C){
                    copySelection(0, x, y, z);
                }
                if(key==GLFW.GLFW_KEY_X){
                    cutSelection(0, x, y, z);
                }
                if(key==GLFW.GLFW_KEY_V){
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
        for(Iterator<int[]> it = set.locations.iterator(); it.hasNext();){
            int[] b = it.next();
            if(hasSelection(id)&&!isSelected(id, b[0], b[1], b[2]))it.remove();
            else if(Core.isControlPressed()){
                if(set.block==null){
                    if(multiblock.getBlock(b[0], b[1], b[2])!=null&&!multiblock.getBlock(b[0], b[1], b[2]).matches(getSelectedBlock(0)))it.remove();
                }else{
                    if(multiblock.getBlock(b[0], b[1], b[2])!=null&&!Core.isShiftPressed()){
                        it.remove();
                    }else if(multiblock.getBlock(b[0], b[1], b[2])!=null&&!multiblock.getBlock(b[0], b[1], b[2]).canBeQuickReplaced()){
                        it.remove();
                    }else if(multiblock.getBlock(b[0], b[1], b[2])==null||multiblock.getBlock(b[0], b[1], b[2])!=null&&Core.isShiftPressed()){
                        if(!multiblock.isValid(set.block, b[0], b[1], b[2]))it.remove();
                    }
                }
            }
        }
        if(set.block!=null&&multiblock instanceof OverhaulSFR){
            multiblock.overhaul.fissionsfr.Block block = (multiblock.overhaul.fissionsfr.Block)set.block;
            if(!block.template.allRecipes.isEmpty()||(block.template.parent!=null&&!block.template.parent.allRecipes.isEmpty())){
                block.recipe = getSelectedOverhaulSFRBlockRecipe(id);
            }
        }
        if(set.block!=null&&multiblock instanceof OverhaulMSR){
            multiblock.overhaul.fissionmsr.Block block = (multiblock.overhaul.fissionmsr.Block)set.block;
            if(!block.template.allRecipes.isEmpty()||(block.template.parent!=null&&!block.template.parent.allRecipes.isEmpty())){
                block.recipe = getSelectedOverhaulMSRBlockRecipe(id);
            }
        }
        if(set.block!=null&&multiblock instanceof OverhaulFusionReactor){
            multiblock.overhaul.fusion.Block block = (multiblock.overhaul.fusion.Block)set.block;
            if(!block.template.allRecipes.isEmpty()){
                block.recipe = getSelectedOverhaulFusionBlockRecipe(id);
            }
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
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
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
    public void selectGroup(int id, int x, int y, int z){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        ArrayList<Block> g = multiblock.getGroup(multiblock.getBlock(x, y, z));
        if(g==null){
            selectAll(id);
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
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        ArrayList<Block> g = multiblock.getGroup(multiblock.getBlock(x, y, z));
        if(g==null){
            deselectAll(id);
            return;
        }
        ArrayList<int[]> is = new ArrayList<>();
        for(Block b : g){
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
                    Block b = multiblock.getBlock(is[0], is[1], is[2]);
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
    public void setCoolantRecipe(int idx){
        underFuelOrCoolantRecipe.setSelectedIndex(idx);
    }
    @Override
    public void setUnderhaulFuel(int idx){
        underFuelOrCoolantRecipe.setSelectedIndex(idx);
    }
    @Override
    public void setFusionCoolantRecipe(int idx){
        underFuelOrCoolantRecipe.setSelectedIndex(idx);
    }
    @Override
    public void setFusionRecipe(int idx){
        fusionRecipe.setSelectedIndex(idx);
    }
    @Override
    public void setTurbineRecipe(int idx){
        underFuelOrCoolantRecipe.setSelectedIndex(idx);
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
        if(Core.autoBuildCasing&&multiblock instanceof CuboidalMultiblock){
            ((CuboidalMultiblock)multiblock).buildDefaultCasing();
            if(autoRecalc)multiblock.recalculate();
        }
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(getSelectedBlock(0)!=lastSelectedBlock){
            refreshBlockRecipes();
            lastSelectedBlock = getSelectedBlock(0);
        }
        if(multiblock instanceof UnderhaulSFR){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                multiblock.configuration.underhaul.fissionsfr.Fuel fuel = Core.configuration.underhaul.fissionSFR.allFuels.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((UnderhaulSFR)multiblock).fuel!=fuel){
                    action(new SetFuelAction(this, fuel), true);
                }
            }
        }
        if(multiblock instanceof OverhaulSFR){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                multiblock.configuration.overhaul.fissionsfr.CoolantRecipe recipe = Core.configuration.overhaul.fissionSFR.allCoolantRecipes.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((OverhaulSFR)multiblock).coolantRecipe!=recipe){
                    action(new SetCoolantRecipeAction(this, recipe), true);
                }
            }
        }
        if(multiblock instanceof OverhaulFusionReactor){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                multiblock.configuration.overhaul.fusion.CoolantRecipe recipe = Core.configuration.overhaul.fusion.allCoolantRecipes.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((OverhaulFusionReactor)multiblock).coolantRecipe!=recipe){
                    action(new SetFusionCoolantRecipeAction(this, recipe), true);
                }
            }
            if(fusionRecipe.getSelectedIndex()>-1){
                multiblock.configuration.overhaul.fusion.Recipe recipe = Core.configuration.overhaul.fusion.allRecipes.get(fusionRecipe.getSelectedIndex());
                if(((OverhaulFusionReactor)multiblock).recipe!=recipe){
                    action(new SetFusionRecipeAction(this, recipe), true);
                }
            }
        }
        if(multiblock instanceof OverhaulTurbine){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                multiblock.configuration.overhaul.turbine.Recipe recipe = Core.configuration.overhaul.turbine.allRecipes.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((OverhaulTurbine)multiblock).recipe!=recipe){
                    action(new SetTurbineRecipeAction(this, recipe), true);
                }
            }
        }
    }
    @Override
    public boolean onMouseScrolled(double x, double y, double dx, double dy){
        if(Core.isControlPressed()&&dy!=0){
            zoom(dy*zoomScrollMagnitude);
            return true;
        }
        return super.onMouseScrolled(x, y, dx, dy);
    }
    private void zoom(double zoom){
        for(int i = 0; i<Math.abs(zoom); i++){
            scale = Math.max(minScale, Math.min(maxScale, scale*Math.pow(scaleFac, zoom)));
        }
        CELL_SIZE = (int) (16*scale);
        LAYER_GAP = CELL_SIZE/2;
        multibwauk.setScrollMagnitude(CELL_SIZE*scrollMagnitude);multibwauk.setScrollWheelMagnitude(CELL_SIZE*scrollMagnitude);
        recalculateOnOpen = false;
        onGUIOpened();
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
        Object was = null;
        MenuComponent comp = blockRecipe.getSelectedComponent();
        if(comp instanceof MenuComponentOverhaulSFRBlockRecipe)was = ((MenuComponentOverhaulSFRBlockRecipe)comp).recipe;
        if(comp instanceof MenuComponentOverhaulMSRBlockRecipe)was = ((MenuComponentOverhaulMSRBlockRecipe)comp).recipe;
        if(comp instanceof MenuComponentOverhaulFusionBlockRecipe)was = ((MenuComponentOverhaulFusionBlockRecipe)comp).recipe;
        blockRecipe.clear();
        if(multiblock instanceof OverhaulSFR){
            multiblock.configuration.overhaul.fissionsfr.Block b = ((multiblock.overhaul.fissionsfr.Block)getSelectedBlock(0)).template;
            if(b.parent!=null)b = b.parent;
            for(multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : b.allRecipes){
                blockRecipe.add(new MenuComponentOverhaulSFRBlockRecipe(b, recipe));
            }
        }
        if(multiblock instanceof OverhaulMSR){
            multiblock.configuration.overhaul.fissionmsr.Block b = ((multiblock.overhaul.fissionmsr.Block)getSelectedBlock(0)).template;
            if(b.parent!=null)b = b.parent;
            for(multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : b.allRecipes){
                blockRecipe.add(new MenuComponentOverhaulMSRBlockRecipe(b, recipe));
            }
        }
        if(multiblock instanceof OverhaulFusionReactor){
            multiblock.configuration.overhaul.fusion.Block b = ((multiblock.overhaul.fusion.Block)getSelectedBlock(0)).template;
            for(multiblock.configuration.overhaul.fusion.BlockRecipe recipe : b.allRecipes){
                blockRecipe.add(new MenuComponentOverhaulFusionBlockRecipe(b, recipe));
            }
        }
        for(int i = 0; i<blockRecipe.allComponents.size(); i++){
            MenuComponent c = blockRecipe.allComponents.get(i);
            if(c instanceof MenuComponentOverhaulSFRBlockRecipe&&was==((MenuComponentOverhaulSFRBlockRecipe)c).recipe)blockRecipe.setSelectedIndex(i);
            if(c instanceof MenuComponentOverhaulMSRBlockRecipe&&was==((MenuComponentOverhaulMSRBlockRecipe)c).recipe)blockRecipe.setSelectedIndex(i);
            if(c instanceof MenuComponentOverhaulFusionBlockRecipe&&was==((MenuComponentOverhaulFusionBlockRecipe)c).recipe)blockRecipe.setSelectedIndex(i);
        }
    }
    private synchronized void refreshPartsList(){
        List<Block> availableBlocks = ((Multiblock<Block>)multiblock).getAvailableBlocks();
        ArrayList<Block> searchedAvailable = new ArrayList<>();
        for(Block b : availableBlocks){
            if(Searchable.isValidForSearch(b, partsSearch.text))searchedAvailable.add(b);
        }
        Block selectedBlock = getSelectedBlock(0);
        int i = 0;
        int idx = 0;
        parts.components.clear();
        for(Block availableBlock : searchedAvailable){
            parts.add(new MenuComponentEditorListBlock(this, availableBlock));
            if(selectedBlock.isEqual(availableBlock))idx = i;
            i++;
        }
        parts.setSelectedIndex(idx);
    }
    public void draw3D(){
        BoundingBox bbox = multiblock.getBoundingBox();
        float resonatingAlpha = 0.25f;
        double blockSize = 1;
        Core.applyColor(Core.theme.get3DMultiblockOutlineColor());
        VRCore.drawCubeOutline(-blockSize/32,-blockSize/32,-blockSize/32,bbox.getWidth()+blockSize/32,bbox.getHeight()+blockSize/32,bbox.getDepth()+blockSize/32,blockSize/24);
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
                    return block.shouldRenderFace(b);
                });
            }
            if(isSelected(0, x, y, z)){
                Core.applyColor(Core.theme.getSelectionColor());
                VRCore.drawCubeOutline(X-border, Y-border, Z-border, X+blockSize+border, Y+blockSize+border, Z+blockSize+border, border, (t) -> {
                    boolean d1 = isSelected(0, xx+t[0].x, yy+t[0].y, zz+t[0].z);
                    boolean d2 = isSelected(0, xx+t[1].x, yy+t[1].y, zz+t[1].z);
                    boolean d3 = isSelected(0, xx+t[0].x+t[1].x, yy+t[0].y+t[1].y, zz+t[0].z+t[1].z);
                    if(d1&&d2&&!d3)return true;//both sides, but not the corner
                    if(!d1&&!d2)return true;//neither side
                    return false;
                });
            }
            //TODO There's a better way to do this, but this'll do for now
            for(Suggestion s : getSuggestions()){
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
        for(MenuComponent comp : multibwauk.components){
            if(comp instanceof MenuComponentEditorGrid){
                MenuComponentEditorGrid grid = (MenuComponentEditorGrid)comp;
                if(grid.mouseover==null)continue;
                int bx = (grid.x1+grid.mouseover[0])*grid.xAxis.x+(grid.y1+grid.mouseover[1])*grid.yAxis.x+grid.layer*grid.axis.x;
                int by = (grid.x1+grid.mouseover[0])*grid.xAxis.y+(grid.y1+grid.mouseover[1])*grid.yAxis.y+grid.layer*grid.axis.y;
                int bz = (grid.x1+grid.mouseover[0])*grid.xAxis.z+(grid.y1+grid.mouseover[1])*grid.yAxis.z+grid.layer*grid.axis.z;
                double X = bx*blockSize;
                double Y = by*blockSize;
                double Z = bz*blockSize;
                double border = blockSize/16;
                Core.applyColor(Core.theme.get3DDeviceoverOutlineColor());
                VRCore.drawCubeOutline(X-border/2, Y-border/2, Z-border/2, X+blockSize+border/2, Y+blockSize+border/2, Z+blockSize+border/2, border);
                Core.applyColor(Core.theme.getEditorMouseoverLineColor());
                X+=blockSize/2;
                Y+=blockSize/2;
                Z+=blockSize/2;
                VRCore.drawCube(0, Y-border/2, Z-border/2, X-blockSize/2, Y+border/2, Z+border/2, 0);//NX
                VRCore.drawCube(X-border/2, 0, Z-border/2, X+border/2, Y-blockSize/2, Z+border/2, 0);//NY
                VRCore.drawCube(X-border/2, Y-border/2, 0, X+border/2, Y+border/2, Z-blockSize/2, 0);//NZ
                VRCore.drawCube(X+blockSize/2, Y-border/2, Z-border/2, bbox.getWidth()*blockSize, Y+border/2, Z+border/2, 0);//PX
                VRCore.drawCube(X-border/2, Y+blockSize/2, Z-border/2, X+border/2, bbox.getHeight()*blockSize, Z+border/2, 0);//PY
                VRCore.drawCube(X-border/2, Y-border/2, Z+blockSize/2, X+border/2, Y+border/2, bbox.getDepth()*blockSize, 0);//PZ
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
            if(multiblock instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multiblock).getLocationCategory(x, y, z)==OverhaulFusionReactor.LocationCategory.PLASMA){
                Core.applyWhite();
                VRCore.drawCube(X, Y, Z, X+blockSize, Y+blockSize, Z+blockSize, ImageStash.instance.getTexture("/textures/overhaul/fusion/plasma.png"), (t) -> {
                    if(!multiblock.contains(xx+t.x, yy+t.y, zz+t.z))return true;
                    Block b = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                    if(((OverhaulFusionReactor)multiblock).getLocationCategory(xx+t.x, yy+t.y, zz+t.z)!=OverhaulFusionReactor.LocationCategory.PLASMA)return true;
                    return b==null||Core.hasAlpha(b.getBaseTexture());
                });
            }
            if(isControlPressed(0)){
                if(block==null||(isShiftPressed(0)&&block.canBeQuickReplaced())){
                    for(EditorSpace space : ((Multiblock<Block>)multiblock).getEditorSpaces()){
                        if(space.isSpaceValid(getSelectedBlock(0), x, y, z)&&multiblock.isValid(getSelectedBlock(0), x, y, z)){
                            getSelectedBlock(0).render(X, Y, Z, blockSize, blockSize, blockSize, false, resonatingAlpha, multiblock, (t) -> {
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
            if(isSelected(0, x, y, z)){
                Core.applyColor(convertToolColor(Core.theme.getSelectionColor(), 0), .5f);
                VRCore.drawCube(X-border/4, Y-border/4, Z-border/4, X+blockSize+border/4, Y+blockSize+border/4, Z+blockSize+border/4, 0, (t) -> {
                    if(!multiblock.contains(xx+t.x, yy+t.y, zz+t.z))return true;
                    Block o = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                    return !isSelected(0, xx+t.x, yy+t.y, zz+t.z)&&o==null;
                });
            }
        });
        for(EditorSpace space : ((Multiblock<Block>)multiblock).getEditorSpaces()){
            getSelectedTool(0).drawVRGhosts(space, 0, 0, 0, 1, 1, 1, blockSize, (getSelectedBlock(0)==null?0:Core.getTexture(getSelectedBlock(0).getTexture())));
        }
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==recalc){
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
        }
        if(button==calcStep)multiblock.recalcStep();
    }
}
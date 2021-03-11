package planner.menu;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.action.ClearSelectionAction;
import multiblock.action.CopyAction;
import multiblock.action.DeselectAction;
import multiblock.action.MoveAction;
import multiblock.action.PasteAction;
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
import planner.menu.component.MenuComponentMinimalistTextView;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.editor.MenuComponentCoolantRecipe;
import planner.menu.component.editor.MenuComponentEditorGrid;
import planner.menu.component.editor.MenuComponentEditorListBlock;
import planner.menu.component.editor.MenuComponentEditorTool;
import planner.menu.component.editor.MenuComponentFusionBreedingBlanketRecipe;
import planner.menu.component.editor.MenuComponentFusionCoolantRecipe;
import planner.menu.component.editor.MenuComponentMSRIrradiatorRecipe;
import planner.menu.component.editor.MenuComponentMultiblockProgressBar;
import planner.menu.component.editor.MenuComponentOverFusionRecipe;
import planner.menu.component.editor.MenuComponentOverMSRFuel;
import planner.menu.component.editor.MenuComponentOverSFRFuel;
import planner.menu.component.editor.MenuComponentSFRIrradiatorRecipe;
import planner.menu.component.editor.MenuComponentSuggestion;
import planner.menu.component.editor.MenuComponentSuggestor;
import planner.menu.component.editor.MenuComponentTurbineBladeEditorGrid;
import planner.menu.component.editor.MenuComponentTurbineCoilEditorGrid;
import planner.menu.component.editor.MenuComponentTurbineRecipe;
import planner.menu.component.editor.MenuComponentUnderFuel;
import planner.module.Module;
import simplelibrary.game.Framebuffer;
import simplelibrary.opengl.ImageStash;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
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
    public final MenuComponentMinimalistScrollable multibwauk = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 32, 32));
    private final MenuComponentMinimalistButton zoomOut = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Zoom out", true, true));
    private final MenuComponentMinimalistButton zoomIn = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Zoom in", true, true));
    private final MenuComponentMinimalistButton resize = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Resize", true, true).setTooltip("Resize the multiblock\nWARNING: This clears the edit history! (undo/redo)"));
    public final MenuComponentDropdownList underFuelOrCoolantRecipe = new MenuComponentDropdownList(0, 0, 0, 32);
    public final MenuComponentDropdownList overFuel = new MenuComponentDropdownList(0, 0, 0, 32);
    private final MenuComponentDropdownList irradiatorRecipe = new MenuComponentDropdownList(0, 0, 0, 32);
    private final MenuComponentMinimalistTextView textBox = add(new MenuComponentMinimalistTextView(0, 0, 0, 0, 24, 24));
    private final MenuComponentMinimalistButton editMetadata = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true).setTooltip("Modify the multiblock metadata"));
    public final MenuComponentMinimaList tools = add(new MenuComponentMinimaList(0, 0, 0, 0, partSize/2));
    private final MenuComponentMinimalistButton generate = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Generate", true, true).setTooltip("Generate or improve this multiblock"));
    private final MenuComponentMultiblockProgressBar progress = add(new MenuComponentMultiblockProgressBar(this, 0, 0, 0, 0));;
    private final MenuComponentMinimaList suggestionList = add(new MenuComponentMinimaList(0, 0, 0, 0, partSize/2));
    private final MenuComponentDropdownList suggestorSettings = add(new MenuComponentDropdownList(0, 0, 0, 0){
        @Override
        public void render(){
            if(!isDown){
                Color col = Core.theme.getButtonColor();
                if(isMouseOver)col = col.brighter();//TODO .brighter()
                Core.applyColor(col);
                drawRect(x, y, x+width, y+height, 0);
                Core.applyColor(Core.theme.getTextColor());
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
    private int multisPerRow = 0;
    private long lastChange = 0;
    private boolean closed = false;
    public MenuEdit(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
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
            add(overFuel);
            for(multiblock.configuration.overhaul.fissionsfr.Fuel fuel : Core.configuration.overhaul.fissionSFR.allFuels){
                overFuel.add(new MenuComponentOverSFRFuel(fuel));
            }
            overFuel.setSelectedIndex(0);
            add(irradiatorRecipe);
            for(multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe recipe : Core.configuration.overhaul.fissionSFR.allIrradiatorRecipes){
                irradiatorRecipe.add(new MenuComponentSFRIrradiatorRecipe(recipe));
            }
            irradiatorRecipe.setSelectedIndex(0);
        }
        if(multiblock instanceof OverhaulTurbine){
            add(underFuelOrCoolantRecipe);
            for(multiblock.configuration.overhaul.turbine.Recipe recipe : Core.configuration.overhaul.turbine.allRecipes){
                underFuelOrCoolantRecipe.add(new MenuComponentTurbineRecipe(recipe));
            }
        }
        if(multiblock instanceof OverhaulMSR){
            add(overFuel);
            for(multiblock.configuration.overhaul.fissionmsr.Fuel fuel : Core.configuration.overhaul.fissionMSR.allFuels){
                overFuel.add(new MenuComponentOverMSRFuel(fuel));
            }
            overFuel.setSelectedIndex(0);
            add(irradiatorRecipe);
            for(multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe recipe : Core.configuration.overhaul.fissionMSR.allIrradiatorRecipes){
                irradiatorRecipe.add(new MenuComponentMSRIrradiatorRecipe(recipe));
            }
            irradiatorRecipe.setSelectedIndex(0);
        }
        if(multiblock instanceof OverhaulFusionReactor){
            add(underFuelOrCoolantRecipe);
            for(multiblock.configuration.overhaul.fusion.CoolantRecipe recipe : Core.configuration.overhaul.fusion.allCoolantRecipes){
                underFuelOrCoolantRecipe.add(new MenuComponentFusionCoolantRecipe(recipe));
            }
            add(overFuel);
            for(multiblock.configuration.overhaul.fusion.Recipe recipe : Core.configuration.overhaul.fusion.allRecipes){
                overFuel.add(new MenuComponentOverFusionRecipe(recipe));
            }
            overFuel.setSelectedIndex(0);
            add(irradiatorRecipe);
            for(multiblock.configuration.overhaul.fusion.BreedingBlanketRecipe recipe : Core.configuration.overhaul.fusion.allBreedingBlanketRecipes){
                irradiatorRecipe.add(new MenuComponentFusionBreedingBlanketRecipe(recipe));
            }
            irradiatorRecipe.setSelectedIndex(0);
        }
        this.multiblock = multiblock;
        multibwauk.setScrollMagnitude(CELL_SIZE*scrollMagnitude);
        back.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SplitTransition.slideOut((parts.x+parts.width)/gui.helper.displayWidth()), 5));
        });
        undo.addActionListener((e) -> {
            multiblock.undo();
        });
        redo.addActionListener((e) -> {
            multiblock.redo();
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
        for(Block availableBlock : ((Multiblock<Block>)multiblock).getAvailableBlocks()){
            parts.add(new MenuComponentEditorListBlock(this, availableBlock));
        }
        parts.setSelectedIndex(0);
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
    @Override
    public void onGUIOpened(){
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
        multisPerRow = Math.max(1, (int)((multibwauk.width-multibwauk.horizScrollbarHeight)/(CELL_SIZE*multiblock.getX()+LAYER_GAP)));
        if(multiblock instanceof OverhaulTurbine){
            int nextY = 0;
            for(int z = 0; z<2; z++){
                int column = z%multisPerRow;
                int row = z/multisPerRow;
                int layerWidth = multiblock.getX()*CELL_SIZE+LAYER_GAP;
                int layerHeight = multiblock.getY()*CELL_SIZE+LAYER_GAP;
                nextY = (row+1)*layerHeight+LAYER_GAP/2;
                if(z==1)z = multiblock.getZ()-1;//the coils
                multibwauk.add(new MenuComponentTurbineCoilEditorGrid(column*layerWidth+LAYER_GAP/2, row*layerHeight+LAYER_GAP/2, CELL_SIZE, this, (OverhaulTurbine)multiblock, z));
            }
            multibwauk.add(new MenuComponentTurbineBladeEditorGrid(LAYER_GAP/2, nextY, CELL_SIZE, this, (OverhaulTurbine)multiblock));
        }else{
            for(int y = 0; y<multiblock.getY(); y++){
                int column = y%multisPerRow;
                int row = y/multisPerRow;
                int layerWidth = multiblock.getX()*CELL_SIZE+LAYER_GAP;
                int layerHeight = multiblock.getZ()*CELL_SIZE+LAYER_GAP;
                multibwauk.add(new MenuComponentEditorGrid(column*layerWidth+LAYER_GAP/2, row*layerHeight+LAYER_GAP/2, CELL_SIZE, this, multiblock, y));
            }
        }
        multiblock.recalculate();
    }
    @Override
    public void onGUIClosed(){
        closed = true;
    }
    @Override
    public void render(int millisSinceLastTick){
        textBox.setText(multiblock.getTooltip());
        if(multisPerRow!=Math.max(1, (int)((multibwauk.width-multibwauk.horizScrollbarHeight)/(CELL_SIZE*multiblock.getX()+LAYER_GAP)))){
            onGUIOpened();
        }
        tools.x = textBox.x = back.x = progress.x = 0;
        parts.width = partsWide*partSize+parts.vertScrollbarWidth*(parts.hasVertScrollbar()?1:0);
        tools.width = partSize;
        parts.x = tools.width+partSize/4;
        generate.x = editMetadata.x = textBox.width = multibwauk.x = parts.x+parts.width;
        suggestorSettings.preferredHeight = generate.height = tools.y = multibwauk.y = parts.y = editMetadata.height = back.height = 48;
        back.width = parts.x+parts.width-back.height*2;
        undo.width = undo.height = redo.width = redo.height = back.height;
        undo.x = back.width;
        redo.x = undo.x+undo.width;
        undo.enabled = !multiblock.history.isEmpty();
        redo.enabled = !multiblock.future.isEmpty();
        generate.y = gui.helper.displayHeight()-generate.height;
        tools.height = editorTools.size()*partSize;
        tools.height = parts.height = Math.max(tools.height, Math.min(gui.helper.displayHeight()/2, ((parts.components.size()+5)/partsWide)*partSize));
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
        suggestionList.x = irradiatorRecipe.x = overFuel.x = underFuelOrCoolantRecipe.x = resize.x;
        underFuelOrCoolantRecipe.y = resize.height*2+underFuelOrCoolantRecipe.preferredHeight;
        suggestionList.width = irradiatorRecipe.width = overFuel.width = underFuelOrCoolantRecipe.width = resize.width;
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
            overFuel.y = underFuelOrCoolantRecipe.y+underFuelOrCoolantRecipe.height+overFuel.preferredHeight;
            irradiatorRecipe.y = overFuel.y+overFuel.height+irradiatorRecipe.preferredHeight;
        }
        if(multiblock instanceof OverhaulFusionReactor){
            overFuel.y = underFuelOrCoolantRecipe.y+underFuelOrCoolantRecipe.height+overFuel.preferredHeight;
            irradiatorRecipe.y = overFuel.y+overFuel.height+irradiatorRecipe.preferredHeight;
        }
        if(multiblock instanceof OverhaulMSR){
            underFuelOrCoolantRecipe.x = -5000;
            overFuel.y = resize.height*2+overFuel.preferredHeight;
            irradiatorRecipe.y = overFuel.y+overFuel.height+irradiatorRecipe.preferredHeight;
        }
        suggestionList.y = Math.max(underFuelOrCoolantRecipe.y+underFuelOrCoolantRecipe.height, Math.max(overFuel.y+overFuel.height, irradiatorRecipe.y+irradiatorRecipe.height));
        suggestionList.height = gui.helper.displayHeight()-suggestionList.y;
        multibwauk.height = gui.helper.displayHeight()-multibwauk.y-generate.height;
        progress.y = generate.y-generate.height;
        progress.width = textBox.width;
        progress.height = generate.height*2;
        textBox.y = parts.y+parts.height;
        textBox.height = gui.helper.displayHeight()-textBox.y-progress.height;
        if(multiblock instanceof OverhaulTurbine){
            OverhaulTurbine turbine = (OverhaulTurbine)multiblock;
            double width = turbine.getDisplayZ()*CELL_SIZE;
            double height = CELL_SIZE*4;
            if(turbine.bladesValid){
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
                turbineGraph = new Framebuffer(gui.helper, null, (int)width, (int)height);
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
            Core.applyColor(Core.theme.getDarkButtonColor());
            drawRect(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, 0);
            Core.applyColor(Core.theme.getTextColor());
            drawCenteredText(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, "Fuel");
        }
        if(multiblock instanceof OverhaulSFR){
            Core.applyColor(Core.theme.getDarkButtonColor());
            drawRect(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, 0);
            drawRect(overFuel.x, overFuel.y-overFuel.preferredHeight, overFuel.x+overFuel.width, overFuel.y, 0);
            drawRect(irradiatorRecipe.x, irradiatorRecipe.y-irradiatorRecipe.preferredHeight, irradiatorRecipe.x+irradiatorRecipe.width, irradiatorRecipe.y, 0);
            Core.applyColor(Core.theme.getTextColor());
            drawCenteredText(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, "Coolant Recipe");
            drawCenteredText(overFuel.x, overFuel.y-overFuel.preferredHeight, overFuel.x+overFuel.width, overFuel.y, "Fuel");
            drawCenteredText(irradiatorRecipe.x, irradiatorRecipe.y-irradiatorRecipe.preferredHeight, irradiatorRecipe.x+irradiatorRecipe.width, irradiatorRecipe.y, "Irradiator Recipe");
        }
        if(multiblock instanceof OverhaulMSR){
            Core.applyColor(Core.theme.getDarkButtonColor());
            drawRect(overFuel.x, overFuel.y-overFuel.preferredHeight, overFuel.x+overFuel.width, overFuel.y, 0);
            drawRect(irradiatorRecipe.x, irradiatorRecipe.y-irradiatorRecipe.preferredHeight, irradiatorRecipe.x+irradiatorRecipe.width, irradiatorRecipe.y, 0);
            Core.applyColor(Core.theme.getTextColor());
            drawCenteredText(overFuel.x, overFuel.y-overFuel.preferredHeight, overFuel.x+overFuel.width, overFuel.y, "Fuel");
            drawCenteredText(irradiatorRecipe.x, irradiatorRecipe.y-irradiatorRecipe.preferredHeight, irradiatorRecipe.x+irradiatorRecipe.width, irradiatorRecipe.y, "Irradiator Recipe");
        }
        if(multiblock instanceof OverhaulTurbine){
            Core.applyColor(Core.theme.getDarkButtonColor());
            drawRect(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, 0);
            Core.applyColor(Core.theme.getTextColor());
            drawCenteredText(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, "Recipe");
        }
        if(multiblock instanceof OverhaulFusionReactor){
            Core.applyColor(Core.theme.getDarkButtonColor());
            drawRect(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, 0);
            drawRect(overFuel.x, overFuel.y-overFuel.preferredHeight, overFuel.x+overFuel.width, overFuel.y, 0);
            drawRect(irradiatorRecipe.x, irradiatorRecipe.y-irradiatorRecipe.preferredHeight, irradiatorRecipe.x+irradiatorRecipe.width, irradiatorRecipe.y, 0);
            Core.applyColor(Core.theme.getTextColor());
            drawCenteredText(underFuelOrCoolantRecipe.x, underFuelOrCoolantRecipe.y-underFuelOrCoolantRecipe.preferredHeight, underFuelOrCoolantRecipe.x+underFuelOrCoolantRecipe.width, underFuelOrCoolantRecipe.y, "Coolant Recipe");
            drawCenteredText(overFuel.x, overFuel.y-overFuel.preferredHeight, overFuel.x+overFuel.width, overFuel.y, "Recipe");
            drawCenteredText(irradiatorRecipe.x, irradiatorRecipe.y-irradiatorRecipe.preferredHeight, irradiatorRecipe.x+irradiatorRecipe.width, irradiatorRecipe.y, "Breeding Blanket Recipe");
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
        if(parts.getSelectedIndex()==-1)return null;
        return ((MenuComponentEditorListBlock) parts.components.get(parts.getSelectedIndex())).block;
    }
    public void setSelectedBlock(Block block){
        if(block==null)return;
        for(int i = 0; i<parts.components.size(); i++){
            MenuComponentEditorListBlock comp = (MenuComponentEditorListBlock)parts.components.get(i);
            if(comp.block.isEqual(block))parts.setSelectedIndex(i);
        }
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
    public multiblock.configuration.overhaul.fusion.BreedingBlanketRecipe getSelectedFusionBreedingBlanketRecipe(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        return ((MenuComponentFusionBreedingBlanketRecipe) irradiatorRecipe.getSelectedComponent()).recipe;
    }
    @Override
    public multiblock.configuration.overhaul.fissionsfr.Fuel getSelectedOverSFRFuel(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        return ((MenuComponentOverSFRFuel) overFuel.getSelectedComponent()).fuel;
    }
    @Override
    public multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe getSelectedSFRIrradiatorRecipe(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        return ((MenuComponentSFRIrradiatorRecipe) irradiatorRecipe.getSelectedComponent()).recipe;
    }
    @Override
    public multiblock.configuration.overhaul.fissionmsr.Fuel getSelectedOverMSRFuel(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        return ((MenuComponentOverMSRFuel) overFuel.getSelectedComponent()).fuel;
    }
    @Override
    public multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe getSelectedMSRIrradiatorRecipe(int id){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        return ((MenuComponentMSRIrradiatorRecipe) irradiatorRecipe.getSelectedComponent()).recipe;
    }
    @Override
    public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
        super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
        if(isPress){
            if(key==GLFW.GLFW_KEY_ESCAPE){
                if(getSelectedTool(0) instanceof PasteTool||getSelectedTool(0) instanceof CopyTool||getSelectedTool(0) instanceof CutTool){
                    tools.setSelectedIndex(1);
                }else{
                    clearSelection(0);
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
            if(key==GLFW.GLFW_KEY_M||key==GLFW.GLFW_KEY_1)tools.setSelectedIndex(0);
            if(key==GLFW.GLFW_KEY_S||key==GLFW.GLFW_KEY_2)tools.setSelectedIndex(1);
            if(key==GLFW.GLFW_KEY_P||key==GLFW.GLFW_KEY_3)tools.setSelectedIndex(2);
            if(key==GLFW.GLFW_KEY_L||key==GLFW.GLFW_KEY_4)tools.setSelectedIndex(3);
            if(key==GLFW.GLFW_KEY_B||key==GLFW.GLFW_KEY_5)tools.setSelectedIndex(4);
            if(Core.isControlPressed()){
                if(key==GLFW.GLFW_KEY_A){
                    ArrayList<int[]> sel = new ArrayList<>();
                    for(int x = 0; x<multiblock.getX(); x++){
                        for(int y = 0; y<multiblock.getY(); y++){
                            for(int z = 0; z<multiblock.getZ(); z++){
                                sel.add(new int[]{x,y,z});
                            }
                        }
                    }
                    setSelection(0, sel);
                }
                if(key==(Core.invertUndoRedo?GLFW.GLFW_KEY_Y:GLFW.GLFW_KEY_Z)){
                    multiblock.undo();
                }
                if(key==(Core.invertUndoRedo?GLFW.GLFW_KEY_Z:GLFW.GLFW_KEY_Y)){
                    multiblock.redo();
                }
                MenuComponent grid = null;
                for(MenuComponent c : multibwauk.components){
                    if(!c.isMouseOver)continue;
                    if(c instanceof MenuComponentEditorGrid)grid = c;
                    if(c instanceof MenuComponentTurbineCoilEditorGrid)grid = c;
                    if(c instanceof MenuComponentTurbineBladeEditorGrid)grid = c;
                }
                if(grid!=null){
                    int x = -1,y = -1,z = -1;
                    double mx = gui.mouseX-(multibwauk.x+grid.x-multibwauk.getHorizScroll());
                    double my = gui.mouseY-(multibwauk.y+grid.y-multibwauk.getVertScroll());
                    if(grid instanceof MenuComponentEditorGrid){
                        x = (int)(mx/((MenuComponentEditorGrid)grid).blockSize);
                        y = ((MenuComponentEditorGrid)grid).layer;
                        z = (int)(my/((MenuComponentEditorGrid)grid).blockSize);
                    }
                    if(grid instanceof MenuComponentTurbineCoilEditorGrid){
                        x = (int)(mx/((MenuComponentTurbineCoilEditorGrid)grid).blockSize);
                        y = (int)(my/((MenuComponentTurbineCoilEditorGrid)grid).blockSize);
                        z = ((MenuComponentTurbineCoilEditorGrid)grid).layer;
                    }
                    if(grid instanceof MenuComponentTurbineBladeEditorGrid){
                        x = multiblock.getX()/2;
                        y = 0;
                        z = (int)(mx/((MenuComponentTurbineBladeEditorGrid)grid).blockSize);
                    }
                    if(x<0||y<0||z<0||x>=multiblock.getX()||y>=multiblock.getY()||z>=multiblock.getZ()){
                        //do nothing
                    }else{
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
                }else{
                    if(key==GLFW.GLFW_KEY_C){
                        copySelection(0, -1, -1, -1);
                    }
                    if(key==GLFW.GLFW_KEY_X){
                        cutSelection(0, -1, -1, -1);
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
            if(((multiblock.overhaul.fissionsfr.Block)set.block).isFuelCell()){
                ((multiblock.overhaul.fissionsfr.Block)set.block).fuel = getSelectedOverSFRFuel(id);
            }
            if(((multiblock.overhaul.fissionsfr.Block)set.block).isIrradiator()){
                ((multiblock.overhaul.fissionsfr.Block)set.block).irradiatorRecipe = getSelectedSFRIrradiatorRecipe(id);
            }
        }
        if(set.block!=null&&multiblock instanceof OverhaulMSR){
            if(((multiblock.overhaul.fissionmsr.Block)set.block).isFuelVessel()){
                ((multiblock.overhaul.fissionmsr.Block)set.block).fuel = getSelectedOverMSRFuel(id);
            }
            if(((multiblock.overhaul.fissionmsr.Block)set.block).isIrradiator()){
                ((multiblock.overhaul.fissionmsr.Block)set.block).irradiatorRecipe = getSelectedMSRIrradiatorRecipe(id);
            }
        }
        if(set.block!=null&&multiblock instanceof OverhaulFusionReactor){
            if(((multiblock.overhaul.fusion.Block)set.block).isBreedingBlanket()){
                ((multiblock.overhaul.fusion.Block)set.block).breedingBlanketRecipe = getSelectedFusionBreedingBlanketRecipe(id);
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
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
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
    public void moveSelection(int id, int x, int y, int z){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        action(new MoveAction(this, id, selection, x, y, z), true);
    }
    @Override
    public void cloneSelection(int id, int x, int y, int z){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        action(new CopyAction(this, id, selection, x, y, z), true);
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
    @Override
    public void pasteSelection(int id, int x, int y, int z){
        if(id!=0)throw new IllegalArgumentException("Standard editor only supports one cursor!");
        synchronized(clipboard){
            action(new PasteAction(clipboard, x, y, z), true);
        }
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
        overFuel.setSelectedIndex(idx);
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
        multiblock.action(action, allowUndo);
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
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
            if(overFuel.getSelectedIndex()>-1){
                multiblock.configuration.overhaul.fusion.Recipe recipe = Core.configuration.overhaul.fusion.allRecipes.get(overFuel.getSelectedIndex());
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
        onGUIOpened();
    }
    private void zoomOut(double amount){
        zoom(-amount);
    }
    private void zoomIn(double amount){
        zoom(amount);
    }
}
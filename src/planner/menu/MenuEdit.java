package planner.menu;
import java.util.ArrayList;
import java.util.Iterator;
import planner.Core;
import planner.menu.component.editor.MenuComponentCoolantRecipe;
import planner.menu.component.editor.MenuComponentEditorListBlock;
import planner.menu.component.editor.MenuComponentEditorTool;
import planner.menu.component.editor.MenuComponentSFRIrradiatorRecipe;
import planner.menu.component.editor.MenuComponentMSRIrradiatorRecipe;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.MenuComponentMinimalistTextView;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.editor.MenuComponentOverMSRFuel;
import planner.menu.component.editor.MenuComponentOverSFRFuel;
import planner.menu.component.editor.MenuComponentUnderFuel;
import planner.tool.EditorTool;
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
import multiblock.action.SetblockAction;
import multiblock.action.SetblocksAction;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.overhaul.turbine.OverhaulTurbine;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import planner.menu.component.MenuComponentDropdownList;
import planner.menu.component.editor.MenuComponentEditorGrid;
import planner.menu.component.editor.MenuComponentFusionBreedingBlanketRecipe;
import planner.menu.component.editor.MenuComponentFusionCoolantRecipe;
import planner.menu.component.editor.MenuComponentOverFusionRecipe;
import planner.menu.component.editor.MenuComponentTurbineBladeEditorGrid;
import planner.menu.component.editor.MenuComponentTurbineCoilEditorGrid;
import planner.menu.component.editor.MenuComponentTurbineRecipe;
import planner.tool.CopyTool;
import planner.tool.CutTool;
import planner.tool.LineTool;
import planner.tool.MoveTool;
import planner.tool.PasteTool;
import planner.tool.PencilTool;
import planner.tool.RectangleTool;
import planner.tool.SelectionTool;
import simplelibrary.game.Framebuffer;
import simplelibrary.opengl.ImageStash;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuEdit extends Menu{
    private final ArrayList<EditorTool> editorTools = new ArrayList<>();
    public Framebuffer turbineGraph;
    public ArrayList<ClipboardEntry> clipboard = new ArrayList<>();
    private EditorTool copy = new CopyTool(this);
    private MenuComponentEditorTool copyComp = new MenuComponentEditorTool(copy);
    private EditorTool cut = new CutTool(this);
    private MenuComponentEditorTool cutComp = new MenuComponentEditorTool(cut);
    private EditorTool paste = new PasteTool(this);
    private MenuComponentEditorTool pasteComp = new MenuComponentEditorTool(paste);
    {
        editorTools.add(new MoveTool(this));
        editorTools.add(new SelectionTool(this));
        editorTools.add(new PencilTool(this));
        editorTools.add(new LineTool(this));
        editorTools.add(new RectangleTool(this));
    }
    public final Multiblock multiblock;
    private final int partSize = 48;
    private final int partsWide = 7;
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true).setTooltip("Stop editing this multiblock and return to the main menu"));
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
    public final ArrayList<int[]> selection = new ArrayList<>();
    private double scale = 4;
    private double minScale = 0.5;
    private double maxScale = 16;
    public int CELL_SIZE = (int) (16*scale);
    private int LAYER_GAP = CELL_SIZE/2;
    private int multisPerRow = 0;
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
        multibwauk.setScrollMagnitude(CELL_SIZE/2);
        back.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(1, 0), 5));
        });
        resize.addActionListener((e) -> {
            multiblock.openResizeMenu(gui, this);
        });
        zoomOut.addActionListener((e) -> {
            scale = Math.max(minScale, Math.min(maxScale, scale/1.5));
            CELL_SIZE = (int) (16*scale);
            LAYER_GAP = CELL_SIZE/2;
            multibwauk.setScrollMagnitude(CELL_SIZE/2);multibwauk.setScrollWheelMagnitude(CELL_SIZE/2);
            onGUIOpened();
        });
        zoomIn.addActionListener((e) -> {
            scale = Math.max(minScale, Math.min(maxScale, scale*1.5));
            CELL_SIZE = (int) (16*scale);
            LAYER_GAP = CELL_SIZE/2;
            multibwauk.setScrollMagnitude(CELL_SIZE/2);multibwauk.setScrollWheelMagnitude(CELL_SIZE/2);
            onGUIOpened();
        });
        editMetadata.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, new MenuMultiblockMetadata(gui, this, multiblock), MenuTransition.SlideTransition.slideTo(0, 1), 4));
        });
        generate.addActionListener((e) -> {
            gui.open(new MenuGenerator(gui, this, multiblock));
        });
        for(Block availableBlock : ((Multiblock<Block>)multiblock).getAvailableBlocks()){
            parts.add(new MenuComponentEditorListBlock(this, availableBlock));
        }
        parts.setSelectedIndex(0);
        for(EditorTool tool : editorTools){
            tools.add(new MenuComponentEditorTool(tool));
        }
        tools.setSelectedIndex(2);
    }
    @Override
    public void onGUIOpened(){
        Core.delCircle = true;
        Core.circleSize = CELL_SIZE;
        editMetadata.label = multiblock.getName().isEmpty()?"Edit Metadata":(multiblock.getName()+" | Edit Metadata");
//        generate.label = multiblock.isEmpty()?"Generate":"Generate Suggestions";
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
    public void render(int millisSinceLastTick){
        textBox.setText(multiblock.getTooltip());
        if(multisPerRow!=Math.max(1, (int)((multibwauk.width-multibwauk.horizScrollbarHeight)/(CELL_SIZE*multiblock.getX()+LAYER_GAP)))){
            onGUIOpened();
        }
        parts.width = partsWide*partSize+parts.vertScrollbarWidth*(parts.hasVertScrollbar()?1:0);
        tools.width = partSize;
        parts.x = tools.width+partSize/4;
        generate.x = editMetadata.x = textBox.width = multibwauk.x = back.width = parts.x+parts.width;
        generate.height = tools.y = multibwauk.y = parts.y = editMetadata.height = back.height = 48;
        generate.y = gui.helper.displayHeight()-generate.height;
        tools.height = editorTools.size()*partSize;
        tools.height = parts.height = Math.max(tools.height, Math.min(gui.helper.displayHeight()/2, ((parts.components.size()+5)/partsWide)*partSize));
        resize.width = 320;
        generate.width = editMetadata.width = multibwauk.width = gui.helper.displayWidth()-parts.x-parts.width-resize.width;
        zoomIn.height = zoomOut.height = resize.height = back.height;
        zoomIn.width = zoomOut.width = resize.width/2;
        zoomIn.y = zoomOut.y = resize.height;
        resize.x = gui.helper.displayWidth()-resize.width;
        zoomIn.x = resize.x;
        zoomOut.x = zoomIn.x+zoomIn.width;
        irradiatorRecipe.x = overFuel.x = underFuelOrCoolantRecipe.x = resize.x;
        underFuelOrCoolantRecipe.y = resize.height*2+underFuelOrCoolantRecipe.preferredHeight;
        irradiatorRecipe.width = overFuel.width = underFuelOrCoolantRecipe.width = resize.width;
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
        if(multiblock instanceof UnderhaulSFR){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                multiblock.configuration.underhaul.fissionsfr.Fuel fuel = Core.configuration.underhaul.fissionSFR.allFuels.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((UnderhaulSFR)multiblock).fuel!=fuel){
                    multiblock.action(new SetFuelAction(this, fuel), true);
                }
            }
        }
        if(multiblock instanceof OverhaulSFR){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                multiblock.configuration.overhaul.fissionsfr.CoolantRecipe recipe = Core.configuration.overhaul.fissionSFR.allCoolantRecipes.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((OverhaulSFR)multiblock).coolantRecipe!=recipe){
                    multiblock.action(new SetCoolantRecipeAction(this, recipe), true);
                }
            }
        }
        if(multiblock instanceof OverhaulFusionReactor){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                multiblock.configuration.overhaul.fusion.CoolantRecipe recipe = Core.configuration.overhaul.fusion.allCoolantRecipes.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((OverhaulFusionReactor)multiblock).coolantRecipe!=recipe){
                    multiblock.action(new SetFusionCoolantRecipeAction(this, recipe), true);
                }
            }
            if(overFuel.getSelectedIndex()>-1){
                multiblock.configuration.overhaul.fusion.Recipe recipe = Core.configuration.overhaul.fusion.allRecipes.get(overFuel.getSelectedIndex());
                if(((OverhaulFusionReactor)multiblock).recipe!=recipe){
                    multiblock.action(new SetFusionRecipeAction(this, recipe), true);
                }
            }
        }
        if(multiblock instanceof OverhaulTurbine){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                multiblock.configuration.overhaul.turbine.Recipe recipe = Core.configuration.overhaul.turbine.allRecipes.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((OverhaulTurbine)multiblock).recipe!=recipe){
                    multiblock.action(new SetTurbineRecipeAction(this, recipe), true);
                }
            }
        }
        multibwauk.height = gui.helper.displayHeight()-multibwauk.y-generate.height;
        textBox.y = parts.y+parts.height;
        textBox.height = gui.helper.displayHeight()-textBox.y;
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
        if(multiblock instanceof UnderhaulSFR){
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
    }
    public Block getSelectedBlock(){
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
    public EditorTool getSelectedTool(){
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
    public multiblock.configuration.overhaul.fusion.BreedingBlanketRecipe getSelectedFusionBreedingBlanketRecipe(){
        return ((MenuComponentFusionBreedingBlanketRecipe) irradiatorRecipe.getSelectedComponent()).recipe;
    }
    public multiblock.configuration.overhaul.fissionsfr.Fuel getSelectedOverSFRFuel(){
        return ((MenuComponentOverSFRFuel) overFuel.getSelectedComponent()).fuel;
    }
    public multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe getSelectedSFRIrradiatorRecipe(){
        return ((MenuComponentSFRIrradiatorRecipe) irradiatorRecipe.getSelectedComponent()).recipe;
    }
    public multiblock.configuration.overhaul.fissionmsr.Fuel getSelectedOverMSRFuel(){
        return ((MenuComponentOverMSRFuel) overFuel.getSelectedComponent()).fuel;
    }
    public multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe getSelectedMSRIrradiatorRecipe(){
        return ((MenuComponentMSRIrradiatorRecipe) irradiatorRecipe.getSelectedComponent()).recipe;
    }
    public void setblock(int x, int y, int z, Block template){
        if(hasSelection()&&!isSelected(x, y, z))return;
        if(template==null){
            if(Core.isControlPressed()){
                if(multiblock.getBlock(x, y, z)!=null&&!multiblock.getBlock(x, y, z).matches(getSelectedBlock()))return;
            }
            multiblock.action(new SetblockAction(x,y,z,null), true);
            return;
        }
        if(Core.isControlPressed()){
            if(multiblock.getBlock(x, y, z)!=null&&!Core.isShiftPressed())return;
            if(multiblock.getBlock(x, y, z)!=null&&!multiblock.getBlock(x, y, z).canBeQuickReplaced())return;
            if(multiblock.getBlock(x, y, z)==null||multiblock.getBlock(x, y, z)!=null&&Core.isShiftPressed()){
                if(!isValid(template, x, y, z))return;
            }
        }
        Block blok = template.newInstance(x, y, z);
        if(multiblock instanceof OverhaulSFR){
            if(((multiblock.overhaul.fissionsfr.Block)blok).isFuelCell()){
                ((multiblock.overhaul.fissionsfr.Block)blok).fuel = getSelectedOverSFRFuel();
            }
            if(((multiblock.overhaul.fissionsfr.Block)blok).isIrradiator()){
                ((multiblock.overhaul.fissionsfr.Block)blok).irradiatorRecipe = getSelectedSFRIrradiatorRecipe();
            }
        }
        if(multiblock instanceof OverhaulMSR){
            if(((multiblock.overhaul.fissionmsr.Block)blok).isFuelVessel()){
                ((multiblock.overhaul.fissionmsr.Block)blok).fuel = getSelectedOverMSRFuel();
            }
            if(((multiblock.overhaul.fissionmsr.Block)blok).isIrradiator()){
                ((multiblock.overhaul.fissionmsr.Block)blok).irradiatorRecipe = getSelectedMSRIrradiatorRecipe();
            }
        }
        if(multiblock instanceof OverhaulFusionReactor){
            if(((multiblock.overhaul.fusion.Block)blok).isBreedingBlanket()){
                ((multiblock.overhaul.fusion.Block)blok).breedingBlanketRecipe = getSelectedFusionBreedingBlanketRecipe();
            }
        }
        multiblock.action(new SetblockAction(x,y,z,blok), true);
    }
    @Override
    public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
        super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
        if(isPress){
            if(key==GLFW.GLFW_KEY_ESCAPE){
                if(getSelectedTool() instanceof PasteTool||getSelectedTool() instanceof CopyTool||getSelectedTool() instanceof CutTool){
                    tools.setSelectedIndex(1);
                }else{
                    clearSelection();
                }
            }
            if(key==GLFW.GLFW_KEY_DELETE){
                SetblocksAction ac = new SetblocksAction(null);
                synchronized(selection){
                    for(int[] i : selection){
                        ac.add(i[0], i[1], i[2]);
                    }
                }
                multiblock.action(ac, true);
                clearSelection();
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
                    setSelection(sel);
                }
                if(key==GLFW.GLFW_KEY_Z){
                    multiblock.undo();
                }
                if(key==GLFW.GLFW_KEY_Y){
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
                            copySelection(x, y, z);
                        }
                        if(key==GLFW.GLFW_KEY_X){
                            cutSelection(x, y, z);
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
                        copySelection(-1, -1, -1);
                    }
                    if(key==GLFW.GLFW_KEY_X){
                        cutSelection(-1, -1, -1);
                    }
                }
            }
        }
    }
    public void setblocks(SetblocksAction set){
        for(Iterator<int[]> it = set.locations.iterator(); it.hasNext();){
            int[] b = it.next();
            if(hasSelection()&&!isSelected(b[0], b[1], b[2]))it.remove();
            else if(Core.isControlPressed()){
                if(set.block==null){
                    if(multiblock.getBlock(b[0], b[1], b[2])!=null&&!multiblock.getBlock(b[0], b[1], b[2]).matches(getSelectedBlock()))it.remove();
                }else{
                    if(multiblock.getBlock(b[0], b[1], b[2])!=null&&!Core.isShiftPressed()){
                        it.remove();
                    }else if(multiblock.getBlock(b[0], b[1], b[2])!=null&&!multiblock.getBlock(b[0], b[1], b[2]).canBeQuickReplaced()){
                        it.remove();
                    }else if(multiblock.getBlock(b[0], b[1], b[2])==null||multiblock.getBlock(b[0], b[1], b[2])!=null&&Core.isShiftPressed()){
                        if(!isValid(set.block, b[0], b[1], b[2]))it.remove();
                    }
                }
            }
        }
        if(set.block!=null&&multiblock instanceof OverhaulSFR){
            if(((multiblock.overhaul.fissionsfr.Block)set.block).isFuelCell()){
                ((multiblock.overhaul.fissionsfr.Block)set.block).fuel = getSelectedOverSFRFuel();
            }
            if(((multiblock.overhaul.fissionsfr.Block)set.block).isIrradiator()){
                ((multiblock.overhaul.fissionsfr.Block)set.block).irradiatorRecipe = getSelectedSFRIrradiatorRecipe();
            }
        }
        if(set.block!=null&&multiblock instanceof OverhaulMSR){
            if(((multiblock.overhaul.fissionmsr.Block)set.block).isFuelVessel()){
                ((multiblock.overhaul.fissionmsr.Block)set.block).fuel = getSelectedOverMSRFuel();
            }
            if(((multiblock.overhaul.fissionmsr.Block)set.block).isIrradiator()){
                ((multiblock.overhaul.fissionmsr.Block)set.block).irradiatorRecipe = getSelectedMSRIrradiatorRecipe();
            }
        }
        if(set.block!=null&&multiblock instanceof OverhaulFusionReactor){
            if(((multiblock.overhaul.fusion.Block)set.block).isBreedingBlanket()){
                ((multiblock.overhaul.fusion.Block)set.block).breedingBlanketRecipe = getSelectedFusionBreedingBlanketRecipe();
            }
        }
        multiblock.action(set, true);
    }
    public boolean isValid(Block selectedBlock, int x, int layer, int z){
        return multiblock.isValid(selectedBlock, x, layer, z);
    }
    public void select(int x1, int y1, int z1, int x2, int y2, int z2){
        ArrayList<int[]> is = new ArrayList<>();
        for(int x = Math.min(x1,x2); x<=Math.max(x1,x2); x++){
            for(int y = Math.min(y1,y2); y<=Math.max(y1,y2); y++){
                for(int z = Math.min(z1,z2); z<=Math.max(z1,z2); z++){
                    is.add(new int[]{x,y,z});
                }
            }
        }
        select(is);
    }
    public void deselect(int x1, int y1, int z1, int x2, int y2, int z2){
        ArrayList<int[]> is = new ArrayList<>();
        for(int x = Math.min(x1,x2); x<=Math.max(x1,x2); x++){
            for(int y = Math.min(y1,y2); y<=Math.max(y1,y2); y++){
                for(int z = Math.min(z1,z2); z<=Math.max(z1,z2); z++){
                    is.add(new int[]{x,y,z});
                }
            }
        }
        deselect(is);
    }
    public void select(ArrayList<int[]> is){
        if(Core.isControlPressed()){
            multiblock.action(new SelectAction(this, is), true);
        }else{
            multiblock.action(new SetSelectionAction(this, is), true);
        }
    }
    public void setSelection(ArrayList<int[]> is){
        multiblock.action(new SetSelectionAction(this, is), true);
    }
    public void deselect(ArrayList<int[]> is){
        if(!Core.isControlPressed()){
            clearSelection();
            return;
        }
        multiblock.action(new DeselectAction(this, is), true);
    }
    public boolean isSelected(int x, int y, int z){
        synchronized(selection){
            for(int[] s : selection){
                if(s==null)continue;//THIS SHOULD NEVER HAPPEN but it does anyway
                if(s[0]==x&&s[1]==y&&s[2]==z)return true;
            }
        }
        return false;
    }
    private boolean hasSelection(){
        synchronized(selection){
            return !selection.isEmpty();
        }
    }
    public void selectCluster(int x, int y, int z){
        if(multiblock instanceof OverhaulSFR){
            OverhaulSFR osfr = (OverhaulSFR) multiblock;
            OverhaulSFR.Cluster c = osfr.getCluster(osfr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            select(is);
        }
        if(multiblock instanceof OverhaulMSR){
            OverhaulMSR omsr = (OverhaulMSR) multiblock;
            OverhaulMSR.Cluster c = omsr.getCluster(omsr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            select(is);
        }
        if(multiblock instanceof OverhaulFusionReactor){
            OverhaulFusionReactor ofr = (OverhaulFusionReactor) multiblock;
            OverhaulFusionReactor.Cluster c = ofr.getCluster(ofr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            select(is);
        }
    }
    public void deselectCluster(int x, int y, int z){
        if(multiblock instanceof OverhaulSFR){
            OverhaulSFR osfr = (OverhaulSFR) multiblock;
            OverhaulSFR.Cluster c = osfr.getCluster(osfr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            deselect(is);
        }
        if(multiblock instanceof OverhaulMSR){
            OverhaulMSR omsr = (OverhaulMSR) multiblock;
            OverhaulMSR.Cluster c = omsr.getCluster(omsr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            deselect(is);
        }
        if(multiblock instanceof OverhaulFusionReactor){
            OverhaulFusionReactor ofr = (OverhaulFusionReactor) multiblock;
            OverhaulFusionReactor.Cluster c = ofr.getCluster(ofr.getBlock(x, y, z));
            if(c==null)return;
            ArrayList<int[]> is = new ArrayList<>();
            for(Block b : c.blocks){
                is.add(new int[]{b.x,b.y,b.z});
            }
            deselect(is);
        }
    }
    public void selectGroup(int x, int y, int z){
        ArrayList<Block> g = multiblock.getGroup(multiblock.getBlock(x, y, z));
        if(g==null){
            select(0, 0, 0, multiblock.getX()-1, multiblock.getY()-1, multiblock.getZ()-1);
            return;
        }
        ArrayList<int[]> is = new ArrayList<>();
        for(Block b : g){
            is.add(new int[]{b.x,b.y,b.z});
        }
        select(is);
    }
    public void deselectGroup(int x, int y, int z){
        ArrayList<Block> g = multiblock.getGroup(multiblock.getBlock(x, y, z));
        if(g==null){
            deselect(0, 0, 0, multiblock.getX()-1, multiblock.getY()-1, multiblock.getZ()-1);
            return;
        }
        ArrayList<int[]> is = new ArrayList<>();
        for(Block b : g){
            is.add(new int[]{b.x,b.y,b.z});
        }
        deselect(is);
    }
    public void moveSelection(int x, int y, int z){
        multiblock.action(new MoveAction(this, selection, x, y, z), true);
    }
    public void cloneSelection(int x, int y, int z){
        multiblock.action(new CopyAction(this, selection, x, y, z), true);
    }
    public void clearSelection(){
        multiblock.action(new ClearSelectionAction(this), true);
    }
    public void addSelection(ArrayList<int[]> sel){
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
    public void copySelection(int x, int y, int z){//like copySelection, but clipboardier
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
    public void cutSelection(int x, int y, int z){
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
        copySelection(x,y,z);
        SetblocksAction ac = new SetblocksAction(null);
        synchronized(selection){
            for(int[] i : selection){
                ac.add(i[0], i[1], i[2]);
            }
        }
        multiblock.action(ac, true);
        clearSelection();
    }
    public void pasteSelection(int x, int y, int z){
        synchronized(clipboard){
            multiblock.action(new PasteAction(clipboard, x, y, z), true);
        }
    }
    public static class ClipboardEntry{
        public int x;
        public int y;
        public int z;
        public final Block block;
        public ClipboardEntry(int[] xyz, Block b){
            this(xyz[0], xyz[1], xyz[2], b);
        }
        public ClipboardEntry(int x, int y, int z, Block b){
            this.x = x;
            this.y = y;
            this.z = z;
            this.block = b;
        }
    }
}
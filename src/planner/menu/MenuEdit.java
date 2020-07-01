package planner.menu;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.configuration.overhaul.fissionsfr.IrradiatorRecipe;
import planner.menu.component.MenuComponentCoolantRecipe;
import planner.menu.component.MenuComponentEditorListBlock;
import planner.menu.component.MenuComponentEditorTool;
import planner.menu.component.MenuComponentIrradiatorRecipe;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.MenuComponentMinimalistTextView;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.MenuComponentOverFuel;
import planner.menu.component.MenuComponentUnderFuel;
import planner.tool.EditorTool;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
import planner.multiblock.action.SetCoolantRecipeAction;
import planner.multiblock.action.SetFuelAction;
import planner.multiblock.action.SetblockAction;
import planner.multiblock.action.SetblocksAction;
import planner.multiblock.overhaul.fissionsfr.OverhaulSFR;
import planner.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.tool.LineTool;
import planner.tool.PencilTool;
import planner.tool.RectangleTool;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuEdit extends Menu{
    private final ArrayList<EditorTool> editorTools = new ArrayList<>();
    {
        editorTools.add(new PencilTool(this));
        editorTools.add(new LineTool(this));
        editorTools.add(new RectangleTool(this));
    }
    public final Multiblock multiblock;
    private final int partSize = 48;
    private final int partsWide = 7;
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final MenuComponentMulticolumnMinimaList parts = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, partSize, partSize, partSize/2));
    private final MenuComponentMinimalistScrollable multibwauk = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 32, 32));
    private final MenuComponentMinimalistButton zoomOut = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Zoom out", true, true));
    private final MenuComponentMinimalistButton zoomIn = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Zoom in", true, true));
    private final MenuComponentMinimalistButton resize = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Resize", true, true));
    public final MenuComponentMinimaList underFuelOrCoolantRecipe = new MenuComponentMinimaList(0, 0, 0, 0, 24);
    private final MenuComponentMinimaList overFuel = new MenuComponentMinimaList(0, 0, 0, 0, 24);
    private final MenuComponentMinimaList irradiatorRecipe = new MenuComponentMinimaList(0, 0, 0, 0, 24);
    private final MenuComponentMinimalistTextView textBox = add(new MenuComponentMinimalistTextView(0, 0, 0, 0, 24, 24));
    private final MenuComponentMinimalistButton editMetadata = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true));
    private final MenuComponentMinimaList tools = add(new MenuComponentMinimaList(0, 0, 0, 0, partSize/2));
    private double scale = 4;
    private double minScale = 0.5;
    private double maxScale = 16;
    private int CELL_SIZE = (int) (16*scale);
    private int LAYER_GAP = CELL_SIZE/2;
    private int multisPerRow = 0;
    public MenuEdit(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
        if(multiblock instanceof UnderhaulSFR){
            add(underFuelOrCoolantRecipe);
            for(planner.configuration.underhaul.fissionsfr.Fuel fuel : Core.configuration.underhaul.fissionSFR.fuels){
                underFuelOrCoolantRecipe.add(new MenuComponentUnderFuel(fuel));
            }
        }
        if(multiblock instanceof OverhaulSFR){
            add(underFuelOrCoolantRecipe);
            for(planner.configuration.overhaul.fissionsfr.CoolantRecipe recipe : Core.configuration.overhaul.fissionSFR.coolantRecipes){
                underFuelOrCoolantRecipe.add(new MenuComponentCoolantRecipe(recipe));
            }
            add(overFuel);
            for(planner.configuration.overhaul.fissionsfr.Fuel fuel : Core.configuration.overhaul.fissionSFR.fuels){
                overFuel.add(new MenuComponentOverFuel(fuel));
            }
            overFuel.setSelectedIndex(0);
            add(irradiatorRecipe);
            for(planner.configuration.overhaul.fissionsfr.IrradiatorRecipe recipe : Core.configuration.overhaul.fissionSFR.irradiatorRecipes){
                irradiatorRecipe.add(new MenuComponentIrradiatorRecipe(recipe));
            }
            irradiatorRecipe.setSelectedIndex(0);
        }
        this.multiblock = multiblock;
        multibwauk.setScrollMagnitude(CELL_SIZE/2);
        back.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(1, 0), 5));
        });
        resize.addActionListener((e) -> {
            gui.open(new MenuResize(gui, this, multiblock));
        });
        zoomOut.addActionListener((e) -> {
            scale = Math.max(minScale, Math.min(maxScale, scale/1.5));
            CELL_SIZE = (int) (16*scale);
            LAYER_GAP = CELL_SIZE/2;
            onGUIOpened();
        });
        zoomIn.addActionListener((e) -> {
            scale = Math.max(minScale, Math.min(maxScale, scale*1.5));
            CELL_SIZE = (int) (16*scale);
            LAYER_GAP = CELL_SIZE/2;
            onGUIOpened();
        });
        editMetadata.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, new MenuMultiblockMetadata(gui, this, multiblock), MenuTransition.SlideTransition.slideTo(0, 1), 4));
        });
        for(Block availableBlock : ((Multiblock<Block>)multiblock).getAvailableBlocks()){
            parts.add(new MenuComponentEditorListBlock(this, availableBlock));
        }
        parts.setSelectedIndex(0);
        for(EditorTool tool : editorTools){
            tools.add(new MenuComponentEditorTool(tool));
        }
        tools.setSelectedIndex(0);
    }
    @Override
    public void onGUIOpened(){
        editMetadata.label = multiblock.getName();
        if(multiblock instanceof UnderhaulSFR){
            underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.underhaul.fissionSFR.fuels.indexOf(((UnderhaulSFR)multiblock).fuel));
        }
        if(multiblock instanceof OverhaulSFR){
            underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.overhaul.fissionSFR.coolantRecipes.indexOf(((OverhaulSFR)multiblock).coolantRecipe));
        }
        multisPerRow = Math.max(1, (int)((multibwauk.width-multibwauk.horizScrollbarHeight)/(CELL_SIZE*multiblock.getX()+LAYER_GAP)));
        multibwauk.components.clear();
        for(int y = 0; y<multiblock.getY(); y++){
            int column = y%multisPerRow;
            int row = y/multisPerRow;
            int layerWidth = multiblock.getX()*CELL_SIZE+LAYER_GAP;
            int layerHeight = multiblock.getZ()*CELL_SIZE+LAYER_GAP;
            multibwauk.add(new planner.menu.component.MenuComponentEditorGrid(column*layerWidth+LAYER_GAP/2, row*layerHeight+LAYER_GAP/2, CELL_SIZE, this, multiblock, y));
        }
        recalculate();
    }
    @Override
    public void render(int millisSinceLastTick){
        setTooltip("");
        if(multisPerRow!=Math.max(1, (int)((multibwauk.width-multibwauk.horizScrollbarHeight)/(CELL_SIZE*multiblock.getX()+LAYER_GAP)))){
            onGUIOpened();
        }
        parts.width = partsWide*partSize+parts.vertScrollbarWidth*(parts.hasVertScrollbar()?1:0);
        tools.width = partSize;
        parts.x = tools.width+partSize/4;
        editMetadata.x = textBox.width = multibwauk.x = back.width = parts.x+parts.width;
        tools.y = multibwauk.y = parts.y = editMetadata.height = back.height = 48;
        tools.height = parts.height = (parts.components.size()+5)/partsWide*partSize;
        resize.width = 320;
        editMetadata.width = multibwauk.width = Display.getWidth()-parts.x-parts.width-resize.width;
        zoomIn.height = zoomOut.height = resize.height = back.height;
        zoomIn.width = zoomOut.width = resize.width/2;
        zoomIn.y = zoomOut.y = resize.height;
        resize.x = Display.getWidth()-resize.width;
        zoomIn.x = resize.x;
        zoomOut.x = zoomIn.x+zoomIn.width;
        irradiatorRecipe.x = overFuel.x = underFuelOrCoolantRecipe.x = resize.x;
        underFuelOrCoolantRecipe.y = resize.height*2;
        irradiatorRecipe.width = overFuel.width = underFuelOrCoolantRecipe.width = resize.width;
        underFuelOrCoolantRecipe.height = Display.getHeight()-resize.height*2;
        for(MenuComponent c : tools.components){
            c.width = c.height = partSize;
        }
        if(multiblock instanceof OverhaulSFR){
            underFuelOrCoolantRecipe.height = 96;
            irradiatorRecipe.height = 96;
            irradiatorRecipe.y = Display.getHeight()-irradiatorRecipe.height;
            overFuel.y = underFuelOrCoolantRecipe.y+underFuelOrCoolantRecipe.height;
            overFuel.height = irradiatorRecipe.y-overFuel.y;
        }
        for(MenuComponent c : underFuelOrCoolantRecipe.components){
            c.width = underFuelOrCoolantRecipe.width-underFuelOrCoolantRecipe.vertScrollbarWidth;
            c.height = 32;
        }
        for(MenuComponent c : irradiatorRecipe.components){
            c.width = irradiatorRecipe.width-irradiatorRecipe.vertScrollbarWidth;
            c.height = 32;
        }
        for(MenuComponent c : overFuel.components){
            c.width = overFuel.width-overFuel.vertScrollbarWidth;
            c.height = 32;
        }
        if(multiblock instanceof UnderhaulSFR){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                planner.configuration.underhaul.fissionsfr.Fuel fuel = Core.configuration.underhaul.fissionSFR.fuels.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((UnderhaulSFR)multiblock).fuel!=fuel){
                    multiblock.action(new SetFuelAction(this, fuel));
                }
            }
        }
        if(multiblock instanceof OverhaulSFR){
            if(underFuelOrCoolantRecipe.getSelectedIndex()>-1){
                planner.configuration.overhaul.fissionsfr.CoolantRecipe recipe = Core.configuration.overhaul.fissionSFR.coolantRecipes.get(underFuelOrCoolantRecipe.getSelectedIndex());
                if(((OverhaulSFR)multiblock).coolantRecipe!=recipe){
                    multiblock.action(new SetCoolantRecipeAction(this, recipe));
                }
            }
        }
        multibwauk.height = Display.getHeight()-multibwauk.y;
        textBox.y = parts.y+parts.height;
        textBox.height = Display.getHeight()-textBox.y;
        super.render(millisSinceLastTick);
    }
    public void setTooltip(String tooltip){
        if(!tooltip.isEmpty())tooltip+="\n\n";
        textBox.setText(tooltip+multiblock.getTooltip());
    }
    public Block getSelectedBlock(){
        if(parts.getSelectedIndex()==-1)return null;
        return ((MenuComponentEditorListBlock) parts.components.get(parts.getSelectedIndex())).block;
    }
    public EditorTool getSelectedTool(){
        if(tools.getSelectedIndex()==-1)return null;
        return ((MenuComponentEditorTool) tools.components.get(tools.getSelectedIndex())).tool;
    }
    public planner.configuration.overhaul.fissionsfr.Fuel getSelectedOverFuel(){
        return ((MenuComponentOverFuel) overFuel.components.get(overFuel.getSelectedIndex())).fuel;
    }
    public IrradiatorRecipe getSelectedIrradiatorRecipe(){
        return ((MenuComponentIrradiatorRecipe) irradiatorRecipe.components.get(irradiatorRecipe.getSelectedIndex())).recipe;
    }
    public void recalculate(){
        multiblock.recalculate();
    }
    public void setblock(int x, int y, int z, Block template){
        if(template==null){
            if(Core.isControlPressed()){
                if(multiblock.getBlock(x, y, z)!=null&&!multiblock.getBlock(x, y, z).matches(getSelectedBlock()))return;
            }
            multiblock.action(new SetblockAction(x,y,z,null));
            return;
        }
        if(Core.isControlPressed()){
            if(multiblock.getBlock(x, y, z)!=null||!isValid(template, x, y, z))return;
        }
        Block blok = template.newInstance(x, y, z);
        if(multiblock instanceof OverhaulSFR){
            if(((planner.multiblock.overhaul.fissionsfr.Block)blok).isFuelCell()){
                ((planner.multiblock.overhaul.fissionsfr.Block)blok).fuel = getSelectedOverFuel();
            }
            if(((planner.multiblock.overhaul.fissionsfr.Block)blok).isIrradiator()){
                ((planner.multiblock.overhaul.fissionsfr.Block)blok).recipe = getSelectedIrradiatorRecipe();
            }
        }
        multiblock.action(new SetblockAction(x,y,z,blok));
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        super.keyboardEvent(character, key, pressed, repeat);
        if(pressed&&(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)||Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))){
            if(key==Keyboard.KEY_Z){
                multiblock.undo();
            }
            if(key==Keyboard.KEY_Y){
                multiblock.redo();
            }
        }
    }
    public void setblocks(SetblocksAction set){
        for(Iterator<int[]> it = set.locations.iterator(); it.hasNext();){
            int[] b = it.next();
            if(Core.isControlPressed()){
                if(set.block==null){
                    if(multiblock.getBlock(b[0], b[1], b[2])!=null&&!multiblock.getBlock(b[0], b[1], b[2]).matches(getSelectedBlock()))it.remove();
                }else{
                    if(multiblock.getBlock(b[0], b[1], b[2])!=null||!isValid(set.block, b[0], b[1], b[2]))it.remove();
                }
            }
        }
        if(set.block!=null&&multiblock instanceof OverhaulSFR){
            if(((planner.multiblock.overhaul.fissionsfr.Block)set.block).isFuelCell()){
                ((planner.multiblock.overhaul.fissionsfr.Block)set.block).fuel = getSelectedOverFuel();
            }
            if(((planner.multiblock.overhaul.fissionsfr.Block)set.block).isIrradiator()){
                ((planner.multiblock.overhaul.fissionsfr.Block)set.block).recipe = getSelectedIrradiatorRecipe();
            }
        }
        multiblock.action(set);
    }
    public boolean isValid(Block selectedBlock, int x, int layer, int z){
        Block b = selectedBlock.newInstance(x, layer, z);
        return b.hasRules()&&b.calculateRules(multiblock);
    }
}
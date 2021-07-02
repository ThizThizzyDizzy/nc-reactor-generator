package planner.menu.component.editor;
import java.util.ArrayList;
import multiblock.FluidStack;
import multiblock.Multiblock;
import multiblock.overhaul.turbine.OverhaulTurbine;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.MenuMain;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMultiblock extends MenuComponent{
    private final MenuMain main;
    public final Multiblock multiblock;
    public final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glVertex2d(x+width*.25, y+height*.75);
            GL11.glVertex2d(x+width*.375, y+height*.75);
            GL11.glVertex2d(x+width*.25, y+height*.625);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2d(x+width*.4, y+height*.725);
            GL11.glVertex2d(x+width*.275, y+height*.6);
            GL11.glVertex2d(x+width*.5, y+height*.375);
            GL11.glVertex2d(x+width*.625, y+height*.5);

            GL11.glVertex2d(x+width*.525, y+height*.35);
            GL11.glVertex2d(x+width*.65, y+height*.475);
            GL11.glVertex2d(x+width*.75, y+height*.375);
            GL11.glVertex2d(x+width*.625, y+height*.25);
            GL11.glEnd();
        }
    }.setTooltip("Modify multiblock"));
    public MenuComponentMultiblock(MenuMain main, Multiblock multiblock){
        super(0, 0, 0, 100);
        this.main = main;
        this.multiblock = multiblock;
    }
    @Override
    public void renderBackground(){
        super.renderBackground();
        edit.x = width-height/2-height/4;
        edit.y = height/4;
        edit.width = edit.height = height/2;
    }
    @Override
    public void render(){
        ArrayList<FluidStack> outs = multiblock.getFluidOutputs();
        boolean has = false;
        if(main.settingInputs!=null){
            for(FluidStack s : outs)if(s.name.equals(main.settingInputs.recipe.inputName))has = true;
        }
        if(main.settingInputs!=null&&!has){
            isMouseOver = false;
        }
        if(isSelected){
            if(isMouseOver)Core.applyColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else Core.applyColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseOver)Core.applyColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else Core.applyColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        drawRect(x, y, x+width, y+height, 0);
        if(main.getSelectedMultiblock()!=null&&main.getSelectedMultiblock() instanceof OverhaulTurbine&&((OverhaulTurbine)main.getSelectedMultiblock()).inputs.contains(multiblock)){
            Core.applyColor(Core.theme.getMultiblockSelectedInputColor(), .25f);
            drawRect(x, y, x+width, y+height, 0);
        }
        if(main.settingInputs!=null&&!has){
            Core.applyColor(Core.theme.getMultiblockInvalidInputColor(), .25f);
            drawRect(x, y, x+width, y+height, 0);
        }
    }
    @Override
    public void renderForeground(){
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(x, y, x+width, y+height/4, multiblock.getName());
        drawText(x, y+height/4, x+width, y+height/2, multiblock.getDefinitionName());
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&pressed){
            if(main.settingInputs!=null){
                if(multiblock!=main.settingInputs){
                    ArrayList<FluidStack> outs = multiblock.getFluidOutputs();
                    boolean has = false;
                    for(FluidStack s : outs)if(s.name.equals(main.settingInputs.recipe.inputName))has = true;
                    if(has){
                        if(main.settingInputs.inputs.contains(multiblock)){
                            main.settingInputs.inputs.remove(multiblock);
                        }else{
                            main.settingInputs.inputs.add(multiblock);
                        }
                    }
                }
            }
        }
    }
}
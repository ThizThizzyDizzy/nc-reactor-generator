package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.FluidStack;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.MenuMain;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import static org.lwjgl.glfw.GLFW.*;
public class MenuComponentMultiblock extends Component{
    private final MenuMain main;
    public final Multiblock multiblock;
    public final Button edit = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("pencil", x, y, width, height);
        }
    }.setTooltip("Modify multiblock"));
    public MenuComponentMultiblock(MenuMain main, Multiblock multiblock){
        super(0, 0, 0, 100);
        this.main = main;
        this.multiblock = multiblock;
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        edit.x = width-height/2-height/4;
        edit.y = height/4;
        edit.width = edit.height = height/2;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        ArrayList<FluidStack> outs = multiblock.getFluidOutputs();
        boolean has = false;
        if(main.settingInputs!=null){
            for(FluidStack s : outs)if(s.name.equals(main.settingInputs.recipe.inputName))has = true;
        }
        if(main.settingInputs!=null&&!has){
            isMouseFocused = false;
        }
        if(isFocused){
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        renderer.fillRect(x, y, x+width, y+height);
        if(main.getSelectedMultiblock()!=null&&main.getSelectedMultiblock() instanceof OverhaulTurbine&&((OverhaulTurbine)main.getSelectedMultiblock()).inputs.contains(multiblock)){
            renderer.setColor(Core.theme.getMultiblockSelectedInputColor(), .25f);
            renderer.fillRect(x, y, x+width, y+height);
        }
        if(main.settingInputs!=null&&!has){
            renderer.setColor(Core.theme.getMultiblockInvalidInputColor(), .25f);
            renderer.fillRect(x, y, x+width, y+height);
        }
    }
    @Override
    public void drawForeground(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        renderer.drawText(x, y, x+width, y+height/4, multiblock.getName());
        renderer.drawText(x, y+height/4, x+width, y+height/2, multiblock.getDefinitionName());
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(button==GLFW_MOUSE_BUTTON_LEFT&&action==GLFW_PRESS){
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
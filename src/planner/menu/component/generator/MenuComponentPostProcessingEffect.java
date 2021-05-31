package planner.menu.component.generator;
import multiblock.ppe.PostProcessingEffect;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import planner.menu.component.MenuComponentToggleButton;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentPostProcessingEffect extends MenuComponentToggleButton {
    public final PostProcessingEffect postProcessingEffect;
    public MenuComponentPostProcessingEffect(PostProcessingEffect postProcessingEffect){
        super(0, 0, 0, 32, postProcessingEffect.name, postProcessingEffect.defaultEnabled());
        this.postProcessingEffect = postProcessingEffect;
    }
}
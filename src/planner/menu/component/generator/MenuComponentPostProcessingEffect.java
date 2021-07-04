package planner.menu.component.generator;
import multiblock.ppe.PostProcessingEffect;
import planner.menu.component.MenuComponentToggleButton;
public class MenuComponentPostProcessingEffect extends MenuComponentToggleButton {
    public final PostProcessingEffect postProcessingEffect;
    public MenuComponentPostProcessingEffect(PostProcessingEffect postProcessingEffect){
        super(0, 0, 0, 32, postProcessingEffect.name, postProcessingEffect.defaultEnabled());
        this.postProcessingEffect = postProcessingEffect;
    }
}
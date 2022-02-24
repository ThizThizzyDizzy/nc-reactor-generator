package net.ncplanner.plannerator.planner.gui.menu.component.generator;
import net.ncplanner.plannerator.multiblock.editor.ppe.PostProcessingEffect;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleButton;
public class MenuComponentPostProcessingEffect extends ToggleButton {
    public final PostProcessingEffect postProcessingEffect;
    public MenuComponentPostProcessingEffect(PostProcessingEffect postProcessingEffect){
        super(0, 0, 0, 32, postProcessingEffect.name, postProcessingEffect.defaultEnabled());
        this.postProcessingEffect = postProcessingEffect;
    }
}
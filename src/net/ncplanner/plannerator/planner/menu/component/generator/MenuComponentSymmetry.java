package net.ncplanner.plannerator.planner.menu.component.generator;
import net.ncplanner.plannerator.multiblock.editor.symmetry.Symmetry;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentToggleButton;
public class MenuComponentSymmetry extends MenuComponentToggleButton {
    public final Symmetry symmetry;
    public MenuComponentSymmetry(Symmetry symmetry){
        super(0, 0, 0, 32, symmetry.name, symmetry.defaultEnabled());
        this.symmetry = symmetry;
    }
}
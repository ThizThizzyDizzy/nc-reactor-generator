package planner.menu.component.generator;
import multiblock.symmetry.Symmetry;
import planner.menu.component.MenuComponentToggleButton;
public class MenuComponentSymmetry extends MenuComponentToggleButton {
    public final Symmetry symmetry;
    public MenuComponentSymmetry(Symmetry symmetry){
        super(0, 0, 0, 32, symmetry.name, symmetry.defaultEnabled());
        this.symmetry = symmetry;
    }
}
package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.GeneratorMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ExpandingGridLayout;
public class MenuPickGeneratorMutator<T extends LiteMultiblock> extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    public MenuPickGeneratorMutator(GUI gui, Menu parent, Mutator<T> mutator, Consumer<GeneratorMutator<T>> onConfirm){
        super(gui, parent);
        minWidth = minHeight = 0;
        for(Function<Mutator, GeneratorMutator> func : GeneratorMutator.mutators){
            GeneratorMutator<T> genMutator = func.apply(mutator);
            buttons.add(new Button(0, 0, 0, 0, genMutator.getTitle(), true).setTooltip(genMutator.getTooltip()).addAction(() -> {
                close();
                onConfirm.accept(genMutator);
            }));
        }
        setTitle("Choose Mutator Type");
        setContent(new ExpandingGridLayout(192, 64, 3).addAll(buttons));
        addButton("Cancel", () -> {
            close();
        });
    }
}
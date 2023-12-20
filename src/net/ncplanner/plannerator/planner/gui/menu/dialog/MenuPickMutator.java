package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.legacy.LegacyExpandingGridLayout;
public class MenuPickMutator<T extends LiteMultiblock> extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    public MenuPickMutator(GUI gui, Menu parent, T multiblock, Consumer<Mutator<T>> onConfirm){
        super(gui, parent);
        minWidth = minHeight = 0;
        for(Supplier<Mutator> supplier : Mutator.registeredMutators.values()){
            try{
                Mutator<T> mutator = supplier.get();
                mutator.init(multiblock);
                mutator.setIndicies(multiblock);
                buttons.add(new Button(mutator.getTitle(), true).setTooltip(mutator.getTooltip()).addAction(() -> {
                    close();
                    onConfirm.accept(mutator);
                }));
            }catch(ClassCastException ex){}
        }
        setTitle("Choose a mutator");
        setContent(new LegacyExpandingGridLayout(192, 64, 3).addAll(buttons));
        addButton("Cancel", () -> {
            close();
        });
    }
}
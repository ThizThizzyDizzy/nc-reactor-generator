package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.file.ncpf.NCPFFileWriter;
import net.ncplanner.plannerator.planner.file.ncpf.NCPFFormatWriter;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.legacy.LegacyExpandingGridLayout;
public class MenuPickNCPF extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    public MenuPickNCPF(GUI gui, Menu parent, Consumer<NCPFFormatWriter> onConfirm){
        super(gui, parent);
        minWidth = minHeight = 0;
        for(NCPFFormatWriter writer : NCPFFileWriter.formats){
            buttons.add(new Button(writer.getExtension().toUpperCase(Locale.ROOT), true).addAction(() -> {
                close();
                onConfirm.accept(writer);
                gui.menu.onOpened();
            }));
        }
        setTitle("Choose one");
        setContent(new LegacyExpandingGridLayout(192, 64, 3).addAll(buttons));
        addButton("Cancel", () -> {
            close();
        });
    }
}
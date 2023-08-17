package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.legacy.LegacyExpandingGridLayout;
public class MenuPickElementDefinition extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    public MenuPickElementDefinition(GUI gui, Menu parent, Consumer<NCPFElementDefinition> onConfirm){
        super(gui, parent);
        minWidth = minHeight = 0;
        for(String key : NCPFElement.recognizedElements.keySet()){
            Supplier<NCPFElementDefinition> supplier = NCPFElement.recognizedElements.get(key);
            NCPFElementDefinition def = supplier.get();
            if(def.typeMatches(NCPFModuleElement::new))continue;//no modules
            if(!def.matches(def))continue;//unknown element
            buttons.add(new Button(def.getTypeName(), true).addAction(() -> {
                close();
                onConfirm.accept(def);
            }));
        }
        setTitle("Choose Element Definition");
        setContent(new LegacyExpandingGridLayout(192, 64, 3).addAll(buttons));
        addButton("Cancel", () -> {
            close();
        });
    }
}
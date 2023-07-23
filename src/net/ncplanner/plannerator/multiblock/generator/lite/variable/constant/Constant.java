package net.ncplanner.plannerator.multiblock.generator.lite.variable.constant;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public interface Constant{
    public static final HashMap<String, Supplier<Constant>> registeredConstants = new HashMap<>();
    public void addSettings(SingleColumnList list, MenuGenerator menu);
    public String getType();
    public void convertToObject(NCPFObject ncpf);
    public void convertFromObject(NCPFObject ncpf);
}
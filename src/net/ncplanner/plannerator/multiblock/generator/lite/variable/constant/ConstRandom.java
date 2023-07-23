package net.ncplanner.plannerator.multiblock.generator.lite.variable.constant;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public class ConstRandom extends VariableFloat implements Constant{
    Random rand = new Random();
    public ConstRandom(){
        super("Random");
    }
    @Override
    public float getValue(){
        return rand.nextFloat();
    }
    public void addSettings(SingleColumnList list, MenuGenerator menu){}
    @Override
    public String getType(){
        return "random";
    }
    @Override
    public void convertToObject(NCPFObject ncpf){}
    @Override
    public void convertFromObject(NCPFObject ncpf){}
}
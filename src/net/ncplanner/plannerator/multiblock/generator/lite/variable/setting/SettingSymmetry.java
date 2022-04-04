package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.Symmetry;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public class SettingSymmetry implements Setting<Symmetry>{
    private Symmetry symmetry = new Symmetry();
    @Override
    public String getName(){
        return "Symmetry";
    }
    @Override
    public void set(Symmetry value){
        symmetry = value;
    }
    @Override
    public Symmetry get(){
        return symmetry;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        menu.addSettings(symmetry);
    }
}
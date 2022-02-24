package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingInt;
public class StageTransition<T extends LiteMultiblock>{
    public ArrayList<Condition> conditions = new ArrayList<>();
    public SettingInt targetStage = new SettingInt("Target Stage", 0);
    public long hits;
}
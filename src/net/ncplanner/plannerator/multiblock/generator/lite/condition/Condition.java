package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;
public abstract class Condition{
    public static final ArrayList<Supplier<Condition>> conditions = new ArrayList<>();
    static{
        conditions.add(ConditionEqual::new);
        conditions.add(ConditionNotEqual::new);
        conditions.add(ConditionLess::new);
        conditions.add(ConditionGreater::new);
        conditions.add(ConditionLessEqual::new);
        conditions.add(ConditionGreaterEqual::new);
        conditions.add(ConditionAnd::new);
        conditions.add(ConditionOr::new);
        conditions.add(ConditionNot::new);
    }
    public long hits = 0;
    public abstract boolean check(Random rand);
}
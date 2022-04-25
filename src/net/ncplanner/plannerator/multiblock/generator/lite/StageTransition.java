package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingBoolean;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingInt;
public class StageTransition<T extends LiteMultiblock> implements ThingWithSettings, ThingWithVariables{
    public Variable[] vars = new Variable[]{new VariableLong("Hits"){
        @Override
        public long getValue(){
            return hits;
        }
    }};
    public ArrayList<Condition> conditions = new ArrayList<>();
    public SettingInt targetStage = new SettingInt("Target Stage", 0);
    public SettingBoolean store = new SettingBoolean("Store Multiblock", false);
    public SettingBoolean consolidate = new SettingBoolean("Consolidate Stored Multiblocks", false);
    public SettingBoolean stop = new SettingBoolean("Stop Generation", false);
    private Setting[] settings = new Setting[]{targetStage, store, consolidate, stop};
    public long hits;
    @Override
    public int getVariableCount(){
        return vars.length;
    }
    @Override
    public Variable getVariable(int i){
        return vars[i];
    }
    @Override
    public int getSettingCount(){
        return settings.length;
    }
    @Override
    public Setting getSetting(int i){
        return settings[i];
    }
    void reset(){
        hits = 0;
        for(Condition condition : conditions){
            condition.reset();
        }
    }
}
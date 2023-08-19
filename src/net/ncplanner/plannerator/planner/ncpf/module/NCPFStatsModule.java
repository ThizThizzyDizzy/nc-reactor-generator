package net.ncplanner.plannerator.planner.ncpf.module;
public abstract class NCPFStatsModule extends NCPFSettingsModule implements ElementStatsModule{
    public NCPFStatsModule(String name){
        super(name);
    }
    @Override
    public abstract String getFriendlyName();
}
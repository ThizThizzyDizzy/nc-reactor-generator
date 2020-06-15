package planner.configuration.overhaul.fissionsfr;
public class IrradiatorRecipe{
    public String name;
    public float efficiency;
    public float heat;
    public IrradiatorRecipe(String name, float efficiency, float heat){
        this.name = name;
        this.efficiency = efficiency;
        this.heat = heat;
    }
}
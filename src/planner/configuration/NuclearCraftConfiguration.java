package planner.configuration;
import planner.configuration.overhaul.OverhaulConfiguration;
import planner.configuration.overhaul.fissionsfr.Block;
import planner.configuration.overhaul.fissionsfr.FissionSFRConfiguration;
import planner.configuration.overhaul.fissionsfr.Source;
public class NuclearCraftConfiguration extends Configuration{
    public NuclearCraftConfiguration(String version){
        super("NuclearCraft", version);
        //TODO underhaul
        overhaul = new OverhaulConfiguration();
        overhaul.fissionSFR = new FissionSFRConfiguration();
        overhaul.fissionSFR.sources.add(new Source("Ra-Be", .9f));
        overhaul.fissionSFR.sources.add(new Source("Po-Be", .95f));
        overhaul.fissionSFR.sources.add(new Source("Cf-252", 1f));
//        overhaul.fissionSFR.blocks.add(heatsink("Water", 55));
    }
}
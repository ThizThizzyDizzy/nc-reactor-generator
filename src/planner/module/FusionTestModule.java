package planner.module;
import java.util.ArrayList;
import multiblock.configuration.Configuration;
import static multiblock.configuration.Configuration.getInputStream;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import planner.file.FileReader;
public class FusionTestModule extends Module{
    public FusionTestModule(){
        super("fusion_test");
    }
    @Override
    public String getDisplayName(){
        return "Fusion Test";
    }
    @Override
    public String getDescription(){
        return "A testbed for future overhaul fusion reactors.";
    }
    @Override
    public void addMultiblockTypes(ArrayList multiblockTypes){
        multiblockTypes.add(new OverhaulFusionReactor());
    }
    @Override
    public void addConfigurations(){
        Configuration.configurations.add(FileReader.read(() -> {
            return getInputStream("configurations/fusion_test.ncpf");
        }).configuration.addAlternative("Fusion"));
    }
}
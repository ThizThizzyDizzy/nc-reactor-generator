package planner.module;
import java.util.ArrayList;
import multiblock.configuration.Configuration;
import static multiblock.configuration.Configuration.getInputStream;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.file.FileReader;
import planner.tutorial.Tutorial;
import planner.tutorial.TutorialFileReader;
public class UnderhaulModule extends Module{
    public UnderhaulModule(){
        super(true);
    }
    @Override
    public String getName(){
        return "Underhaul";
    }
    @Override
    public String getDescription(){
        return "All the base NuclearCraft multiblocks";
    }
    @Override
    public void addMultiblockTypes(ArrayList multiblockTypes){
        multiblockTypes.add(new UnderhaulSFR());
    }
    @Override
    public void addTutorials(){
        Tutorial.addTutorials("Underhaul",
                TutorialFileReader.read("tutorials/underhaul/sfr.ncpt"));
    }
    @Override
    public void addConfigurations(){
        Configuration.configurations.add(FileReader.read(() -> {
            return getInputStream("configurations/po3.ncpf");
        }).configuration.addAlternative("PO3"));
        Configuration.configurations.add(FileReader.read(() -> {
            return getInputStream("configurations/e2e.ncpf");
        }).configuration.addAlternative("E2E"));
    }
}
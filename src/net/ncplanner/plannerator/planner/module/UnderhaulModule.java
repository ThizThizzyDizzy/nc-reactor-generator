package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import static net.ncplanner.plannerator.multiblock.configuration.Configuration.getInputStream;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.tutorial.TutorialFileReader;
public class UnderhaulModule extends Module{
    public UnderhaulModule(){
        super("underhaul", true);
    }
    @Override
    public String getDisplayName(){
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
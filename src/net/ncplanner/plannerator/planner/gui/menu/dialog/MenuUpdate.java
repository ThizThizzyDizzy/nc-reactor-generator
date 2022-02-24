package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.IOException;
import java.net.URISyntaxException;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Main;
import net.ncplanner.plannerator.planner.Updater;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuUpdate extends MenuDialog{
    public MenuUpdate(GUI gui, Menu parent, Updater updater){
        super(gui, parent);
        textBox.setText("New version available!\n(Version "+updater.getLatestDownloadableVersion()+")\nWould you like to update now?");
        addButton("Yes", () -> {
            System.out.println("Updating...");
            try{
                Main.startJava(new String[0], new String[]{"justUpdated"}, updater.update(updater.getLatestDownloadableVersion()));
            }catch(URISyntaxException|IOException ex){
                close();
                Core.error("Failed to update!", ex);
                return;
            }
            System.exit(0);
        });
        addButton("No", () -> {
            close();
        });
    }
}
package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.IOException;
import java.net.URISyntaxException;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Main;
import com.thizthizzydizzy.dizzyengine.updater.DizzyUpdater;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuUpdate extends MenuDialog{
    public MenuUpdate(GUI gui, Menu parent, DizzyUpdater updater){
        super(gui, parent);
        textBox.setText("New version available!\n(Version "+updater.newVersionString+")\nWould you like to update now?");
        addButton("Yes", () -> {
            System.out.println("Updating...");
            try{
                if(!updater.update())throw new IOException("Unable to download update");
                Main.restartApplication(updater.newVersionFileName);
            }catch(IOException ex){
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

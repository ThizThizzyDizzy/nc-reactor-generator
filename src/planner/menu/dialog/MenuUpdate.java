package planner.menu.dialog;
import java.io.IOException;
import java.net.URISyntaxException;
import planner.Main;
import planner.Updater;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuUpdate extends MenuDialog{
    public MenuUpdate(GUI gui, Menu parent, Updater updater){
        super(gui, parent);
        textBox.setText("New version available!\n(Version "+updater.getLatestDownloadableVersion()+")\nWould you like to update now?");
        addButton("Yes", (e) -> {
            System.out.println("Updating...");
            try{
                Main.startJava(new String[0], new String[]{"justUpdated"}, updater.update(updater.getLatestDownloadableVersion()));
            }catch(URISyntaxException|IOException ex){
                close();
                Sys.error(ErrorLevel.severe, "Failed to update!", ex, ErrorCategory.other);
                return;
            }
            System.exit(0);
        });
        addButton("No", (e) -> {
            close();
        });
    }
}
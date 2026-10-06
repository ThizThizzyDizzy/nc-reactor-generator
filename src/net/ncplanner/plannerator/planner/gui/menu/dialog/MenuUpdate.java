package net.ncplanner.plannerator.planner.gui.menu.dialog;
import com.thizthizzydizzy.dizzyengine.DizzyEngine;
import java.io.IOException;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Main;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuUpdate extends MenuDialog{
    public MenuUpdate(GUI gui, Menu parent){
        throw new UnsupportedOperationException("Pending refactor");
//
//         super(gui, parent);
//         textBox.setText("New version available!\n(Version "+Core.updater.newVersionString+")\nWould you like to update now?");
//         addButton("Yes", () -> {
//             System.out.println("Updating...");
//             if(Core.updater.update()){
//                 try{
//                     DizzyEngine.stop();
//                     Main.restartApplication(Core.updater.newVersionFileName);
//                 }catch(IOException ex){
//                     close();
//                     Core.error("Failed to update!", ex);
//                 }
//             }
//         });
//         addButton("No", () -> {
//             close();
//         });
//
    }
}

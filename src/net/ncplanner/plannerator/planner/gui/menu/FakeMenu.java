package net.ncplanner.plannerator.planner.gui.menu;
import net.ncplanner.plannerator.planner.gui.Menu;
public class FakeMenu extends Menu{
    private final Runnable onOpened;
    public FakeMenu(Menu menu, Runnable onOpened){
        super(menu.gui, menu);
        this.onOpened = onOpened;
    }
    @Override
    public void onOpened(){
        try{
            onOpened.run();
        }catch(RuntimeException ex){
            gui.open(parent);
            throw ex;
        }
        gui.open(parent);
    }
}
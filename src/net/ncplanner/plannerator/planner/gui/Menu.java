package net.ncplanner.plannerator.planner.gui;
public class Menu extends Component{
    public Menu parent;
    public Menu(GUI gui, Menu parent){
        this.gui = gui;
        this.parent = parent;
    }
    public void render3d(double deltaTime){}
    public void onClosed(){}
    public void onOpened(){}
}
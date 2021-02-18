package planner.tutorial;
import java.util.ArrayList;
import simplelibrary.opengl.Renderer2D;
public abstract class Tutorial extends Renderer2D{
    public static final ArrayList<TutorialCategory> categories = new ArrayList<>();
    private static final TutorialCategory underhaul;
    private static final TutorialCategory overhaul;
    private static final TutorialCategory planner;
    public final String name;
    public boolean live = false;
    static{
        categories.add(planner = new TutorialCategory("Planner"));
        planner.add(TutorialFileReader.read("tutorials/planner/introduction.ncpt"));
        planner.add(TutorialFileReader.read("tutorials/planner/basics.ncpt"));
        planner.add(TutorialFileReader.read("tutorials/planner/editing.ncpt"));
        categories.add(underhaul = new TutorialCategory("Underhaul"));
        categories.add(overhaul = new TutorialCategory("Overhaul"));
        underhaul.add(TutorialFileReader.read("tutorials/underhaul/sfr.ncpt"));
        overhaul.add(TutorialFileReader.read("tutorials/overhaul/sfr.ncpt"));
        overhaul.add(TutorialFileReader.read("tutorials/overhaul/msr.ncpt"));
        overhaul.add(TutorialFileReader.read("tutorials/overhaul/turbine.ncpt"));
        //TODO tutorial on modifying configs and whatnot (first redo config system
        //TODO tutorial on using the generator
    }
    public Tutorial(String name){
        this.name = name;
    }
    public abstract double getHeight();
    public void tick(int tick){}
    /**
     * Called before the coordinates are scaled for render
     */
    public void preRender(){}
    public abstract void render(float resonatingBrightness, float frame);
}
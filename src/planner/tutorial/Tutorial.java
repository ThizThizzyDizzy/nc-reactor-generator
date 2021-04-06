package planner.tutorial;
import java.util.ArrayList;
import simplelibrary.opengl.Renderer2D;
public abstract class Tutorial extends Renderer2D{
    public static final ArrayList<TutorialCategory> categories = new ArrayList<>();
    public final String name;
    public boolean live = false;
    public static void init(){
        categories.clear();
        addTutorials("Planner",
                TutorialFileReader.read("tutorials/planner/introduction.ncpt"),
                TutorialFileReader.read("tutorials/planner/basics.ncpt"),
                TutorialFileReader.read("tutorials/planner/editing.ncpt"));
        //TODO tutorial on modifying configs and whatnot
        //TODO tutorial on using the generator
    }
    public static void addTutorials(String categoryName, Tutorial... tutorials){
        TutorialCategory category = new TutorialCategory(categoryName);
        for(Tutorial t : tutorials){
            category.add(t);
        }
        categories.add(category);
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
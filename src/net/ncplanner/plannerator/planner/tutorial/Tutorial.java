package net.ncplanner.plannerator.planner.tutorial;
import java.util.ArrayList;
public abstract class Tutorial{
    public static final ArrayList<TutorialCategory> categories = new ArrayList<>();
    public final String name;
    public boolean live = false;
    public static void init(){
        categories.clear();
        Tutorial.addTutorials("Planner",
                TutorialFileReader.read("tutorials/planner/introduction.ncpt"));
        //TODO tutorial on modifying configs and whatnot
        //TODO tutorial on using the generator
    }
    public static void addTutorials(String categoryName, Tutorial... tutorials){
        TutorialCategory cat = null;
        for(TutorialCategory c : categories){
            if(c.name.equals(categoryName))cat = c;
        }
        if(cat==null){
            cat = new TutorialCategory(categoryName);
            categories.add(cat);
        }
        for(Tutorial t : tutorials){
            cat.add(t);
        }
    }
    public Tutorial(String name){
        this.name = name;
    }
    public abstract float getHeight();
    public void tick(int tick){}
    /**
     * Called before the coordinates are scaled for render
     */
    public void preRender(){}
    public abstract void render(float resonatingBrightness, float frame);
}
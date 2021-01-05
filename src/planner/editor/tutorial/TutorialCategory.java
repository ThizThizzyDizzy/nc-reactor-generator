package planner.editor.tutorial;
import java.util.ArrayList;
public class TutorialCategory{
    public final String name;
    public final ArrayList<Tutorial> tutorials = new ArrayList<>();
    public TutorialCategory(String name){
        this.name = name;
    }
    public void add(Tutorial tutorial){
        tutorials.add(tutorial);
    }
}
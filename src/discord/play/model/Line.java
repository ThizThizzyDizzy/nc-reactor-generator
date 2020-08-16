package discord.play.model;
import java.util.ArrayList;
public class Line{
    public final ArrayList<Integer> verticies;
    public Line(ArrayList<Integer> verticies){
        this.verticies = verticies;
    }
    public Line(Line l){
        verticies = new ArrayList<>();
        verticies.addAll(l.verticies);
    }
}
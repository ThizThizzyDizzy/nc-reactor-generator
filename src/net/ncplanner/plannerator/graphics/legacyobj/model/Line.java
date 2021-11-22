package net.ncplanner.plannerator.graphics.legacyobj.model;
import java.util.ArrayList;
public class Line{
    public final ArrayList<Integer> verticies;
    public Line(ArrayList<Integer> verticies){
        this.verticies = new ArrayList<>(verticies);
    }
    public Line(Line l){
        this(l.verticies);
    }
}
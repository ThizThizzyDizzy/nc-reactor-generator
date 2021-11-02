package net.ncplanner.plannerator.planner.tutorial;
import java.io.InputStream;
public interface TutorialFormatReader{
    public boolean formatMatches(InputStream stream);
    public Tutorial read(InputStream stream);
}
package net.ncplanner.plannerator.planner.tutorial;
import net.ncplanner.plannerator.planner.file.InputStreamProvider;
public class UpdatingTutorial extends Tutorial{
    private Tutorial tutorial;
    private int updateTimer = 20;
    private InputStreamProvider path;
    public UpdatingTutorial(InputStreamProvider path){
        super(getName(path));
        this.path = path;
        tutorial = TutorialFileReader.read(path);
    }
    private static String getName(InputStreamProvider path){
        return TutorialFileReader.read(path).name;
    }
    @Override
    public double getHeight(){
        return tutorial.getHeight();
    }
    @Override
    public void render(float resonatingBrightness, float frame){
        tutorial.render(resonatingBrightness, frame);
    }
    @Override
    public void tick(int tick){
        tutorial.tick(tick);
        if(tutorial.live){
            updateTimer--;
            if(updateTimer<=0){
                updateTimer+=20;
                tutorial = TutorialFileReader.read(path);
            }
        }
    }
    @Override
    public void preRender(){
        tutorial.preRender();
    }
}

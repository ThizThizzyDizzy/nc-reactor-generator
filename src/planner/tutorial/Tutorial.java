package planner.tutorial;
import java.util.ArrayList;
import planner.Core;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public abstract class Tutorial extends Renderer2D{
    public static ArrayList<TutorialCategory> categories = new ArrayList<>();
    private static final TutorialCategory underhaul;
    private static final TutorialCategory overhaul;
    private static final TutorialCategory planner;
    public final String name;
    static{
        categories.add(planner = new TutorialCategory("Planner"));
        categories.add(underhaul = new TutorialCategory("Underhaul"));
        categories.add(overhaul = new TutorialCategory("Overhaul"));
        planner.add(new Tutorial("Introduction"){
            @Override
            public double getHeight(double width){
                return width*1.5;
            }
            @Override
            public void render(){
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(.05, .1, .95, .15, "Welcome to the tutorials!");
                drawCenteredText(.05, .15, .95, .175, "Select a category and tutorial on the left to get started");
                drawCenteredText(0, .25, .5, .3, "Underhaul");
                drawCenteredText(.5, .25, 1, .3, "Overhaul");
                Core.applyWhite();
                drawRect(.1, .35, .45, .7, ImageStash.instance.getTexture("/textures/tutorials/planner/introduction/underhaul_example.png"));
                drawRect(.55, .35, .9, .7, ImageStash.instance.getTexture("/textures/tutorials/planner/introduction/overhaul_example.png"));
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(.1,.7,.45,.72, "Example SFR");
                drawCenteredText(.55,.7,.9,.72, "Example SFR");
                drawCenteredText(0, 1, .5, 1.025, "If you're playing underhaul, this is called a ");
                drawCenteredText(0, 1.025, .5, 1.05, "Water Cooler");
                drawCenteredText(.5, 1, 1, 1.025, "If you're playing overhaul, this is called a ");
                drawCenteredText(.5, 1.025, 1, 1.05, "Water Fission Heat Sink");
                Core.applyWhite();
                drawRect(.2, 1.1, .3, 1.2, ImageStash.instance.getTexture("/textures/underhaul/water.png"));
                drawRect(.7, 1.1, .8, 1.2, ImageStash.instance.getTexture("/textures/overhaul/water.png"));
            }
        });
        planner.add(new Tutorial("Basics"){
            @Override
            public double getHeight(double width){
                return width;
            }
            @Override
            public void render(){
            }
        });
        planner.add(new Tutorial("Advanced Editing"){
            @Override
            public double getHeight(double width){
                return width;
            }
            @Override
            public void render(){
            }
        });
        planner.add(new Tutorial("Configurations"){
            @Override
            public double getHeight(double width){
                return width;
            }
            @Override
            public void render(){
            }
        });
        planner.add(new Tutorial("Generators"){
            @Override
            public double getHeight(double width){
                return width;
            }
            @Override
            public void render(){
            }
        });
        underhaul.add(new Tutorial("SFRs"){
            @Override
            public double getHeight(double width){
                return width;
            }
            @Override
            public void render(){
            }
        });
        overhaul.add(new Tutorial("Fission"){
            @Override
            public double getHeight(double width){
                return width;
            }
            @Override
            public void render(){
            }
        });
        overhaul.add(new Tutorial("SFRs"){
            @Override
            public double getHeight(double width){
                return width;
            }
            @Override
            public void render(){
            }
        });
        overhaul.add(new Tutorial("MSRs"){
            @Override
            public double getHeight(double width){
                return width;
            }
            @Override
            public void render(){
            }
        });
        overhaul.add(new Tutorial("Turbines"){
            @Override
            public double getHeight(double width){
                return width;
            }
            @Override
            public void render(){
            }
        });
    }
    public Tutorial(String name){
        this.name = name;
    }
    public abstract double getHeight(double width);
    public abstract void render();
}
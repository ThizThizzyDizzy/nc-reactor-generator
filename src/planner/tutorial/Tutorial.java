package planner.tutorial;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import multiblock.action.SetSelectionAction;
import multiblock.action.SetblocksAction;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.MenuEdit;
import planner.menu.component.editor.MenuComponentEditorGrid;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public abstract class Tutorial extends Renderer2D{
    public static ArrayList<TutorialCategory> categories = new ArrayList<>();
//    private static final TutorialCategory underhaul;
//    private static final TutorialCategory overhaul;
    private static final TutorialCategory planner;
    public final String name;
    public boolean live = false;
    static{
        categories.add(planner = new TutorialCategory("Planner"));
        planner.add(TutorialFileReader.read("tutorials/planner/introduction.ncpt"));
        planner.add(TutorialFileReader.read("tutorials/planner/basics.ncpt"));
        planner.add(TutorialFileReader.read("tutorials/planner/editing.ncpt"));
//        categories.add(underhaul = new TutorialCategory("Underhaul"));
//        categories.add(overhaul = new TutorialCategory("Overhaul"));
        //TODO tutorial on modifying configs and whatnot (first redo config system
        //TODO tutorial on using the generator
//        underhaul.add(new Tutorial("SFRs"){
//            @Override
//            public double getHeight(){
//                return .1;
//            }
//            @Override
//            public void render(float resonatingBrightness, float frame){
//            }
//        });
//        overhaul.add(new Tutorial("Fission"){
//            @Override
//            public double getHeight(double width){
//                return width*.1;
//            }
//            @Override
//            public void render(float resonatingBrightness, float frame){
//            }
//        });
//        overhaul.add(new Tutorial("SFRs"){
//            @Override
//            public double getHeight(double width){
//                return width*.1;
//            }
//            @Override
//            public void render(float resonatingBrightness, float frame){
//            }
//        });
//        overhaul.add(new Tutorial("MSRs"){
//            @Override
//            public double getHeight(double width){
//                return width*.1;
//            }
//            @Override
//            public void render(float resonatingBrightness, float frame){
//            }
//        });
//        overhaul.add(new Tutorial("Turbines"){
//            @Override
//            public double getHeight(double width){
//                return width*.1;
//            }
//            @Override
//            public void render(float resonatingBrightness, float frame){
//            }
//        });
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
package planner.editor.tutorial;
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
    static{
        categories.add(planner = new TutorialCategory("Planner"));
//        categories.add(underhaul = new TutorialCategory("Underhaul"));
//        categories.add(overhaul = new TutorialCategory("Overhaul"));
        planner.add(new Tutorial("Introduction"){
            @Override
            public double getHeight(double width){
                return width*1.1;
            }
            @Override
            public void render(float resonatingBrightness, float frame){
                Core.applyColor(Core.theme.getTextColor());
                GL11.glTranslated(0, -.05, 0);//too lazy to change the values of every method here
                drawCenteredText(.05, .1, .95, .15, "Welcome to the tutorials!");
                drawCenteredText(.05, .15, .95, .175, "Select a category and tutorial on the left to get started");
                drawCenteredText(.05, .175, .95, .2, "Or press Done to exit the tutorial");
                drawCenteredText(0, .25, .5, .3, "Underhaul");
                drawCenteredText(.5, .25, 1, .3, "Overhaul");
                Core.applyWhite();
                drawRect(.1, .35, .45, .7, ImageStash.instance.getTexture("/textures/tutorials/planner/introduction/underhaul_example.png"));
                drawRect(.55, .35, .9, .7, ImageStash.instance.getTexture("/textures/tutorials/planner/introduction/overhaul_example.png"));
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(.1,.7,.45,.72, "Example SFR");
                drawCenteredText(.55,.7,.9,.72, "Example SFR");
                drawCenteredText(0, .75, 1, .775, "If you're playing a modpack, you most likely are playing Underhaul");
                drawCenteredText(0, .775, 1, .8, "Such modpacks include (but are not limited to):");
                drawCenteredText(0, .8, 1, .825, "Enigmatica 2");
                drawCenteredText(0, .825, 1, .85, "Project Ozone 3");
                drawCenteredText(0, .85, 1, .875, "Sky Factory 4");
                drawCenteredText(0, .9, .5, .925, "If you're playing underhaul, this is called a");
                drawCenteredText(0, .925, .5, .95, "Water Cooler");
                drawCenteredText(.5, .9, 1, .925, "If you're playing overhaul, this is called a");
                drawCenteredText(.5, .925, 1, .95, "Water Fission Heat Sink");
                Core.applyWhite();
                drawRect(.2, 1, .3, 1.1, ImageStash.instance.getTexture("/textures/underhaul/water.png"));
                drawRect(.7, 1, .8, 1.1, ImageStash.instance.getTexture("/textures/overhaul/water.png"));
            }
        });
        planner.add(new Tutorial("Basics"){
            @Override
            public double getHeight(double width){
                return width*3.175;
            }
            @Override
            public void render(float resonatingBrightness, float frame){
                //loading E2E/PO3 configuration
                //<editor-fold defaultstate="collapsed" desc="Loading alternate configurations">
                Core.applyColor(Core.theme.getButtonColor());
                drawRect(.9, .15, .95, .2, 0);
                Core.applyColor(Core.theme.getButtonColor().brighter(), resonatingBrightness);
                drawRect(.9, .15, .95, .2, 0);
                Core.applyColor(Core.theme.getTextColor());
                {
                    ImageStash.instance.bindTexture(0);
                    double siz = .05;
                    double x = .9;
                    double y = .15;
                    double holeRad = siz*.1;
                    int teeth = 8;
                    double averageRadius = siz*.3;
                    double toothSize = siz*.1;
                    double rot = 360/16d;
                    int resolution = (int)(2*Math.PI*averageRadius*Core.helper.displayWidth()/3*2/teeth);//an extra *2 to account for wavy surface?
                    GL11.glBegin(GL11.GL_QUADS);
                    double angle = rot;
                    double radius = averageRadius+toothSize/2;
                    for(int i = 0; i<teeth*resolution; i++){
                        double inX = x+siz/2+Math.cos(Math.toRadians(angle-90))*holeRad;
                        double inY = y+siz/2+Math.sin(Math.toRadians(angle-90))*holeRad;
                        GL11.glVertex2d(inX, inY);
                        double outX = x+siz/2+Math.cos(Math.toRadians(angle-90))*radius;
                        double outY = y+siz/2+Math.sin(Math.toRadians(angle-90))*radius;
                        GL11.glVertex2d(outX,outY);
                        angle+=(360d/(teeth*resolution));
                        if(angle>=360)angle-=360;
                        radius = averageRadius+(toothSize/2)*Math.cos(Math.toRadians(teeth*(angle-rot)));
                        outX = x+siz/2+Math.cos(Math.toRadians(angle-90))*radius;
                        outY = y+siz/2+Math.sin(Math.toRadians(angle-90))*radius;
                        GL11.glVertex2d(outX,outY);
                        inX = x+siz/2+Math.cos(Math.toRadians(angle-90))*holeRad;
                        inY = y+siz/2+Math.sin(Math.toRadians(angle-90))*holeRad;
                        GL11.glVertex2d(inX, inY);
                    }
                    GL11.glEnd();
                }
                drawCenteredText(0, .05, 1, .1, "Loading alternate configurations");
                drawText(.05, .15, .95, .175, "If you are playing a modpack such as E2E or PO3, you will need to load the");
                drawText(.05, .175, .95, .2, "modpack configuration. Some modpacks' configurations are included and can");
                drawText(.05, .2, .95, .225, "be accessedthrough the settings menu. Alternatively, you can load your pack's");
                drawText(.05, .225, .95, .25, "nuclearcraft.cfg with the Load Configuration button");
                GL11.glTranslated(0, .25, 0);
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Adding, Editing, and Removing Multiblocks">
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "Adding, Editing, and Removing Multiblocks");
                //adding
                Core.applyColor(Core.theme.getDarkButtonColor());
                drawRect(.05, .15, .4, .2, 0);
                Core.applyColor(Core.theme.getButtonColor());
                drawRect(.4, .15, .45, .2, 0);
                Core.applyColor(Core.theme.getButtonColor().brighter(), resonatingBrightness);//TODO .brighter()
                drawRect(.4, .15, .45, .2, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(.05, .15, .4, .2, "Multiblocks");
                drawCenteredText(.4, .15, .45, .2, "+");
                drawText(.5, .15, 1, .175, "To add a multiblock, press the + button next");
                drawText(.5, .175, 1, .2, "to the multiblocks list, then select a multiblock");
                //editing
                Core.applyColor(Core.theme.getDarkButtonColor());
                drawRect(.55, .25, .9, .3, 0);
                Core.applyColor(Core.theme.getButtonColor());
                drawRect(.9, .25, .95, .3, 0);
                drawRect(.55, .3, .95, .4, 0);
                Core.applyColor(Core.theme.getDarkButtonColor());
                drawRect(.875, .325, .925, .375, 0);
                Core.applyColor(Core.theme.getDarkButtonColor().brighter(), resonatingBrightness);
                drawRect(.875, .325, .925, .375, 0);
                Core.applyColor(Core.theme.getTextColor());
                //<editor-fold defaultstate="collapsed" desc="Pencil icon">
                GL11.glBegin(GL11.GL_TRIANGLES);
                GL11.glVertex2d(.875+.05*.25, .325+.05*.75);
                GL11.glVertex2d(.875+.05*.375, .325+.05*.75);
                GL11.glVertex2d(.875+.05*.25, .325+.05*.625);
                GL11.glEnd();
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2d(.875+.05*.4, .325+.05*.725);
                GL11.glVertex2d(.875+.05*.275, .325+.05*.6);
                GL11.glVertex2d(.875+.05*.5, .325+.05*.375);
                GL11.glVertex2d(.875+.05*.625, .325+.05*.5);
                
                GL11.glVertex2d(.875+.05*.525, .325+.05*.35);
                GL11.glVertex2d(.875+.05*.65, .325+.05*.475);
                GL11.glVertex2d(.875+.05*.75, .325+.05*.375);
                GL11.glVertex2d(.875+.05*.625, .325+.05*.25);
                GL11.glEnd();
//</editor-fold>
                drawCenteredText(.55, .25, .9, .3, "Multiblocks");
                drawCenteredText(.9, .25, .95, .3, "+");
                drawText(.55, .325, .95, .35, "Overhaul SFR");
                drawText(.05, .3, .55, .325,"To edit a multiblock, press the edit button");
                drawText(.05, .325, .55, .35, "highlighted here.");
                drawText(.05, .35, .55, .375, "For more information, see the editing tutorial");
                //removing
                Core.applyColor(Core.theme.getDarkButtonColor());
                drawRect(.05, .45, .4, .5, 0);
                Core.applyColor(Core.theme.getButtonColor());
                drawRect(.4, .45, .45, .5, 0);
                drawRect(.05, .5, .45, .6, 0);
                drawRect(.5, .55, .95, .6, 0);
                Core.applyColor(Core.theme.getButtonColor().brighter(), resonatingBrightness);
                drawRect(.5, .55, .95, .6, 0);
                Core.applyColor(Core.theme.getSelectedMultiblockColor(), resonatingBrightness);
                drawRect(.05, .5, .45, .6, 0);
                Core.applyColor(Core.theme.getDarkButtonColor());
                drawRect(.375, .525, .425, .575, 0);
                Core.applyColor(Core.theme.getTextColor());
                //<editor-fold defaultstate="collapsed" desc="Pencil icon">
                GL11.glBegin(GL11.GL_TRIANGLES);
                GL11.glVertex2d(.375+.05*.25, .525+.05*.75);
                GL11.glVertex2d(.375+.05*.375, .525+.05*.75);
                GL11.glVertex2d(.375+.05*.25, .525+.05*.625);
                GL11.glEnd();
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2d(.375+.05*.4, .525+.05*.725);
                GL11.glVertex2d(.375+.05*.275, .525+.05*.6);
                GL11.glVertex2d(.375+.05*.5, .525+.05*.375);
                GL11.glVertex2d(.375+.05*.625, .525+.05*.5);

                GL11.glVertex2d(.375+.05*.525, .525+.05*.35);
                GL11.glVertex2d(.375+.05*.65, .525+.05*.475);
                GL11.glVertex2d(.375+.05*.75, .525+.05*.375);
                GL11.glVertex2d(.375+.05*.625, .525+.05*.25);
                GL11.glEnd();
                //</editor-fold>
                drawCenteredText(.05, .45, .4, .5, "Multiblocks");
                drawCenteredText(.4, .45, .45, .5, "+");
                drawText(.55, .325, .95, .35, "Overhaul SFR");
                drawText(.5, .45, 1, .475, "To delete a multiblock, select it and");
                drawText(.5, .475, 1, .5, "shift-click the delete button");
                Core.applyColor(Core.theme.getRed());
                drawCenteredText(.5, .55, .95, .6, "Delete (Hold Shift)");
                GL11.glTranslated(0, .6, 0);
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Resizing Multiblocks">
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "Resizing Multiblocks");
                Core.applyColor(Core.theme.getButtonColor());
                drawRect(.55, .15, .95, .2, 0);
                Core.applyColor(Core.theme.getButtonColor().brighter(), resonatingBrightness);
                drawRect(.55, .15, .95, .2, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawText(.05, .15, .5, .175, "To resize a multiblock, first click the");
                drawText(.05, .175, .5, .2, "resize button");
                drawCenteredText(.55, .15, .95, .2, "Resize");
                Core.applyColor(Core.theme.getButtonColor());
                //<editor-fold defaultstate="collapsed" desc="The resize graphics">
                double s = .4/7;
                drawRect(.05, .25, .45, .25+s, 0);//top
                drawRect(.05, .25+s*1.5, .05+s*5, .25+s*3.5, 0);//top inner
                drawRect(.05, .25+s*3.5, .05+s*2, .25+s*6.5, 0);//left
                drawRect(.05+s*5, .25+s*3.5, .05+s*6, .25+s*6.5, 0);//right
                drawRect(.05+s*2, .25+s*6.5, .05+s*5, .25+s*7.5, 0);//bottom inner
                drawRect(.05, .25+s*8, .45, .25+s*9, 0);//bottom
                Core.applyColor(Core.theme.getButtonColor().brighter(), resonatingBrightness);
                drawRect(.05, .25, .45, .25+s, 0);//top
                drawRect(.05, .25+s*8, .45, .25+s*9, 0);//bottom
                Core.applyColor(Core.theme.getGreen());
                drawCenteredText(.05, .25, .45, .25+s, "+");//top
                drawCenteredText(.05+s*2, .25+s*1.5, .05+s*5, .25+s*2.5, "+");//top inner
                drawCenteredText(.05, .25+s*4.5, .05+s, .25+s*5.5, "+");//left
                drawCenteredText(.05+s*5, .25+s*4.5, .05+s*6, .25+s*5.5, "+");//right
                drawCenteredText(.05+s*2, .25+s*6.5, .05+s*5, .25+s*7.5, "+");//bottom inner
                drawCenteredText(.05, .25+s*8, .45, .25+s*9, "+");//bottom
                Core.applyColor(Core.theme.getRed());
                drawCenteredText(.05, .25+s*1.5, .05+s*2, .25+s*3.5, "-");//top inner
                drawCenteredText(.05+s*2, .25+s*2.5, .05+s*3, .25+s*3.5, "-");//top 1
                drawCenteredText(.05+s*3, .25+s*2.5, .05+s*4, .25+s*3.5, "-");//top 2
                drawCenteredText(.05+s*4, .25+s*2.5, .05+s*5, .25+s*3.5, "-");//top 3
                drawCenteredText(.05+s, .25+s*3.5, .05+s*2, .25+s*4.5, "-");//left 1
                drawCenteredText(.05+s, .25+s*4.5, .05+s*2, .25+s*5.5, "-");//left 2
                drawCenteredText(.05+s, .25+s*5.5, .05+s*2, .25+s*6.5, "-");//left 3
                Core.applyColor(Core.theme.getEditorListBorderColor());
                drawRect(.05+s*2, .25+s*3.5, .05+s*5, .25+s*6.5, 0);
                Core.applyColor(Core.theme.getTextColor());
                double border = s/32;
                for(int x = 0; x<3; x++){
                    for(int y = 0; y<3; y++){
                        double X = .05+s*(2+x);
                        double Y = .25+s*(3.5+y);
                        drawRect(X,Y,X+s,Y+border,0);
                        drawRect(X,Y+s-border,X+s,Y+s,0);
                        drawRect(X,Y+border,X+border,Y+s-border,0);
                        drawRect(X+s-border,Y+border,X+s,Y+s-border,0);
                    }
                }
//</editor-fold>
                drawText(.5, .3, .95, .325, "Buttons with a  + will incease the size,");
                drawText(.5, .325, .95, .35, "while buttons with a - will decrease the");
                drawText(.5, .35, .95, .375, "size.");
                drawText(.5, .4, .95, .425, "The highlighted buttons will add a layer");
                drawText(.5, .425, .95, .45, "above or below this layer.");
                drawText(.5, .475, .95, .5, "Hovering over any other button will show");
                drawText(.5, .5, .95, .525, "what blocks will be added or removed.");
                drawText(.5, .55, .95, .575, "The size of the multiblock is shown");
                drawText(.5, .575, .95, .6, "on the right.");
                GL11.glTranslated(0, .775, 0);
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Metadata">
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "Metadata");
                drawText(.05, .15, .95, .175, "Metadata refers to any information stored about multiblocks, such as name and");
                drawText(.05, .175, .95, .2, "author. It is recommended to set the name and author for each multiblock.");
                drawText(.05, .225, .95, .25, "To manage metadata for each multiblock, press the center button at the top of");
                drawText(.05, .25, .95, .275, "the edit menu, shown below.");
                Core.applyColor(Core.theme.getButtonColor());
                drawRect(.05, .3, .95, .35, 0);
                Core.applyColor(Core.theme.getButtonColor().brighter(), resonatingBrightness);
                drawRect(.3, .3, .7, .35, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(.05, .31, .3, .34, "Done");
                drawCenteredText(.3, .31, .7, .34, "Tutorial Build v1 | Edit Metadata");
                drawCenteredText(.7, .31, .95, .34, "Resize");
                Core.applyColor(Core.theme.getMetadataPanelHeaderColor());
                drawRect(.05, .4, .95, .45, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(.05, .4, .95, .45, "Metadata");
                String[][] texts = {{"Name", "Tutorial Build v1"}, {"Author", "tomdodd4598"}};
                for(int x = 0; x<2; x++){
                    for(int y = 0; y<2; y++){
                        double inset = .005;
                        Core.applyColor(Core.theme.getListColor().darker());
                        drawRect(.05+.45*x, .45+.05*y, .5+.45*x, .5+.05*y, 0);
                        Core.applyColor(Core.theme.getListColor());
                        drawRect(.05+.45*x+inset/2, .45+.05*y+inset/2, .5+.45*x-inset/2, .5+.05*y-inset/2, 0);
                        Core.applyColor(Core.theme.getTextColor());
                        drawText(.05+.45*x+inset, .45+.05*y+inset, .5+.45*x-inset, .5+.05*y-inset, texts[y][x]);
                    }
                }
                
                drawText(.05, .575, .95, .6, "To manage the metadata for the group of multiblocks, press the center button at");
                drawText(.05, .6, .95, .625, "the top of the main menu, shown below.");
                Core.applyColor(Core.theme.getButtonColor());
                drawRect(.05, .65, .95, .7, 0);
                Core.applyColor(Core.theme.getButtonColor().brighter(), resonatingBrightness);
                drawRect(.33, .65, .9, .7, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(.05, .665, .12, .685, "Import");
                drawCenteredText(.12, .665, .19, .685, "Export");
                drawCenteredText(.19, .665, .26, .685, "Save");
                drawCenteredText(.26, .665, .33, .685, "Load");
                drawCenteredText(.3, .66, .85, .69, "Tutorial Collection | Edit Metadata");
                {
                    ImageStash.instance.bindTexture(0);
                    double siz = .05;
                    double x = .9;
                    double y = .65;
                    double holeRad = siz*.1;
                    int teeth = 8;
                    double averageRadius = siz*.3;
                    double toothSize = siz*.1;
                    double rot = 360/16d;
                    int resolution = (int)(2*Math.PI*averageRadius*Core.helper.displayWidth()/3*2/teeth);//an extra *2 to account for wavy surface?
                    GL11.glBegin(GL11.GL_QUADS);
                    double angle = rot;
                    double radius = averageRadius+toothSize/2;
                    for(int i = 0; i<teeth*resolution; i++){
                        double inX = x+siz/2+Math.cos(Math.toRadians(angle-90))*holeRad;
                        double inY = y+siz/2+Math.sin(Math.toRadians(angle-90))*holeRad;
                        GL11.glVertex2d(inX, inY);
                        double outX = x+siz/2+Math.cos(Math.toRadians(angle-90))*radius;
                        double outY = y+siz/2+Math.sin(Math.toRadians(angle-90))*radius;
                        GL11.glVertex2d(outX,outY);
                        angle+=(360d/(teeth*resolution));
                        if(angle>=360)angle-=360;
                        radius = averageRadius+(toothSize/2)*Math.cos(Math.toRadians(teeth*(angle-rot)));
                        outX = x+siz/2+Math.cos(Math.toRadians(angle-90))*radius;
                        outY = y+siz/2+Math.sin(Math.toRadians(angle-90))*radius;
                        GL11.glVertex2d(outX,outY);
                        inX = x+siz/2+Math.cos(Math.toRadians(angle-90))*holeRad;
                        inY = y+siz/2+Math.sin(Math.toRadians(angle-90))*holeRad;
                        GL11.glVertex2d(inX, inY);
                    }
                    GL11.glEnd();
                }
                Core.applyColor(Core.theme.getMetadataPanelHeaderColor());
                drawRect(.05, .75, .95, .8, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(.05, .75, .95, .8, "Metadata");
                String[][] txts = {{"Name", "Tutorial Collection"}, {"Author", "tomdodd4598"}};
                for(int x = 0; x<2; x++){
                    for(int y = 0; y<2; y++){
                        double inset = .005;
                        Core.applyColor(Core.theme.getListColor().darker());
                        drawRect(.05+.45*x, .8+.05*y, .5+.45*x, .85+.05*y, 0);
                        Core.applyColor(Core.theme.getListColor());
                        drawRect(.05+.45*x+inset/2, .8+.05*y+inset/2, .5+.45*x-inset/2, .85+.05*y-inset/2, 0);
                        Core.applyColor(Core.theme.getTextColor());
                        drawText(.05+.45*x+inset, .8+.05*y+inset, .5+.45*x-inset, .85+.05*y-inset, txts[y][x]);
                    }
                }
                GL11.glTranslated(0, .9, 0);
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Saving and Loading multiblocks">
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "Saving and Loading multiblocks");
                Core.applyColor(Core.theme.getButtonColor());
                drawRect(.55, .15, .95, .2, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(.55, .16, .65, .19, "Import");
                drawCenteredText(.65, .16, .75, .19, "Export");
                drawCenteredText(.75, .16, .85, .19, "Save");
                drawCenteredText(.85, .16, .95, .19, "Load");
                drawText(.05, .15, .5, .175, "The Import button adds multiblocks from");
                drawText(.05, .175, .5, .2, "a file");
                drawText(.05, .2, .5, .225, "The Export button saves the selected");
                drawText(.05, .225, .5, .25, "multiblock to a file");
                drawText(.05, .25, .5, .275, "The Save button saves the entire list of");
                drawText(.05, .275, .5, .3, "multiblocks to one .ncpf file");
                drawText(.05, .3, .5, .325, "The Load button replaces the current");
                drawText(.05, .325, .5, .35, "multiblock list with those loaded from a file");
                drawText(.05, .375, 1, .4, "Alternatively, Drag-and-drop files onto the planner to import them");
                //export formats
                drawCenteredText(.05, .45, .95, .475, "Export formats");
                drawText(.05, .475, .95, .5, "NCPF - Stores all multiblocks and metadata. This is the standard save format for");
                drawText(.05, .5, .95, .525, "this planner");
                drawText(.05, .525, .95, .55, "Hellrage JSON - Stores Overhaul and Underhaul SFRs. These can be loaded into");
                drawText(.05, .55, .95, .575, "Hellrage's reactor planner");
                drawText(.05, .575, .95, .6, "PNG - Exports an image detailing the multiblock design");
                //</editor-fold>
            }
        });
        planner.add(new Tutorial("Editing"){
            private int editorImage = -1;
            private MenuEdit pencilEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(0).newInstance(null, 9, 1, 5));
            private MenuEdit lineEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(0).newInstance(null, 9, 1, 5));
            private MenuEdit boxEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(0).newInstance(null, 9, 1, 5));
            private MenuEdit selectEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(0).newInstance(null, 9, 1, 5));
            private MenuEdit moveEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(0).newInstance(null, 9, 1, 5));
            private MenuComponentEditorGrid pencil, line, box, select, move;
            private MenuComponentEditorGrid[] editors;
            private Random rand = new Random();
            {
                pencilEditor.onGUIOpened();
                pencilEditor.tools.setSelectedIndex(2);
                lineEditor.onGUIOpened();
                lineEditor.tools.setSelectedIndex(3);
                boxEditor.onGUIOpened();
                boxEditor.tools.setSelectedIndex(4);
                selectEditor.onGUIOpened();
                selectEditor.tools.setSelectedIndex(1);
                moveEditor.onGUIOpened();
                moveEditor.tools.setSelectedIndex(0);
                pencil = (MenuComponentEditorGrid)pencilEditor.multibwauk.components.get(0);
                line = (MenuComponentEditorGrid)lineEditor.multibwauk.components.get(0);
                box = (MenuComponentEditorGrid)boxEditor.multibwauk.components.get(0);
                select = (MenuComponentEditorGrid)selectEditor.multibwauk.components.get(0);
                move = (MenuComponentEditorGrid)moveEditor.multibwauk.components.get(0);
                editors = new MenuComponentEditorGrid[]{pencil,line,box,select,move};
                moveEditor.multiblock.action(new SetblocksAction(moveEditor.getSelectedBlock(0)).add(0, 0, 1).add(1, 0, 1).add(2, 0, 1).add(0, 0, 2).add(1, 0, 2).add(2, 0, 2).add(0, 0, 3).add(1, 0, 3).add(2, 0, 3), false);
                ArrayList<int[]> selection = new ArrayList<>();
                selection.add(new int[]{0, 0, 1});
                selection.add(new int[]{1, 0, 1});
                selection.add(new int[]{2, 0, 1});
                selection.add(new int[]{0, 0, 2});
                selection.add(new int[]{1, 0, 2});
                selection.add(new int[]{2, 0, 2});
                selection.add(new int[]{0, 0, 3});
                selection.add(new int[]{1, 0, 3});
                selection.add(new int[]{2, 0, 3});
                moveEditor.multiblock.action(new SetSelectionAction(moveEditor, 0, selection), false);
            }
            @Override
            public double getHeight(double width){
                return width*3.475;
            }
            @Override
            public void preRender(){
                if(editorImage==-1){
                    MenuEdit editor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(0).newInstance());
                    GL11.glPushMatrix();
                    GL11.glScaled(0, 0, 0);
                    editor.render(0);
                    pencilEditor.render(0);
                    pencilEditor.render(0);
                    lineEditor.render(0);
                    lineEditor.render(0);
                    boxEditor.render(0);
                    boxEditor.render(0);
                    selectEditor.render(0);
                    selectEditor.render(0);
                    moveEditor.render(0);
                    moveEditor.render(0);
                    GL11.glPopMatrix();
                    editor.onGUIOpened();
                    BufferedImage image = Core.makeImage(Core.helper.displayWidth(), Core.helper.displayHeight(), (buff) -> {
                        GL11.glColor4d(1, 1, 1, 1);
                        editor.render(0);
                    });
                    editorImage = ImageStash.instance.allocateAndSetupTexture(image);
                }
            }
            private static final int squiggleLength = 40;//2 seconds of squiggle
            private static final int undoTime = 60;//3 seconds total
            private static final int loopLength = 70;//3 seconds total
            @Override
            public void tick(int tick){
                super.tick(tick);
                int t = tick%loopLength;//2.5 second loop
                if(t<=squiggleLength){
                    double x = (6*t)/(float)squiggleLength+1.5f;
                    double y = Math.cos((Math.PI*(x-3))/3)+5/2f;
                    for(MenuComponentEditorGrid grid : editors){
                        double X = x*grid.blockSize;
                        double Y = y*grid.blockSize;
                        grid.onMouseMove(X, Y);
                        if(t==0){
                            grid.editor.parts.setSelectedIndex(rand.nextInt(grid.editor.parts.components.size()));
                            grid.onMouseButton(X, Y, 0, true, 0);
                        }
                        else if(t==squiggleLength){
                            grid.onMouseButton(X, Y, 0, false, 0);
                            grid.onMouseMovedElsewhere(-1, -1);
                        }
                        else grid.mouseDragged(X, Y, 0);
                    }
                }
                if(t==undoTime){
                    for(MenuComponentEditorGrid grid : editors){
                        grid.editor.multiblock.undo();
                    }
                    moveEditor.multiblock.action(new SetblocksAction(moveEditor.getSelectedBlock(0)).add(0, 0, 1).add(1, 0, 1).add(2, 0, 1).add(0, 0, 2).add(1, 0, 2).add(2, 0, 2).add(0, 0, 3).add(1, 0, 3).add(2, 0, 3), false);
                    ArrayList<int[]> selection = new ArrayList<>();
                    selection.add(new int[]{0, 0, 1});
                    selection.add(new int[]{1, 0, 1});
                    selection.add(new int[]{2, 0, 1});
                    selection.add(new int[]{0, 0, 2});
                    selection.add(new int[]{1, 0, 2});
                    selection.add(new int[]{2, 0, 2});
                    selection.add(new int[]{0, 0, 3});
                    selection.add(new int[]{1, 0, 3});
                    selection.add(new int[]{2, 0, 3});
                    moveEditor.multiblock.action(new SetSelectionAction(moveEditor, 0, selection), false);
                }
            }
            @Override
            public void render(float resonatingBrightness, float frame){
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "Editing Overview");
                drawText(.05, .125, .95, .15, "There are many panels in the editor window.");
                drawText(.05, .15, .95, .175, "The tools panel is found on the top left, and contains all of the editing tools.");
                drawText(.05, .175, .95, .2, "The parts panel is found next to the tools panel, and contains all blocks that can");
                drawText(.05, .2, .95, .225, "be used in the multiblock.");
                drawText(.05, .225, .95, .25, "The info panel is found on the bottom left, and contains the overall multiblock info");
                drawText(.05, .25, .95, .275, "The Settings panel is found on the right, and contains multiblock-specific settings");
                drawText(.05, .275, .95, .3, "such as fuel or coolant/irradiator recipes");
                drawCenteredText(.05, .325, .95, .345, "Shown below: The editor window for a blank "+Core.multiblockTypes.get(0).getDefinitionName());
                Core.applyColor(Core.theme.getEditorListBorderColor());
                Renderer2D.drawRect(.05, .35, .95, .85, editorImage);
                GL11.glTranslated(0, .85, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "The Pencil Tool");
                {
                    GL11.glPushMatrix();
                    GL11.glTranslated(.05, .125, 0);
                    double scale = .45/pencil.width;
                    GL11.glScaled(scale, scale, 1);
                    pencil.render(0);
                    GL11.glPopMatrix();
                }
                Core.applyColor(Core.theme.getTextColor());
                drawText(.55, .125, .95, .15, "The Pencil tool lets you freely draw");
                drawText(.55, .15, .95, .175, "in the editor window. Click and drag");
                drawText(.55, .175, .95, .2, "to draw across the editor with the");
                drawText(.55, .2, .95, .225, "selected block");
                GL11.glTranslated(0, .4, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "The Line Tool");
                {
                    GL11.glPushMatrix();
                    GL11.glTranslated(.05, .125, 0);
                    double scale = .45/line.width;
                    GL11.glScaled(scale, scale, 1);
                    line.render(0);
                    GL11.glPopMatrix();
                }
                Core.applyColor(Core.theme.getTextColor());
                drawText(.55, .125, .95, .15, "The Line tool lets you draw straight");
                drawText(.55, .15, .95, .175, "lines of blocks through the");
                drawText(.55, .175, .95, .2, "multiblock. Click and drag from one");
                drawText(.55, .2, .95, .225, "point to another to draw a line");
                drawText(.55, .225, .95, .25, "between them. Lines can be drawn");
                drawText(.55, .25, .95, .275, "across layers, and will be drawn as");
                drawText(.55, .275, .95, .3, "a straight line through the multiblock.");
                GL11.glTranslated(0, .4, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "The Box Tool");
                {
                    GL11.glPushMatrix();
                    GL11.glTranslated(.05, .125, 0);
                    double scale = .45/box.width;
                    GL11.glScaled(scale, scale, 1);
                    box.render(0);
                    GL11.glPopMatrix();
                }
                Core.applyColor(Core.theme.getTextColor());
                drawText(.55, .125, .95, .15, "The Box tool lets you draw");
                drawText(.55, .15, .95, .175, "rectangles (or cuboids) in the");
                drawText(.55, .175, .95, .2, "multiblock. Click and drag from one");
                drawText(.55, .2, .95, .225, "corner to another to draw a cuboid");
                drawText(.55, .225, .95, .25, "between them.");
                GL11.glTranslated(0, .4, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "The Selection Tool");
                {
                    GL11.glPushMatrix();
                    GL11.glTranslated(.05, .125, 0);
                    double scale = .45/select.width;
                    GL11.glScaled(scale, scale, 1);
                    select.render(0);
                    GL11.glPopMatrix();
                }
                Core.applyColor(Core.theme.getTextColor());
                drawText(.55, .125, .95, .15, "The Selection tool lets you select");
                drawText(.55, .15, .95, .175, "areas of the multiblock. When an");
                drawText(.55, .175, .95, .2, "area is selected, edits will only be");
                drawText(.55, .2, .95, .225, "applied within the selection.");
                drawText(.55, .225, .95, .25, "Hold Ctrl to select multiple areas, or");
                drawText(.55, .25, .95, .275, "Ctrl+Right click to deselect an area.");
                drawText(.55, .275, .95, .3, "Press delete to remove all blocks in");
                drawText(.55, .3, .95, .325, "the selection.");
                drawText(.55, .325, .95, .35, "Press Ctrl+A to select the entire");
                drawText(.55, .35, .95, .375, "multiblock.");
                drawText(.55, .375, .95, .4, "Press Escape to deselect everything");
                drawText(.05, .4, .95, .425, "In some multiblocks, Shift-click and Alt-click may have special functions, such as");
                drawText(.05, .425, .95, .45, "selecting clusters or groups of heatsinks.");
                GL11.glTranslated(0, .45, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "The Move Tool");
                {
                    GL11.glPushMatrix();
                    GL11.glTranslated(.05, .125, 0);
                    double scale = .45/move.width;
                    GL11.glScaled(scale, scale, 1);
                    move.render(0);
                    GL11.glPopMatrix();
                }
                Core.applyColor(Core.theme.getTextColor());
                drawText(.55, .125, .95, .15, "The Selection tool lets you move or");
                drawText(.55, .15, .95, .175, "copy any selected areas. Click and");
                drawText(.55, .175, .95, .2, "drag to move a selection. Hold Ctrl");
                drawText(.55, .2, .95, .225, "to copy the selected blocks. Hold");
                drawText(.55, .225, .95, .25, "Ctrl+Shift to copy and keep the");
                drawText(.55, .25, .95, .275, "selection.");
                GL11.glTranslated(0, .4, 0);
                Core.applyColor(Core.theme.getTextColor());
                drawCenteredText(0, .05, 1, .1, "Other functions");
                drawText(.05, .125, .95, .15, "With any editing tool (pencil, line, box,) you can hold Ctrl to highlight possible");
                drawText(.05, .15, .95, .175, "placements for blocks with placement rules (such as heatsinks)");
                drawText(.05, .175, .95, .2, "Holding Ctrl while editing will only place blocks in these locations.");
                drawText(.05, .2, .95, .225, "Holding Ctrl+Shift Will allow other blocks to be overwritten.");
                drawCenteredText(.05, .25, .95, .275, "Copy/Paste");
                drawText(.05, .275, .95, .3, "Press Ctrl+X or Ctrl+C to switch to the Cut or Copy tools. Selecting any area will");
                drawText(.05, .3, .95, .325, "cut or copy it and switch to the paste tool.");
                drawText(.05, .325, .95, .35, "With the paste tool selected, Click to place copies of the original selected area.");
                drawText(.05, .35, .95, .375, "Press Escape to exit the paste tool.");
                drawText(.05, .375, .95, .4, "Press Ctrl+V to open the paste tool with the most recently copied selection.");
                drawCenteredText(.05, .45, .95, .475, "Undo/Redo");
                drawText(.05, .475, .95, .5, "Press Ctrl+"+(Core.invertUndoRedo?"Y":"Z")+" to undo the most recent action. Press Ctrl+"+(Core.invertUndoRedo?"Z":"Y")+" to redo.");
                drawText(.05, .5, .95, .525, "Some actions, such as resizing the multiblock, will clear all undo/redo history");
                //copy/paste, undo/redo, etc.
            }
        });
        //TODO tutorial on modifying configs and whatnot (first redo config system
        //TODO tutorial on using the generator
//        underhaul.add(new Tutorial("SFRs"){
//            @Override
//            public double getHeight(double width){
//                return width*.1;
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
    public abstract double getHeight(double width);
    public void tick(int tick){}
    /**
     * Called before the coordinates are scaled for render
     */
    public void preRender(){}
    public abstract void render(float resonatingBrightness, float frame);
}
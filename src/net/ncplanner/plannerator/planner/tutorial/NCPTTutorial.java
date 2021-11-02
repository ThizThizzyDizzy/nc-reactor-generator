package net.ncplanner.plannerator.planner.tutorial;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.editor.action.SetSelectionAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import org.lwjgl.opengl.GL11;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.menu.MenuEdit;
import net.ncplanner.plannerator.planner.menu.component.editor.MenuComponentEditorGrid;
import simplelibrary.image.Image;
import simplelibrary.opengl.ImageStash;
public class NCPTTutorial extends Tutorial{
    private static final float scale = .025f;
    private final ArrayList<Component> components = new ArrayList<>();
    private float innerMargin;
    private float outerMargin;
    private float[] offsets;
    private int columns = 1;
    private int column = 0;
    public NCPTTutorial(String name){
        super(name);
    }
    @Override
    public double getHeight(){
        innerMargin = outerMargin = 0;
        columns = 1;
        column = 0;
        offsets = new float[columns];
        Renderer renderer = new Renderer();
        for(Component c : components)c.draw(null, 0, 0);
        float offset = 0;
        for(float f : offsets){
            offset = Math.max(offset,f);
        }
        return offset;
    }
    @Override
    public void tick(int tick){
        for(Component c : components)c.tick(tick);
    }
    @Override
    public void preRender(){
        for(Component c : components)c.preRender();
    }
    @Override
    public void render(float resonatingBrightness, float frame){
        Renderer renderer = new Renderer();
        innerMargin = outerMargin = 0;
        columns = 1;
        column = 0;
        offsets = new float[columns];
        for(Component c : components)c.draw(renderer, resonatingBrightness, frame);
    }
    public void setMargin(float inner, float outer){
        add(new Component(){
            @Override
            public void draw(Renderer renderer, float resonatingBrightness, float frame){
                innerMargin = inner*scale;
                outerMargin = outer*scale;
            }
        });
    }
    public void translate(float offset){
        add(new Component(){
            @Override
            public void draw(Renderer renderer, float resonatingBrightness, float frame){
                offsets[column]+=offset*scale;
            }
        });
    }
    public void ltext(float height, String text){
        add(new Component(){
            @Override
            public void draw(Renderer renderer, float resonatingBrightness, float frame){
                if(renderer!=null)renderer.setColor(Core.theme.getTutorialTextColor());
                float columnLeft = column/(float)columns;
                float columnRight = (column+1f)/columns;
                if(column==0)columnLeft+=outerMargin;
                else columnLeft+=innerMargin;
                if(column==columns-1)columnRight-=outerMargin;
                else columnRight-=innerMargin;
                if(renderer!=null)renderer.drawText(columnLeft, offsets[column], columnRight, offsets[column]+height*scale, text);
                offsets[column]+=height*scale;
                column++;
                if(column>=columns)column = 0;
            }
        });
    }
    public void text(float height, String text){
        add(new Component(){
            @Override
            public void draw(Renderer renderer, float resonatingBrightness, float frame){
                if(renderer!=null)renderer.setColor(Core.theme.getTutorialTextColor());
                float columnLeft = column/(float)columns;
                float columnRight = (column+1f)/columns;
                if(column==0)columnLeft+=outerMargin;
                else columnLeft+=innerMargin;
                if(column==columns-1)columnRight-=outerMargin;
                else columnRight-=innerMargin;
                if(renderer!=null)renderer.drawCenteredText(columnLeft, offsets[column], columnRight, offsets[column]+height*scale, text);
                offsets[column]+=height*scale;
                column++;
                if(column>=columns)column = 0;
            }
        });
    }
    public void columns(int count){
        add(new Component(){
            @Override
            public void draw(Renderer renderer, float resonatingBrightness, float frame){
                float offset = 0;
                for(float f : offsets){
                    offset = Math.max(offset,f);
                }
                columns = count;
                column = 0;
                offsets = new float[count];
                for(int i = 0; i<offsets.length; i++){
                    offsets[i] = offset;
                }
            }
        });
    }
    public void squareImage(String path){
        add(new Component(){
            @Override
            public void draw(Renderer renderer, float resonatingBrightness, float frame){
                if(renderer!=null)renderer.setWhite();
                float columnLeft = column/(float)columns;
                float columnRight = (column+1f)/columns;
                if(column==0)columnLeft+=outerMargin;
                else columnLeft+=innerMargin;
                if(column==columns-1)columnRight-=outerMargin;
                else columnRight-=innerMargin;
                if(renderer!=null)renderer.drawImage("/textures/"+path, columnLeft, offsets[column], columnRight, offsets[column]+(columnRight-columnLeft));
                offsets[column]+=(columnRight-columnLeft);
                column++;
                if(column>=columns)column = 0;
            }
        });
    }
    private void add(Component component){
        components.add(component);
    }
    public void special(String name, boolean addHeight){
        components.add(new Component() {
            private Image editorImage;
            private MenuEdit pencilEditor,lineEditor,boxEditor,selectEditor,moveEditor;
            private MenuComponentEditorGrid pencil, line, box, select, move;
            private static final int squiggleLength = 40;//2 seconds of squiggle
            private static final int undoTime = 60;//3 seconds total
            private static final int loopLength = 70;//3 seconds total
            private Random rand = new Random();
            @Override
            public void preRender(){
                if(Core.multiblockTypes.size()<2)return;
                switch(name){
                    case "editor":
                        if(editorImage==null){
                            MenuEdit editor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance());
                            GL11.glPushMatrix();
                            GL11.glScaled(0, 0, 0);
                            editor.render(0);
                            GL11.glPopMatrix();
                            editor.onGUIOpened();
                            editorImage = Core.makeImage(Core.helper.displayWidth(), Core.helper.displayHeight(), (buff) -> {
                                GL11.glColor4d(1, 1, 1, 1);
                                editor.render(0);
                            });
                        }
                        break;
                    case "editor/tool/pencil":
                        if(pencilEditor==null){
                            pencilEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            pencilEditor.onGUIOpened();
                            pencilEditor.tools.setSelectedIndex(2);
                            pencil = (MenuComponentEditorGrid)pencilEditor.multibwauk.components.get(0);
                            pencilEditor.render(0);
                            pencilEditor.render(0);
                        }
                        break;
                    case "editor/tool/line":
                        if(lineEditor==null){
                            lineEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            lineEditor.onGUIOpened();
                            lineEditor.tools.setSelectedIndex(3);
                            line = (MenuComponentEditorGrid)lineEditor.multibwauk.components.get(0);
                            lineEditor.render(0);
                            lineEditor.render(0);
                        }
                        break;
                    case "editor/tool/box":
                        if(boxEditor==null){
                            boxEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            boxEditor.onGUIOpened();
                            boxEditor.tools.setSelectedIndex(4);
                            box = (MenuComponentEditorGrid)boxEditor.multibwauk.components.get(0);
                            boxEditor.render(0);
                            boxEditor.render(0);
                        }
                        break;
                    case "editor/tool/select":
                        if(selectEditor==null){
                            selectEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            selectEditor.onGUIOpened();
                            selectEditor.tools.setSelectedIndex(1);
                            select = (MenuComponentEditorGrid)selectEditor.multibwauk.components.get(0);
                            selectEditor.render(0);
                            selectEditor.render(0);
                        }
                        break;
                    case "editor/tool/move":
                        if(moveEditor==null){
                            moveEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            moveEditor.onGUIOpened();
                            moveEditor.tools.setSelectedIndex(0);
                            moveEditor.parts.setSelectedIndex(1);
                            move = (MenuComponentEditorGrid)moveEditor.multibwauk.components.get(0);
                            moveEditor.multiblock.action(new SetblocksAction(moveEditor.getSelectedBlock(0)).add(0, 0, 1).add(1, 0, 1).add(2, 0, 1).add(0, 0, 2).add(1, 0, 2).add(2, 0, 2).add(0, 0, 3).add(1, 0, 3).add(2, 0, 3), true, false);
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
                            moveEditor.multiblock.action(new SetSelectionAction(moveEditor, 0, selection), true, false);
                            moveEditor.render(0);
                            moveEditor.render(0);
                        }
                        break;
                }
            }
            @Override
            public void tick(int tick){
                if(Core.multiblockTypes.isEmpty())return;
                if(name.startsWith("editor/tool")){
                    MenuComponentEditorGrid grid;
                    switch(name){
                        case "editor/tool/pencil":
                            grid = pencil;
                            break;
                        case "editor/tool/line":
                            grid = line;
                            break;
                        case "editor/tool/box":
                            grid = box;
                            break;
                        case "editor/tool/select":
                            grid = select;
                            break;
                        case "editor/tool/move":
                            grid = move;
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown editor tool special: "+name);
                    }
                    int t = tick%loopLength;//2.5 second loop
                    if(t<=squiggleLength){
                        double x = (6*t)/(float)squiggleLength+1.5f;
                        double y = Math.cos((Math.PI*(x-3))/3)+5/2f;
                        double X = x*grid.blockSize;
                        double Y = y*grid.blockSize;
                        grid.onMouseMove(X, Y);
                        if(t==0){
                            grid.editor.parts.setSelectedIndex(1);
                            grid.onMouseButton(X, Y, 0, true, 0);
                        }
                        else if(t==squiggleLength){
                            grid.onMouseButton(X, Y, 0, false, 0);
                            grid.onMouseMovedElsewhere(-1, -1);
                        }
                        else grid.mouseDragged(X, Y, 0);
                    }
                    if(t==undoTime){
                        grid.editor.multiblock.undo(true);
                        if(grid==move){
                            grid.editor.multiblock.action(new SetblocksAction(grid.editor.getSelectedBlock(0)).add(0, 0, 1).add(1, 0, 1).add(2, 0, 1).add(0, 0, 2).add(1, 0, 2).add(2, 0, 2).add(0, 0, 3).add(1, 0, 3).add(2, 0, 3), true, false);
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
                            grid.editor.multiblock.action(new SetSelectionAction(grid.editor, 0, selection), true, false);
                        }
                    }
                }
            }
            @Override
            public void draw(Renderer renderer, float resonatingBrightness, float frame){
                if(Core.multiblockTypes.isEmpty())return;
                float colLeft = column/(float)columns;
                float colRight = (column+1f)/columns;
                if(column==0)colLeft+=outerMargin;
                else colLeft+=innerMargin;
                if(column==columns-1)colRight-=outerMargin;
                else colRight-=innerMargin;
                switch(name){
                    case "menu/settings":
                        //<editor-fold defaultstate="collapsed" desc="menu/settings">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getComponentColor(0));
                            renderer.fillRect(.9, offsets[column], .95, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            renderer.fillRect(.9, offsets[column], .95, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            {
                                ImageStash.instance.bindTexture(0);
                                double siz = .05;
                                double x = .9;
                                double y = offsets[column];
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
                        }
                        if(addHeight)offsets[column]+=scale*2;
                        break;
    //</editor-fold>
                    case "menu/multiblocks/add":
                        //<editor-fold defaultstate="collapsed" desc="menu/multiblocks/add">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            renderer.fillRect(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getComponentColor(0));
                            renderer.fillRect(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            renderer.fillRect(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2, "Multiblocks");
                            renderer.drawCenteredText(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2, "+");
                        }
                        if(addHeight)offsets[column]+=scale*2;
                        break;
    //</editor-fold>
                    case "menu/multiblocks/edit":
                        //<editor-fold defaultstate="collapsed" desc="menu/multiblocks/edit">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            renderer.fillRect(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getComponentColor(0));
                            renderer.fillRect(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.fillRect(colLeft, offsets[column]+scale*2, colRight, offsets[column]+scale*6);
                            renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            renderer.fillRect(colRight-scale*3, offsets[column]+scale*3, colRight-scale, offsets[column]+scale*5);
                            renderer.setColor(Core.theme.getSecondaryComponentMouseoverColor(0), resonatingBrightness);
                            renderer.fillRect(colRight-scale*3, offsets[column]+scale*3, colRight-scale, offsets[column]+scale*5);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            //<editor-fold defaultstate="collapsed" desc="Pencil icon">
                            GL11.glBegin(GL11.GL_TRIANGLES);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.25, offsets[column]+scale*3+scale*2*.75);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.375, offsets[column]+scale*3+scale*2*.75);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.25, offsets[column]+scale*3+scale*2*.625);
                            GL11.glEnd();
                            GL11.glBegin(GL11.GL_QUADS);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.4, offsets[column]+scale*3+scale*2*.725);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.275, offsets[column]+scale*3+scale*2*.6);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.5, offsets[column]+scale*3+scale*2*.375);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.625, offsets[column]+scale*3+scale*2*.5);

                            GL11.glVertex2d(colRight-scale*3+scale*2*.525, offsets[column]+scale*3+scale*2*.35);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.65, offsets[column]+scale*3+scale*2*.475);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.75, offsets[column]+scale*3+scale*2*.375);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.625, offsets[column]+scale*3+scale*2*.25);
                            GL11.glEnd();
                            //</editor-fold>
                            renderer.drawCenteredText(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2, "Multiblocks");
                            renderer.drawCenteredText(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2, "+");
                            renderer.drawText(colLeft, offsets[column]+scale*3, colRight, offsets[column]+scale*4, "Overhaul SFR");
                        }
                        if(addHeight)offsets[column]+=scale*6;
                        break;
    //</editor-fold>
                    case "menu/multiblocks/select":
                        //<editor-fold defaultstate="collapsed" desc="menu/multiblocks/select">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            renderer.fillRect(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getComponentColor(0));
                            renderer.fillRect(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.fillRect(colLeft, offsets[column]+scale*2, colRight, offsets[column]+scale*6);
                            renderer.setColor(Core.theme.getSelectedComponentColor(0), resonatingBrightness);
                            renderer.fillRect(colLeft, offsets[column]+scale*2, colRight, offsets[column]+scale*6);
                            renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            renderer.fillRect(colRight-scale*3, offsets[column]+scale*3, colRight-scale, offsets[column]+scale*5);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            //<editor-fold defaultstate="collapsed" desc="Pencil icon">
                            GL11.glBegin(GL11.GL_TRIANGLES);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.25, offsets[column]+scale*3+scale*2*.75);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.375, offsets[column]+scale*3+scale*2*.75);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.25, offsets[column]+scale*3+scale*2*.625);
                            GL11.glEnd();
                            GL11.glBegin(GL11.GL_QUADS);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.4, offsets[column]+scale*3+scale*2*.725);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.275, offsets[column]+scale*3+scale*2*.6);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.5, offsets[column]+scale*3+scale*2*.375);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.625, offsets[column]+scale*3+scale*2*.5);

                            GL11.glVertex2d(colRight-scale*3+scale*2*.525, offsets[column]+scale*3+scale*2*.35);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.65, offsets[column]+scale*3+scale*2*.475);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.75, offsets[column]+scale*3+scale*2*.375);
                            GL11.glVertex2d(colRight-scale*3+scale*2*.625, offsets[column]+scale*3+scale*2*.25);
                            GL11.glEnd();
                            //</editor-fold>
                            renderer.drawCenteredText(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2, "Multiblocks");
                            renderer.drawCenteredText(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2, "+");
                            renderer.drawText(colLeft, offsets[column]+scale*3, colRight, offsets[column]+scale*4, "Overhaul SFR");
                        }
                        if(addHeight)offsets[column]+=scale*6;
                        break;
    //</editor-fold>
                    case "menu/multiblocks/delete":
                        //<editor-fold defaultstate="collapsed" desc="menu/multiblocks/delete">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getComponentColor(0));
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getDeleteButtonTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+scale*2, "Delete (Hold Shift)");
                        }
                        if(addHeight)offsets[column]+=scale*2;
                        break;
    //</editor-fold>
                    case "editor/resize":
                        //<editor-fold defaultstate="collapsed" desc="editor/resize">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getComponentColor(0));
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+scale*2, "Resize");
                        }
                        if(addHeight)offsets[column]+=scale*2;
                        break;
    //</editor-fold>
                    case "resize":
                        //<editor-fold defaultstate="collapsed" desc="resize">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getComponentColor(0));
                            double s = scale*2;
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+s);//top
                            renderer.fillRect(colLeft, offsets[column]+s*1.5, colLeft+s*5, offsets[column]+s*3.5);//top inner
                            renderer.fillRect(colLeft, offsets[column]+s*3.5, colLeft+s*2, offsets[column]+s*6.5);//left
                            renderer.fillRect(colLeft+s*5, offsets[column]+s*3.5, colLeft+s*6, offsets[column]+s*6.5);//right
                            renderer.fillRect(colLeft+s*2, offsets[column]+s*6.5, colLeft+s*5, offsets[column]+s*7.5);//bottom inner
                            renderer.fillRect(colLeft, offsets[column]+s*8, colRight, offsets[column]+s*9);//bottom
                            renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+s);//top
                            renderer.fillRect(colLeft, offsets[column]+s*8, colRight, offsets[column]+s*9);//bottom
                            renderer.setColor(Core.theme.getAddButtonTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+s, "+");//top
                            renderer.drawCenteredText(colLeft+s*2, offsets[column]+s*1.5, colLeft+s*5, offsets[column]+s*2.5, "+");//top inner
                            renderer.drawCenteredText(colLeft, offsets[column]+s*4.5, colLeft+s, offsets[column]+s*5.5, "+");//left
                            renderer.drawCenteredText(colLeft+s*5, offsets[column]+s*4.5, colLeft+s*6, offsets[column]+s*5.5, "+");//right
                            renderer.drawCenteredText(colLeft+s*2, offsets[column]+s*6.5, colLeft+s*5, offsets[column]+s*7.5, "+");//bottom inner
                            renderer.drawCenteredText(colLeft, offsets[column]+s*8, colRight, offsets[column]+s*9, "+");//bottom
                            renderer.setColor(Core.theme.getDeleteButtonTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column]+s*1.5, colLeft+s*2, offsets[column]+s*3.5, "-");//top inner
                            renderer.drawCenteredText(colLeft+s*2, offsets[column]+s*2.5, colLeft+s*3, offsets[column]+s*3.5, "-");//top 1
                            renderer.drawCenteredText(colLeft+s*3, offsets[column]+s*2.5, colLeft+s*4, offsets[column]+s*3.5, "-");//top 2
                            renderer.drawCenteredText(colLeft+s*4, offsets[column]+s*2.5, colLeft+s*5, offsets[column]+s*3.5, "-");//top 3
                            renderer.drawCenteredText(colLeft+s, offsets[column]+s*3.5, colLeft+s*2, offsets[column]+s*4.5, "-");//left 1
                            renderer.drawCenteredText(colLeft+s, offsets[column]+s*4.5, colLeft+s*2, offsets[column]+s*5.5, "-");//left 2
                            renderer.drawCenteredText(colLeft+s, offsets[column]+s*5.5, colLeft+s*2, offsets[column]+s*6.5, "-");//left 3
                            renderer.setColor(Core.theme.getEditorBackgroundColor());
                            renderer.fillRect(colLeft+s*2, offsets[column]+s*3.5, colLeft+s*5, offsets[column]+s*6.5);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            double border = s/32;
                            for(int x = 0; x<3; x++){
                                for(int y = 0; y<3; y++){
                                    double X = colLeft+s*(2+x);
                                    double Y = offsets[column]+s*(3.5+y);
                                    renderer.fillRect(X,Y,X+s,Y+border);
                                    renderer.fillRect(X,Y+s-border,X+s,Y+s);
                                    renderer.fillRect(X,Y+border,X+border,Y+s-border);
                                    renderer.fillRect(X+s-border,Y+border,X+s,Y+s-border);
                                }
                            }
                        }
                        if(addHeight)offsets[column]+=scale*18;
                        break;
    //</editor-fold>
                    case "editor/header":
                        //<editor-fold defaultstate="collapsed" desc="editor/header">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getComponentColor(0));
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            renderer.fillRect(colLeft+.25, offsets[column], colRight-.25, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column]+scale/2.5, colLeft+.25, offsets[column]+scale*2-scale/2.5, "Done");
                            renderer.drawCenteredText(colLeft+.25, offsets[column]+scale/2.5, colRight-.25, offsets[column]+scale*2-scale/2.5, "Tutorial Build v1 | Edit Metadata");
                            renderer.drawCenteredText(colRight-.25, offsets[column]+scale/2.5, colRight, offsets[column]+scale*2-scale/2.5, "Resize");
                        }
                        if(addHeight)offsets[column]+=scale*2;
                        break;
    //</editor-fold>
                    case "editor/metadata":
                        //<editor-fold defaultstate="collapsed" desc="editor/metadata">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getMetadataPanelHeaderColor());
                            //.4
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+scale*2, "Metadata");
                            String[][] texts = {{"Name", "Tutorial Build v1"}, {"Author", "tomdodd4598"}};
                            double w = (colRight-colLeft)/2;
                            double c = (colLeft+colRight)/2;
                            for(int x = 0; x<2; x++){
                                for(int y = 0; y<2; y++){
                                    double inset = .005;
                                    renderer.setColor(Core.theme.getTextBoxBorderColor());
                                    renderer.fillRect(colLeft+w*x, offsets[column]+scale*2+scale*2*y, c+w*x, offsets[column]+scale*4+scale*2*y);
                                    renderer.setColor(Core.theme.getTextBoxColor());
                                    renderer.fillRect(colLeft+w*x+inset/2, offsets[column]+scale*2+scale*2*y+inset/2, c+w*x-inset/2, offsets[column]+scale*4+scale*2*y-inset/2);
                                    renderer.setColor(Core.theme.getTutorialTextColor());
                                    renderer.drawText(colLeft+w*x+inset, offsets[column]+scale*2+scale*2*y+inset, c+w*x-inset, offsets[column]+scale*4+scale*2*y-inset, texts[y][x]);
                                }
                            }
                        }
                        if(addHeight)offsets[column]+=scale*6;
                        break;
    //</editor-fold>
                    case "menu/header/metadata":
                        //<editor-fold defaultstate="collapsed" desc="menu/header/metadata">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getComponentColor(0));
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            renderer.fillRect(colLeft+scale*11.2, offsets[column], colRight-scale*2, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column]+.015, colLeft+scale*2.8, offsets[column]+scale*2-.015, "Import");
                            renderer.drawCenteredText(colLeft+scale*2.8, offsets[column]+.015, colLeft+scale*2.8*2, offsets[column]+scale*2-.015, "Export");
                            renderer.drawCenteredText(colLeft+scale*2.8*2, offsets[column]+.015, colLeft+scale*2.8*3, offsets[column]+scale*2-.015, "Save");
                            renderer.drawCenteredText(colLeft+scale*2.8*3, offsets[column]+.015, colLeft+scale*2.8*4, offsets[column]+scale*2-.015, "Load");
                            renderer.drawCenteredText(colLeft+scale*11.2, offsets[column]+.01, colRight-scale*2, offsets[column]+scale*2-.01, "Tutorial Collection | Edit Metadata");
                            {
                                renderer.setColor(Core.theme.getTutorialTextColor());
                                ImageStash.instance.bindTexture(0);
                                double siz = .05;
                                double x = .9;
                                double y = offsets[column];
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
                        }
                        if(addHeight)offsets[column]+=scale*2;
                        break;
    //</editor-fold>
                    case "menu/metadata":
                        //<editor-fold defaultstate="collapsed" desc="menu/metadata">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getMetadataPanelHeaderColor());
                            //.4
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+scale*2, "Metadata");
                            String[][] texts = {{"Name", "Tutorial Collection"}, {"Author", "tomdodd4598"}};
                            double w = (colRight-colLeft)/2;
                            double c = (colLeft+colRight)/2;
                            for(int x = 0; x<2; x++){
                                for(int y = 0; y<2; y++){
                                    double inset = .005;
                                    renderer.setColor(Core.theme.getTextBoxBorderColor());
                                    renderer.fillRect(colLeft+w*x, offsets[column]+scale*2+scale*2*y, c+w*x, offsets[column]+scale*4+scale*2*y);
                                    renderer.setColor(Core.theme.getTextBoxColor());
                                    renderer.fillRect(colLeft+w*x+inset/2, offsets[column]+scale*2+scale*2*y+inset/2, c+w*x-inset/2, offsets[column]+scale*4+scale*2*y-inset/2);
                                    renderer.setColor(Core.theme.getTutorialTextColor());
                                    renderer.drawText(colLeft+w*x+inset, offsets[column]+scale*2+scale*2*y+inset, c+w*x-inset, offsets[column]+scale*4+scale*2*y-inset, texts[y][x]);
                                }
                            }
                        }
                        if(addHeight)offsets[column]+=scale*6;
                        break;
    //</editor-fold>
                    case "menu/file":
                        //<editor-fold defaultstate="collapsed" desc="menu/file">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getComponentColor(0));
                            renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            double w = (colRight-colLeft)/4;
                            renderer.setColor(Core.theme.getTutorialTextColor());
                            renderer.drawCenteredText(colLeft, offsets[column]+.01, colLeft+w, offsets[column]+scale*2-.01, "Import");
                            renderer.drawCenteredText(colLeft+w, offsets[column]+.01, colLeft+w*2, offsets[column]+scale*2-.01, "Export");
                            renderer.drawCenteredText(colLeft+w*2, offsets[column]+.01, colLeft+w*3, offsets[column]+scale*2-.01, "Save");
                            renderer.drawCenteredText(colLeft+w*3, offsets[column]+.01, colRight, offsets[column]+scale*2-.01, "Load");
                        }
                        if(addHeight)offsets[column]+=scale*2;
                        break;
    //</editor-fold>
                    case "editor":
                        //<editor-fold defaultstate="collapsed" desc="editor">
                        if(renderer!=null){
                            renderer.setColor(Core.theme.getTutorialBackgroundColor());
                            renderer.drawImage(editorImage, colLeft, offsets[column], colRight, offsets[column]+scale*20);
                        }
                        if(addHeight)offsets[column]+=scale*20;
                        break;
    //</editor-fold>
                    case "editor/tool/pencil":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/pencil">
                        double scal = (colRight-colLeft)/pencil.width;
                        if(renderer!=null){
                            GL11.glPushMatrix();
                            GL11.glTranslated(colLeft, offsets[column], 0);
                            double scale = (colRight-colLeft)/pencil.width;
                            GL11.glScaled(scale, scale, 1);
                            pencil.render(0);
                            GL11.glPopMatrix();
                        }
                        if(addHeight)offsets[column]+=scal*pencil.height;
                        break;
    //</editor-fold>
                    case "editor/tool/line":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/line">
                        scal = (colRight-colLeft)/line.width;
                        if(renderer!=null){
                            GL11.glPushMatrix();
                            GL11.glTranslated(colLeft, offsets[column], 0);
                            double scale = (colRight-colLeft)/line.width;
                            GL11.glScaled(scale, scale, 1);
                            line.render(0);
                            GL11.glPopMatrix();
                        }
                        if(addHeight)offsets[column]+=scal*line.height;
                        break;
    //</editor-fold>
                    case "editor/tool/box":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/box">
                        scal = (colRight-colLeft)/box.width;
                        if(renderer!=null){
                            GL11.glPushMatrix();
                            GL11.glTranslated(colLeft, offsets[column], 0);
                            double scale = (colRight-colLeft)/box.width;
                            GL11.glScaled(scale, scale, 1);
                            box.render(0);
                            GL11.glPopMatrix();
                        }
                        if(addHeight)offsets[column]+=scal*box.height;
                        break;
    //</editor-fold>
                    case "editor/tool/select":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/select">
                        scal = (colRight-colLeft)/select.width;
                        if(renderer!=null){
                            GL11.glPushMatrix();
                            GL11.glTranslated(colLeft, offsets[column], 0);
                            double scale = (colRight-colLeft)/select.width;
                            GL11.glScaled(scale, scale, 1);
                            select.render(0);
                            GL11.glPopMatrix();
                        }
                        if(addHeight)offsets[column]+=scal*select.height;
                        break;
    //</editor-fold>
                    case "editor/tool/move":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/move">
                        scal = (colRight-colLeft)/move.width;
                        if(renderer!=null){
                            GL11.glPushMatrix();
                            GL11.glTranslated(colLeft, offsets[column], 0);
                            double scale = (colRight-colLeft)/move.width;
                            GL11.glScaled(scale, scale, 1);
                            move.render(0);
                            GL11.glPopMatrix();
                        }
                        if(addHeight)offsets[column]+=scal*move.height;
                        break;
    //</editor-fold>
                    default:
                        throw new IllegalArgumentException("unknown special: "+name);
                }
            }
        });
        if(addHeight)skip();
    }
    public void skip(){
        add(new Component() {
            @Override
            public void draw(Renderer renderer, float resonatingBrightness, float frame){
                column++;
                if(column>=columns)column = 0;
            }
        });
    }
    private abstract class Component{
        public abstract void draw(Renderer renderer, float resonatingBrightness, float frame);
        public void tick(int tick){}
        public void preRender(){}
    }
}
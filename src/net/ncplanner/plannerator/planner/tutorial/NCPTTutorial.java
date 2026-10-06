package net.ncplanner.plannerator.planner.tutorial;
import com.thizthizzydizzy.dizzyengine.MathUtil;
import com.thizthizzydizzy.dizzyengine.ResourceManager;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.Core;
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
    public float getHeight(){
        innerMargin = outerMargin = 0;
        columns = 1;
        column = 0;
        offsets = new float[columns];
        for(Component c : components)c.draw(0, 0);
        float offset = 0;
        for(float f : offsets){
            offset = MathUtil.max(offset, f);
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
        innerMargin = outerMargin = 0;
        columns = 1;
        column = 0;
        offsets = new float[columns];
        for(Component c : components)c.draw(resonatingBrightness, frame);
    }
    public void setMargin(float inner, float outer){
        add(new Component(){
            @Override
            public void draw(float resonatingBrightness, float frame){
                innerMargin = inner*scale;
                outerMargin = outer*scale;
            }
        });
    }
    public void translate(float offset){
        add(new Component(){
            @Override
            public void draw(float resonatingBrightness, float frame){
                offsets[column] += offset*scale;
            }
        });
    }
    public void ltext(float height, String text){
        add(new Component(){
            @Override
            public void draw(float resonatingBrightness, float frame){
                Renderer.setColor(Core.theme.getTutorialTextColor());
                float columnLeft = column/(float)columns;
                float columnRight = (column+1f)/columns;
                if(column==0)columnLeft += outerMargin;
                else
                    columnLeft += innerMargin;
                if(column==columns-1)columnRight -= outerMargin;
                else
                    columnRight -= innerMargin;
                Renderer.drawText(columnLeft, offsets[column], columnRight, offsets[column]+height*scale, text);
                offsets[column] += height*scale;
                column++;
                if(column>=columns)column = 0;
            }
        });
    }
    public void text(float height, String text){
        add(new Component(){
            @Override
            public void draw(float resonatingBrightness, float frame){
                Renderer.setColor(Core.theme.getTutorialTextColor());
                float columnLeft = column/(float)columns;
                float columnRight = (column+1f)/columns;
                if(column==0)columnLeft += outerMargin;
                else
                    columnLeft += innerMargin;
                if(column==columns-1)columnRight -= outerMargin;
                else
                    columnRight -= innerMargin;
                Renderer.drawCenteredText(columnLeft, offsets[column], columnRight, offsets[column]+height*scale, text);
                offsets[column] += height*scale;
                column++;
                if(column>=columns)column = 0;
            }
        });
    }
    public void columns(int count){
        add(new Component(){
            @Override
            public void draw(float resonatingBrightness, float frame){
                float offset = 0;
                for(float f : offsets){
                    offset = MathUtil.max(offset, f);
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
            private Integer texture = null;
            @Override
            public void draw(float resonatingBrightness, float frame){
                if(texture==null){
                    texture = ResourceManager.getTexture("/textures/"+path);
                }
                Renderer.setColor(Color.WHITE);
                float columnLeft = column/(float)columns;
                float columnRight = (column+1f)/columns;
                if(column==0)columnLeft += outerMargin;
                else
                    columnLeft += innerMargin;
                if(column==columns-1)columnRight -= outerMargin;
                else
                    columnRight -= innerMargin;
                Renderer.fillRect(columnLeft, offsets[column], columnRight, offsets[column]+(columnRight-columnLeft), texture);
                offsets[column] += (columnRight-columnLeft);
                column++;
                if(column>=columns)column = 0;
            }
        });
    }
    private void add(Component component){
        components.add(component);
    }
    public void special(String name, boolean addHeight){
        if(true)throw new UnsupportedOperationException("Not yet implemented");
        /*
        components.add(new Component(){
            private Image editorImage;
            private MenuEdit pencilEditor, lineEditor, boxEditor, selectEditor, moveEditor;
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
                            editor.render2d(0);
                            editor.onOpened();
                            editorImage = Core.makeImage(Core.gui.getWidth(), Core.gui.getHeight(), (buffRenderer, buffWidth, buffHeight) -> {
                                buffRenderer.setColor(Color.WHITE);
                                editor.render2d(0);
                            }).flip();
                        }
                        break;
                    case "editor/tool/pencil":
                        if(pencilEditor==null){
                            pencilEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            pencilEditor.onOpened();
                            pencilEditor.tools.setSelectedIndex(2);
                            pencil = (MenuComponentEditorGrid)pencilEditor.multibwauk.components.get(0);
                            pencilEditor.render2d(0);
                            pencilEditor.render2d(0);
                        }
                        break;
                    case "editor/tool/line":
                        if(lineEditor==null){
                            lineEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            lineEditor.onOpened();
                            lineEditor.tools.setSelectedIndex(3);
                            line = (MenuComponentEditorGrid)lineEditor.multibwauk.components.get(0);
                            lineEditor.render2d(0);
                            lineEditor.render2d(0);
                        }
                        break;
                    case "editor/tool/box":
                        if(boxEditor==null){
                            boxEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            boxEditor.onOpened();
                            boxEditor.tools.setSelectedIndex(4);
                            box = (MenuComponentEditorGrid)boxEditor.multibwauk.components.get(0);
                            boxEditor.render2d(0);
                            boxEditor.render2d(0);
                        }
                        break;
                    case "editor/tool/select":
                        if(selectEditor==null){
                            selectEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            selectEditor.onOpened();
                            selectEditor.tools.setSelectedIndex(1);
                            select = (MenuComponentEditorGrid)selectEditor.multibwauk.components.get(0);
                            selectEditor.render2d(0);
                            selectEditor.render2d(0);
                        }
                        break;
                    case "editor/tool/move":
                        if(moveEditor==null){
                            moveEditor = new MenuEdit(Core.gui, null, Core.multiblockTypes.get(1).newInstance(null, 7, 1, 3));
                            moveEditor.onOpened();
                            moveEditor.tools.setSelectedIndex(0);
                            moveEditor.parts.setSelectedIndex(1);
                            move = (MenuComponentEditorGrid)moveEditor.multibwauk.components.get(0);
                            moveEditor.multiblock.action(new SetblocksAction(moveEditor.getSelectedBlock(0))
                                .add(new BlockPos(0, 0, 1))
                                .add(new BlockPos(1, 0, 1))
                                .add(new BlockPos(2, 0, 1))
                                .add(new BlockPos(0, 0, 2))
                                .add(new BlockPos(1, 0, 2))
                                .add(new BlockPos(2, 0, 2))
                                .add(new BlockPos(0, 0, 3))
                                .add(new BlockPos(1, 0, 3))
                                .add(new BlockPos(2, 0, 3)),
                                 true, false);
                            ArrayList<BlockPos> selection = new ArrayList<>();
                            selection.add(new BlockPos(0, 0, 1));
                            selection.add(new BlockPos(1, 0, 1));
                            selection.add(new BlockPos(2, 0, 1));
                            selection.add(new BlockPos(0, 0, 2));
                            selection.add(new BlockPos(1, 0, 2));
                            selection.add(new BlockPos(2, 0, 2));
                            selection.add(new BlockPos(0, 0, 3));
                            selection.add(new BlockPos(1, 0, 3));
                            selection.add(new BlockPos(2, 0, 3));
                            moveEditor.multiblock.action(new SetSelectionAction(moveEditor, 0, selection), true, false);
                            moveEditor.render2d(0);
                            moveEditor.render2d(0);
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
                        double y = MathUtil.cos((MathUtil.pi()*(x-3))/3)+5/2f;
                        double X = x*grid.blockSize;
                        double Y = y*grid.blockSize;
                        grid.onCursorMoved(X, Y);
                        if(t==0){
                            grid.editor.parts.setSelectedIndex(1);
                            grid.onMouseButton(X, Y, 0, GLFW_PRESS, 0);
                        }else if(t==squiggleLength){
                            grid.onMouseButton(X, Y, 0, GLFW_RELEASE, 0);
                            grid.onCursorExited();
                        }elsegrid.mouseDragged(X, Y, 0);
                    }
                    if(t==undoTime){
                        grid.editor.multiblock.undo(true);
                        if(grid==move){
                            grid.editor.multiblock.action(new SetblocksAction(grid.editor.getSelectedBlock(0))
                                .add(new BlockPos(0, 0, 1))
                                .add(new BlockPos(1, 0, 1))
                                .add(new BlockPos(2, 0, 1))
                                .add(new BlockPos(0, 0, 2))
                                .add(new BlockPos(1, 0, 2))
                                .add(new BlockPos(2, 0, 2))
                                .add(new BlockPos(0, 0, 3))
                                .add(new BlockPos(1, 0, 3))
                                .add(new BlockPos(2, 0, 3)),
                                 true, false);
                            ArrayList<BlockPos> selection = new ArrayList<>();
                            selection.add(new BlockPos(0, 0, 1));
                            selection.add(new BlockPos(1, 0, 1));
                            selection.add(new BlockPos(2, 0, 1));
                            selection.add(new BlockPos(0, 0, 2));
                            selection.add(new BlockPos(1, 0, 2));
                            selection.add(new BlockPos(2, 0, 2));
                            selection.add(new BlockPos(0, 0, 3));
                            selection.add(new BlockPos(1, 0, 3));
                            selection.add(new BlockPos(2, 0, 3));
                            grid.editor.multiblock.action(new SetSelectionAction(grid.editor, 0, selection), true, false);
                        }
                    }
                }
            }
            @Override
            public void draw(float resonatingBrightness, float frame){
                if(Core.multiblockTypes.isEmpty())return;
                float colLeft = column/(float)columns;
                float colRight = (column+1f)/columns;
                if(column==0)colLeft += outerMargin;
                else
                    colLeft += innerMargin;
                if(column==columns-1)colRight -= outerMargin;
                else
                    colRight -= innerMargin;
                switch(name){
                    case "menu/settings":
                        //<editor-fold defaultstate="collapsed" desc="menu/settings">
                        Renderer.setColor(Core.theme.getComponentColor(0));
                        Renderer.fillRect(.9f, offsets[column], .95f, offsets[column]+scale*2);
                        Renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                        Renderer.fillRect(.9f, offsets[column], .95f, offsets[column]+scale*2);
                        Renderer.setColor(Core.theme.getTutorialTextColor());
                        float siz = .05f;
                        Renderer.drawGear(.9f+siz/2, offsets[column]+siz/2, siz*.1f, 8, siz*.3f, siz*.1f, 360/16f);
                        if(addHeight)offsets[column] += scale*2;
                        break;
                    //</editor-fold>
                    case "menu/multiblocks/add":
                        //<editor-fold defaultstate="collapsed" desc="menu/multiblocks/add">
                        if(true){
                            Renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            Renderer.fillRect(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getComponentColor(0));
                            Renderer.fillRect(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            Renderer.fillRect(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2, "Multiblocks");
                            Renderer.drawCenteredText(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2, "+");
                        }
                        if(addHeight)offsets[column] += scale*2;
                        break;
                    //</editor-fold>
                    case "menu/multiblocks/edit":
                        //<editor-fold defaultstate="collapsed" desc="menu/multiblocks/edit">
                        if(true){
                            Renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            Renderer.fillRect(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getComponentColor(0));
                            Renderer.fillRect(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.fillRect(colLeft, offsets[column]+scale*2, colRight, offsets[column]+scale*6);
                            Renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            Renderer.fillRect(colRight-scale*3, offsets[column]+scale*3, colRight-scale, offsets[column]+scale*5);
                            Renderer.setColor(Core.theme.getSecondaryComponentMouseoverColor(0), resonatingBrightness);
                            Renderer.fillRect(colRight-scale*3, offsets[column]+scale*3, colRight-scale, offsets[column]+scale*5);
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            Renderer.drawElement("pencil", colRight-scale*3, offsets[column]+scale*3, scale*2, scale*2);
                            Renderer.drawCenteredText(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2, "Multiblocks");
                            Renderer.drawCenteredText(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2, "+");
                            Renderer.drawText(colLeft, offsets[column]+scale*3, colRight, offsets[column]+scale*4, "Overhaul SFR");
                        }
                        if(addHeight)offsets[column] += scale*6;
                        break;
                    //</editor-fold>
                    case "menu/multiblocks/select":
                        //<editor-fold defaultstate="collapsed" desc="menu/multiblocks/select">
                        if(true){
                            Renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            Renderer.fillRect(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getComponentColor(0));
                            Renderer.fillRect(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.fillRect(colLeft, offsets[column]+scale*2, colRight, offsets[column]+scale*6);
                            Renderer.setColor(Core.theme.getSelectedComponentColor(0), resonatingBrightness);
                            Renderer.fillRect(colLeft, offsets[column]+scale*2, colRight, offsets[column]+scale*6);
                            Renderer.setColor(Core.theme.getSecondaryComponentColor(0));
                            Renderer.fillRect(colRight-scale*3, offsets[column]+scale*3, colRight-scale, offsets[column]+scale*5);
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            Renderer.drawElement("pencil", colRight-scale*3, offsets[column]+scale*3, scale*2, scale*2);
                            Renderer.drawCenteredText(colLeft, offsets[column], colRight-scale*2, offsets[column]+scale*2, "Multiblocks");
                            Renderer.drawCenteredText(colRight-scale*2, offsets[column], colRight, offsets[column]+scale*2, "+");
                            Renderer.drawText(colLeft, offsets[column]+scale*3, colRight, offsets[column]+scale*4, "Overhaul SFR");
                        }
                        if(addHeight)offsets[column] += scale*6;
                        break;
                    //</editor-fold>
                    case "menu/multiblocks/delete":
                        //<editor-fold defaultstate="collapsed" desc="menu/multiblocks/delete">
                        if(true){
                            Renderer.setColor(Core.theme.getComponentColor(0));
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getDeleteButtonTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+scale*2, "Delete (Hold Shift)");
                        }
                        if(addHeight)offsets[column] += scale*2;
                        break;
                    //</editor-fold>
                    case "editor/resize":
                        //<editor-fold defaultstate="collapsed" desc="editor/resize">
                        if(true){
                            Renderer.setColor(Core.theme.getComponentColor(0));
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+scale*2, "Resize");
                        }
                        if(addHeight)offsets[column] += scale*2;
                        break;
                    //</editor-fold>
                    case "resize":
                        //<editor-fold defaultstate="collapsed" desc="resize">
                        if(true){
                            Renderer.setColor(Core.theme.getComponentColor(0));
                            float s = scale*2;
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+s);//top
                            Renderer.fillRect(colLeft, offsets[column]+s*1.5f, colLeft+s*5, offsets[column]+s*3.5f);//top inner
                            Renderer.fillRect(colLeft, offsets[column]+s*3.5f, colLeft+s*2, offsets[column]+s*6.5f);//left
                            Renderer.fillRect(colLeft+s*5, offsets[column]+s*3.5f, colLeft+s*6, offsets[column]+s*6.5f);//right
                            Renderer.fillRect(colLeft+s*2, offsets[column]+s*6.5f, colLeft+s*5, offsets[column]+s*7.5f);//bottom inner
                            Renderer.fillRect(colLeft, offsets[column]+s*8, colRight, offsets[column]+s*9);//bottom
                            Renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+s);//top
                            Renderer.fillRect(colLeft, offsets[column]+s*8, colRight, offsets[column]+s*9);//bottom
                            Renderer.setColor(Core.theme.getAddButtonTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+s, "+");//top
                            Renderer.drawCenteredText(colLeft+s*2, offsets[column]+s*1.5f, colLeft+s*5, offsets[column]+s*2.5f, "+");//top inner
                            Renderer.drawCenteredText(colLeft, offsets[column]+s*4.5f, colLeft+s, offsets[column]+s*5.5f, "+");//left
                            Renderer.drawCenteredText(colLeft+s*5, offsets[column]+s*4.5f, colLeft+s*6, offsets[column]+s*5.5f, "+");//right
                            Renderer.drawCenteredText(colLeft+s*2, offsets[column]+s*6.5f, colLeft+s*5, offsets[column]+s*7.5f, "+");//bottom inner
                            Renderer.drawCenteredText(colLeft, offsets[column]+s*8, colRight, offsets[column]+s*9, "+");//bottom
                            Renderer.setColor(Core.theme.getDeleteButtonTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column]+s*1.5f, colLeft+s*2, offsets[column]+s*3.5f, "-");//top inner
                            Renderer.drawCenteredText(colLeft+s*2, offsets[column]+s*2.5f, colLeft+s*3, offsets[column]+s*3.5f, "-");//top 1
                            Renderer.drawCenteredText(colLeft+s*3, offsets[column]+s*2.5f, colLeft+s*4, offsets[column]+s*3.5f, "-");//top 2
                            Renderer.drawCenteredText(colLeft+s*4, offsets[column]+s*2.5f, colLeft+s*5, offsets[column]+s*3.5f, "-");//top 3
                            Renderer.drawCenteredText(colLeft+s, offsets[column]+s*3.5f, colLeft+s*2, offsets[column]+s*4.5f, "-");//left 1
                            Renderer.drawCenteredText(colLeft+s, offsets[column]+s*4.5f, colLeft+s*2, offsets[column]+s*5.5f, "-");//left 2
                            Renderer.drawCenteredText(colLeft+s, offsets[column]+s*5.5f, colLeft+s*2, offsets[column]+s*6.5f, "-");//left 3
                            Renderer.setColor(Core.theme.getEditorBackgroundColor());
                            Renderer.fillRect(colLeft+s*2, offsets[column]+s*3.5f, colLeft+s*5, offsets[column]+s*6.5f);
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            float border = s/32;
                            for(int x = 0; x<3; x++){
                                for(int y = 0; y<3; y++){
                                    float X = colLeft+s*(2+x);
                                    float Y = offsets[column]+s*(3.5f+y);
                                    Renderer.fillRect(X, Y, X+s, Y+border);
                                    Renderer.fillRect(X, Y+s-border, X+s, Y+s);
                                    Renderer.fillRect(X, Y+border, X+border, Y+s-border);
                                    Renderer.fillRect(X+s-border, Y+border, X+s, Y+s-border);
                                }
                            }
                        }
                        if(addHeight)offsets[column] += scale*18;
                        break;
                    //</editor-fold>
                    case "editor/header":
                        //<editor-fold defaultstate="collapsed" desc="editor/header">
                        if(true){
                            Renderer.setColor(Core.theme.getComponentColor(0));
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            Renderer.fillRect(colLeft+.25f, offsets[column], colRight-.25f, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column]+scale/2.5f, colLeft+.25f, offsets[column]+scale*2-scale/2.5f, "Done");
                            Renderer.drawCenteredText(colLeft+.25f, offsets[column]+scale/2.5f, colRight-.25f, offsets[column]+scale*2-scale/2.5f, "Tutorial Build v1 | Edit Metadata");
                            Renderer.drawCenteredText(colRight-.25f, offsets[column]+scale/2.5f, colRight, offsets[column]+scale*2-scale/2.5f, "Resize");
                        }
                        if(addHeight)offsets[column] += scale*2;
                        break;
                    //</editor-fold>
                    case "editor/metadata":
                        //<editor-fold defaultstate="collapsed" desc="editor/metadata">
                        if(true){
                            Renderer.setColor(Core.theme.getMetadataPanelHeaderColor());
                            //.4
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+scale*2, "Metadata");
                            String[][] texts = {{"Name", "Tutorial Build v1"}, {"Author", "tomdodd4598"}};
                            float w = (colRight-colLeft)/2;
                            float c = (colLeft+colRight)/2;
                            for(int x = 0; x<2; x++){
                                for(int y = 0; y<2; y++){
                                    float inset = .005f;
                                    Renderer.setColor(Core.theme.getTextBoxBorderColor());
                                    Renderer.fillRect(colLeft+w*x, offsets[column]+scale*2+scale*2*y, c+w*x, offsets[column]+scale*4+scale*2*y);
                                    Renderer.setColor(Core.theme.getTextBoxColor());
                                    Renderer.fillRect(colLeft+w*x+inset/2, offsets[column]+scale*2+scale*2*y+inset/2, c+w*x-inset/2, offsets[column]+scale*4+scale*2*y-inset/2);
                                    Renderer.setColor(Core.theme.getTutorialTextColor());
                                    Renderer.drawText(colLeft+w*x+inset, offsets[column]+scale*2+scale*2*y+inset, c+w*x-inset, offsets[column]+scale*4+scale*2*y-inset, texts[y][x]);
                                }
                            }
                        }
                        if(addHeight)offsets[column] += scale*6;
                        break;
                    //</editor-fold>
                    case "menu/header/metadata":
                        //<editor-fold defaultstate="collapsed" desc="menu/header/metadata">
                        if(true){
                            Renderer.setColor(Core.theme.getComponentColor(0));
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getComponentMouseoverColor(0), resonatingBrightness);
                            Renderer.fillRect(colLeft+scale*11.2f, offsets[column], colRight-scale*2, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column]+.015f, colLeft+scale*2.8f, offsets[column]+scale*2-.015f, "Import");
                            Renderer.drawCenteredText(colLeft+scale*2.8f, offsets[column]+.015f, colLeft+scale*2.8f*2, offsets[column]+scale*2-.015f, "Export");
                            Renderer.drawCenteredText(colLeft+scale*2.8f*2, offsets[column]+.015f, colLeft+scale*2.8f*3, offsets[column]+scale*2-.015f, "Save");
                            Renderer.drawCenteredText(colLeft+scale*2.8f*3, offsets[column]+.015f, colLeft+scale*2.8f*4, offsets[column]+scale*2-.015f, "Load");
                            Renderer.drawCenteredText(colLeft+scale*11.2f, offsets[column]+.01f, colRight-scale*2, offsets[column]+scale*2-.01f, "Tutorial Collection | Edit Metadata");
                            float siz = .05f;
                            Renderer.drawGear(.9f+siz/2, offsets[column]+siz/2, siz*.1f, 8, siz*.3f, siz*.1f, 360/16f);
                        }
                        if(addHeight)offsets[column] += scale*2;
                        break;
                    //</editor-fold>
                    case "menu/metadata":
                        //<editor-fold defaultstate="collapsed" desc="menu/metadata">
                        if(true){
                            Renderer.setColor(Core.theme.getMetadataPanelHeaderColor());
                            //.4
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column], colRight, offsets[column]+scale*2, "Metadata");
                            String[][] texts = {{"Name", "Tutorial Collection"}, {"Author", "tomdodd4598"}};
                            float w = (colRight-colLeft)/2;
                            float c = (colLeft+colRight)/2;
                            for(int x = 0; x<2; x++){
                                for(int y = 0; y<2; y++){
                                    float inset = .005f;
                                    Renderer.setColor(Core.theme.getTextBoxBorderColor());
                                    Renderer.fillRect(colLeft+w*x, offsets[column]+scale*2+scale*2*y, c+w*x, offsets[column]+scale*4+scale*2*y);
                                    Renderer.setColor(Core.theme.getTextBoxColor());
                                    Renderer.fillRect(colLeft+w*x+inset/2, offsets[column]+scale*2+scale*2*y+inset/2, c+w*x-inset/2, offsets[column]+scale*4+scale*2*y-inset/2);
                                    Renderer.setColor(Core.theme.getTutorialTextColor());
                                    Renderer.drawText(colLeft+w*x+inset, offsets[column]+scale*2+scale*2*y+inset, c+w*x-inset, offsets[column]+scale*4+scale*2*y-inset, texts[y][x]);
                                }
                            }
                        }
                        if(addHeight)offsets[column] += scale*6;
                        break;
                    //</editor-fold>
                    case "menu/file":
                        //<editor-fold defaultstate="collapsed" desc="menu/file">
                        if(true){
                            Renderer.setColor(Core.theme.getComponentColor(0));
                            Renderer.fillRect(colLeft, offsets[column], colRight, offsets[column]+scale*2);
                            float w = (colRight-colLeft)/4;
                            Renderer.setColor(Core.theme.getTutorialTextColor());
                            Renderer.drawCenteredText(colLeft, offsets[column]+.01f, colLeft+w, offsets[column]+scale*2-.01f, "Import");
                            Renderer.drawCenteredText(colLeft+w, offsets[column]+.01f, colLeft+w*2, offsets[column]+scale*2-.01f, "Export");
                            Renderer.drawCenteredText(colLeft+w*2, offsets[column]+.01f, colLeft+w*3, offsets[column]+scale*2-.01f, "Save");
                            Renderer.drawCenteredText(colLeft+w*3, offsets[column]+.01f, colRight, offsets[column]+scale*2-.01f, "Load");
                        }
                        if(addHeight)offsets[column] += scale*2;
                        break;
                    //</editor-fold>
                    case "editor":
                        //<editor-fold defaultstate="collapsed" desc="editor">
                        if(true){
                            Renderer.setColor(Core.theme.getTutorialBackgroundColor());
                            Renderer.drawImage(editorImage, colLeft, offsets[column], colRight, offsets[column]+scale*20);
                        }
                        if(addHeight)offsets[column] += scale*20;
                        break;
                    //</editor-fold>
                    case "editor/tool/pencil":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/pencil">
                        float scal = (colRight-colLeft)/pencil.width;
                        if(true){
                            float scale = (colRight-colLeft)/pencil.width;
                            Renderer.model(new Matrix4f().translate(colLeft, offsets[column], 0).scale(scale, scale, 1));
                            pencil.render2d(0);
                            Renderer.resetModelMatrix();
                        }
                        if(addHeight)offsets[column] += scal*pencil.height;
                        break;
                    //</editor-fold>
                    case "editor/tool/line":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/line">
                        scal = (colRight-colLeft)/line.width;
                        if(true){
                            float scale = (colRight-colLeft)/line.width;
                            Renderer.model(new Matrix4f().translate(colLeft, offsets[column], 0).scale(scale, scale, 1));
                            line.render2d(0);
                            Renderer.resetModelMatrix();
                        }
                        if(addHeight)offsets[column] += scal*line.height;
                        break;
                    //</editor-fold>
                    case "editor/tool/box":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/box">
                        scal = (colRight-colLeft)/box.width;
                        if(true){
                            float scale = (colRight-colLeft)/box.width;
                            Renderer.model(new Matrix4f().translate(colLeft, offsets[column], 0).scale(scale, scale, 1));
                            box.render2d(0);
                        }
                        if(addHeight)offsets[column] += scal*box.height;
                        break;
                    //</editor-fold>
                    case "editor/tool/select":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/select">
                        scal = (colRight-colLeft)/select.width;
                        if(true){
                            float scale = (colRight-colLeft)/select.width;
                            Renderer.model(new Matrix4f().translate(colLeft, offsets[column], 0).scale(scale, scale, 1));
                            select.render2d(0);
                        }
                        if(addHeight)offsets[column] += scal*select.height;
                        break;
                    //</editor-fold>
                    case "editor/tool/move":
                        //<editor-fold defaultstate="collapsed" desc="editor/tool/move">
                        scal = (colRight-colLeft)/move.width;
                        if(true){
                            float scale = (colRight-colLeft)/move.width;
                            Renderer.model(new Matrix4f().translate(colLeft, offsets[column], 0).scale(scale, scale, 1));
                            move.render2d(0);
                        }
                        if(addHeight)offsets[column] += scal*move.height;
                        break;
                    //</editor-fold>
                    default:
                        throw new IllegalArgumentException("unknown special: "+name);
                }
            }
        });
        */
        if(addHeight)skip();
    }
    public void skip(){
        add(new Component(){
            @Override
            public void draw(float resonatingBrightness, float frame){
                column++;
                if(column>=columns)column = 0;
            }
        });
    }
    private abstract class Component{
        public abstract void draw(float resonatingBrightness, float frame);
        public void tick(int tick){
        }
        public void preRender(){
        }
    }
}

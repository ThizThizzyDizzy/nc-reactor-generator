package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.legacy.LegacyGridLayout;
public class MenuLoad extends MenuLoadFile{
    public MenuLoad(GUI gui, Menu parent, Runnable onLoad){
        super(gui, parent, (ncpf) -> {
            new MenuLoadConfirm(gui, parent, ncpf, onLoad).open();
        });
        refresh();
    }
    @Override
    public void onOpened(){
        refresh();
    }
    private void refresh(){
        LegacyGridLayout layout = new LegacyGridLayout(36, 1);
        File root = new File("file").getAbsoluteFile().getParentFile();
        for(File file : root.listFiles()){
            String filename = file.getName();
            if(filename.contains(".ncpf")||filename.endsWith(".json")){
                Label mainLabel;
                mainLabel = layout.add(new Label(0, 0, 0, 36, filename, true){
                    Button del = add(new Button(0, 0, 64, height, "Del", true));
                    Button load = add(new Button(0, 0, 96, height, "Load", true));
                    {
                        del.addAction(() -> {
                            new MenuDialog(MenuLoad.this.gui, MenuLoad.this){
                                {
                                    textBox.setText("Delete "+filename+"?");
                                    addButton("Cancel", () -> {
                                        close();
                                    });
                                    addButton("Delete", () -> {
                                        try{
                                            Files.delete(file.toPath());
                                            close();
                                            MenuLoad.this.refresh();
                                        }catch(IOException ex){
                                            Core.error("Failed to delete file!", ex);
                                        }
                                    });
                                }
                            }.open();
                        });
                        load.addAction(() -> {
                            readFile(file);
                        });
                    }
                    @Override
                    public void render2d(double deltaTime){
                        del.x = width-del.width;
                        load.x = del.x-load.width;
                        super.render2d(deltaTime);
                    }
                    @Override
                    public void drawText(Renderer renderer){
                        float textLength = renderer.getStringWidth(text, height);
                        float scale = Math.min(1, (width-del.width-load.width-textInset*2)/textLength);
                        float textHeight = (int)((height-textInset*2)*scale)-4;
                        renderer.drawCenteredText(x, y+height/2-textHeight/2, x+width-del.width-load.width, y+height/2+textHeight/2, text);
                    }
                });
                layout.add(new Label(0, 0, 0, 0, "", true));
            }
        }
        maxWidth = 0.75f;
        layout.width = gui.getWidth()*2/5;
        setContent(layout);
    }
}
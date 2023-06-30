package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.file.LegacyNCPFHeader;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
public class MenuLoad extends MenuDialog{
    public MenuLoad(GUI gui, Menu parent){
        super(gui, parent);
        addButton("Cancel", () -> {
            close();
        });
        addButton("System File Chooser", () -> {
            try{
                Core.createFileChooser((file) -> {
                    Thread t = new Thread(() -> {
                        LegacyNCPFFile ncpf = FileReader.read(file);
                        if(ncpf==null)return;
                        Core.multiblocks.clear();
                        Core.saved = true;
                        Core.metadata.clear();
                        Core.metadata.putAll(ncpf.metadata);
                        if(ncpf.configuration==null||ncpf.configuration.isPartial()){
                            if(ncpf.configuration!=null&&!ncpf.configuration.name.equals(Core.configuration.name)){
                                Core.warning("File configuration '"+ncpf.configuration.name+"' does not match currently loaded configuration '"+Core.configuration.name+"'!", null);
                            }
                        }else{
                            Core.configuration = ncpf.configuration;
                        }
                        convertAndImportMultiblocks(ncpf.multiblocks);
                        close();
                    });
                    t.setDaemon(true);
                    t.start();
                }, FileFormat.ALL_PLANNER_FORMATS);
            }catch(IOException ex){
                Core.error("Failed to load file!", ex);
            }
        });
        refresh();
    }
    @Override
    public void onOpened(){
        refresh();
    }
    private void refresh(){
        GridLayout layout = new GridLayout(36, 1);
        File root = new File("file").getAbsoluteFile().getParentFile();
        for(File file : root.listFiles()){
            String filename = file.getName();
            if(filename.endsWith(".ncpf")||filename.endsWith(".json")){
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
                            Thread t = new Thread(() -> {
                                LegacyNCPFFile ncpf = FileReader.read(file);
                                if(ncpf==null)return;
                                Core.multiblocks.clear();
                                Core.saved = true;
                                Core.metadata.clear();
                                Core.metadata.putAll(ncpf.metadata);
                                if(ncpf.configuration==null||ncpf.configuration.isPartial()){
                                    if(ncpf.configuration!=null&&!ncpf.configuration.name.equals(Core.configuration.name)){
                                        Core.warning("File configuration '"+ncpf.configuration.name+"' does not match currently loaded configuration '"+Core.configuration.name+"'!", null);
                                    }
                                }else{
                                    Core.configuration = ncpf.configuration;
                                }
                                convertAndImportMultiblocks(ncpf.multiblocks);
                                close();
                            });
                            t.setDaemon(true);
                            t.start();
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
                try{
                    LegacyNCPFHeader header = FileReader.readHeader(file);
                    if(header!=null){
                        GridLayout l2 = new GridLayout(36, 3);
                        if(header.metadata!=null){
                            String actualName = header.metadata.get("Name");
                            if(actualName!=null&&!actualName.isEmpty()){
                                mainLabel.text = actualName;
                            }
                        }
                        l2.add(new Label(0, 0, 0, 0, filename, true));
                        l2.add(new Label(0, 0, 0, 0, header.multiblocks+" Multiblock"+(header.multiblocks==1?"":"s"), true));
                        l2.add(new Label(0, 0, 0, 0, "NCPF "+header.version, true));
                        layout.add(l2);
                    }
                }catch(Exception ex){}
                layout.add(new Label(0, 0, 0, 0, "", true));
            }
        }
        maxWidth = 0.75f;
        layout.width = gui.getWidth()*2/5;
        setContent(layout);
    }
    private void convertAndImportMultiblocks(ArrayList<Multiblock> multiblocks){
        for(Multiblock mb : multiblocks){
            try{
                mb.convertTo(Core.configuration);
            }catch(MissingConfigurationEntryException ex){
                Core.warning("Failed to load multiblock - Are you missing an addon?", ex);
                continue;
            }
            Core.multiblocks.add(mb);
        }
    }
}
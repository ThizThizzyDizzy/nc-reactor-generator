package net.ncplanner.plannerator.planner.file;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
import org.lwjgl.glfw.GLFW;
public abstract class StringFormatWriter extends FormatWriter{
    @Override
    public FileFormat getFileFormat(){
        return FileFormat.BG_STRING;
    }
    @Override
    public void write(NCPFFile ncpf, OutputStream stream){
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream))){
            writer.write(write(ncpf));
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public abstract String write(NCPFFile ncpf);
    @Override
    public void openExportSettings(NCPFFile ncpf, Runnable onExport){
        new MenuMessageDialog(Core.gui, Core.gui.menu, "Export String").addButton("Copy String", () -> {
            GLFW.glfwSetClipboardString(Core.window, write(ncpf));
        }, true).addButton("Export as file", onExport, true).addButton("Cancel", true).open();
    }
}
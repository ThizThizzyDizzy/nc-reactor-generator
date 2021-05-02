package planner.file;
import discord.Bot;
import simplelibrary.image.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import multiblock.Block;
import multiblock.BoundingBox;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.FormattedText;
import planner.Main;
import planner.exception.MissingConfigurationEntryException;
import planner.file.writer.HellrageFormatWriter;
import planner.file.writer.NCPFFormatWriter;
import planner.file.writer.PNGFormatWriter;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class FileWriter{
    public static final ArrayList<FormatWriter> formats = new ArrayList<>();
    public static boolean botRunning;
    public static FormatWriter NCPF,HELLRAGE;
    public static ImageFormatWriter PNG;
    static{
        formats.add(HELLRAGE = new HellrageFormatWriter());
        formats.add(NCPF = new NCPFFormatWriter());
        formats.add(PNG = new PNGFormatWriter());
    }
    public static void write(NCPFFile ncpf, OutputStream stream, FormatWriter format){
        format.write(ncpf, stream);
    }
    public static void write(NCPFFile ncpf, File file, FormatWriter format){
        if(file.exists())file.delete();
        try{
            file.createNewFile();
            write(ncpf, new FileOutputStream(file), format);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
}
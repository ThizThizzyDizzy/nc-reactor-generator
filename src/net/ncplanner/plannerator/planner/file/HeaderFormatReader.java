package net.ncplanner.plannerator.planner.file;
import java.io.InputStream;
public interface HeaderFormatReader extends FormatReader{
    public NCPFHeader readHeader(InputStream stream);
}
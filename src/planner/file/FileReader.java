package planner.file;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import planner.file.reader.NCPF10Reader;
import planner.file.reader.NCPF1Reader;
import planner.file.reader.NCPF2Reader;
import planner.file.reader.NCPF3Reader;
import planner.file.reader.NCPF4Reader;
import planner.file.reader.NCPF5Reader;
import planner.file.reader.NCPF6Reader;
import planner.file.reader.NCPF7Reader;
import planner.file.reader.NCPF8Reader;
import planner.file.reader.NCPF9Reader;
import planner.file.reader.OverhaulHellrageMSR1Reader;
import planner.file.reader.OverhaulHellrageMSR2Reader;
import planner.file.reader.OverhaulHellrageMSR3Reader;
import planner.file.reader.OverhaulHellrageMSR4Reader;
import planner.file.reader.OverhaulHellrageMSR5Reader;
import planner.file.reader.OverhaulHellrageMSR6Reader;
import planner.file.reader.OverhaulHellrageSFR1Reader;
import planner.file.reader.OverhaulHellrageSFR2Reader;
import planner.file.reader.OverhaulHellrageSFR3Reader;
import planner.file.reader.OverhaulHellrageSFR4Reader;
import planner.file.reader.OverhaulHellrageSFR5Reader;
import planner.file.reader.OverhaulHellrageSFR6Reader;
import planner.file.reader.OverhaulNCConfigReader;
import planner.file.reader.UnderhaulHellrage1Reader;
import planner.file.reader.UnderhaulHellrage2Reader;
import planner.file.reader.UnderhaulNCConfigReader;
public class FileReader{
    public static final ArrayList<FormatReader> formats = new ArrayList<>();
    static{
        formats.add(new UnderhaulNCConfigReader());// UNDERHAUL nuclearcraft.cfg
        formats.add(new OverhaulNCConfigReader());// OVERHAUL nuclearcraft.cfg
        formats.add(new UnderhaulHellrage1Reader());// hellrage .json 1.2.5-1.2.22
        formats.add(new UnderhaulHellrage2Reader());// hellrage .json 1.2.23-1.2.25 (present)
        formats.add(new OverhaulHellrageSFR1Reader());// hellrage SFR .json 2.0.1-2.0.6
        formats.add(new OverhaulHellrageSFR2Reader());// hellrage SFR .json 2.0.7-2.0.29
        formats.add(new OverhaulHellrageSFR3Reader());// hellrage SFR .json 2.0.30
        formats.add(new OverhaulHellrageSFR4Reader());// hellrage SFR .json 2.0.31
        formats.add(new OverhaulHellrageSFR5Reader());// hellrage SFR .json 2.0.32-2.0.37
        formats.add(new OverhaulHellrageSFR6Reader());// hellrage SFR .json 2.1.1-2.1.7 (present)
        formats.add(new OverhaulHellrageMSR1Reader());// hellrage MSR .json 2.0.1-2.0.6
        formats.add(new OverhaulHellrageMSR2Reader());// hellrage MSR .json 2.0.7-2.0.29
        formats.add(new OverhaulHellrageMSR3Reader());// hellrage MSR .json 2.0.30
        formats.add(new OverhaulHellrageMSR4Reader());// hellrage MSR .json 2.0.31
        formats.add(new OverhaulHellrageMSR5Reader());// hellrage MSR .json 2.0.32-2.0.37
        formats.add(new OverhaulHellrageMSR6Reader());// hellrage MSR .json 2.1.1-2.1.7 (present)
        formats.add(new NCPF1Reader());
        formats.add(new NCPF2Reader());
        formats.add(new NCPF3Reader());
        formats.add(new NCPF4Reader());
        formats.add(new NCPF5Reader());
        formats.add(new NCPF6Reader());
        formats.add(new NCPF7Reader());
        formats.add(new NCPF8Reader());
        formats.add(new NCPF9Reader());
        formats.add(new NCPF10Reader());
    }
    public static NCPFFile read(InputStreamProvider provider){
        for(FormatReader reader : formats){
            boolean matches = false;
            try{
                if(reader.formatMatches(provider.getInputStream()))matches = true;
            }catch(Throwable t){}
            if(matches)return reader.read(provider.getInputStream());
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
    public static NCPFFile read(File file){
        return read(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        });
    }
}
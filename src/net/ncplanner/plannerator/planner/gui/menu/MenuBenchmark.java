package net.ncplanner.plannerator.planner.gui.menu;
import java.io.File;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.CompiledUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
public class MenuBenchmark extends Menu{
    private final Task task;
    private final ProgressBar progressBar;
    public MenuBenchmark(GUI gui){
        super(gui, null);
        File f = new File("benchmark.ncpf");
        if(!f.exists())f = new File("benchmark.json");
        task = new Task("Running Benchmark");
        Task load = task.addSubtask(f.exists()?"Loading "+f.getName():"Benchmark not found");
        Task benchmark = task.addSubtask("Benchmarking");
        progressBar = add(new ProgressBar(0, 0, gui.getWidth(), gui.getHeight(), 3){
            @Override
            public Task getTask(){
                return task;
            }
        });
        File file = f;
        if(f==null)return;
        Thread thread = new Thread(() -> {
            NCPFFile ncpf = FileReader.read(file);
            if(ncpf==null)return;
            Core.multiblocks.clear();
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
            load.finish();
            Multiblock multiblock = Core.multiblocks.get(0);
            if(multiblock instanceof UnderhaulSFR){
                System.out.println("Commencing test...");
                UnderhaulSFR sfr = (UnderhaulSFR)multiblock;
                long duration = 60_000_000_000l;
                long now = System.nanoTime();
                int count = 0;
//                    while(System.nanoTime()-duration<now){
//                        sfr.recalculate();
//                        count++;
//                    }
//                    System.out.println("V3 clc: "+count);
//                    now = System.nanoTime();
//                    count = 0;
//                    while(System.nanoTime()-duration<now){
//                        Multiblock v = sfr.copy();
//                        v.recalculate();
//                        count++;
//                    }
//                    System.out.println("V3 cp/clc: "+count);
                CompiledUnderhaulSFRConfiguration compiledConfig = CompiledUnderhaulSFRConfiguration.compile(sfr.configuration.underhaul.fissionSFR);
                LiteUnderhaulSFR lite = new LiteUnderhaulSFR(compiledConfig);
                lite.importAndConvert(sfr);
                lite.calculate();
                System.out.println(lite.getTooltip());
                now = System.nanoTime();
                count = 0;
//                while(System.nanoTime()-duration<now){
//                    lite.calculate();
//                    count++;
//                }
//                System.out.println("V4 Lite clc: "+count);
                now = System.nanoTime();
                count = 0;
                while(System.nanoTime()-duration<now){
                    LiteUnderhaulSFR liteCopy = new LiteUnderhaulSFR(compiledConfig);
                    liteCopy.fuel = lite.fuel;
                    liteCopy.dims[0] = lite.dims[0];
                    liteCopy.dims[1] = lite.dims[1];
                    liteCopy.dims[2] = lite.dims[2];
                    for(int x = 0; x<lite.dims[0]; x++){
                        for(int y = 0; y<lite.dims[1]; y++){
                            for(int z = 0; z<lite.dims[2]; z++){
                                liteCopy.blocks[x][y][z] = lite.blocks[x][y][z];
                            }
                        }
                    }
                    liteCopy.calculate();
                    count++;
                    double secs = Math.max(1,(System.nanoTime()-now)/1000000000d);
                    benchmark.progress = (System.nanoTime()-now)/(double)duration;
                    task.name = "Running Benchmark ("+count+" | "+Math.round(count/secs)+"/sec)";
                }
                System.out.println("V4 Lite cp/clc: "+count);
            }
            benchmark.finish();
        }, "Benchmark Thread");
        thread.setDaemon(true);
        thread.start();
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
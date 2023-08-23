package net.ncplanner.plannerator.planner.gui.menu;
import java.io.File;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.CompiledUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.LayoutMenu;
import net.ncplanner.plannerator.planner.gui.menu.component.BenchmarkComponent;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
public class MenuBenchmark extends LayoutMenu{
    private Task task;
    private final ProgressBar bar;
    private final GridLayout grid;
    private BenchmarkComponent benchmark;
    public MenuBenchmark(GUI gui){
        super(gui, new SplitLayout(SplitLayout.Y_AXIS, 0));
        File f = new File("benchmark.ncpf.json");
        if(!f.exists())f = new File("benchmark.ncpf");
        if(!f.exists())f = new File("benchmark.json");
        task = new Task(f.exists()?"Loading "+f.getName():"Benchmark not found");
        bar = add(new ProgressBar(3){
            @Override
            public Task getTask(){
                return task;
            }
        });
        grid = add(new GridLayout(1, 0));
        File file = f;
        Thread thread = new Thread(() -> {
            Project ncpf = FileReader.read(file);
            if(ncpf==null)return;
            Core.multiblocks.clear();
            Core.saved = true;
            Core.setConfiguration(new Configuration(ncpf));
            for(Design d : ncpf.designs){
                d.file = Core.project;
                if(d instanceof MultiblockDesign){
                    ((MultiblockDesign)d).convertElements();
                    Core.multiblocks.add(((MultiblockDesign)d).toMultiblock());
                }
            }
            task.finish();
            while(true){
                Multiblock multiblock = Core.multiblocks.get(0);
                switch(new MenuMessageDialog("Choose Benchmark").addButton("V3").addButton("V4 (Lite)").addButton("Done").openAsync()){
                    case 0:
                        benchmark(new String[]{"V3 stp/clc", "V3 clc", "V3 cp/clc"}, ()->{
                            multiblock.clearCaches();
                            ArrayList<AbstractBlock> blocks = multiblock.getBlocks();
                            multiblock.clearData(blocks);
                            multiblock.validate();
                            {
                                multiblock.calculateTask = new Task("Calculating Multiblock");
                                multiblock.genCalcSubtasks();
                                while(multiblock.doCalculationStep(blocks, true))multiblock.decals.clear();
                                multiblock.calculationPaused = false;
                                for(net.ncplanner.plannerator.planner.module.Module m : Core.modules){
                                    if(m.isActive()){
                                        Object result = m.calculateMultiblock(multiblock);
                                        if(result!=null)multiblock.moduleData.put(m, result);
                                    }
                                }
                                multiblock.calculateTask = null;
                            }
                            multiblock.calculate(blocks);
                        }, multiblock::recalculate, ()->{
                            Multiblock v = multiblock.copy();
                            v.recalculate();
                        });
                        break;
                    case 1: //lite
                        if(multiblock instanceof UnderhaulSFR){
                            System.out.println("Commencing test...");
                            LiteUnderhaulSFR lite = ((UnderhaulSFR)multiblock).compile();
                            CompiledUnderhaulSFRConfiguration compiledConfig = lite.configuration;
                            lite.calculate();
                            System.out.println(lite.getTooltip());
                            benchmark(new String[]{"V4 Lite clc", "V4 Lite cp/clc"}, lite::calculate, ()->{
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
                            });
                        }else new MenuMessageDialog("No lite model exists for "+multiblock.getDefinitionName()+"!").openAsync();
                        break;
                    case 2:
                        gui.open(new MenuMain(gui));
                        return;
                }
            }
        }, "Benchmark Thread");
        thread.setDaemon(true);
        thread.start();
    }
    @Override
    public void render2d(double deltaTime){
        ((SplitLayout)layout).minSize1 = bar.getTaskHeight();
        super.render2d(deltaTime);
    }
    public void benchmark(String[] name, Runnable... r){
        if(name.length!=r.length)throw new IllegalArgumentException("Length of names and benchmark arrays must match!");
        task = new Task("Benchmarking");
        Task[] tasks = new Task[r.length];
        if(benchmark==null)benchmark = grid.add(new BenchmarkComponent());
        for(int i = 0; i<r.length; i++){
            tasks[i] = task.addSubtask(name[i]);
        }
        for(int i = 0; i<r.length; i++){
            benchmark(name[i], r[i], tasks[i]);
            tasks[i].finish();
        }
    }
    public long benchmark(String name, Runnable r, Task task){
        long duration = 60_000_000_000l;
        long now = System.nanoTime();
        long count = 0;
        benchmark.addBenchmark(name);
        double thresh = 0.1;//log time per this many seconds
        double logged = 0;
        while(System.nanoTime()-duration<now){
            r.run();
            count++;
            double secs = (System.nanoTime()-now)/1000000000d;
            if(secs>logged+thresh){
                benchmark.log(count);
                logged+= thresh;
            }
            task.progress = (System.nanoTime()-now)/(double)duration;
            task.name = "Running Benchmark: "+name+" ("+count+" | "+Math.round(count/Math.max(1,secs))+"/sec)";
        }
        return count;
    }
}
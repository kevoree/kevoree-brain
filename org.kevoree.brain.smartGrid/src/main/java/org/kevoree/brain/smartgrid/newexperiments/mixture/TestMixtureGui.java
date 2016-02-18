package org.kevoree.brain.smartgrid.newexperiments.mixture;


import org.kevoree.brain.smartgrid.util.CsvLoader;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;
import org.math.plot.Plot3DPanel;
import org.math.plot.plotObjects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by assaad on 19/02/15.
 */
public class TestMixtureGui extends JFrame implements PropertyChangeListener {

    // executes in event dispatch thread
    public void propertyChange(PropertyChangeEvent event) {
        // if the operation is finished or has been canceled by
        // the user, take appropriate action
        if (progressMonitor.isCanceled()) {
            operation.cancel(true);
        } else if (event.getPropertyName().equals("progress")) {
            // get the % complete from the progress event
            // and set it on the progress monitor
            int progress = ((Integer)event.getNewValue()).intValue();
            progressMonitor.setProgress(progress);
        }
    }

    class Calculate extends SwingWorker<Plot3DPanel, String> implements ProgressReport {
        private final int num;
        private long starttime;
        private long endtime;

        public Calculate(int num){
            lock=false;
            this.num=Math.min(num,data.size()-loc);
        }
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Plot3DPanel doInBackground() {
            starttime=System.nanoTime();
            if (loc < data.size()&&num>0) {
                publish("Processing "+num+" values started...");
                double[] temp = new double[2];
                for (int i = 0; i < num; i++) {
                    this.setProgress(i*50/num);
                    if(isCancelled()){
                        return null;
                    }
                    if (loc < data.size()) {
                        ElectricMeasure em = data.get(loc);
                        temp[0] = em.getDoubleTime();
                        temp[1] = em.aplus;
                        mm.feed(temp);
                        loc++;
                    } else {
                        break;
                    }
                }
                publish("Processing done in "+getTime()+", generating 3D plot...");

                	/*
		 * Plot the distribution estimated by the sample model
		 */

                double xmax=24;
                double ymax=1000;
                double[][] zArray;




                // first create a 100x100 grid
                double[] xArray = new double[100];
                double[] yArray = new double[100];

                double zmax=Double.MIN_VALUE;


                if(mm==null){
                    for (int i = 0; i < 100; i++) {
                        xArray[i] = i * 0.01*xmax;
                        yArray[i] = i * 0.01*ymax;
                    }
                    zArray=new double[yArray.length][xArray.length];
                }
                else {

                    if (mm.getMax() != null) {
                        ymax = Math.max(mm.getMax()[1] * 1.2, ymax);
                    }
                    for (int i = 0; i < 100; i++) {
                        xArray[i] = i * 0.01*xmax;
                        yArray[i] = i * 0.01*ymax;
                    }

                    double[][] featArray=new double[(xArray.length*yArray.length)][2];
                    int count=0;
                    for (int i = 0; i < xArray.length; i++) {
                        for (int j = 0; j < yArray.length; j++) {
                            double[] point = {xArray[i], yArray[j]};
                            featArray[count]=point;
                            count++;
                        }
                    }


                    //this.setProgress(50+i*50/xArray.length);

                    double[] z= mm.calculateArray(featArray,this);
                    if(isCancelled()){
                        return null;
                    }
                    zArray= new double[xArray.length][yArray.length];
                    count=0;
                    for (int i = 0; i < xArray.length; i++) {
                        for (int j = 0; j < yArray.length; j++) {
                            zArray[j][i]=z[count];
                            if(zArray[j][i]>zmax){
                                zmax=zArray[j][i];
                            }
                            count++;
                        }
                    }
                    if(zmax==0){
                        zmax=1;
                    }
                }

                Plot3DPanel plot = emptyPlot();

                // add grid plot to the PlotPanel
                plot.addGridPlot("Electric consumption probability distribution", xArray, yArray,
                        zArray);

                plot.setFixedBounds(0,0,xmax);
                plot.setFixedBounds(1,0,ymax);
                plot.setFixedBounds(2,0,zmax);
                return plot;

            }
            return null;
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            if(isCancelled()) { return; }

            for(String s: chunks){
                progressMonitor.setNote(s);
            }
        }
        /*
         * Executed in event dispatching thread
         */

        private String getTime(){
            endtime=System.nanoTime();
            double v=endtime-starttime;
            String s;
            NumberFormat formatter = new DecimalFormat("#0.00");
            if(v>1000000000){
                v=v/1000000000;
                s=formatter.format(v)+" s";
            }
            else if(v>1000000){
                v=v/1000000;
                s=formatter.format(v)+" ms";
            }
            else{
                s=v + " ns";
            }
            return s;
        }

        @Override
        public void done() {
            lock=true;
            try {
                if(!this.isCancelled()) {
                    publish("Processing done in "+getTime());
                    Plot3DPanel pd = get();
                    spUp.setLeftComponent(pd);
                    spUp.setDividerLocation(getWidth() - 300);
                    progressMonitor.close();
                }
                elecStat.setText("Electrical Values loaded: "+((int)mm.getWeight()));
               // compStat.setText("Number of components: "+mm.totalComponents());
                topStat.setText("Top level components: "+mm.getTopLevelComp());
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public void updateProgress(double progress) {
            this.setProgress((int)(progress*50+50));
        }


    }


    public TestMixtureGui() {
        initUI();
    }



    private Plot3DPanel emptyPlot(){
        Plot3DPanel plot = new Plot3DPanel("SOUTH");

        plot.setAxisLabel(0,"Time");
        plot.setAxisLabel(1,"Electric load");
        plot.setAxisLabel(2,"Probability");
        return plot;
    }

    protected JLabel elecStat;
   // protected JLabel compStat;
    protected JLabel topStat;
    protected JSplitPane spUp;
    protected JPanel board;
    protected boolean lock=true;
    private void initUI() {

        // some configuration for plotting
        setTitle("Smart Grid consumption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        board=new JPanel(new FlowLayout(FlowLayout.LEFT));
        board.setPreferredSize(new Dimension(300,850));
        board.setMaximumSize(new Dimension(350,1000));
        spUp= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JPanel(),board);
        spUp.setContinuousLayout(true);
        spUp.setDividerLocation(this.getWidth()-300);

      /*  this.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent evt) {
                spUp.setDividerLocation(getWidth()-300);
            }
        });*/

        elecStat=new JLabel();
        board.add(elecStat);

        //compStat=new JLabel();
       // board.add(compStat);

        topStat=new JLabel();
        board.add(topStat);

        getContentPane().add(spUp, BorderLayout.CENTER);



        setSize(1400, 850);
        setLocationRelativeTo(null);



        menu();
        Plot3DPanel pp = emptyPlot();
        pp.addGridPlot("Electric consumption probability distribution",new double[]{0,24},new double[]{0,1000},new double[][]{{0,0},{0,0}});
        pp.setFixedBounds(0,0,24);
        pp.setFixedBounds(1,0,1000);
        pp.setFixedBounds(2,0,1);
        spUp.setLeftComponent(pp);
        spUp.setDividerLocation(getWidth()-300);
    }


    private int loc=0;
    ArrayList<ElectricMeasure> data;
    private MixtureModel mm;
    private ProgressMonitor progressMonitor;
    private Calculate operation;
    private void feed(final int num){
        if(data!=null) {

            if(lock) {
                lock = false;
                progressMonitor = new ProgressMonitor(this,"Loading values...", "", 0, 100);
                operation = new Calculate(num);
                operation.addPropertyChangeListener(this);
                operation.execute();
            }
            else{
                JOptionPane.showMessageDialog(null, "Please wait till the first process is done", "Process not finished yet", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Please open a user csv file first", "No user data loaded", JOptionPane.ERROR_MESSAGE);
        }

    }



    private void dataInit() {

        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                data = CsvLoader.loadFile(file.getAbsolutePath());
                JOptionPane.showMessageDialog(null, "Loaded " + data.size() + " measures", "Loading successful", JOptionPane.INFORMATION_MESSAGE);
                int feat = 2;

                double[] err = new double[feat];
                err[0] = 0.25;
                err[1] = 30;

                Configration configration = new Configration();
                configration.capacity = 96;
                configration.compression = 96*5;
                configration.kmeanIterations = 30;
                configration.err = err;
                configration.maxLevels = 3;
                configration.calcLevel = 3;
                mm = new MixtureModel(configration);
            }
            catch (Exception ex){
                data=null;
                JOptionPane.showMessageDialog(null, "Could not load the file ", "Loading failed", JOptionPane.ERROR_MESSAGE);
            }
        }

    }


    
    private void menu() {

//Where the GUI is created:
        JMenuBar menuBar;
        JMenu menu, filemenu;
        JMenuItem menuItem;

//Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        filemenu = new JMenu("File");
        filemenu.setMnemonic(KeyEvent.VK_I);
        filemenu.getAccessibleContext().setAccessibleDescription(
                "File");
        menuBar.add(filemenu);

        menuItem = new JMenuItem("Open",
                KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open user Csv file");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataInit();
            }
        });

        filemenu.add(menuItem);

//Build the first menu.
        menu = new JMenu("Electric Load");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "Electric load menu");
        menuBar.add(menu);

//a group of JMenuItems
        menuItem = new JMenuItem("Load One Value",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load next electric consumption");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(1);
            }
        });

        menu.add(menuItem);

        menuItem = new JMenuItem("Load 100 Values",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load 100 electric consumptions values");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(100);
            }
        });

        menu.add(menuItem);

        menuItem = new JMenuItem("Load 1000 Values",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_3,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load 1000 electric consumptions values");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(1000);
            }
        });

        menu.add(menuItem);

        menuItem = new JMenuItem("Load all Values",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_4,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load all electric consumptions values of this client");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(data.size());
            }
        });

        menu.add(menuItem);



        setJMenuBar(menuBar);


    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                TestMixtureGui ps = new TestMixtureGui();
                ps.setVisible(true);
            }
        });
    }



}

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;


public class DataTimeSeries_AWT <T> extends ApplicationFrame {
    public DataTimeSeries_AWT(String title, ArrayList<T> arrayList, ArrayList<T> arrayListSV,String type) {
        super(title);
        final XYDataset dataset = createDataset(arrayList,arrayListSV,type);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1536 , 512));
        chartPanel.setMouseZoomable(true , false);
        setContentPane(chartPanel);
    }

    private XYDataset createDataset(ArrayList<T> arrayList,ArrayList<T> arrayListSV,String type) {

        final TimeSeries series = new TimeSeries("Random Data");
     //   final TimeSeries series1 = new TimeSeries("Random Data");
//        final TimeSeries series1 = new TimeSeries("Random Data");

       // Millisecond current = new Millisecond();
//140042.000
        PeriodUpdateMillisecond current = new PeriodUpdateMillisecond(0,37,50,6,10,10,2022,100);
       // PeriodUpdateMillisecond current1 = new PeriodUpdateMillisecond(0,10,47,5,10,10,2022,1000);
       // PeriodUpdateMillisecond current2 = new PeriodUpdateMillisecond(0,10,10,10,10,10,2022,1000);
        // PeriodUpdateMillisecond current = new PeriodUpdateMillisecond();
       // current.setPeriodTimeMs(500);

        for (int i = 0; i < arrayList.size(); i++) {
            try {

                switch (type){
                    case "double":
                        double a = (Double) arrayList.get(i);
                        series.add(current,a);
                        break;
                    case "int":
                        int b = (Integer) arrayList.get(i);
                        series.add(current,b);
                        break;
                }
              //  System.out.println(a);

                current = (PeriodUpdateMillisecond) current.next();
            }
            catch (SeriesException e) {
                System.err.println("Error adding to series");
            }
        }

//        for (int i = 0; i < arrayListSV.size(); i++) {
//            try {
//                int a = (Integer) arrayListSV.get(i);
//                //  System.out.println(a);
//                series1.add(current1,a);
//                current1 = (PeriodUpdateMillisecond) current1.next();
//            }
//            catch (SeriesException e) {
//                System.err.println("Error adding to series");
//            }
//        }

//        for (int i = 0; i < arrayList.size(); i++) {
//            try {
//                series1.add(current2, arrayList.get(i) + 10);
//                current2 = (PeriodUpdateMillisecond) current2.next();
//            }
//            catch ( SeriesException e ) {
//                System.err.println("Error adding to series");
//            }
//        }

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        timeSeriesCollection.addSeries(series);
 //       timeSeriesCollection.addSeries(series1);

        return timeSeriesCollection;
    }

    private JFreeChart createChart(final XYDataset dataset ) {
        return ChartFactory.createTimeSeriesChart(
                "Speed Trend Chart",
                "UTC-TIME",
                "Speed",
                dataset,
                false,
                false,
                false);
    }



    public static void main(String[] args) {

        final String title = "Time Series Management";
       // final DataTimeSeries_AWT demo = new DataTimeSeries_AWT( title );
      //  demo.pack( );
        //RefineryUtilities.positionFrameRandomly( demo );
     //   demo.setVisible( true );
    }

}

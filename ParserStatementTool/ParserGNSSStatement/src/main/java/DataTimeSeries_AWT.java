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
    public DataTimeSeries_AWT(String title, ArrayList<T> arrayList) {
        super(title);
        final XYDataset dataset = createDataset(arrayList);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1536 , 512));
        chartPanel.setMouseZoomable(true , false);
        setContentPane(chartPanel);
    }

    private XYDataset createDataset(ArrayList<T> arrayList) {

        final TimeSeries series = new TimeSeries("Random Data");
//        final TimeSeries series1 = new TimeSeries("Random Data");

       // Millisecond current = new Millisecond();

        PeriodUpdateMillisecond current = new PeriodUpdateMillisecond(0,3,50,2,10,10,2022,100);
      //  PeriodUpdateMillisecond current2 = new PeriodUpdateMillisecond(0,10,10,10,10,10,2022,1000);
        //PeriodUpdateMillisecond current = new PeriodUpdateMillisecond();
       // current.setPeriodTimeMs(500);

        for (int i = 0; i < arrayList.size(); i++) {
            try {

                double a = (Double) arrayList.get(i);
              //  System.out.println(a);
                series.add(current,a);
                current = (PeriodUpdateMillisecond) current.next();
            }
            catch (SeriesException e) {
                System.err.println("Error adding to series");
            }
        }

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
//        timeSeriesCollection.addSeries(series1);

        return timeSeriesCollection;
    }

    private JFreeChart createChart(final XYDataset dataset ) {
        return ChartFactory.createTimeSeriesChart(
                "WheelSpeed",
                "UTC-TIME",
                "Value-VEH",
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

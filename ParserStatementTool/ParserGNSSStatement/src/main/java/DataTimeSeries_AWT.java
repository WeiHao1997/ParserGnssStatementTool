import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import java.util.Date;


public class DataTimeSeries_AWT extends ApplicationFrame {
    public DataTimeSeries_AWT(String title) {
        super(title);
        final XYDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize( new java.awt.Dimension(1536 , 512));
        chartPanel.setMouseZoomable( true , false );
        setContentPane( chartPanel );
    }

    private XYDataset createDataset() {
        final TimeSeries series = new TimeSeries("Random Data");

       // Millisecond current = new Millisecond();

        PeriodUpdateMillisecond current = new PeriodUpdateMillisecond(0,10,10,10,10,10,2022,500);
        //PeriodUpdateMillisecond current = new PeriodUpdateMillisecond();
       // current.setPeriodTimeMs(500);

        double value = 100.0;
        for (int i = 0; i < 3600 * 1000; i++) {
            try {
                value = value + Math.random() - 0.5;
                series.add(current, new Double( value ) );
                current = (PeriodUpdateMillisecond) current.next();
            }
            catch ( SeriesException e ) {
                System.err.println("Error adding to series");
            }
        }
        return new TimeSeriesCollection(series);
    }

    private JFreeChart createChart(final XYDataset dataset ) {
        return ChartFactory.createTimeSeriesChart(
                "Computing Test",
                "UTC-TIME",
                "Value-CN/0",
                dataset,
                false,
                false,
                false);
    }



    public static void main(String[] args) {

        final String title = "Time Series Management";
        final DataTimeSeries_AWT demo = new DataTimeSeries_AWT( title );
        demo.pack( );
        //RefineryUtilities.positionFrameRandomly( demo );
        demo.setVisible( true );
    }

}

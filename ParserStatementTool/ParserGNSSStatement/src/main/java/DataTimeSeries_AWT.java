import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
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


//        series.addOrUpdate(new Hour().next(),10);
//        series.addOrUpdate(new Hour().next(),101);
//        series.addOrUpdate(new Hour().next(),120);
//        series.addOrUpdate(new Hour().next(),130);
//        series.addOrUpdate(new Hour().next(),140);
//        series.addOrUpdate(new Hour().next(),150);




        Millisecond current = new Millisecond(10,0,0,8,8,12,2022);

        System.out.println(current);
        System.out.println(current);

        double value = 100.0;
        for (int i = 0; i < 3600 * 24; i++) {
            try {
                value = value + Math.random() - 0.5;
                series.add(current, new Double( value ) );
                current = (Millisecond) current.next();
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

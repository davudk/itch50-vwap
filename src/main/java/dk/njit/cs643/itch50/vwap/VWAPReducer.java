package dk.njit.cs643.itch50.vwap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class VWAPReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
    private static Logger logger = LoggerFactory.getLogger(VWAPMapper.class);

    @Override
    public void reduce(Text key,
                       Iterator<Text> values,
                       OutputCollector<Text, Text> output,
                       Reporter reporter) throws IOException {
        logger.info("Reducing: {}", key);

        double totalVolumePrice = 0;
        double totalVolume = 0;
        while (values.hasNext()) {
            final String[] parts = values.next().toString().split(",");
            final int volume = Integer.parseInt(parts[0]);
            final double price = Double.parseDouble(parts[1]);
            final double volumePrice = volume * price;

            totalVolumePrice += volumePrice;
            totalVolume += volume;
        }

        final double vwap = totalVolumePrice / totalVolume;

        logger.info("Reduced: {} as: {}", key, vwap);

        output.collect(key, new Text(String.valueOf(vwap)));
    }
}

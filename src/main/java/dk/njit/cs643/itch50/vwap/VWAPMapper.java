package dk.njit.cs643.itch50.vwap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class VWAPMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
    private static Logger logger = LoggerFactory.getLogger(VWAPMapper.class);
    private String stock;

    @Override
    public void configure(JobConf job) {
        super.configure(job);
        stock = job.get("vwap.stock");
        logger.info("Performing VWAP on: {}", stock);
    }

    @Override
    public void map(LongWritable key,
                    Text value,
                    OutputCollector<Text, Text> output,
                    Reporter reporter) throws IOException {
        if (!Character.isDigit(value.charAt(0))) return;

        final String[] parts = value.toString().split(",");
        final long timestamp = Long.parseLong(parts[0]); // nanoseconds since midnight
        final long hour = timestamp / 3600000000000L;
        final String shares = parts[1];
        final String stock = parts[2];
        final String price = parts[3].startsWith("-") ? parts[3].substring(1) : parts[3];

        if (stock.equalsIgnoreCase(this.stock)) {
            output.collect(new Text(String.valueOf(hour)),
                    new Text(shares + "," + price));
        }
    }
}
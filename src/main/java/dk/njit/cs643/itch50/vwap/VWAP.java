package dk.njit.cs643.itch50.vwap;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VWAP {
    private static Logger logger = LoggerFactory.getLogger(VWAP.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            logger.error("First arg must be the output path.");
            logger.error("Second arg and beyond must be input paths.");
            return;
        }

        final Path outputPath = new Path(args[0]);
        final List<Path> inputPaths = new ArrayList<>(args.length - 1);
        for (int i = 1; i < args.length; i++) {
            inputPaths.add(new Path(args[i]));
        }

        JobConf conf = new JobConf(VWAP.class);
        conf.set("vwap.stock", "AMZN");

        conf.setJobName("VWAP");
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setMapperClass(VWAPMapper.class);
        conf.setReducerClass(VWAPReducer.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        inputPaths.forEach(p -> FileInputFormat.addInputPath(conf, p));
        FileOutputFormat.setOutputPath(conf, outputPath);

        conf.set("textinputformat.record.delimiter", "\r\n");

        JobClient.runJob(conf);
    }
}

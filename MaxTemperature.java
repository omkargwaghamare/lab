import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MaxTemperature {

    // Mapper class
    public static class TempMapper 
    extends Mapper<LongWritable, Text, Text, IntWritable> 
    {

        private Text year = new Text();
        private IntWritable temperature = new IntWritable();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] fields = line.split(" ");
            year.set(fields[0]); 
            // First field is the year
            temperature.set(Integer.parseInt(fields[1])); 
            // Second field is the temperature
            context.write(year, temperature); 
            // Emit key-value pair (year, temperature)
        }
    }
    // Reducer class
    public static class TempReducer 
    extends Reducer<Text, IntWritable, Text, IntWritable> 
    {

        private IntWritable maxTemperature = new IntWritable();
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int maxTemp = Integer.MIN_VALUE;
            // Iterate through all temperatures for the year and find the max
            for (IntWritable val : values) {
                maxTemp = Math.max(maxTemp, val.get());
            }
            maxTemperature.set(maxTemp);
            context.write(key, maxTemperature); // Emit (year, maxTemperature)
        }
    }

    // Main driver method
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Max Temperature");
        job.setJarByClass(MaxTemperature.class);
        
        // Set Mapper and Reducer classes
        job.setMapperClass(TempMapper.class);
        job.setReducerClass(TempReducer.class);
        
        // Set output key and value types for the Mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        
        // Set output key and value types for the Reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        
        // Input and Output paths
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

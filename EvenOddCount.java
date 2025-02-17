import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class EvenOddCount {

    // Mapper class
    public static class EvenOddMapper extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text evenOdd = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            int number = Integer.parseInt(value.toString());

            if (number % 2 == 0) {
                evenOdd.set("Even");
            } else {
                evenOdd.set("Odd");
            }
            context.write(evenOdd, one);  // Emit ("Even", 1) or ("Odd", 1)
        }
    }

    // Reducer class
    public static class EvenOddReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();  // Sum all occurrences of "Even" and "Odd"
            }
            result.set(sum);
            context.write(key, result);  // Emit final count of "Even" or "Odd"
        }
    }

    // Main method
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Even and Odd Count");

        job.setJarByClass(EvenOddCount.class);
        job.setMapperClass(EvenOddMapper.class);
        job.setCombinerClass(EvenOddReducer.class);
        job.setReducerClass(EvenOddReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}


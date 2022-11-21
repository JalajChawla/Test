package config;

import listener.JobMonitoringListener;
import model.Input;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import writer.ConsoleItemWriter;
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobFactory;
    @Autowired
    private StepBuilderFactory stepFactory;


    //listener
    @Bean
    public JobExecutionListener createListener() {
        return new JobMonitoringListener();
    }


    @Bean
    public	FlatFileItemReader<Input> createReader(){
        FlatFileItemReader<Input> reader=new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("test.csv"));
        reader.setLineMapper(new DefaultLineMapper<Input>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setDelimiter(",");
                setNames("Order_ID","Amount","Currency","Comment");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Input>() {{
                setTargetType(Input.class);
            }});

        }});
        return reader;
    }

    @Bean
    public ConsoleItemWriter createWriter() {
        return  new ConsoleItemWriter();
    }

    @Bean(name="step1")
    public Step createStep1() {
        return   stepFactory.get("step1")
                .<Input,Input>chunk(3)
                .reader(createReader())
                .writer(createWriter())
                .build();
    }

    @Bean(name="job1")
    public Job createJob1() {
        return jobFactory.get("job1")
                .incrementer(new RunIdIncrementer())
                .listener(createListener())
                .start(createStep1())
                .build();
    }
}

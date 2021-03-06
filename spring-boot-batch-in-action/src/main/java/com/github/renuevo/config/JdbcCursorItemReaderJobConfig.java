package com.github.renuevo.config;

import com.github.renuevo.entity.Pay;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import javax.sql.DataSource;

/**
 * <pre>
 * @className : JdbcCursorItemReaderJobConfig
 * @author : Deokhwa.Kim
 * @since : 2019-12-25
 * @summary : Jdbc Cursor Item Reader Example
 * </pre>
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class JdbcCursorItemReaderJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    private static final int chunkSize = 10;    //트랜잭션 범위

    @Bean
    @SneakyThrows
    public Job jdbcCursorItemReaderJob() {
        return jobBuilderFactory.get("jdbcCursorItemReaderJob") // job name
                .start(jdbcCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep() {
        return stepBuilderFactory.get("jdbcCursorItemReaderStep")   //step name
                .<Pay, Pay>chunk(chunkSize) //Reader의 반환타입 & Writer의 파라미터타입
                .reader(jdbcCursorItemReader())
                //.processor()  생략
                .writer(jdbcCursorItemWriter())
                .build();
    }


    @Bean
    public JdbcCursorItemReader<Pay> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Pay>()   //Cursor는 하나의 Connection으로 사용하기 때문에 Timeout 시간을 길게 부여해야 한다
                .fetchSize(chunkSize)       //Database에서 가져오는 개수 / read()를 통해 1개씩 (Paging과 다름)
                .dataSource(dataSource)
                //.rowMapper(SingleColumnRowMapper.newInstance(Long.class))
                //.sql("SELECT id FROM pay")
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
                .name("jdbcCursorItemReader")   //reader name
                .build();
    }

    @Bean
    public ItemWriter<Pay> jdbcCursorItemWriter() {
        return list -> {
            for (Pay pay : list) {
                log.info("Current Pay = {}", pay);
            }
        };
    }

}

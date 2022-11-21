package writer;

import model.Input;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ConsoleItemWriter implements ItemWriter<Input> {

    @Override
    public void write(List<? extends Input> items) {
        items.forEach(System.out::println);
    }
}

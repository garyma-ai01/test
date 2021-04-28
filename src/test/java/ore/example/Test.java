package ore.example;

import org.example.App;
import org.example.service.WeatherService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class Test {

    @Autowired
    WeatherService weatherService;


    @org.junit.Test(expected = IllegalArgumentException.class)
    public void test_invalid_provence() {
        weatherService.getTemperature("朝鲜", "新义", "郫县");
    }

    @org.junit.Test
    public void test_valid_provence() {
        Optional opt = weatherService.getTemperature("江苏", "苏州", "吴中");
        assertThat(opt.isPresent(), equalTo(true));
        assertThat(opt.get().toString().length()>=0, equalTo(true));
    }

}

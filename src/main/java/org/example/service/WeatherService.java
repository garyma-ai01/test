package org.example.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("weatherService")
public class WeatherService {

    @Autowired
    RestTemplate restTemplate;

    private RateLimiter limiter = RateLimiter.create(100);

    public Optional<String> getTemperature(String
                                                   province, String city, String county) {
        limiter.tryAcquire(5, TimeUnit.SECONDS);
        String provId = province(province);
        String cityId = city(city, provId);
        String countyId = county(county, cityId, provId);
        return Optional.of(restTemperature(provId, cityId, countyId));
    }

    private String province(String province) {
        HashMap<String, String> provinces =  parse(meta("http://www.weather.com.cn/data/city3jdata/china.html"));
        Preconditions.checkArgument(provinces.containsKey(province), "Invalid province - %s", province);

        return provinces.get(province);
    }

    private String city(String city, String provId) {
        HashMap<String, String> cities =  parse(meta("http://www.weather.com.cn/data/city3jdata/provshi/{prov}.html", provId));
        Preconditions.checkArgument(cities.containsKey(city), "Invalid city - %s", city);
        return cities.get(city);

    }

    private String county(String county, String cityId, String provId) {
        HashMap<String, String> cities = parse(
                meta("http://www.weather.com.cn/data/city3jdata/station/{prov}{city}.html", provId, cityId)
        );
        Preconditions.checkArgument(cities.containsKey(county), "Invalid county - %s", county);
        return cities.get(county);

    }

    private String meta(String url, Object... ids) {
        int retry = 1;
        while (retry < 5) {
            ResponseEntity<String> responseEntity = sent(url, ids);
            HttpStatus status = responseEntity.getStatusCode();

            if (status.value() >= 500) {
                log.warn("Retry %d/%d ... - %s %s", retry, 5, status.value(), responseEntity.getBody());
                retry++;
                continue;
            }
            return responseEntity.getBody();
        }
        return "";
    }

    private HashMap<String, String> parse(String str) {
        HashMap result = Maps.newHashMap();
        Map metas = JSON.parseObject(str);
        metas.forEach((k, v) -> {
            result.putIfAbsent(v, k);
        });
        return result;
    }

    private ResponseEntity<String> sent(String url, Object... ids) {
        return restTemplate.getForEntity(url, String.class, ids);
    }

    private String restTemperature(String prov, String city, String county) {
        String body = meta("http://www.weather.com.cn/data/sk/{prov}{city}{county}.html", prov, city, county);
        return JSON.parseObject(body).getJSONObject("weatherinfo").get("temp").toString();

    }

}

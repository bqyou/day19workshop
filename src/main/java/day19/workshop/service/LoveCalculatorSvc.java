package day19.workshop.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import day19.workshop.model.LoveResult;

@Service
public class LoveCalculatorSvc {

    private static final String LOVE_CALCULATOR_URL = "https://love-calculator.p.rapidapi.com/getPercentage";
    private static final String LOVE_LIST = "lovelist";

    public Optional<LoveResult> getLoveResult(String fname, String sname) throws IOException {
        String loveUrl = UriComponentsBuilder.fromUriString(LOVE_CALCULATOR_URL)
                .queryParam("sname", sname.replaceAll(" ", "+"))
                .queryParam("fname", fname.replaceAll(" ", "+"))
                .toUriString();
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = testHeader(loveUrl, template);
        LoveResult loveResult = LoveResult.create(resp.getBody());
        if (loveResult != null) {
            return Optional.of(loveResult);
        }
        return Optional.empty();
    }

    public ResponseEntity<String> testHeader(String loveUrl, final RestTemplate restTemplate) {
        final HttpHeaders headers = new HttpHeaders();
        String loverApiKey = System.getenv("LOVERAPIKEY");
        String loverApiHost = System.getenv("LOVERAPIHOST");
        headers.set("X-RapidAPI-Key", loverApiKey);
        headers.set("X-RapidAPI-Host", loverApiHost);
        final HttpEntity<String> entity = new HttpEntity<String>(headers);

        // Execute the method writing your HttpEntity to the request
        ResponseEntity<String> response = restTemplate.exchange(loveUrl, HttpMethod.GET,
                entity, String.class);
        return response;
    }

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void save(final LoveResult loveResult) {
        redisTemplate.opsForList().leftPush(LOVE_LIST, loveResult.getFname() + " and " + loveResult.getSname());
        redisTemplate.opsForHash().put(LOVE_LIST + "_Map", loveResult.getFname() + " and " + loveResult.getSname(),
                loveResult);
    }

    public List<LoveResult> findAll() {
        List<Object> fromLoveResults = redisTemplate.opsForList().range(LOVE_LIST, 0,
                redisTemplate.opsForList().size(LOVE_LIST));
        List<LoveResult> loveResults = redisTemplate.opsForHash()
                .multiGet(LOVE_LIST + "_Map", fromLoveResults)
                .stream()
                .filter(LoveResult.class::isInstance)
                .map(LoveResult.class::cast)
                .toList();
        return loveResults;
    }

    public void clear() {
        while (redisTemplate.opsForList().size(LOVE_LIST) > 0) {
            redisTemplate.opsForList().leftPop(LOVE_LIST);
        }
    }

}
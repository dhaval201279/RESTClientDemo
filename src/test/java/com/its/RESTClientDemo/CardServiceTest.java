package com.its.RESTClientDemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.its.RESTClientDemo.entity.CardEntity;
import com.its.RESTClientDemo.infrastructure.HttpClientConfig;
import com.its.RESTClientDemo.infrastructure.RestTemplateConfig;
import com.its.RESTClientDemo.infrastructure.ssl.UntrustedHttpClientSslHelper;
import com.its.RESTClientDemo.service.CardService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

//import org.junit.jupiter.api.Test;

//@RunWith(SpringRunner.class)
//@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RestClientDemoApplication.class)
//@ContextConfiguration(classes = {RestTemplateConfig.class, HttpClientConfig.class, UntrustedHttpClientSslHelper.class})
/*public class CardServiceTest {

    @Autowired
    CardService cardService;

    @Autowired
    RestTemplate mockRestTemplate;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(mockRestTemplate);
    }

    @Test
    public void generateAlias_is_retry_successfull_on_httpclienterrorexception() {*/
        /*Mockito
            .when(mockRestTemplate.getForObject(ArgumentMatchers.eq("http://localhost:7080/%s"),
                ArgumentMatchers.any().getClass(), (Object) ArgumentMatchers.any()))
            .thenThrow(HttpClientErrorException.class)
            .thenReturn(null);

        cardService.generateAlias("123");

        Mockito
            .verify(mockRestTemplate, Mockito.times(3))
            .getForObject(ArgumentMatchers.eq("http://localhost:7080/%s"),
                ArgumentMatchers.any().getClass(), (Object) ArgumentMatchers.any());*/

        // new approach
/*        CardEntity testCard = CardEntity
                                .builder()
                                .cardNumber("12345678996325741")
                                .expiryDate("1225")
                                .issuingNetwork("MC")
                                .name("YDS")
                                .build();


        mockServer
            .expect(ExpectedCount.times(4), MockRestRequestMatchers.requestTo("http://localhost:7080/12345678996325741"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        cardService.enroll(testCard);

        mockServer
            .verify();

        //Assertions.assertThrows(HttpClientErrorException.class, () -> cardService.enroll(testCard));
    }
}*/

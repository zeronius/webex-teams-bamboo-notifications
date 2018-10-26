package ut.net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Message;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsMessageService;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.teamsclient.TeamsRestHttpClient;

public class TeamsMessageServiceTest {

    private TeamsRestHttpClient teamsRestHttpClient;

    @Before
    public void setupMocks() {
        teamsRestHttpClient = Mockito.mock(TeamsRestHttpClient.class);
    }

    @Test
    public void givenSendMessage_whenMessageIsSent_thenTrueIsReturned() {
        Mockito.when(teamsRestHttpClient.sendPostRequest(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyObject()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        final boolean result = new TeamsMessageService(teamsRestHttpClient).sendMessage("token", new Message());
        Assert.assertTrue(result);
    }

    @Test
    public void givenSendMessage_whenMessageSendingFails_thenFalseIsReturned() {
        Mockito.when(teamsRestHttpClient.sendPostRequest(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyObject()))
                .thenThrow(new RestClientException("Some exception"));
        final boolean result = new TeamsMessageService(teamsRestHttpClient).sendMessage("token", new Message());
        Assert.assertFalse(result);
    }
}
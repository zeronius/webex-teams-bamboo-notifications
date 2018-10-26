package ut.net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Room;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsRoomService;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.teamsclient.TeamsRestHttpClient;

public class TeamsRoomServiceTest {

    private TeamsRestHttpClient teamsRestHttpClient;

    @Before
    public void setupMocks() {
        teamsRestHttpClient = Mockito.mock(TeamsRestHttpClient.class);
    }

    @Test
    public void givenFindByIdentifierOrName_whenRequestToApiIsSuccessful_thenResultIsReturned() {
        Mockito.when(teamsRestHttpClient.sendGetRequest(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(new Room(), HttpStatus.OK));
        final Optional<Room> result = new TeamsRoomService(teamsRestHttpClient).findByIdentifierOrName("token", "roomId");
        Assert.assertTrue(result.isPresent());
    }

    @Test
    public void givenFindByIdentifierOrName_whenRequestToApiFails_thenEmptyResultIsReturned() {
        Mockito.when(teamsRestHttpClient.sendGetRequest(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenThrow(new RestClientException("Some exception"));
        final Optional<Room> result = new TeamsRoomService(teamsRestHttpClient).findByIdentifierOrName("token", "roomId");
        Assert.assertFalse(result.isPresent());
    }
}
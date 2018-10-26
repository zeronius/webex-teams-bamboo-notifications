package ut.net.cimbalek.bamboo.plugin.webexteamsnotifications;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atlassian.bamboo.notification.Notification;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.TeamsBotNotificationTransport;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.dto.TeamsBotNotificationSettings;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Message;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Room;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsMessageService;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsRoomService;

public class TeamsBotNotificationTransportTest {

    private Notification notification;
    private TeamsRoomService teamsRoomService;
    private TeamsMessageService teamsMessageService;

    @Before
    public void setupMocks() {
        teamsRoomService = Mockito.mock(TeamsRoomService.class);
        notification = Mockito.mock(Notification.class);
        teamsMessageService = Mockito.mock(TeamsMessageService.class);
    }

    @Test
    public void givenSendNotification_whenNotificationRecipientRoomIsFound_thenMessageIsSent() {
        final TeamsBotNotificationSettings teamsBotNotificationSettings = buildSettings();
        final TeamsBotNotificationTransport teamsBotNotificationTransport = new TeamsBotNotificationTransport(teamsBotNotificationSettings,
                teamsRoomService, teamsMessageService);
        final String messageContent = "messageContent";
        final Room room = buildRoom(teamsBotNotificationSettings);
        final Message message = new Message();
        message.setRoomId(room.getId());
        message.setMarkdown(messageContent);
        Mockito.when(notification.getIMContent()).thenReturn(messageContent);
        Mockito.when(teamsRoomService.findByIdentifierOrName(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.of(room));
        teamsBotNotificationTransport.sendNotification(notification);
        Mockito.verify(teamsMessageService, Mockito.atLeastOnce()).sendMessage(teamsBotNotificationSettings.getBotAccessToken(), message);
    }

    @Test
    public void givenSendNotification_whenNotificationRecipientRoomIsFound_thenMessageIsNotSent() {
        final TeamsBotNotificationSettings teamsBotNotificationSettings = buildSettings();
        final TeamsBotNotificationTransport teamsBotNotificationTransport = new TeamsBotNotificationTransport(teamsBotNotificationSettings,
                teamsRoomService, teamsMessageService);
        final String messageContent = "messageContent";
        final Room room = buildRoom(teamsBotNotificationSettings);
        final Message message = new Message();
        message.setRoomId(room.getId());
        message.setMarkdown(messageContent);
        Mockito.when(notification.getIMContent()).thenReturn(messageContent);
        Mockito.when(teamsRoomService.findByIdentifierOrName(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.empty());
        teamsBotNotificationTransport.sendNotification(notification);
        Mockito.verify(teamsMessageService, Mockito.never()).sendMessage(teamsBotNotificationSettings.getBotAccessToken(), message);
    }

    private Room buildRoom(final TeamsBotNotificationSettings teamsBotNotificationSettings) {
        final Room room = new Room();
        room.setTitle("roomName");
        room.setId(teamsBotNotificationSettings.getRoomIdentifier());
        return room;
    }

    private TeamsBotNotificationSettings buildSettings() {
        final TeamsBotNotificationSettings teamsBotNotificationSettings = new TeamsBotNotificationSettings();
        teamsBotNotificationSettings.setBotAccessToken("botAccessToken");
        teamsBotNotificationSettings.setRoomIdentifier("roomId");
        return teamsBotNotificationSettings;
    }
}
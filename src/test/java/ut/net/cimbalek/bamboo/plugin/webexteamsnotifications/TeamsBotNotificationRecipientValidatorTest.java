package ut.net.cimbalek.bamboo.plugin.webexteamsnotifications;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.sal.api.message.I18nResolver;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.TeamsBotNotificationRecipientValidator;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.dto.TeamsBotNotificationSettings;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Room;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsRoomService;

public class TeamsBotNotificationRecipientValidatorTest {

    private TeamsRoomService teamsRoomService;
    private I18nResolver i18nResolver;

    @Before
    public void setupMocks() {
        teamsRoomService = Mockito.mock(TeamsRoomService.class);
        i18nResolver = Mockito.mock(I18nResolver.class);
    }

    @Test
    public void givenValidate_whenFilledValidNotificationSettingsProvided_thenReturnEmptyErrors() {
        final TeamsBotNotificationRecipientValidator validator = new TeamsBotNotificationRecipientValidator(teamsRoomService, i18nResolver);
        final String roomId = "roomId";
        final String botToken = "botToken";
        final Room room = new Room();
        room.setId(roomId);
        Mockito.when(teamsRoomService.findByIdentifierOrName(botToken, roomId)).thenReturn(Optional.of(room));
        final ErrorCollection errors = validator.validate(buildSettings(roomId, botToken));
        Assert.assertFalse(errors.hasAnyErrors());
    }

    @Test
    public void givenValidate_whenFilledInvalidRoomName_thenReturnError() {
        final TeamsBotNotificationRecipientValidator validator = new TeamsBotNotificationRecipientValidator(teamsRoomService, i18nResolver);
        final String roomId = "roomId";
        final String botToken = "botToken";
        final Room room = new Room();
        room.setId(roomId);
        Mockito.when(teamsRoomService.findByIdentifierOrName(botToken, roomId)).thenReturn(Optional.empty());
        final ErrorCollection errors = validator.validate(buildSettings(roomId, botToken));
        Assert.assertTrue(errors.hasAnyErrors());
        Assert.assertEquals(errors.getErrors().size(), 1);
    }

    @Test
    public void givenValidate_whenNoRoomIdProvided_thenReturnError() {
        final TeamsBotNotificationRecipientValidator validator = new TeamsBotNotificationRecipientValidator(teamsRoomService, i18nResolver);
        final String botToken = "botToken";
        final ErrorCollection errors = validator.validate(buildSettings(null, botToken));
        Mockito.verify(teamsRoomService, Mockito.never()).findByIdentifierOrName(botToken, "someRoomId");
        Assert.assertTrue(errors.hasAnyErrors());
        Assert.assertEquals(errors.getErrors().size(), 1);
    }

    @Test
    public void givenValidate_whenNoBotTokenProvided_thenReturnEmptyErrors() {
        final TeamsBotNotificationRecipientValidator validator = new TeamsBotNotificationRecipientValidator(teamsRoomService, i18nResolver);
        final String roomId = "roomId";
        final Room room = new Room();
        room.setId(roomId);
        final ErrorCollection errors = validator.validate(buildSettings(roomId, null));
        Mockito.verify(teamsRoomService, Mockito.never()).findByIdentifierOrName("someBotToken", roomId);
        Assert.assertTrue(errors.hasAnyErrors());
        Assert.assertEquals(errors.getErrors().size(), 1);
    }

    @Test
    public void givenValidate_whenNoBotTokenNorRoomIdProvided_thenReturnEmptyErrors() {
        final TeamsBotNotificationRecipientValidator validator = new TeamsBotNotificationRecipientValidator(teamsRoomService, i18nResolver);
        final ErrorCollection errors = validator.validate(buildSettings(null, null));
        Mockito.verify(teamsRoomService, Mockito.never()).findByIdentifierOrName("someBotToken", "someRoom");
        Assert.assertTrue(errors.hasAnyErrors());
        Assert.assertEquals(errors.getErrors().size(), 2);
    }

    private TeamsBotNotificationSettings buildSettings(final String roomId, final String botToken) {
        final TeamsBotNotificationSettings teamsBotNotificationSettings = new TeamsBotNotificationSettings();
        teamsBotNotificationSettings.setRoomIdentifier(roomId);
        teamsBotNotificationSettings.setBotAccessToken(botToken);
        return teamsBotNotificationSettings;
    }
}
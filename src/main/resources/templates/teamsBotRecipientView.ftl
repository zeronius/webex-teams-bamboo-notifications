[#if roomIdentifier?has_content]
    ${roomIdentifier}
[#else]
    [@s.text name='nofification.recipient.webexTeamsNotifications.roomIdentifier.error.noSpecified'/]
[/#if]
 <span class='notificationRecipientType'> ([@s.text name='nofification.recipient.webexTeamsNotifications.type.name'/])</span>
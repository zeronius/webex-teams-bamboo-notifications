[#if botAccessToken?has_content]
    [@s.textfield key='nofification.recipient.webexTeamsNotifications.accessToken.label' value='${botAccessToken?html}' name='botAccessToken' /]
[#else]
    [@ww.textfield labelKey='nofification.recipient.webexTeamsNotifications.accessToken.label'  name='botAccessToken' /]
[/#if]
[#if roomIdentifier?has_content]
    [@s.textfield key='nofification.recipient.webexTeamsNotifications.roomIdentifier.label' value='${roomIdentifier?html}' name='roomIdentifier' descriptionKey='nofification.recipient.webexTeamsNotifications.roomIdentifier.description'/]
[#else]
    [@ww.textfield labelKey='nofification.recipient.webexTeamsNotifications.roomIdentifier.label'  name='roomIdentifier' descriptionKey='nofification.recipient.webexTeamsNotifications.roomIdentifier.description' /]
[/#if]
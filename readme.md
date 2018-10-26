# Atlassian Bamboo notification plugin for Cisco Webex Teams

## How it works

## Installation of the plugin

## TODOs
* Improve UX by loading bot's rooms while notification type is selected and provide user a selectbox with all bot's rooms.
* Remove not necessary web resources as css, js, etc. 

## Contribution
It's based completely on Atlassian Plugin SDK, see documentation at https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK

### SDK commands for immediate use

* atlas-run   -- installs this plugin into the product and starts it on localhost
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-cli   -- after atlas-run or atlas-debug, opens a Maven command line window:
                 - 'pi' reinstalls the plugin into the running product instance
* atlas-help  -- prints description for all commands in the SDK
* atlas-mvn package -- generates plugin for quick reload ( https://developer.atlassian.com/server/framework/atlassian-sdk/modify-the-plugin-using-quickreload/ )
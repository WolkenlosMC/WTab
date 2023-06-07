# WTab

## Installing
[LuckPerms](https://luckperms.net) is recommented.

## [Click to install WTab v1.0](coming soon)

## Roadmap 
- finish the plugin
- MySQL support
- animatable tablist

## Example Config
```yml
########################################################################################
#                                   Wolkenlos - Tab                                    #
#                                                                                      #
#               Version                                     Author                     #
#               1.0.0                                     TheSkyScout                  #
#                                                                                      #
########################################################################################
#                                       SETTINGS                                       #
########################################################################################
prefix: "<gradient:green:white><bold>Tab </bold><gray>| <gray>"
#Use Adventure color codes for prefix https://docs.advntr.dev/minimessage/format.html#color | https://webui.advntr.dev
save-method: "FILE"
#Save Method - FILE or MONGODB
#FILE - Save data in files
#MONGODB - Save data in MongoDB | !! Server wide data !!
#MySQL support will be added in the future!
tablist-method: "PERMISSION"
#Tablist Method - PERMISSION or LUCKPERMS
#PERMISSION - Use Permissions to set the tablist
#LUCKPERMS - Use LuckPerms to set the tablist

# Only needed when save method is FILE
header: ""
#Header of the tablist
footer: ""
#Footer of the tablist

########################################################################################
#                                         Database                                     #
########################################################################################
#ONLY NEEDED WHEN SAVE METHOD IS MONGODB
mongo_uri: "mongodb://localhost:27017/"
database: "database"
```

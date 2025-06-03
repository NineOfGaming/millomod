# MilloMod

*A client-side utility mod for the Minecraft server [MCDiamondFire](https://mcdiamondfire.com/home/), designed to boost productivity for creators and developers building games with codes blocks.*

## About
This mod targets to make coding efficient.

## Compatibility
Minecraft Version: 1.21.3

Mod Loader: Fabric

## Core Features
|Feature|Description|
|-------|-----------|
|Lagslayer HUD|A radial display of plot CPU usage|
|Menu Search|Quickly search through code menus|
|Plot Caching|Cache entire plots and read through the code in a text-based form|
|Show Tags|Hold a keybind to display custom item tags on items|
|Side Chat|A seperate chat window to keep track of private/support/moderation/administrative messages|
|FS Toggle|A keybind to toggle high flightspeed|
|Mode Switcher|A paginated radial to switch modes, servers, and execute quick commands|
|Argument Insert|Shift-Right Click an empty slot in a value-chest to insert a value-item without needing to close the menu|
|Not Switcher|A keybind to toggle the NOT state on an if statement|
|Pick Chest Value|Keybind to obtain the first item in a value-chest|
|Socket Serve|A local socket server for tool integration and retreiving items or templates within the client (port 31321)|
|Spectator Toggle|Enter spectator mode (cannot currently exit properly)|
|Sound Preview|A button to play all sounds in a value-chehst at once|
|Angels Grace|Start automatically flying whenever you drop down in dev-mode whilst having a menu open|


## Configuration
In game the `/settings` command can be ran to open the settings menu.
A feature list and patchnotes can be found under `/millo`
Retrieve the action dump to use some features using `/milloactiondump`

## Plot Caching
A visual representation of Code Templates. Accessable through a keybind and `/cache`.
Within the cache gui you can:
 - search
 - utilize folders (`folder.functionName`)
 - teleport to code blocks by clicking the line number
 - obtain value-items by clicking, (e.g. click a number to obtain that exact number)
 - ctrl + shift + f to search in the hierarchy
 - ctrl + f to search text in the current function, click the `search` button in this menu to search the entire plot
 - Access any cached-plots code by entering the desired plot id in the top left corner.

## Developer Tools
`SocketServe` provides a local socket access on port `31321`, allowing external tools to:
	- Send Items
 	- Send Templates

Sending data to the client requires a JSON formatted like this:
```json
{
	"type": "item",
	"source": "Whomever Sent Item",
	"data": "minecraft:stone[custom_name=\"Hello\"]"
}
```

## Notice
MilloMod was made for personal use. People have asked for this mod, if you have any suggestions or bug reports, please message `@im.endersaltz` on discord.

I ain't responsible.


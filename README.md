# MilloMod

*A client-side utility mod for the Minecraft server [MCDiamondFire](https://mcdiamondfire.com/home/), designed to boost productivity for creators and developers building games with codes blocks.*

## About
This mod aims to make coding more efficient.

## Compatibility
Minecraft Version: 1.21.8

Mod Loader: Fabric

## Core Features
|Feature|Description|
|-------|-----------|
|**Lagslayer HUD**|A radial display of plot CPU usage|
|**Menu Search**|Quickly search through code menus|
|**Plot Caching**|Cache entire plots and read through the code in a text-based form|
|**Show Tags**|Hold a keybind to display custom item tags on items|
|**Side Chat**|A separate chat window to keep track of private/support/moderation/administrative messages|
|**FS Toggle**|A keybind to toggle high flightspeed|
|**Mode Switcher**|A paginated radial to switch modes, servers, and execute quick commands|
|**Argument Insert**|Shift-Right Click an empty slot in a value-chest to insert a value-item without needing to close the menu|
|**Not Switcher**|A keybind to toggle the NOT state on an if statement|
|**Pick Chest Value**|Keybind to obtain the first item in a value-chest|
|**Socket Serve**|A local socket server for tool integration and retrieving items or templates within the client (port 31321)|
|**Spectator Toggle**|Enter spectator mode (cannot currently exit properly)|
|**Sound Preview**|A button to play all sounds in a value-chest at once|
|**Angels Grace**|Automatically start flying whenever you fall in dev mode while a menu is open|


## Configuration
 - Use `/settings` to open the in-game settings menu.
 - A feature list and patchnotes can be found under `/millo`
 - Retrieve the action dump to use some features using `/milloactiondump`

## Plot Caching
A visual representation of Code Templates. Accessable through a keybind and `/cache`.
Within the cache gui you can:
 - Search by text
 - Use folders (`folder.functionName`)
 - Teleport to code blocks by clicking line numbers
 - Obtain value-items by clicking values (e.g. click a number to obtain that exact number)
 - Use `Ctrl + Shift + F` to search across the hierarchy.
 - Use `Ctrl + F` to search within the current function (click the **Search** button to expand to the entire plot)
 - Access any cached-plots code by entering the desired plot id in the top-left corner.

### How to use
- To cache an entire plot, use `/cache plotold` and wait for it to finish. (`/cache plot` only works if you have admin permissions or something 💀)
- If you have a keybind set for Cache Line / Menu in keybind settings, using it while looking at a line starter will cache that line and open the menu to that line

> [!NOTE]
> The cached code does not automatically refresh when its changed and needs to be re-cached using one of the two methodes mentioned above.  
> The keybind also works if your aiming at up to two blocks around the actual block.

> [!IMPORTANT]
> There is a chance that caching of a line will not work for some reason idk why, you should check if all your code is cached especially after using the command to cache the entire plot.  
> There is a bug causing the item in your main hand to be deleted when caching something.

## Developer Tools
`SocketServe` provides a local socket access on port `31321`, allowing external tools to:
 - Send items
 - Send templates

Sending data to the client requires a JSON formatted like this:
```json
{
	"type": "item",
	"source": "Whomever Sent Item",
	"data": "minecraft:stone[custom_name=\"Hello\"]"
}
```

## Notice
This is a fork, please do not bother the original creator about stuff on this repository.

I am not planing on consistently maintaining this fork, i just wanted to use it on 1.21.8 early because i already switched to prepare for df update and fix/add a few things while im at it.

I intentionally did not include the build jar file because then people will take the mod for granted until i inevitably stop maintaining this fork and then go bother the original creator about it.

## Notice (from the original repository)

MilloMod was made for personal use. People have asked for this mod- if you have any suggestions or bug reports, please message `@im.endersaltz` on discord.

I ain't responsible.


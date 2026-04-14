# ItemSpinner

**Item Spinner** is an MC Java edition spigot MC plugin.
This plugin detects all "item display" entitys with the tag "spin", and spins them.
You can summon an item display with the tag like this:

```bash
/summon minecraft:item_display ~ ~ ~ {Tags:["spin"], item:{id:"<nameSpace:path>",Count:1}}
```

and you can also scale them, using a command like this:

```bash
/summon minecraft:item_display ~ ~ ~ {Tags:["spin"], transformation:{right_rotation:[0f,0f,0f,1f], scale:[2.0f,2.0f,2.0f], left_rotation:[0f,0f,0f,1f], translation:[0f,0f,0f]}, item:{id:"<nameSpace:path>",Count:1}}
```

At first I just had it tp the entity around the yaw axis, but that often dropped frames and was jittery,
so in newer versions, I get around this using client side lerps(linear interpolations) to smoothly calculate the animation
on the client's machine. You can also configure the speed from the config.yml file in the repo.

You can install this like any other spigot MC plugin.

© 2026 David s all rights reserved.

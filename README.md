# Max Hit
This plugin includes methods for getting all the information required for calculating the player's max hit in their 
current gear, with their current (boosted) stats, and current active prayers. The equipped weapon dictates which 
style of combat (melee or ranged) will be calculated. For magic, the user can select a spell from the configuration
menu and the plugin will show their max hit with that spell in addition to the max hit of the current equipped weapon/
gear. If the equipped weapon has a damage bonus, the special attack max hit will also be shown.

Currently, this plugin does not work for the blowpipe, as the weapon ID for the blowpipe does not change
based on the type of darts equipped, and item IDs are the primary way equipment bonus is entered into the calculation.
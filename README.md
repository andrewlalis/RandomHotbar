# RandomHotbar
Utility for randomizing chosen items on a minecraft hotbar.

Simply download the latest version from the [releases page](https://github.com/andrewlalis/RandomHotbar/releases), then run it like so:

```
java -jar RandomHotbar.jar <settings>
```

Where `<settings>` is a specification of how likely it should be to select each hotbar slot. Each setting is specified as `<slot>:<probability>` Here are some examples:
- I want to select slots 1, 2, and 3, all with equal probability: `1:1, 2:1, 3:1`
- I want to select 5x as much of slot 2 as slot 1: `1:1, 2:5`
- I want half as much of slot 1, as slots 2 and 3: `1:0.5, 2:1, 3:1`

For example:
```
java -jar RandomHotbar.jar 1:1, 2:1, 3:1.5, 4:0.75
```

Alternatively, if you like spam-clicking to place randomized blocks, opt instead for the **cycle** option. It's used like so:
```
java -jar RandomHotbar.jar cycle
```
Using this setting, you will automatically cycle through all hotbar slots, shifting in random intervals to make sure that no patterns are introduced in the blocks you place. You can also restrict the cycle mode to the first N slots like so:
```
java -jar RandomHotbar.jar cycle 3
```

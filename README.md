# PlayerRPGSystem - Daily Reward System

A Minecraft mod for NeoForge 1.21.1 that adds an RPG progression system with daily rewards to Pixelmon gameplay.

## Features

### RPG System
- Player experience and leveling system
- Experience gained from capturing Pokémon
- Level-based Pokémon capture restrictions
- Player level affects Pokémon stats in battle

### Daily Reward System
- **Daily Rewards**: Players can claim rewards once per day
- **UTC Reset**: Rewards reset automatically at 00:00 UTC
- **Loot Table Configuration**: Rewards are configured via JSON loot tables for easy customization
- **Server-Side Security**: All reward generation happens server-side to prevent exploits
- **GUI Integration**: Inventory button shows reward availability status
- **NBT Persistence**: Last claim timestamp is saved in player data

## Daily Rewards

### Fixed Rewards (Always Given)
- 10 Pokéballs
- 5 Great Balls  
- 2 Ultra Balls

### Random Rewards (1-3 items with weighted chances)
- Rare Candy (1-3x) - 70% weight
- Luxury Ball - 20% weight
- Safari Ball - 15% weight
- Potion - 30% weight
- Super Potion - 20% weight
- Revive - 25% weight

## Installation & Development

### Prerequisites
- Java 21+
- NeoForge 1.21.1
- Pixelmon mod

### Building the Project
```bash
cd neoforge
./gradlew build
```

### Running in Development
```bash
cd neoforge
./gradlew runClient --no-daemon
```

### Running Tests
```bash
cd neoforge
./gradlew test --no-daemon
```

### Linting
```bash
cd neoforge
./gradlew check --no-daemon
```

## Configuration

### Customizing Daily Rewards
Edit `data/pixelmonrpgsystem/loot_table/daily_reward.json` to modify rewards:

- **Fixed Pool**: Items always given (rolls: 1)
- **Random Pool**: Items with weighted chances (rolls: 1-3)
- **Weights**: Higher numbers = more likely to be selected
- **Functions**: Use `minecraft:set_count` to specify quantities
- **Number Providers**: Use `minecraft:uniform` for random ranges

Example modification:
```json
{
  "type": "minecraft:item",
  "name": "pixelmon:master_ball",
  "weight": 5,
  "functions": [
    {
      "function": "minecraft:set_count", 
      "count": 1
    }
  ]
}
```

## How It Works

### Architecture
- **NBT Storage**: `lastDailyReward` field stores UTC timestamp in PlayerRPGData
- **Capability System**: PlayerRPGCapability provides access to daily reward methods
- **Loot Tables**: Native Minecraft system for flexible reward configuration
- **Networking**: Secure packet system for client-server communication
- **GUI**: Inventory overlay button using NeoForge ScreenEvent system

### Security Flow
1. Client clicks reward button → sends `DailyRewardRequestPacket`
2. Server validates `canClaimDailyReward()` using UTC day comparison
3. Server marks as claimed BEFORE generating rewards (prevents exploits)
4. Server uses loot table to generate rewards
5. Server adds items to player inventory
6. Server sends `DailyRewardResponsePacket` with reward list
7. Client displays reward screen

### Daily Reset Logic
```java
long currentDay = System.currentTimeMillis() / (24 * 60 * 60 * 1000);
long lastRewardDay = lastDailyReward / (24 * 60 * 60 * 1000);
return currentDay > lastRewardDay; // Can claim if different day
```

## Testing

### Manual Testing Scenarios
1. **First Claim**: Open inventory, verify button shows "🎁 Claim Reward", click to claim
2. **Already Claimed**: After claiming, button should show "🎁 Already Claimed" and be inactive
3. **Persistence**: Restart client, verify reward status persists
4. **Daily Reset**: Wait for UTC day change or modify timestamp to test reset
5. **Loot Generation**: Verify rewards match loot table configuration
6. **Inventory Full**: Test reward dropping when inventory is full

### Debug Tips
- Check server logs for loot table loading errors
- Use `/time set` commands to test different times
- Monitor network packets in development environment
- Verify NBT data persistence in world saves

## References

- [NeoForge Documentation](https://docs.neoforged.net/)
- [NeoForge GUI Screens](https://docs.neoforged.net/docs/gui/screens/)
- [NeoForge Loot Tables](https://docs.neoforged.net/docs/resources/server/loottables/)
- [Minecraft NBT Format](https://minecraft.wiki/w/NBT_format)
- [Pixelmon Wiki](https://pixelmonmod.com/wiki/)

## Roadmap

### Future Improvements
- **Weekly/Monthly Rewards**: Extended reward cycles
- **Streak Bonuses**: Rewards for consecutive daily claims
- **Player Level Scaling**: Better rewards for higher level players
- **Seasonal Events**: Special rewards during holidays
- **Achievement Integration**: Rewards tied to player achievements
- **GUI Enhancements**: Better visual feedback and animations
- **Admin Commands**: Tools for managing player reward status
- **Statistics Tracking**: Analytics for reward claim patterns

### Configuration Enhancements
- **Config File**: Move basic settings to configuration file
- **Per-Player Rewards**: Different rewards based on player progression
- **Biome-Based Rewards**: Location-specific reward variations
- **Time-Based Modifiers**: Different rewards at different times

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes following the existing code style
4. Test thoroughly with `./gradlew runClient`
5. Run linting: `./gradlew check`
6. Commit your changes: `git commit -m 'Add amazing feature'`
7. Push to the branch: `git push origin feature/amazing-feature`
8. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Credits

- **Development**: Devin AI (@relrypesan)
- **Framework**: NeoForge Team
- **Pixelmon**: Pixelmon Development Team
- **Minecraft**: Mojang Studios

---

*For support, please open an issue on the GitHub repository.*

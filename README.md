
# MXVote

**MXVote** is a Minecraft voting plugin for Paper/Spigot servers, allowing players to vote on **time and weather changes**. It also supports **automatic voting settings** and provides a clean UI with **BossBars** and clickable chat components.

----------

## Features

-   Vote for **time changes** (`Day` / `Night`) and **weather changes** (`Sunny`) in-game.

-   **Group voting**: Multiple players can vote simultaneously.

-   **AutoVote system**:

    -   Automatically vote based on personal preferences.

    -   Settings for **time**, **weather**, and **requests**.

-   **BossBar progress** displays ongoing votes.

-   Clickable `[YES]` / `[NO]` chat options for easy voting.

-   Permissions-based voting system.


----------

## Commands

| Command     | Description                    | Usage                         | Permission                                  |
|------------|--------------------------------|-------------------------------|--------------------------------------------|
| `/vote`     | Start a vote for time or weather | `/vote <day/night/sunny>`     | `mxvote.vote.day` / `mxvote.vote.night` / `mxvote.vote.sunny` |
| `/autovote` | Adjust your auto-vote preferences | `/autovote <time/weather/requests> <option>` | `mxvote.vote.contribute`                   |
| `/mxvote`   | Test MXVote plugin              | `/mxvote`                     | `op` (for testing)                         |


### Example AutoVote Settings

-   `/autovote time day` → Automatically vote for Day when Night starts.

-   `/autovote weather sunny` → Automatically vote for Sunny weather.

-   `/autovote requests yes` → Automatically vote Yes for incoming requests.


----------

## Permissions

-   `mxvote.vote.day` – Vote for Day.

-   `mxvote.vote.night` – Vote for Night.

-   `mxvote.vote.sunny` – Vote for Sunny weather.

-   `mxvote.vote.contribute` – Allow contributing to any vote.

-   `mxvote.test.version` – Check plugin version (admin/test).


----------

## API

MXVote exposes core classes for integration:

-   **Voting**: Handles starting, managing, and ending votes.

-   **VoteType**: Enum representing `Time`, `Weather`, or `Requests`.

-   **AutoVote**: Handles automatic voting settings per player.


----------

## Features in Detail

### Voting Flow

1.  A player initiates a vote using `/vote <option>`.

2.  Players are prompted via chat with clickable `[YES] / [NO]`.

3.  BossBar shows vote progress and remaining time.

4.  AutoVotes are automatically applied if configured.

5.  Vote passes if more than 50% of players agree.


### AutoVote

-   Each player can configure preferred `Time`, `Weather`, and `Requests`.

-   Votes are triggered **only when the state changes** to prevent unnecessary voting.


----------

## Configuration

-   **Vote duration** and **time change step size** are configurable in `config.yml`.

-   Prefix for messages can be customized.


----------

## Installation

1.  Place `MXVote.jar` into your `plugins` folder.

2.  Start or restart the server.

3.  Configure settings in `config.yml` if needed.


----------

## Compatibility

-   Designed for **Paper 1.20+**.

-   Works on most Spigot servers supporting Bukkit API.


----------

## Example

`// Start a daytime vote voting.startVote(player, Time.DAY) // Check AutoVote settings  val userData = data.getUserDataByPlayer(player)
println(userData.autovotes)`

----------

## Contributing

-   Submit issues on GitHub.

-   Pull requests welcome.

-   Follow Kotlin/Spigot conventions.


----------

## License

MIT License — Free to use and modify.
## Aggregator
Aggregator forwards messages from channels which follow announcement channels to a singular target channel in order to circumvent Discord's 15 webhook per channel limitation.

Using Aggregator is simple. Simply use /redirect set and specify the channel and the target channel.

**Links:** [Bot Invite](https://discord.com/api/oauth2/authorize?client_id=1034850407450693714&permissions=536871936&scope=bot) / [Discord Server](https://discord.gg/kYFZtajTdx) / [GitHub](https://github.com/Peter-Crawley/Aggregator) / [Documentation](https://tinyurl.com/2dumcd5k)

### Development and Self-Hosting
Aggregator uses the Gradle build system, and as such building the project is easy as using `./gradlew build` (Linux) or `gradlew.bat` (Windows). Users who want to self-host the bot are expected to build it themselves, no pre-built jar files will be provided.

The bot is written in Kotlin and uses MongoDB for data storage. There are not many contribution guidelines, just keep code clean.

For simplicity, instead of using a configuration file, Aggregator gets its credentials from environment variables:

| Name           | Description                                                                                                                                      | Example Value                            |
|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------|
| DISCORD_TOKEN  | A bot authentication token provided by Discord. [See Discord Documentation](https://discord.com/developers/docs/getting-started#creating-an-app) | *Lots of letters, numbers, and symbols.* |
| MONGO_URI      | A MongoDB Connection URI. [See MongoDB Documentation](https://www.mongodb.com/docs/manual/reference/connection-string)                           | `mongo://username:password@host:27017`   |
| MONGO_DATABASE | The name of the MongoDB Database.                                                                                                                | `aggregator`                             |

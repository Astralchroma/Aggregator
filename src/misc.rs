use crate::{Context, Error};

#[poise::command(slash_command)]
pub async fn status(context: Context<'_>) -> Result<(), Error> {
	let timestamp = context.data().start_time_seconds;
	let message = format!("Aggregator was started <t:{timestamp}:R> at <t:{timestamp}>.");
	context.reply(message).await?;

	Ok(())
}

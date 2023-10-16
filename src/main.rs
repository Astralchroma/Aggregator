use poise::{Framework, FrameworkOptions};
use serenity::{GatewayIntents, SerenityError};
use std::convert::Infallible;
use std::env::{var, VarError};
use thiserror::Error;

use poise::serenity_prelude as serenity;

struct Aggregator();

#[tokio::main]
async fn main() -> Result<(), AggregatorError> {
	Ok(Framework::builder()
		.options(FrameworkOptions::default())
		.token(var("DISCORD_TOKEN")?)
		.intents(GatewayIntents::empty())
		.setup(|_context, _ready, _framework| {
			Box::pin(async { Ok::<Aggregator, Infallible>(Aggregator()) })
		})
		.run()
		.await?)
}

#[derive(Debug, Error)]
#[error(transparent)]
enum AggregatorError {
	VarError(#[from] VarError),
	SerenityError(#[from] SerenityError),
}

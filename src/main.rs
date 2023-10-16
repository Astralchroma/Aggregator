#![deny(clippy::unwrap_used)]

mod misc;

use crate::misc::status;
use poise::{Framework, FrameworkOptions};
use serenity::{GatewayIntents, SerenityError};
use std::env::{var, VarError};
use std::time::SystemTime;
use thiserror::Error;

use poise::serenity_prelude as serenity;

type Error = Box<dyn std::error::Error + Send + Sync>;
type Context<'a> = poise::Context<'a, Aggregator, Error>;

pub struct Aggregator {
	start_time_seconds: u64,
}

#[tokio::main]
async fn main() -> Result<(), AggregatorError> {
	Ok(Framework::builder()
		.options(FrameworkOptions {
			commands: vec![status()],
			..Default::default()
		})
		.token(var("DISCORD_TOKEN")?)
		.intents(GatewayIntents::empty())
		.setup(|_context, _ready, _framework| {
			Box::pin(async {
				Ok(Aggregator {
					start_time_seconds: SystemTime::now()
						.duration_since(SystemTime::UNIX_EPOCH)
						.expect("current time should never be after the Unix Epoch")
						.as_secs(),
				})
			})
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

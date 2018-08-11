package de.domisum.lib.auxilium.util.time;

import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DurationUtil
{

	// MATH
	@API public static Duration getDelta(Duration duration1, Duration duration2)
	{
		return duration1.minus(duration2).abs();
	}


	// NOW
	@API public static Duration toNow(Temporal from)
	{
		return Duration.between(from, Instant.now());
	}

}

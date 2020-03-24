package io.domisum.lib.auxiliumlib.util;

import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DurationUtil
{
	
	// MATH
	@API
	public static Duration getDelta(Duration duration1, Duration duration2)
	{
		return duration1.minus(duration2).abs();
	}
	
	@API
	public static Duration min(Duration a, Duration b)
	{
		return (a.compareTo(b)<0) ?
				a :
				b;
	}
	
	@API
	public static Duration max(Duration a, Duration b)
	{
		return (a.compareTo(b)>0) ?
				a :
				b;
	}
	
	
	// FLOATING COMMA CONVERSION
	@API
	public static double getMinutesDecimal(Duration duration)
	{
		return duration.getSeconds()/(double) Duration.ofMinutes(1).getSeconds();
	}
	
	@API
	public static double getSecondsDecimal(Duration duration)
	{
		return duration.toMillis()/(double) Duration.ofSeconds(1).toMillis();
	}
	
	@API
	public static Duration fromSecondsDecimal(double seconds)
	{
		return Duration.ofMillis(Math.round(seconds*1000));
	}
	
	
	// DISPLAY
	@API
	public static String displayMinutesSeconds(Duration duration)
	{
		final int secondsInMinute = 60;
		
		long totalSeconds = (int) Math.ceil(duration.toMillis()/(double) 1000);
		long displayMinutes = totalSeconds/secondsInMinute;
		long displaySeconds = totalSeconds%secondsInMinute;
		
		String secondsString = displaySeconds+"";
		if(secondsString.length() == 1)
			secondsString = "0"+secondsString;
		
		return displayMinutes+":"+secondsString;
	}
	
	
	// NOW
	@API
	public static Duration toNow(Temporal from)
	{
		return Duration.between(from, Instant.now());
	}
	
	@API
	public static boolean isOlderThan(Instant instant, Duration duration)
	{
		var age = toNow(instant);
		return age.compareTo(duration)>0;
	}
	
	@API
	public static boolean hasPassed(Instant instant)
	{
		return instant.isBefore(Instant.now());
	}
	
}
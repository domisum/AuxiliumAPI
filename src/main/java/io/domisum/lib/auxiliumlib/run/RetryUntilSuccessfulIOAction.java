package io.domisum.lib.auxiliumlib.run;

import io.domisum.lib.auxiliumlib.contracts.iosource.ioaction.IoAction;
import io.domisum.lib.auxiliumlib.contracts.iosource.ioaction.VoidIoAction;
import io.domisum.lib.auxiliumlib.display.DurationDisplay;
import io.domisum.lib.auxiliumlib.util.java.thread.ThreadUtil;
import io.domisum.lib.auxiliumlib.util.DurationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
public class RetryUntilSuccessfulIOAction<O>
{

	private final Logger logger = LoggerFactory.getLogger(getClass());


	// CONSTANTS
	private static final Duration MAX_WAIT_DURATION = Duration.ofMinutes(30);

	// INPUT
	private final IoAction<O> action;
	private final String failMessage;


	// INIT
	public RetryUntilSuccessfulIOAction(VoidIoAction action, String failMessage)
	{
		this.action = ()->
		{
			action.execute();
			return null;
		};
		this.failMessage = failMessage;
	}


	// EXECUTE
	public O execute()
	{
		Duration wait = Duration.ofSeconds(1);
		while(true)
		{
			try
			{
				return action.execute();
			}
			catch(IOException e)
			{
				logger.warn(failMessage+" (will retry in "+DurationDisplay.of(wait)+")", e);
				ThreadUtil.sleep(wait);
			}

			wait = DurationUtil.min(wait.multipliedBy(2), MAX_WAIT_DURATION);
		}
	}

}

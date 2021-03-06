package io.domisum.lib.auxiliumlib.datacontainers.tuple;

import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@API
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class Duo<T>
{
	
	// ATTRIBUTES
	@Getter
	private final T a;
	@Getter
	private final T b;
	
	
	// GETTERS
	@API
	public Duo<T> getInverted()
	{
		return new Duo<>(b, a);
	}
	
}
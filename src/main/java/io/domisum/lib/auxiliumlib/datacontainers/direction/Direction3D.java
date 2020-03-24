package io.domisum.lib.auxiliumlib.datacontainers.direction;

import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.AllArgsConstructor;

import java.util.Random;

@API
@AllArgsConstructor
public enum Direction3D
{

	// VALUES
	NORTH(0, 0, -1),
	SOUTH(0, 0, 1),
	EAST(1, 0, 0),
	WEST(-1, 0, 0),
	UP(0, 1, 0),
	DOWN(0, -1, 0);


	// ATTRIBUTES
	public final int dX;
	public final int dY;
	public final int dZ;


	// GETTERS
	@API
	public static Direction3D getRandom(Random r)
	{
		return values()[r.nextInt(values().length)];
	}

}

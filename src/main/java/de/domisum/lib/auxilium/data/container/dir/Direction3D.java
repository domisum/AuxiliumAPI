package de.domisum.lib.auxilium.data.container.dir;

import de.domisum.lib.auxilium.util.java.annotations.API;

import java.util.Random;

@API
public enum Direction3D
{

	// @formatter:off
	NORTH(0, 0, -1),
	SOUTH(0,0,  1),
	EAST(1, 0, 0),
	WEST(-1, 0, 0),
	UP(0, 1, 0),
	DOWN(0, -1, 0);
	// @formatter:on

	public final int dX;
	public final int dY;
	public final int dZ;


	// INIT
	Direction3D(int dX, int dY, int dZ)
	{
		this.dX = dX;
		this.dY = dY;
		this.dZ = dZ;
	}


	// GETTERS
	@API public static Direction3D getRandom(Random r)
	{
		return values()[r.nextInt(values().length)];
	}

}

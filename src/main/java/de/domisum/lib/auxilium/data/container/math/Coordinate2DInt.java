package de.domisum.lib.auxilium.data.container.math;

import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@API
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Coordinate2DInt
{

	public final int x;
	public final int y;


	public Coordinate2DInt add(Coordinate2DInt other)
	{
		return new Coordinate2DInt(x+other.x, y+other.y);
	}

	public Coordinate2DInt add(int dX, int dY)
	{
		return new Coordinate2DInt(x+dX, y+dY);
	}

}
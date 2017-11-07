package de.domisum.lib.auxilium.util.math;

import de.domisum.lib.auxilium.data.container.math.Vector2D;
import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.util.java.annotations.API;

@API
public class VectorUtil
{

	// MISC
	@API public static Vector2D getDirectionVector(double yaw)
	{
		double dX = -Math.sin(yaw);
		double dZ = Math.cos(yaw);

		return new Vector2D(dX, dZ);
	}


	// MINECRAFT
	@API public static double getYawFromDirection(Vector3D direction)
	{
		return Math.toDegrees(Math.atan2(direction.z, direction.x))-90;
	}

	@API public static Vector3D getCenter(Vector3D vector)
	{
		return new Vector3D(Math.floor(vector.x)+.5, Math.floor(vector.y)+.5, Math.floor(vector.z)+.5);
	}

	@API public static Vector3D convertOffsetToMinecraftCoordinates(Vector3D offset)
	{
		return new Vector3D(-offset.x, offset.y, -offset.z);
	}


	// ROTATION
	@API public static Vector3D rotateOnXZPlane(Vector3D vector, double degrees)
	{
		double rad = Math.toRadians(-degrees);

		double nX = (vector.x*Math.cos(rad))-(vector.z*Math.sin(rad));
		double nZ = (vector.x*Math.sin(rad))+(vector.z*Math.cos(rad));

		return new Vector3D(nX, vector.y, nZ);
	}


	// DISTANCES
	@API public static double getDistanceFromLineToPoint(Vector2D l1, Vector2D l2, Vector2D p)
	{
		// is this even right? may be just for line segment instead of infinite line

		double normalLength = Math.sqrt(((l2.x-l1.x)*(l2.x-l1.x))+((l2.y-l1.y)*(l2.y-l1.y)));
		return Math.abs(((p.x-l1.x)*(l2.y-l1.y))-((p.y-l1.y)*(l2.x-l1.x)))/normalLength;
	}

}

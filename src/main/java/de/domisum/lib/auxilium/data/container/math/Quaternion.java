package de.domisum.lib.auxilium.data.container.math;

import de.domisum.lib.auxilium.util.java.annotations.APIUsage;
import de.domisum.lib.auxilium.util.math.MathUtil;

@APIUsage
public class Quaternion
{

	// PROPERTIES
	@APIUsage public final double w;
	@APIUsage public final double x;
	@APIUsage public final double y;
	@APIUsage public final double z;


	// INIT
	@APIUsage public Quaternion()
	{
		this(0, new Vector3D(1, 0, 0)); // vector can't be null-vector since this fucks everything up
	}

	@APIUsage public Quaternion(double w, double x, double y, double z)
	{
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@APIUsage public Quaternion(double angleRad, Vector3D vector)
	{
		double cos = Math.cos(angleRad/2);
		double sin = Math.sin(angleRad/2);

		this.w = cos;
		this.x = sin*vector.x;
		this.y = sin*vector.y;
		this.z = sin*vector.z;
	}

	@Override public String toString()
	{
		return "quaternion[w="+MathUtil.round(this.w, 3)+",x="+MathUtil.round(this.x, 3)+",y="+MathUtil.round(this.y, 3)+",z="
				+MathUtil.round(this.z, 3)+"]";
	}


	@APIUsage public static Quaternion getRotationFromTo(Vector3D vector1, Vector3D vector2)
	{
		double norm = Math.sqrt(vector1.lengthSquared()*vector2.lengthSquared());
		double w = norm+vector1.dotProduct(vector2);

		Vector3D axis;
		if(w < (1.e-5d*norm))
		{
			w = 0;
			// noinspection SuspiciousNameCombination
			axis = vector1.x > vector1.z ? new Vector3D(-vector1.y, vector1.x, 0) : new Vector3D(0, -vector1.z, vector1.y);
		}
		else
			axis = vector1.crossProduct(vector2);

		return new Quaternion(-w, axis.x, axis.y, axis.z).normalize(); // idfk why -w, but whatever
	}


	// SELF
	@APIUsage public double length()
	{
		return Math.sqrt((this.w*this.w)+(this.x*this.x)+(this.y*this.y)+(this.z*this.z));
	}

	@APIUsage public Quaternion normalize()
	{
		return multiply(1/length());
	}

	@APIUsage public Quaternion inverse()
	{
		double d = (this.w*this.w)+(this.x*this.x)+(this.y*this.y)+(this.z*this.z);
		return new Quaternion(this.w/d, -this.x/d, -this.y/d, -this.z/d);
	}

	@APIUsage public Quaternion conjugate()
	{
		return new Quaternion(this.w, -this.x, -this.y, -this.z);
	}

	@APIUsage public Vector3D getVector()
	{
		return new Vector3D(this.x, this.y, this.z);
	}


	// COMBINE
	@APIUsage public Quaternion add(Quaternion b)
	{
		return new Quaternion(this.w+b.w, this.x+b.x, this.y+b.y, this.z+b.z);
	}

	@APIUsage public Quaternion multiply(Quaternion b)
	{
		double nW = (this.w*b.w)-(this.x*b.x)-(this.y*b.y)-(this.z*b.z);
		double nX = ((this.w*b.x)+(this.x*b.w)+(this.y*b.z))-(this.z*b.y);
		double nY = ((this.w*b.y)-(this.x*b.z))+(this.y*b.w)+(this.z*b.x);
		double nZ = (((this.w*b.z)+(this.x*b.y))-(this.y*b.x))+(this.z*b.w);
		return new Quaternion(nW, nX, nY, nZ);
	}

	@APIUsage public Quaternion multiply(double d)
	{
		return new Quaternion(this.w*d, this.x*d, this.y*d, this.z*d);
	}

	@APIUsage public Quaternion divide(Quaternion b)
	{
		return multiply(b.inverse());
	}

}

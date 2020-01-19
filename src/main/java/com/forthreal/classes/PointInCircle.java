package com.forthreal.classes;

import com.forthreal.exceptions.ShapeOutOfBoundaries;

public class PointInCircle
{
	private int circleRadius;
	private int circleCentreX;
	private int circleCentreY;
	private int canvasWidth;
	private int canvasHeight;
	
	public void setCircle
	 (
		int radius,
		int centreX,
		int centreY,
		int canvasWidth,
		int canvasHeight
	 ) throws ShapeOutOfBoundaries
	{
		if( ( (circleCentreX + circleRadius) > canvasWidth) ||
			( (circleCentreY + circleRadius) > canvasHeight )
		  )
		{
			ShapeOutOfBoundaries shapeOut = new ShapeOutOfBoundaries();
			throw shapeOut;
		}
		
		if( (radius > centreX) || (radius > centreY) )
		{
			ShapeOutOfBoundaries shapeOut = new ShapeOutOfBoundaries();
			throw shapeOut;
		}

		this.circleRadius = radius;
		this.circleCentreX = centreX;
		this.circleCentreY = centreY;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		
	}
	
	private float pointDistance(int pointX, int pointY)
	{
		/*
		 * distance = sqrt( (pointX - centreX)^2 + (pointY - centreY)^2 ) 
		 */
		
		float distance = (float)
			Math.sqrt
			(
				Math.pow( (float) pointX - circleCentreX, 2) +
				Math.pow( (float) pointY - circleCentreY, 2)
			);
		
		return distance;
	}
	
	public boolean testClickedInCircle( int x, int y )
	{
		return ( pointDistance(x,y) <=  (float) circleRadius );
	}
}

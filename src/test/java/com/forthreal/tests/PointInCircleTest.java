package com.forthreal.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.forthreal.classes.PointInCircle;
import com.forthreal.exceptions.ShapeOutOfBoundaries;

@DisplayName("Checking if we can correctly compute inclusion of points within circles")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PointInCircleTest
{
	static PointInCircle pointInCircle;
	
	@BeforeAll
	static public void setup()
	{
		pointInCircle = new PointInCircle();
	}
	
	@Test
	@DisplayName("Checking if setting of wrong circle coordinates generates an exception")
	@Order(1)
	public void setWrongCoordTest()
	{
		Assertions.assertThrows
		  (
			ShapeOutOfBoundaries.class,
			() -> {
				pointInCircle.setCircle( 100, 50, 50, 20, 200);				
			}
		  );
	}

	@Test
	@DisplayName("Checking if setting of wrong circle centre position generates an exception")
	@Order(0)
	public void setWrongCentreTest()
	{
		Assertions.assertThrows
		  (
			ShapeOutOfBoundaries.class,
			() -> {
				pointInCircle.setCircle( 51, 50, 50, 200, 200);				
			}
		  );
	}
	
	@Test
	@DisplayName("Checking if a point is computed outside circle")
	@Order(2)
	public void testOutside()
	{
		Assertions.assertDoesNotThrow(() -> {
			pointInCircle.setCircle(10, 20, 20, 20, 20);	
		});
		
		Assertions.assertFalse( pointInCircle.testClickedInCircle(11, 11) );
	}

	@Test
	@DisplayName("Checking if a point is computed inside circle")
	@Order(3)
	public void testInside()
	{
		pointInCircle = new PointInCircle();
		
		Assertions.assertDoesNotThrow(() -> {
			pointInCircle.setCircle(10, 20, 20, 20, 20);
		});
		
		Assertions.assertTrue( pointInCircle.testClickedInCircle( 13, 13) );
	}
}

package com.forthreal.apps;

import javax.imageio.stream.FileImageInputStream;

import com.forthreal.classes.PointInCircle;
import com.forthreal.exceptions.ShapeOutOfBoundaries;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.fxml.FXMLLoader;

public class View extends Application
{
	private int constraintX = 400;
	private int constraintY = 400;
	
	private String currentDir;
	private Text leftText;
	private Text rightText;
		
	private GraphicsContext graphicContext;
	private GraphicsContext savedGraphicContext;
	
	private boolean startedMoving = false;
	
	private boolean ctrlPressed = false;
	private boolean lockedForCopy = false;
	private boolean copied = false;
	
	class ImagePosition
	{
		public int x;
		public int y;
		public int width;
		public int height;
		
		private Image image = null;
		
		public void setPosition(int x, int y, int width, int height)
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public void setImage(Image image)
		{
			this.image = image;
		}
		
		public Image getImage()
		{
			return this.image;
		}
	}
	
	private List<Tuple2<ImagePosition,PointInCircle>> circles;
	/* set cursor position to fake coords for initialisation purposes */
	private int cursorX = constraintX;
	private int cursorY = constraintY;
	
	@Override
	public void start(Stage stage) throws Exception
	{
		Tuple2 tuple = Tuple.of(new ImagePosition(), new PointInCircle());
		
		currentDir = System.getProperty("user.dir");
		
		leftText = new Text(10,constraintY + 10,"Text");
		rightText = new Text(10,constraintY + 22,"");
		
		circles = List.of( tuple );
		
		/* the window */
		Group root = new Group();
		
		/* the logical scene */
		Scene scene = new Scene( root, constraintX, constraintY + 30 );
		stage.setScene( scene );

		Canvas newCanvas = new Canvas( constraintX, constraintY );
		graphicContext  = newCanvas.getGraphicsContext2D();
		
		root.getChildren().add( newCanvas );
		root.getChildren().add( leftText );
		root.getChildren().add( rightText );

		/* set up the graphics */
		doGraphics( graphicContext  );
		/* register event handlers */
		registerEvents( root );
		
		stage.setTitle("Dijkstra Algorithm Grapher");
		stage.show();
		
		/* without this keyboard events won't work */
		root.requestFocus();
	}

	/* check if there is any image object which has the cursor over it */
	private Option<Integer> pointerIsOverObject( int pointerX, int pointerY )
	{
		return io.vavr.collection.Stream.range(0, circles.size()).
				filter( i -> circles.get(i)._2.testClickedInCircle( pointerX, pointerY) ).
				headOption();
		
	}
	
	/* make a new object with an image */
	private void spawnObject( int x, int y)
	{
		/* copy the image from the first object */
		Image buttonImage = circles.get(0)._1.getImage();

		ImagePosition imagePosition = new ImagePosition();
		imagePosition.setImage( buttonImage );

		graphicContext.drawImage( buttonImage, x, y );
				
		PointInCircle pointInCircle = new PointInCircle();

		int radius = (int) buttonImage.getWidth() / 2;
		/* fixate the position of the object for the future */
		try
		{
			pointInCircle.setCircle
			    (
			      radius,
			      x + radius,
			      y + radius,
				  constraintX,
				  constraintY
			    );
		}
		catch(Exception exc)
		{
			
		}
		
		/* update the current position of the object */
		imagePosition.setPosition
		  (
			x,
			y,
			(int) buttonImage.getWidth(),
			(int) buttonImage.getHeight()
		  );

		Tuple2 tuple = Tuple.of( imagePosition, pointInCircle );
		circles = circles.append( tuple );
		
		//rightText.setText("X: " + x + " Y: " + y);
		
		copied = true;
	
	}
	
	/* the setup of the graphics part is done here */
	private void doGraphics(GraphicsContext graphicCtx)
	{
		Image buttonImage =
			new Image("file:" + currentDir + "\\src\\main\\resources\\yellow_button_25px.png");
			
		graphicCtx.drawImage( buttonImage, 100, 100);
		
		circles.get(0)._1.setImage( buttonImage );
		circles.get(0)._1.setPosition
		  (
			100,
			100,
			(int) buttonImage.getWidth(),
			(int) buttonImage.getHeight()
		  );
		
		int circleCentreX = 100 + (int) buttonImage.getWidth() / 2;
		int circleCentreY = 100 + (int) buttonImage.getHeight() / 2;
		
		try
		{
			circles.get(0)._2.setCircle
			 (
			   (int) buttonImage.getHeight() / 2,
			   circleCentreX,
			   circleCentreY,
			   constraintX,
			   constraintY
			 );
		}
		catch(ShapeOutOfBoundaries exc)
		{
			
		}
	}
	
	/* keyboard events */
	
	private void handleKeyboardEvent(KeyEvent event)
	{
		/* if the control key is pressed, we're in the copy mode */
		if(
			( event.getEventType() == KeyEvent.KEY_PRESSED ) &&
			( event.isControlDown() == true )
		  )
		{
			ctrlPressed = true;
		}
		else
		{
			ctrlPressed = false;
		}
		
		/* check if the cursor is over any object */
		Option< Integer > objectUnderPointer = pointerIsOverObject( cursorX, cursorY );
		
		/* if the cursor is over any object */
		if( objectUnderPointer.isEmpty() == false )
		{
			String text = leftText.getText();

			if(ctrlPressed == true)
			{	
				text += " locked for copy";
				lockedForCopy = true;
			}
			else
			{
				if( text.contains(" locked for copy") == true )
				{
					int position = text.indexOf(" locked for copy");
					text = text.substring( 0, position );
					
				}
				
				lockedForCopy = false;
			}

			leftText.setText( text );
		}
	}
	
	/* this function is for the case when user drags an object */
	private void handleMouseDraggingOverObject(int objectNum)
	{
		String positionText =
				"Objects: " + circles.size() + " Cursor: " + (int) cursorX + " : " + (int) cursorY;
		
		/* check if the click comes to the circle */
		if( circles.get(objectNum)._2.testClickedInCircle ( cursorX, cursorY ) )
		{
			/* set the object to move */
			startMoving();
			
		}
		
		/* if moving */
		if(isMoving() == true)
		{
			int xLowerLimit = (int) ( cursorX - circles.get(objectNum)._1.getImage().getWidth() / 2 );
			int yLowerLimit = (int) ( cursorY - circles.get(objectNum)._1.getImage().getHeight() / 2 );

			int xUpperLimit = (int) ( cursorX + circles.get(objectNum)._1.getImage().getWidth() / 2 );
			int yUpperLimit = (int) ( cursorY + circles.get(objectNum)._1.getImage().getHeight() / 2 );

			
			/* check that we're within the boundaries of the canvas */
			if(
				(xLowerLimit > 0) &&
				(yLowerLimit > 0) &&
				(xUpperLimit < constraintX) &&
				(yUpperLimit < constraintY)
			)
			{
				int radius = (int) circles.get(objectNum)._1.getImage().getWidth() / 2;

				/* if don't want the depiction of the object in the
				 * old position to stay, so let's wipe it out */
				graphicContext.clearRect
				  (
					circles.get(objectNum)._1.x,
					circles.get(objectNum)._1.y,
					circles.get(objectNum)._1.width,
					circles.get(objectNum)._1.height);
				
				/* draw the image with the centre being at the tip of the cursor */
				graphicContext.drawImage
				   (
					 circles.get(objectNum)._1.getImage(),
					 cursorX - radius,
					 cursorY - radius
				   );
				
				/* fixate the position of the object for the future */
				try
				{
					circles.get(objectNum)._2.setCircle
					    (
					      radius,
					      cursorX,
					      cursorY,
						  constraintX,
						  constraintY
					    );
				}
				catch(Exception exc)
				{
					
				}
				
				/* update the current position of the object */
				circles.get(objectNum)._1.setPosition
				  (
					cursorX - radius,
					cursorY - radius,
					circles.get(objectNum)._1.width,
					circles.get(objectNum)._1.height
				  );
				
				if( ( lockedForCopy == true ) && ( copied == false ) )
				{
					spawnObject( cursorX + radius, cursorY + radius );
				}
			}
		}
		leftText.setText( positionText );		
	}
	
	/* handling of mouse events: Dragging and Mouse Move */
	private void handleMouseEvent(MouseEvent event)
	{
		cursorX = (int) event.getX();
		cursorY = (int) event.getY();
		
		String positionText =
			"Objects: " + circles.size() + " Cursor: " + (int) cursorX + " : " + (int) cursorY;

		
		/* check if the cursor is over any object */
		Option< Integer > objectUnderPointer = pointerIsOverObject( cursorX, cursorY );
		
		if(objectUnderPointer.isEmpty() == false)
		{
			positionText += " over " + (objectUnderPointer.get() + 1);
			
			if( ctrlPressed == true )
			{
				positionText += " locked for copy";
				lockedForCopy = true;
			}
			else
			{
				lockedForCopy = false;
			}
		}
		else
		{
			lockedForCopy = false;
		}
		
		/* if we're dragging a circle */
		if(
			( event.getEventType() == MouseEvent.MOUSE_DRAGGED ) &&
			( event.getButton() == MouseButton.PRIMARY ) &&
			( objectUnderPointer.isEmpty() == false )
		  )
		{
			handleMouseDraggingOverObject( objectUnderPointer.get() );
		}
		if(event.getEventType() == MouseEvent.MOUSE_MOVED)
		{
			leftText.setText( positionText );
			copied = false;
		}
		else
		{
			//newText.setText("");
			
			stopMoving();
		}
	}

	private void startMoving()
	{
		startedMoving = true;
	}
	
	private void stopMoving()
	{
		startedMoving = false;
	}
	
	private boolean isMoving()
	{
		return startedMoving;
	}
	
	private void registerEvents(Group eventGroup)
	{
		
		/* capture keyboard presses */
		
		eventGroup.addEventHandler
		  (
			KeyEvent.KEY_PRESSED,
			new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event)
				{
					handleKeyboardEvent(event);
				}
			}
		  );

		eventGroup.addEventHandler
		  (
			KeyEvent.KEY_RELEASED,
			new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event)
				{
					handleKeyboardEvent(event);
				}
			}
		  );
		
		/* capture Mouse actions */
		
		eventGroup.addEventHandler
		  (
			MouseEvent.MOUSE_DRAGGED,
			new EventHandler<MouseEvent>()
			{
				@Override
				public void handle(MouseEvent event)
				{
					handleMouseEvent(event);
				}
			}
		  );
		
		eventGroup.addEventHandler
		  (
			MouseEvent.MOUSE_RELEASED,
			new EventHandler<MouseEvent>()
			{
				@Override
				public void handle(MouseEvent event)
				{
					handleMouseEvent(event);
				}				
			}
		  );
		
		eventGroup.addEventHandler
		  (
			MouseEvent.MOUSE_MOVED,
		    new EventHandler<MouseEvent>()
		    {
				@Override
				public void handle(MouseEvent event)
				{
					handleMouseEvent(event);
				}
		    }
		  );
	}
	
	/*
 		Line newLine = new Line(0,0,150,150);
		newLine.setStroke(Color.GREEN);
		
		Circle newCircle = new Circle(200,200,100);
		newCircle.setStroke(Color.GREEN);
		

		root.getChildren().add( newLine );
		root.getChildren().add( newCircle );
		root.getChildren().add( newText );
	 */
	
	static public void main(String[] args)
	{
		Application.launch( args );
	}

}

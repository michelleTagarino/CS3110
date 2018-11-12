/*
    Author: Michelle Joy Tagarino   ID: 5029967
    Assignment: Lab Assignment #5
    Purpose: User Interactive Program with Shapes
*/
package interactiveshapes;

import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.TraversalEngine;
import java.io.File;
import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;

class ShapePane extends ClippingPane {}

public class InteractiveShapes extends Application {
    
    Scene scene;
    
    protected ShapePane root; //Holds all the shapes
    protected BorderPane container;
    protected HBox toolbar;
    protected Shape selectedShape = null;
    protected Paint selectedShapeStroke = null;
    protected double selectedStrokeWidth = 0;
    protected double deltaX, deltaY, pressedX, pressedY, diffX, diffY, destX, destY;
    protected double width = 0, height = 0, size = 65, angle = 0;
    protected int numberOfShapes = 0, count = 0, clickCount;
    Rectangle ro;
    Ellipse eo;
    Text to;
    Font font;
    TranslateTransition rt;
    Button newRectangle, newEllipse, newText, rotateLeft, rotateRight, incSize, decSize;
    CheckBox flickOn;
    ToggleGroup flickGroup;
    Boolean mouseClicked = false;
    
    private void selectShape(Shape s) {
        if (selectedShape != null) {
            selectedShape.setStrokeWidth(selectedStrokeWidth);
            selectedShape.setStroke(selectedShapeStroke);
        }
        selectedShape = s; //Store the current shape
        selectedStrokeWidth = s.getStrokeWidth();
        selectedShapeStroke = s.getStroke();
        if (selectedShape instanceof Text) {
            s.setStrokeWidth(2);
            s.setStroke(Color.WHITE);
        }
        else {
            s.setStrokeWidth(5);
            s.setStroke(Color.WHITE);
        }
    }
    private void deselectShape() {
        if (selectedShape != null) {
            selectedShape.setStrokeWidth(selectedStrokeWidth);
            selectedShape.setStroke(selectedShapeStroke);
            selectedShape = null;
        }
    }
    @Override
    public void start(Stage primaryStage) {
        
        root = new ShapePane();
        container = new BorderPane();
        toolbar = new HBox();
        
        toolbar.setPadding(new Insets(5,10,5,10));
        toolbar.setSpacing(5);

        newRectangle = new Button("\u25A0");
        newRectangle.getStyleClass().add("buttons");
        newRectangle.setTooltip(new Tooltip("Add Rectangle"));
        newRectangle.setOnMouseEntered( e-> newRectangle.getStyleClass().add("buttons-hover"));
        newRectangle.setOnMouseExited ( e-> newRectangle.getStyleClass().remove("buttons-hover"));
        newRectangle.setOnAction(e -> {
            rectangleEvents();
        });

        newEllipse = new Button("\u25EF");
        newEllipse.getStyleClass().add("buttons");
        newEllipse.setTooltip(new Tooltip("Add Ellipse"));
        newEllipse.setOnMouseEntered( e-> newEllipse.getStyleClass().add("buttons-hover"));
        newEllipse.setOnMouseExited ( e-> newEllipse.getStyleClass().remove("buttons-hover"));
        newEllipse.setOnAction(e -> {
            ellipseEvents();
        });

        newText = new Button("A");
        newText.getStyleClass().add("buttons");
        newText.setTooltip(new Tooltip("Add Text"));
        newText.setOnMouseEntered( e-> newText.getStyleClass().add("buttons-hover"));
        newText.setOnMouseExited ( e-> newText.getStyleClass().remove("buttons-hover"));
        newText.setOnAction(e -> {
            textEvents();
        });
        
        rotateLeft = new Button("\u27F2");
        rotateLeft.getStyleClass().add("buttons");
        rotateLeft.setTooltip(new Tooltip("Rotate Left"));
        rotateLeft.setOnMouseEntered( e-> rotateLeft.getStyleClass().add("buttons-hover"));
        rotateLeft.setOnMouseExited ( e-> rotateLeft.getStyleClass().remove("buttons-hover"));
        
        rotateRight = new Button("\u27F3");
        rotateRight.getStyleClass().add("buttons");
        rotateRight.setTooltip(new Tooltip("Rotate Right"));
        rotateRight.setOnMouseEntered( e-> rotateRight.getStyleClass().add("buttons-hover"));
        rotateRight.setOnMouseExited ( e-> rotateRight.getStyleClass().remove("buttons-hover"));
        
        incSize = new Button("+");
        incSize.getStyleClass().add("buttons");
        incSize.setTooltip(new Tooltip("Increase Size"));
        incSize.setOnMouseEntered( e-> incSize.getStyleClass().add("buttons-hover"));
        incSize.setOnMouseExited ( e-> incSize.getStyleClass().remove("buttons-hover"));
        
        decSize = new Button("-");
        decSize.getStyleClass().add("buttons");
        decSize.setTooltip(new Tooltip("Decrease Size"));
        decSize.setOnMouseEntered( e-> decSize.getStyleClass().add("buttons-hover"));
        decSize.setOnMouseExited ( e-> decSize.getStyleClass().remove("buttons-hover"));
        
        flickOn  = new CheckBox("FLICK ON");
        flickOn.getStyleClass().add("checkbox");
        flickOn.setTooltip(new Tooltip("Enable Flick to Allow Self-Floating Objects"));
        
        toolbar.getChildren().addAll(newRectangle, newEllipse, newText, rotateLeft,rotateRight,incSize,decSize,flickOn);
        container.setTop(toolbar);
        container.setCenter(root);
        
        toolbar.getStyleClass().add("toolbar");
        root.getStyleClass().add("shapePane");
        
//------Disable buttons when toolbar is not hovered over
        /*
        newRectangle.setDisable(true);

        rotateLeft.setDisable(true);
        rotateRight.setDisable(true);
        
        incSize.setDisable(true);
        decSize.setDisable(true);
        
        flickOn.setDisable(true);
        
        toolbar.setOnMouseEntered(e ->{
            newRectangle.setDisable(false);
            
            rotateLeft.setDisable(false);
            rotateRight.setDisable(false);
            
            incSize.setDisable(false);
            decSize.setDisable(false);

            flickOn.setDisable(false);
            
            root.requestFocus();
        });
        
        toolbar.setOnMouseExited(e ->{
            
            rotateLeft.setDisable(true);
            rotateRight.setDisable(true);
            
            incSize.setDisable(true);
            decSize.setDisable(true);

            flickOn.setDisable(true);
            
            root.requestFocus();
        });
        */


        
        root.setOnKeyPressed(e->{
            if (e.getCode() == KeyCode.R) rectangleEvents();
            if (e.getCode() == KeyCode.E) ellipseEvents();
            if (e.getCode() == KeyCode.T) textEvents();
            if (e.getCode() == KeyCode.LEFT){
                if (selectedShape != null) {
                    if (selectedShape instanceof Rectangle) {
                        Rectangle ro = (Rectangle)selectedShape;
                        ro.setX(ro.getX()-3);
                    }
                    if (selectedShape instanceof Ellipse) {
                        Ellipse eo = (Ellipse)selectedShape;
                        eo.setCenterX(eo.getCenterX()-3);
                    }
                    if (selectedShape instanceof Text) {
                        Text to = (Text)selectedShape;
                        to.setX(to.getX()-3);
                    }
                }
            }
            if (e.getCode() == KeyCode.RIGHT){
                if (selectedShape != null) {
                    if (selectedShape instanceof Rectangle) {
                        Rectangle ro = (Rectangle)selectedShape;
                        ro.setX(ro.getX()+3);
                    }
                    if (selectedShape instanceof Ellipse) {
                        Ellipse eo = (Ellipse)selectedShape;
                        eo.setCenterX(eo.getCenterX()+3);
                    }
                    if (selectedShape instanceof Text) {
                        Text to = (Text)selectedShape;
                        to.setX(to.getX()+3);
                    }
                }
            }
            if (e.getCode() == KeyCode.UP){
                if (selectedShape != null) {
                    if (selectedShape instanceof Rectangle) {
                        Rectangle ro = (Rectangle)selectedShape;
                        ro.setY(ro.getY()-3);
                    }
                    if (selectedShape instanceof Ellipse) {
                        Ellipse eo = (Ellipse)selectedShape;
                        eo.setCenterY(eo.getCenterY()-3);
                    }
                    if (selectedShape instanceof Text) {
                        Text to = (Text)selectedShape;
                        to.setY(to.getY()-3);
                    }
                }
            }
            if (e.getCode() == KeyCode.DOWN){
                if (selectedShape != null) {
                    if (selectedShape instanceof Rectangle) {
                        Rectangle ro = (Rectangle)selectedShape;
                        ro.setY(ro.getY()+3);
                    }
                    if (selectedShape instanceof Ellipse) {
                        Ellipse eo = (Ellipse)selectedShape;
                        eo.setCenterY(eo.getCenterY()+3);
                    }
                    if (selectedShape instanceof Text) {
                        Text to = (Text)selectedShape;
                        to.setY(to.getY()+3);
                    }
                }
            }
            if (e.getCode() == KeyCode.COMMA){
                if (selectedShape != null) {
                    if (selectedShape instanceof Rectangle) {
                        Rectangle ro = (Rectangle)selectedShape;
                        angle = angle - 5;
                        ro.setRotate(angle);
                        if (angle < 0) angle = 360;
                    }
                    if (selectedShape instanceof Ellipse) {
                        Ellipse eo = (Ellipse)selectedShape;
                        angle = angle - 5;
                        eo.setRotate(angle);
                        if (angle < 0) angle = 360;
                    }
                    if (selectedShape instanceof Text) {
                        Text to = (Text)selectedShape;
                        angle = angle - 5;
                        to.setRotate(angle);
                        if (angle < 0) angle = 360;
                    }
                }
            }
            if (e.getCode() == KeyCode.PERIOD){
                if (selectedShape != null) {
                    if (selectedShape instanceof Rectangle) {
                        Rectangle ro = (Rectangle)selectedShape;
                        angle = angle + 5;
                        ro.setRotate(angle);
                        if (angle > 360) angle = 0;
                    }
                    if (selectedShape instanceof Ellipse) {
                        Ellipse eo = (Ellipse)selectedShape;
                        angle = angle + 5;
                        eo.setRotate(angle);
                        if (angle > 360) angle = 0;
                    }
                    if (selectedShape instanceof Text) {
                        Text to = (Text)selectedShape;
                        angle = angle + 5;
                        to.setRotate(angle);
                        if (angle > 360) angle = 0;
                    }
                }
            }
            if (e.getCode() == KeyCode.Z) {
                if (selectedShape != null) {
                    if (selectedShape instanceof Rectangle) {
                        Rectangle ro = (Rectangle)selectedShape;
                        width = ro.getWidth() + 2;
                        height = ro.getHeight() + 2;
                        ro.setWidth(width);
                        ro.setHeight(height);
                    }
                    if (selectedShape instanceof Ellipse) {
                        Ellipse o = (Ellipse)selectedShape;
                        width = o.getRadiusX() + 2;
                        height = o.getRadiusY() + 2;
                        o.setRadiusX(width);
                        o.setRadiusY(height);
                    }
                    if (selectedShape instanceof Text) {
                        Text o = (Text)selectedShape;
                        size++;
                        o.setFont(Font.font("Courier New",size));
                    }
                }
            }
            if (e.getCode() == KeyCode.X) {
                if (selectedShape != null) {
                    if (selectedShape instanceof Rectangle) {
                        Rectangle o = (Rectangle)selectedShape;
                        width  = o.getWidth() - 2;
                        height = o.getHeight()- 2;
                        o.setWidth(width);
                        o.setHeight(height);
                    }
                    if (selectedShape instanceof Ellipse) {
                        Ellipse o = (Ellipse)selectedShape;
                        width  = o.getRadiusX() - 2;
                        height = o.getRadiusY() - 2;
                        o.setRadiusX(width);
                        o.setRadiusY(height);
                    }
                    if (selectedShape instanceof Text) {
                        Text o = (Text)selectedShape;
                        size--;
                        o.setFont(Font.font("Courier New",size));
                    }
                }
            }
            if (e.getCode() == KeyCode.TAB){
                
                if (count >= numberOfShapes) count = 0;
                
                if (root.getChildren().get(count) instanceof Rectangle){
                    selectShape((Rectangle)root.getChildren().get(count));
                }
                else if (root.getChildren().get(count) instanceof Ellipse){
                    selectShape((Ellipse)root.getChildren().get(count));
                }
                else if (root.getChildren().get(count) instanceof Text){
                    selectShape((Text)root.getChildren().get(count));
                }
                count++;
            }
        });
        
        File f = new File("custom-styles.css");
        scene = new Scene(container, 854, 480);
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        root.requestFocus();
        
        primaryStage.setTitle("Assignment #5: User Interaction with Shapes!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void rectangleEvents() {
        numberOfShapes++;
        Rectangle ro;
        root.getChildren().add(ro=new Rectangle(root.getWidth()/2,root.getHeight()/2,75,75));
        ro.getStyleClass().add("customRect");
        
        rotateLeft.setOnAction(e -> {
            angle = angle - 20;
            ro.setRotate(angle);
            if (angle > 360) angle = 0;
        });
        
        rotateRight.setOnAction(e -> {
            angle = angle + 20;
            ro.setRotate(angle);
            if (angle < 0) angle = 360;
        });
        
        incSize.setOnAction(e -> {
            width = ro.getWidth() + 2;
            height = ro.getHeight() + 2;
            ro.setWidth(width);
            ro.setHeight(height);
        });
        
        decSize.setOnAction(e -> {
            width = ro.getWidth() - 2;
            height = ro.getHeight() - 2;
            ro.setWidth(width);
            ro.setHeight(height);
        });
        
        flickOn.setOnAction(e->{
            if (flickOn.isSelected()) mouseClicked = true;
            else mouseClicked = false;
        });
        
        ro.setOnMousePressed(er->{
            
            selectShape((Shape)er.getSource());
            deltaX = er.getX()-ro.getX();
            deltaY = er.getY()-ro.getY();
            pressedX = er.getX();
            pressedY = er.getY();
            clickCount = er.getClickCount();
            
            er.consume();
        });
        
        ro.setOnMouseDragged(er->{
            ro.setX(er.getX()-deltaX);
            ro.setY(er.getY()-deltaY);
            
            if (mouseClicked){
                if (er.getX() < pressedX){
                    if (er.getY() < pressedY){
                        diffX = (pressedX-er.getX())*5;
                        diffY = (pressedY-er.getY())*5;
                        destX = deltaX-diffX;
                        destY = deltaY-diffY;
                        shapeFlick(ro,destX,destY);
                    }
                    else{
                        diffX = (er.getX()-pressedX)*5;
                        diffY = (er.getY()-pressedY)*5;
                        destX = deltaX+diffX;
                        destY = deltaY+diffY;
                        shapeFlick(ro,destX,destY);
                    }
                }
                else{
                    if (er.getY() > pressedY){
                        diffX = (er.getX()-pressedX)*5;
                        diffY = (er.getY()-pressedY)*5;
                        destX = deltaX+diffX;
                        destY = deltaY+diffY;
                        shapeFlick(ro,destX,destY);
                    }
                    else{
                        diffX = (pressedX-er.getX())*5;
                        diffY = (pressedY-er.getY())*5;
                        destX = deltaX-diffX;
                        destY = deltaY-diffY;
                        shapeFlick(ro,destX,destY);
                    }
                }
            }
            if (destX < -800 || destY < -500 || destX > 800 || destY > 500) {
                numberOfShapes--;
                root.getChildren().remove(ro);
                deselectShape();
                System.out.println("Node Removed");
            }
            destX = 0;
            destY = 0;
            er.consume();
        });
    }
    public void ellipseEvents() {
        numberOfShapes++;
        Ellipse eo;
        root.getChildren().add(eo=new Ellipse(root.getWidth()/2,root.getHeight()/2,65,50));
        eo.getStyleClass().add("customEllp");
        
        rotateLeft.setOnAction(e -> {
            angle = angle - 20;
            eo.setRotate(angle);
            if (angle > 360) angle = 0;
        });
        
        rotateRight.setOnAction(e -> {
            angle = angle + 20;
            eo.setRotate(angle);
            if (angle < 0) angle = 360;
        });
        
        incSize.setOnAction(e -> {
            width = eo.getRadiusX() + 2;
            height = eo.getRadiusY() + 2;
            eo.setRadiusX(width);
            eo.setRadiusY(height);
        });
        
        decSize.setOnAction(e -> {
            width = eo.getRadiusX() - 2;
            height = eo.getRadiusY() - 2;
            eo.setRadiusX(width);
            eo.setRadiusY(height);
        });
        
        flickOn.setOnAction(e->{
            if (flickOn.isSelected()) mouseClicked = true;
            else mouseClicked = false;
        });
        
        eo.setOnMousePressed(er->{
            selectShape((Shape)er.getSource());
            deltaX = er.getX()-eo.getCenterX();
            deltaY = er.getY()-eo.getCenterY();
            pressedX = er.getX();
            pressedY = er.getY();
            clickCount = er.getClickCount();
            er.consume();
        }); 
        eo.setOnMouseDragged(er->{
            eo.setCenterX(er.getX()-deltaX);
            eo.setCenterY(er.getY()-deltaY);
            
            if (mouseClicked){
                if (er.getX() < pressedX){
                    if (er.getY() < pressedY){
                        diffX = (pressedX-er.getX())*5;
                        diffY = (pressedY-er.getY())*5;
                        destX = deltaX-diffX;
                        destY = deltaY-diffY;
                        shapeFlick(eo,destX,destY);
                    }
                    else{
                        diffX = (er.getX()-pressedX)*5;
                        diffY = (er.getY()-pressedY)*5;
                        destX = deltaX+diffX;
                        destY = deltaY+diffY;
                        shapeFlick(eo,destX,destY);
                    }
                }
                else{
                    if (er.getY() > pressedY){
                        diffX = (er.getX()-pressedX)*5;
                        diffY = (er.getY()-pressedY)*5;
                        destX = deltaX+diffX;
                        destY = deltaY+diffY;
                        shapeFlick(eo,destX,destY);
                    }
                    else{
                        diffX = (pressedX-er.getX())*5;
                        diffY = (pressedY-er.getY())*5;
                        destX = deltaX-diffX;
                        destY = deltaY-diffY;
                        shapeFlick(eo,destX,destY);
                    }
                }
                System.out.println("" + (root.getChildren().get(0)).getLayoutX());
            }
            if (destX < -800 || destY < -500 || destX > 800 || destY > 500) {
                numberOfShapes--;
                root.getChildren().remove(eo);
                deselectShape();
                System.out.println("Node Removed");
            }
            destX = 0;
            destY = 0;
            er.consume();
        });
    }
    public void textEvents() {
        numberOfShapes++;
        Text o;
        root.getChildren().add(o=new Text(root.getWidth()/2,root.getHeight()/2,"I AM A TEXT"));
        o.getStyleClass().add("customfont");
        
        rotateLeft.setOnAction(e -> {
            angle = angle - 20;
            o.setRotate(angle);
            if (angle > 360) angle = 0;
        });
        
        rotateRight.setOnAction(e -> {
            angle = angle + 20;
            o.setRotate(angle);
            if (angle < 0) angle = 360;
        });
        
        incSize.setOnAction(e -> {
            size++;
            o.setFont(Font.font("Courier New",size));
        });
        
        decSize.setOnAction(e -> {
            size--;
            o.setFont(Font.font("Courier New",size));
        });
        
        flickOn.setOnAction(e->{
            if (flickOn.isSelected()) mouseClicked = true;
            else mouseClicked = false;
        });
        
        o.setOnMousePressed(er->{
            selectShape((Shape)er.getSource());
            deltaX = er.getX()-o.getX();
            deltaY = er.getY()-o.getY();
            pressedX = er.getX();
            pressedY = er.getY();
            clickCount = er.getClickCount();
            er.consume();
        }); 
        
        o.setOnMouseDragged(er->{
            o.setX(er.getX()-deltaX);
            o.setY(er.getY()-deltaY);
            
            if (mouseClicked){
                if (er.getX() < pressedX){
                    if (er.getY() < pressedY){
                        diffX = (pressedX-er.getX())*5;
                        diffY = (pressedY-er.getY())*5;
                        destX = deltaX-diffX;
                        destY = deltaY-diffY;
                        shapeFlick(o,destX,destY);
                    }
                    else{
                        diffX = (er.getX()-pressedX)*5;
                        diffY = (er.getY()-pressedY)*5;
                        destX = deltaX+diffX;
                        destY = deltaY+diffY;
                        shapeFlick(o,destX,destY);
                    }
                }
                else{
                    if (er.getY() > pressedY){
                        diffX = (er.getX()-pressedX)*5;
                        diffY = (er.getY()-pressedY)*5;
                        destX = deltaX+diffX;
                        destY = deltaY+diffY;
                        shapeFlick(o,destX,destY);
                    }
                    else{
                        diffX = (pressedX-er.getX())*5;
                        diffY = (pressedY-er.getY())*5;
                        destX = deltaX-diffX;
                        destY = deltaY-diffY;
                        shapeFlick(o,destX,destY);
                    }
                }
            }
            if (destX < -800 || destY < -500 || destX > 800 || destY > 500) {
                numberOfShapes--;
                root.getChildren().remove(o);
                deselectShape();
                System.out.println("Node Removed");
            }
            destX = 0;
            destY = 0;
            er.consume();
        });
    }
    public void shapeFlick(Shape o, double destX, double destY) {
        if (o instanceof Rectangle) o = (Rectangle)o;
        if (o instanceof Ellipse) o = (Ellipse)o;
        if (o instanceof Text) o = (Text)o;
        rt = new TranslateTransition();
        rt.setDuration(Duration.millis(10000));
        rt.setNode(o);
        rt.setFromX(o.getTranslateX());
        rt.setFromY(o.getTranslateY());
        rt.setByX(destX);
        rt.setByY(destY);
        System.out.println("destX: " + destX + " destY: " + destY);
        rt.setRate(10);
        rt.setInterpolator(Interpolator.EASE_OUT);
        rt.play();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
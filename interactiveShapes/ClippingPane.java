/*
    Author  : Michelle Tagarino
    Purpose : Since panes do not clip themselves, this class is used
              to accomplish exactly that.
*/
package interactiveshapes;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class ClippingPane extends Pane{
    
    private Rectangle clippingRegion = new Rectangle();
    
    public ClippingPane() {
        super();
        this.setClip(clippingRegion);
        clippingRegion.widthProperty().bind(this.widthProperty());
        clippingRegion.heightProperty().bind(this.widthProperty()); 
    }
}
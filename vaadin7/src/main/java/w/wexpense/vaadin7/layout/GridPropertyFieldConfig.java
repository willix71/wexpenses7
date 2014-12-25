package w.wexpense.vaadin7.layout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public class GridPropertyFieldConfig<T> extends PropertyFieldConfig<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridPropertyFieldConfig.class);

    public static class Incrementer {
        private int currentX, currentY;
        public void newLine() {currentX=0;currentY++;}
        public Position add() {
            return add(1,1);
        }
        public Position add(int width) {
            return add(width,1);
        }
        public Position add(int width, int height) {
            Position p = new Position(currentX,currentY,currentX+width-1,currentY+height-1);
            currentX+=width;
            return p;
        }        
    }
    
    public static class Position {
        private int x1, y1, x2, y2;
        public Position(int col, int row) {
            this(col,row,col,row);
        }
        public Position(int col, int row, int colEnd, int rowEnd) {
            this.x1 = col; this.y1 = row; this.x2 = colEnd; this.y2 = rowEnd;
        }
        @Override
        public boolean equals(Object arg0) {
            if (arg0 != null && arg0 instanceof Position) {
                Position other = (Position) arg0;
                return x1==other.x1 && y1==other.y1 && x2==other.x2 && y2==other.y2;
            }
            return false;
        }
        @Override
        public String toString() {
            return String.format("[%1$d,%2$d-%3$d,%4$d]",x1,y1,x2,y2);
        }
        
    }
    
    private Position position;
    
    public GridPropertyFieldConfig(GridLayout layout, Object id, Position position) {
        super(layout, id);
        this.position = position;
    }    
    
    public GridPropertyFieldConfig(GridLayout layout, Object id, int col, int row) {
        this(layout, id, col, row, col, row);
    }
    
    public GridPropertyFieldConfig(GridLayout layout, Object id, int col, int row, int colEnd, int rowEnd) {
        this(layout, id, new Position(col,row,colEnd,rowEnd));
    }
    
    @Override
    public void addToLayout(Component c) {
        LOGGER.info("Adding component for " + getId() + " at " + position);
        
        ((GridLayout) getLayout()).addComponent( c, position.x1, position.y1, position.x2, position.y2);
    }
}

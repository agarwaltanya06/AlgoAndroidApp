package com.example.algoapp;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomView extends View {

    private static final String PROPERTY_X = "prop_x";
    private static final String PROPERTY_Y = "prop_y";
    private static final String PROPERTY_X2 = "prop_x2";
    private static final String PROPERTY_Y2 = "prop_y2";

    private String circleText;
    private Paint circlePaint, textPaint, strokePaint, compareStrokePaint;
    private int radius;
    private int textsize;
    private int topheight;
    private int nodedistance;
    public ArrayList<Node> mNodes = new ArrayList<>();
    public ArrayList<Node> mNodesTempCopy;
    private static Node rootNode = null;
    private int mLastNodeX;
    private int mLastNodeY;
    private int highlightX;
    private int highlightY;
    private String mLastNodeValue;
    public static String preorderText = "Preorder elements: ";
    public static String inorderText = "Inorder elements: ";
    public static String postorderText = "Postorder elements: ";
    public static String insertionText = "Insertion result: ";
    public static String deletionText = "Deletion result: ";
    public static String searchText = "Search result: ";

    private int counter;
    private List<AnimatorSet> myList;
    int repNode = 0; //if repeated node inserted

    private ValueAnimator valueAnimator;
    private AnimatorSet animatorSet;

    private String deleting = "100";

    int lastNodeStarted = -1;


    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        circlePaint = new Paint();
        textPaint = new Paint();
        strokePaint = new Paint();
        compareStrokePaint = new Paint();
        radius = getResources().getDimensionPixelOffset(R.dimen.circle_radius);
        textsize = getResources().getDimensionPixelOffset(R.dimen.text_size);
        topheight = getResources().getDimensionPixelOffset(R.dimen.height_top_node);
        nodedistance = getResources().getDimensionPixelOffset(R.dimen.node_distance);


        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CustomView, 0, 0);

        int circleCol;
        int labelCol;
        try {
            //getting the text and colors specified using the names in attrs.xml
            circleText = a.getString(R.styleable.CustomView_circleLabel);
            circleCol = a.getInteger(R.styleable.CustomView_circleColor, 0);
            labelCol = a.getInteger(R.styleable.CustomView_labelColor, 0);
        } finally {
            a.recycle();
        }

        circlePaint.setStyle(Style.FILL);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(circleCol);
        circlePaint.setStrokeWidth(radius/2);

        strokePaint.setStyle(Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(Color.GRAY);
        strokePaint.setStrokeWidth(radius/2);

        compareStrokePaint.setStyle(Style.STROKE);
        compareStrokePaint.setAntiAlias(true);
        compareStrokePaint.setColor(Color.LTGRAY);
        compareStrokePaint.setStrokeWidth(radius/2);

        textPaint.setStyle(Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setColor(labelCol);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textsize);
    }

    @Override
    public void onDraw(Canvas canvas) {   //this overrides the OnDraw method of the View from which this class has been extended

        if (deleting.compareTo("100") == 0)   //insertion/ default case
        {

            if (!mNodes.isEmpty()) {
                for (int index = 0; index < mNodes.size(); index++) {
                    Node item = mNodes.get(index);

                    //to draw lines between nodes
                    if (index > 0) {
                        int findKey = item.parentKey;
                        Node findNode = null;
                        for (int i = 0; i < index; i++) {
                            if (mNodes.get(i).key == findKey) findNode = mNodes.get(i);
                        }
                        if (findNode != null && !(index == mNodes.size() - 1 && highlightX != item.parent.x)) {
                            circlePaint.setStrokeWidth(radius / 3);
                            canvas.drawLine(findNode.x, findNode.y + radius / 2, item.x, item.y - radius / 2, circlePaint);
                            circlePaint.setStrokeWidth(radius / 2);
                        }
                    }



                    if (index == mNodes.size() - 1 && item.parent != null && item.parent.x == highlightX) {
                        canvas.drawCircle(mLastNodeX, mLastNodeY, radius, circlePaint);
                        canvas.drawText(mLastNodeValue, mLastNodeX, mLastNodeY + (radius / 2), textPaint);  //string, x, y, paint object
                        circleText = Integer.toString(item.key);
                        if (mLastNodeX != item.x && mLastNodeY != item.y) {
                            canvas.drawCircle(mLastNodeX, mLastNodeY, radius + 8, strokePaint);
                        }
                    } else if (!(index == mNodes.size() - 1 && item.parent != null && highlightX != item.parent.x)) {
                        canvas.drawCircle(item.x, item.y, radius, circlePaint);  //x, y, radius, object of paint class
                        circleText = Integer.toString(item.key);
                        canvas.drawText(circleText, item.x, item.y + radius / 2, textPaint);  //string, x, y, paint object
                    }

                    //     if(index != mNodes.size() -1)  //for intermediate steps' highlightion, in 1st valueAnimator call...


                }

                if(repNode ==1) { //when a repeated node was added, last added node was not being redrawn. So to fix that bug.

                    Node n = mNodes.get(mNodes.size() -1);
                    circleText = Integer.toString(n.key);
                    canvas.drawCircle(n.x, n.y, radius, circlePaint);


                    int findKey = n.parentKey;
                    Node findNode = null;

                    for (int i = 0; i < mNodes.size()-1; i++) {
                        if (mNodes.get(i).key == findKey)
                        {
                            findNode = mNodes.get(i);
                            break;
                        }
                    }
                    if (findNode != null) {
                        circlePaint.setStrokeWidth(radius / 3);
                        canvas.drawLine(findNode.x, findNode.y + radius / 2, n.x, n.y - radius / 2, circlePaint);
                        circlePaint.setStrokeWidth(radius / 2);
                    }


                    canvas.drawText(circleText, n.x, n.y + radius / 2, textPaint);

                }

                if (mNodes.size() != 1 && highlightX != mNodes.get(mNodes.size() - 1).parent.x && highlightY != mNodes.get(mNodes.size() - 1).parent.y) {
                    canvas.drawCircle(highlightX, highlightY, radius + 8, compareStrokePaint);
                } //because, nodeA.x and nodeA.y are final coords...

            }

        }


        else if(deleting.compareTo("010") == 0)  //deletion
        {

            if(myList.size()!=0 && myList.get(myList.size()-1).isStarted())
            {
                lastNodeStarted = 1;
            }

            if(mNodesTempCopy.isEmpty())
            {
                //do nothing
            }

            else if(myList.size()!=0 && (lastNodeStarted!=1 || myList.get(myList.size()-1).isRunning()))  //at some animation before the last one
            {
                for(int index = 0; index<mNodesTempCopy.size(); index++)
                {
                    Node item = mNodesTempCopy.get(index);

                    canvas.drawCircle(item.x, item.y, radius, circlePaint);  //x, y, radius, object of paint class


                    if (index > 0) {
                        int findKey = item.parentKey;
                        Node findNode = null;
                        for (int i = 0; i < index; i++) {
                            if (mNodesTempCopy.get(i).key == findKey) findNode = mNodesTempCopy.get(i);
                        }
                        if (findNode != null) {
                            circlePaint.setStrokeWidth(radius / 3);
                            canvas.drawLine(findNode.x, findNode.y + radius / 2, item.x, item.y - radius / 2, circlePaint);
                            circlePaint.setStrokeWidth(radius / 2);
                        }
                    }

                    circleText = Integer.toString(item.key);
                    canvas.drawText(circleText, item.x, item.y + radius / 2, textPaint);  //string, x, y, paint object

                    if (highlightX != mNodesTempCopy.get(mNodesTempCopy.size() - 1).x && highlightY != mNodesTempCopy.get(mNodesTempCopy.size() - 1).y) {
                        canvas.drawCircle(highlightX, highlightY, radius + 8, compareStrokePaint);
                    } //because, nodeA.x and nodeA.y are final coords...

                }
            }

            else if(lastNodeStarted ==1 && !myList.get(myList.size()-1).isRunning()) //draw new tree
            {
                for (int index = 0; index < mNodes.size(); index++) {
                    Node item = mNodes.get(index);

                    canvas.drawCircle(item.x, item.y, radius, circlePaint);  //x, y, radius, object of paint class


                    if (index > 0) {
                        int findKey = item.parentKey;
                        Node findNode = null;
                        for (int i = 0; i < index; i++) {
                            if (mNodes.get(i).key == findKey) findNode = mNodes.get(i);
                        }
                        if (findNode != null) {
                            circlePaint.setStrokeWidth(radius / 3);
                            canvas.drawLine(findNode.x, findNode.y + radius / 2, item.x, item.y - radius / 2, circlePaint);
                            circlePaint.setStrokeWidth(radius / 2);
                        }
                    }

                    circleText = Integer.toString(item.key);
                    canvas.drawText(circleText, item.x, item.y + radius / 2, textPaint);  //string, x, y, paint object

                }
            }

            else
            {
                //do nothing
            }

        }

        else if(deleting.compareTo("001")==0)  //search
        {
            if (!mNodes.isEmpty()) {
                for (int index = 0; index < mNodes.size(); index++) {
                    Node item = mNodes.get(index);

                    canvas.drawCircle(item.x, item.y, radius, circlePaint);  //x, y, radius, object of paint class

                    if (index > 0) {
                        int findKey = item.parentKey;
                        Node findNode = null;
                        for (int i = 0; i < index; i++) {
                            if (mNodes.get(i).key == findKey) findNode = mNodes.get(i);
                        }
                        if (findNode != null) {
                            circlePaint.setStrokeWidth(radius / 3);
                            canvas.drawLine(findNode.x, findNode.y + radius / 2, item.x, item.y - radius / 2, circlePaint);
                            circlePaint.setStrokeWidth(radius / 2);
                        }
                    }

                    circleText = Integer.toString(item.key);
                    canvas.drawText(circleText, item.x, item.y + radius / 2, textPaint);  //string, x, y, paint object

                    if (highlightX != mNodes.get(mNodes.size() - 1).parent.x && highlightY != mNodes.get(mNodes.size() - 1).parent.y) {
                        canvas.drawCircle(highlightX, highlightY, radius + 8, compareStrokePaint);
                    } //because, nodeA.x and nodeA.y are final coords...


                }
            }
        }

        else if(deleting.compareTo("111") == 0) //redraw tree
        {
            for (int index = 0; index < mNodes.size(); index++) {
                Node item = mNodes.get(index);

                canvas.drawCircle(item.x, item.y, radius, circlePaint);  //x, y, radius, object of paint class

                if (index > 0) {
                    int findKey = item.parentKey;
                    Node findNode = null;
                    for (int i = 0; i < index; i++) {
                        if (mNodes.get(i).key == findKey) findNode = mNodes.get(i);
                    }
                    if (findNode != null) {
                        circlePaint.setStrokeWidth(radius / 3);
                        canvas.drawLine(findNode.x, findNode.y + radius / 2, item.x, item.y - radius / 2, circlePaint);
                        circlePaint.setStrokeWidth(radius / 2);
                    }
                }

                circleText = Integer.toString(item.key);
                canvas.drawText(circleText, item.x, item.y + radius / 2, textPaint);  //string, x, y, paint object

            }
        }
    }

    //    AnimatorSet as = new AnimatorSet();

    public Node addNode(int inputValue, Node nodeA, int mNodeX, int mNodeY, Node parent) {

        Node node;


        if (nodeA == null) {

            node = new Node(mNodeX, mNodeY, inputValue, parent);
            if (parent!=null) node.parentKey = parent.key;
            mNodes.add(node);  //corresponding change in the display (and hence in mNodes arraylist)
            rootNode=mNodes.get(0);

            insertionText += "Creating new node";


            mNodeX=mNodes.get(mNodes.size()-1).x;           //final x coordinate of node to be added
            mNodeY=mNodes.get(mNodes.size()-1).y;           //final y coordinate of node to be added

            PropertyValuesHolder propertyX = PropertyValuesHolder.ofInt(PROPERTY_X, parent == null ? mNodeX : getWidth()/2 - 3*nodedistance, mNodeX);
            PropertyValuesHolder propertyY = PropertyValuesHolder.ofInt(PROPERTY_Y, parent == null ? mNodeY : topheight, mNodeY);

            /*i.e. if root node of tree, then the value of string PROPERTY_X or "prop_x" (which is used as arg for getAnimatedValue later),
              would store mNodeX from beginning to end.
              Else, that will vary from top left screen corner to its final coordinate. */

            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(1200);
            valueAnimator.setValues(propertyX, propertyY);
            mLastNodeValue = Integer.toString(inputValue);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mLastNodeX = (int) animation.getAnimatedValue(PROPERTY_X);
                    mLastNodeY = (int) animation.getAnimatedValue(PROPERTY_Y);
                    invalidate();
                }
            });

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);

            return node;
        }

        else if (inputValue < nodeA.key) {

            insertionText+=inputValue + " < " + nodeA.key + " ; ";

            nodeA = leftChildAdded(nodeA);

            highlightX = nodeA.x;
            highlightY = nodeA.y;


            PropertyValuesHolder propertyX2 = PropertyValuesHolder.ofInt(PROPERTY_X2, nodeA.x + 1, nodeA.x );
            PropertyValuesHolder propertyY2 = PropertyValuesHolder.ofInt(PROPERTY_Y2, nodeA.y +1, nodeA.y);


            //      ValueAnimator valueAnimator2 = new ValueAnimator();
            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(800);
            valueAnimator.setValues(propertyX2, propertyY2);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation2) {
                    highlightX = (int) animation2.getAnimatedValue(PROPERTY_X2);
                    highlightY = (int) animation2.getAnimatedValue(PROPERTY_Y2);
                    invalidate();

                }
            });

            //       valueAnimator2.start();

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);


            nodeA.left = addNode(inputValue, nodeA.left, nodeA.x - nodedistance, nodeA.y + 2*nodedistance, nodeA);


        } else if (inputValue > nodeA.key) {

            insertionText+=inputValue + " > " + nodeA.key + " ; ";

            nodeA = rightChildAdded(nodeA);

            //        ValueAnimator valueAnimator2 = new ValueAnimator();

            highlightX = nodeA.x;
            highlightY = nodeA.y;

            PropertyValuesHolder propertyX2 = PropertyValuesHolder.ofInt(PROPERTY_X2, nodeA.x + 1, nodeA.x );
            PropertyValuesHolder propertyY2 = PropertyValuesHolder.ofInt(PROPERTY_Y2, nodeA.y +1, nodeA.y);

            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(1200);
            valueAnimator.setValues(propertyX2, propertyY2);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    highlightX = (int) animation.getAnimatedValue(PROPERTY_X2);
                    highlightY = (int) animation.getAnimatedValue(PROPERTY_Y2);
                    invalidate();
                }
            });
            //           valueAnimator2.start();

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);

            nodeA.right = addNode(inputValue, nodeA.right, nodeA.x + nodedistance, nodeA.y + 2*nodedistance, nodeA);

        } else //nodeA.key == inputValue
        {

            insertionText+=inputValue + " = " + nodeA.key + "(Value already exists.)";

            highlightX = nodeA.x;
            highlightY = nodeA.y;


            PropertyValuesHolder propertyX2 = PropertyValuesHolder.ofInt(PROPERTY_X2, nodeA.x + 1, nodeA.x );
            PropertyValuesHolder propertyY2 = PropertyValuesHolder.ofInt(PROPERTY_Y2, nodeA.y +1, nodeA.y);


            //      ValueAnimator valueAnimator2 = new ValueAnimator();
            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(800);
            valueAnimator.setValues(propertyX2, propertyY2);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation2) {
                    highlightX = (int) animation2.getAnimatedValue(PROPERTY_X2);
                    highlightY = (int) animation2.getAnimatedValue(PROPERTY_Y2);
                    invalidate();

                }
            });

            //       valueAnimator2.start();

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);


            repNode = 1;
            return nodeA; //2 equal nodes cannot exist in bst, same as an empty else condition
        }


        return nodeA;
    }


    public Node delNode(int inputValue, Node parentNode) {
        if (mNodes != null && !mNodes.isEmpty()) rootNode = mNodes.get(0);

        if (parentNode == null) {

            deletionText+= "Value not find";
            return parentNode;
        }

        else if (inputValue < parentNode.key) {

            deletionText+= inputValue + " < " + parentNode.key + " ; ";

            highlightX = parentNode.x;
            highlightY = parentNode.y;


            PropertyValuesHolder propertyX2 = PropertyValuesHolder.ofInt(PROPERTY_X2, parentNode.x + 1, parentNode.x );
            PropertyValuesHolder propertyY2 = PropertyValuesHolder.ofInt(PROPERTY_Y2, parentNode.y +1, parentNode.y);


            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(800);
            valueAnimator.setValues(propertyX2, propertyY2);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation2) {
                    highlightX = (int) animation2.getAnimatedValue(PROPERTY_X2);
                    highlightY = (int) animation2.getAnimatedValue(PROPERTY_Y2);
                    invalidate();

                }
            });

            //       valueAnimator2.start();

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);


            parentNode.left = delNode(inputValue, parentNode.left);
        }
        else if (inputValue > parentNode.key) {

            deletionText+= inputValue + " > " + parentNode.key + " ; ";

            highlightX = parentNode.x;
            highlightY = parentNode.y;


            PropertyValuesHolder propertyX2 = PropertyValuesHolder.ofInt(PROPERTY_X2, parentNode.x + 1, parentNode.x );
            PropertyValuesHolder propertyY2 = PropertyValuesHolder.ofInt(PROPERTY_Y2, parentNode.y +1, parentNode.y);


            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(800);
            valueAnimator.setValues(propertyX2, propertyY2);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation2) {
                    highlightX = (int) animation2.getAnimatedValue(PROPERTY_X2);
                    highlightY = (int) animation2.getAnimatedValue(PROPERTY_Y2);
                    invalidate();

                }
            });

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);


            parentNode.right = delNode(inputValue, parentNode.right);
        }
        else //inputValue == parentNode.key
        {

            deletionText+= inputValue + " = " + parentNode.key + " (Deleting this.) ";

            highlightX = parentNode.x;
            highlightY = parentNode.y;


            PropertyValuesHolder propertyX2 = PropertyValuesHolder.ofInt(PROPERTY_X2, parentNode.x + 1, parentNode.x );
            PropertyValuesHolder propertyY2 = PropertyValuesHolder.ofInt(PROPERTY_Y2, parentNode.y +1, parentNode.y);

            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(800);
            valueAnimator.setValues(propertyX2, propertyY2);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation2) {
                    highlightX = (int) animation2.getAnimatedValue(PROPERTY_X2);
                    highlightY = (int) animation2.getAnimatedValue(PROPERTY_Y2);
                    invalidate();

                }
            });

            //       valueAnimator2.start();

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);


            if (mNodes != null) {
                int delIndex=-1;
                int parentIndex=-1;
                int newDelIndex=-1;

                if (parentNode.left == null && parentNode.right == null) {  //deleting node with no child
                    deletionText+="\nNo child node. Replacing node with null value.";
                    for (int i = 0; i < mNodes.size(); i++) {
                        if (mNodes.get(i).key == inputValue) {
                            delIndex = i;
                            Node A = mNodes.get(i);
                            Node AParent = A.parent; //or mNdoes.get(i).parent
                            if(AParent!=null) {
                                for (int j = 0; j < mNodes.size(); j++) {
                                    if (mNodes.get(j).key == AParent.key) parentIndex = j;
                                }
                            }
                        }
                    }

                    Node A = mNodes.get(delIndex);
                    Node AParent=null;
                    if(parentIndex!=-1) {
                        AParent = mNodes.get(parentIndex);
                    }
                    Node B = reduceChildNumber(mNodes.get(delIndex));
                    mNodes.remove(delIndex);
                    mNodes.add(delIndex, B);

                    for(int i=0;i<mNodes.size();i++){
                        if(mNodes.get(i).key==inputValue) newDelIndex = i;
                    }

                    if(newDelIndex!=-1){
                        mNodes.remove(newDelIndex);
                        if(mNodes.size()!=0) {
                            mLastNodeX = mNodes.get(mNodes.size() - 1).x;
                            mLastNodeY = mNodes.get(mNodes.size() - 1).y;
                            mLastNodeValue = Integer.toString(mNodes.get(mNodes.size() - 1).key);
                        }
                    }

                    invalidate();
                    return null;

                } else if (parentNode.right == null) {   //deleting node with only left child

                    deletionText+="\nOnly 1 child. Replacing with child node.";
                    int assignParentKey = -1;
                    int shiftUpKey = parentNode.left.key;
                    if(parentNode.parent!=null) assignParentKey = parentNode.parent.key;
                    int assignChildKey = parentNode.left.key;

                    parentNode.left.parentKey= parentNode.parentKey;
                    parentNode.left.parent = parentNode.parent;
                    if(assignParentKey<assignChildKey) {
                        if (parentNode.parent != null) parentNode.parent.right = parentNode.left;
                    }
                    else if(assignParentKey>assignChildKey){
                        if (parentNode.parent != null) parentNode.parent.left = parentNode.left;
                    }

                    int shiftIndex=-1;
                    for(int i=0;i<mNodes.size();i++){
                        if(mNodes.get(i).key==shiftUpKey) shiftIndex=i;
                    }

                    shiftUp(mNodes.get(shiftIndex));

                    int removeIndex = -1;
                    for(int i=0;i<mNodes.size();i++){
                        if(mNodes.get(i).key==inputValue) removeIndex=i;
                    }


                    mNodes.remove(removeIndex);

                    if(mNodes.size()!=0) {
                        mLastNodeX = mNodes.get(mNodes.size() - 1).x;
                        mLastNodeY = mNodes.get(mNodes.size() - 1).y;
                        mLastNodeValue = Integer.toString(mNodes.get(mNodes.size() - 1).key);
                    }

                    int returnIndex=-1;
                    for(int i=0;i<mNodes.size();i++){
                        if(mNodes.get(i).key==assignChildKey) returnIndex = i;
                    }

                    updatemArrayList(rootNode);
                    invalidate();

                    return mNodes.get(returnIndex);

                } else if (parentNode.left == null) {     //deleting node with only right child

                    deletionText+="Only 1 child. Replacing with child node. ";

                    int assignParentKey=-1;
                    int shiftUpKey = parentNode.right.key;
                    if(parentNode.parent!=null) assignParentKey = parentNode.parent.key;
                    int assignChildKey = parentNode.right.key;

                    parentNode.right.parentKey= parentNode.parentKey;
                    parentNode.right.parent = parentNode.parent;
                    if(assignParentKey<assignChildKey) {
                        if(parentNode.parent!=null) parentNode.parent.right = parentNode.right;
                    }
                    else if(assignParentKey>assignChildKey){
                        if(parentNode.parent!=null) parentNode.parent.left = parentNode.right;
                    }


                    int shiftIndex=-1;
                    for(int i=0;i<mNodes.size();i++){
                        if(mNodes.get(i).key==shiftUpKey) shiftIndex=i;
                    }

                    shiftUp(mNodes.get(shiftIndex));

                    int removeIndex = -1;
                    for(int i=0;i<mNodes.size();i++){
                        if(mNodes.get(i).key==inputValue) removeIndex=i;
                    }

                    mNodes.remove(removeIndex);


                    if(mNodes.size()!=0) {
                        mLastNodeX = mNodes.get(mNodes.size() - 1).x;
                        mLastNodeY = mNodes.get(mNodes.size() - 1).y;
                        mLastNodeValue = Integer.toString(mNodes.get(mNodes.size() - 1).key);
                    }

                    int returnIndex=-1;
                    for(int i=0;i<mNodes.size();i++){
                        if(mNodes.get(i).key==assignChildKey) returnIndex = i;
                    }


                    updatemArrayList(rootNode);
                    invalidate();
                    return mNodes.get(returnIndex);

                }  else {  //deleting node with both left and right child

                    deletionText += "\nReplacing with max valued node from left subtree.\n";

                    int newKey = maximumValue(parentNode.left);


                    rootNode = mNodes.get(0);
                    rootNode = delNode(newKey, rootNode);

                    if(parentNode.left!=null) {
                        parentNode.left.parent = parentNode;
                        parentNode.left.parentKey = newKey;
                    }

                    parentNode.right.parentKey = newKey;


                    for(int i=0;i<mNodes.size();i++){
                        if(mNodes.get(i).key==newKey) mNodes.remove(i);
                    }

                    parentNode.key = newKey;


                    invalidate();
                    return parentNode;

                }

            }

            invalidate();
            return null;
        }
        invalidate();
        return parentNode;
    }

    public void searchNode(int input, Node nodeA, TextView displayTextView){

        if (nodeA==null) {

            searchText+= "Value not found";
            displayTextView.setText(searchText);
        }

        else if (nodeA.key == input){
            searchText+= input + " = " + input + " (Element found!)";
            displayTextView.setText(searchText);


            highlightX = nodeA.x;
            highlightY = nodeA.y;

            PropertyValuesHolder propertyX2 = PropertyValuesHolder.ofInt(PROPERTY_X2, nodeA.x + 1, nodeA.x );
            PropertyValuesHolder propertyY2 = PropertyValuesHolder.ofInt(PROPERTY_Y2, nodeA.y +1, nodeA.y);

            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(800);
            valueAnimator.setValues(propertyX2, propertyY2);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation2) {
                    highlightX = (int) animation2.getAnimatedValue(PROPERTY_X2);
                    highlightY = (int) animation2.getAnimatedValue(PROPERTY_Y2);
                    invalidate();

                }
            });

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);

        }

        else if (input < nodeA.key){
            searchText+= input + " < " + nodeA.key +" ; ";


            highlightX = nodeA.x;
            highlightY = nodeA.y;


            PropertyValuesHolder propertyX2 = PropertyValuesHolder.ofInt(PROPERTY_X2, nodeA.x + 1, nodeA.x );
            PropertyValuesHolder propertyY2 = PropertyValuesHolder.ofInt(PROPERTY_Y2, nodeA.y +1, nodeA.y);

            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(800);
            valueAnimator.setValues(propertyX2, propertyY2);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation2) {
                    highlightX = (int) animation2.getAnimatedValue(PROPERTY_X2);
                    highlightY = (int) animation2.getAnimatedValue(PROPERTY_Y2);
                    invalidate();

                }
            });

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);


            searchNode(input, nodeA.left, displayTextView);
        }

        else //input > nodeA.key
        {
            searchText+= input + " > " + nodeA.key +" ; ";

            highlightX = nodeA.x;
            highlightY = nodeA.y;

            PropertyValuesHolder propertyX2 = PropertyValuesHolder.ofInt(PROPERTY_X2, nodeA.x + 1, nodeA.x );
            PropertyValuesHolder propertyY2 = PropertyValuesHolder.ofInt(PROPERTY_Y2, nodeA.y +1, nodeA.y);


            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(800);
            valueAnimator.setValues(propertyX2, propertyY2);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation2) {
                    highlightX = (int) animation2.getAnimatedValue(PROPERTY_X2);
                    highlightY = (int) animation2.getAnimatedValue(PROPERTY_Y2);
                    invalidate();

                }
            });

            animatorSet = new AnimatorSet();
            animatorSet.play(valueAnimator);
            myList.add(animatorSet);
            animatorSet.addListener(myListener);


            searchNode(input, nodeA.right, displayTextView);
        }

    }



    public class Node {
        int key, x, y;
        int parentKey;
        int leftChildNumber, rightChildNumber;
        Node left = null, right = null;
        Node parent=null;

        Node(int X, int Y, int KeyVal, Node parent) {
            x = X;
            y = Y;
            key = KeyVal;
            this.parent = parent;
            leftChildNumber =0;
            rightChildNumber=0;
            parentKey = parent == null ? -32700 :parent.key;
            left = null;
            right = null;
        }
    }

    public int maximumValue(Node xyz)
    {
        int maxVal = xyz.key;
        while (xyz.right != null)
        {
            maxVal = xyz.right.key;
            xyz = xyz.right;
        }
        return maxVal;
    }
    ////////////////////////////////////////////////////
    public Node leftChildAdded(Node n){
        n=increaseLeftChildNumber(n);
        Node r =mNodes.get(0);
        updatemArrayList(r);
        return n;
    }

    public Node rightChildAdded(Node n){
        n=increaseRightChildNumber(n);
        Node r =mNodes.get(0);
        updatemArrayList(r);
        return n;
    }

    public Node increaseLeftChildNumber(Node n){
        n.leftChildNumber++;
        if(n.parent!=null) {
            leftChildAdded(n.parent);
        }
        return n;
    }

    public Node increaseRightChildNumber(Node n){
        n.rightChildNumber++;
        if(n.parent!=null) {
            rightChildAdded(n.parent);
        }
        return n;
    }

    public void updatemArrayList(Node r){

        for(int i=0; i<mNodes.size();i++){
            if(r.left!=null && r.left.key==mNodes.get(i).key){
                mNodes.get(i).x = r.x - (mNodes.get(i).rightChildNumber + 1) * nodedistance;
                updatemArrayList(r.left);

            }
            if(r.right!=null && r.right.key==mNodes.get(i).key){
                mNodes.get(i).x = r.x + (mNodes.get(i).leftChildNumber + 1) * nodedistance;
                updatemArrayList(r.right);
            }

        }
    }


    public void shiftUp(Node topmost){
        for(int i=0;i<mNodes.size();i++){
            if((topmost!=null)&& (mNodes.get(i).key==topmost.key))   topmost.y = topmost.y - (2*nodedistance);
        }
        if(topmost.left!=null){
            shiftUp(topmost.left);
        }
        if(topmost.right!=null){
            shiftUp(topmost.right);
        }

    }


    public Node reduceChildNumber(Node n){
        if ((n.parent!=null) && (n.key<n.parent.key)){
            n.parent.leftChildNumber--;
            n.parent=reduceChildNumber(n.parent);
        }
        else if((n.parent!=null)&&(n.key>n.parent.key)) {
            n.parent.rightChildNumber--;
            n.parent = reduceChildNumber(n.parent);
        }
        Node r =mNodes.get(0);
        updatemArrayList(r);
        return n;
    }



///////////////////////


    private Animator.AnimatorListener myListener = new Animator.AnimatorListener(){
        @Override public void onAnimationStart(Animator animation){ }

        @Override public void onAnimationRepeat(Animator animation){ }

        @Override public void onAnimationCancel(Animator animation){ }

        @Override public void onAnimationEnd(Animator animation){
            startNextAnimation();
        }
    };


    private void startNextAnimation(){
        counter++;
        if(counter >= myList.size()){
            return;
        }
        myList.get(counter).start();
    }


    public void addToBst(int input, TextView displayTextView){

        deleting = "100";
        repNode = 0;
        insertionText = "Inserting "+input+": \n";
        myList = new ArrayList<>();
        rootNode=addNode(input,rootNode, getWidth()/2, topheight, null);

        displayTextView.setText(insertionText);

        counter = 0;
        if(myList.size()!=0) myList.get(0).start();
    }

    public void delFromBst (int input, TextView displayTextView){
        int newDelIndex = -1;


        deleting = "010";
        lastNodeStarted = -1;

        mNodesTempCopy = new ArrayList<>();
        for(int i=0; i<mNodes.size(); i++) {

            Node n = new Node(mNodes.get(i).x, mNodes.get(i).y, mNodes.get(i).key, mNodes.get(i).parent);
            mNodesTempCopy.add(n);
        }

        deletionText = "Deleting "+input+": \n";
        myList = new ArrayList<>();

        rootNode = delNode(input, rootNode);

        displayTextView.setText(deletionText);

        counter = 0;
        if(myList.size()!=0) myList.get(0).start();

    }

    public void searchInBst (int input, TextView displayTextView){

        deleting = "001";
        myList = new ArrayList<>();
        searchText = "Searching for "+input+": \n";
        searchNode(input, rootNode, displayTextView);

        counter = 0;
        if(myList.size()!=0) myList.get(0).start();
    }

    public void redrawTree()
    {
        deleting = "111";
        invalidate();
    }

    public void preorderTraverse(Node x){

        deleting = "100";

        if(x==null) {
            preorderText = preorderText.concat("");
            return;
        }
        else{
            preorderText =preorderText.concat(Integer.toString(x.key)+" ");
            preorderTraverse(x.left);
            preorderTraverse(x.right);
        }
    }

    public void inorderTraverse(Node x){

        deleting = "100";

        if(x==null) {
            inorderText = inorderText.concat("");
            return;
        }
        else{
            inorderTraverse(x.left);
            inorderText =inorderText.concat(Integer.toString(x.key)+" ");
            inorderTraverse(x.right);
        }
    }

    public void postorderTraverse(Node x){

        deleting = "100";

        if(x==null) {
            postorderText = postorderText.concat("");
            return;
        }
        else{
            postorderTraverse(x.left);
            postorderTraverse(x.right);
            postorderText =postorderText.concat(Integer.toString(x.key)+" ");
        }
    }


}